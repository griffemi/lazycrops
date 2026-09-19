package xyz.funky493.lazycrops.machines.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import xyz.funky493.lazycrops.machines.LazySeedUtil;

/** An input slot that only accepts crop seeds. */
public class SeedSlot extends Slot {

    public SeedSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return LazySeedUtil.isLazySeed(stack);
    }
}
