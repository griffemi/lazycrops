package xyz.funky493.lazycrops;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.funky493.lazycrops.blocks.InvincibleFarmland;
import xyz.funky493.lazycrops.blocks.LazyBlocks;
import xyz.funky493.lazycrops.cropblocks.LazyCoreItems;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlock;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlocks;
import xyz.funky493.lazycrops.machines.LazyBlockEntities;
import xyz.funky493.lazycrops.machines.LazyScreenHandlers;
import team.reborn.energy.api.EnergyStorage;

import java.util.Set;

public class LazyCrops implements ModInitializer {
	public static final String MODID = "lazycrops";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(MODID, "main_group"));

	public static final GameRules.Key<GameRules.BooleanRule> CAN_FERTILIZE_LAZYCROPS =
			GameRuleRegistry.register("canFertilizeLazyCrops", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

	/**
	 * When false (the default), lazy crops ignore vanilla's "sky/block light must be >= 9"
	 * requirement and grow underground or in a sealed room. They also stop being scaled by
	 * seasonal growth mods, because those hook CropBlock#randomTick and we no longer call it.
	 * <p>
	 * Set it to true to get stock vanilla behaviour back: the light gate returns, and seasonal
	 * mods apply again (see the shipped data/lazycrops/seasons/crop configs, which keep winter
	 * from being a hard stop in that case).
	 */
	public static final GameRules.Key<GameRules.BooleanRule> LAZY_CROPS_NEED_LIGHT =
			GameRuleRegistry.register("lazyCropsNeedLight", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));

	/**
	 * When true, <em>all</em> vanilla farmland stops being destroyed: it can't be trampled by
	 * anything landing on it, and it never dries out or reverts to dirt. This covers the two
	 * ways a farm actually dies, so it needs no new block and no client update -- it is pure
	 * server-side behaviour (see the FarmlandBlock mixin).
	 * <p>
	 * Farmland still reverts when a block is placed on top of it. That is a placement-validity
	 * check rather than damage, and suppressing it would leave farmland stranded under solid
	 * blocks.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> INVINCIBLE_FARMLAND =
			GameRuleRegistry.register("invincibleFarmland", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));

	@Override
	public void onInitialize() {

		LOGGER.info("Initializing Lazy Crops...");

		Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
				.displayName(Text.translatable("itemGroup." + MODID + ".main_group"))
				.icon(() -> new ItemStack(LazyCoreItems.LAZY_SEEDS))
				.build()
		);

		LOGGER.info("Registering other blocks...");
		Registry.register(Registries.BLOCK, new Identifier(MODID, "invincible_farmland"), LazyBlocks.INVINCIBLE_FARMLAND);
		Registry.register(Registries.ITEM, new Identifier(MODID, "invincible_farmland"), LazyBlocks.INVINCIBLE_FARMLAND_ITEM);
		ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(content -> {
			content.add(LazyBlocks.INVINCIBLE_FARMLAND_ITEM);
		});
		LOGGER.info("Registered other blocks.");

		LOGGER.info("Registering machines...");
		// Order matters: blocks and their items first, then the block entity types that
		// reference them, then the screen handlers, and only then the energy lookup -- which
		// needs the block entity types to already exist.
		Registry.register(Registries.BLOCK, new Identifier(MODID, "harvester"), LazyBlocks.HARVESTER);
		Registry.register(Registries.ITEM, new Identifier(MODID, "harvester"), LazyBlocks.HARVESTER_ITEM);
		Registry.register(Registries.BLOCK, new Identifier(MODID, "essence_extractor"), LazyBlocks.ESSENCE_EXTRACTOR);
		Registry.register(Registries.ITEM, new Identifier(MODID, "essence_extractor"), LazyBlocks.ESSENCE_EXTRACTOR_ITEM);

		Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MODID, "harvester"), LazyBlockEntities.HARVESTER);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MODID, "essence_extractor"), LazyBlockEntities.ESSENCE_EXTRACTOR);

		Registry.register(Registries.SCREEN_HANDLER, new Identifier(MODID, "harvester"), LazyScreenHandlers.HARVESTER);
		Registry.register(Registries.SCREEN_HANDLER, new Identifier(MODID, "essence_extractor"), LazyScreenHandlers.ESSENCE_EXTRACTOR);

		// Accept power from any side.
		EnergyStorage.SIDED.registerForBlockEntity((be, dir) -> be.energy, LazyBlockEntities.HARVESTER);
		EnergyStorage.SIDED.registerForBlockEntity((be, dir) -> be.energy, LazyBlockEntities.ESSENCE_EXTRACTOR);

		ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(content -> {
			content.add(LazyBlocks.HARVESTER_ITEM);
			content.add(LazyBlocks.ESSENCE_EXTRACTOR_ITEM);
		});
		LOGGER.info("Registered machines.");

		LOGGER.info("Registering core items...");
		for (int i = 0; i < LazyCoreItems.ITEMS.size(); i++) {
			Item item = LazyCoreItems.ITEMS.keySet().toArray(new Item[0])[i];
			String itemId = LazyCoreItems.ITEMS.values().toArray(new String[0])[i];
			Registry.register(Registries.ITEM, new Identifier(MODID, itemId), item);
			LOGGER.info("- Registered " + itemId + ".");
		}
		ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(content -> {
			content.add(LazyCoreItems.LAZY_SEEDS);
			content.add(LazyCoreItems.LAZIER_SEEDS);
			content.add(LazyCoreItems.LAZIEST_SEEDS);
			content.add(LazyCoreItems.WITHER_SKULL_SHARD);
		});
		LOGGER.info("Registered core items.");

		LOGGER.info("Registering crop blocks and seeds...");
		for (LazyCropBlock cropBlock : LazyCropBlocks.CROP_BLOCKS) {
			Registry.register(Registries.BLOCK, new Identifier(MODID, cropBlock.cropId), cropBlock);
			Registry.register(Registries.ITEM, new Identifier(MODID, cropBlock.seedsId), cropBlock.seedsItem);
			ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(content -> {
				content.add(cropBlock.seedsItem);
			});
			LOGGER.info("- Registered " + cropBlock.cropId + " and " + cropBlock.seedsId + ".");
		}
		LOGGER.info("Registered crop blocks and seeds.");

		LOGGER.info("Modifying loot tables...");
		Set<Identifier> chestTables = Set.of(
				LootTables.ABANDONED_MINESHAFT_CHEST,
				LootTables.DESERT_PYRAMID_CHEST,
				LootTables.JUNGLE_TEMPLE_CHEST
		);
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {
			Identifier injectId = new Identifier(MODID, "inject/" + id.getPath());
			if (chestTables.contains(id)) {
				supplier.pool(LootPool.builder()
						.with(LootTableEntry.builder(injectId))
						.build()
				);
			}
		});
		LOGGER.info("Modified loot tables.");

	}
}