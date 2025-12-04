package com.test.nosugar.network;

import com.test.nosugar.NoSugar;
import com.test.nosugar.utils.intercafes.ILivingEntity;
import com.test.nosugar.network.packets.EraseEntityPacket;
import com.test.nosugar.utils.TaskScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {
    public static void handleEraseEntity(EraseEntityPacket msg) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level != null) {
            Entity e = null;
            for (Entity ent : mc.level.entitiesForRendering()) {
                if (ent.getUUID().equals(msg.entityUuid())) {
                    e = ent;
                    break;
                }
            }
            if (e == null) {
                return;
            }

            if (((LivingEntity)e) instanceof ILivingEntity erased) {
                //LOGGER.info("[NoSugar] Received EraseEntityPacket for: " + e.getName().getString());
                //erased.setErased(true);
                erased.markErased(e.getUUID());

                if(!(e instanceof Player)) {
                    if(msg.skipAnimation()) erased.eraseClientEntity();
                    else TaskScheduler.schedule(erased::eraseClientEntity, 22);
                }

            }
        }
    }

    public static void handleSyncPacket(int entityId, double x, double y, double z,
                                        float yRot, float xRot, boolean isAttacking) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        Entity entity = level.getEntity(entityId);

        if (entity == null) {
            entity = createClientEntity(level, entityId);
            if (entity != null) {
                level.addFreshEntity(entity);
            }
        }

        if (entity != null) {
            // 位置と回転を更新
            entity.setPos(x, y, z);
            entity.setYRot(yRot);
            entity.setXRot(xRot);

            if (entity instanceof LivingEntity livingEntity) {
                //livingEntity.getEntityData().set(ClientEntityCache.ClientEntityData.IS_ATTACKING, isAttacking);
            }
        }
    }

    private static Entity createClientEntity(ClientLevel level, int entityId) {
        try {
            return null;
            //return new Sand_Bag_v2(level, BlockPos.ZERO);
        } catch (Exception e) {
            NoSugar.LOGGER.error("Failed to create client entity for ID: {}", entityId, e);
            return null;
        }
    }

}
