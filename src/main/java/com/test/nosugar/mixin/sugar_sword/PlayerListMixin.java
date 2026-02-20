package com.test.nosugar.mixin.sugar_sword;

import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "respawn", at = @At("HEAD"), cancellable = true)
    private void onrespawn(ServerPlayer player, boolean keepinventory, CallbackInfoReturnable<ServerPlayer> cir) {
        if (player instanceof ILivingEntity iliving && (iliving.isErased() || iliving.isErased(player.getUUID()))) {
            iliving.unmarkErased(player.getUUID());
            iliving.setErased(false);
        }
        return;
    }
}

