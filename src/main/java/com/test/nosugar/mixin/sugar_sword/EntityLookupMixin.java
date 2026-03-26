package com.test.nosugar.mixin.sugar_sword;

import com.test.nosugar.NoSugar;
import com.test.nosugar.utils.interfaces.EraseEntityLookupBridge;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Mixin(EntityLookup.class)
public abstract class EntityLookupMixin<T extends EntityAccess> implements EraseEntityLookupBridge<T>, EntityLookupAccessor<T> {

    @Unique
    @Override
    public boolean eraseEntity(T entity) {
        if (entity == null) return false;
        UUID uuid = entity.getUUID();
        int id = entity.getId();
        Int2ObjectMap<T> idMap = this.getById();
        Map<UUID, T> uuidMap = this.getByUuid();
        NoSugar.LOGGER.info("Lookup Map Sizes: ID-Map={}, UUID-Map={}",
                idMap != null ? idMap.size() : "NULL",
                uuidMap != null ? uuidMap.size() : "NULL");
        Int2ObjectMap<T> vanillaById = this.getById();
        Map<UUID, T> vanillaByUuid = this.getByUuid();

        if (vanillaByUuid != null) vanillaByUuid.remove(uuid);
        if (vanillaById != null) vanillaById.remove(id);

        this.setByUuid(vanillaByUuid);
        this.setById(vanillaById);

        return true;
    }
}