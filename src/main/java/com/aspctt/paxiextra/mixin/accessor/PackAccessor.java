package com.aspctt.paxiextra.mixin.accessor;

import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Reads the two pieces of an already built pack that are needed to re-create it under Paxi's pack source.
 * Going through these rather than {@code Pack.readMetaAndCreate} means the pack's metadata is never parsed
 * a second time, so a pack that supplies its metadata in code rather than from a readable pack.mcmeta is
 * copied as faithfully as one backed by a file.
 */
@Mixin(Pack.class)
public interface PackAccessor {
    @Accessor("resources")
    Pack.ResourcesSupplier paxiExtra$resources();

    @Accessor("metadata")
    Pack.Metadata paxiExtra$metadata();
}
