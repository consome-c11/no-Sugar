package com.test.nosugar.mixin.sugar_sword;

import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("remapping")
@Mixin(targets = "net.minecraft.world.level.entity.PersistentEntitySectionManager$Callback")
public abstract class PersistentEntitySectionManagerCallbackMixin {
    @Shadow(remap = false)
    private Entity realEntity;

    @Inject(method = "onMove", at = @At("HEAD"), cancellable = true, remap = true)
    private void onMoveGuard(CallbackInfo ci) {
        if (realEntity instanceof ILivingEntity erased && erased.isErased()) {
            //System.out.println("[NoSugar] Prevented moving entity in PersistentEntitySectionManager");
            ci.cancel();
        }
    }
}