package xyz.funky493.lazycrops.cropblocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.state.property.IntProperty;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import xyz.funky493.lazycrops.LazyCrops;

public class LazyCropBlock extends CropBlock {
    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)
    };

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[state.get(this.getAgeProperty())];
    }

    public String cropId;
    public Item seedsItem;
    public String seedsId;
    public int level;

    public LazyCropBlock(String cropId, String seedsId, int level) {
        super(Settings.copy(net.minecraft.block.Blocks.WHEAT).nonOpaque().noCollision().ticksRandomly().breakInstantly().sounds(net.minecraft.sound.BlockSoundGroup.CROP));
        this.cropId = cropId;
        this.seedsId = seedsId;
        this.seedsItem = new AliasedBlockItem(this, new Item.Settings());
        this.level = level;
    }

    public LazyCropBlock(String id, int level) {
        this(id + "_crop", id + "_seeds", level);
    }

    public ItemConvertible getSeedsItem() {
        return seedsItem;
    }
    public IntProperty getAgeProperty() {
        return AGE;
    }
    public int getLevel() {
        return level;
    }

    /**
     * Vanilla CropBlock#randomTick refuses to grow unless getBaseLightLevel(pos, 0) >= 9.
     * These are magical resource crops, not wheat, and a sealed indoor farm is a perfectly
     * reasonable place to put them -- so by default we run the same growth maths without the
     * light gate. getAvailableMoisture and the 1/(25/f + 1) roll are unchanged, so growth
     * speed and farmland hydration still matter exactly as much as they did.
     */
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getGameRules().getBoolean(LazyCrops.LAZY_CROPS_NEED_LIGHT)) {
            // Opt back into vanilla: light requirement, and seasonal mods that inject into
            // CropBlock#randomTick get to run.
            super.randomTick(state, world, pos, random);
            return;
        }

        int age = this.getAge(state);
        if (age >= this.getMaxAge()) {
            return;
        }
        float moisture = getAvailableMoisture(this, world, pos);
        if (random.nextInt((int) (25.0F / moisture) + 1) == 0) {
            world.setBlockState(pos, this.withAge(age + 1), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        if (!world.getGameRules().getBoolean(LazyCrops.CAN_FERTILIZE_LAZYCROPS)) {
            return;
        }
        super.applyGrowth(world, pos, state);
    }

    /**
     * Sneak-right-click grows the crop one stage, gated by the {@code growCropsOnShift}
     * gamerule (off by default). Independent of {@link LazyCrops#CAN_FERTILIZE_LAZYCROPS}: this
     * is a manual, no-item lever rather than a form of fertilizing.
     */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.isSneaking() || !world.getGameRules().getBoolean(LazyCrops.GROW_CROPS_ON_SHIFT)) {
            return ActionResult.PASS;
        }
        int age = this.getAge(state);
        if (age >= this.getMaxAge()) {
            return ActionResult.PASS;
        }
        if (!world.isClient) {
            world.setBlockState(pos, this.withAge(age + 1), Block.NOTIFY_LISTENERS);
        }
        return ActionResult.SUCCESS;
    }
}
