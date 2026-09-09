package com.aspctt.paxiextra.mixin;

import com.aspctt.paxiextra.PaxiExtra;
import com.aspctt.paxiextra.interfaces.PackTricks;
import com.aspctt.paxiextra.util.PaxiExtraDiscovery;
import com.aspctt.paxiextra.util.PaxiExtraPacks;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves the load order entries that named no file, once every source has been asked for its packs.
 *
 * <p>Paxi's source cannot do this itself. It is one of many registered through
 * {@code AddPackFindersEvent}, those fire in mod loading order, and a pack from a mod whose listener fires
 * after Paxi's does not exist yet while Paxi is running. Waiting until discovery returns is the only point
 * where the answer is the same regardless of where in the mod list the pack came from.
 *
 * <p>The resolved copy replaces the original under the same id, so the pack is not duplicated and every
 * other entry stays exactly where the repository put it.
 */
@Mixin(PackRepository.class)
public abstract class PackRepositoryMixin {
    @WrapMethod(method = "discoverAvailable")
    private Map<String, Pack> paxiExtra$resolvePacksNamedInLoadOrder(Operation<Map<String, Pack>> original) {
        PaxiExtraDiscovery.begin();
        Map<String, Pack> discovered;
        List<PaxiExtraDiscovery.DeferredPack> deferred;
        try {
            discovered = original.call();
            deferred = PaxiExtraDiscovery.deferred();
        } finally {
            PaxiExtraDiscovery.end();
        }

        if (deferred.isEmpty()) {
            return discovered;
        }

        // A LinkedHashMap so that replacing an entry leaves it where it was, and everything already
        // discovered keeps the order the repository put it in.
        Map<String, Pack> resolved = new LinkedHashMap<>(discovered);
        for (PaxiExtraDiscovery.DeferredPack entry : deferred) {
            Pack originalPack = resolved.get(entry.id());
            if (originalPack == null) {
                PaxiExtra.LOGGER.error("Unable to find pack with name {} specified in the load order file! Skipping...", entry.id());
                continue;
            }
            Pack paxiPack = PaxiExtraPacks.asPaxiPack(originalPack);
            ((PackTricks) paxiPack).paxiExtra$setBelowUserPacks(entry.belowUserPacks());
            resolved.put(entry.id(), paxiPack);
        }
        return ImmutableMap.copyOf(resolved);
    }
}
