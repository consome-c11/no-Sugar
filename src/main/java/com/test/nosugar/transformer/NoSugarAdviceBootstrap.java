package com.test.nosugar.transformer;

import cpw.mods.modlauncher.api.ITransformerActivity;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public final class NoSugarAdviceBootstrap {

    private static final ConcurrentHashMap<String, Boolean> TRANSFORMED = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;
    private static volatile Method transformMethod = null;
    private static volatile ClassLoader transformerClassLoader = null;
    public static void setTransformerClassLoader(ClassLoader cl) {
        transformerClassLoader = cl;
    }
    public static byte[] dispatch(String className, byte[] classBytes, String reason) {
        try {
            String key = className + ":ClassTransformer";
            if (Boolean.FALSE.equals(TRANSFORMED.putIfAbsent(key, Boolean.TRUE))) {
                return null;
            }

            if (!initialized) {
                initialize();
            }
            if (transformMethod == null) {
                return classBytes;
            }
            if (!ITransformerActivity.CLASSLOADING_REASON.equals(reason)) {
                return classBytes;
            }
            Object result = transformMethod.invoke(null, className, classBytes);
            if (result instanceof byte[]) {
                return (byte[]) result;
            }
            return classBytes;

        } catch (Throwable t) {
            System.err.println("[NoSugar] Advice error: " + t.getMessage());
            return classBytes;
        }
    }

    private static synchronized void initialize() {
        if (initialized) return;
        try {
            ClassLoader cl = transformerClassLoader;
            if (cl == null) {
                cl = Thread.currentThread().getContextClassLoader();
            }
            if (cl == null) {
                cl = NoSugarAdviceBootstrap.class.getClassLoader();
            }

            Class<?> classNodeClass = Class.forName("org.objectweb.asm.tree.ClassNode", false, cl);

            Class<?> coreClass = Class.forName(
                    "com.test.nosugar.transformer.TransformerCore",
                    false,
                    cl
            );

            transformMethod = coreClass.getMethod("transformForAdvice", String.class, byte[].class);
            initialized = true;

        } catch (Throwable t) {
            System.err.println("[NoSugar] Failed to initialize TransformerCore: " + t.getMessage());
            if (t instanceof ClassNotFoundException) {
                System.err.println("[NoSugar] Missing class: " + ((ClassNotFoundException)t).getMessage());
            }
            transformMethod = null;
        }
    }

    private NoSugarAdviceBootstrap() {}
}