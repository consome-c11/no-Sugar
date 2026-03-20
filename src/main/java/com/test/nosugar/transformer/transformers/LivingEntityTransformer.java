package com.test.nosugar.transformer.transformers;

import com.test.nosugar.NoSugar;
import com.test.nosugar.transformer.ITransformerModule;
import com.test.nosugar.transformer.MethodMatcher;
import com.test.nosugar.transformer.TransformerCore.Phase;
import com.test.nosugar.transformer.event.LivingEntityMethodEvent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class LivingEntityTransformer implements ITransformerModule {
    private static final MethodMatcher GET_HEALTH = MethodMatcher.of(
            "net/minecraft/world/entity/LivingEntity",
            "m_21223_", "getHealth", "()F", false
    );

    private static final MethodMatcher IS_DEAD_OR_DYING = MethodMatcher.of(
            "net/minecraft/world/entity/LivingEntity",
            "m_21224_", "isDeadOrDying", "()Z", false
    );

    private static final MethodMatcher IS_ALIVE = MethodMatcher.of(
            "net/minecraft/world/entity/Entity",
            "m_6084_", "isAlive", "()Z", false
    );

    private static final String HOOK_CLASS = "com/test/nosugar/transformer/hook/livingentity/LivingEntityMethodsImpl";
    private static final String HOOK_FIELD = "INSTANCE";
    private static final String HOOK_DESC = "Lcom/test/nosugar/transformer/hook/livingentity/ILivingEntityHook;";
    private static final String PHASE_INTERNAL = "com/test/nosugar/transformer/event/LivingEntityMethodEvent$MethodPhase";
    private static final String PHASE_DESC = "L" + PHASE_INTERNAL + ";";

    @Override
    public boolean matchesClass(String className) {
        return true;
    }

    @Override
    public boolean matchesMethod(String className, MethodNode method) {
        if (GET_HEALTH.matches(method, className) ||
                IS_DEAD_OR_DYING.matches(method, className) ||
                IS_ALIVE.matches(method, className)) {
            return true;
        }

        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn instanceof MethodInsnNode minsn && isInvocation(minsn.getOpcode())) {
                if (GET_HEALTH.matches(minsn) ||
                        IS_DEAD_OR_DYING.matches(minsn) ||
                        IS_ALIVE.matches(minsn)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean transform(Phase phase, ClassNode classNode, MethodNode method) {
        boolean modified = false;
        if(phase == Phase.AFTER) return false;
        AbstractInsnNode[] insns = method.instructions.toArray();
        AbstractInsnNode[] callInsns = method.instructions.toArray();
        for (AbstractInsnNode insn : insns) {
            if (GET_HEALTH.matches(method, classNode.name) && insn.getOpcode() == Opcodes.FRETURN) {
                injectInterfaceHook(method, insn, "getHealth",
                        "(FLnet/minecraft/world/entity/LivingEntity;" + PHASE_DESC + ")F");
                modified = true;
                NoSugar.LOGGER.info("[NoSugar] transforming getHealth... \n Class: " + classNode.name);
            } else if (insn.getOpcode() == Opcodes.IRETURN) {
                if (IS_DEAD_OR_DYING.matches(method, classNode.name)) {
                    injectInterfaceHook(method, insn, "isDeadOrDying",
                            "(ZLnet/minecraft/world/entity/LivingEntity;" + PHASE_DESC + ")Z");
                    modified = true;
                    NoSugar.LOGGER.info("[NoSugar] transforming isDeadOrDying...\n Class: " + classNode.name);
                } else if (IS_ALIVE.matches(method, classNode.name)) {
                    injectInterfaceHook(method, insn, "isAlive",
                            "(ZLnet/minecraft/world/entity/Entity;" + PHASE_DESC + ")Z");
                    modified = true;
                    NoSugar.LOGGER.info("[NoSugar] transforming isAlive...\n Class: " + classNode.name);
                }
            }
        }
        for (AbstractInsnNode insn : callInsns) {
            if (!(insn instanceof MethodInsnNode methodInsn)) continue;
            if (!isInvocation(methodInsn.getOpcode())) continue;

            if (GET_HEALTH.matches(methodInsn)) {
                injectPostCallHook(method, methodInsn, "getHealth",
                        "(FLnet/minecraft/world/entity/LivingEntity;" + PHASE_DESC + ")F",
                        Opcodes.FSTORE, Opcodes.FLOAD);
                NoSugar.LOGGER.info("[NoSugar] wrapping getHealth...\n Class: " + classNode.name);
                modified = true;
            } else if (IS_DEAD_OR_DYING.matches(methodInsn)) {
                injectPostCallHook(method, methodInsn, "isDeadOrDying",
                        "(ZLnet/minecraft/world/entity/LivingEntity;" + PHASE_DESC + ")Z",
                        Opcodes.ISTORE, Opcodes.ILOAD);
                NoSugar.LOGGER.info("[NoSugar] wrapping isDeadOrDying...\n Class: " + classNode.name);
                modified = true;
            } else if (IS_ALIVE.matches(methodInsn)) {
                injectPostCallHook(method, methodInsn, "isAlive",
                        "(ZLnet/minecraft/world/entity/Entity;" + PHASE_DESC + ")Z",
                        Opcodes.ISTORE, Opcodes.ILOAD);
                NoSugar.LOGGER.info("[NoSugar] wrapping isAlive...\n Class: " + classNode.name);
                modified = true;
            }
        }
        return modified;
    }

    private void pushEnumConstant(InsnList list, LivingEntityMethodEvent.MethodPhase phase) {
        String name = phase.name(); //"RETURN" "AFTER"
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, PHASE_INTERNAL, name, PHASE_DESC));
    }

    private void injectInterfaceHook(MethodNode method, AbstractInsnNode returnInsn,
                                     String hookMethod, String hookDesc,
                                     LivingEntityMethodEvent.MethodPhase phase) {
        InsnList injection = new InsnList();
        injection.add(new FieldInsnNode(Opcodes.GETSTATIC, HOOK_CLASS, HOOK_FIELD, HOOK_DESC));
        injection.add(new InsnNode(Opcodes.SWAP));
        injection.add(new VarInsnNode(Opcodes.ALOAD, 0));

        pushEnumConstant(injection, phase);
        injection.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,
                "com/test/nosugar/transformer/hook/livingentity/ILivingEntityHook",
                hookMethod,
                hookDesc,
                true));

        method.instructions.insertBefore(returnInsn, injection);
        method.maxStack = Math.max(method.maxStack, 6);
    }

    private void injectInterfaceHook(MethodNode method, AbstractInsnNode returnInsn,
                                     String hookMethod, String hookDesc) {
        injectInterfaceHook(method, returnInsn, hookMethod, hookDesc,
                LivingEntityMethodEvent.MethodPhase.RETURN);
    }

    private boolean isInvocation(int opcode) {
        return opcode >= Opcodes.INVOKEVIRTUAL && opcode <= Opcodes.INVOKEINTERFACE;
    }

    private void injectPostCallHook(MethodNode method, MethodInsnNode originalCall, String hookMethod, String hookDesc, int storeOpcode, int loadOpcode) {
        int receiverSlot = method.maxLocals++;

        InsnList before = new InsnList();
        before.add(new InsnNode(Opcodes.DUP));
        before.add(new VarInsnNode(Opcodes.ASTORE, receiverSlot));

        InsnList after = new InsnList();

        after.add(new InsnNode(Opcodes.DUP));
        after.add(new FieldInsnNode(Opcodes.GETSTATIC, HOOK_CLASS, HOOK_FIELD, HOOK_DESC));
        after.add(new InsnNode(Opcodes.SWAP));
        after.add(new VarInsnNode(Opcodes.ALOAD, receiverSlot));
        pushEnumConstant(after, LivingEntityMethodEvent.MethodPhase.AFTER);
        after.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,
                "com/test/nosugar/transformer/hook/livingentity/ILivingEntityHook",
                hookMethod,
                hookDesc,
                true));
        after.add(new InsnNode(Opcodes.SWAP));
        after.add(new InsnNode(Opcodes.POP));

        method.instructions.insertBefore(originalCall, before);
        method.instructions.insert(originalCall, after);

        method.maxStack = Math.max(method.maxStack, method.maxStack + 5);
    }

    @Override
    public int getPriority() {
        return 100;
    }
}