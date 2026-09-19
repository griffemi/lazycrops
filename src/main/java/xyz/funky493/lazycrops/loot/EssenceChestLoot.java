package xyz.funky493.lazycrops.loot;

import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Structure chests that get a seed essence roll injected into them.
 * <p>
 * One list, read by both the runtime injector in {@code LazyCrops#onInitialize} and the datagen
 * provider that writes the injected pools. They used to be declared separately, which silently
 * shipped injections pointing at tables that were never generated.
 * <p>
 * Modded entries cost nothing when their mod is absent: the loot table event only fires for tables
 * that actually load, so no dependency or version check is needed.
 */
public final class EssenceChestLoot {
    private EssenceChestLoot() {
    }

    private static Identifier yungsDesert(String path) {
        return new Identifier("betterdeserttemples", "chests/" + path);
    }

    private static Identifier vanillaStronghold(String path) {
        return new Identifier("minecraft", "chests/stronghold/" + path);
    }

    public static final List<Identifier> TABLES = List.of(
            // Vanilla structures.
            LootTables.ABANDONED_MINESHAFT_CHEST,
            LootTables.DESERT_PYRAMID_CHEST,
            LootTables.JUNGLE_TEMPLE_CHEST,
            LootTables.SIMPLE_DUNGEON_CHEST,
            LootTables.IGLOO_CHEST_CHEST,
            LootTables.PILLAGER_OUTPOST_CHEST,
            LootTables.SHIPWRECK_SUPPLY_CHEST,
            LootTables.UNDERWATER_RUIN_SMALL_CHEST,
            LootTables.UNDERWATER_RUIN_BIG_CHEST,
            LootTables.STRONGHOLD_CORRIDOR_CHEST,
            LootTables.STRONGHOLD_CROSSING_CHEST,
            LootTables.STRONGHOLD_LIBRARY_CHEST,
            LootTables.WOODLAND_MANSION_CHEST,
            LootTables.RUINED_PORTAL_CHEST,
            LootTables.VILLAGE_PLAINS_CHEST,
            LootTables.VILLAGE_DESERT_HOUSE_CHEST,
            LootTables.VILLAGE_SAVANNA_HOUSE_CHEST,
            LootTables.VILLAGE_TAIGA_HOUSE_CHEST,
            LootTables.VILLAGE_SNOWY_HOUSE_CHEST,

            // YUNG's Better Desert Temples.
            yungsDesert("tomb"),
            yungsDesert("tomb_pharaoh"),
            yungsDesert("pharaoh_hidden"),
            yungsDesert("storage"),
            yungsDesert("food_storage"),
            yungsDesert("library"),
            yungsDesert("lab"),
            yungsDesert("wardrobe"),
            yungsDesert("statue"),
            yungsDesert("pot"),

            // YUNG's Better Jungle Temples.
            new Identifier("betterjungletemples", "chests/treasure"),
            new Identifier("betterjungletemples", "chests/campsite"),

            // Dungeons and Taverns' stronghold rework splits the vanilla stronghold tables into
            // these, which only exist while that mod is loaded.
            vanillaStronghold("base"),
            vanillaStronghold("generic"),
            vanillaStronghold("jail"),
            vanillaStronghold("library"),
            vanillaStronghold("library_bookshelf"),
            vanillaStronghold("sewer")
    );

    /**
     * Where the injected pool for a given chest table lives. The namespace is part of the path so
     * that two mods using the same table name cannot collide.
     */
    public static Identifier injectId(Identifier tableId) {
        return new Identifier("resourcecrops", "inject/" + tableId.getNamespace() + "/" + tableId.getPath());
    }
}
