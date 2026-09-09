package com.aspctt.paxiextra.mixin;

import com.aspctt.paxiextra.util.PaxiExtraDiscovery;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Records what each repository source contributes as discovery runs, so that by the time Paxi's source is
 * asked for its packs it can see everything found before it. That is what lets a load order name a pack a
 * mod provides rather than a file on disk.
 *
 * <p>NeoForge appends the sources mods register through {@code AddPackFindersEvent} after the built-in and
 * folder sources, so Paxi's source is always among the last to run and the map is complete by then.
 */
@Mixin(PackRepository.class)
public abstract class PackRepositoryMixin {
    @Unique
    private final Map<String, Pack> paxiExtra$discovered = new LinkedHashMap<>();

    @WrapMethod(method = "discoverAvailable")
    private Map<String, Pack> paxiExtra$publishDiscoveryInProgress(Operation<Map<String, Pack>> original) {
        this.paxiExtra$discovered.clear();
        PaxiExtraDiscovery.begin(this.paxiExtra$discovered);
        try {
            return original.call();
        } finally {
            PaxiExtraDiscovery.end();
            this.paxiExtra$discovered.clear();
        }
    }

    @WrapOperation(
            method = "discoverAvailable",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/RepositorySource;loadPacks(Ljava/util/function/Consumer;)V"))
    private void paxiExtra$recordDiscoveredPacks(RepositorySource source, Consumer<Pack> packAdder,
                                                Operation<Void> original) {
        original.call(source, (Consumer<Pack>) pack -> {
            pack.streamSelfAndChildren().forEach(child -> this.paxiExtra$discovered.put(child.getId(), child));
            packAdder.accept(pack);
        });
    }
}
