package com.test.nosugar.mixin.client;

import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.additional.SugarTotem;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(
            method = "handlePlayerCombatKill",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onHandlePlayerCombatKill(ClientboundPlayerCombatKillPacket packet, CallbackInfo ci) {
        if (Minecraft.getInstance() == null) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (((SnackArmor.SnackProtector.isFullSet(mc.player) || SugarTotem.hasTotem(mc.player)) && !((ILivingEntity) mc.player).isErased(mc.player.getUUID())))
            ci.cancel();

    }
}