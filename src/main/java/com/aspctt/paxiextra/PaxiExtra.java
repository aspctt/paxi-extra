package com.aspctt.paxiextra;

import com.aspctt.paxiextra.util.PaxiExtraOrdering;
import com.yungnickyoung.minecraft.paxi.PaxiCommon;
import com.yungnickyoung.minecraft.yungsapi.io.JSON;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

@Mod(PaxiExtra.MOD_ID)
public class PaxiExtra {
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

    public PaxiExtra(IEventBus modEventBus, ModContainer modContainer) {
        BASE_GAME_DIRECTORY = FMLPaths.GAMEDIR.get().toFile();
        BASE_PACK_DIRECTORY = new File(FMLPaths.CONFIGDIR.get().toFile(), "paxi");

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
