package com.test.nosugar.mixin.eraser;

import net.minecraft.util.ClassInstanceMultiMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(ClassInstanceMultiMap.class)
public interface ClassInstanceMultiMapAccessor<T> {
    @Accessor("byClass")
    Map<Class<?>, List<T>> getByClass();
}
