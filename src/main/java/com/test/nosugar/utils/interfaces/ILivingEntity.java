package com.test.nosugar.utils.interfaces;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public interface ILivingEntity {
    boolean isErased();

    void setErased(boolean erased);

    void instantKill(LivingEntity attacker, boolean SkipAnimation, DamageSource src);

    void instantKill();

    void instantKill(DamageSource source);

    void forceErase();

    boolean wasFullset();//SnackProtector

    void setwasFullset(boolean fullset);

    void markErased(UUID uuid);

    void unmarkErased(UUID uuid);

    boolean isErased(UUID uuid);

    // "client.ClientLevelAccessor",
    void eraseClientEntity();

    int getDeathCount();

    void setDeathCount(int count);

    long getLastDeathTime();

    void setLastDeathTime(long time);

    float getDelta();

    void setDelta(float delta);
}
