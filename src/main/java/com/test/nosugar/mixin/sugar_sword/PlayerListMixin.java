package com.test.nosugar.mixin.sugar_sword;

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
    private void nosugar$onrespawnHEAD(ServerPlayer player, boolean keepinventory, CallbackInfoReturnable<ServerPlayer> cir) {
        if (player instanceof ILivingEntity iliving && (iliving.isErased() || iliving.isErased(player.getUUID()))) {
            iliving.unmarkErased(player.getUUID());
            iliving.setErased(false);
        }
    }

    @Inject(method = "respawn", at = @At("RETURN"), cancellable = true)
    private void nosugar$onrespawnRETURN(ServerPlayer player, boolean keepinventory, CallbackInfoReturnable<ServerPlayer> cir) {
        if (cir.getReturnValue() instanceof ILivingEntity iliving && (iliving.isErased() || iliving.isErased(player.getUUID()))) {
            iliving.unmarkErased(player.getUUID());
            iliving.setErased(false);
        }
    }
}

