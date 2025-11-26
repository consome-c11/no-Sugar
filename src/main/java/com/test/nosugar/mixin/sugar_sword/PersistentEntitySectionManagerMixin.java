package com.test.nosugar.mixin.sugar_sword;

import com.test.nosugar.utils.intercafes.ILivingEntity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerMixin {
    @Inject(method = "addEntityUuid", at = @At("HEAD"), cancellable = true)
    private void onAddEntityUuid(EntityAccess entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof ILivingEntity erase && erase.isErased()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}