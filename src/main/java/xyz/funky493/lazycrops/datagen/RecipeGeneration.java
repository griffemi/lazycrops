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

    /** Eight around an empty centre, the chest/furnace shape. */
    private void ring(Consumer<RecipeJsonProvider> exporter, Item fill, Item output, Identifier recipeId) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, output)
                .pattern("fff")
                .pattern("f f")
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
                tagDonut(exporter, LazyCoreItems.getItemFromCropLevel(cropBlock.getLevel()), ((LazyTagCropBlock) cropBlock).productTag, cropBlock.seedsItem, new Identifier("lazycrops", cropBlock.seedsId + "_from_donut"));
            } else if (cropBlock instanceof LazyItemCropBlock) {
                donut(exporter, LazyCoreItems.getItemFromCropLevel(cropBlock.getLevel()), ((LazyItemCropBlock) cropBlock).product, cropBlock.seedsItem, new Identifier("lazycrops", cropBlock.seedsId + "_from_donut"));
                inputOutput(exporter, cropBlock.seedsItem, ((LazyItemCropBlock) cropBlock).product, new Identifier("lazycrops", cropBlock.seedsId + "_from_input_output"));
            } else if (cropBlock instanceof LazyEntityCropBlock) {
                donut(exporter, LazyCoreItems.getItemFromCropLevel(cropBlock.getLevel()), ((LazyEntityCropBlock) cropBlock).craftItem, cropBlock.seedsItem, new Identifier("lazycrops", cropBlock.seedsId + "_from_donut"));
                inputOutput(exporter, cropBlock.seedsItem, ((LazyEntityCropBlock) cropBlock).craftItem, new Identifier("lazycrops", cropBlock.seedsId + "_from_input_output"));
            }
        }
        threeByThree(exporter, LazyCoreItems.LAZY_SEEDS, LazyCoreItems.LAZIER_SEEDS, new Identifier("lazycrops", "lazier_seeds_from_three_by_three"));
        reverseThreeByThree(exporter, LazyCoreItems.LAZIER_SEEDS, LazyCoreItems.LAZY_SEEDS, new Identifier("lazycrops", "lazy_seeds_from_lazier_seeds"));
        threeByThree(exporter, LazyCoreItems.LAZIER_SEEDS, LazyCoreItems.LAZIEST_SEEDS, new Identifier("lazycrops", "laziest_seeds_from_three_by_three"));
        reverseThreeByThree(exporter, LazyCoreItems.LAZIEST_SEEDS, LazyCoreItems.LAZIER_SEEDS, new Identifier("lazycrops", "lazier_seeds_from_laziest_seeds"));

        threeByThree(exporter, Blocks.FARMLAND.asItem(), LazyBlocks.INVINCIBLE_FARMLAND_ITEM, new Identifier("lazycrops", "invincible_farmland_from_three_by_three"));

        // Eight shards make a skull, so a wither still costs 24 harvests.
        ring(exporter, LazyCoreItems.WITHER_SKULL_SHARD, Items.WITHER_SKELETON_SKULL, new Identifier("lazycrops", "wither_skeleton_skull_from_shards"));
    }
}
