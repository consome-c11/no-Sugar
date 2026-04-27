package com.test.nosugar.mixin.client;

import com.test.nosugar.NoSugar;
import com.test.nosugar.mixin.sugar_sword.ClassInstanceMultiMapAccessor;
import com.test.nosugar.mixin.sugar_sword.EntityAccessor;
import com.test.nosugar.mixin.sugar_sword.EntitySectionAccessor;
import com.test.nosugar.mixin.sugar_sword.LevelEntityGetterAdapterAccessor;
import com.test.nosugar.network.PacketHandler;
import com.test.nosugar.network.packets.HandleErasePacket;
import com.test.nosugar.utils.interfaces.EraseEntityLookupBridge;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.SectionPos;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@OnlyIn(Dist.CLIENT)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntity {

    @Override
    public void eraseClientEntity() {
        try {
            LivingEntity self = (LivingEntity) (Object) this;
            Minecraft mc = Minecraft.getInstance();
            if (self == mc.player || mc.level == null) return;

            self.setPose(Pose.DYING);
            self.onClientRemoval();
            ((EntityAccessor) self).setlevelCallback(EntityInLevelCallback.NULL);
            TransientEntitySectionManager<Entity> tManager =
                    ((ClientLevelAccessor) mc.level).getTransientEntityManager();
            TransientEntitySectionManagerAccessor<Entity> accessor = (TransientEntitySectionManagerAccessor<Entity>) tManager;

            long sectionKey = SectionPos.asLong(self.blockPosition());
            EntitySection<Entity> section = accessor.getSectionStorage().getSection(sectionKey);

            if (section != null) {
                ClassInstanceMultiMap<Entity> multiMap =
                        ((EntitySectionAccessor<Entity>) section).getStorage();
                Map<Class<?>, List<Entity>> byClass = ((ClassInstanceMultiMapAccessor<Entity>) multiMap).getByClass();
                if (byClass != null) hardRemove(self, byClass);
            }

            LevelCallback<Entity> callbacks = accessor.getCallbacks();
            callbacks.onTickingEnd(self);
            callbacks.onTrackingEnd(self);
            callbacks.onDestroyed(self);

            EntityLookup<Entity> lookup = accessor.getEntityStorage();
            ((EraseEntityLookupBridge<Entity>) lookup).eraseEntity(self);

            mc.level.removeEntity(self.getId(), Entity.RemovalReason.KILLED);
            self.setRemoved(Entity.RemovalReason.KILLED);

        }
        catch (Throwable throwable) {
            NoSugar.LOGGER.warn("An error occurred while trying to erase the entity", throwable);
        }
    }
    @Unique
    private static void hardRemove(Entity self, Map<Class<?>, List<Entity>> byClass) {//サンキューチャッピー
        Class<?> c = self.getClass();
        List<Entity> list = byClass.get(c);
        if (list != null) {
            list.remove(self);
            if (list.isEmpty()) {
                byClass.remove(c);
            }
        }
        List<Class<?>> keysToRemove = new java.util.ArrayList<>();

        for (Map.Entry<Class<?>, List<Entity>> e : byClass.entrySet()) {
            List<Entity> l = e.getValue();
            if (l != null && !l.isEmpty()) {
                l.remove(self);
                if (l.isEmpty()) {
                    keysToRemove.add(e.getKey());
                }
            }
        }

        for (Class<?> key : keysToRemove) {
            byClass.remove(key);
        }
    }
    /*@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void eraser$shrinkAABBOnTick(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (this.isErased()) {
            //self.setBoundingBox(new AABB(self.getX(), self.getY(), self.getZ(), self.getX(), self.getY(), self.getZ()));
            ci.cancel();
            self.deathTime ++;
        }
    }*/
}