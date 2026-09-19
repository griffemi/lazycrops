Do you hate grinding for resources? Do you hate having to work for things? Do you generally dislike fun? This mod is for you!

## Lazy Crops

**Work in progress!**

Lazy Crops is a mod for Fabric 1.20.1 which adds crops that can grow resources and entities for you.
It is *heavily* inspired by [Mystical Agriculture](https://www.curseforge.com/minecraft/mc-mods/mystical-agriculture), [Magical Crops](https://www.curseforge.com/minecraft/mc-mods/magical-crops), [Croparia](https://modrinth.com/mod/croparia), and [Mystical Crops](https://modrinth.com/mod/mystical_crops).

What differentiates this mod from the others is that it is designed to be as lazy (and simple) as possible.
There are no essences, mob chunks, machines, magic, etc., it is designed to be as vanilla friendly as possible.

The entire mod revolves around **Lazy Seeds**. These are the seeds you will use as a base to craft other seeds.
They can only be found rarely in desert pyramids and jungle temples. The more useful the crop, the more expensive the seed.
You will need to upgrade your **Lazy Seeds** to get **Lazier Seeds** and **Laziest Seeds**.

### Versions

For 1.20.1 Fabric only at the moment, a Forge port will **not** happen. If you wish to create a Forge port, this mod's license permits you to do so. Backports *may* come eventually.

---

## Fork notes (Girlies301)

Fork of [funkychicken493/lazycrops](https://github.com/funkychicken493/lazycrops) (archived, MIT).
Targets **Minecraft 1.20.1 / Fabric** only.

### Toolchain

The upstream build pinned Gradle 8.2 + Loom 1.3-SNAPSHOT + JDK 17. This fork runs:

| | version | why |
|---|---|---|
| JDK | 21 | Loom 1.17.x declares `org.gradle.jvm.version=21` |
| Gradle | 9.7.1 | Loom 1.17.x declares `plugin.api-version=9.5.0`, so Gradle 8.x cannot resolve it |
| Fabric Loom | 1.17.21 | newest line that still runs on a JDK 21 JVM — **1.18.x requires a JDK 25 JVM** |

Output bytecode is deliberately kept at **Java 17** (`options.release = 17` in `build.gradle`).
Minecraft 1.20.1 launchers commonly ship a Java 17 runtime, and a jar compiled to class file
major 65 will not load there. Building on 21 and emitting 17 is the safe combination. Raise
`options.release` only if every client is known to be on Java 21+.

Gradle needs to be pointed at a real JDK — set in `~/.gradle/gradle.properties` (not committed):

    org.gradle.java.home=/path/to/jdk-21

The upstream CurseForge/Modrinth publishing plugins were removed: they read tokens from
`local.properties` at configure time, so the build failed outright without secrets present.
`local.properties` is now untracked.

### Namespace migration (lazycrops -> resourcecrops)

The mod id changed from `lazycrops` to `resourcecrops` to match the "Adeya's Resource Crops"
rebrand, and the old `LazyCoreItems`/`lazy_seeds` naming became `SeedEssenceItems`/
`weak_seed_essence` (and friends) to match the in-game "Seed Essence" text. Both changes rename
the ids Minecraft actually saves to disk, which would normally turn every placed crop, machine,
and held seed on the live server into air or a vanished item the next time that chunk or
inventory loaded.

Three Mixins (`xyz.funky493.lazycrops.mixin.legacy`) close that gap by rewriting the old
`lazycrops:*` ids to their `resourcecrops:*` equivalents the moment save data is read, before
Minecraft resolves them against the registry:

- `ItemStackLegacyIdMixin` on `ItemStack.fromNbt` — covers every item everywhere (inventories,
  chests, item frames, dropped items, our own machine slots).
- `BlockStateLegacyIdMixin` on `NbtHelper.toBlockState` — covers placed blocks in the chunk
  palette.
- `BlockEntityLegacyIdMixin` on `BlockEntity.createFromNbt` — covers the Harvester/Extractor
  block entities.

The map lives in `xyz.funky493.lazycrops.legacy.LegacyIds`, deliberately outside the
`xyz.funky493.lazycrops.mixin` package tree -- Mixin reserves a config's declared package for
`@Mixin` classes only and refuses to load a plain class from inside it. This is permanent, not a
one-time migration script: old ids
convert silently and immediately wherever they're touched, with no server downtime and no direct
edits to region files, but a chunk nobody visits keeps its old ids (harmless) until someone does.
Advancement progress and stats keyed by the old ids are not migrated and will reset.

### Origins compatibility

Every crop block is tagged `#minecraft:crops` and `#c:crops` (`BlockTagGeneration`). Growth/
harvest powers in the Origins ecosystem -- the `Origins: Classes` Farmer class (doubled bone
meal, 2x harvest yield) and Moth Fae's Verdant Touch -- target blocks by tag rather than by
Java type, so an untagged crop is invisible to them even though `LazyCropBlock` already extends
vanilla `CropBlock` and correctly implements `Fertilizable`. No Origins/Apoli dependency is
needed for this: tagging is the actual fix, not a mod-specific integration.

`growCropsOnShift` gamerule (off by default): sneaking and right-clicking a crop grows it one
stage, independent of bone meal and the `canFertilizeLazyCrops` gate -- a manual, no-item lever
for servers that don't run one of the above.

### Build

    ./gradlew build        # -> build/libs/resourcecrops-<version>.jar
