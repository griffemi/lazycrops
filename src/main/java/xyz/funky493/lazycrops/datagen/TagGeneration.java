package xyz.funky493.lazycrops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import xyz.funky493.lazycrops.LazyCrops;
import xyz.funky493.lazycrops.cropblocks.SeedEssenceItems;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlock;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlocks;

import java.util.concurrent.CompletableFuture;

public class TagGeneration extends FabricTagProvider.ItemTagProvider {
    public TagGeneration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    //"easy common key"
    private static TagKey<Item> ezCKey(String path) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier("c", path));
    }

    private static TagKey<Item> ownKey(String name) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier(LazyCrops.MODID, "products/" + name));
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        // One tag per modded product that has no common tag. addOptional writes
        // "required": false, so the tag resolves to nothing rather than erroring when the
        // providing mod is absent.
        LazyCropBlocks.OPTIONAL_PRODUCTS.forEach((name, itemId) ->
                getOrCreateTagBuilder(ownKey(name))
                        .addOptional(new Identifier(itemId))
                        .setReplace(false));

        getOrCreateTagBuilder(ezCKey("seeds")).add(SeedEssenceItems.WEAK_SEED_ESSENCE).add(SeedEssenceItems.STANDARD_SEED_ESSENCE).add(SeedEssenceItems.RICH_SEED_ESSENCE).setReplace(false);
        for (LazyCropBlock cropBlock : LazyCropBlocks.CROP_BLOCKS) {
            getOrCreateTagBuilder(ezCKey("seeds")).add(cropBlock.seedsItem).setReplace(false);
            getOrCreateTagBuilder(ezCKey("seeds/" + cropBlock.cropId.split("_")[0])).add(cropBlock.seedsItem).setReplace(false);
        }
    }
}
