package com.test.nosugar.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.test.nosugar.Config;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static com.test.nosugar.utils.entity.EntityUtils.enable_tag;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {

    @WrapOperation(
            method = "is(Lnet/minecraft/tags/TagKey;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean onIsTag(
            Holder<DamageType> instance,
            TagKey<DamageType> tag,
            Operation<Boolean> original
    ) {
        DamageSource this_ = (DamageSource)((Object)this);
        boolean result = original.call(instance, tag);

        if (enable_tag(this_, tag)) {
            return true;
        }

        return result;
    }

}
