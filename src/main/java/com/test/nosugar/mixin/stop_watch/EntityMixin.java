package com.test.nosugar.mixin.stop_watch;

import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nosugar$ontick(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        EntityAccessor iself = (EntityAccessor) self;
        if(TimeStopManager.isStopped(self.level()) && !TimeStopManager.CanMove(self)) {
            /*iself.setXo(self.getX());
            iself.setYo(self.getY());
            iself.setZo(self.getZ());
            iself.setXRotO(self.getXRot());
            iself.setYRotO(self.getYRot());
            self.setDeltaMovement(Vec3.ZERO);*/
            ci.cancel();
        }
    }

    /*@Inject(method = "lerpMotion", at = @At("HEAD"), cancellable = true)
    private void nosugar$onlerpMotion(double p_20306_, double p_20307_, double p_20308_, CallbackInfo ci){
        Entity self = (Entity) (Object) this;
        EntityAccessor iself = (EntityAccessor) self;
        if(TimeStopManager.isStopped(self.level()) && !TimeStopManager.CanMove(self)) {
            ci.cancel();
        }
    }

    @Inject(method = "setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), cancellable = true)
    private void nosugar$onsetDeltaMovement(Vec3 p_20257_, CallbackInfo ci){
        Entity self = (Entity) (Object) this;
        EntityAccessor iself = (EntityAccessor) self;
        if(TimeStopManager.isStopped(self.level()) && !TimeStopManager.CanMove(self)) {
            ci.cancel();
        }
    }

    @Inject(method = "setDeltaMovement(DDD)V", at = @At("HEAD"), cancellable = true)
    private void nosugar$onsetDeltaMovement(double p_20335_, double p_20336_, double p_20337_, CallbackInfo ci){
        Entity self = (Entity) (Object) this;
        EntityAccessor iself = (EntityAccessor) self;
        if(TimeStopManager.isStopped(self.level()) && !TimeStopManager.CanMove(self)) {
            ci.cancel();
        }
    }

    @Inject(method = "getPosition", at = @At("RETURN"), cancellable = true)
    private void nosugar$ongetPosition(float p_20319_, CallbackInfoReturnable<Vec3> cir) {
        Entity self = (Entity) (Object) this;
        if(TimeStopManager.isStopped(self.level()) && !TimeStopManager.CanMove(self)) {
            double d0 = self.getX();
            double d1 = self.getY();
            double d2 = self.getZ();
            cir.setReturnValue(new Vec3(d0, d1, d2));
        }
    }

    @Inject(method = "getXRot", at = @At("RETURN"), cancellable = true)
    private void nosugar$onGetXRot(CallbackInfoReturnable<Float> cir) {
        Entity self = (Entity) (Object) this;
        EntityAccessor iself = (EntityAccessor) self;
        if(TimeStopManager.isStopped(self.level()) && !TimeStopManager.CanMove(self)) {
            cir.setReturnValue(iself.getXRotO());
        }
    }

    @Inject(method = "getYRot", at = @At("RETURN"), cancellable = true)
    private void nosugar$onGetYRot(CallbackInfoReturnable<Float> cir) {
        Entity self = (Entity) (Object) this;
        EntityAccessor iself = (EntityAccessor) self;
        if(TimeStopManager.isStopped(self.level()) && !TimeStopManager.CanMove(self)) {
            cir.setReturnValue(iself.getYRotO());
        }
    }

    @Inject(method = "turn", at = @At("RETURN"), cancellable = true)
    private void nosugar$onturn(double p_19885_, double p_19886_, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        EntityAccessor iself = (EntityAccessor) self;
        if(TimeStopManager.isStopped(self.level()) && !TimeStopManager.CanMove(self)) {
            ci.cancel();
        }
    }*/
}
