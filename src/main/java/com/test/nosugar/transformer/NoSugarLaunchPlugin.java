package com.test.nosugar.transformer;

import com.test.nosugar.NoSugar;
import com.test.nosugar.agent.transformer.TransformerCore;
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
        if (type.getClassName().startsWith("com.test.nosugar")) {
            return EnumSet.noneOf(Phase.class);
        }
        return EnumSet.of(Phase.AFTER, Phase.BEFORE);
    }

    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType, String reason) {
        if (classNode.name.startsWith("com.test.nosugar")) {
            return false;
        }
        if (!ITransformerActivity.CLASSLOADING_REASON.equals(reason)) {
            return false;
        }

        try {
            TransformerCore.Phase corePhase = switch (phase) {
                case BEFORE -> TransformerCore.Phase.BEFORE;
                case AFTER -> TransformerCore.Phase.AFTER;
                default -> TransformerCore.Phase.CLASS_LOADING;
            };
            NSTransformDebug.scanAndDump(classNode);

            return false;
        } catch (Throwable e) {
            TransformerCore.LOGGER.error("[NoSugar] Transform error: " + classNode.name, e);
            return false;
        }
    }
}