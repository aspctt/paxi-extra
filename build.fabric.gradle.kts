plugins {
    id("java-library")
    id("maven-publish")
    id("net.fabricmc.fabric-loom") version "1.17.20"
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

loom {
    runs {
        // One run directory shared by every target, so worlds, options and the Paxi config survive
        // switching between them.
        named("client") { runDir(rootProject.file("run").relativeTo(projectDir).path) }
        named("server") { runDir(rootProject.file("run-server").relativeTo(projectDir).path) }
    }
}

dependencies {
    // Minecraft ships deobfuscated from 26.1, so there are no mappings to declare and nothing that has
    // to be remapped on the way in or out.
    minecraft("com.mojang:minecraft:${prop("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${prop("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${prop("fabric_api_version")}")

    // Paxi is what every mixin here targets, and YUNG's API supplies the JSON helper that reads and
    // writes the load order file. Players install both themselves, so neither is bundled.
    compileOnly("maven.modrinth:paxi:${prop("paxi_version")}")
    compileOnly("maven.modrinth:yungs-api:${prop("yungsapi_version")}")
    localRuntime("maven.modrinth:paxi:${prop("paxi_version")}")
    localRuntime("maven.modrinth:yungs-api:${prop("yungsapi_version")}")

    // Dev runtime only. YUNG's API nests these rather than depending on them, and Loom does not unpack a
    // nested jar for a local runtime dependency, so a development run needs them on the classpath directly.
    // A player installing YUNG's API gets them from inside its own jar, and Cloth Config from its own page.
    localRuntime("org.reflections:reflections:0.10.2")
    localRuntime("org.javassist:javassist:3.29.2-GA")
    localRuntime("maven.modrinth:cloth-config:${prop("cloth_config_version")}")
}

// The other loader's entry point is the one file that cannot compile here. It lives in a package of
// its own so it can simply be left out, rather than being carried as a commented-out block.
sourceSets.main.get().java.exclude("**/neoforge/**")

// Expand the declared properties into the mod metadata templates. The shared keys come from
// common.gradle.kts; the ones below exist only in fabric.mod.json.
@Suppress("UNCHECKED_CAST")
val commonMetadataProperties = extra["commonMetadataProperties"] as Map<String, String>

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = commonMetadataProperties + mapOf(
        "java_version" to javaVersion.toString(),
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from(rootProject.file("src/main/templates/common"))
    from(rootProject.file("src/main/templates/fabric"))
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}
sourceSets.main.get().resources.srcDir(generateModMetadata)
