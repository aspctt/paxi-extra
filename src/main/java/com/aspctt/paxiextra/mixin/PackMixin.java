package com.aspctt.paxiextra.mixin;

import com.aspctt.paxiextra.interfaces.PackTricks;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

@Mixin(Pack.class)
public class PackMixin implements PackTricks {
    @Unique
    private boolean paxiExtra$belowUserPacks = false;

    @Override
    public boolean paxiExtra$isBelowUserPacks() {
        return this.paxiExtra$belowUserPacks;
    }

    @Override
    public void paxiExtra$setBelowUserPacks(boolean belowUserPacks) {
        this.paxiExtra$belowUserPacks = belowUserPacks;
    }

    /**
     * Paxi packs are inserted at the top, which puts them above everything the player selected themselves.
     * A pack listed before the {@code --user--} entry in the load order is meant to sit underneath those
     * instead, so it is placed before the first top pack that is not itself one of these. Packs marked this
     * way keep their order relative to each other, because the scan walks past them.
     */
    @Mixin(Pack.Position.class)
    public static abstract class PackPositionMixin {
        @Inject(method = "insert", at = @At("HEAD"), cancellable = true)
        private void paxiExtra$insertBelowUserPacks(List<Object> list, Object element,
                                                   Function<Object, PackSelectionConfig> configFactory,
                                                   boolean flipPosition, CallbackInfoReturnable<Integer> cir) {
            // flipPosition is only set when the pack screen moves a pack between the selected and
            // unselected lists, which is not what this placement is about.
            if (flipPosition || !(element instanceof Pack pack) || !((PackTricks) pack).paxiExtra$isBelowUserPacks()) {
                return;
            }

            int index;
            for (index = 0; index < list.size(); index++) {
                if (list.get(index) instanceof Pack other
                        && other.getDefaultPosition() == Pack.Position.TOP
                        && !((PackTricks) other).paxiExtra$isBelowUserPacks()) {
                    break;
                }
            }

            list.add(index, element);
            cir.setReturnValue(index);
        }
    }
}
