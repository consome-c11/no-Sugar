package com.test.nosugar.mixin.sugar_sword;

import com.test.nosugar.NoSugar;
import com.test.nosugar.utils.Mapping;
import com.test.nosugar.utils.UnsafeUtils;
import com.test.nosugar.utils.interfaces.EraseEntityLookupBridge;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mixin(EntityLookup.class)
public abstract class EntityLookupMixin<T extends EntityAccess> implements EraseEntityLookupBridge<T> {

    @Unique private static final long BY_ID_OFFSET = UnsafeUtils.getFieldOffset(EntityLookup.class, "byId", Mapping.BY_ID);
    @Unique private static final long BY_UUID_OFFSET = UnsafeUtils.getFieldOffset(EntityLookup.class, "byUuid", Mapping.BY_UUID);

    @Unique
    @Override
    public boolean eraseEntity(T entity) {
        if (entity == null) {
            return false;
        }

        UUID targetUuid = entity.getUUID();
        int targetId = entity.getId();
        Map<UUID, T> currentUuidMap = ((EntityLookupAccessor)this).getByUuid();
        Int2ObjectMap<T> currentIdMap = ((EntityLookupAccessor)this).getById();
        if(UnsafeUtils.SUCCESS) {
            currentUuidMap = (Map<UUID, T>) UnsafeUtils.getObject(this, BY_UUID_OFFSET);
            currentIdMap = (Int2ObjectMap<T>) UnsafeUtils.getObject(this, BY_ID_OFFSET);
        }

        if (currentUuidMap == null || currentIdMap == null) return false;

        Map<UUID, T> nextUuidMap = new HashMap<>(currentUuidMap);
        nextUuidMap.remove(targetUuid);

        Int2ObjectMap<T> nextIdMap = new Int2ObjectLinkedOpenHashMap<>();
        currentIdMap.int2ObjectEntrySet().forEach(e -> {
            if (e.getIntKey() != targetId) {
                nextIdMap.put(e.getIntKey(), e.getValue());
            }
        });

        if(UnsafeUtils.SUCCESS) {
            UnsafeUtils.setField(this, BY_UUID_OFFSET, nextUuidMap);
            UnsafeUtils.setField(this, BY_ID_OFFSET, nextIdMap);
        }
        else {
            ((EntityLookupAccessor) this).setById(nextIdMap);
            ((EntityLookupAccessor) this).setByUuid(nextUuidMap);
        }
        return true;
    }
}