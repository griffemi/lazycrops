package xyz.funky493.lazycrops.machines;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Default-method {@link SidedInventory} over a {@link DefaultedList}.
 * <p>
 * Implementing {@code SidedInventory} is all that is needed for both kinds of automation:
 * hoppers use it directly, and the Fabric Transfer API ships a fallback that wraps any
 * {@code BlockEntity implements Inventory} into a storage, honouring the sided predicates
 * below. That is why there is no {@code ItemStorage.SIDED.register*} call anywhere.
 */
public interface ImplementedInventory extends SidedInventory {

    DefaultedList<ItemStack> getItems();

    @Override
    default int size() {
        return getItems().size();
    }

    @Override
    default boolean isEmpty() {
        for (ItemStack stack : getItems()) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }

    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack removed = Inventories.splitStack(getItems(), slot, count);
        if (!removed.isEmpty()) {
            markDirty();
        }
        return removed;
    }

    @Override
    default ItemStack removeStack(int slot) {
        ItemStack removed = Inventories.removeStack(getItems(), slot);
        if (!removed.isEmpty()) {
            markDirty();
        }
        return removed;
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    /**
     * Blanks every slot without resizing.
     * <p>
     * Deliberately not {@code getItems().clear()}: DefaultedList delegates to its backing list,
     * so clearing it can drop the size to zero and permanently corrupt the inventory.
     */
    @Override
    default void clear() {
        getItems().replaceAll(ignored -> ItemStack.EMPTY);
    }

    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    default int[] getAvailableSlots(Direction side) {
        int[] slots = new int[size()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    default boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return true;
    }

    @Override
    default boolean canExtract(int slot, ItemStack stack, Direction side) {
        return true;
    }

    /**
     * Whether every one of {@code incoming} would fit in slots {@code [from, to)}, checked
     * against a copy so nothing is mutated.
     * <p>
     * This exists so a harvest can be abandoned <em>before</em> the crop is broken. Inserting
     * optimistically and discarding the overflow would silently void the player's drops.
     */
    default boolean canAcceptAll(List<ItemStack> incoming, int from, int to) {
        DefaultedList<ItemStack> simulated = DefaultedList.ofSize(size(), ItemStack.EMPTY);
        for (int i = 0; i < size(); i++) {
            simulated.set(i, getStack(i).copy());
        }
        for (ItemStack stack : incoming) {
            if (!distribute(simulated, stack.copy(), from, to, getMaxCountPerStack())) {
                return false;
            }
        }
        return true;
    }

    /** Commits what {@link #canAcceptAll} simulated. Only call once that has returned true. */
    default void insertAll(List<ItemStack> incoming, int from, int to) {
        for (ItemStack stack : incoming) {
            distribute(getItems(), stack.copy(), from, to, getMaxCountPerStack());
        }
        markDirty();
    }

    /**
     * Merges {@code stack} into {@code target} across {@code [from, to)}, topping up matching
     * stacks before claiming empty slots. Returns false if any remainder could not be placed.
     */
    static boolean distribute(List<ItemStack> target, ItemStack stack, int from, int to, int maxPerSlot) {
        int limit = Math.min(maxPerSlot, stack.getMaxCount());

        for (int i = from; i < to && !stack.isEmpty(); i++) {
            ItemStack existing = target.get(i);
            if (existing.isEmpty() || !ItemStack.canCombine(existing, stack)) {
                continue;
            }
            int room = Math.min(limit, existing.getMaxCount()) - existing.getCount();
            if (room <= 0) {
                continue;
            }
            int moved = Math.min(room, stack.getCount());
            existing.increment(moved);
            stack.decrement(moved);
        }

        for (int i = from; i < to && !stack.isEmpty(); i++) {
            if (!target.get(i).isEmpty()) {
                continue;
            }
            int moved = Math.min(limit, stack.getCount());
            ItemStack placed = stack.copy();
            placed.setCount(moved);
            target.set(i, placed);
            stack.decrement(moved);
        }

        return stack.isEmpty();
    }
}
