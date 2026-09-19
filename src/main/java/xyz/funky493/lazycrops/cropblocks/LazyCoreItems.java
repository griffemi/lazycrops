package xyz.funky493.lazycrops.cropblocks;

import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class LazyCoreItems {
    public static final Item LAZY_SEEDS = new Item(new Item.Settings());
    public static final Item LAZIER_SEEDS = new Item(new Item.Settings());
    public static final Item LAZIEST_SEEDS = new Item(new Item.Settings());

    /**
     * What the wither skull crop drops. The crop deliberately does not drop whole skulls:
     * eight shards ring-craft into one, so summoning a wither still costs 24 harvests. It removes
     * the tedium of farming wither skeletons without removing the boss fight.
     */
    public static final Item WITHER_SKULL_SHARD = new Item(new Item.Settings());

    public static Item getItemFromCropLevel(int cropLevel) {
        return switch (cropLevel) {
            case 0 -> LAZY_SEEDS;
            case 1 -> LAZIER_SEEDS;
            case 2 -> LAZIEST_SEEDS;
            default -> null;
        };
    }

    public static final Map<Item, String> ITEMS = new HashMap<>() {{
        put(LAZY_SEEDS, "lazy_seeds");
        put(LAZIER_SEEDS, "lazier_seeds");
        put(LAZIEST_SEEDS, "laziest_seeds");
        put(WITHER_SKULL_SHARD, "wither_skull_shard");
    }};
}
