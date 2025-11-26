package com.test.nosugar.command;

import com.mojang.brigadier.Command;
import com.test.nosugar.NoSugar;
import com.test.nosugar.utils.intercafes.ILivingEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NoSugar.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("eraser")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            if (source.getPlayer() instanceof ILivingEntity player) {
                                player.instantKill(source.getPlayer(), false);
                            }

                            return Command.SINGLE_SUCCESS;
                        })

        );
    }
}
