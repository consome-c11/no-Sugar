package com.test.nosugar.utils.interfaces;

import net.minecraft.world.level.entity.EntityAccess;
import org.spongepowered.asm.mixin.Unique;

public interface EraseEntityLookupBridge<T extends EntityAccess> {

    @Unique
    boolean eraseEntity(T entity);
}
