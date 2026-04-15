package com.test.nosugar.events;

import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.additional.SnackArmor;

import com.test.nosugar.items.CreativeSword;
import com.test.nosugar.utils.entity.event.*;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = NoSugar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {
    @SubscribeEvent
    public static void onLivingMethod(com.test.nosugar.utils.entity.event.LivingEntityMethodEvent event) {
        if (!(event.getEntity() instanceof LivingEntity self) || !(self instanceof ILivingEntity iliving)) return;
        if (event.getMethodType() == LivingEntityMethodEvent.MethodType.GET_HEALTH) {
            if (iliving.isErased(self.getUUID()) || iliving.isErased()) {
                event.setReturnValue(0.f);
                return;
            }
            if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player, true)) {
                event.setReturnValue(player.getMaxHealth());
            }
            if(iliving.getDelta() > 0.f) {
                float maxHealthCap = self.getMaxHealth() - iliving.getDelta();
                event.setReturnValue(Math.min((Float) event.getReturnValue(), maxHealthCap));
            }
        } else if (event.getMethodType() == LivingEntityMethodEvent.MethodType.IS_ALIVE) {
            if (iliving.isErased(self.getUUID()) || iliving.isErased()) {
                event.setReturnValue(false);
                return;
            }
            if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player, true)) {
                event.setReturnValue(true);
            }
            if(iliving.getDelta() > 0.f) {
                event.setReturnValue(self.getHealth() > 0.f);
            }
        } else if (event.getMethodType() == LivingEntityMethodEvent.MethodType.IS_DEAD_OR_DYING) {
            if (iliving.isErased(self.getUUID()) || iliving.isErased()) {
                event.setReturnValue(true);
                return;
            }
            if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player, true)) {
                event.setReturnValue(false);
            }
            if(iliving.getDelta() > 0.f) {
                event.setReturnValue(!self.isAlive());
            }
        }
        else if (event.getMethodType() == LivingEntityMethodEvent.MethodType.IS_REMOVED) {
            if (!self.level().isClientSide && !(self instanceof Player) &&(iliving.isErased(self.getUUID()) || iliving.isErased())) {
                event.setReturnValue(true);
                return;
            }
            if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player, true) &&
                    self.getRemovalReason() != Entity.RemovalReason.CHANGED_DIMENSION &&
                    self.getRemovalReason() != Entity.RemovalReason.DISCARDED) {
                event.setReturnValue(false);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingField(com.test.nosugar.utils.entity.event.LivingEntityFieldEvent event) {
        if (!(event.getEntity() instanceof LivingEntity self) || !(self instanceof Player player)) return;
        if(event.getFieldType() == LivingEntityFieldEvent.FieldType.HURT_TIME && SnackArmor.SnackProtector.isFullSet(player)) {
            event.setNewValue(0);
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        //if (event.player.level().isClientSide()) return;
        if (event.player instanceof ILivingEntity iliving) {
            Item targetItem = ModItems.CREATIVE_SWORD.get();
            ItemStack swordStack = ItemStack.EMPTY;
            var inv = event.player.getInventory();

            for (int i = 0; i < 9; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack.getItem() == targetItem) {
                    swordStack = stack;
                    break;
                }
            }

            boolean hasSword = !swordStack.isEmpty();
            boolean aggroImmune = hasSword && CreativeSword.isAggroImmune(swordStack);
            boolean invulnerable = hasSword && CreativeSword.isInvulnerable(swordStack);

            ForceHaloEvent haloEvent = new ForceHaloEvent(event.player, aggroImmune);
            NoSugarBus.BUS.post(haloEvent);
            iliving.setForceHalo(haloEvent.isForceHalo());

            ForceArmorEvent armorEvent = new ForceArmorEvent(event.player, invulnerable);
            NoSugarBus.BUS.post(armorEvent);
            iliving.setForceFullset(armorEvent.isForceFullset());
        }
    }
}
