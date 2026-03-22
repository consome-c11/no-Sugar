package com.test.nosugar.transformer;

import cpw.mods.modlauncher.api.ITransformerActivity;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.util.EnumSet;

public class NoSugarLaunchPlugin implements ILaunchPluginService {

    @Override
    public String name() {
        return "nosugar";
    }

    @Override
    public EnumSet<Phase> handlesClass(Type type, boolean isEmpty) {
        if (type.getClassName().startsWith("com.test.nosugar.transformer")) {
            return EnumSet.noneOf(Phase.class);
        }
        return EnumSet.of(Phase.BEFORE);
    }

    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType, String reason) {
        if (classNode.name.startsWith("com.test.nosugar.transformer")) {
            return false;
        }

        if (!ITransformerActivity.CLASSLOADING_REASON.equals(reason)) {
            return false;
        }

        try {
            TransformerCore.Phase corePhase = switch (phase) {
                case BEFORE -> TransformerCore.Phase.BEFORE;
                case AFTER -> TransformerCore.Phase.AFTER;
            };

            return TransformerCore.transform(corePhase, classNode);
        } catch (Throwable e) {
            com.test.nosugar.NoSugar.LOGGER.error(
                    "[NoSugar] Transformer error in class: " + classNode.name, e);
            return false;
        }
    }
}