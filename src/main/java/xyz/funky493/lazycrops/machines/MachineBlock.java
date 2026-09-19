package xyz.funky493.lazycrops.machines;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Shared plumbing for the machine blocks: opening the screen, dropping contents when broken,
 * and comparator support.
 */
public abstract class MachineBlock extends BlockWithEntity {

    protected MachineBlock(Settings settings) {
        super(settings);
    }

    /**
     * BlockWithEntity defaults to INVISIBLE, which would leave the machine rendering as nothing
     * in world while its item model still looked correct.
     */
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);
            if (factory != null) {
                player.openHandledScreen(factory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof Inventory inventory) {
                ItemScatterer.spawn(world, pos, inventory);
            }
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }
}
