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
import xyz.funky493.lazycrops.machines.HarvesterBlockEntity;
import xyz.funky493.lazycrops.machines.LazyScreenHandlers;

public class HarvesterScreenHandler extends ScreenHandler {

    private static final int MACHINE_SIZE = HarvesterBlockEntity.SLOT_COUNT;
    private static final int HOTBAR_START = MACHINE_SIZE + 27;
    private static final int TOTAL = MACHINE_SIZE + 36;

    private final Inventory inventory;
    private final PropertyDelegate properties;
    private final ScreenHandlerContext context;

    /** Client side: the real inventory and values arrive through the normal slot/property sync. */
    public HarvesterScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(MACHINE_SIZE), new ArrayPropertyDelegate(5), ScreenHandlerContext.EMPTY);
        buf.readBlockPos();
    }

    public HarvesterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
                                  PropertyDelegate properties, ScreenHandlerContext context) {
        super(LazyScreenHandlers.HARVESTER, syncId);
        checkSize(inventory, MACHINE_SIZE);
        this.inventory = inventory;
        this.properties = properties;
        this.context = context;

        inventory.onOpen(playerInventory.player);

        // Slot order defines the index ranges used by quickMove -- machine, then main, then hotbar.
        for (int i = 0; i < MACHINE_SIZE; i++) {
            this.addSlot(new Slot(inventory, i, 34 + (i % 6) * 18, 18 + (i / 6) * 18));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 122 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 180));
        }

        this.addProperties(properties);
    }

    public int getRadius() {
        return properties.get(2);
    }

    public int getStatus() {
        return properties.get(3);
    }

    /** Energy arrives as two shorts; mask before recombining or the sign bit corrupts it. */
    public long getEnergy() {
        return (properties.get(0) & 0xFFFFL) | ((long) (properties.get(1) & 0xFFFF) << 16);
    }

    /**
     * Sets the harvest radius. Button ids 0..3 map to a 3x3..9x9 footprint.
     * <p>
     * This runs on <em>both</em> sides: the client interaction manager invokes it locally before
     * sending the packet. Going through the context makes the client pass a no-op, because the
     * client handler is built with {@link ScreenHandlerContext#EMPTY}.
     */
    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id < 0 || id > 3) {
            return false;
        }
        this.context.run((world, pos) -> {
            if (world.getBlockEntity(pos) instanceof HarvesterBlockEntity be) {
                be.setRadius(id + 1);
            }
        });
        return true;
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
            // Buffer -> player, hotbar first.
            if (!this.insertItem(inSlot, MACHINE_SIZE, TOTAL, true)) {
                return ItemStack.EMPTY;
            }
        } else if (index < HOTBAR_START) {
            // The buffer is output-only, so player slots just shuffle between themselves.
            if (!this.insertItem(inSlot, HOTBAR_START, TOTAL, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.insertItem(inSlot, MACHINE_SIZE, HOTBAR_START, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (inSlot.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        // Without this guard the caller loops on quickMove until it returns EMPTY, and hangs.
        if (inSlot.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTakeItem(player, inSlot);
        return original;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return ScreenHandler.canUse(this.context, player, LazyBlocks.HARVESTER);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }
}
