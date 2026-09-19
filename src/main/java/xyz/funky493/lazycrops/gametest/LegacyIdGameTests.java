package xyz.funky493.lazycrops.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import xyz.funky493.lazycrops.blocks.LazyBlocks;
import xyz.funky493.lazycrops.cropblocks.SeedEssenceItems;
import xyz.funky493.lazycrops.machines.HarvesterBlockEntity;

/**
 * Exercises the three legacy-id Mixins end to end, the way real save data would trigger them --
 * see {@link xyz.funky493.lazycrops.legacy.LegacyIds} for why they exist.
 */
public class LegacyIdGameTests implements FabricGameTest {

    private static final BlockPos POS = new BlockPos(3, 2, 3);

    /** Every item stack in existence deserializes through this one path. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void itemStackMigratesFromLegacyId(TestContext context) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", "lazycrops:lazy_seeds");
        nbt.putInt("Count", 1);

        ItemStack stack = ItemStack.fromNbt(nbt);

        context.assertTrue(stack.getItem() == SeedEssenceItems.WEAK_SEED_ESSENCE,
                "Legacy lazycrops:lazy_seeds should deserialize as the current weak seed essence item");
        context.complete();
    }

    /** The chunk block palette (and structure templates) use this {Name, Properties} format. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void blockStateMigratesFromLegacyId(TestContext context) {
        ServerWorld world = context.getWorld();
        NbtCompound nbt = new NbtCompound();
        nbt.putString("Name", "lazycrops:harvester");

        BlockState state = NbtHelper.toBlockState(world.getRegistryManager().get(RegistryKeys.BLOCK).getReadOnlyWrapper(), nbt);

        context.assertTrue(state.getBlock() == LazyBlocks.HARVESTER,
                "Legacy lazycrops:harvester block state should resolve to the current harvester block");
        context.complete();
    }

    /** Harvester/Extractor block entities deserialize through this one static factory. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 200)
    public void blockEntityMigratesFromLegacyId(TestContext context) {
        context.setBlockState(POS, LazyBlocks.HARVESTER.getDefaultState());
        BlockPos absPos = context.getAbsolutePos(POS);
        BlockState state = context.getWorld().getBlockState(absPos);

        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", "lazycrops:harvester");

        BlockEntity created = BlockEntity.createFromNbt(absPos, state, nbt);

        context.assertTrue(created instanceof HarvesterBlockEntity,
                "Legacy lazycrops:harvester block entity id should resolve to the current HarvesterBlockEntity");
        context.complete();
    }
}
