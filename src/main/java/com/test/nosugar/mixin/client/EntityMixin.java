package com.test.nosugar.mixin.client;

import com.test.nosugar.additional.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class)
@OnlyIn(Dist.CLIENT)
public class EntityMixin {

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void onIsCurrentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof LivingEntity)) return;

        Player localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;

        if (localPlayer.isShiftKeyDown() && localPlayer.getMainHandItem().getItem() == ModItems.ERASER_ITEM.get() && entity != localPlayer) {
            double radius = 10.0;
            AABB inflatedPlayerAABB = localPlayer.getBoundingBox().inflate(radius);

            AABB entityAABB = entity.getBoundingBox();

            if (inflatedPlayerAABB.intersects(entityAABB)) {
                cir.setReturnValue(true);
            }
        }
    }

    //CitadelがRedirectでやってるせいで落ちるわクソが
    //なんで使うんだよマジで
    /*@Inject(method = "getTeamColor", at = @At("RETURN"))
    private void onGetTeamColor(CallbackInfoReturnable<Integer> cir) {
        if (GlowFlagHolder.isGlowingContext() && ((Object)this) instanceof Entity entity && entity instanceof LivingEntity living) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if(localPlayer == null) return;
            if (localPlayer.isShiftKeyDown() && localPlayer.getMainHandItem().getItem() == ModItems.ERASER_ITEM.get()) {
                double radius = 10.0;
                AABB inflatedPlayerAABB = localPlayer.getBoundingBox().inflate(radius);

                AABB entityAABB = entity.getBoundingBox();

                if (inflatedPlayerAABB.intersects(entityAABB)) {
                    long time = Minecraft.getInstance().level.getGameTime();
                    int index = System.identityHashCode(this) % 100;
                    cir.setReturnValue(waveGrayWhiteColor(time, index, 20.0));
                }
            }
        }
    }*/

}

