package xyz.funky493.lazycrops.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import xyz.funky493.lazycrops.datagen.advancements.AdvancementGeneration;
import xyz.funky493.lazycrops.datagen.lang.LanguageEnglishAmericanGeneration;

public class DataGeneration implements DataGeneratorEntrypoint {

    private void addLangProviders(FabricDataGenerator.Pack pack) {
        pack.addProvider(LanguageEnglishAmericanGeneration::new);
    }
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModelGeneration::new);
        pack.addProvider(BlockLootTableGeneration::new);
        pack.addProvider(ChestLootTableGeneration::new);
        pack.addProvider(RecipeGeneration::new);
        pack.addProvider(AdvancementGeneration::new);
        pack.addProvider(TagGeneration::new);
        pack.addProvider(SeasonCropGeneration::new);
        addLangProviders(pack);
    }
}
