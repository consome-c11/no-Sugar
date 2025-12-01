package com.test.nosugar.utils;

import com.test.nosugar.mixin.sugar_sword.SynchedEntityDataAccessor;
import com.test.nosugar.mixin.sugar_sword.SynchedEntityDataItemAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;

import java.util.Objects;

public class SynchedEntityDataUtil {
    public static <T> void forceSet(SynchedEntityData data, EntityDataAccessor<T> accessor, T value) {
        SynchedEntityDataAccessor acc = (SynchedEntityDataAccessor) data;
        SynchedEntityData.DataItem<T> item = acc.invokeGetItem(accessor);

        if (!Objects.equals(item.getValue(), value)) {
            ((SynchedEntityDataItemAccessor)item).setValue(Float.MIN_VALUE);
            //acc.getEntity().onSyncedDataUpdated(accessor);
            ((SynchedEntityDataItemAccessor)item).setDirty(true);
            acc.setDirtyFlag(true);
        }
    }
}

