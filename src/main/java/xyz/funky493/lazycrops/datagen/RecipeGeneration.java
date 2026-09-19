package xyz.funky493.lazycrops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import xyz.funky493.lazycrops.blocks.LazyBlocks;
import xyz.funky493.lazycrops.cropblocks.*;


import java.util.function.Consumer;

public class RecipeGeneration extends FabricRecipeProvider {
    public RecipeGeneration(FabricDataOutput output) {
        super(output);
    }

    private void donut(Consumer<RecipeJsonProvider> exporter, Item middle, Item surrounding, Item output, Identifier recipeId) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, output)
                .pattern("sss")
                .pattern("sms")
                .pattern("sss")
                .input('s', surrounding)
                .input('m', middle)
                .criterion(FabricRecipeProvider.hasItem(middle), FabricRecipeProvider.conditionsFromItem(middle))
                .criterion(FabricRecipeProvider.hasItem(surrounding), FabricRecipeProvider.conditionsFromItem(surrounding))
                .offerTo(exporter, recipeId);
    }

    /**
     * Donut whose surrounding ingredient is a tag rather than a concrete item, for crops
     * bound to another mod's material. There is no matching reverse recipe: a recipe result
     * must be a concrete item, and a tag cannot name one.
     */
    private void tagDonut(Consumer<RecipeJsonProvider> exporter, Item middle, TagKey<Item> surrounding, Item output, Identifier recipeId) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, output)
                .pattern("sss")
                .pattern("sms")
                .pattern("sss")
                .input('s', surrounding)
                .input('m', middle)
                .criterion(FabricRecipeProvider.hasItem(middle), FabricRecipeProvider.conditionsFromItem(middle))
                .criterion("has_" + surrounding.id().getPath(), FabricRecipeProvider.conditionsFromTag(surrounding))
                .offerTo(exporter, recipeId);
    }

    private void threeByThree(Consumer<RecipeJsonProvider> exporter, Item fill, Item output, Identifier recipeId) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, output)
                .pattern("fff")
                .pattern("fff")
                .pattern("fff")
                .input('f', fill)
                .criterion(FabricRecipeProvider.hasItem(fill), FabricRecipeProvider.conditionsFromItem(fill))
                .offerTo(exporter, recipeId);
    }

    private void reverseThreeByThree(Consumer<RecipeJsonProvider> exporter, Item input, Item output, Identifier recipeId) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, output, 9)
                .input(input)
                .criterion(FabricRecipeProvider.hasItem(input), FabricRecipeProvider.conditionsFromItem(input))
                .offerTo(exporter, recipeId);
    }

    private void inputOutput(Consumer<RecipeJsonProvider> exporter, Item input, Item output, Identifier recipeId) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, output)
                .input(input)
                .criterion(FabricRecipeProvider.hasItem(input), FabricRecipeProvider.conditionsFromItem(input))
                .offerTo(exporter, recipeId);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        for (LazyCropBlock cropBlock : LazyCropBlocks.CROP_BLOCKS) {
            if (cropBlock instanceof LazyTagCropBlock) {
                tagDonut(exporter, SeedEssenceItems.getItemFromCropLevel(cropBlock.getLevel()), ((LazyTagCropBlock) cropBlock).productTag, cropBlock.seedsItem, new Identifier("resourcecrops", cropBlock.seedsId + "_from_donut"));
            } else if (cropBlock instanceof LazyItemCropBlock) {
                donut(exporter, SeedEssenceItems.getItemFromCropLevel(cropBlock.getLevel()), ((LazyItemCropBlock) cropBlock).product, cropBlock.seedsItem, new Identifier("resourcecrops", cropBlock.seedsId + "_from_donut"));
                inputOutput(exporter, cropBlock.seedsItem, ((LazyItemCropBlock) cropBlock).product, new Identifier("resourcecrops", cropBlock.seedsId + "_from_input_output"));
            } else if (cropBlock instanceof LazyEntityCropBlock) {
                donut(exporter, SeedEssenceItems.getItemFromCropLevel(cropBlock.getLevel()), ((LazyEntityCropBlock) cropBlock).craftItem, cropBlock.seedsItem, new Identifier("resourcecrops", cropBlock.seedsId + "_from_donut"));
                inputOutput(exporter, cropBlock.seedsItem, ((LazyEntityCropBlock) cropBlock).craftItem, new Identifier("resourcecrops", cropBlock.seedsId + "_from_input_output"));
            }
        }
        threeByThree(exporter, SeedEssenceItems.WEAK_SEED_ESSENCE, SeedEssenceItems.STANDARD_SEED_ESSENCE, new Identifier("resourcecrops", "standard_seed_essence_from_three_by_three"));
        reverseThreeByThree(exporter, SeedEssenceItems.STANDARD_SEED_ESSENCE, SeedEssenceItems.WEAK_SEED_ESSENCE, new Identifier("resourcecrops", "weak_seed_essence_from_standard_seed_essence"));
        threeByThree(exporter, SeedEssenceItems.STANDARD_SEED_ESSENCE, SeedEssenceItems.RICH_SEED_ESSENCE, new Identifier("resourcecrops", "rich_seed_essence_from_three_by_three"));
        reverseThreeByThree(exporter, SeedEssenceItems.RICH_SEED_ESSENCE, SeedEssenceItems.STANDARD_SEED_ESSENCE, new Identifier("resourcecrops", "standard_seed_essence_from_rich_seed_essence"));

        threeByThree(exporter, Blocks.FARMLAND.asItem(), LazyBlocks.INVINCIBLE_FARMLAND_ITEM, new Identifier("resourcecrops", "invincible_farmland_from_three_by_three"));

        // Eight shards round a weak essence make a skull, so a wither still costs 24 harvests
        // plus the essence the player could otherwise have spent on another seed.
        donut(exporter, SeedEssenceItems.WEAK_SEED_ESSENCE, SeedEssenceItems.WITHER_SKULL_SHARD, Items.WITHER_SKELETON_SKULL, new Identifier("resourcecrops", "wither_skeleton_skull_from_shards"));

        // Machines. Neither shape fits the donut/ring helpers, which assume a uniform surround.
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, LazyBlocks.HARVESTER_ITEM)
                .pattern("sss")
                .pattern("imi")
                .pattern("sss")
                .input('s', Items.STONE_BRICKS)
                .input('i', Items.IRON_INGOT)
                .input('m', SeedEssenceItems.WEAK_SEED_ESSENCE)
                .criterion(FabricRecipeProvider.hasItem(SeedEssenceItems.WEAK_SEED_ESSENCE), FabricRecipeProvider.conditionsFromItem(SeedEssenceItems.WEAK_SEED_ESSENCE))
                .offerTo(exporter, new Identifier("resourcecrops", "harvester"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, LazyBlocks.ESSENCE_EXTRACTOR_ITEM)
                .pattern("bdb")
                .pattern("bmb")
                .pattern("brb")
                .input('b', Items.POLISHED_BLACKSTONE_BRICKS)
                .input('d', Items.DIAMOND)
                .input('m', SeedEssenceItems.RICH_SEED_ESSENCE)
                .input('r', Items.REDSTONE)
                .criterion(FabricRecipeProvider.hasItem(SeedEssenceItems.RICH_SEED_ESSENCE), FabricRecipeProvider.conditionsFromItem(SeedEssenceItems.RICH_SEED_ESSENCE))
                .offerTo(exporter, new Identifier("resourcecrops", "essence_extractor"));
    }
}
