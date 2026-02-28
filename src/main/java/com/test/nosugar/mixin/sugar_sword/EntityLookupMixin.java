package com.test.nosugar.mixin.sugar_sword;

import com.google.common.collect.Maps;
import com.test.nosugar.utils.interfaces.EraseEntityLookupBridge;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(value = EntityLookup.class, priority = 114514)
public abstract class EntityLookupMixin<T extends EntityAccess>
        implements EraseEntityLookupBridge<T>, EntityLookupAccessor<T> {

    @Unique
    @Override
    public boolean eraseEntity(T entity) {
        if (entity == null) return false;
        UUID uuid = entity.getUUID();
        int id = entity.getId();

        boolean removed = false;

        Int2ObjectMap<T> vanillaById = this.getById();
        Map<UUID, T> vanillaByUuid = this.getByUuid();
        if (vanillaByUuid != null && vanillaByUuid.remove(uuid) != null) {
            this.setByUuid(vanillaByUuid);
            removed = true;
        }
        if (vanillaById != null && vanillaById.remove(id) != null) {
            this.setById(vanillaById);
            removed = true;
        }
        return removed;
    }

}
