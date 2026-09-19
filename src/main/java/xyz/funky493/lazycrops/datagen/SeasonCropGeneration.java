package xyz.funky493.lazycrops.datagen;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import xyz.funky493.lazycrops.LazyCrops;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlock;
import xyz.funky493.lazycrops.cropblocks.LazyCropBlocks;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Emits per-crop growth multipliers for Fabric Seasons, at
 * {@code data/resourcecrops/seasons/crop/<block path>.json}.
 * <p>
 * Fabric Seasons looks up a config keyed by the crop's block id and falls back to its own
 * {@code seasons/hardcoded/crop/default.json} when there isn't one. That default is
 * {@code spring 1.0 / summer 0.7 / fall 0.3 / winter 0.0} -- a hard zero, not "slow" -- so
 * without these files every lazy crop silently stops dead for an entire winter.
 * <p>
 * These are resource crops rather than plants, so they are season-neutral. Change
 * {@link #MULTIPLIERS} if you want them to care about the calendar; anything above zero for
 * winter is enough to avoid the dead stop.
 * <p>
 * Inert when Fabric Seasons isn't installed -- nothing else reads this path -- so this adds
 * no dependency.
 */
public class SeasonCropGeneration implements DataProvider {

    /** spring, summer, fall, winter. */
    private static final float[] MULTIPLIERS = {1.0F, 1.0F, 1.0F, 1.0F};
    private static final String[] SEASONS = {"spring", "summer", "fall", "winter"};

    private final FabricDataOutput output;

    public SeasonCropGeneration(FabricDataOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        DataOutput.PathResolver resolver =
                this.output.getResolver(DataOutput.OutputType.DATA_PACK, "seasons/crop");

        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (LazyCropBlock cropBlock : LazyCropBlocks.CROP_BLOCKS) {
            JsonObject json = new JsonObject();
            for (int i = 0; i < SEASONS.length; i++) {
                json.addProperty(SEASONS[i], MULTIPLIERS[i]);
            }
            Path path = resolver.resolveJson(new Identifier(LazyCrops.MODID, cropBlock.cropId));
            futures.add(DataProvider.writeToPath(writer, json, path));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Fabric Seasons crop configs";
    }
}
