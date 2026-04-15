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
            NSTransformDebug.scanAndDump(classNode);

            return false;
        } catch (Throwable e) {

            return false;
        }
    }
}