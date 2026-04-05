package com.test.nosugar.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.*;
import java.util.jar.JarFile;
import java.util.concurrent.ConcurrentHashMap;

public class NSAgent {
    public static NSAgentLogger LOGGER = new NSAgentLogger("NoSugar Agent");

    private static final ConcurrentHashMap<Class<?>, Object> hookInstances = new ConcurrentHashMap<>();

    public static void agentmain(String args, Instrumentation inst) {
        String agentJarPath = getAgentJarPath();
        try {
            if (agentJarPath != null) {
                inst.appendToBootstrapClassLoaderSearch(new JarFile(agentJarPath));
                inst.appendToSystemClassLoaderSearch(new JarFile(agentJarPath));
                LOGGER.info("Appended agent JAR to bootstrap and system classloader: " + agentJarPath);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to append agent JAR", e);
        }

        ensureHookClassesLoaded();

        LOGGER.info("Installing transformer");
        init(inst);
    }

    private static void init(Instrumentation inst) {
        try {
            ClassLoader cl = NSAgent.class.getClassLoader();
            Class<?> transformerClass = Class.forName(
                    "com.test.nosugar.agent.NSTransformer",
                    true,
                    cl
            );

            ClassFileTransformer transformer = (ClassFileTransformer) transformerClass.getDeclaredConstructor().newInstance();
            inst.addTransformer(transformer, true);
            LOGGER.info("Transformer registered");

        } catch (Exception e) {
            LOGGER.error("Failed to register transformer", e);
            return;
        }

        retransformClassTransformer(inst);
    }

    private static void ensureHookClassesLoaded() {
        ClassLoader loader = NSAgent.class.getClassLoader();
        LOGGER.info("Using ClassLoader: " + loader);

        String[] classesToLoad = {};

        for (String className : classesToLoad) {
            try {
                Class.forName(className, true, loader);
                LOGGER.info("Successfully loaded: " + className);
            } catch (ClassNotFoundException e) {
                LOGGER.error("Failed to load: " + className, e);
            }
        }
    }

    private static void retransformClassTransformer(Instrumentation inst) {
        for (Class<?> c : inst.getAllLoadedClasses()) {
            if ("cpw.mods.modlauncher.ClassTransformer".equals(c.getName())) {
                LOGGER.info("Found ClassTransformer: " + c.getName());

                if (inst.isModifiableClass(c)) {
                    LOGGER.info("Retransforming: " + c.getName());
                    try {
                        inst.retransformClasses(c);
                    } catch (Exception e) {
                        LOGGER.error("Retransform failed", e);
                    }
                } else {
                    LOGGER.warn("ClassTransformer is not modifiable");
                }
                break;
            }
        }
    }

    private static String getAgentJarPath() {
        String path = System.getProperty("nosugar.agent.jar");
        if (path != null && new File(path).exists()) {
            return path;
        }
        return null;
    }

    private static String getTransformerJarPath() {
        String path = System.getProperty("nosugar.transformer.jar");
        if (path != null && new File(path).exists()) {
            return path;
        }
        return null;
    }
}