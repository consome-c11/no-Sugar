package com.test.nosugar.command;

import com.mojang.brigadier.Command;
import com.test.nosugar.NoSugar;
import com.test.nosugar.utils.item.Eraser_Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
                            Eraser_Utils.killIfParentFound(source.getPlayer(), source.getPlayer());

                            return Command.SINGLE_SUCCESS;
                        })

        );
        event.getDispatcher().register(
                Commands.literal("dump_hp")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            ServerPlayer player =source.getPlayer();
                            if(player == null) return Command.SINGLE_SUCCESS;
                            player.displayClientMessage(Component.literal("HP: " + player.getHealth() + " isDeadOrDying: " + player.isDeadOrDying() + " isAlive: " + player.isAlive() + " isRemoved: " + player.isRemoved()), true);
                            return Command.SINGLE_SUCCESS;
                        })

        );
    }
}
