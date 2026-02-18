package com.test.nosugar.mixin.client;

import com.test.nosugar.events.ClientEvents;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import com.test.nosugar.mixin.sugar_sword.EntityAccessor;
import com.test.nosugar.network.PacketHandler;
import com.test.nosugar.network.packets.HandleErasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;

@OnlyIn(Dist.CLIENT)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntity {
    @Override
    public void eraseClientEntity() {
        LivingEntity self = (LivingEntity) (Object) this;
        Minecraft mc = Minecraft.getInstance();
        if(self == mc.player) return;
        if (self instanceof Player) {
            PacketHandler.CHANNEL.sendToServer(new HandleErasePacket());
            ((ILivingEntity) self).setErased(false);
            ((ILivingEntity) self).unmarkErased(self.getUUID());
            return;
        }

        ClientLevel clientLevel = mc.level;
        self.setPose(Pose.DYING);
        //self.deathTime = 1;
        /*TransientEntitySectionManager<Entity> tManager = ((ClientLevelAccessor) clientLevel).getTransientEntityManager();
        self.onClientRemoval();*/

        ((EntityAccessor) (self)).setRemovalReason(Entity.RemovalReason.KILLED);
        //removeFromOtherIndexes(self.getUUID(), clientLevel, tManager);
        clientLevel.removeEntity(self.getId(), Entity.RemovalReason.KILLED);
        self.remove(Entity.RemovalReason.KILLED);
        //self.invalidateCaps();
        Entity e = clientLevel.getEntity(self.getId());
        /*List<Entity> snapshot = StreamSupport.stream(((LevelEntityGetterAdapterAccessor<Entity>) tManager.getEntityGetter()).getVisibleEntities().getAllEntities().spliterator(), false)
                .collect(Collectors.toList());*/


        ClientboundRemoveEntitiesPacket packet =
                new ClientboundRemoveEntitiesPacket(self.getId());
        ClientPacketListener connection = mc.getConnection();
        packet.handle(connection);
        ClientEvents.erasedEntities.add(self);
    }

    /*@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void eraser$shrinkAABBOnTick(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (this.isErased()) {
            //self.setBoundingBox(new AABB(self.getX(), self.getY(), self.getZ(), self.getX(), self.getY(), self.getZ()));
            ci.cancel();
            self.deathTime ++;
        }
    }*/
}