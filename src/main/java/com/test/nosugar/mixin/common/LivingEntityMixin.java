package com.test.nosugar.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.test.nosugar.Config;
import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.utils.item.TicUtils;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import static com.test.nosugar.utils.entity.EntityUtils.enable_tag;
import static com.test.nosugar.utils.entity.EntityUtils.getretInvulnerable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    /*@WrapOperation(
            method = "hurt",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean hurt_CheckTag(
            DamageSource source, TagKey<DamageType> tag, Operation<Boolean> original
    ) {
        if(enable_tag(source, tag)) return true;
        return original.call(source, tag);
    }

    @WrapOperation(
            method = "hurt",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/world/damagesource/DamageSource;)Z")
    )
    private boolean hurt_CheckInvulnerable(
            LivingEntity instance, DamageSource source, Operation<Boolean> original
    ) {

        if(getretInvulnerable(instance, source)) return false;
        return original.call(instance, source);
    }

    @WrapOperation(
            method = "actuallyHurt",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/world/damagesource/DamageSource;)Z")
    )
    private boolean actuallyHurt_CheckInvulnerable(
            LivingEntity instance, DamageSource source, Operation<Boolean> original
    ) {

        if(getretInvulnerable(instance, source)) return false;
        return original.call(instance, source);
    }

    @WrapOperation(
            method = "getDamageAfterArmorAbsorb",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean on_getDamageAfterArmorAbsorb_CheckTag(
            DamageSource source, TagKey<DamageType> tag, Operation<Boolean> original
    ) {
        if(enable_tag(source, tag)) return true;
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
        if(enable_tag(source, tag)) return true;
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
        if(enable_tag(source, tag)) return true;
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
        if(enable_tag(source, tag)) return true;
        return original.call(source, tag);
    }*/

}
