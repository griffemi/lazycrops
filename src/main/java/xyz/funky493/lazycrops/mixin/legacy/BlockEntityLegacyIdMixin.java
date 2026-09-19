package xyz.funky493.lazycrops.mixin.legacy;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.funky493.lazycrops.legacy.LegacyIds;

/** Covers the Harvester and Essence Extractor block entities. */
@Mixin(BlockEntity.class)
public class BlockEntityLegacyIdMixin {

    @Inject(method = "createFromNbt", at = @At("HEAD"))
    private static void resourcecrops$remapLegacyId(
            BlockPos pos, BlockState state, NbtCompound nbt, CallbackInfoReturnable<BlockEntity> cir) {
        if (!nbt.contains("id", NbtElement.STRING_TYPE)) {
            return;
        }
        String remapped = LegacyIds.remap(nbt.getString("id"));
        if (remapped != null) {
            nbt.putString("id", remapped);
        }
    }
}
