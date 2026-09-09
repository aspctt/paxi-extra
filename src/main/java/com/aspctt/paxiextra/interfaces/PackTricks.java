package com.aspctt.paxiextra.interfaces;

/**
 * Implemented on {@code Pack} by {@code PackMixin}. Marks a Paxi pack that was listed before the
 * {@code --user--} entry in the load order, which places it underneath the player's own packs instead of
 * on top of them.
 */
public interface PackTricks {
    boolean paxiExtra$isBelowUserPacks();

    void paxiExtra$setBelowUserPacks(boolean belowUserPacks);
}
