package com.aspctt.paxiextra.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.yungnickyoung.minecraft.paxi.PaxiPackSource;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Takes the reorder arrows off a Paxi pack on the pack screen.
 *
 * <p>They do nothing on one. The load order file decides where a Paxi pack sits, and it is reapplied every
 * time the repository reloads, so a pack dragged up or down snaps back where it was. Leaving the arrows
 * there invites a modpack player to spend a while wondering why the game keeps undoing them.
 */
@Mixin(targets = "net.minecraft.client.gui.screens.packs.PackSelectionModel$EntryBase")
public abstract class PackSelectionModelEntryBaseMixin {
    @Shadow
    @Final
    private Pack pack;

    @ModifyReturnValue(method = "canMoveUp", at = @At("RETURN"))
    private boolean paxiExtra$noMovingPaxiPacksUp(boolean original) {
        return original && this.pack.getPackSource() != PaxiPackSource.PACK_SOURCE_PAXI;
    }

    @ModifyReturnValue(method = "canMoveDown", at = @At("RETURN"))
    private boolean paxiExtra$noMovingPaxiPacksDown(boolean original) {
        return original && this.pack.getPackSource() != PaxiPackSource.PACK_SOURCE_PAXI;
    }
}
