package xyz.funky493.lazycrops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;
import xyz.funky493.lazycrops.cropblocks.SeedEssenceItems;

import java.util.function.BiConsumer;

public class ChestLootTableGeneration extends SimpleFabricLootTableProvider {
    public ChestLootTableGeneration(FabricDataOutput output) {
        super(output, LootContextTypes.CHEST);
    }

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
        exporter.accept(new Identifier("resourcecrops", "inject/chests/desert_pyramid"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(SeedEssenceItems.WEAK_SEED_ESSENCE).conditionally(RandomChanceLootCondition.builder(0.3f)))
                        .with(ItemEntry.builder(SeedEssenceItems.STANDARD_SEED_ESSENCE).conditionally(RandomChanceLootCondition.builder(0.05f)))
                        .with(ItemEntry.builder(SeedEssenceItems.RICH_SEED_ESSENCE).conditionally(RandomChanceLootCondition.builder(0.01f)))
                )
        );
    }
}
