plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.137"
    id("idea")
}

apply(from = rootProject.file("common.gradle.kts"))

fun prop(name: String): String = property(name) as String

// Compared numerically, because a plain string compare puts 1.21.10 before 1.21.5.
fun versionAtLeast(target: String): Boolean {
    fun parts(version: String) = version.split('.').map { it.toIntOrNull() ?: 0 }
    val current = parts(prop("minecraft_version"))
    val other = parts(target)
    for (i in 0 until maxOf(current.size, other.size)) {
        val a = current.getOrElse(i) { 0 }
        val b = other.getOrElse(i) { 0 }
        if (a != b) return a > b
    }
    return true
}

// Mojang ships Java 21 to end users through 1.21.11, and Java 25 from 26.1.
val javaVersion = if (versionAtLeast("26.1")) 25 else 21
java.toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)

neoForge {
    version = prop("neo_version")

    // Parchment lags new Minecraft releases, so a target without it still builds; only the parameter names
    // and javadoc are missing.
    if (project.hasProperty("parchment_mappings_version")) {
        parchment {
            mappingsVersion = prop("parchment_mappings_version")
            minecraftVersion = prop("parchment_minecraft_version")
        }
    }

    runs {
        create("client") {
            client()
            logLevel = org.slf4j.event.Level.DEBUG
            // One run directory shared by every target, so worlds, options and the Paxi config survive
            // switching between them.
            gameDirectory = rootProject.file("run")
        }
        create("server") {
            server()
            logLevel = org.slf4j.event.Level.DEBUG
            gameDirectory = rootProject.file("run-server")
        }
    }

    mods {
        create(prop("mod_id")) {
            sourceSet(sourceSets.main.get())
        }
    }
}

// Paxi and YUNG's API are required at runtime, so a development run has to have them, but they are
// declared in the mod metadata rather than bundled. localRuntime keeps them off the published metadata.
val localRuntime = configurations.create("localRuntime")
configurations.runtimeClasspath.get().extendsFrom(localRuntime)

dependencies {
    // Paxi is what every mixin here targets, and YUNG's API supplies the JSON helper that reads and
    // writes the load order file. compileOnly because players install both themselves.
    compileOnly("maven.modrinth:paxi:${prop("paxi_version")}")
    compileOnly("maven.modrinth:yungs-api:${prop("yungsapi_version")}")
    localRuntime("maven.modrinth:paxi:${prop("paxi_version")}")
    localRuntime("maven.modrinth:yungs-api:${prop("yungsapi_version")}")
}

// Expand the declared properties into the mod metadata templates. The shared keys come from
// common.gradle.kts; the ones below exist only in neoforge.mods.toml.
@Suppress("UNCHECKED_CAST")
val commonMetadataProperties = extra["commonMetadataProperties"] as Map<String, String>

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = commonMetadataProperties + mapOf(
        "neo_version" to prop("neo_version"),
        "neo_version_range" to prop("neo_version_range"),
        "java_version" to javaVersion.toString(),
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from(rootProject.file("src/main/templates/common"))
    from(rootProject.file("src/main/templates/neoforge"))
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}
sourceSets.main.get().resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)
