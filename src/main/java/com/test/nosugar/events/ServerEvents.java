package com.test.nosugar.events;

import com.test.nosugar.NoSugar;
import com.test.nosugar.items.ModItems;
import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.entity.HomingArrowEntity;
import com.test.nosugar.utils.intercafes.ILivingEntity;
import com.test.nosugar.utils.item.BlessingUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static com.test.nosugar.utils.item.Eraser_Utils.killIfParentFound;

@Mod.EventBusSubscriber(modid = NoSugar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onLivingHurt(LivingHurtEvent event) {
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

            boolean hasEraser = !main.isEmpty() && main.getItem() == ModItems.SUGAR_SWORD.get()
                    || !off.isEmpty() && off.getItem() == ModItems.SUGAR_SWORD.get();

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
            //if (stack.getItem() == ModItems.SUGAR_SWORD.get() || stack.getItem() == ModItems.WORLD_DESTROYER.get()) {
                event.setCanceled(true);
            //}
        }
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ILivingEntity living) {
            living.setErased(false);
            living.unmarkErased(event.getEntity().getUUID());
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.getPlayer().level().isClientSide()) {
            return;
        }

        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!right.is(ModItems.NULL_INGOT.get())) {
            return;
        }

        boolean isValidItem = left.getItem() instanceof ArmorItem ||
                left.getItem() instanceof SwordItem ||
                left.getItem() instanceof PickaxeItem ||
                left.getItem() instanceof AxeItem ||
                left.getItem() instanceof ShovelItem;

        if (!isValidItem) {
            //return;
        }

        if (left.hasTag() && left.getTag().contains("Blessing_of_Sugar") && left.getTag().getBoolean("Blessing_of_Sugar")) {
            //event.setCost(40);
            //event.setOutput(ItemStack.EMPTY);
            return;
        }

        int baseRepairCost = left.getBaseRepairCost() + right.getBaseRepairCost();
        int totalCost = AnvilMenu.calculateIncreasedRepairCost(baseRepairCost) + 10;

        if (totalCost > 39) {
            totalCost = 39;
        }

        ItemStack output = left.copy();
        CompoundTag nbt = output.getOrCreateTag().copy();

        nbt.putBoolean("Blessing_of_Sugar", true);
        nbt.putString("SpecialEffect", "BlessingOfSugar");
        output.setTag(nbt);

        output.setRepairCost(AnvilMenu.calculateIncreasedRepairCost(baseRepairCost));

        event.setOutput(output);
        event.setCost(totalCost);
        event.setMaterialCost(1);
    }

    @SubscribeEvent
    public static void onAnvilUpdate2(AnvilUpdateEvent event) {
        if (event.getPlayer().level().isClientSide()) {
            return;
        }

        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!ModItems.getAllItems().contains(left.getItem()) && !BlessingUtils.isBlessed(left)) {
            return;
        }

        ItemStack output = left.copy();

        ListTag outputModifiers =
                output.getOrCreateTag().getList("AttributeModifiers", Tag.TAG_COMPOUND);

        ListTag rightModifiers =
                right.getOrCreateTag().getList("AttributeModifiers", Tag.TAG_COMPOUND);

        for (int i = 0; i < rightModifiers.size(); i++) {
            CompoundTag rightMod = rightModifiers.getCompound(i);

            String attrName = rightMod.getString("AttributeName");
            String modName = rightMod.getString("Name");
            int operation = rightMod.getInt("Operation");
            double amount = rightMod.getDouble("Amount");
            String slot = rightMod.getString("Slot");

            boolean merged = false;
            for (int j = 0; j < outputModifiers.size(); j++) {
                CompoundTag existing = outputModifiers.getCompound(j);
                if (existing.getString("AttributeName").equals(attrName)
                        && existing.getString("Name").equals(modName)
                        && existing.getInt("Operation") == operation
                        && existing.getString("Slot").equals(slot)) {

                    double current = existing.getDouble("Amount");
                    existing.putDouble("Amount", current + amount);
                    merged = true;
                    break;
                }
            }

            if (!merged) {
                CompoundTag newTag = rightMod.copy();
                newTag.putUUID("UUID", UUID.randomUUID());
                outputModifiers.add(newTag);
            }
        }

        output.getOrCreateTag().put("AttributeModifiers", outputModifiers);

        event.setOutput(output);
        event.setCost(1);
        event.setMaterialCost(1);
    }

    private boolean isValidBaseItem(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ArmorItem ||
                item instanceof SwordItem ||
                item instanceof PickaxeItem ||
                item instanceof AxeItem ||
                item instanceof ShovelItem;
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent e) {
            AttributeInstance instance = e.player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (instance != null) {
                UUID modifierId = UUID.fromString("8c19a0a5-4c3d-4d35-9a5c-8a5c9e5a9c5a");

                if (SnackArmor.SnackProtector.isFullSet(e.player) && e.player.isSprinting()) {
                    if (instance.getModifier(modifierId) == null) {
                        instance.addTransientModifier(new AttributeModifier(
                                modifierId,
                                "snackprotector_speed",
                                0.015777,
                                AttributeModifier.Operation.ADDITION
                        ));
                    }
                    Vec3 lookVec = e.player.getLookAngle().normalize();
                    double amount = e.player.onGround() ?  0.05 : 0.01;
                    Vec3 additionalVelocity = lookVec.scale(amount);
                    e.player.setDeltaMovement(
                            e.player.getDeltaMovement().add(additionalVelocity.x, 0, additionalVelocity.z));
                } else {
                    AttributeModifier existingModifier = instance.getModifier(modifierId);
                    if (existingModifier != null) {
                        instance.removeModifier(existingModifier);
                    }
                }
            }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if(!(event.getSource().getEntity() instanceof LivingEntity living) ||  living.getMainHandItem().getItem() != ModItems.SUGAR_SWORD.get()) return;
        //if(event.getEntity() instanceof ILivingEntity Iliving)Iliving.instantKill();
        System.out.println("Target: " + event.getEntity().getName().getString() + "Attacker: " + event.getSource().getEntity().getName().getString());
    }

}


