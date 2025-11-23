package com.test.nosugar.mixin.client;

import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.utils.ILivingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class SetScreenMixin {//for witherzilla

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (Minecraft.getInstance() == null) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if ((SnackArmor.SnackProtector.isFullSet(mc.player) && !((ILivingEntity) mc.player).isErased(((LivingEntity)mc.player).getUUID())))
            if (screen instanceof DeathScreen) {
                ci.cancel();
            }
    }
}