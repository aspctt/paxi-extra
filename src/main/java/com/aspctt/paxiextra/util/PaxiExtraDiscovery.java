package com.aspctt.paxiextra.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Carries the load order entries that named no file from the Paxi repository source, which resolves them,
 * to the {@code PackRepository} that asked it for packs, which is the only place that can see every pack
 * every source contributed.
 *
 * <p>Paxi's source is one of many registered through {@code AddPackFindersEvent}, and the order those fire
 * in follows mod loading, so a pack from a mod whose listener fires later does not exist yet while Paxi is
 * being asked. Rather than reorder the repository's sources to put Paxi last, which changes the order every
 * other pack is discovered in, the lookup simply waits until discovery has finished.
 *
 * <p>Only meaningful while a repository is discovering, and only on the thread doing it. The client and a
 * dedicated server each own a repository, so this is per thread rather than a single static field.
 */
public final class PaxiExtraDiscovery {
    private static final ThreadLocal<List<DeferredPack>> DEFERRED = new ThreadLocal<>();

    private PaxiExtraDiscovery() {
    }

    /**
     * A load order entry to be resolved against the finished set of discovered packs.
     *
     * @param id             the entry as written in the load order, which is also the id it is looked up by
     * @param belowUserPacks whether it was listed before the {@code --user--} entry
     */
    public record DeferredPack(String id, boolean belowUserPacks) {
    }

    public static void begin() {
        DEFERRED.set(new ArrayList<>());
    }

    public static void end() {
        DEFERRED.remove();
    }

    /**
     * Records an entry to resolve once discovery is done. Ignored outside discovery, which is when nothing
     * is going to read it back.
     */
    public static void defer(String id, boolean belowUserPacks) {
        List<DeferredPack> deferred = DEFERRED.get();
        if (deferred != null) {
            deferred.add(new DeferredPack(id, belowUserPacks));
        }
    }

    public static List<DeferredPack> deferred() {
        List<DeferredPack> deferred = DEFERRED.get();
        return deferred == null ? List.of() : List.copyOf(deferred);
    }
}
