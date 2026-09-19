package xyz.funky493.lazycrops.datagen.advancements;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.funky493.lazycrops.cropblocks.SeedEssenceItems;

import java.util.function.Consumer;

import static xyz.funky493.lazycrops.LazyCrops.MODID;

public class LazyAdvancements implements Consumer<Consumer<Advancement>>{
    @Override
    public void accept(Consumer<Advancement> advancementConsumer) {
        Advancement root = Advancement.Builder.create()
                .display(
                        SeedEssenceItems.WEAK_SEED_ESSENCE,
                        Text.translatable("advancements.resourcecrops.root.title"),
                        Text.translatable("advancements.resourcecrops.root.description"),
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("obtain_weak_seed_essence", InventoryChangedCriterion.Conditions.items(SeedEssenceItems.WEAK_SEED_ESSENCE))
                .build(advancementConsumer, MODID + "/root");
        Advancement standard_seed_essence = Advancement.Builder.create()
                .parent(root)
                .display(
                        SeedEssenceItems.STANDARD_SEED_ESSENCE,
                        Text.translatable("advancements.resourcecrops.standard_seed_essence.title"),
                        Text.translatable("advancements.resourcecrops.standard_seed_essence.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true,
                        true,
                        false
                )
                .criterion("obtain_standard_seed_essence", InventoryChangedCriterion.Conditions.items(SeedEssenceItems.STANDARD_SEED_ESSENCE))
                .build(advancementConsumer, MODID + "/standard_seed_essence");
        Advancement rich_seed_essence = Advancement.Builder.create()
                .parent(standard_seed_essence)
                .display(
                        SeedEssenceItems.RICH_SEED_ESSENCE,
                        Text.translatable("advancements.resourcecrops.rich_seed_essence.title"),
                        Text.translatable("advancements.resourcecrops.rich_seed_essence.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .criterion("obtain_rich_seed_essence", InventoryChangedCriterion.Conditions.items(SeedEssenceItems.RICH_SEED_ESSENCE))
                .build(advancementConsumer, MODID + "/rich_seed_essence");
    }
}
