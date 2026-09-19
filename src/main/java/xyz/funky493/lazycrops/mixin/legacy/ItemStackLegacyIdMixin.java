package xyz.funky493.lazycrops.mixin.legacy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import xyz.funky493.lazycrops.legacy.LegacyIds;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Covers every item everywhere: player inventories, chests and other containers, item frames,
 * dropped items, and our own machine slots all deserialize through this one static factory.
 */
@Mixin(ItemStack.class)
public class ItemStackLegacyIdMixin {

    @Inject(method = "fromNbt", at = @At("HEAD"))
    private static void resourcecrops$remapLegacyId(NbtCompound nbt, CallbackInfoReturnable<ItemStack> cir) {
        if (!nbt.contains("id", NbtElement.STRING_TYPE)) {
            return;
        }
        String remapped = LegacyIds.remap(nbt.getString("id"));
        if (remapped != null) {
            nbt.putString("id", remapped);
        }
    }
}
