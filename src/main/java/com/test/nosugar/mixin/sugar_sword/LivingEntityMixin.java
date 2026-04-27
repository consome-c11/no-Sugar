package com.test.nosugar.mixin.sugar_sword;

import com.test.nosugar.Config;
import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.ModDamageSources;
import com.test.nosugar.network.PacketHandler;
import com.test.nosugar.network.packets.EraseEntityPacket;
import com.test.nosugar.network.packets.SyncDeltaPacket;
import com.test.nosugar.utils.SynchedEntityDataUtil;
import com.test.nosugar.utils.TaskScheduler;
import com.test.nosugar.utils.entity.EntityUtils;
import com.test.nosugar.utils.entity.LivingEntityUtils;
import com.test.nosugar.utils.interfaces.EraseEntityLookupBridge;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = LivingEntity.class, priority = 0)
public abstract class LivingEntityMixin implements ILivingEntity {

    private static final Set<UUID> erasedUuids = ConcurrentHashMap.newKeySet();
    @Unique
    private boolean erased = false;
    @Unique
    private boolean Fullset = false;
    @Unique
    private float delta = 0;
    @Unique
    private boolean forcefullset = false;
    @Unique
    private boolean forcehalo = false;

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

    @Override
    public boolean isErased() {
        LivingEntity self = (LivingEntity) (Object) this;
        return this.erased || erasedUuids.contains(self.getUUID());
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
    public float getDelta() {
        return delta;
    }


    @Override
    public void setDelta(float d) {
        LivingEntity self = (LivingEntity) (Object) this;
        if(delta != d && !self.level().isClientSide)
            for (ServerPlayer sp : ((ServerLevel) self.level()).players()) {
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new SyncDeltaPacket(self.getId(), d));
            }
        delta = d;
    }

    @Override
    public boolean isForceHalo() {
        return this.forcehalo;
    }

    @Override
    public void setForceHalo(boolean flag) {
        this.forcehalo = flag;
    }

    @Override
    public boolean isForceFullset() {
        return this.forcefullset;
    }

    @Override
    public void setForceFullset(boolean flag) {
        this.forcefullset = flag;
    }

    @Override
    public void instantKill(LivingEntity attacker, boolean SkipAnimation, DamageSource src) {
        LivingEntity self = (LivingEntity) (Object) this;
        if(EntityUtils.hasHaloOfSugar(self)) {
            setDelta(getDelta() + 1.f);
            if(self.getHealth() > 0.f)return;
        }
        self.setPose(Pose.DYING);
        //SynchedEntityDataUtil.forceSet(self.getEntityData(), EntityAccessor.getDataPoseId(), 0.0F);
        if (this.isErased() || self.level().isClientSide) return;
        EntityDataAccessor<Float> healthId = LivingEntityAccessor.getDataHealthId();
        self.getCombatTracker().recordDamage(src, Float.POSITIVE_INFINITY);
        if (attacker instanceof Player player) ((LivingEntityAccessor) self).setLastHurtByPlayer(player);
        if (attacker != null) ((LivingEntityAccessor) self).setLastHurtByMob(attacker);
        ((LivingEntityAccessor) self).setLastHurtByPlayerTime(1);//0以上かで判断してるし1でもええやろ(フラグ)
        if (Config.isNormalDieEntity(self) || self instanceof Player) {

            self.getEntityData().set(healthId, 0.f, true);
            if (self instanceof ServerPlayer player) {
                this.setErased(true);
                SynchedEntityDataUtil.forceSet(self.getEntityData(), healthId, 0.f);
                forcedie(src);
                for (ServerPlayer sp : ((ServerLevel) self.level()).players()) {
                    PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new EraseEntityPacket(self.getUUID(), SkipAnimation || Config.SKIP_DEATH_ANIMATION.get()));
                }
            }
            self.hurt(src, Float.MAX_VALUE);

            //((LivingEntityAccessor) self).callDie(eraseSrc);
        } else if (Config.FORCE_DIE.get()) {
            this.setErased(true);
            SynchedEntityDataUtil.forceSet(self.getEntityData(), healthId, 0.f);
            ServerBossEvent event = getBossBar((ServerLevel) self.level());
            if (event != null) event.setProgress(0.f);
            markErased(self.getUUID());
            for (ServerPlayer sp : ((ServerLevel) self.level()).players()) {
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new EraseEntityPacket(self.getUUID(), SkipAnimation || Config.SKIP_DEATH_ANIMATION.get()));
            }
            if(self instanceof TamableAnimal animal && animal.isTame()) self.die(src);
            forcedie(src);
            self.level().broadcastEntityEvent(self, (byte) 60);
            self.level().broadcastEntityEvent(self, (byte) 60);
            //MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(self, src, Float.POSITIVE_INFINITY));
            MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(self, src));
            self.playSound(((LivingEntityAccessor)self).invokegetDeathSound());
            self.playSound(SoundEvents.PLAYER_ATTACK_STRONG, ((LivingEntityAccessor)self).invokegetSoundVolume(), self.getVoicePitch());
            if (!SkipAnimation && !Config.SKIP_DEATH_ANIMATION.get()) {
                TaskScheduler.schedule(this::forceErase, 21);
            } else forceErase();
        }

    }

    @Unique
    void unMarkErased() {
        LivingEntity self = (LivingEntity) (Object) this;
        this.unmarkErased(self.getUUID());
    }

    @Unique
    private void forcedie(DamageSource source) {
        LivingEntity self = (LivingEntity) (Object) this;
        //if(!(self instanceof ServerPlayer)) { self.die(source);}
        ((LivingEntityAccessor) self).setDeadFlag(true);
        self.deathTime = 1;

        if (!self.level().isClientSide) {

            if (self instanceof ServerPlayer sp) {
                Component deathMsg = sp.getCombatTracker().getDeathMessage();
                //チェック無いと夢幻終焉とかでMixinが上書きされてる時に酷い事になる :(
                if (self.isDeadOrDying() && !self.isAlive() && self.getHealth() <= 0.f) {
                    sp.connection.send(new ClientboundPlayerCombatKillPacket(sp.getId(), deathMsg));
                    //sp.server.getPlayerList().broadcastSystemMessage(deathMsg, false);
                }
                //((LivingEntityAccessor) self).callDie(source);
                sp.die(source);
            }

            LivingEntity killer = self.getKillCredit();
            if (killer != null) {
                if (self instanceof ServerPlayer player)
                    player.awardStat(Stats.ENTITY_KILLED_BY.get(killer.getType()));
                killer.awardKillScore(self, 0, source);
            }
            if (!LivingEntityUtils.isAlive(self) && LivingEntityUtils.isDeadOrDying(self)) {
                self.setPose(Pose.DYING);
                ((LivingEntityAccessor) self).invokeDropAllDeathLoot(source);
            }
            //((LivingEntityAccessor)self).invokedropFromLootTable(source,false);
            //((LivingEntityAccessor)self).invokedropExperience();
        }
    }

    @Override
    public void instantKill() {
        LivingEntity self = (LivingEntity) (Object) this;
        instantKill(null, false, ModDamageSources.erase(self, null));
    }

    @Override
    public void instantKill(DamageSource source) {
        LivingEntity self = (LivingEntity) (Object) this;
        instantKill(null, false, source);
    }

    @Unique
    ServerBossEvent getBossBar(ServerLevel serverLevel) {
        LivingEntity self = (LivingEntity) (Object) this;
        Class<?> clazz = self.getClass();
        for (int depth = 0; depth < 3 && clazz != null; depth++) {
            for (Field f : clazz.getDeclaredFields()) {
                if (ServerBossEvent.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    try {
                        ServerBossEvent event = (ServerBossEvent) f.get(self);
                        if (event == null) continue;
                        return event;

                    } catch (ReflectiveOperationException | ClassCastException ex) {
                        NoSugar.LOGGER.error("Failed to get boss bar from {} (id={}, uuid={})",
                                self.getName().getString(), self.getId(), self.getUUID(), ex);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    @Override
    public void forceErase() {
        try {
            LivingEntity self = (LivingEntity) (Object) this;
            //フィールド書き換えてるだけ
            ((EntityAccessor) self).setRemovalReason(Entity.RemovalReason.KILLED);

            if (self.level() instanceof ServerLevel serverLevel) {
                ServerBossEvent event = getBossBar(serverLevel);
                if (event != null) event.removeAllPlayers();
                self.stopRiding();
                self.invalidateCaps();

                EntityTickList tickList = ((ServerLevelAccessor) serverLevel).getEntityTickList();

                if (((EntityTickListAccessor) tickList).getIterated() == ((EntityTickListAccessor) tickList).getActive()) {
                    ((EntityTickListAccessor) tickList).getPassive().clear();

                    for (Int2ObjectMap.Entry<Entity> entry : Int2ObjectMaps.fastIterable(((EntityTickListAccessor) tickList).getActive())) {
                        ((EntityTickListAccessor) tickList).getPassive().put(entry.getIntKey(), entry.getValue());
                    }

                    Int2ObjectMap<Entity> int2objectmap = ((EntityTickListAccessor) tickList).getActive();
                    ((EntityTickListAccessor) tickList).setActive(((EntityTickListAccessor) tickList).getPassive());
                    ((EntityTickListAccessor) tickList).setPassive(int2objectmap);
                }

                Int2ObjectMap<Entity> active = ((EntityTickListAccessor) tickList).getActive();
                active.remove(self.getId());
                Int2ObjectMap<Entity> passive = ((EntityTickListAccessor) tickList).getPassive();
                passive.remove(self.getId());
                Int2ObjectMap<Entity> iterated = ((EntityTickListAccessor) tickList).getIterated();
                if (iterated != null)
                    iterated.remove(self.getId());
                ((EntityAccessor) ((Entity) self)).isAddedToWorld(false);
                removefromSectionManager(serverLevel);
                PersistentEntitySectionManager<Entity> manager =
                        ((ServerLevelAccessor) serverLevel).getEntityManager();
                PersistentEntitySectionManagerAccessor<Entity> acc =
                        (PersistentEntitySectionManagerAccessor<Entity>) manager;

                EntityLookup<Entity> vis = acc.getVisibleEntityStorage();

                ChunkMap chunkMap = serverLevel.getChunkSource().chunkMap;
                Int2ObjectMap<?> entityMap = ((ChunkMapAccessor) chunkMap).getEntityMap();
                entityMap.remove(self.getId());
                if (self instanceof TrackedEntityAccessor accessor) {
                    accessor.invokeBroadcastRemoved();
                }
                ((EntityAccessor) self).setlevelCallback(EntityInLevelCallback.NULL);
            }

        }
        catch (Throwable throwable) {
            NoSugar.LOGGER.warn("An error occurred while trying to erase the entity", throwable);
        }
    }

    @Unique
    public void removefromSectionManager(ServerLevel serverLevel) {
        LivingEntity self = (LivingEntity) (Object) this;
        PersistentEntitySectionManager<Entity> manager =
                ((ServerLevelAccessor) serverLevel).getEntityManager();
        PersistentEntitySectionManagerAccessor<Entity> acc =
                (PersistentEntitySectionManagerAccessor<Entity>) manager;

        EntityLookup<Entity> vis = acc.getVisibleEntityStorage();
        boolean removed = ((EraseEntityLookupBridge<Entity>) vis).eraseEntity(self);
        //NoSugar.LOGGER.info("Lookup Removal: {}", removed);
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
            if (byClass != null) hardRemove(self, byClass);
        }

        acc.getKnownUuids().remove(self.getUUID());

        EntitySectionStorage<Entity> storage = acc.getSectionStorage();
        EntitySection<Entity> section = storage.getSection(sectionKey);
        if (section != null) {
            //((EntitySectionAccessor) section).getStorage().remove(self);
            ClassInstanceMultiMap<Entity> multiMap = ((EntitySectionAccessor<Entity>) section).getStorage();
            Map<Class<?>, List<Entity>> byClass = ((ClassInstanceMultiMapAccessor<Entity>) multiMap).getByClass();
            if (byClass != null) hardRemove(self, byClass);
        }
        Object callback = ((EntityAccessor) self).getLevelCallBack();
        if (callback instanceof PersistentEntitySectionManagerCallbackAccessor cbAcc) {
            //NoSugar.LOGGER.info("what the hell???");
            EntitySection<Entity> section3 = cbAcc.getCurrentSection();

            if (section3 != null) {
                ClassInstanceMultiMap<Entity> multiMap = ((EntitySectionAccessor<Entity>) section3).getStorage();
                Map<Class<?>, List<Entity>> byClass = ((ClassInstanceMultiMapAccessor<Entity>) multiMap).getByClass();

                if (byClass != null) {
                    hardRemove(self, byClass);
                }
                if (section3.isEmpty()) {
                    acc.getSectionStorage().remove(cbAcc.getCurrentSectionKey());
                }
            }
        }
        /*else if(callback != null){
            NoSugar.LOGGER.info("Class: " + callback.getClass().getName());
        }
        else NoSugar.LOGGER.info(":sob:");*/
        //((EntityAccessor) self).getLevelCallBack().onRemove(Entity.RemovalReason.KILLED);
        /*acc.getCallbacks().onTickingEnd(self);
        acc.getCallbacks().onTrackingEnd(self);
        acc.getCallbacks().onDestroyed(self);*/
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void nosugar$baseTickDeath(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if(self.level().isClientSide()) {
            if (this.isErased() && self.deathTime <= 20) {
                self.deathTime++;
            }
        }
    }

    @Inject(method = "tickDeath", at = @At("HEAD"), cancellable = true)
    private void nosugar$cancelNormalTickDeath(CallbackInfo ci) {
        if (this.isErased()) {
            ci.cancel();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void nosugar$writeNSData(CompoundTag nbt, CallbackInfo ci) {
        nbt.putFloat("nosugar_delta", this.delta);
        nbt.putBoolean("nosugar_fullset", this.Fullset);
        nbt.putBoolean("nosugar_forcefullset", this.forcefullset);
        nbt.putBoolean("nosugar_forcehalo", this.forcehalo);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void nosugar$readNSData(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("nosugar_delta")) {
            this.delta = nbt.getFloat("nosugar_delta");

            LivingEntity entity = (LivingEntity)(Object)this;
            Level level = entity.level();

            if (level instanceof ServerLevel serverLevel) {
                SyncDeltaPacket packet = new SyncDeltaPacket(entity.getId(), this.delta);

                for (ServerPlayer player : serverLevel.players()) {
                    PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
                }
            }
        }
        if (nbt.contains("nosugar_fullset")) {
            this.Fullset = nbt.getBoolean("nosugar_fullset");
        }
        if (nbt.contains("nosugar_forcefullset")) {
            this.forcefullset = nbt.getBoolean("nosugar_forcefullset");
        }
        if (nbt.contains("nosugar_forcehalo")) {
            this.forcehalo = nbt.getBoolean("nosugar_forcehalo");
        }
    }
    /*@Inject(method = "getHealth", at = @At("HEAD"), cancellable = true)
    private void nosugar$getHealth(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof ILivingEntity iLiving && (iLiving.isErased(self.getUUID()) || iLiving.isErased())) {
            cir.setReturnValue(0.0F);
            cir.cancel();
        }
    }

    @Inject(method = "getMaxHealth", at = @At("HEAD"), cancellable = true)
    private void nosugar$getMaxHealth(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof ILivingEntity iLiving && iLiving.isErased()) {
            cir.setReturnValue(0F);
        }
    }

    @Inject(method = "isAlive", at = @At("HEAD"), cancellable = true)
    private void nosugar$isAlive(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof ILivingEntity iLiving && (iLiving.isErased(self.getUUID()) || iLiving.isErased())) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "isDeadOrDying", at = @At("HEAD"), cancellable = true)
    private void nosugar$isDeadOrDying(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof ILivingEntity iLiving && (iLiving.isErased(self.getUUID()) || iLiving.isErased())) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }*/

}