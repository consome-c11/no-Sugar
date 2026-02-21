package com.test.nosugar.transformer;

import com.test.nosugar.transformer.transformers.LivingEntityTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class TransformerCore {

    private static final List<ITransformerModule> MODULES = new ArrayList<>();
    private static boolean initialized = false;

    public enum Phase {
        BEFORE,
        AFTER,
        CLASS_LOADING
    }

    static {
        registerModule(new LivingEntityTransformer());
        MODULES.sort(Comparator.comparingInt(ITransformerModule::getPriority));
    }

    public static void registerModule(ITransformerModule module) {
        MODULES.add(module);
    }

    public static boolean transform(Phase phase, ClassNode classNode) {
        if (classNode.name.startsWith("com/test/nosugar/transformer")) {
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
                    "Transformer error in class: " + classNode.name, e);
            return false;
        }
        return modified;
    }
}