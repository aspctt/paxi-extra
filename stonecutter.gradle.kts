plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.1-neoforge"

// Runs the targets' builds one at a time, in version order, rather than side by side. Not needed for
// correctness: each target preprocesses the shared source into its own build directory and never writes
// back to it, so this only keeps a full build's output grouped per target.
stonecutter tasks {
    order("build", versionComparator)
}

// A named entry point for CI and release runs. A plain ./gradlew build from the root builds every target
// too, since it runs build in each of them.
tasks.register("buildAll") {
    group = "project"
    description = "Builds every Stonecutter target."
    dependsOn(stonecutter.tasks.named("build"))
}

stonecutter parameters {
    // Available to source files as `//$ minecraft` swaps and in `//? if` conditions.
    swaps["minecraft"] = "\"${node.metadata.version}\";"

    // Which loader this target builds for, so the handful of places the two genuinely differ can say so
    // inline. Everything else, all six mixins included, compiles for both unchanged.
    val fabric = node.metadata.project.endsWith("-fabric")
    constants.put("fabric", fabric)
    constants.put("neoforge", !fabric)

    // No replacements yet. Every Minecraft and Paxi class this mod touches has the same shape on both
    // Minecraft versions, so a rename that arrives later belongs here, and anything that changes arity,
    // arguments or semantics belongs in an inline `//? if` where it is visible.
}
