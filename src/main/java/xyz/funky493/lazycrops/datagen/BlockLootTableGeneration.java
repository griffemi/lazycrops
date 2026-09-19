package xyz.funky493.lazycrops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.predicate.StatePredicate;
import xyz.funky493.lazycrops.LazyCrops;
import xyz.funky493.lazycrops.blocks.LazyBlocks;
import xyz.funky493.lazycrops.cropblocks.*;

public class BlockLootTableGeneration extends FabricBlockLootTableProvider {

    protected BlockLootTableGeneration(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    /**
     * cropDrops() in the vanilla provider needs a concrete product item, which a tag-bound
     * crop does not have. This mirrors it: always drop the seed, and once fully grown also
     * drop one item from the product tag. TagEntry with expand=true turns the tag into one
     * weighted choice per member, so a tag holding several equivalent ingots picks one --
     * rather than dropping all of them, which is what expand=false would do.
     */
    private LootTable.Builder tagCropDrops(LazyTagCropBlock crop) {
        LootCondition.Builder mature = BlockStatePropertyLootCondition.builder(crop)
                .properties(StatePredicate.Builder.create().exactMatch(crop.getAgeProperty(), 7));
        return LootTable.builder()
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(crop.seedsItem)))
                .pool(LootPool.builder()
                        .conditionally(mature)
                        .with(TagEntry.expandBuilder(crop.productTag)));
    }

    @Override
    public void generate() {
        LazyCrops.LOGGER.info("Generating loot tables...");
        for (LazyCropBlock crop : LazyCropBlocks.CROP_BLOCKS) {
            if (crop instanceof LazyTagCropBlock) {
                addDrop(crop, tagCropDrops((LazyTagCropBlock) crop));
            } else if (crop instanceof LazyItemCropBlock && ((LazyItemCropBlock) crop).noDrop) {
                addDrop(crop, cropDrops(crop, Items.AIR, crop.seedsItem, BlockStatePropertyLootCondition.builder(crop).properties(StatePredicate.Builder.create().exactMatch(crop.getAgeProperty(), 7))));
            } else if (crop instanceof LazyItemCropBlock) {
                addDrop(crop, cropDrops(crop, ((LazyItemCropBlock) crop).product, crop.seedsItem, BlockStatePropertyLootCondition.builder(crop).properties(StatePredicate.Builder.create().exactMatch(crop.getAgeProperty(), 7))));
            } else {
                addDrop(crop, cropDrops(crop, Items.AIR, crop.seedsItem, BlockStatePropertyLootCondition.builder(crop).properties(StatePredicate.Builder.create().exactMatch(crop.getAgeProperty(), 7))));
            }
            LazyCrops.LOGGER.info("- Added loot table for " + crop.cropId + ".");
        }

        // Mandatory, not optional: these blocks have their own loot table id in our namespace,
        // and the provider validates strictly, so omitting them fails datagen outright.
        // (Invincible farmland escapes this only because Settings.copy inherits vanilla's id.)
        addDrop(LazyBlocks.HARVESTER);
        addDrop(LazyBlocks.ESSENCE_EXTRACTOR);

        LazyCrops.LOGGER.info("Generated loot tables.");
    }
}
