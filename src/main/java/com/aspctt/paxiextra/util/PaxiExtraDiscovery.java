package com.aspctt.paxiextra.util;

import net.minecraft.server.packs.repository.Pack;

import java.util.Map;

/**
 * Hands the packs a {@code PackRepository} has discovered so far to the Paxi repository source, which is
 * asked for its packs without being told which repository is asking.
 *
 * <p>Only meaningful while a repository is discovering, and only on the thread doing it. The client and a
 * dedicated server each own a repository, so this is per thread rather than a single static field.
 */
public final class PaxiExtraDiscovery {
    private static final ThreadLocal<Map<String, Pack>> IN_PROGRESS = new ThreadLocal<>();

    private PaxiExtraDiscovery() {
    }

    public static void begin(Map<String, Pack> discovered) {
        IN_PROGRESS.set(discovered);
    }

    public static void end() {
        IN_PROGRESS.remove();
    }

    /**
     * {@return every pack discovered before the source now loading, by id, or an empty map outside
     * discovery}
     */
    public static Map<String, Pack> available() {
        Map<String, Pack> discovered = IN_PROGRESS.get();
        return discovered == null ? Map.of() : discovered;
    }
}
