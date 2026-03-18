package com.test.nosugar.mixin.stop_watch;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("xo")
    double getXo();
    @Accessor("xo")
    void setXo(double value);

    @Accessor("yo")
    double getYo();
    @Accessor("yo")
    void setYo(double value);

    @Accessor("zo")
    double getZo();
    @Accessor("zo")
    void setZo(double value);

    @Accessor("xRotO")
    float getXRotO();
    @Accessor("xRotO")
    void setXRotO(float value);

    @Accessor("yRotO")
    float getYRotO();
    @Accessor("yRotO")
    void setYRotO(float value);

}