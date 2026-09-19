package xyz.funky493.lazycrops.machines;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import xyz.funky493.lazycrops.blocks.LazyBlocks;

/**
 * Holder for the machine block entity types, mirroring how {@link LazyBlocks} holds blocks.
 * Actual registration happens in {@code LazyCrops.onInitialize()}.
 */
public class LazyBlockEntities {

    public static final BlockEntityType<HarvesterBlockEntity> HARVESTER =
            FabricBlockEntityTypeBuilder.create(HarvesterBlockEntity::new, LazyBlocks.HARVESTER).build();

    public static final BlockEntityType<EssenceExtractorBlockEntity> ESSENCE_EXTRACTOR =
            FabricBlockEntityTypeBuilder.create(EssenceExtractorBlockEntity::new, LazyBlocks.ESSENCE_EXTRACTOR).build();
}
