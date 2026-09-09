package com.aspctt.paxiextra;

import com.aspctt.paxiextra.util.PaxiExtraOrdering;
import com.yungnickyoung.minecraft.paxi.PaxiCommon;
import com.yungnickyoung.minecraft.yungsapi.io.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Everything the mod does at start-up, minus the part that differs by loader. Each loader has an entry
 * point of its own that hands the two directories over, because that is the only thing NeoForge and Fabric
 * disagree on here: NeoForge asks a mod for a constructor and Fabric for an interface, and each has its own
 * way of naming the instance folder.
 */
public final class PaxiExtra {
    public static final String MOD_ID = "paxiextra";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /**
     * The instance directory, which is what a load order entry is resolved against before the Paxi pack
     * folder is tried. Paxi keeps the same field but only fills it in from its own entry point, so this
     * mod reads the path itself rather than depending on the order the two are constructed in.
     */
    public static File BASE_GAME_DIRECTORY;
    public static File BASE_PACK_DIRECTORY;
    public static File DATA_PACK_DIRECTORY;
    public static File DATAPACK_ORDERING_FILE;

    private PaxiExtra() {
    }

    public static void init(File gameDirectory, File configDirectory) {
        BASE_GAME_DIRECTORY = gameDirectory;
        BASE_PACK_DIRECTORY = new File(configDirectory, "paxi");

        // Paxi creates the data pack folder and its load order file inside loadPacks, which for server data
        // does not run until a world is loaded. Doing it here means a fresh instance has both to edit before
        // the player ever enters a world. Paxi is a required dependency ordered before this mod, so its
        // paths are already set; the fallbacks only cover a Paxi that changed how it initialises.
        DATA_PACK_DIRECTORY = PaxiCommon.DATA_PACK_DIRECTORY == null
                ? new File(BASE_PACK_DIRECTORY, "datapacks")
                : PaxiCommon.DATA_PACK_DIRECTORY.toFile();
        DATAPACK_ORDERING_FILE = PaxiCommon.DATAPACK_ORDERING_FILE == null
                ? new File(BASE_PACK_DIRECTORY, "datapack_load_order.json")
                : PaxiCommon.DATAPACK_ORDERING_FILE;

        createPackFolder(DATA_PACK_DIRECTORY);
        createOrderingFile(DATAPACK_ORDERING_FILE);
    }

    private static void createPackFolder(File folder) {
        if (!folder.isDirectory() && !folder.mkdirs()) {
            LOGGER.warn("Couldn't create the pack folder at {}", folder);
        }
    }

    private static void createOrderingFile(File orderingFile) {
        if (orderingFile.exists()) {
            return;
        }
        try {
            JSON.createJsonFileFromObject(orderingFile.toPath(), new PaxiExtraOrdering(new String[0]));
        } catch (IOException e) {
            LOGGER.warn("Couldn't create the load order file {}", orderingFile, e);
        }
    }
}
