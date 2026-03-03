package com.test.nosugar.entity;

import com.mojang.authlib.GameProfile;
import com.test.nosugar.network.PacketHandler;
import com.test.nosugar.network.packets.SyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

public class Sand_Bag_v2 extends FakePlayer {
    private static final EntityDataAccessor<Float> DATA_HEALTH_ID =
            SynchedEntityData.defineId(Sand_Bag_v2.class, EntityDataSerializers.FLOAT);

    public Sand_Bag_v2(ServerLevel level, BlockPos pos) {
        super(level, new GameProfile(UUID.randomUUID(), "SandBag"));
        this.setPos(pos.getX(), pos.getY(), pos.getZ());

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100.0);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10.0);

        this.setHealth(100.0f);
        this.setCustomNameVisible(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HEALTH_ID, 100.0f);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Health")) {
            this.setHealth(tag.getFloat("Health"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Health", this.getHealth());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    public int getClientEntityId() {
        return this.getId() + 10000000;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.tickCount % 2 == 0) {
            sendSyncPacket();
        }
    }

    private void sendSyncPacket() {
        if (this.level() instanceof ServerLevel serverLevel) {

            PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(),
                    new SyncPacket(this.getClientEntityId(),
                            this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot(),
                            this.tickCount % 20 < 10));
        }
    }
}