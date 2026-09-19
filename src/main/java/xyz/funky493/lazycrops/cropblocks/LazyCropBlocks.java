package xyz.funky493.lazycrops.cropblocks;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import xyz.funky493.lazycrops.LazyCrops;

import java.util.Map;

public class LazyCropBlocks {

    /** Common item tag, e.g. c:tin_ingots. */
    private static TagKey<Item> c(String path) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier("c", path));
    }

    /** One of our own product tags, e.g. resourcecrops:products/fiery. */
    private static TagKey<Item> own(String name) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier(LazyCrops.MODID, "products/" + name));
    }

    /**
     * Modded products with no common tag to bind to.
     * <p>
     * We can't reference these as concrete Items: their mods aren't on the datagen
     * classpath, and a generated loot table naming an absent item fails to parse on load.
     * So TagGeneration emits a resourcecrops:products/&lt;name&gt; tag per entry using
     * addOptional, which writes {@code "required": false} -- the tag resolves to nothing
     * when the mod is missing instead of erroring, and the crop still registers either way.
     */
    public static final Map<String, String> OPTIONAL_PRODUCTS = Map.ofEntries(
            Map.entry("fiery", "twilightforest:fiery_ingot"),
            Map.entry("knightmetal", "twilightforest:knightmetal_ingot"),
            Map.entry("steeleaf", "twilightforest:steeleaf_ingot"),
            Map.entry("ironwood", "twilightforest:ironwood_ingot"),
            Map.entry("soul_crystal", "deeperdarker:soul_crystal"),
            Map.entry("amber", "betterend:amber_gem"),
            Map.entry("aurora_crystal", "betterend:aurora_crystal"),
            Map.entry("smaragdant", "betterend:smaragdant_crystal"),
            Map.entry("sulphur", "betterend:sulphur_crystal"),
            Map.entry("cincinnasite", "betternether:cincinnasite_ingot"),
            Map.entry("ambrosium", "aether:ambrosium_shard"));

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
    new LazyTagCropBlock("fluix", c("fluix"), 1),
    //#endregion

    //#region Vanilla ores
    new LazyItemCropBlock("coal", Items.COAL, 0),
    new LazyItemCropBlock("redstone", Items.REDSTONE, 1),
    new LazyItemCropBlock("lapis", Items.LAPIS_LAZULI, 1),
    new LazyItemCropBlock("glowstone", Items.GLOWSTONE_DUST, 1),
    new LazyItemCropBlock("prismarine", Items.PRISMARINE_SHARD, 1),
    new LazyItemCropBlock("amethyst", Items.AMETHYST_SHARD, 1),
    new LazyItemCropBlock("echo_shard", Items.ECHO_SHARD, 2),
    //#endregion

    //#region Vanilla mob and misc drops
    new LazyItemCropBlock("bone", Items.BONE, 0),
    new LazyItemCropBlock("string", Items.STRING, 0),
    new LazyItemCropBlock("spider_eye", Items.SPIDER_EYE, 0),
    new LazyItemCropBlock("feather", Items.FEATHER, 0),
    new LazyItemCropBlock("flint", Items.FLINT, 0),
    new LazyItemCropBlock("clay", Items.CLAY_BALL, 0),
    // White, because it is the one colour every other colour dyes from.
    new LazyItemCropBlock("wool", Items.WHITE_WOOL, 0),
    new LazyItemCropBlock("ink_sac", Items.INK_SAC, 0),
    // Lazy tier: with the TNT crop gone this is the only homegrown explosive route.
    new LazyItemCropBlock("gunpowder", Items.GUNPOWDER, 0),
    new LazyItemCropBlock("leather", Items.LEATHER, 0),
    new LazyItemCropBlock("magma_cream", Items.MAGMA_CREAM, 1),
    new LazyItemCropBlock("ghast_tear", Items.GHAST_TEAR, 1),
    new LazyItemCropBlock("phantom_membrane", Items.PHANTOM_MEMBRANE, 2),
    new LazyItemCropBlock("shulker_shell", Items.SHULKER_SHELL, 2),
    new LazyItemCropBlock("wither_skull", SeedEssenceItems.WITHER_SKULL_SHARD, 2),
    //#endregion

    //#region Dyes -- the whole palette, all at Lazy tier. A dye crop is a convenience,
    // not a scarce resource: every colour is already reachable from flowers or a
    // crafting combination, so gating any of them behind Lazier/Laziest would only make
    // the set inconsistent. Blue dye undercuts the lapis crop a little, but only for dye
    // supply -- dye doesn't craft back into lapis, so the lapis economy is untouched.
    new LazyItemCropBlock("white_dye", Items.WHITE_DYE, 0),
    new LazyItemCropBlock("light_gray_dye", Items.LIGHT_GRAY_DYE, 0),
    new LazyItemCropBlock("gray_dye", Items.GRAY_DYE, 0),
    new LazyItemCropBlock("black_dye", Items.BLACK_DYE, 0),
    new LazyItemCropBlock("brown_dye", Items.BROWN_DYE, 0),
    new LazyItemCropBlock("red_dye", Items.RED_DYE, 0),
    new LazyItemCropBlock("orange_dye", Items.ORANGE_DYE, 0),
    new LazyItemCropBlock("yellow_dye", Items.YELLOW_DYE, 0),
    new LazyItemCropBlock("lime_dye", Items.LIME_DYE, 0),
    new LazyItemCropBlock("green_dye", Items.GREEN_DYE, 0),
    new LazyItemCropBlock("cyan_dye", Items.CYAN_DYE, 0),
    new LazyItemCropBlock("light_blue_dye", Items.LIGHT_BLUE_DYE, 0),
    new LazyItemCropBlock("blue_dye", Items.BLUE_DYE, 0),
    new LazyItemCropBlock("purple_dye", Items.PURPLE_DYE, 0),
    new LazyItemCropBlock("magenta_dye", Items.MAGENTA_DYE, 0),
    new LazyItemCropBlock("pink_dye", Items.PINK_DYE, 0),
    //#endregion

    //#region More modded metals, by common tag
    new LazyTagCropBlock("zinc", c("zinc_ingots"), 1),
    new LazyTagCropBlock("titanium", c("titanium_ingots"), 2),
    new LazyTagCropBlock("chrome", c("chromium_ingots"), 2),
    new LazyTagCropBlock("uranium", c("raw_uraninite_ores"), 2),
    new LazyTagCropBlock("sky_steel", c("sky_steel_ingots"), 2),
    //#endregion

    //#region Modded materials with no common tag -- bound to our own optional tags
    new LazyTagCropBlock("ironwood", own("ironwood"), 1),
    new LazyTagCropBlock("ambrosium", own("ambrosium"), 1),
    new LazyTagCropBlock("cincinnasite", own("cincinnasite"), 1),
    new LazyTagCropBlock("amber", own("amber"), 1),
    new LazyTagCropBlock("sulphur", own("sulphur"), 1),
    new LazyTagCropBlock("knightmetal", own("knightmetal"), 2),
    new LazyTagCropBlock("steeleaf", own("steeleaf"), 2),
    new LazyTagCropBlock("fiery", own("fiery"), 2),
    new LazyTagCropBlock("soul_crystal", own("soul_crystal"), 2),
    new LazyTagCropBlock("aurora_crystal", own("aurora_crystal"), 2),
    new LazyTagCropBlock("smaragdant", own("smaragdant"), 2)
    //#endregion
    };

}
