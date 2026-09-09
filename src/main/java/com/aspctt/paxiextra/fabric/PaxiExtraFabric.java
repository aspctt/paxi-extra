package com.aspctt.paxiextra.fabric;

import com.aspctt.paxiextra.PaxiExtra;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class PaxiExtraFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricLoader loader = FabricLoader.getInstance();
        PaxiExtra.init(loader.getGameDir().toFile(), loader.getConfigDir().toFile());
    }
}
