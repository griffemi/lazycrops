package xyz.funky493.lazycrops.machines;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xyz.funky493.lazycrops.cropblocks.LazyCoreItems;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlock;

/**
 * Maps seed items back to the crop they plant.
 * <p>
 * No lookup table is needed: every crop's seed item is an {@code AliasedBlockItem} of the crop
 * block itself, so {@link Block#getBlockFromItem} inverts the relationship exactly. This also
 * deliberately avoids {@code CropBlock#getSeedsItem()}, which is protected and would otherwise
 * force an access widener.
 */
public final class LazySeedUtil {

    private LazySeedUtil() {
    }

    /** The crop this stack plants, or null if it isn't one of our seeds. */
    @Nullable
    public static LazyCropBlock cropOf(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Block block = Block.getBlockFromItem(stack.getItem());
        return block instanceof LazyCropBlock crop ? crop : null;
    }

    /**
     * Whether the extractor will accept this stack.
     * <p>
     * The three essence items are plain {@code Item}s rather than BlockItems, so they fail this
     * check and cannot be fed back in — there is no self-feeding loop to guard against.
     */
    public static boolean isLazySeed(ItemStack stack) {
        return cropOf(stack) != null;
    }

    /** The essence tier a given seed yields, or null if the stack isn't one of our seeds. */
    @Nullable
    public static Item essenceFor(ItemStack stack) {
        LazyCropBlock crop = cropOf(stack);
        return crop == null ? null : LazyCoreItems.getItemFromCropLevel(crop.getLevel());
    }
}
