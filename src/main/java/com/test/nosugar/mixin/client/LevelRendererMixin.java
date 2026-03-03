package com.test.nosugar.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.utils.render.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
@OnlyIn(Dist.CLIENT)
public class LevelRendererMixin {

    @WrapOperation(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I")
    )
    private int wrapGetTeamColorForGlow(Entity entity, Operation<Integer> original) {
        int originalColor = original.call(entity);

        if (entity instanceof LivingEntity) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            Level level = mc.level;
            ItemStack mainHand = player.getMainHandItem();
            if (player == null || level == null || !player.isShiftKeyDown() || mainHand.getItem() != ModItems.SUGAR_SWORD.get()) {
                return originalColor;
            }

            double radius = 10.0;
            AABB playerAABB = player.getBoundingBox().inflate(radius);
            AABB entityAABB = entity.getBoundingBox();

            if (playerAABB.intersects(entityAABB)) {
                long time = level.getGameTime();
                int index = System.identityHashCode(entity) % 100;
                //System.out.println("Rendering " + entity.getName().getString() + " for " + entityAABB);
                return ColorUtils.waveGrayWhiteColor(time, index, 10.0);
            }
        }

        return originalColor;
    }

}