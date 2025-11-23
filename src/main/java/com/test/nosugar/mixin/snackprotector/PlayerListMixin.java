package com.test.nosugar.mixin.snackprotector;

import com.test.nosugar.additional.SnackArmor;
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
        if (SnackArmor.SnackProtector.isFullSet(player) && player.isAlive() && !player.isRemoved()) {
            cir.cancel();
            cir.setReturnValue(player);//アホやらかした 何故自分はあんなにも頭が悪いのだろうか。
            //@test ちゃんとMixinするときは元関数読めよ!
        }
        return;
    }
}
