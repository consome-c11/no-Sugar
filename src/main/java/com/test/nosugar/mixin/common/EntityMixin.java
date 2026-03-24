package com.test.nosugar.mixin.common;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

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
