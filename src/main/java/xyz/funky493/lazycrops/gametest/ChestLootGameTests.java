package xyz.funky493.lazycrops.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.loot.LootDataType;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import xyz.funky493.lazycrops.loot.EssenceChestLoot;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/** Guards the seed essence chest injections against the two ways they silently break. */
public class ChestLootGameTests implements FabricGameTest {

    /**
     * Every injected table must actually exist. The mod previously injected into three chest
     * tables while shipping only one of the pools, so two structures rolled nothing at all and
     * nothing anywhere reported an error.
     */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void everyInjectedTableIsShipped(TestContext context) {
        Collection<Identifier> loaded = context.getWorld().getServer().getLootManager()
                .getIds(LootDataType.LOOT_TABLES);

        List<Identifier> missing = new ArrayList<>();
        for (Identifier table : EssenceChestLoot.TABLES) {
            Identifier injectId = EssenceChestLoot.injectId(table);
            if (!loaded.contains(injectId)) {
                missing.add(injectId);
            }
        }

        context.assertTrue(missing.isEmpty(), "Missing generated inject loot tables: " + missing);
        context.complete();
    }

    /** Two chest tables mapping to one inject id would make them share a roll. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void injectIdsAreUnique(TestContext context) {
        List<Identifier> injectIds = EssenceChestLoot.TABLES.stream()
                .map(EssenceChestLoot::injectId)
                .toList();

        context.assertTrue(injectIds.size() == injectIds.stream().distinct().count(),
                "Two chest tables resolved to the same inject id");
        context.complete();
    }
}
