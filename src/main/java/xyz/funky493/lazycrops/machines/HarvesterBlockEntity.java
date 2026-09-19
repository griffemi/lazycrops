package xyz.funky493.lazycrops.machines;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.funky493.lazycrops.machines.screen.HarvesterScreenHandler;

import java.util.List;

/**
 * Sweeps a configurable area for mature crops, replants them, and buffers the surplus.
 */
public class HarvesterBlockEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory {

    public static final int SLOT_COUNT = 24;

    public static final long CAPACITY = 100_000L;
    public static final long MAX_INSERT = 2_000L;
    private static final long ENERGY_PER_HARVEST = 250L;

    private static final int SWEEP_INTERVAL = 20;
    private static final int MAX_HARVESTS_PER_SWEEP = 8;

    public static final int STATUS_IDLE = 0;
    public static final int STATUS_WORKING = 1;
    public static final int STATUS_NO_POWER = 2;
    public static final int STATUS_FULL = 3;

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);
    public final MachineEnergyStorage energy = new MachineEnergyStorage(this, CAPACITY, MAX_INSERT, 0);

    /** 1..4, meaning a 3x3 .. 9x9 footprint. */
    private int radius = 1;
    private int status = STATUS_IDLE;
    private int cooldown = SWEEP_INTERVAL;

    private final PropertyDelegate properties = new PropertyDelegate() {
        @Override
        public int get(int index) {
            // Energy is split across two indices on purpose: the sync packet writes each
            // property with writeShort, so anything above 32767 would truncate and sign-flip.
            return switch (index) {
                case 0 -> (int) (energy.amount & 0xFFFFL);
                case 1 -> (int) ((energy.amount >>> 16) & 0xFFFFL);
                case 2 -> radius;
                case 3 -> status;
                case 4 -> cooldown;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Client-side optimism only; the server's values win on the next sync.
            if (index == 2) {
                radius = MathHelper.clamp(value, 1, 4);
            }
        }

        @Override
        public int size() {
            return 5;
        }
    };

    public HarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(LazyBlockEntities.HARVESTER, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    /** The buffer is an output: automation may pull from it, but never push into it. */
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return false;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int newRadius) {
        this.radius = MathHelper.clamp(newRadius, 1, 4);
        markDirty();
    }

    public static void tick(World world, BlockPos pos, BlockState state, HarvesterBlockEntity be) {
        if (world.isClient) {
            return;
        }
        if (--be.cooldown > 0) {
            return;
        }
        be.cooldown = SWEEP_INTERVAL;

        if (be.energy.amount < ENERGY_PER_HARVEST) {
            be.status = STATUS_NO_POWER;
            return;
        }

        ServerWorld serverWorld = (ServerWorld) world;
        int harvested = 0;
        boolean blockedByFullBuffer = false;

        for (BlockPos target : BlockPos.iterate(
                pos.add(-be.radius, -1, -be.radius),
                pos.add(be.radius, 1, be.radius))) {

            if (harvested >= MAX_HARVESTS_PER_SWEEP) {
                break;
            }
            if (be.energy.amount < ENERGY_PER_HARVEST) {
                be.status = STATUS_NO_POWER;
                break;
            }
            if (target.equals(pos)) {
                continue;
            }
            // Don't let a harvester on a chunk border force-load its neighbour every sweep.
            if (!serverWorld.getChunkManager().isChunkLoaded(target.getX() >> 4, target.getZ() >> 4)) {
                continue;
            }

            BlockState cropState = serverWorld.getBlockState(target);
            if (!(cropState.getBlock() instanceof CropBlock crop) || !crop.isMature(cropState)) {
                continue;
            }

            // BlockPos.iterate hands back a reused Mutable, so anything kept must be immutable.
            BlockPos immutable = target.toImmutable();
            List<ItemStack> drops = Block.getDroppedStacks(cropState, serverWorld, immutable, null);

            // The replant seed is taken out of the drops FIRST; the buffer only ever receives
            // the remainder. split(1) mutates the entry in place, which is what makes `drops`
            // the remainder rather than the full yield.
            ItemStack seed = ItemStack.EMPTY;
            for (ItemStack drop : drops) {
                if (!drop.isEmpty() && Block.getBlockFromItem(drop.getItem()) == crop) {
                    seed = drop.split(1);
                    break;
                }
            }
            if (seed.isEmpty()) {
                // Nothing to replant with; leave the crop standing rather than farm it for free.
                continue;
            }
            drops.removeIf(ItemStack::isEmpty);

            // Simulate before touching the world: a full buffer must stall, not void the drops.
            if (!be.canAcceptAll(drops, 0, SLOT_COUNT)) {
                blockedByFullBuffer = true;
                continue;
            }

            be.insertAll(drops, 0, SLOT_COUNT);
            be.energy.spend(ENERGY_PER_HARVEST);
            // Breaking the crop is deliberately last, once every failure mode is ruled out.
            serverWorld.setBlockState(immutable, crop.withAge(0), Block.NOTIFY_LISTENERS);
            harvested++;
        }

        if (harvested > 0) {
            be.status = STATUS_WORKING;
            be.markDirty();
        } else if (blockedByFullBuffer) {
            be.status = STATUS_FULL;
        } else if (be.status != STATUS_NO_POWER) {
            be.status = STATUS_IDLE;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);
        nbt.putInt("Radius", radius);
        nbt.putLong("Energy", energy.amount);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        items.replaceAll(ignored -> ItemStack.EMPTY);
        Inventories.readNbt(nbt, items);
        // Clamped because an out-of-range radius would sweep a huge volume and stall the tick.
        radius = MathHelper.clamp(nbt.getInt("Radius"), 1, 4);
        // Written out rather than MathHelper.clamp, which has no long overload and would
        // silently bind to the float one, losing precision at these magnitudes.
        energy.amount = Math.max(0L, Math.min(CAPACITY, nbt.getLong("Energy")));
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.resourcecrops.harvester");
    }

    @Override
    public void writeScreenOpeningData(net.minecraft.server.network.ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new HarvesterScreenHandler(syncId, playerInventory, this, properties,
                net.minecraft.screen.ScreenHandlerContext.create(this.world, this.pos));
    }
}
