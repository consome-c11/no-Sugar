package com.test.nosugar.mixin.common;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.test.nosugar.NoSugar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.common.capability.ItemizedCurioCapability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Mixin(ItemizedCurioCapability.class)
public abstract class ItemizedCurioCapabilityMixin {
    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), remap = false, cancellable = true)
    private void mergeSugarEffect(SlotContext slotContext, UUID uuid, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir) {
        ItemStack stack = ((ItemizedCurioCapability) (Object) this).getStack();
        if (!stack.hasTag() || !stack.getTag().contains("SugarEffect", Tag.TAG_LIST)) {
            return;
        }
        ListTag sugarEffects = stack.getTag().getList("SugarEffect", Tag.TAG_COMPOUND);

        Multimap<Attribute, AttributeModifier> original = cir.getReturnValue();
        if (original == null) {
            return;
        }

        HashMultimap<Attribute, AttributeModifier> result = HashMultimap.create();
        result.putAll(original);

        mergeEffect(sugarEffects, result);

        cir.setReturnValue(result);
    }

    @Unique
    private static void mergeEffect(ListTag sugarEffects, Multimap<Attribute, AttributeModifier> result) {
        for (int i = 0; i < sugarEffects.size(); i++) {
            CompoundTag tag = sugarEffects.getCompound(i);

            String attrName = tag.getString("AttributeName");
            ResourceLocation attrId = ResourceLocation.tryParse(attrName);
            if (attrId == null) continue;

            Attribute attribute = BuiltInRegistries.ATTRIBUTE.getOptional(attrId).orElse(null);
            if (attribute == null) continue;

            AttributeModifier modifier = AttributeModifier.load(tag);
            if (modifier == null) continue;

            boolean merged = false;

            Collection<AttributeModifier> existingModifiers = result.get(attribute);
            List<AttributeModifier> toRemove = new ArrayList<>();

            for (AttributeModifier existing : existingModifiers) {
                if (existing.getId().equals(modifier.getId())) {
                    double newAmount = existing.getAmount() + modifier.getAmount();

                    AttributeModifier newModifier = new AttributeModifier(
                            existing.getId(),
                            existing.getName(),
                            newAmount,
                            existing.getOperation()
                    );

                    toRemove.add(existing);
                    result.put(attribute, newModifier);

                    NoSugar.LOGGER.info("[NoSugar]   [MERGE] {} Amount: {} + {} = {}",
                            attrName, existing.getAmount(), modifier.getAmount(), newAmount);

                    merged = true;
                    break;
                }
            }

            for (AttributeModifier old : toRemove) {
                result.remove(attribute, old);
            }

            if (!merged) {
                result.put(attribute, modifier);
            }
        }
    }
}