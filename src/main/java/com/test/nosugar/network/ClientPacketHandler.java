package com.test.nosugar.network;

import com.test.nosugar.utils.ILivingEntity;
import com.test.nosugar.network.packets.EraseEntityPacket;
import com.test.nosugar.utils.TaskScheduler;
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

            if (((LivingEntity)e) instanceof ILivingEntity erased) {
                //LOGGER.info("[NoSuger] Received EraseEntityPacket for: " + e.getName().getString());
                //erased.setErased(true);
                erased.markErased(e.getUUID());

                if(!(e instanceof Player)) {
                    if(msg.skipAnimation()) erased.eraseClientEntity();
                    else TaskScheduler.schedule(erased::eraseClientEntity, 22);
                }

            }
        }
    }

}
