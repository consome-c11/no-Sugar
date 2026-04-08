package com.test.nosugar.mixin.snackprotector;

import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.utils.entity.LivingEntityUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerList.class, priority = Integer.MAX_VALUE)
public class PlayerListMixin {

    //互換性の都合上で一度ヘルパー関数挟んでる
    @Inject(method = "respawn", at = @At("HEAD"), cancellable = true)
    private void nosugar$onrespawn(ServerPlayer player, boolean keepinventory, CallbackInfoReturnable<ServerPlayer> cir) {
        System.out.println("isAlive: " + LivingEntityUtils.isAlive(player) + " isDeadOrDying: " + LivingEntityUtils.isDeadOrDying(player) + " Health: " + LivingEntityUtils.getHealth(player));
        if (SnackArmor.SnackProtector.isFullSet(player) && player.isAlive() && !player.isDeadOrDying()
                && player.getHealth() > 0.f && !player.isRemoved() && !player.isRespawnForced()) {
            cir.cancel();
            cir.setReturnValue(player);
            //@test ちゃんとMixinするときは元関数読めよ!
        }
    }
}
