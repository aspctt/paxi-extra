plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.137"
    id("idea")
}

// This script is the central build for every versioned subproject, so anything that reads a file has to
// reach for the root rather than the version directory it is being evaluated in.
val modArchivesName: String = property("mod_archives_name") as String
val modVersion: String = property("mod_version") as String
val minecraftVersion: String = property("minecraft_version") as String

fun prop(name: String): String = property(name) as String

fun extraBuildMetadata(): String {
    val buildNumber = System.getenv("GITHUB_RUN_NUMBER") ?: return ""
    return ".build.$buildNumber"
}

version = "$modVersion+$minecraftVersion" + extraBuildMetadata()
group = prop("mod_group_id")

base {
    archivesName = modArchivesName
}

// Compared numerically, because a plain string compare puts 1.21.10 before 1.21.5.
fun versionAtLeast(target: String): Boolean {
    fun parts(version: String) = version.split('.').map { it.toIntOrNull() ?: 0 }
    val current = parts(stonecutter.current.version)
    val other = parts(target)
    for (i in 0 until maxOf(current.size, other.size)) {
        val a = current.getOrElse(i) { 0 }
        val b = other.getOrElse(i) { 0 }
        if (a != b) return a > b
    }
    return true
}

repositories {
    mavenCentral()
    // Paxi and YUNG's API. Both publish NeoForge builds in Mojang mappings, which is what a ModDev
    // compile classpath expects, so they go on it as they are shipped with no remapping step.
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
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

// Listed here rather than in the template because a nested mixin is named with a "$", which the template
// engine would read as the start of a placeholder.
val mixins = listOf(
    "PackMixin",
    "PackMixin\$PackPositionMixin",
    "PackRepositoryMixin",
    "PaxiRepositorySourceMixin",
    "accessor.FolderRepositorySourceAccessor",
    "accessor.PackAccessor",
).joinToString(",\n    ") { "\"" + it + "\"" }

// Expand the declared properties into the mod metadata template.
val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
        "minecraft_version" to prop("minecraft_version"),
        "minecraft_version_range" to prop("minecraft_version_range"),
        "neo_version" to prop("neo_version"),
        "neo_version_range" to prop("neo_version_range"),
        "loader_version_range" to prop("loader_version_range"),
        "paxi_version_range" to prop("paxi_version_range"),
        "yungsapi_version_range" to prop("yungsapi_version_range"),
        "mod_id" to prop("mod_id"),
        "mod_name" to prop("mod_name"),
        "mod_license" to prop("mod_license"),
        "mod_version" to project.version.toString(),
        "mod_authors" to prop("mod_authors"),
        "mod_description" to prop("mod_description"),
        "java_version" to javaVersion.toString(),
        "mixins" to mixins,
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from(rootProject.file("src/main/templates"))
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}
sourceSets.main.get().resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)

tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_$modArchivesName" }
    }
}

java {
    withSourcesJar()
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = modArchivesName
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = rootProject.file("repo").toURI()
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
