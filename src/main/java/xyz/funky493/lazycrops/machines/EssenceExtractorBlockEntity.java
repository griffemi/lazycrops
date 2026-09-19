package xyz.funky493.lazycrops.machines;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.funky493.lazycrops.machines.screen.EssenceExtractorScreenHandler;

import java.util.List;

/**
 * Renders seeds down into seed essence.
 * <p>
 * Each attempt consumes one seed and succeeds only half the time, so essence is a genuine sink
 * for surplus harvest rather than a lossless conversion.
 */
public class EssenceExtractorBlockEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory {

    public static final int INPUT_SLOTS = 24;
    public static final int OUTPUT_SLOTS = 9;
    public static final int SLOT_COUNT = INPUT_SLOTS + OUTPUT_SLOTS;

    public static final long CAPACITY = 40_000L;
    public static final long MAX_INSERT = 1_000L;
    private static final long ENERGY_PER_TICK = 20L;
    public static final int MAX_PROGRESS = 40;

    public static final int STATUS_IDLE = 0;
    public static final int STATUS_WORKING = 1;
    public static final int STATUS_NO_POWER = 2;

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);
    public final MachineEnergyStorage energy = new MachineEnergyStorage(this, CAPACITY, MAX_INSERT, 0);

    private int progress = 0;
    private int status = STATUS_IDLE;
    private int activeSlot = -1;
    private ItemStack activeSnapshot = ItemStack.EMPTY;

    private final PropertyDelegate properties = new PropertyDelegate() {
        @Override
        public int get(int index) {
            // Energy is split low/high: properties are sent as shorts, so a raw 40000 would wrap.
            return switch (index) {
                case 0 -> (int) (energy.amount & 0xFFFFL);
                case 1 -> (int) ((energy.amount >>> 16) & 0xFFFFL);
                case 2 -> progress;
                case 3 -> MAX_PROGRESS;
                case 4 -> status;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int size() {
            return 5;
        }
    };

    public EssenceExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(LazyBlockEntities.ESSENCE_EXTRACTOR, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return slot < INPUT_SLOTS && LazySeedUtil.isLazySeed(stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return slot >= INPUT_SLOTS;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot < INPUT_SLOTS && LazySeedUtil.isLazySeed(stack);
    }

    public static void tick(World world, BlockPos pos, BlockState state, EssenceExtractorBlockEntity be) {
        if (world.isClient) {
            return;
        }

        int slot = be.findWorkableInput();
        if (slot < 0) {
            be.progress = 0;
            be.activeSlot = -1;
            be.status = STATUS_IDLE;
            return;
        }
        if (be.energy.amount < ENERGY_PER_TICK) {
            // Progress is intentionally preserved, so a power blip doesn't waste the work.
            be.status = STATUS_NO_POWER;
            return;
        }

        ItemStack input = be.getStack(slot);
        if (slot != be.activeSlot || !ItemStack.canCombine(input, be.activeSnapshot)) {
            be.progress = 0;
            be.activeSlot = slot;
            be.activeSnapshot = input.copy();
        }

        be.energy.spend(ENERGY_PER_TICK);
        be.progress++;
        be.status = STATUS_WORKING;

        if (be.progress < MAX_PROGRESS) {
            return;
        }

        be.progress = 0;
        Item essence = LazySeedUtil.essenceFor(input);
        // The seed is consumed whether or not the extraction takes.
        be.removeStack(slot, 1);
        if (essence != null && world.getRandom().nextBoolean()) {
            be.insertAll(List.of(new ItemStack(essence, 1)), INPUT_SLOTS, SLOT_COUNT);
        }
        be.markDirty();
    }

    /**
     * First input slot holding a seed whose essence tier still has somewhere to go.
     * <p>
     * Checking output room up front is what keeps a full output from consuming seeds into
     * nothing while it spins.
     */
    private int findWorkableInput() {
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = getStack(i);
            Item essence = LazySeedUtil.essenceFor(stack);
            if (essence == null) {
                continue;
            }
            if (canAcceptAll(List.of(new ItemStack(essence, 1)), INPUT_SLOTS, SLOT_COUNT)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);
        nbt.putLong("Energy", energy.amount);
        nbt.putInt("Progress", progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        items.replaceAll(ignored -> ItemStack.EMPTY);
        Inventories.readNbt(nbt, items);
        // MathHelper.clamp has no long overload; it would bind to float and lose precision.
        energy.amount = Math.max(0L, Math.min(CAPACITY, nbt.getLong("Energy")));
        progress = MathHelper.clamp(nbt.getInt("Progress"), 0, MAX_PROGRESS);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.lazycrops.essence_extractor");
    }

    @Override
    public void writeScreenOpeningData(net.minecraft.server.network.ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new EssenceExtractorScreenHandler(syncId, playerInventory, this, properties,
                net.minecraft.screen.ScreenHandlerContext.create(this.world, this.pos));
    }
}
