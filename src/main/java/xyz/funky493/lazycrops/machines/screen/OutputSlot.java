package xyz.funky493.lazycrops.machines.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

/** A slot players and automation may take from but never put into. */
public class OutputSlot extends Slot {

    public OutputSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }
}
