// Everything both loaders' build scripts need. Applied from each of them rather than being a plugin of its
// own, so the properties it reads resolve against the target being built.

fun prop(name: String): String = project.property(name) as String

fun extraBuildMetadata(): String {
    val buildNumber = System.getenv("GITHUB_RUN_NUMBER") ?: return ""
    return ".build.$buildNumber"
}

// Everything after the "+" is semver build metadata, so it is ignored when versions are compared while
// still naming the jar and showing up in the mods list. That is what makes one build tellable from
// another on sight: PaxiExtra-1.2.0+1.21.1-neoforge.jar.
version = "${prop("mod_version")}+${prop("minecraft_version")}-${prop("mod_loader")}" + extraBuildMetadata()
group = prop("mod_group_id")

// An applied script gets no typed accessors from the applying script's plugins block, so the extensions
// are reached by type rather than by name.
configure<BasePluginExtension> {
    archivesName = prop("mod_archives_name")
}

repositories {
    mavenCentral()
    // Paxi and YUNG's API, which are published per loader and per Minecraft version.
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
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

// PackSelectionModel is the pack screen, so this one is client only and belongs in its own
// section of the config, where a dedicated server will not try to load it.
val clientMixins = listOf(
    "PackSelectionModelEntryBaseMixin",
).joinToString(",\n    ") { "\"" + it + "\"" }

// Expand the declared properties into the mod metadata templates. Each loader's script contributes the
// keys only its own metadata file uses.
val commonMetadataProperties: Map<String, String> = mapOf(
    "minecraft_version" to prop("minecraft_version"),
    "minecraft_version_range" to prop("minecraft_version_range"),
    "loader_version_range" to prop("loader_version_range"),
    "paxi_version_range" to prop("paxi_version_range"),
    "yungsapi_version_range" to prop("yungsapi_version_range"),
    "mod_id" to prop("mod_id"),
    "mod_name" to prop("mod_name"),
    "mod_license" to prop("mod_license"),
    "mod_version" to project.version.toString(),
    "mod_authors" to prop("mod_authors"),
    // fabric.mod.json wants a JSON array rather than the one comma-separated string the toml takes.
    "mod_authors_json" to prop("mod_authors").split(",").joinToString(", ") { "\"" + it.trim() + "\"" },
    "mod_description" to prop("mod_description"),
    "mixins" to mixins,
    "client_mixins" to clientMixins,
)
extra["commonMetadataProperties"] = commonMetadataProperties

tasks.named<Jar>("jar") {
    // Read up front, so the rename lambda captures a string rather than this script, which the
    // configuration cache cannot serialise.
    val licenseSuffix = "_" + prop("mod_archives_name")
    from(rootProject.file("LICENSE")) {
        rename { it + licenseSuffix }
    }
}

configure<JavaPluginExtension> {
    withSourcesJar()
}

configure<PublishingExtension> {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = prop("mod_archives_name")
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

configure<org.gradle.plugins.ide.idea.model.IdeaModel> {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
