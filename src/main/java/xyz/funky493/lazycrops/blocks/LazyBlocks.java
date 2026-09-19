package xyz.funky493.lazycrops.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import xyz.funky493.lazycrops.machines.EssenceExtractorBlock;
import xyz.funky493.lazycrops.machines.HarvesterBlock;

public class LazyBlocks {
    public static final InvincibleFarmland INVINCIBLE_FARMLAND = new InvincibleFarmland(AbstractBlock.Settings.copy(Blocks.FARMLAND));
    public static final Item INVINCIBLE_FARMLAND_ITEM = new BlockItem(INVINCIBLE_FARMLAND, new Item.Settings());

    public static final HarvesterBlock HARVESTER = new HarvesterBlock(
            AbstractBlock.Settings.create().strength(3.5F).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final Item HARVESTER_ITEM = new BlockItem(HARVESTER, new Item.Settings());

    public static final EssenceExtractorBlock ESSENCE_EXTRACTOR = new EssenceExtractorBlock(
            AbstractBlock.Settings.create().strength(3.5F).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final Item ESSENCE_EXTRACTOR_ITEM = new BlockItem(ESSENCE_EXTRACTOR, new Item.Settings());
}
