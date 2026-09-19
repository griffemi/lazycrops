package xyz.funky493.lazycrops.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import xyz.funky493.lazycrops.LazyCrops;
import xyz.funky493.lazycrops.blocks.LazyBlocks;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlock;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlocks;

import java.nio.file.Files;
import java.nio.file.Path;

import static xyz.funky493.lazycrops.LazyCrops.MODID;

public class LanguageEnglishAmericanGeneration extends FabricLanguageProvider {
    private final Path existingFilePath;
    public LanguageEnglishAmericanGeneration(FabricDataOutput dataOutput) {
        super(dataOutput, "en_us");
        try {
            existingFilePath = dataOutput.getModContainer().findPath("assets/lazycrops/lang/existing/en_us.json").get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find existing language file!", e);
        }
    }

    private String snakeToTitle(String input) {
        String[] words = input.split("_");
        StringBuilder output = new StringBuilder();
        for (String word : words) {
            output.append(word.substring(0, 1).toUpperCase()).append(word.substring(1)).append(" ");
        }
        return output.toString().trim();
    }

    private void advancement(TranslationBuilder translationBuilder, String advancementId, String title, String description) {
        translationBuilder.add("advancements." + MODID + "." + advancementId + ".title", title);
        translationBuilder.add("advancements."  + MODID + "." + advancementId + ".description", description);
    }

    private boolean alreadyExists(String langKey){
        try {
            return Files.readString(existingFilePath).contains(langKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read existing language file!", e);
        }
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {

        LazyCrops.LOGGER.info(existingFilePath.toFile().toString());

        for (LazyCropBlock cropBlock : LazyCropBlocks.CROP_BLOCKS) {
            if (alreadyExists(cropBlock.cropId)) {
                continue;
            }
            translationBuilder.add(cropBlock, snakeToTitle(cropBlock.cropId));
            translationBuilder.add(cropBlock.seedsItem, snakeToTitle(cropBlock.seedsId));
        }

        translationBuilder.add(LazyCrops.ITEM_GROUP, "Adeya's Resource Crops");

        //#region Core items and other blocks

        // Display names only. The item IDs stay lazy/lazier/laziest_seeds so that stacks
        // already in players' inventories on the live server survive the rename.
        translationBuilder.add("item." + MODID + ".lazy_seeds", "Weak Seed Essence");
        translationBuilder.add("item." + MODID + ".lazier_seeds", "Standard Seed Essence");
        translationBuilder.add("item." + MODID + ".laziest_seeds", "Rich Seed Essence");
        translationBuilder.add("item." + MODID + ".wither_skull_shard", "Wither Skull Shard");
        translationBuilder.add("block." + MODID + ".invincible_farmland", "Invincible Farmland");
        translationBuilder.add("item." + MODID + ".invincible_farmland", "Invincible Farmland");

        translationBuilder.add("block." + MODID + ".harvester", "Harvester");
        translationBuilder.add("item." + MODID + ".harvester", "Harvester");
        translationBuilder.add("block." + MODID + ".essence_extractor", "Seed Essence Extractor");
        translationBuilder.add("item." + MODID + ".essence_extractor", "Seed Essence Extractor");

        translationBuilder.add("container." + MODID + ".harvester", "Harvester");
        translationBuilder.add("container." + MODID + ".essence_extractor", "Seed Essence Extractor");

        translationBuilder.add("gui." + MODID + ".radius", "%sx%s");
        translationBuilder.add("gui." + MODID + ".radius.tooltip", "Harvest area. Always 3 blocks tall: this level, one above, one below.");
        translationBuilder.add("gui." + MODID + ".energy", "%s / %s E");
        translationBuilder.add("gui." + MODID + ".extract_chance", "Each seed has a 50% chance to yield essence.");
        translationBuilder.add("gui." + MODID + ".status.idle", "Idle");
        translationBuilder.add("gui." + MODID + ".status.working", "Working");
        translationBuilder.add("gui." + MODID + ".status.no_power", "Out of power");
        translationBuilder.add("gui." + MODID + ".status.full", "Inventory full");

        //#endregion

        //#region Advancements

        advancement(translationBuilder, "root", "Adeya's Resource Crops", "Obtain weak seed essence");
        advancement(translationBuilder, "lazier_seeds", "Standard Seed Essence", "Refine standard seed essence to be more lazy");
        advancement(translationBuilder, "laziest_seeds", "Rich Seed Essence", "I am become lazy, the doer of nothing");

        //#endregion

        try {
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }
}
