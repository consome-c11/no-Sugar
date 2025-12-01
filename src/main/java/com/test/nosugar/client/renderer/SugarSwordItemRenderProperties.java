package com.test.nosugar.client.renderer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class SugarSwordItemRenderProperties implements IClientItemExtensions {

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack itemStack) {
        if (entity instanceof Player player) {
            if (player.getUsedItemHand() == hand && player.isUsingItem()) {
                return HumanoidModel.ArmPose.BLOCK;
            }
        }
        return HumanoidModel.ArmPose.ITEM;
    }

}