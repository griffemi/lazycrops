package xyz.funky493.lazycrops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import xyz.funky493.lazycrops.blocks.LazyBlocks;

import java.util.concurrent.CompletableFuture;

/**
 * Block tags for the machines, so they drop when mined with the right tool.
 * <p>
 * Separate from {@link TagGeneration}, which is an ItemTagProvider and cannot emit block tags.
 */
public class BlockTagGeneration extends FabricTagProvider.BlockTagProvider {

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
    }
}
