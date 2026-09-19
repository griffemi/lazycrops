package xyz.funky493.lazycrops.legacy;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * {@code lazycrops} was this mod's namespace before the {@code resourcecrops} rename, and three
 * items additionally changed path when {@code LazyCoreItems} became {@link
 * xyz.funky493.lazycrops.cropblocks.SeedEssenceItems}. Every other registered id kept its path,
 * only the namespace changed.
 * <p>
 * Old worlds still have {@code lazycrops:*} ids baked into their save data -- placed blocks,
 * block entities, and every item stack anywhere -- so the three legacy Mixins in {@code
 * xyz.funky493.lazycrops.mixin.legacy} rewrite them back to their current id the moment that
 * data is read. This class lives outside that package deliberately: Mixin reserves a config's
 * declared package exclusively for {@code @Mixin} classes and refuses to load a plain class
 * from inside it. This map has to keep working forever, not just through one migration window:
 * a chunk nobody visits keeps its old ids until someone does.
 */
public final class LegacyIds {
    private static final String OLD_NAMESPACE = "lazycrops";
    private static final String OLD_PREFIX = OLD_NAMESPACE + ":";
    private static final String NEW_NAMESPACE = "resourcecrops";

    /** Only the paths that changed independently of the namespace swap. */
    private static final Map<String, String> RENAMED_PATHS = Map.of(
            "lazy_seeds", "weak_seed_essence",
            "lazier_seeds", "standard_seed_essence",
            "laziest_seeds", "rich_seed_essence"
    );

    private LegacyIds() {
    }

    /**
     * Rewrites a full {@code lazycrops:<path>} identifier string to its current {@code
     * resourcecrops:<path>} equivalent. Returns null if {@code rawId} isn't one of our legacy
     * ids (including if it's null or belongs to vanilla or some other mod), so callers can tell
     * "nothing to do" apart from "rewrote it to itself".
     */
    @Nullable
    public static String remap(@Nullable String rawId) {
        if (rawId == null || !rawId.startsWith(OLD_PREFIX)) {
            return null;
        }
        String path = rawId.substring(OLD_PREFIX.length());
        return NEW_NAMESPACE + ":" + RENAMED_PATHS.getOrDefault(path, path);
    }
}
