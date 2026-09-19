package xyz.funky493.lazycrops;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlock;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlocks;
import xyz.funky493.lazycrops.machines.LazyScreenHandlers;
import xyz.funky493.lazycrops.machines.screen.EssenceExtractorScreen;
import xyz.funky493.lazycrops.machines.screen.HarvesterScreen;

public class LazyCropsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		for (LazyCropBlock cropBlock : LazyCropBlocks.CROP_BLOCKS) {
			BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), cropBlock);
		}

		HandledScreens.register(LazyScreenHandlers.HARVESTER, HarvesterScreen::new);
		HandledScreens.register(LazyScreenHandlers.ESSENCE_EXTRACTOR, EssenceExtractorScreen::new);
	}
}
