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

### Build

    ./gradlew build        # -> build/libs/lazycrops-<version>.jar
