package com.aspctt.paxiextra.mixin;

import com.aspctt.paxiextra.PaxiExtra;
import com.aspctt.paxiextra.interfaces.PackTricks;
import com.aspctt.paxiextra.mixin.accessor.FolderRepositorySourceAccessor;
import com.aspctt.paxiextra.util.PaxiExtraDiscovery;
import com.aspctt.paxiextra.util.PaxiExtraOrdering;
import com.aspctt.paxiextra.util.PaxiExtraPacks;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.yungnickyoung.minecraft.paxi.PaxiCommon;
import com.yungnickyoung.minecraft.paxi.PaxiPackSource;
import com.yungnickyoung.minecraft.paxi.PaxiRepositorySource;
import com.yungnickyoung.minecraft.yungsapi.io.JSON;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Replaces how Paxi turns a load order file into packs.
 *
 * <p>Three things change. A pack sitting in the Paxi folder that the load order does not name is no longer
 * loaded, so the file is the whole story rather than a hint. An entry that matches no file is looked up
 * among the packs mods have already contributed, which is what makes a mod's built-in pack orderable. And
 * the {@code --user--} entry marks where the player's own packs sit: everything listed before it is placed
 * underneath them, everything after stays on top.
 *
 * <p>The load order file is read here rather than through Paxi's own private method because the entries
 * that are not files have to survive the walk, and Paxi drops them with an error.
 */
@Mixin(value = PaxiRepositorySource.class, remap = false)
public abstract class PaxiRepositorySourceMixin {
    /**
     * The load order entry standing for the player's own packs. It is a marker rather than a pack: it takes
     * no slot of its own and is only read for where it sits among the other entries.
     */
    @Unique
    private static final String USER_PACKS_MARKER = "--user--";

    @Shadow
    @Final
    private static FileFilter PACK_FILTER;

    @Shadow
    @Final
    private File orderingFile;

    @Shadow
    public List<String> orderedPaxiPacks;

    @Shadow
    public List<String> unorderedPaxiPacks;

    @Shadow
    private Pack.ResourcesSupplier createPackResourcesSupplier(Path path) {
        throw new AssertionError();
    }

    @Inject(method = "loadPacks", at = @At("HEAD"), cancellable = true)
    private void paxiExtra$loadOnlyWhatTheLoadOrderNames(Consumer<Pack> packAdder, CallbackInfo ci) {
        ci.cancel();

        FolderRepositorySourceAccessor self = (FolderRepositorySourceAccessor) this;
        File folder = self.paxiExtra$folder().toFile();
        if (!folder.isDirectory() && !folder.mkdirs()) {
            PaxiExtra.LOGGER.warn("Couldn't create the pack folder at {}", folder);
        }

        this.orderedPaxiPacks.clear();
        this.unorderedPaxiPacks.clear();

        if (this.orderingFile == null) {
            return;
        }
        if (!this.orderingFile.isFile()) {
            paxiExtra$createEmptyOrderingFile();
        }

        String[] entries = paxiExtra$readLoadOrder();
        if (entries == null) {
            return;
        }

        // Only meaningful once the marker is known to be in the file at all. Without it every pack keeps
        // Paxi's usual placement above the player's packs.
        boolean belowUserPacks = paxiExtra$containsUserMarker(entries);

        for (String entry : entries) {
            if (USER_PACKS_MARKER.equals(entry)) {
                belowUserPacks = false;
                continue;
            }

            File packFile = paxiExtra$findPackFile(entry, folder);
            if (packFile == null) {
                // Names no file, so it may name a pack a mod provides. Those cannot be looked up yet, since
                // the source contributing it may not have run. The id keeps its place in the load order and
                // the repository fills it in once every source has been asked.
                this.orderedPaxiPacks.add(entry);
                PaxiExtraDiscovery.defer(entry, belowUserPacks);
                continue;
            }

            Pack pack = paxiExtra$packFromFile(entry, packFile, self);
            if (pack == null) {
                continue;
            }
            ((PackTricks) pack).paxiExtra$setBelowUserPacks(belowUserPacks);
            this.orderedPaxiPacks.add(pack.getId());
            packAdder.accept(pack);
        }
    }

    /**
     * Resolves an entry against the instance directory first and the Paxi pack folder second, which is what
     * lets a load order point at a pack anywhere in the instance rather than only inside Paxi's own folder.
     *
     * @return the file, or null if the entry names none, in which case it may name a pack a mod provides
     */
    @Unique
    private File paxiExtra$findPackFile(String entry, File folder) {
        File packFile = new File(PaxiExtra.BASE_GAME_DIRECTORY, entry);
        if (!packFile.exists()) {
            packFile = new File(folder, entry);
        }
        return packFile.exists() ? packFile : null;
    }

    /**
     * {@return the pack the given file holds, or null if it is not a pack this can read, which is an error
     * rather than something to look for elsewhere}
     */
    @Unique
    private Pack paxiExtra$packFromFile(String entry, File packFile, FolderRepositorySourceAccessor self) {
        if (!PACK_FILTER.accept(packFile)) {
            PaxiCommon.LOGGER.error("Attempted to load pack {} but it is not a valid pack format! It may be missing a pack.mcmeta file. Skipping...", entry);
            return null;
        }

        String packName = packFile.getName();
        PackLocationInfo location = new PackLocationInfo(
                packName, Component.literal(packName), PaxiPackSource.PACK_SOURCE_PAXI, Optional.empty());
        Pack pack = Pack.readMetaAndCreate(
                location,
                this.createPackResourcesSupplier(packFile.toPath()),
                self.paxiExtra$packType(),
                PaxiExtraPacks.PAXI_PACK_SELECTION);
        if (pack == null) {
            PaxiCommon.LOGGER.error("Unable to read the metadata of pack {}! Skipping...", entry);
        }
        return pack;
    }

    @Unique
    private static boolean paxiExtra$containsUserMarker(String[] entries) {
        for (String entry : entries) {
            if (USER_PACKS_MARKER.equals(entry)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private void paxiExtra$createEmptyOrderingFile() {
        try {
            JSON.createJsonFileFromObject(this.orderingFile.toPath(), new PaxiExtraOrdering(new String[0]));
        } catch (IOException e) {
            PaxiCommon.LOGGER.error("Unable to create default pack ordering file! This shouldn't happen.");
            PaxiCommon.LOGGER.error(e.toString());
        }
    }

    /**
     * {@return the entries of the load order file, or null if it could not be read, in which case nothing is
     * loaded}
     */
    @Unique
    private String[] paxiExtra$readLoadOrder() {
        PaxiExtraOrdering ordering;
        try {
            ordering = JSON.loadObjectFromJsonFile(this.orderingFile.toPath(), PaxiExtraOrdering.class);
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            PaxiCommon.LOGGER.error("Error loading Paxi ordering JSON file {}: {}", this.orderingFile.getName(), e.toString());
            return null;
        }

        if (ordering == null) {
            PaxiCommon.LOGGER.error("Unable to load ordering JSON file {}! Is it proper JSON formatting? Loading no packs...", this.orderingFile.getName());
            return null;
        }
        if (ordering.orderedPackNames() == null) {
            PaxiCommon.LOGGER.error("Unable to find entry with name 'loadOrder' in load ordering JSON file {}! Loading no packs...", this.orderingFile.getName());
            return null;
        }
        return ordering.orderedPackNames();
    }
}
