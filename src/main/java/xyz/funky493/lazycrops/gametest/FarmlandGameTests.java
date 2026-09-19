package xyz.funky493.lazycrops.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import xyz.funky493.lazycrops.LazyCrops;

/**
 * In-world tests for the {@code invincibleFarmland} mixin.
 * <p>
 * The gamerule is global to the server, and tests inside one batch can run concurrently, so each
 * test here declares its own batch to force them to run one at a time.
 */
public class FarmlandGameTests implements FabricGameTest {

    private static final BlockPos FARMLAND = new BlockPos(3, 1, 3);

    private static void setRule(TestContext context, boolean value) {
        ServerWorld world = context.getWorld();
        world.getGameRules().get(LazyCrops.INVINCIBLE_FARMLAND).set(value, world.getServer());
    }

    /** Dry farmland with nothing planted on it, which vanilla reverts to dirt on a random tick. */
    private static void placeDryFarmland(TestContext context) {
        context.setBlockState(FARMLAND, Blocks.FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, 0));
        context.setBlockState(FARMLAND.up(), Blocks.AIR);
    }

    private static void randomTick(TestContext context, int times) {
        ServerWorld world = context.getWorld();
        BlockPos abs = context.getAbsolutePos(FARMLAND);
        for (int i = 0; i < times; i++) {
            world.getBlockState(abs).randomTick(world, abs, world.getRandom());
        }
    }

    /**
     * Simulates something heavy landing on the farmland.
     * <p>
     * A cow clears vanilla's size threshold, and a large fall distance makes the random check
     * certain. The entity is never spawned -- onLandedUpon only reads its dimensions.
     */
    private static void trample(TestContext context) {
        ServerWorld world = context.getWorld();
        BlockPos abs = context.getAbsolutePos(FARMLAND);
        CowEntity cow = EntityType.COW.create(world);
        Blocks.FARMLAND.onLandedUpon(world, world.getBlockState(abs), abs, cow, 10.0F);
    }

    // ---------------------------------------------------------------- control cases (rule off)

    /** Control: without the rule, vanilla still dries farmland out. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200, batchId = "farmlandRuleOffDry")
    public void farmlandStillDriesOutWhenRuleOff(TestContext context) {
        setRule(context, false);
        placeDryFarmland(context);

        randomTick(context, 40);

        context.assertTrue(context.getBlockState(FARMLAND).isOf(Blocks.DIRT),
                "With the rule off, dry unplanted farmland should revert to dirt");
        context.complete();
    }

    /** Control: without the rule, vanilla still tramples farmland. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200, batchId = "farmlandRuleOffTrample")
    public void farmlandStillTramplesWhenRuleOff(TestContext context) {
        setRule(context, false);
        context.setBlockState(FARMLAND, Blocks.FARMLAND);

        trample(context);

        context.assertTrue(context.getBlockState(FARMLAND).isOf(Blocks.DIRT),
                "With the rule off, landing on farmland should trample it to dirt");
        context.complete();
    }

    // ---------------------------------------------------------------- rule on

    /** With the rule on, farmland neither dries out nor reverts, and is pinned to max moisture. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200, batchId = "farmlandRuleOnDry")
    public void farmlandSurvivesDryingWhenRuleOn(TestContext context) {
        setRule(context, true);
        placeDryFarmland(context);

        randomTick(context, 40);

        BlockState state = context.getBlockState(FARMLAND);
        context.assertTrue(state.isOf(Blocks.FARMLAND),
                "With the rule on, farmland must not revert to dirt");
        context.assertTrue(state.get(FarmlandBlock.MOISTURE) == FarmlandBlock.MAX_MOISTURE,
                "With the rule on, farmland should be held at full moisture");

        setRule(context, false);
        context.complete();
    }

    /** With the rule on, nothing landing on farmland can trample it. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200, batchId = "farmlandRuleOnTrample")
    public void farmlandResistsTramplingWhenRuleOn(TestContext context) {
        setRule(context, true);
        context.setBlockState(FARMLAND, Blocks.FARMLAND);

        for (int i = 0; i < 20; i++) {
            trample(context);
        }

        context.assertTrue(context.getBlockState(FARMLAND).isOf(Blocks.FARMLAND),
                "With the rule on, farmland must survive being landed on");

        setRule(context, false);
        context.complete();
    }
}
