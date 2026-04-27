package com.test.nosugar.command;

import com.google.common.reflect.ClassPath;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.test.nosugar.NoSugar;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import com.test.nosugar.utils.item.Eraser_Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = NoSugar.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("nosugar")
                        .requires(source -> source.hasPermission(2))

                        .then(Commands.literal("eraser")
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ServerPlayer player = source.getPlayer();
                                    if (player != null) {
                                        Eraser_Utils.killIfParentFound(player, player);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(Commands.literal("dump_hp")
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ServerPlayer player = source.getPlayer();
                                    if (player == null) return Command.SINGLE_SUCCESS;

                                    boolean erased = ((ILivingEntity) player).isErased();

                                    player.displayClientMessage(
                                            Component.literal("HP: " + player.getHealth() +
                                                    " | Dead/Dying: " + player.isDeadOrDying() +
                                                    " | Alive: " + player.isAlive() +
                                                    " | Removed: " + player.isRemoved() +
                                                    " | Erased: " + erased),
                                            true
                                    );
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(Commands.literal("dump_class_files")
                                .then(Commands.argument("packageName", StringArgumentType.string())
                                        .executes(context -> {
                                            String pkg = StringArgumentType.getString(context, "packageName");
                                            var source = context.getSource();
                                            try {
                                                Path baseDir = Path.of("dumps/classes/" + pkg.replace(".", "_"));
                                                Files.createDirectories(baseDir);

                                                ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
                                                var classes = cp.getTopLevelClassesRecursive(pkg);

                                                int count = 0;
                                                for (ClassPath.ClassInfo info : classes) {
                                                    String resourceName = info.getName().replace('.', '/') + ".class";
                                                    try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
                                                        if (is != null) {
                                                            Path outputPath = baseDir.resolve(info.getSimpleName() + ".class");
                                                            Files.copy(is, outputPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                                            count++;
                                                        }
                                                    }
                                                }

                                                if (count == 0) {
                                                    source.sendFailure(Component.literal("§cクラスが見つかりませんでした。パッケージ名が正しいか、またはそのクラスが一度でも実行（ロード）されているか確認してください。"));
                                                    source.sendFailure(Component.literal("§7入力された値: " + pkg));
                                                } else {
                                                    int finalCount = count;
                                                    source.sendSuccess(() -> Component.literal("§a[NoSugar] §f" + finalCount + " 個のファイルをダンプしました。"), false);
                                                }

                                            } catch (Exception e) {
                                                source.sendFailure(Component.literal("error: " + e.getMessage()));
                                                e.printStackTrace();
                                            }

                                            return 1;
                                        })
                                )
                        )
        );
    }
}