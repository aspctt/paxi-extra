package com.aspctt.paxiextra.util;

import com.aspctt.paxiextra.mixin.accessor.PackAccessor;
import com.yungnickyoung.minecraft.paxi.PaxiPackSource;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.Pack;

import java.util.List;

public final class PaxiExtraPacks {
    /**
     * Paxi packs are always on and always at the top; the load order is what decides their order among
     * themselves.
     */
    public static final PackSelectionConfig PAXI_PACK_SELECTION =
            new PackSelectionConfig(true, Pack.Position.TOP, false);

    private PaxiExtraPacks() {
    }

    /**
     * Re-creates a pack another source contributed, under Paxi's pack source and forced on, so the load
     * order can position a pack that ships inside a mod. The copy carries the original's resources and
     * metadata across, so nothing is read a second time and a pack that supplies its metadata in code is
     * copied as faithfully as one backed by a file.
     *
     * <p>It keeps the original's id, so it replaces rather than duplicates that entry and a selection saved
     * against that id still resolves, and it carries the original's children across. Those matter: NeoForge
     * gathers every mod's own pack as hidden children of a single {@code mod_resources} root, so a copy
     * that dropped them would take every mod's assets out of the game the moment that root was named in a
     * load order.
     */
    public static Pack asPaxiPack(Pack original) {
        PackAccessor accessor = (PackAccessor) original;
        Pack.Metadata metadata = accessor.paxiExtra$metadata();
        if (metadata.isHidden()) {
            // A mod's pack is discovered hidden unless the mod asks to be shown separately. Naming it in the
            // load order is that request, so the copy is visible on the pack screen.
            metadata = new Pack.Metadata(
                    metadata.description(),
                    metadata.compatibility(),
                    metadata.requestedFeatures(),
                    metadata.overlays(),
                    false);
        }

        PackLocationInfo location = new PackLocationInfo(
                original.getId(),
                original.getTitle(),
                PaxiPackSource.PACK_SOURCE_PAXI,
                original.location().knownPackInfo());
        Pack copy = new Pack(location, accessor.paxiExtra$resources(), metadata, PAXI_PACK_SELECTION);
        List<Pack> children = original.getChildren();
        return children.isEmpty() ? copy : copy.withChildren(children);
    }
}
