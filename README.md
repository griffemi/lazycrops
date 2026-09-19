# Adeya's Resource Crops

**Grow what you are tired of digging up. The first eight are still on you.**

## What this is

Adeya's Resource Crops is a resource-farming mod for Fabric 1.20.1. You plant a crop, wait, and
harvest a diamond, or an iron ingot, or an oak log, or a bucket of lava. There are 109 crops
covering most of vanilla's materials, a good spread of mob drops, and a set of modded metals and
gems where those mods happen to be installed.

If you have played [Mystical Agriculture](https://www.curseforge.com/minecraft/mc-mods/mystical-agriculture),
[Magical Crops](https://www.curseforge.com/minecraft/mc-mods/magical-crops),
[Croparia](https://modrinth.com/mod/croparia) or their relatives, you know how the genre usually
ends. A week after the first seed goes into the ground someone has a field of tiered essence
compounding into stacks of diamonds, a second progression tree running alongside the vanilla one,
and nothing left in the game that costs anything. Those mods are good at what they set out to do.
What they set out to do is abolish scarcity, and scarcity is a large part of what makes Minecraft
a game rather than a very slow creative mode.

This mod comes from one specific complaint: Mystical Agriculture, minus the overpowered parts, and
built to sit inside vanilla instead of next to it. The pitch is narrower as a result, and more
honest. Resource Crops automates tedium. It does not remove cost. Every seed is paid for up front,
in the resource it grows. Every harvest yields one item. There is no essence to multiply, no
infusion altar, no new ore to locate, no top tier that turns the server economy into a rounding
error. The four-hundredth diamond gets cheap. The first eight you dig out of the ground yourself.

## Who is Adeya?

Adeya is the person whose opinions the balance sheet reflects. She is not a wizard and there is no
lore. Think of her as the farmer on your server who kept a ledger: the one who counted how many
bones went into the bone meal and told you, correctly, that the wheat farm was not paying for
itself.

She came to resource farming with a farmer's assumptions instead of a mage's. Seed is the expensive
part. The first season is the worst season. You never plant what you cannot afford to lose.
Nothing, anywhere, grows from nothing. Those assumptions are why the mod looks the way it does, and
if you disagree with them you will probably be happier with a different mod.

> "I am not against automation. I am against pretending the work never happened. Dig up your own
> diamonds. Then come and talk to me about the rest of them."

Some other positions of hers, since between them they explain almost every design decision below:

- A farm should pay back what went into it, slowly. If it pays back faster than the mine did, the
  farm is broken.
- Boredom is the enemy. Difficulty is not. The mod is allowed to kill the first and never the
  second.
- A boss fight is an achievement. The walk to get there is a chore. Only one of those needs fixing.
- If a seed turns out to be useless, that is her fault rather than yours, so you can always turn it
  back into what it was.

## What you can grow

There are 109 crops in three tiers. The tier decides which seed essence a crop needs and, with it,
how expensive the crop is to start.

| Tier | Crops | Essence needed |
|---|---|---|
| Weak | 53 | Weak Seed Essence |
| Standard | 29 | Standard Seed Essence |
| Rich | 22 | Rich Seed Essence |

The vanilla side covers the things you build farms and quarries for: dirt, sand, gravel and
cobblestone; every log type; obsidian, quartz, emerald, diamond and netherite; blaze rods and ender
pearls; experience; water and lava. The mob-derived crops replace the dark rooms and animal pens you
were going to build anyway, with eggs, honeycomb, rotten flesh, slime, and drops from cows, pigs and
chickens.

Where the relevant mods are present, the crop list grows to match. Twilight Forest players get
fiery, knightmetal, steeleaf and ironwood. BetterEnd and BetterNether players get amber, aurora
crystal, smaragdant, sulphur, cincinnasite and ambrosium. The common tech metals (tin, lead,
aluminum) are covered so the mod fits into a modpack with a machine mod in it, which brings us to
the machines.

### Seed essence

Essence is the one ingredient you cannot make. It shows up only in chest loot, across 37 structure
chest tables: mineshafts, desert pyramids, jungle temples, dungeons, igloos, pillager outposts,
shipwrecks, ocean ruins, strongholds, woodland mansions, ruined portals and village houses. Each
chest roll has a 30% chance of Weak essence, a 5% chance of Standard, and a 1% chance of Rich. In
practice that means you go exploring before you go farming, and a Rich essence is a genuine find
rather than a milestone on a crafting tree.

The spread matters more than the rates do. Essence sitting in one or two structure types turns the
early game into a desert pyramid checklist, so it is scattered across the structures you were going
to open anyway. If a mod on your server replaces those structures, the pools follow: YUNG's Better
Desert Temples and Better Jungle Temples get their own chest tables covered, as does Dungeons and
Taverns' stronghold rework. That works with no dependency and no configuration, and it costs
nothing when those mods are absent.

The three tiers convert at nine to one. Nine Weak make a Standard, nine Standard make a Rich, and
both recipes reverse, so a lucky Rich drop can be broken down into 81 Weak if that is what your farm
actually needs.

### Two optional machines

Both machines run on the Tech Reborn energy API, so they plug into whatever power you already
generate and do not bring a generator of their own.

The Harvester sweeps an area for mature crops, replants them, and buffers what it collects in 24
internal slots. It draws 250 energy per harvest and will do at most 8 harvests a second. That is
fast enough to keep a serious field moving and slow enough that it is never free.

The Seed Essence Extractor is for the seeds you end up not wanting. Feed it a surplus seed and it
will render it back into essence about half the time; the other half, the seed is gone and you get
nothing. It eats the seed either way. That makes it a real sink for overflow instead of a lossless
converter, which is the point. If it were lossless, every spare seed would be free essence, and free
essence is the thing this mod is built to avoid.

## The design rules, with receipts

Two numbers carry most of the balance, and they are easy to check in the recipe book.

To craft a seed you surround one seed essence with eight of the resource you want to farm. A diamond
seed costs eight diamonds. A netherite seed costs eight netherite ingots. You cannot farm a material
you have not already gathered in quantity, so the mod acts as a multiplier on effort you have
already spent, never as a shortcut around it. A fully grown crop yields exactly one item. Fortune
raises the number of seeds returned and does nothing to the product. If you want a stack of diamonds
from your field, you plant a field and wait for 64 harvests, the same way a wheat farmer does.

The reverse recipe turns a seed back into its product, one for one. A seed you regret is never a
dead end; it just becomes the item again.

The wither skull crop is the clearest single case of the philosophy. It does not grow wither
skeleton skulls. It grows Wither Skull Shards, and eight shards plus a Weak seed essence make one
skull. Summoning a wither therefore still costs 24 harvests and three essences, which is a lot of
waiting and a lot of loot chests. What it no longer costs is an evening of pacing a Nether fortress
bridge hoping the right skeleton spawns. The tedium is gone. The boss fight is still an achievement,
and the skulls are still a thing you have to earn.

## Compatibility

The crops are real vanilla `CropBlock` subclasses. They grow on ordinary farmland, accept ordinary
bone meal, and are broken and replanted like any vanilla crop. There is no new ore, no new
dimension, no parallel magic system, and no custom progression tree; if you know how to grow carrots
you already know how to use this mod.

Because they are tagged as crops, other mods that care about crops treat them correctly. The Origins
ecosystem's Farmer class and Moth Fae's Verdant Touch both work as expected. Fabric Seasons crop
configs ship with the mod so that a winter does not halt a farm. Structure mods are handled on the
loot side, covered under seed essence above.

Four server gamerules let an admin adjust the fit:

- By default the crops ignore vanilla's light requirement, so indoor and underground farms work.
  This can be switched back to vanilla behaviour.
- Bone meal on resource crops can be disabled for servers that want growth to take real time.
- Farmland can be made immune to trampling and to drying out.
- Sneak-right-click to advance a crop a growth stage can be enabled. It is off by default.

## Credit

This mod is downstream of two projects it owes a great deal to, and the disagreement it picks with
one of them should not be mistaken for a lack of respect.

[Mystical Agriculture](https://www.curseforge.com/minecraft/mc-mods/mystical-agriculture) invented
the thing being argued with. Growing a material instead of mining it, tiering the seeds, making a
crop out of every entry in the ore dictionary: that is its idea, and it is a good enough idea that
people have been rebuilding it for a decade. Everything here is a variation on it. This mod's whole
position is a quarrel about numbers with a design it plainly admires, and if you want the version
with the infusion altar and the essence economy running at full power, go and play the original.
It does that better, because that is what it is for. The same goes for
[Magical Crops](https://www.curseforge.com/minecraft/mc-mods/magical-crops) and
[Croparia](https://modrinth.com/mod/croparia), which worked the same ground in their own directions.

[Lazy Crops](https://github.com/funkychicken493/lazycrops) by funkychicken493 is the direct
ancestor. This is a fork of it, archived and MIT licensed. The original built the crop roster and
the essence-and-donut seed economy that this mod still runs on, and it got the central call right
before anyone else did: one item per harvest, no multiplying essence. That single decision is the
reason this mod has a philosophy to write about at all, and it was funkychicken493's. What is new
in the fork is the rebrand, the harvester and the extractor, the wider spread of essence through
structure loot, the server gamerules, the crop tagging that makes other mods' farming powers work,
and a rebalance that put essence back into recipes like the wither skull.

## Versions

The mod targets Fabric 1.20.1 only. There will not be a Forge port from this project, though the MIT
license means anyone who wants to make one is welcome to. Backports to earlier versions may happen
eventually.

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
| Fabric Loom | 1.17.21 | newest line that still runs on a JDK 21 JVM (**1.18.x requires a JDK 25 JVM**) |

Output bytecode is deliberately kept at **Java 17** (`options.release = 17` in `build.gradle`).
Minecraft 1.20.1 launchers commonly ship a Java 17 runtime, and a jar compiled to class file
major 65 will not load there. Building on 21 and emitting 17 is the safe combination. Raise
`options.release` only if every client is known to be on Java 21+.

Gradle needs to be pointed at a real JDK, set in `~/.gradle/gradle.properties` (not committed):

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

- `ItemStackLegacyIdMixin` on `ItemStack.fromNbt` covers every item everywhere (inventories,
  chests, item frames, dropped items, our own machine slots).
- `BlockStateLegacyIdMixin` on `NbtHelper.toBlockState` covers placed blocks in the chunk
  palette.
- `BlockEntityLegacyIdMixin` on `BlockEntity.createFromNbt` covers the Harvester/Extractor
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
