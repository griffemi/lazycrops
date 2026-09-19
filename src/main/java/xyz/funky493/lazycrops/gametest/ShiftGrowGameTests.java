package xyz.funky493.lazycrops.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import xyz.funky493.lazycrops.LazyCrops;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlock;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlocks;

/** In-world tests for the {@code growCropsOnShift} gamerule. */
public class ShiftGrowGameTests implements FabricGameTest {

    private static final BlockPos CROP = new BlockPos(3, 1, 3);

    private static void setRule(TestContext context, boolean value) {
        ServerWorld world = context.getWorld();
        world.getGameRules().get(LazyCrops.GROW_CROPS_ON_SHIFT).set(value, world.getServer());
    }

    private static LazyCropBlock anyCrop() {
        return LazyCropBlocks.CROP_BLOCKS[0];
    }

    private static int age(TestContext context, BlockPos pos) {
        LazyCropBlock crop = anyCrop();
        return crop.getAge(context.getBlockState(pos));
    }

    /** Off by default: sneak-right-click does nothing unless the rule is explicitly enabled. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void shiftDoesNothingByDefault(TestContext context) {
        LazyCropBlock crop = anyCrop();
        context.setBlockState(CROP.down(), Blocks.FARMLAND);
        context.setBlockState(CROP, crop.withAge(0));

        PlayerEntity player = context.createMockSurvivalPlayer();
        player.setSneaking(true);
        context.useBlock(CROP, player);

        context.assertTrue(age(context, CROP) == 0,
                "Sneak-right-click should not grow the crop while growCropsOnShift is off");
        context.complete();
    }

    /** With the rule on, a sneak-right-click grows the crop exactly one stage. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void shiftGrowsOneStageWhenRuleOn(TestContext context) {
        setRule(context, true);
        LazyCropBlock crop = anyCrop();
        context.setBlockState(CROP.down(), Blocks.FARMLAND);
        context.setBlockState(CROP, crop.withAge(0));

        PlayerEntity player = context.createMockSurvivalPlayer();
        player.setSneaking(true);
        context.useBlock(CROP, player);

        context.assertTrue(age(context, CROP) == 1,
                "Sneak-right-click should grow the crop exactly one stage when growCropsOnShift is on");
        context.complete();
    }

    /** A crop already at max age is left alone rather than erroring or wrapping around. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void shiftDoesNothingAtMaxAge(TestContext context) {
        setRule(context, true);
        LazyCropBlock crop = anyCrop();
        context.setBlockState(CROP.down(), Blocks.FARMLAND);
        context.setBlockState(CROP, crop.withAge(crop.getMaxAge()));

        PlayerEntity player = context.createMockSurvivalPlayer();
        player.setSneaking(true);
        context.useBlock(CROP, player);

        context.assertTrue(age(context, CROP) == crop.getMaxAge(),
                "A fully grown crop should stay at max age on sneak-right-click");
        context.complete();
    }

    /** Right-clicking without sneaking never triggers the manual grow, even with the rule on. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void nonSneakUseDoesNothing(TestContext context) {
        setRule(context, true);
        LazyCropBlock crop = anyCrop();
        context.setBlockState(CROP.down(), Blocks.FARMLAND);
        context.setBlockState(CROP, crop.withAge(0));

        PlayerEntity player = context.createMockSurvivalPlayer();
        player.setSneaking(false);
        context.useBlock(CROP, player);

        context.assertTrue(age(context, CROP) == 0,
                "A non-sneaking right-click should not grow the crop even when growCropsOnShift is on");
        context.complete();
    }
}
