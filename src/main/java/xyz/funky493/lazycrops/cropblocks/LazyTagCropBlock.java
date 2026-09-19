package xyz.funky493.lazycrops.cropblocks;

import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

/**
 * A crop whose product comes from another mod, bound by common ({@code c:}) item tag rather
 * than a concrete item.
 * <p>
 * Binding by tag matters for two reasons. The block and its seed register unconditionally, so
 * the registry is identical whether or not the providing mod is installed -- which keeps
 * clients and servers in sync. And a generated loot table naming a missing item id fails to
 * parse on load, whereas a tag that resolves to nothing simply drops nothing.
 * <p>
 * The trade-off: a recipe *output* has to be a concrete item, so unlike {@link
 * LazyItemCropBlock} these crops get no seed-back-into-material recipe. The seed is still
 * craftable from the tag, and the crop still drops from the tag.
 */
public class LazyTagCropBlock extends LazyCropBlock {
    public final TagKey<Item> productTag;

    public LazyTagCropBlock(String cropId, String seedsId, TagKey<Item> productTag, int level) {
        super(cropId, seedsId, level);
        this.productTag = productTag;
    }

    public LazyTagCropBlock(String id, TagKey<Item> productTag, int level) {
        super(id, level);
        this.productTag = productTag;
    }
}
