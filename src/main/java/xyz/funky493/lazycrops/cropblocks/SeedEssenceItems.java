package xyz.funky493.lazycrops.cropblocks;

import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class SeedEssenceItems {
    public static final Item WEAK_SEED_ESSENCE = new Item(new Item.Settings());
    public static final Item STANDARD_SEED_ESSENCE = new Item(new Item.Settings());
    public static final Item RICH_SEED_ESSENCE = new Item(new Item.Settings());

    /**
     * What the wither skull crop drops. The crop deliberately does not drop whole skulls:
     * eight shards ring-craft into one, so summoning a wither still costs 24 harvests. It removes
     * the tedium of farming wither skeletons without removing the boss fight.
     */
    public static final Item WITHER_SKULL_SHARD = new Item(new Item.Settings());

    public static Item getItemFromCropLevel(int cropLevel) {
        return switch (cropLevel) {
            case 0 -> WEAK_SEED_ESSENCE;
            case 1 -> STANDARD_SEED_ESSENCE;
            case 2 -> RICH_SEED_ESSENCE;
            default -> null;
        };
    }

    public static final Map<Item, String> ITEMS = new HashMap<>() {{
        put(WEAK_SEED_ESSENCE, "weak_seed_essence");
        put(STANDARD_SEED_ESSENCE, "standard_seed_essence");
        put(RICH_SEED_ESSENCE, "rich_seed_essence");
        put(WITHER_SKULL_SHARD, "wither_skull_shard");
    }};
}
