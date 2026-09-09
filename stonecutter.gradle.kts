plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.1"

// Stonecutter rewrites the one shared source tree into versions/<target>/src for whichever target is
// building, so two targets must never build at once. Ordering the build task serialises them.
stonecutter tasks {
    order("build", versionComparator)
}

// What CI and a release run. Building the root project alone only covers the active target.
tasks.register("buildAll") {
    group = "project"
    description = "Builds every Stonecutter target."
    dependsOn(stonecutter.tasks.named("build"))
}

stonecutter parameters {
    // Available to source files as `//$ minecraft` swaps and in `//? if` conditions.
    swaps["minecraft"] = "\"${node.metadata.version}\";"

    // No replacements yet. Every Minecraft and Paxi class this mod touches has the same shape on both
    // targets, so the source compiles unchanged; a rename that arrives later belongs here, and anything
    // that changes arity, arguments or semantics belongs in an inline `//? if` where it is visible.
}
