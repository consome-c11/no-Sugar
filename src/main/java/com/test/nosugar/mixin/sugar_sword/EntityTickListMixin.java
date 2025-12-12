package com.test.nosugar.mixin.sugar_sword;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(EntityTickList.class)
public abstract class EntityTickListMixin {
    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void onAdd(Entity entity, CallbackInfo ci) {
        if (entity instanceof ILivingEntity erase && erase.isErased()) {
            ci.cancel();
        }
    }

    @WrapWithCondition(
            method = "forEach",
            at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V")
    )
    private boolean onforEach(Consumer<Entity> consumer, Object entityObj) {//実行される時はObjectになるんかぁ   めんどくせ()
        if (entityObj instanceof Entity entity) {
            return !(entity instanceof ILivingEntity erase && erase.isErased());
        }
        return true;
    }

}