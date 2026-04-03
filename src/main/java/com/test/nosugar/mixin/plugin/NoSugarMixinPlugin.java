package com.test.nosugar.mixin.plugin;

import com.test.nosugar.NoSugar;
import com.test.nosugar.transformer.NoSugarLaunchPlugin;
import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NoSugarMixinPlugin implements IMixinConfigPlugin {

    private static boolean registered = false;

    static{
        if (!registered) {
            registerTransformer();
            registered = true;
        }
    }
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    private static void registerTransformer() {
        try {
            /*NoSugarLaunchPlugin plugin = new NoSugarLaunchPlugin();

            Field field = Launcher.class.getDeclaredField("launchPlugins");
            field.setAccessible(true);
            LaunchPluginHandler pluginHandler = (LaunchPluginHandler) field.get(Launcher.INSTANCE);

            field = LaunchPluginHandler.class.getDeclaredField("plugins");
            field.setAccessible(true);
            Map<String, ILaunchPluginService> map =
                    (Map<String, ILaunchPluginService>) field.get(pluginHandler);

            if (!map.containsKey(plugin.name())) {
                map.put(plugin.name(), plugin);
                NoSugar.LOGGER.info("[NoSugar] Transformer registered.");
            }*/
        } catch (Exception e) {
            NoSugar.LOGGER.debug("[NoSugar] Failed to register Transformer: " + e);
        }
    }
}