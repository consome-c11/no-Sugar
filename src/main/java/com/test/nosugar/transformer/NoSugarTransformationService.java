package com.test.nosugar.transformer;

import com.test.nosugar.transformer.transformers.AbilitiesTransformer;
import com.test.nosugar.transformer.transformers.LivingEntityTransformer;
import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.*;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.matcher.ElementMatchers;
import org.objectweb.asm.tree.ClassNode;
import org.jetbrains.annotations.NotNull;
import java.lang.instrument.Instrumentation;
import cpw.mods.modlauncher.TransformingClassLoader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class NoSugarTransformationService implements ITransformationService {
    private final List<ITransformerModule> modules = new ArrayList<>();
    static {
        try {
            Class.forName("com.test.nosugar.transformer.NSByteBuddy")
                    .getMethod("run")
                    .invoke(null);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    @Override
    public @NotNull String name() {
        return "nosugar";
    }

    @Override
    public void initialize(IEnvironment environment) {
    }

    @Override
     public @NotNull List<ITransformer> transformers() {
        return List.of();
    }

    @Override public void onLoad(IEnvironment env, Set<String> otherServices) {}

}