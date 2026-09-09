package com.aspctt.paxiextra.mixin.accessor;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;

/**
 * Paxi declares an identical accessor, but reaching for another mod's mixin would tie this one to that
 * mixin still existing and still applying. This is the same two fields, declared here.
 */
@Mixin(FolderRepositorySource.class)
public interface FolderRepositorySourceAccessor {
    @Accessor("packType")
    PackType paxiExtra$packType();

    @Accessor("folder")
    Path paxiExtra$folder();
}
