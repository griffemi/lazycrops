package xyz.funky493.lazycrops.cropblocks;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class LazyCropBlocks {

    /** Common item tag, e.g. c:tin_ingots. */
    private static TagKey<Item> c(String path) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier("c", path));
    }

    public static final LazyCropBlock[] CROP_BLOCKS = new LazyCropBlock[]{
    new LazyItemCropBlock("dirt", Items.DIRT, 0),
    new LazyItemCropBlock("sand", Items.SAND, 0),
    new LazyItemCropBlock("gravel", Items.GRAVEL, 0),
    new LazyItemCropBlock("cobblestone", Items.COBBLESTONE, 0),
    new LazyItemCropBlock("oak_log", Items.OAK_LOG, 0),
    new LazyItemCropBlock("birch_log", Items.BIRCH_LOG, 0),
    new LazyItemCropBlock("spruce_log", Items.SPRUCE_LOG, 0),
    new LazyItemCropBlock("jungle_log", Items.JUNGLE_LOG, 0),
    new LazyItemCropBlock("acacia_log", Items.ACACIA_LOG, 0),
    new LazyItemCropBlock("dark_oak_log", Items.DARK_OAK_LOG, 0),
    new LazyItemCropBlock("mangrove_log", Items.MANGROVE_LOG, 0),
    new LazyItemCropBlock("cherry_log", Items.CHERRY_LOG, 0),
    new LazyItemCropBlock("obsidian", Items.OBSIDIAN, 0),
    new LazyItemCropBlock("diamond", Items.DIAMOND, 2),
    new LazyItemCropBlock("egg", Items.EGG, 0),
    new LazyItemCropBlock("honeycomb", Items.HONEYCOMB, 1),
    new LazyItemCropBlock("rotten_flesh", Items.ROTTEN_FLESH, 0),
    new LazyItemCropBlock("cake", Items.CAKE, 2),
    new LazyItemCropBlock("wheat_seeds", Items.WHEAT_SEEDS, 0),
    new LazyEntityCropBlock("cow", 0, EntityType.COW, Items.BEEF),
    new LazyEntityCropBlock("pig", 0, EntityType.PIG, Items.PORKCHOP),
    new LazyEntityCropBlock("chicken", 0, EntityType.CHICKEN, Items.CHICKEN),
    new LazyExperienceCropBlock("experience", 2),
    new LazyTntCropBlock("tnt", 0),

    //#region Metals
    new LazyItemCropBlock("iron", Items.RAW_IRON, 1),
    new LazyItemCropBlock("gold", Items.RAW_GOLD, 1),
    new LazyItemCropBlock("copper",  Items.RAW_COPPER, 1),
    //#endregion

    //#region Stone types
    new LazyItemCropBlock("granite", Items.GRANITE, 0),
    new LazyItemCropBlock("diorite", Items.DIORITE, 0),
    new LazyItemCropBlock("andesite", Items.ANDESITE, 0),
    new LazyItemCropBlock("deepslate", Items.DEEPSLATE, 0),
    new LazyItemCropBlock("tuff", Items.TUFF, 0),
    new LazyItemCropBlock("calcite", Items.CALCITE, 0),
    new LazyItemCropBlock("basalt", Items.BASALT, 0),
    new LazyItemCropBlock("blackstone", Items.BLACKSTONE, 0),
    new LazyItemCropBlock("netherrack", Items.NETHERRACK, 0),
    new LazyItemCropBlock("end_stone", Items.END_STONE, 1),
    //#endregion

    //#region Vanilla materials
    new LazyItemCropBlock("slime", Items.SLIME_BALL, 0),
    new LazyItemCropBlock("quartz", Items.QUARTZ, 1),
    new LazyItemCropBlock("blaze_rod", Items.BLAZE_ROD, 1),
    new LazyItemCropBlock("emerald", Items.EMERALD, 1),
    new LazyItemCropBlock("ender_pearl", Items.ENDER_PEARL, 2),
    // Scrap rather than the ingot, to match the other metals growing their raw form
    // (raw iron/gold/copper) -- so the gold cost and the smithing step still apply.
    new LazyItemCropBlock("netherite", Items.NETHERITE_SCRAP, 2),
    //#endregion

    //#region Fluids -- these drop the filled bucket, so they substitute anywhere a
    // water/lava bucket is used. Not an iron exploit: the mod already grows iron.
    new LazyItemCropBlock("water", Items.WATER_BUCKET, 1),
    new LazyItemCropBlock("lava", Items.LAVA_BUCKET, 2),
    //#endregion

    //#region Modded materials, bound by common tag so they register even when the
    // providing mod is absent (see LazyTagCropBlock).
    new LazyTagCropBlock("tin", c("tin_ingots"), 1),
    new LazyTagCropBlock("lead", c("lead_ingots"), 1),
    new LazyTagCropBlock("silver", c("silver_ingots"), 1),
    new LazyTagCropBlock("nickel", c("nickel_ingots"), 1),
    new LazyTagCropBlock("aluminum", c("aluminum_ingots"), 1),
    new LazyTagCropBlock("certus_quartz", c("certus_quartz"), 1),
    new LazyTagCropBlock("platinum", c("platinum_ingots"), 2),
    new LazyTagCropBlock("tungsten", c("tungsten_ingots"), 2),
    new LazyTagCropBlock("iridium", c("iridium_ingots"), 2),
    new LazyTagCropBlock("fluix", c("fluix"), 2)
    //#endregion
    };

}
