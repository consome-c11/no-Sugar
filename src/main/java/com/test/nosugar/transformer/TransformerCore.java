package com.test.nosugar.transformer;

import com.test.nosugar.transformer.transformers.LivingEntityTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TransformerCore {

    private static final List<ITransformerModule> MODULES = new ArrayList<>();
    private static final Map<String, ClassNode> classNodeCache = new ConcurrentHashMap<>();
    private static final Set<String> transformedClasses = ConcurrentHashMap.newKeySet();

    static {
        registerModule(new LivingEntityTransformer());
        MODULES.sort(Comparator.comparingInt(ITransformerModule::getPriority));
    }

    public static ClassNode getCachedClassNode(String className) {
        return classNodeCache.get(className);
    }

    public static void registerModule(ITransformerModule module) {
        MODULES.add(module);
    }

    public static boolean transform(Phase phase, ClassNode classNode) {

        if (classNode.name.startsWith("com.test.nosugar.transformer")) {
            return false;
        }

        String key = classNode.name + ":" + phase.name();
        if (!transformedClasses.add(key)) {
            return false;
        }

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
            com.test.nosugar.NoSugar.LOGGER.error(
                    "[NoSugar] Transformer error in class: " + classNode.name, e);
            return false;
        } finally {
            classNodeCache.remove(classNode.name);
        }
        return modified;
    }

    public enum Phase {
        BEFORE,
        AFTER,
        CLASS_LOADING
    }
}