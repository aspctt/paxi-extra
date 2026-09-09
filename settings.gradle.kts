pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}


stonecutter {
    create(rootProject) {
        // One target per loader per Paxi release line, because this mod compiles against Paxi and Paxi
        // exists only for these. The 26.1 targets cover 26.1.1 and 26.1.2, which share a jar: Paxi's own
        // API and every Minecraft class touched here are identical across them.
        //
        // The node name carries the loader and the value is the Minecraft version it builds against, so
        // one source tree serves all four and the loader is readable from the target you are on.
        version("1.21.1-neoforge", "1.21.1")
        version("1.21.1-fabric", "1.21.1")
        version("26.1-neoforge", "26.1.2")
        version("26.1-fabric", "26.1.2")
        vcsVersion = "1.21.1-neoforge"

        // Loom and NeoForge ModDev cannot both own the same source set, and applying either one
        // imperatively costs the typed accessors its configuration block relies on. Giving each loader its
        // own build script keeps both blocks ordinary, and the shared half lives in common.gradle.kts.
        mapBuilds { _, node ->
            when {
                !node.project.endsWith("-fabric") -> "build.neoforge.gradle.kts"
                // Minecraft ships deobfuscated from 26.1, so Fabric has no intermediary or Yarn for it and
                // Loom keeps a separate plugin for the versions that still need remapping.
                node.version.startsWith("26.") -> "build.fabric.gradle.kts"
                else -> "build.fabric-remap.gradle.kts"
            }
        }
    }
}

rootProject.name = "paxiextra"
