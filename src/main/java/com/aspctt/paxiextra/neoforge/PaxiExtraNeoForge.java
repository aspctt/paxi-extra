package com.aspctt.paxiextra.neoforge;

import com.aspctt.paxiextra.PaxiExtra;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

@Mod(PaxiExtra.MOD_ID)
public class PaxiExtraNeoForge {
    public PaxiExtraNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        PaxiExtra.init(FMLPaths.GAMEDIR.get().toFile(), FMLPaths.CONFIGDIR.get().toFile());
    }
}
