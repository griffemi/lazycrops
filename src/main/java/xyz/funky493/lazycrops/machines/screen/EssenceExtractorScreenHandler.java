package xyz.funky493.lazycrops.machines.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import xyz.funky493.lazycrops.blocks.LazyBlocks;
import xyz.funky493.lazycrops.machines.EssenceExtractorBlockEntity;
import xyz.funky493.lazycrops.machines.LazySeedUtil;
import xyz.funky493.lazycrops.machines.LazyScreenHandlers;

public class EssenceExtractorScreenHandler extends ScreenHandler {

    private static final int INPUTS = EssenceExtractorBlockEntity.INPUT_SLOTS;
    private static final int MACHINE_SIZE = EssenceExtractorBlockEntity.SLOT_COUNT;
    private static final int PLAYER_START = MACHINE_SIZE;
    private static final int HOTBAR_START = MACHINE_SIZE + 27;
    private static final int TOTAL = MACHINE_SIZE + 36;

    private final Inventory inventory;
    private final PropertyDelegate properties;
    private final ScreenHandlerContext context;

    public EssenceExtractorScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(MACHINE_SIZE), new ArrayPropertyDelegate(5), ScreenHandlerContext.EMPTY);
        buf.readBlockPos();
    }

    public EssenceExtractorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
                                         PropertyDelegate properties, ScreenHandlerContext context) {
        super(LazyScreenHandlers.ESSENCE_EXTRACTOR, syncId);
        checkSize(inventory, MACHINE_SIZE);
        this.inventory = inventory;
        this.properties = properties;
        this.context = context;

        inventory.onOpen(playerInventory.player);

        for (int i = 0; i < INPUTS; i++) {
            this.addSlot(new SeedSlot(inventory, i, 30 + (i % 6) * 18, 18 + (i / 6) * 18));
        }
        for (int o = 0; o < EssenceExtractorBlockEntity.OUTPUT_SLOTS; o++) {
            this.addSlot(new OutputSlot(inventory, INPUTS + o, 170 + (o % 3) * 18, 27 + (o / 3) * 18));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 34 + col * 18, 140 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 34 + col * 18, 198));
        }

        this.addProperties(properties);
    }

    public int getProgress() {
        return properties.get(2);
    }

    public int getMaxProgress() {
        int max = properties.get(3);
        return max == 0 ? EssenceExtractorBlockEntity.MAX_PROGRESS : max;
    }

    public int getStatus() {
        return properties.get(4);
    }

    public long getEnergy() {
        return (properties.get(0) & 0xFFFFL) | ((long) (properties.get(1) & 0xFFFF) << 16);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasStack()) {
            return original;
        }

        ItemStack inSlot = slot.getStack();
        original = inSlot.copy();

        if (index < MACHINE_SIZE) {
            // Inputs and outputs both empty out to the player.
            if (!this.insertItem(inSlot, PLAYER_START, TOTAL, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Player -> inputs, but only for seeds. The output range is never a destination:
            // insertItem skips Slot#canInsert when merging into an existing stack, so aiming
            // at it would push items into output slots despite canInsert being false.
            boolean moved = LazySeedUtil.isLazySeed(inSlot) && this.insertItem(inSlot, 0, INPUTS, false);
            if (!moved) {
                if (index < HOTBAR_START) {
                    if (!this.insertItem(inSlot, HOTBAR_START, TOTAL, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.insertItem(inSlot, PLAYER_START, HOTBAR_START, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if (inSlot.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        if (inSlot.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTakeItem(player, inSlot);
        return original;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return ScreenHandler.canUse(this.context, player, LazyBlocks.ESSENCE_EXTRACTOR);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }
}
