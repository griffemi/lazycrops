package xyz.funky493.lazycrops.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import xyz.funky493.lazycrops.blocks.LazyBlocks;
import xyz.funky493.lazycrops.cropblocks.LazyCoreItems;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlock;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlocks;
import xyz.funky493.lazycrops.machines.EssenceExtractorBlockEntity;
import xyz.funky493.lazycrops.machines.HarvesterBlockEntity;

/**
 * In-world tests for the two machines.
 * <p>
 * The machine tick methods are driven directly in a loop rather than by waiting for real game
 * ticks. That keeps every assertion deterministic (no random-tick growth racing the assert) and
 * lets the statistical extraction test run thousands of iterations instantly.
 */
public class MachineGameTests implements FabricGameTest {

    private static final BlockPos MACHINE = new BlockPos(3, 2, 3);

    private static LazyCropBlock crop(String cropId) {
        for (LazyCropBlock candidate : LazyCropBlocks.CROP_BLOCKS) {
            if (candidate.cropId.equals(cropId)) {
                return candidate;
            }
        }
        throw new IllegalStateException("No crop registered with id " + cropId);
    }

    /** Places farmland with a crop on top, at the given age. */
    private static void plant(TestContext context, BlockPos pos, LazyCropBlock block, int age) {
        context.setBlockState(pos.down(), Blocks.FARMLAND);
        context.setBlockState(pos, block.withAge(age));
    }

    private static HarvesterBlockEntity placeHarvester(TestContext context, int radius, long energy) {
        context.setBlockState(MACHINE, LazyBlocks.HARVESTER);
        HarvesterBlockEntity be = (HarvesterBlockEntity) context.getBlockEntity(MACHINE);
        be.setRadius(radius);
        be.energy.amount = energy;
        return be;
    }

    private static EssenceExtractorBlockEntity placeExtractor(TestContext context, long energy) {
        context.setBlockState(MACHINE, LazyBlocks.ESSENCE_EXTRACTOR);
        EssenceExtractorBlockEntity be = (EssenceExtractorBlockEntity) context.getBlockEntity(MACHINE);
        be.energy.amount = energy;
        return be;
    }

    private static void runHarvester(TestContext context, HarvesterBlockEntity be, int ticks) {
        BlockState state = context.getWorld().getBlockState(be.getPos());
        for (int i = 0; i < ticks; i++) {
            HarvesterBlockEntity.tick(context.getWorld(), be.getPos(), state, be);
        }
    }

    private static int count(HarvesterBlockEntity be, Item item) {
        int total = 0;
        for (int i = 0; i < be.size(); i++) {
            ItemStack stack = be.getStack(i);
            if (stack.getItem() == item) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static int count(EssenceExtractorBlockEntity be, Item item, int from, int to) {
        int total = 0;
        for (int i = from; i < to; i++) {
            ItemStack stack = be.getStack(i);
            if (stack.getItem() == item) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static int age(TestContext context, BlockPos pos) {
        BlockState state = context.getBlockState(pos);
        LazyCropBlock block = (LazyCropBlock) state.getBlock();
        return state.get(block.getAgeProperty());
    }

    // ------------------------------------------------------------------ harvester

    /** The happy path: a mature crop is harvested, replanted at age 0, and its product buffered. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void harvesterReplantsAndBuffersProduct(TestContext context) {
        LazyCropBlock dirt = crop("dirt_crop");
        BlockPos cropPos = new BlockPos(2, 2, 3);
        plant(context, cropPos, dirt, 7);

        HarvesterBlockEntity be = placeHarvester(context, 1, HarvesterBlockEntity.CAPACITY);
        runHarvester(context, be, 25);

        context.assertTrue(context.getBlockState(cropPos).getBlock() == dirt,
                "Crop should still be a crop after harvesting, not air");
        context.assertTrue(age(context, cropPos) == 0,
                "Crop should have been replanted at age 0, was age " + age(context, cropPos));
        context.assertTrue(count(be, Items.DIRT) > 0,
                "Harvested product should be in the buffer");
        context.complete();
    }

    /**
     * The replant seed must come out of the drops, never be conjured. The loot table yields
     * 1..4 seeds, so the buffer must hold strictly fewer seeds than were dropped -- and since a
     * harvest always costs exactly one, at most 3 can remain.
     */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void harvesterTakesReplantSeedFromDrops(TestContext context) {
        LazyCropBlock dirt = crop("dirt_crop");
        BlockPos cropPos = new BlockPos(2, 2, 3);
        plant(context, cropPos, dirt, 7);

        HarvesterBlockEntity be = placeHarvester(context, 1, HarvesterBlockEntity.CAPACITY);
        runHarvester(context, be, 25);

        int seeds = count(be, dirt.seedsItem);
        context.assertTrue(seeds <= 3,
                "One seed must have been spent replanting; buffer held " + seeds + " (max possible drop is 4)");
        context.complete();
    }

    /**
     * The no-void guarantee. With no room in the buffer the crop must be left standing, rather
     * than broken with its drops discarded.
     */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void harvesterStallsInsteadOfVoidingWhenFull(TestContext context) {
        LazyCropBlock dirt = crop("dirt_crop");
        BlockPos cropPos = new BlockPos(2, 2, 3);
        plant(context, cropPos, dirt, 7);

        HarvesterBlockEntity be = placeHarvester(context, 1, HarvesterBlockEntity.CAPACITY);
        // Cobblestone stacks with neither the product nor the seeds, so there is genuinely no room.
        for (int i = 0; i < be.size(); i++) {
            be.getItems().set(i, new ItemStack(Items.COBBLESTONE, 64));
        }

        runHarvester(context, be, 40);

        context.assertTrue(age(context, cropPos) == 7,
                "A full harvester must leave the mature crop standing");
        context.assertTrue(count(be, Items.COBBLESTONE) == be.size() * 64,
                "A full harvester must not displace what it is already holding");
        context.assertTrue(count(be, Items.DIRT) == 0,
                "Nothing should have been harvested into a full buffer");
        context.complete();
    }

    /** Crops outside the selected radius must be left alone. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void harvesterRespectsRadius(TestContext context) {
        LazyCropBlock dirt = crop("dirt_crop");
        BlockPos inside = new BlockPos(2, 2, 3);
        BlockPos outside = new BlockPos(0, 2, 3);
        plant(context, inside, dirt, 7);
        plant(context, outside, dirt, 7);

        // Radius 1 reaches x 2..4; the crop at x=0 is three blocks away.
        HarvesterBlockEntity be = placeHarvester(context, 1, HarvesterBlockEntity.CAPACITY);
        runHarvester(context, be, 25);

        context.assertTrue(age(context, inside) == 0, "Crop inside the radius should be harvested");
        context.assertTrue(age(context, outside) == 7, "Crop outside the radius must be untouched");
        context.complete();
    }

    /** The sweep covers the machine's own level plus one above and one below, and no further. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void harvesterCoversThreeLevels(TestContext context) {
        LazyCropBlock dirt = crop("dirt_crop");
        BlockPos below = new BlockPos(2, 1, 3);
        BlockPos level = new BlockPos(4, 2, 3);
        BlockPos above = new BlockPos(2, 3, 3);
        BlockPos tooHigh = new BlockPos(4, 4, 3);
        plant(context, below, dirt, 7);
        plant(context, level, dirt, 7);
        plant(context, above, dirt, 7);
        plant(context, tooHigh, dirt, 7);

        HarvesterBlockEntity be = placeHarvester(context, 1, HarvesterBlockEntity.CAPACITY);
        runHarvester(context, be, 25);

        context.assertTrue(age(context, below) == 0, "Crop one below should be harvested");
        context.assertTrue(age(context, level) == 0, "Crop on the same level should be harvested");
        context.assertTrue(age(context, above) == 0, "Crop one above should be harvested");
        context.assertTrue(age(context, tooHigh) == 7, "Crop two above must be out of range");
        context.complete();
    }

    /** Immature crops must not be touched. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void harvesterIgnoresImmatureCrops(TestContext context) {
        LazyCropBlock dirt = crop("dirt_crop");
        BlockPos cropPos = new BlockPos(2, 2, 3);
        plant(context, cropPos, dirt, 4);

        HarvesterBlockEntity be = placeHarvester(context, 1, HarvesterBlockEntity.CAPACITY);
        runHarvester(context, be, 40);

        context.assertTrue(age(context, cropPos) == 4, "An immature crop must be left to grow");
        context.assertTrue(count(be, Items.DIRT) == 0, "Nothing should have been harvested");
        context.complete();
    }

    /** With no power nothing happens, and powering it costs energy. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void harvesterRequiresEnergy(TestContext context) {
        LazyCropBlock dirt = crop("dirt_crop");
        BlockPos cropPos = new BlockPos(2, 2, 3);
        plant(context, cropPos, dirt, 7);

        HarvesterBlockEntity be = placeHarvester(context, 1, 0L);
        runHarvester(context, be, 40);
        context.assertTrue(age(context, cropPos) == 7, "An unpowered harvester must not harvest");

        be.energy.amount = HarvesterBlockEntity.CAPACITY;
        runHarvester(context, be, 25);
        context.assertTrue(age(context, cropPos) == 0, "A powered harvester should harvest");
        context.assertTrue(be.energy.amount < HarvesterBlockEntity.CAPACITY,
                "Harvesting should consume energy");
        context.complete();
    }

    /** Automation may pull from the buffer but never push into it. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
    public void harvesterBufferIsOutputOnly(TestContext context) {
        HarvesterBlockEntity be = placeHarvester(context, 1, 0L);
        context.assertFalse(be.canInsert(0, new ItemStack(Items.DIRT), null),
                "The harvester buffer must refuse inserts");
        context.assertTrue(be.canExtract(0, new ItemStack(Items.DIRT), net.minecraft.util.math.Direction.DOWN),
                "The harvester buffer must allow extraction");
        context.complete();
    }

    // ------------------------------------------------------------------ extractor

    /** Essence tier must follow the tier of the seed consumed. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void extractorProducesMatchingTier(TestContext context) {
        LazyCropBlock diamond = crop("diamond_crop");
        context.assertTrue(diamond.getLevel() == 2, "Diamond should be a tier 2 crop");

        EssenceExtractorBlockEntity be = placeExtractor(context, EssenceExtractorBlockEntity.CAPACITY);
        be.getItems().set(0, new ItemStack(diamond.seedsItem, 64));

        BlockState state = context.getWorld().getBlockState(be.getPos());
        // 64 seeds * 40 ticks each, refilling power so this test isolates tier selection.
        for (int i = 0; i < 64 * EssenceExtractorBlockEntity.MAX_PROGRESS; i++) {
            be.energy.amount = EssenceExtractorBlockEntity.CAPACITY;
            EssenceExtractorBlockEntity.tick(context.getWorld(), be.getPos(), state, be);
        }

        int rich = count(be, LazyCoreItems.LAZIEST_SEEDS, EssenceExtractorBlockEntity.INPUT_SLOTS,
                EssenceExtractorBlockEntity.SLOT_COUNT);
        int weak = count(be, LazyCoreItems.LAZY_SEEDS, EssenceExtractorBlockEntity.INPUT_SLOTS,
                EssenceExtractorBlockEntity.SLOT_COUNT);
        int standard = count(be, LazyCoreItems.LAZIER_SEEDS, EssenceExtractorBlockEntity.INPUT_SLOTS,
                EssenceExtractorBlockEntity.SLOT_COUNT);

        context.assertTrue(rich > 0, "A tier 2 seed should yield Rich Seed Essence");
        context.assertTrue(weak == 0 && standard == 0,
                "A tier 2 seed must not yield lower tiers (weak=" + weak + ", standard=" + standard + ")");
        context.complete();
    }

    /**
     * Every seed is consumed, but only about half yield essence. Over 400 attempts the chance of
     * landing outside 150..250 is vanishingly small, so this is a stable bound rather than a flaky one.
     */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void extractorConsumesEverySeedButSucceedsAboutHalf(TestContext context) {
        LazyCropBlock dirt = crop("dirt_crop");
        context.assertTrue(dirt.getLevel() == 0, "Dirt should be a tier 0 crop");

        EssenceExtractorBlockEntity be = placeExtractor(context, EssenceExtractorBlockEntity.CAPACITY);
        int attempts = 400;
        for (int slot = 0; slot < attempts / 64 + 1; slot++) {
            be.getItems().set(slot, new ItemStack(dirt.seedsItem, 64));
        }
        int startingSeeds = count(be, dirt.seedsItem, 0, EssenceExtractorBlockEntity.INPUT_SLOTS);

        BlockState state = context.getWorld().getBlockState(be.getPos());
        for (int i = 0; i < attempts * EssenceExtractorBlockEntity.MAX_PROGRESS; i++) {
            be.energy.amount = EssenceExtractorBlockEntity.CAPACITY;
            EssenceExtractorBlockEntity.tick(context.getWorld(), be.getPos(), state, be);
        }

        int remaining = count(be, dirt.seedsItem, 0, EssenceExtractorBlockEntity.INPUT_SLOTS);
        int consumed = startingSeeds - remaining;
        int produced = count(be, LazyCoreItems.LAZY_SEEDS, EssenceExtractorBlockEntity.INPUT_SLOTS,
                EssenceExtractorBlockEntity.SLOT_COUNT);

        context.assertTrue(consumed == attempts,
                "Every attempt must consume exactly one seed; consumed " + consumed + " of " + attempts);
        context.assertTrue(produced < consumed,
                "Extraction must sometimes fail; produced " + produced + " from " + consumed + " seeds");
        context.assertTrue(produced > attempts / 4 && produced < attempts * 3 / 4,
                "Success rate should be near 50%, got " + produced + "/" + attempts);
        context.complete();
    }

    /** A full output must stop the machine rather than eat seeds for nothing. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void extractorStallsWhenOutputIsFull(TestContext context) {
        LazyCropBlock dirt = crop("dirt_crop");
        EssenceExtractorBlockEntity be = placeExtractor(context, EssenceExtractorBlockEntity.CAPACITY);
        be.getItems().set(0, new ItemStack(dirt.seedsItem, 64));
        for (int i = EssenceExtractorBlockEntity.INPUT_SLOTS; i < EssenceExtractorBlockEntity.SLOT_COUNT; i++) {
            be.getItems().set(i, new ItemStack(LazyCoreItems.LAZY_SEEDS, 64));
        }

        BlockState state = context.getWorld().getBlockState(be.getPos());
        for (int i = 0; i < 400; i++) {
            EssenceExtractorBlockEntity.tick(context.getWorld(), be.getPos(), state, be);
        }

        context.assertTrue(count(be, dirt.seedsItem, 0, EssenceExtractorBlockEntity.INPUT_SLOTS) == 64,
                "No seed may be consumed while the output is full");
        context.complete();
    }

    /** Without power the extractor does nothing, and loses no seeds doing it. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void extractorRequiresEnergy(TestContext context) {
        LazyCropBlock dirt = crop("dirt_crop");
        EssenceExtractorBlockEntity be = placeExtractor(context, 0L);
        be.getItems().set(0, new ItemStack(dirt.seedsItem, 64));

        BlockState state = context.getWorld().getBlockState(be.getPos());
        for (int i = 0; i < 400; i++) {
            EssenceExtractorBlockEntity.tick(context.getWorld(), be.getPos(), state, be);
        }

        context.assertTrue(count(be, dirt.seedsItem, 0, EssenceExtractorBlockEntity.INPUT_SLOTS) == 64,
                "An unpowered extractor must not consume seeds");
        context.complete();
    }

    /** Only seeds go in; essence itself must not be feedable back in. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
    public void extractorAcceptsOnlySeeds(TestContext context) {
        LazyCropBlock dirt = crop("dirt_crop");
        EssenceExtractorBlockEntity be = placeExtractor(context, 0L);

        context.assertTrue(be.canInsert(0, new ItemStack(dirt.seedsItem), null),
                "Seeds should be accepted in the input");
        context.assertFalse(be.canInsert(0, new ItemStack(Items.COBBLESTONE), null),
                "Non-seeds must be rejected");
        context.assertFalse(be.canInsert(0, new ItemStack(LazyCoreItems.LAZY_SEEDS), null),
                "Essence must not be feedable back into the extractor");
        context.assertFalse(be.canInsert(EssenceExtractorBlockEntity.INPUT_SLOTS, new ItemStack(dirt.seedsItem), null),
                "Output slots must refuse inserts");
        context.complete();
    }
}
