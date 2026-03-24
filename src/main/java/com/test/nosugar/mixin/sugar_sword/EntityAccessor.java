package com.test.nosugar.mixin.sugar_sword;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Accessor("DATA_POSE")
    static EntityDataAccessor<Float> getDataPoseId() {
        throw new AssertionError();
    }

    @Accessor("removalReason")
    Entity.RemovalReason getRemovalReason();

    @Accessor("removalReason")
    void setRemovalReason(Entity.RemovalReason reason);

    @Accessor("levelCallback")
    EntityInLevelCallback getLevelCallBack();

    @Accessor("levelCallback")
    void setlevelCallback(EntityInLevelCallback callback);

    @Accessor(value = "isAddedToWorld", remap = false)
    void isAddedToWorld(boolean flag);
}
