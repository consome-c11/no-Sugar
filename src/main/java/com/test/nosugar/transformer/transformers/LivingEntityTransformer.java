package com.test.nosugar.transformer.transformers;

import com.test.nosugar.transformer.MethodMatcher;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import com.test.nosugar.transformer.ITransformerModule;
import com.test.nosugar.transformer.TransformerCore.Phase;

public class LivingEntityTransformer implements ITransformerModule {

    private static final MethodMatcher GET_HEALTH = MethodMatcher.of(
            "net/minecraft/world/entity/LivingEntity",
            "m_21223_", "getHealth", "()F"
    );

    private static final MethodMatcher IS_DEAD_OR_DYING = MethodMatcher.of(
            "net/minecraft/world/entity/LivingEntity",
            "m_21224_", "isDeadOrDying", "()Z"
    );

    private static final MethodMatcher IS_ALIVE = MethodMatcher.of(
            "net/minecraft/world/entity/Entity",
            "m_6084_", "isAlive", "()Z"
    );

    private static final String HOOK_CLASS = "com/test/nosugar/transformer/hook/livingentity/LivingEntityMethodsImpl";
    private static final String HOOK_FIELD = "INSTANCE";
    private static final String HOOK_DESC = "Lcom/test/nosugar/transformer/hook/livingentity/ILivingEntityHook;";

    @Override
    public boolean matchesClass(String className) {
        return "net/minecraft/world/entity/LivingEntity".equals(className)
                || "net/minecraft/world/entity/Entity".equals(className);
    }

    @Override
    public boolean matchesMethod(String className, MethodNode method) {
        if ("net/minecraft/world/entity/LivingEntity".equals(className)) {
            if (GET_HEALTH.matches(method)) return true;
            if (IS_DEAD_OR_DYING.matches(method)) return true;
        }
        if ("net/minecraft/world/entity/Entity".equals(className)) {
            if (IS_ALIVE.matches(method)) return true;
        }
        return false;
    }

    @Override
    public boolean transform(Phase phase, ClassNode classNode, MethodNode method) {
        boolean modified = false;
        //System.out.println("[NoSugar] transforming: " + method.name);
        for (AbstractInsnNode insn : method.instructions) {

            if (GET_HEALTH.matches(method) && insn.getOpcode() == Opcodes.FRETURN) {
                injectInterfaceHook(method, insn, "getHealth", "(FLnet/minecraft/world/entity/LivingEntity;)F");
                modified = true;
                //System.out.println("[NoSugar] transforming getHealth...");
            } else if (insn.getOpcode() == Opcodes.IRETURN) {
                if (IS_DEAD_OR_DYING.matches(method)) {
                    injectInterfaceHook(method, insn, "isDeadOrDying", "(ZLnet/minecraft/world/entity/LivingEntity;)Z");
                    modified = true;
                    //System.out.println("[NoSugar] transforming isDeadOrDying...");
                } else if (IS_ALIVE.matches(method)) {
                    injectInterfaceHook(method, insn, "isAlive", "(ZLnet/minecraft/world/entity/Entity;)Z");
                    modified = true;
                    //System.out.println("[NoSugar] transforming isAlive...");
                }
            }
        }
        return modified;
    }

    private void injectInterfaceHook(MethodNode method, AbstractInsnNode returnInsn,
                                     String hookMethod, String hookDesc) {
        InsnList injection = new InsnList();

        injection.add(new FieldInsnNode(Opcodes.GETSTATIC, HOOK_CLASS, HOOK_FIELD, HOOK_DESC));
        injection.add(new InsnNode(Opcodes.SWAP));
        injection.add(new VarInsnNode(Opcodes.ALOAD, 0));

        injection.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,
                "com/test/nosugar/transformer/hook/livingentity/ILivingEntityHook",
                hookMethod,
                hookDesc,
                true));

        method.instructions.insertBefore(returnInsn, injection);
        method.maxStack = Math.max(method.maxStack, 3);
    }

    @Override
    public int getPriority() {
        return 100;
    }
}