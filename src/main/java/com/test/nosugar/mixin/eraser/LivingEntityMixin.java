package com.test.nosugar.mixin.eraser;

import com.test.nosugar.Config;
import com.test.nosugar.additional.ModDamageSources;
import com.test.nosugar.utils.ILivingEntity;
import com.test.nosugar.network.PacketHandler;
import com.test.nosugar.network.packets.EraseEntityPacket;
import com.test.nosugar.utils.EraseEntityLookupBridge;
import com.test.nosugar.utils.SynchedEntityDataUtil;
import com.test.nosugar.utils.TaskScheduler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.*;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntity {


    private static final Set<UUID> erasedUuids = ConcurrentHashMap.newKeySet();
    @Unique
    private boolean erased = false;
    @Unique
    private boolean Fullset = false;

    @Unique
    private static void hardRemove(Entity self, Map<Class<?>, List<Entity>> byClass) {
        Class<?> c = self.getClass();
        List<Entity> list = byClass.get(c);
        if (list != null) {
            list.remove(self);
            if (list.isEmpty()) byClass.remove(c);
        }
        for (Map.Entry<Class<?>, List<Entity>> e : byClass.entrySet()) {
            List<Entity> l = e.getValue();
            if (l != null && !l.isEmpty()) {
                l.remove(self);
                if (l.isEmpty()) byClass.remove(e.getKey());
            }
        }
    }

    @Unique
    public boolean hardRemove(Object entity, ClassInstanceMultiMap<Entity> map) {
        boolean flag = false;

        for(Map.Entry<Class<?>, List<Entity>> entry : ((ClassInstanceMultiMapAccessor<Entity>) map).getByClass().entrySet()) {
            if (entry.getKey().isInstance(entity)) {
                List<Entity> list = entry.getValue();
                flag |= list.remove(entity);
            }
        }

        return flag;
    }
    @Override
    public boolean isErased() {
        return this.erased;
    }

    @Override
    public void setErased(boolean flag) {
        this.erased = flag;
    }

    @Override
    public boolean wasFullset() {
        return this.Fullset;
    }

    @Override
    public void setwasFullset(boolean Fullset) {
        this.Fullset = Fullset;
    }

    @Override
    public void markErased(UUID uuid) {
        erasedUuids.add(uuid);
    }

    @Override
    public void unmarkErased(UUID uuid) {
        erasedUuids.remove(uuid);
    }

    @Override
    public boolean isErased(UUID uuid) {
        return erasedUuids.contains(uuid);
    }

    @Override
    public void instantKill(LivingEntity attacker, boolean SkipAnimation) {
        LivingEntity self = (LivingEntity) (Object) this;
        //self.setPose(Pose.DYING);
        //SynchedEntityDataUtil.forceSet(self.getEntityData(), EntityAccessor.getDataPoseId(), 0.0F);
        if (this.isErased() || self.level().isClientSide) return;
        DamageSource eraseSrc = ModDamageSources.erase(self, attacker);

        if (Config.isNormalDieEntity(self)) {

            self.setHealth(0);
            if(attacker instanceof Player player)((LivingEntityAccessor) self).setLastHurtByPlayer(player);
            ((LivingEntityAccessor) self).setLastHurtByMob(attacker);
            ((LivingEntityAccessor) self).setLastHurtByPlayerTime(1);
            self.getCombatTracker().recordDamage(eraseSrc, 0);
            //((LivingEntityAccessor) self).callDie(eraseSrc);
        } else if (Config.FORCE_DIE.get()) {
            EntityDataAccessor<Float> healthId = LivingEntityAccessor.getDataHealthId();
            //self.hurt(eraseSrc,Float.MAX_VALUE);
            SynchedEntityDataUtil.forceSet(self.getEntityData(), healthId, 0.0F);
            if(attacker instanceof Player player)((LivingEntityAccessor) self).setLastHurtByPlayer(player);
            ((LivingEntityAccessor) self).setLastHurtByMob(attacker);
            ((LivingEntityAccessor) self).setLastHurtByPlayerTime(1);
            self.getCombatTracker().recordDamage(eraseSrc, 0);
            markErased(self.getUUID());
            for (ServerPlayer sp : ((ServerLevel) self.level()).players()) {
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new EraseEntityPacket(self.getUUID(), SkipAnimation || Config.SKIP_DEATH_ANIMATION.get()));
            }
            this.setErased(true);
            forcedie(eraseSrc);
            if (self instanceof ServerPlayer) return;
            if (!SkipAnimation && !Config.SKIP_DEATH_ANIMATION.get()) {
                TaskScheduler.schedule(this::forceErase, 21);
            } else forceErase();
        }

    }

    private void forcedie(DamageSource source) {
        LivingEntity self = (LivingEntity) (Object) this;
        //if(!(self instanceof ServerPlayer)) { self.die(source);}
        ((LivingEntityAccessor) self).setDeadFlag(true);
        self.deathTime = 1;
        ((EntityAccessor) self).setRemovalReason(Entity.RemovalReason.KILLED);
        if (!self.level().isClientSide) {

           /* if (self instanceof ServerPlayer sp) {
                Component deathMsg = sp.getCombatTracker().getDeathMessage();
                sp.connection.send(new ClientboundPlayerCombatKillPacket(sp.getId(), deathMsg));
                if (self.isDeadOrDying()) sp.server.getPlayerList().broadcastSystemMessage(deathMsg, false);
            }*/
            LivingEntity killer = self.getKillCredit();
            if (killer != null) {
                /*if (self.getKillCredit() instanceof ServerPlayer player)
                    player.awardStat(Stats.ENTITY_KILLED_BY.get(killer.getType()));
                killer.awardKillScore(self, 0, source);*/
                DamageSource eraseSrc = ModDamageSources.erase(self, killer);
                ((LivingEntityAccessor) self).callDie(eraseSrc);
            }
            ((LivingEntityAccessor) self).invokeDropAllDeathLoot(source);
            //((LivingEntityAccessor)self).invokedropFromLootTable(source,false);
            //((LivingEntityAccessor)self).invokedropExperience();
        }
    }

    @Override
    public void instantKill() {
        instantKill(null, false);
    }

    @Unique
    void removeBossBar(ServerLevel serverLevel) {
        LivingEntity self = (LivingEntity) (Object) this;
        //markErased(self.getUUID());
        Class<?> clazz = self.getClass();
        for (int depth = 0; depth < 3 && clazz != null; depth++) {
            for (Field f : clazz.getDeclaredFields()) {
                if (ServerBossEvent.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    try {
                        ServerBossEvent event = (ServerBossEvent) f.get(self);
                        if (event == null) continue;

                        ClientboundBossEventPacket bossRemovePkt =
                                ClientboundBossEventPacket.createRemovePacket(event.getId());

                        for (ServerPlayer sp : serverLevel.players()) {
                            sp.connection.send(bossRemovePkt);
                        }
                        event.removeAllPlayers();

                    } catch (ReflectiveOperationException | ClassCastException ex) {
                        LOGGER.error("Failed to remove boss bar from {} (id={}, uuid={})",
                                self.getName().getString(), self.getId(), self.getUUID(), ex);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    @Override
    public void forceErase() {
        LivingEntity self = (LivingEntity) (Object) this;
        self.level().broadcastEntityEvent(self, (byte) 60);
        ((EntityAccessor) self).setRemovalReason(Entity.RemovalReason.KILLED);
        if (self.level() instanceof ServerLevel serverLevel) {
            removeBossBar(serverLevel);
            boolean debug = false;
            self.stopRiding();
            self.invalidateCaps();

            ((EntityAccessor) self).setlevelCallback(EntityInLevelCallback.NULL);
            EntityTickList tickList = ((ServerLevelAccessor) serverLevel).getEntityTickList();
            Int2ObjectMap<Entity> active = ((EntityTickListAccessor) tickList).getActive();
            active.remove(self.getId());

            PersistentEntitySectionManager<Entity> manager =
                    ((ServerLevelAccessor) serverLevel).getEntityManager();
            PersistentEntitySectionManagerAccessor<Entity> acc =
                    (PersistentEntitySectionManagerAccessor<Entity>) manager;

            EntityLookup<Entity> vis = acc.getVisibleEntityStorage();
            ((EraseEntityLookupBridge<Entity>) vis).eraseEntity(self);

            LevelEntityGetter<Entity> getter = acc.getEntityGetter();
            EntityLookup<Entity> vis2 = ((LevelEntityGetterAdapterAccessor<Entity>) getter).getVisibleEntities();
            ((EraseEntityLookupBridge<Entity>) vis2).eraseEntity(self);
            EntitySectionStorage<Entity> storage2 = ((LevelEntityGetterAdapterAccessor<Entity>) getter).getSectionStorage();
            long sectionKey = SectionPos.asLong(self.blockPosition());
            EntitySection<Entity> section2 = storage2.getSection(sectionKey);
            if (section2 != null) {
                ClassInstanceMultiMap<Entity> multiMap =
                        ((EntitySectionAccessor<Entity>) section2).getStorage();
                Map<Class<?>, List<Entity>> byClass = ((ClassInstanceMultiMapAccessor<Entity>) multiMap).getByClass();
                hardRemove(self, byClass);
                hardRemove(self, multiMap);
                if (debug)
                    System.out.println("[NoSugerMod] forceErase: removed entity id=" + self.getId() + " from LevelEntityGetter section storage");
            }

            acc.getKnownUuids().remove(self.getUUID());

            EntitySectionStorage<Entity> storage = acc.getSectionStorage();
            EntitySection<Entity> section = storage.getSection(sectionKey);
            if (section != null) {
                ((EntitySectionAccessor) section).getStorage().remove(self);
                ClassInstanceMultiMap<Entity> multiMap = ((EntitySectionAccessor<Entity>) section).getStorage();
                Map<Class<?>, List<Entity>> byClass = ((ClassInstanceMultiMapAccessor<Entity>) multiMap).getByClass();
                hardRemove(self, byClass);
                hardRemove(self, multiMap);
            }
            ChunkMap chunkMap = serverLevel.getChunkSource().chunkMap;
            Int2ObjectMap<?> entityMap = ((ChunkMapAccessor) chunkMap).getEntityMap();
            entityMap.remove(self.getId());
            if (self instanceof TrackedEntityAccessor accessor) {
                accessor.invokeBroadcastRemoved();
            }
            if (debug) {
                UUID originalUuid = self.getUUID();
                int id = self.getId();
                if (serverLevel.getEntity(originalUuid) != null
                        || (vis != null && vis.getEntity(originalUuid) != null)
                        || (vis2 != null && vis2.getEntity(originalUuid) != null)
                        || (acc != null && acc.getKnownUuids().contains(originalUuid))) {

                    System.out.println("[EraserMod] failed to fully remove entity id=" + id + " uuid=" + originalUuid);

                    if (serverLevel.getEntity(originalUuid) != null) {
                        System.out.println("[EraserMod]  - still in ServerLevel.getEntity(UUID)");
                    }
                    if (vis != null && vis.getEntity(originalUuid) != null) {
                        System.out.println("[EraserMod]  - still in visibleEntityStorage (acc.getVisibleEntityStorage())");
                    }
                    if (vis2 != null && vis2.getEntity(originalUuid) != null) {
                        System.out.println("[EraserMod]  - still in getter.visibleEntities (LevelEntityGetter adapter)");
                    }
                    if (acc != null && acc.getKnownUuids().contains(originalUuid)) {
                        System.out.println("[EraserMod]  - still in PersistentEntitySectionManager.knownUuids");
                    }

                    ChunkMap debugchunkMap = serverLevel.getChunkSource().chunkMap;
                    if (((ChunkMapAccessor) debugchunkMap).getEntityMap().containsKey(self.getId())) {
                        System.out.println("[EraserMod]  - still in ChunkMap.entityMap");
                    }


                    if (storage2 != null) {
                        SectionPos sp = SectionPos.of(self);
                        EntitySection<Entity> s2 = storage2.getSection(sp.asLong());
                        if (s2 != null && ((EntitySectionAccessor<?>) s2).getStorage().contains(self)) {
                            System.out.println("[EraserMod]  - still in LevelEntityGetter.sectionStorage section");
                        }
                    }


                    if (storage != null) {
                        SectionPos sp = SectionPos.of(self);
                        EntitySection<Entity> s = storage.getSection(sp.asLong());
                        if (s != null && ((EntitySectionAccessor<?>) s).getStorage().contains(self)) {
                            System.out.println("[EraserMod]  - still in PersistentEntitySectionManager.sectionStorage section");
                        }
                    }


                } else {
                    System.out.println("[EraserMod] successfully removed entity id=" + id + " uuid=" + originalUuid);
                }
            }
        }

    }

    @Inject(method = "getHealth", at = @At("RETURN"), cancellable = true)
    private void overrideGetHealth(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (this.isErased()) {
            cir.setReturnValue(0.0F);
        }
    }

    @Inject(method = "getMaxHealth", at = @At("RETURN"), cancellable = true)
    private void overridegetMaxHealth(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (this.isErased()) {
            cir.setReturnValue(0F);
        }
    }

    @Inject(method = "isAlive", at = @At("RETURN"), cancellable = true)
    private void eraser$isAlive(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (this.isErased()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isDeadOrDying", at = @At("RETURN"), cancellable = true)
    private void eraser$isDeadOrDying(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (this.isErased()) {
            cir.setReturnValue(true);
        }
    }


    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void eraser$die(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (this.isErased()) {
            //ci.cancel();
        }
    }

    /*@Inject(method = "baseTick", at = @At("HEAD"), cancellable = true)
    private void eraser$baseTick(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (this.isErased()) {
            ci.cancel();
        }
    }*/

    /*@Inject(method = "tickDeath", at = @At("HEAD"), cancellable = true)
    private void eraser$tickDeath(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (this.isErased()) {
            ci.cancel();
        }
    }*/
}


