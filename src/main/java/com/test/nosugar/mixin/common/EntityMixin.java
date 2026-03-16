package com.test.nosugar.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import static com.test.nosugar.utils.entity.EntityUtils.enable_tag;

@Mixin(Entity.class)
public class EntityMixin {
    /*@WrapOperation(
            method = "isInvulnerableTo",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean on_isInvulnerableTo_CheckTag(
            DamageSource source, TagKey<DamageType> tag, Operation<Boolean> original
    ) {
        if(enable_tag(source, tag)) return true;
        return original.call(source, tag);
    }*/
}
