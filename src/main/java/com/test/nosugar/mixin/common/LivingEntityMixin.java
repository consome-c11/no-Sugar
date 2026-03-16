package com.test.nosugar.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.utils.item.TicUtils;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @WrapOperation(
            method = "hurt",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean hurt_CheckTag(
            DamageSource source, TagKey<DamageType> tag, Operation<Boolean> original
    ) {
        if(getret(source, tag)) return true;
        return original.call(source, tag);
    }

    @WrapOperation(
            method = "getDamageAfterArmorAbsorb",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean on_getDamageAfterArmorAbsorb_CheckTag(
            DamageSource source, TagKey<DamageType> tag, Operation<Boolean> original
    ) {
        if(getret(source, tag)) return true;
        return original.call(source, tag);
    }

    @WrapOperation(
            method = "getDamageAfterMagicAbsorb",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean on_getDamageAfterMagicAbsorb_CheckTag(
            DamageSource source, TagKey<DamageType> tag, Operation<Boolean> original
    ) {
        if(getret(source, tag)) return true;
        return original.call(source, tag);
    }
    @WrapOperation(
            method = "isDamageSourceBlocked",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean on_isDamageSourceBlocked_CheckTag(
            DamageSource source, TagKey<DamageType> tag, Operation<Boolean> original
    ) {
        if(getret(source, tag)) return true;
        return original.call(source, tag);
    }
    @WrapOperation(
            method = "checkTotemDeathProtection",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean on_checkTotemDeathProtection_CheckTag(
            DamageSource source, TagKey<DamageType> tag, Operation<Boolean> original
    ) {
        if(getret(source, tag)) return true;
        return original.call(source, tag);
    }

    @Unique
    private boolean getret(DamageSource source, TagKey<DamageType> tag){
        if(source.getEntity() != null)NoSugar.LOGGER.info("Source Entity: " + source.getEntity().getName());
        if ((tag == DamageTypeTags.BYPASSES_COOLDOWN
                || tag == DamageTypeTags.BYPASSES_ARMOR
                || tag == DamageTypeTags.BYPASSES_EFFECTS
                || tag == DamageTypeTags.BYPASSES_ENCHANTMENTS
                || tag == DamageTypeTags.BYPASSES_INVULNERABILITY
                || tag == DamageTypeTags.BYPASSES_SHIELD)
                && source.getEntity() instanceof LivingEntity living
                && (living.getMainHandItem().getItem() == ModItems.SUGAR_SWORD.get()
                || living.getMainHandItem().getItem() == ModItems.WORLD_DESTROYER.get()
                || living.getMainHandItem().getItem() == ModItems.TAIL_OF_NINE.get()
                || TicUtils.hasSugarMod(living.getMainHandItem()))
        ) {
            return true;
        }
        return false;
    }
}
