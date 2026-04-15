package com.test.nosugar.mixin.client;

import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.additional.SugarTotem;
import com.test.nosugar.utils.entity.LivingEntityUtils;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
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
        if (mc.player == null || mc.level == null || !LivingEntityUtils.isAlive(mc.player) || LivingEntityUtils.isDeadOrDying(mc.player) ||
                LivingEntityUtils.getHealth(mc.player) <= 0.f || ((ILivingEntity) mc.player).isErased()) {
            //if(mc.player != null)System.out.println("isAlive: " + LivingEntityUtils.isAlive(mc.player) + " isDeadOrDying: " + LivingEntityUtils.isDeadOrDying(mc.player) + " Health: " + LivingEntityUtils.getHealth(mc.player));
            return;
        }
        //System.out.println("isAlive: " + LivingEntityUtils.isAlive(mc.player) + " isDeadOrDying: " + LivingEntityUtils.isDeadOrDying(mc.player) + " Health: " + LivingEntityUtils.getHealth(mc.player));
        if ((SnackArmor.SnackProtector.isFullSet(mc.player) || SugarTotem.hasTotem(mc.player)))
            if (screen instanceof DeathScreen) {
                ci.cancel();
            }
    }
}