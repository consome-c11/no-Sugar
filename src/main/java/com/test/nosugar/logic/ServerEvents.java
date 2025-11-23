package com.test.nosugar.logic;

import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.ModDamageTypes;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.entity.HomingArrowEntity;
import com.test.nosugar.utils.ILivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NoSugar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().is(ModDamageTypes.ERASE)) {
            Entity attacker = event.getSource().getEntity();
            if (attacker instanceof Player player) {
                if (event.getEntity() instanceof ILivingEntity player_) player_.instantKill();
            } else if (attacker instanceof ILivingEntity player_) player_.instantKill();
            event.setCanceled(true);
        }
        Entity direct = event.getSource().getDirectEntity();
        if (direct instanceof HomingArrowEntity homing) {
            event.setAmount(0);
            event.setCanceled(true);
        }
        if (event.getEntity() instanceof Player player && SnackArmor.SnackProtector.hasSnackProtector(player)) {
            event.setAmount(0);
        }
    }

    public static BlockHitResult getPlayerLookingAt(Player player, int reach) {
        Level level = player.level();

        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle().scale(reach);
        Vec3 endPosition = eyePosition.add(lookVector);

        ClipContext context = new ClipContext(
                eyePosition,
                endPosition,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                player
        );

        return level.clip(context);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {

    }

    @SubscribeEvent
    public void onLootingLevel(LootingLevelEvent event) {
        if (event.getDamageSource().getEntity() instanceof Player player) {
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();

            boolean hasEraser = !main.isEmpty() && main.getItem() == ModItems.ERASER_ITEM.get()
                    || !off.isEmpty() && off.getItem() == ModItems.ERASER_ITEM.get();

            boolean hasWorldDestroyer = !main.isEmpty() && main.getItem() == ModItems.WORLD_DESTROYER.get()
                    || !off.isEmpty() && off.getItem() == ModItems.WORLD_DESTROYER.get();

            if (hasEraser || hasWorldDestroyer) {
                event.setLootingLevel(7);
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ILivingEntity living) {
            if (living.isErased()) {
                //event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onAttackEntity(LivingAttackEvent event) {
        Entity player = event.getSource().getEntity();
        if (player instanceof LivingEntity player_) {
            ItemStack stack = player_.getMainHandItem();
            if (stack.getItem() == ModItems.ERASER_ITEM.get() || stack.getItem() == ModItems.WORLD_DESTROYER.get()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ILivingEntity living) {
            living.setErased(false);
            living.unmarkErased(event.getEntity().getUUID());
        }
    }

}


