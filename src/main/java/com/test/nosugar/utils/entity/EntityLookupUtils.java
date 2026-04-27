package com.test.nosugar.utils.entity;

import com.test.nosugar.mixin.sugar_sword.EntityLookupAccessor;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.LevelEntityGetterAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class EntityLookupUtils {

    public static <T extends EntityAccess> Collection<T> getvEntities(EntityLookup<T> lookup) {
        return ((EntityLookupAccessor<T>) lookup).getById().values();
    }

    public static <T extends EntityAccess> T getvEntity(EntityLookup<T> lookup, int id) {
        return ((EntityLookupAccessor<T>) lookup).getById().get(id);
    }

    public static <T extends EntityAccess> T getvEntity(EntityLookup<T> lookup, UUID uuid) {
        return ((EntityLookupAccessor<T>) lookup).getById().get(uuid);
    }

    public static <T extends EntityAccess> int getvCount(EntityLookup<T> lookup) {
        return ((EntityLookupAccessor<T>) lookup).getById().size();
    }

    public static <T extends EntityAccess> EntityLookup<T> createnewLookup(EntityLookup<T> original) {
        EntityLookup<T> newLookup = new EntityLookup<>();
        for (T entity : original.getAllEntities()) {
            newLookup.add(entity);
        }
        return newLookup;
    }
}
