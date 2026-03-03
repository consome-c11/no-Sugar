package com.test.nosugar.events;

import com.google.common.collect.Multimap;
import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.entity.HomingArrowEntity;
import com.test.nosugar.utils.Deets;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import com.test.nosugar.utils.item.BlessingUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
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
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.test.nosugar.utils.Deets.TINKERSCONSTRUCT;

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
            //魔改造ぱーりない
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
        output.setHoverName(Component.literal(event.getName()));
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
        CompoundTag outputTag = output.getOrCreateTag();
        Map<String, CompoundTag> merged = new HashMap<>();

        //Left
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Multimap<Attribute, AttributeModifier> leftModifiers = left.getAttributeModifiers(slot);
            if (leftModifiers.isEmpty()) {
                continue;
            }

            String slotName = slot.getName();

            for (Map.Entry<Attribute, AttributeModifier> entry : leftModifiers.entries()) {
                Attribute attr = entry.getKey();
                AttributeModifier mod = entry.getValue();
                String attrName = BuiltInRegistries.ATTRIBUTE.getKey(attr).toString();

                String key = attrName + "_" + mod.getOperation() + "_" + slotName;

                if (!merged.containsKey(key)) {
                    CompoundTag tag = new CompoundTag();
                    tag.putString("AttributeName", attrName);
                    tag.putUUID("UUID", mod.getId());
                    tag.putDouble("Amount", mod.getAmount());
                    tag.putInt("Operation", mod.getOperation().toValue());
                    tag.putString("Name", mod.getName());
                    tag.putString("Slot", slotName);
                    merged.put(key, tag);
                } else {
                    CompoundTag existing = merged.get(key);
                    double current = existing.getDouble("Amount");
                    existing.putDouble("Amount", current + mod.getAmount());
                }
            }
        }

        //Right
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Multimap<Attribute, AttributeModifier> rightModifiers = right.getAttributeModifiers(slot);
            if (rightModifiers.isEmpty()) {
                continue;
            }

            String slotName = slot.getName();

            for (Map.Entry<Attribute, AttributeModifier> entry : rightModifiers.entries()) {
                Attribute attr = entry.getKey();
                AttributeModifier mod = entry.getValue();
                String attrName = BuiltInRegistries.ATTRIBUTE.getKey(attr).toString();

                String key = attrName + "_" + mod.getOperation() + "_" + slotName;

                if (merged.containsKey(key)) {
                    CompoundTag existing = merged.get(key);
                    double current = existing.getDouble("Amount");
                    existing.putDouble("Amount", current + mod.getAmount());
                } else {
                    CompoundTag newTag = new CompoundTag();
                    newTag.putString("AttributeName", attrName);
                    newTag.putUUID("UUID", UUID.randomUUID());
                    newTag.putDouble("Amount", mod.getAmount());
                    newTag.putInt("Operation", mod.getOperation().toValue());
                    newTag.putString("Name", mod.getName());
                    newTag.putString("Slot", slotName);
                    merged.put(key, newTag);
                }
            }
        }

        ListTag finalModifiers = new ListTag();
        for (CompoundTag tag : merged.values()) {
            finalModifiers.add(tag);
        }
        ListTag sugarEffects = output.getOrCreateTag()
                .getList("SugarEffect", Tag.TAG_COMPOUND);

        mergeCurioAttributes(event.getPlayer(), left, sugarEffects);
        mergeCurioAttributes(event.getPlayer(), right, sugarEffects);

        output.getOrCreateTag().put("SugarEffect", sugarEffects);

        outputTag.put("AttributeModifiers", finalModifiers);
        output.setHoverName(Component.literal(event.getName()));
        event.setOutput(output);
        event.setCost(1);
        event.setMaterialCost(1);
    }

    @SuppressWarnings("removal")
    private static void mergeCurioAttributes(LivingEntity player, ItemStack stack, ListTag outputModifiers) {
        Deets.require(TINKERSCONSTRUCT).run(() -> {
            if (stack.isEmpty() || !(stack.getItem() instanceof ICurioItem curioItem)) {
                return;
            }

            CuriosApi.getSlots(player.level()).forEach((identifier, slotType) -> {
                SlotContext context = new SlotContext(identifier, player, 0, false, true);
                UUID uuid = UUID.nameUUIDFromBytes((identifier + stack.getItem()).getBytes());

                if (!curioItem.canEquip(context, stack)) {
                    return;
                }

                Multimap<Attribute, AttributeModifier> modifiers = curioItem.getAttributeModifiers(context, uuid, stack);
                if (modifiers.isEmpty()) {
                    return;
                }

                modifiers.forEach((attribute, modifier) -> {
                    ResourceLocation attrId = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
                    if (attrId == null) return;

                    String attrName = attrId.toString();
                    String name = modifier.getName();
                    int operation = modifier.getOperation().toValue();
                    double amount = modifier.getAmount();

                    boolean merged = false;
                    for (int i = 0; i < outputModifiers.size(); i++) {
                        CompoundTag existing = outputModifiers.getCompound(i);

                        if (!existing.getString("AttributeName").equals(attrName)) continue;
                        if (!existing.getString("Name").equals(name)) continue;
                        if (existing.getInt("Operation") != operation) continue;

                        double currentAmount = existing.getDouble("Amount");
                        existing.putDouble("Amount", currentAmount + amount);

                        merged = true;
                        break;
                    }

                    if (!merged) {
                        CompoundTag tag = new CompoundTag();
                        tag.putString("AttributeName", attrName);
                        tag.putString("Name", name);
                        tag.putDouble("Amount", amount);
                        tag.putInt("Operation", operation);
                        tag.putUUID("UUID", UUID.randomUUID());
                        tag.putString("Slot", "");

                        outputModifiers.add(tag);

                    }
                });
            });
        });
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
                double amount = e.player.onGround() ? 0.05 : 0.01;
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
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ILivingEntity living) {
            living.setErased(false);
            living.unmarkErased(event.getEntity().getUUID());
        }
    }

    private boolean isValidBaseItem(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ArmorItem ||
                item instanceof SwordItem ||
                item instanceof PickaxeItem ||
                item instanceof AxeItem ||
                item instanceof ShovelItem;
    }


}


