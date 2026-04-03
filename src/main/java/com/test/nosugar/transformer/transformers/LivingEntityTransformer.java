package com.test.nosugar.transformer.transformers;

import com.test.nosugar.transformer.*;
import com.test.nosugar.transformer.TransformerCore.Phase;
import com.test.nosugar.transformer.event.LivingEntityFieldEvent;
import com.test.nosugar.transformer.event.LivingEntityMethodEvent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class LivingEntityTransformer implements ITransformerModule {
    private static final MethodMatcher GET_HEALTH = MethodMatcher.of(
            "net/minecraft/world/entity/LivingEntity",
            Mapping.GET_HEALTH, "getHealth", "()F", false
    );

    private static final MethodMatcher IS_DEAD_OR_DYING = MethodMatcher.of(
            "net/minecraft/world/entity/LivingEntity",
            Mapping.IS_DEAD_OR_DYING, "isDeadOrDying", "()Z", false
    );

    private static final MethodMatcher IS_ALIVE = MethodMatcher.of(
            "net/minecraft/world/entity/Entity",
            Mapping.IS_ALIVE, "isAlive", "()Z", false
    );

    private static final MethodMatcher IS_REMOVED = MethodMatcher.of(
            "net/minecraft/world/entity/Entity",
            Mapping.IS_REMOVED, "isRemoved", "()Z", false
    );

    private static final MethodMatcher HURTTIME_FIELD = MethodMatcher.ofField(
            "net/minecraft/world/entity/LivingEntity",
            Mapping.HURT_TIME, "hurtTime", "I", false
    );

    private static final String METHOD_HOOK_CLASS = "com/test/nosugar/transformer/hook/livingentity/LivingEntityMethodImpl";
    private static final String HOOK_FIELD = "INSTANCE";
    private static final String METHOD_HOOK_DESC = "Lcom/test/nosugar/transformer/hook/livingentity/ILivingEntityMethodHook;";
    private static final String METHOD_PHASE_INTERNAL = "com/test/nosugar/transformer/event/LivingEntityMethodEvent$MethodPhase";
    private static final String METHOD_PHASE_DESC = "L" + METHOD_PHASE_INTERNAL + ";";
    private static final String FIELD_PHASE_INTERNAL = "com/test/nosugar/transformer/event/LivingEntityFieldEvent$FieldPhase";
    private static final String FIELD_PHASE_DESC = "L" + FIELD_PHASE_INTERNAL + ";";
    private static final String FIELD_HOOK_CLASS = "com/test/nosugar/transformer/hook/livingentity/LivingEntityFieldImpl";
    private static final String FIELD_HOOK_DESC = "Lcom/test/nosugar/transformer/hook/livingentity/ILivingEntityFieldHook;";
    @Override
    public boolean matchesClass(String className) {
        return true;
    }

    @Override
    public boolean matchesMethod(String className, MethodNode method) {
        if (GET_HEALTH.matches(method, className) ||
                IS_DEAD_OR_DYING.matches(method, className) ||
                IS_ALIVE.matches(method, className) ||
                IS_REMOVED.matches(method, className)) {
            return true;
        }

        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn instanceof MethodInsnNode minsn && isInvocation(minsn.getOpcode())) {
                if (GET_HEALTH.matchesCall(minsn) ||
                        IS_DEAD_OR_DYING.matchesCall(minsn) ||
                        IS_ALIVE.matchesCall(minsn) ||
                        IS_REMOVED.matchesCall(minsn)) {
                    return true;
                }
            }
        }

        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn instanceof FieldInsnNode finsn && finsn.getOpcode() == Opcodes.PUTFIELD) {
                if (HURTTIME_FIELD.matchesCall(finsn)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean transform(Phase phase, ClassNode classNode, MethodNode method) {
        boolean modified = false;
        String className = classNode.name;
        AbstractInsnNode[] insns = method.instructions.toArray();

        if ((GET_HEALTH.matches(method, className) && method.desc.endsWith("F")) ||
                (IS_DEAD_OR_DYING.matches(method, className) && method.desc.endsWith("Z")) ||
                (IS_ALIVE.matches(method, className) && method.desc.endsWith("Z")) ||
                (IS_REMOVED.matches(method, className) && method.desc.endsWith("Z"))) {

            String hookMethod = null;
            String hookDesc = null;

            if (GET_HEALTH.matches(method, className)) {
                hookMethod = "getHealth";
                hookDesc = "(FLjava/lang/Object;" + METHOD_PHASE_DESC + ")F";
            } else if (IS_DEAD_OR_DYING.matches(method, className)) {
                hookMethod = "isDeadOrDying";
                hookDesc = "(ZLjava/lang/Object;" + METHOD_PHASE_DESC + ")Z";
            } else if (IS_ALIVE.matches(method, className)) {
                hookMethod = "isAlive";
                hookDesc = "(ZLjava/lang/Object;" + METHOD_PHASE_DESC + ")Z";
            } else if (IS_REMOVED.matches(method, className)) {
                hookMethod = "isRemoved";
                hookDesc = "(ZLjava/lang/Object;" + METHOD_PHASE_DESC + ")Z";
            }

            if (hookMethod != null) {
                for (AbstractInsnNode insn : insns) {
                    int opcode = insn.getOpcode();
                    if (opcode == Opcodes.FRETURN || opcode == Opcodes.IRETURN) {
                        injectInterfaceHook(method, insn, hookMethod, hookDesc);
                        dumpInsnContext(className, method, insn, "HOOK_DEF: " + hookMethod + "@" + AsmUtil.getOpcodeName(opcode));
                        modified = true;
                    }
                }
            }
        }

        for (AbstractInsnNode insn : insns) {
            if (insn instanceof FieldInsnNode fieldInsn && fieldInsn.getOpcode() == Opcodes.PUTFIELD) {
                if (HURTTIME_FIELD.matchesCall(fieldInsn)) {
                    injectFieldWriteHook(method, fieldInsn, "onWriteHurtTime",
                            "(Ljava/lang/Object;ILjava/lang/String;" + FIELD_PHASE_DESC + ")I");
                    modified = true;
                }
            }

            if (insn instanceof MethodInsnNode callInsn && isInvocation(callInsn.getOpcode())) {
                String hookMethod = null;
                String hookDesc = null;

                if (GET_HEALTH.matchesCall(callInsn, className)) {
                    hookMethod = "getHealth";
                    hookDesc = "(FLjava/lang/Object;" + METHOD_PHASE_DESC + ")F";
                } else if (IS_DEAD_OR_DYING.matchesCall(callInsn, className)) {
                    hookMethod = "isDeadOrDying";
                    hookDesc = "(ZLjava/lang/Object;" + METHOD_PHASE_DESC + ")Z";
                } else if (IS_ALIVE.matchesCall(callInsn, className)) {
                    hookMethod = "isAlive";
                    hookDesc = "(ZLjava/lang/Object;" + METHOD_PHASE_DESC + ")Z";
                } else if (IS_REMOVED.matchesCall(callInsn, className)) {
                    hookMethod = "isRemoved";
                    hookDesc = "(ZLjava/lang/Object;" + METHOD_PHASE_DESC + ")Z";
                }

                if (hookMethod != null) {
                    injectPostCallHook(method, callInsn, hookMethod, hookDesc);
                    dumpInsnContext(className, method, insn, "HOOK_CALL: " + hookMethod + "@IRETURN");
                    modified = true;
                }
            }
        }

        return modified;
    }

    private void dumpInsnContext(String className, MethodNode method, AbstractInsnNode target, String label) {
        if(!TransformerCore.LOGGER.isDebugEnabled()) return;
        AbstractInsnNode[] insns = method.instructions.toArray();
        int idx = -1;
        for (int i = 0; i < insns.length; i++) {
            if (insns[i] == target) {
                idx = i;
                break;
            }
        }
        if (idx == -1) return;

        int start = Math.max(0, idx - 15);
        int end = Math.min(insns.length, idx + 15);

        StringBuilder sb = new StringBuilder();
        sb.append("\n[NoSugar Dump] ").append(label)
                .append(" | Method: ").append(method.name).append(method.desc)
                .append(" | Class: ").append(className);

        for (int i = start; i < end; i++) {
            AbstractInsnNode insn = insns[i];
            sb.append("\n  [").append(i).append("] ");
            if (i == idx) sb.append(">>> ");
            else sb.append("    ");

            if (insn.getOpcode() >= 0) {
                sb.append(AsmUtil.getOpcodeName(insn.getOpcode()));
            } else {
                sb.append(insn.getClass().getSimpleName());
            }

            String operand = getOperandString(insn);
            if (operand != null && !operand.isEmpty()) {
                sb.append(" ").append(operand);
            }
        }
        TransformerCore.LOGGER.info(sb.toString());
    }

    private String getOperandString(AbstractInsnNode insn) {
        if (insn instanceof MethodInsnNode m) {
            return String.format("%s.%s%s", m.owner, m.name, m.desc);
        }
        if (insn instanceof FieldInsnNode f) {
            return String.format("%s.%s:%s", f.owner, f.name, f.desc);
        }
        if (insn instanceof TypeInsnNode t) {
            return t.desc;
        }
        if (insn instanceof IntInsnNode ii) {
            return ii.operand >= 0 && ii.operand < 256 ? String.valueOf(ii.operand) : "0x" + Integer.toHexString(ii.operand);
        }
        if (insn instanceof VarInsnNode v) {
            return "var[" + v.var + "]";
        }
        if (insn instanceof LdcInsnNode ldc) {
            if (ldc.cst instanceof String) return "\"" + ldc.cst + "\"";
            if (ldc.cst instanceof Type) return ((Type) ldc.cst).getDescriptor();
            return String.valueOf(ldc.cst);
        }
        if (insn instanceof JumpInsnNode j) {
            return "label[" + j.label.getLabel() + "]";
        }
        if (insn instanceof InvokeDynamicInsnNode id) {
            return String.format("%s%s [bsm=%s]", id.name, id.desc, id.bsm);
        }
        if (insn instanceof LabelNode l) {
            return "Label[" + l.getLabel() + "]";
        }
        if (insn instanceof FrameNode) {
            return "Frame";
        }
        if (insn instanceof LineNumberNode ln) {
            return "line:" + ln.line;
        }
        return null;
    }

    private void pushEnumConstant(InsnList list, LivingEntityMethodEvent.MethodPhase phase) {
        String name = phase.name();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, METHOD_PHASE_INTERNAL, name, METHOD_PHASE_DESC));
    }

    private void injectInterfaceHook(MethodNode method, AbstractInsnNode returnInsn,
                                     String hookMethod, String hookDesc,
                                     LivingEntityMethodEvent.MethodPhase phase) {
        InsnList injection = new InsnList();
        injection.add(new FieldInsnNode(Opcodes.GETSTATIC, METHOD_HOOK_CLASS, HOOK_FIELD, METHOD_HOOK_DESC));
        injection.add(new InsnNode(Opcodes.SWAP));
        injection.add(new VarInsnNode(Opcodes.ALOAD, 0));

        pushEnumConstant(injection, phase);
        injection.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,
                "com/test/nosugar/transformer/hook/livingentity/ILivingEntityMethodHook",
                hookMethod,
                hookDesc,
                true));

        method.instructions.insertBefore(returnInsn, injection);
        method.maxStack = Math.max(method.maxStack, 8);
    }

    private void injectInterfaceHook(MethodNode method, AbstractInsnNode returnInsn,
                                     String hookMethod, String hookDesc) {
        injectInterfaceHook(method, returnInsn, hookMethod, hookDesc,
                LivingEntityMethodEvent.MethodPhase.RETURN);
    }

    private boolean isInvocation(int opcode) {
        return opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKESPECIAL || opcode == Opcodes.INVOKEDYNAMIC;
    }

    private void injectPostCallHook(MethodNode method, MethodInsnNode originalCall,
                                    String hookMethod, String hookDesc) {

        InsnList prePatch = new InsnList();
        prePatch.add(new InsnNode(Opcodes.DUP));
        method.instructions.insertBefore(originalCall, prePatch);

        InsnList postPatch = new InsnList();

        postPatch.add(new FieldInsnNode(Opcodes.GETSTATIC, METHOD_HOOK_CLASS, HOOK_FIELD, METHOD_HOOK_DESC));
        postPatch.add(new InsnNode(Opcodes.DUP_X2));
        postPatch.add(new InsnNode(Opcodes.POP));
        postPatch.add(new InsnNode(Opcodes.SWAP));
        pushEnumConstant(postPatch, LivingEntityMethodEvent.MethodPhase.AFTER);
        postPatch.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,
                "com/test/nosugar/transformer/hook/livingentity/ILivingEntityMethodHook",
                hookMethod, hookDesc, true));

        method.instructions.insert(originalCall, postPatch);
        method.maxStack = Math.max(method.maxStack, method.maxStack + 4);
    }

    private void pushEnumConstant(InsnList list, LivingEntityFieldEvent.FieldPhase phase) {
        list.add(new FieldInsnNode(Opcodes.GETSTATIC,
                FIELD_PHASE_INTERNAL, phase.name(), FIELD_PHASE_DESC));
    }

    private void injectFieldWriteHook(MethodNode method, FieldInsnNode targetField,
                                      String hookMethod, String hookDesc) {
        InsnList injection = new InsnList();
        injection.add(new InsnNode(Opcodes.DUP2));
        injection.add(new FieldInsnNode(Opcodes.GETSTATIC, FIELD_HOOK_CLASS, "INSTANCE", FIELD_HOOK_DESC));
        injection.add(new InsnNode(Opcodes.DUP_X2));
        injection.add(new InsnNode(Opcodes.POP));
        injection.add(new LdcInsnNode(targetField.name));
        pushEnumConstant(injection, LivingEntityFieldEvent.FieldPhase.BEFORE);
        injection.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,
                "com/test/nosugar/transformer/hook/livingentity/ILivingEntityFieldHook",
                hookMethod, hookDesc, true));
        injection.add(new InsnNode(Opcodes.SWAP));
        injection.add(new InsnNode(Opcodes.POP));

        method.instructions.insertBefore(targetField, injection);
        method.maxStack = Math.max(method.maxStack, 8);
    }

    @Override
    public int getPriority() {
        return 100;
    }
}