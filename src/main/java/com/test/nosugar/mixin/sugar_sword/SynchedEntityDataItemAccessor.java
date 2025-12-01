package com.test.nosugar.mixin.sugar_sword;

import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SynchedEntityData.DataItem.class)
public interface SynchedEntityDataItemAccessor {
    @Accessor("value")
    Object getValue();

    @Accessor("value")
    void setValue(Object value);

    @Accessor("dirty")
    boolean isDirty();

    @Accessor("dirty")
    void setDirty(boolean value);
}
