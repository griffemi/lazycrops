package xyz.funky493.lazycrops.mixin.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryEntryLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.funky493.lazycrops.legacy.LegacyIds;

/**
 * Covers placed blocks: this is the {@code {Name, Properties}} compound format the chunk block
 * palette (and structure templates) use, both read through this one method.
 */
@Mixin(NbtHelper.class)
public class BlockStateLegacyIdMixin {

    @Inject(method = "toBlockState", at = @At("HEAD"))
    private static void resourcecrops$remapLegacyId(
            RegistryEntryLookup<Block> blockLookup, NbtCompound nbt, CallbackInfoReturnable<BlockState> cir) {
        if (!nbt.contains("Name", NbtElement.STRING_TYPE)) {
            return;
        }
        String remapped = LegacyIds.remap(nbt.getString("Name"));
        if (remapped != null) {
            nbt.putString("Name", remapped);
        }
    }
}
