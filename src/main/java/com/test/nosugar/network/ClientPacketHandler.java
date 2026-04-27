package com.test.nosugar.network;

import com.test.nosugar.network.packets.EraseEntityPacket;
import com.test.nosugar.network.packets.SyncDeltaPacket;
import com.test.nosugar.utils.TaskScheduler;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.client.Minecraft;
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

            if (((LivingEntity) e) instanceof ILivingEntity erased) {
                //LOGGER.info("[NoSugar] Received EraseEntityPacket for: " + e.getName().getString());
                erased.setErased(true);
                if (!(e instanceof Player)) {
                    erased.markErased(e.getUUID());
                }

                if (msg.skipAnimation()) erased.eraseClientEntity();
                else TaskScheduler.schedule(erased::eraseClientEntity, 22);
            }
        }
    }

    public static void handleSetDelta(SyncDeltaPacket msg) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            Entity entity = mc.level.getEntity(msg.entityId);
            if (entity instanceof ILivingEntity iliving) {
                iliving.setDelta(msg.deltaValue);
            }
        }

    }
}