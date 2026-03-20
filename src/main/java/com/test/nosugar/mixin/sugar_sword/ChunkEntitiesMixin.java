package com.test.nosugar.mixin.sugar_sword;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.test.nosugar.utils.interfaces.IChunkEntities;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.ChunkEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(ChunkEntities.class)
public class ChunkEntitiesMixin implements IChunkEntities {
    @WrapOperation(
            method = "getEntities()Ljava/util/stream/Stream;",
            at = @At(value = "INVOKE", target = "Ljava/util/List;stream()Ljava/util/stream/Stream;"))
    private <E> Stream<E> onListStream(List instance, Operation<Stream> original) {
        Stream<E> originalStream = original.call(instance);
        return originalStream.filter(element -> {
            if (element instanceof Entity entity) {
                        if(entity instanceof ILivingEntity erase && erase.isErased() && !(entity instanceof Player))
                    return false;
            }
            return true;
        });
    }

    @Unique
    @Override
    public void QueueRemove(Object entity) {
        //((ChunkEntitiesAccessor)this).getEntities().remove(entity);
    }
}
