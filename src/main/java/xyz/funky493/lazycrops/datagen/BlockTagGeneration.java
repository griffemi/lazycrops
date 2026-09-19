package xyz.funky493.lazycrops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import xyz.funky493.lazycrops.blocks.LazyBlocks;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlock;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlocks;

import java.util.concurrent.CompletableFuture;

/**
 * Block tags for the machines, so they drop when mined with the right tool.
 * <p>
 * Separate from {@link TagGeneration}, which is an ItemTagProvider and cannot emit block tags.
 */
public class BlockTagGeneration extends FabricTagProvider.BlockTagProvider {

    private static final TagKey<Block> COMMON_CROPS = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "crops"));

    public BlockTagGeneration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(LazyBlocks.HARVESTER)
                .add(LazyBlocks.ESSENCE_EXTRACTOR);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(LazyBlocks.HARVESTER)
                .add(LazyBlocks.ESSENCE_EXTRACTOR);

        // Every crop-growth/farming power in the Origins ecosystem (the Farmer class's doubled
        // bone meal and 2x harvest yield, Moth Fae's Verdant Touch, and others) targets blocks by
        // tag rather than by Java type, so an untagged custom CropBlock is invisible to them even
        // though it already implements Fertilizable correctly. minecraft:crops covers vanilla
        // farmer-villager behaviour too; c:crops is the Fabric convention tag other mods key off.
        for (LazyCropBlock cropBlock : LazyCropBlocks.CROP_BLOCKS) {
            getOrCreateTagBuilder(BlockTags.CROPS).add(cropBlock);
            getOrCreateTagBuilder(COMMON_CROPS).add(cropBlock);
        }
    }
}
