package com.test.nosugar.agent.transformer;

import com.test.nosugar.agent.NSAgentLogger;
import com.test.nosugar.agent.transformer.transformers.AbilitiesTransformer;
import com.test.nosugar.agent.transformer.transformers.LivingEntityTransformer;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.cl.ModuleClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TransformerCore {

    private static final List<ITransformerModule> MODULES = new ArrayList<>();
    private static final Map<String, ClassNode> classNodeCache = new ConcurrentHashMap<>();
    private static final Set<String> transformedClasses = ConcurrentHashMap.newKeySet();
    public static final NSAgentLogger LOGGER = new NSAgentLogger("NoSugar Transformer");
    static {
        registerModule(new LivingEntityTransformer());
        registerModule(new AbilitiesTransformer());
        MODULES.sort(Comparator.comparingInt(ITransformerModule::getPriority));
    }

    public static ClassNode getCachedClassNode(String className) {
        return classNodeCache.get(className);
    }

    public static void registerModule(ITransformerModule module) {
        MODULES.add(module);
    }

    public static boolean transform(Phase phase, ClassNode classNode) {
        if (classNode.name.startsWith("com.test.nosugar.agent")) {
            return false;
        }
        //なんか無い方が良かった🤔
        String key = classNode.name + ":" + phase.name();
        if (!transformedClasses.add(key)) {
            //LOGGER.info("Skipping transformation for class: " + key);
            //return false;
        }
        //LOGGER.info("Transforming " + classNode.name);
        boolean modified = false;
        try {
            for (ITransformerModule module : MODULES) {
                if (!module.matchesClass(classNode.name)) {
                    continue;
                }
                for (MethodNode method : classNode.methods) {
                    if (module.matchesMethod(classNode.name, method)) {
                        modified |= module.transform(phase, classNode, method);
                    }
                }
            }
        } catch (Throwable e) {
            TransformerCore.LOGGER.error(
                    "[NoSugar] Transformer error in class: " + classNode.name, e);
            return false;
        } finally {
            classNodeCache.remove(classNode.name);
        }
        return modified;
    }

    public static byte[] transformForByte(String internalClassName, byte[] classBytes) {
        try {
            if (classBytes == null || classBytes.length == 0) {
                return classBytes;
            }
            if (internalClassName.startsWith("com.test.nosugar.agent")) {
                return classBytes;
            }

            ClassReader cr = new ClassReader(classBytes);
            ClassNode classNode = new ClassNode(Opcodes.ASM9);
            cr.accept(classNode, ClassReader.EXPAND_FRAMES);

            boolean modified = transform(Phase.CLASS_LOADING, classNode);
            if (!modified) {
                return classBytes;
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(cw);
            return cw.toByteArray();

        } catch (ArrayIndexOutOfBoundsException | NullPointerException | IllegalArgumentException e) {
            LOGGER.warn("Frame computation failed for {}, skipping transformation...", internalClassName);
            LOGGER.error(e.getMessage(), e);
            return classBytes;
        } catch (Throwable t) {
            LOGGER.error("Failed to transform class: " + internalClassName, t);
            return classBytes;
        }
    }

    private static ClassLoader getTransformerClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = TransformerCore.class.getClassLoader();
        }
        return cl != null ? cl : ClassLoader.getSystemClassLoader();
    }
    public enum Phase {
        BEFORE,
        AFTER,
        CLASS_LOADING
    }
}