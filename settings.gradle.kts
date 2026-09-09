pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
    create(rootProject) {
        // One target per Paxi release line, because this mod compiles against Paxi and Paxi exists only
        // for these. The 26.1 target covers 26.1.1 and 26.1.2, which share a jar: Paxi's own API and every
        // Minecraft class touched here are identical across them.
        versions("1.21.1", "26.1")
        vcsVersion = "1.21.1"
    }
}

rootProject.name = "paxiextra"
