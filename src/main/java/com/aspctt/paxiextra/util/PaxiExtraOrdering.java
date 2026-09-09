package com.aspctt.paxiextra.util;

import com.google.gson.annotations.SerializedName;

/**
 * The shape of a Paxi load order file. Paxi declares the same record privately, so it is repeated here to
 * read and write the file without going through Paxi.
 */
public record PaxiExtraOrdering(@SerializedName("loadOrder") String[] orderedPackNames) {
}
