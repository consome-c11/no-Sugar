package com.test.nosugar.mixin.eraser;

import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public interface TrackedEntityAccessor {
    @Accessor("entity")
    Entity getEntity();

    @Accessor("seenBy")
    Set<ServerPlayerConnection> getSeenBy();

    @Invoker("broadcastRemoved")
    void invokeBroadcastRemoved();
}