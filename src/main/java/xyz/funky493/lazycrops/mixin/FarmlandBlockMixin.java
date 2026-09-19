package xyz.funky493.lazycrops.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.funky493.lazycrops.LazyCrops;

/**
 * Implements the {@code invincibleFarmland} gamerule for all vanilla farmland.
 * <p>
 * Both hooks are no-ops while the gamerule is off, so vanilla behaviour is untouched by default.
 */
@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {

    /**
     * Suppresses trampling.
     * <p>
     * This redirects the {@code setToDirt} call rather than cancelling {@code onLandedUpon}
     * outright, because that method also calls {@code super.onLandedUpon}, which is what applies
     * fall damage. Cancelling at HEAD would make farmland silently negate all fall damage.
     */
    @Redirect(
            method = "onLandedUpon",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/FarmlandBlock;setToDirt(Lnet/minecraft/entity/Entity;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
    private void lazycrops$resistTrampling(Entity entity, BlockState state, World world, BlockPos pos) {
        if (!world.getGameRules().getBoolean(LazyCrops.INVINCIBLE_FARMLAND)) {
            FarmlandBlock.setToDirt(entity, state, world, pos);
        }
    }

    /**
     * Suppresses drying out and reverting to dirt, and keeps the block fully hydrated so crops
     * still grow at the moist rate with no water source.
     * <p>
     * Safe to cancel outright: FarmlandBlock#randomTick does not call super, so moisture handling
     * is the entirety of its behaviour.
     */
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void lazycrops$stayHydrated(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!world.getGameRules().getBoolean(LazyCrops.INVINCIBLE_FARMLAND)) {
            return;
        }
        if (state.get(FarmlandBlock.MOISTURE) < FarmlandBlock.MAX_MOISTURE) {
            world.setBlockState(pos, state.with(FarmlandBlock.MOISTURE, FarmlandBlock.MAX_MOISTURE), Block.NOTIFY_LISTENERS);
        }
        ci.cancel();
    }
}
