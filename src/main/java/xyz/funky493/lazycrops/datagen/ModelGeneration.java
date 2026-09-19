package xyz.funky493.lazycrops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import xyz.funky493.lazycrops.LazyCrops;
import xyz.funky493.lazycrops.blocks.LazyBlocks;
import xyz.funky493.lazycrops.cropblocks.LazyCoreItems;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlock;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlocks;

public class ModelGeneration extends FabricModelProvider {
    public ModelGeneration(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        LazyCrops.LOGGER.info("Generating block state models...");
        for (LazyCropBlock crop : LazyCropBlocks.CROP_BLOCKS) {
            blockStateModelGenerator.registerCrop(crop, crop.getAgeProperty(), 0, 1, 2, 3, 4, 5, 6, 7);
            LazyCrops.LOGGER.info("- Added block state model for " + crop.cropId + ".");
        }
        // The machines are plain cubes; registerSimpleCubeAll emits the blockstate, the block
        // model and the parented item model in one go.
        blockStateModelGenerator.registerSimpleCubeAll(LazyBlocks.HARVESTER);
        blockStateModelGenerator.registerSimpleCubeAll(LazyBlocks.ESSENCE_EXTRACTOR);
        LazyCrops.LOGGER.info("Generated block state models.");
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        LazyCrops.LOGGER.info("Generating item models...");
        itemModelGenerator.register(LazyCoreItems.LAZY_SEEDS, Models.GENERATED);
        itemModelGenerator.register(LazyCoreItems.LAZIER_SEEDS, Models.GENERATED);
        itemModelGenerator.register(LazyCoreItems.LAZIEST_SEEDS, Models.GENERATED);
        itemModelGenerator.register(LazyCoreItems.WITHER_SKULL_SHARD, Models.GENERATED);
        LazyCrops.LOGGER.info("Generated item models.");
    }
}
