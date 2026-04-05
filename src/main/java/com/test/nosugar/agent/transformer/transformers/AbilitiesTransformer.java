package com.test.nosugar.agent.transformer.transformers;

import com.test.nosugar.agent.transformer.*;
import com.test.nosugar.agent.transformer.TransformerCore.Phase;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class AbilitiesTransformer implements ITransformerModule {

    private static final MethodMatcher MAY_FLY_FIELD = MethodMatcher.ofField(
            "net/minecraft/world/entity/player/Abilities",
            Mapping.MAY_FLY, "mayfly", "Z", false
    );

    private static final MethodMatcher IS_FLYING_FIELD = MethodMatcher.ofField(
            "net/minecraft/world/entity/player/Abilities",
            Mapping.IS_FLYING, "flying", "Z", false
    );

    private static final String FIELD_HOOK_CLASS = "com/test/nosugar/utils/entity/hook/abilities/AbilitiesFieldImpl";
    private static final String FIELD_HOOK_DESC = "Lcom/test/nosugar/utils/entity/hook/abilities/IAbilitiesFieldHook;";
    private static final String FIELD_PHASE_INTERNAL = "com/test/nosugar/utils/entity/event/AbilitiesFieldEvent$FieldPhase";
    private static final String FIELD_PHASE_DESC = "L" + FIELD_PHASE_INTERNAL + ";";

    @Override
    public boolean matchesClass(String className) {
        return true;
    }

    @Override
    public boolean matchesMethod(String className, MethodNode method) {
        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn instanceof FieldInsnNode finsn && finsn.getOpcode() == Opcodes.PUTFIELD) {
                if (MAY_FLY_FIELD.matchesCall(finsn) || IS_FLYING_FIELD.matchesCall(finsn)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean transform(Phase phase, ClassNode classNode, MethodNode method) {

        boolean modified = false;
        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (!(insn instanceof FieldInsnNode fieldInsn)) continue;
            if (fieldInsn.getOpcode() != Opcodes.PUTFIELD) continue;

            if (MAY_FLY_FIELD.matchesCall(fieldInsn)) {
                injectFieldHook(method, fieldInsn, "onWriteMayFly");
                dumpInsnContext(classNode.name, method, fieldInsn, "HOOK_FIELD: mayfly");
                modified = true;
            } else if (IS_FLYING_FIELD.matchesCall(fieldInsn)) {
                injectFieldHook(method, fieldInsn, "onWriteFlying");
                dumpInsnContext(classNode.name, method, fieldInsn, "HOOK_FIELD: flying");
                modified = true;
            }
        }
        return modified;
    }

    private void injectFieldHook(MethodNode method, FieldInsnNode target, String hookMethod) {
        InsnList insns = new InsnList();
        insns.add(new InsnNode(Opcodes.DUP2));
        insns.add(new FieldInsnNode(Opcodes.GETSTATIC, FIELD_HOOK_CLASS, "INSTANCE", FIELD_HOOK_DESC));
        insns.add(new InsnNode(Opcodes.DUP_X2));
        insns.add(new InsnNode(Opcodes.POP));
        insns.add(new LdcInsnNode(target.name));
        insns.add(new FieldInsnNode(Opcodes.GETSTATIC, FIELD_PHASE_INTERNAL, "BEFORE", FIELD_PHASE_DESC));
        insns.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,
                "com/test/nosugar/utils/entity/hook/abilities/IAbilitiesFieldHook",
                hookMethod, "(Ljava/lang/Object;ZLjava/lang/String;" + FIELD_PHASE_DESC + ")Z", true));
        insns.add(new InsnNode(Opcodes.SWAP));
        insns.add(new InsnNode(Opcodes.POP));
        method.instructions.insertBefore(target, insns);
        method.maxStack = Math.max(method.maxStack, 12);
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

        int start = Math.max(0, idx - 80);
        int end = Math.min(insns.length, idx + 80);

        StringBuilder sb = new StringBuilder();
        sb.append("\n[NoSugar Dump] ").append(label)
                .append(" | Method: ").append(method.name).append(method.desc)
                .append(" | Class: ").append(className);

        for (int i = start; i < end; i++) {
            AbstractInsnNode insn = insns[i];
            sb.append("\n  [").append(i).append("] ");
            if (insn == target) sb.append(">>> ");
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
            return String.valueOf(ii.operand);
        }
        if (insn instanceof VarInsnNode v) {
            return "var[" + v.var + "]";
        }
        if (insn instanceof LdcInsnNode ldc) {
            if (ldc.cst instanceof String) return "\"" + ldc.cst + "\"";
            if (ldc.cst instanceof Type) return ((Type) ldc.cst).getDescriptor();
            return String.valueOf(ldc.cst);
        }
        return "";
    }

    @Override
    public int getPriority() {
        return 100;
    }
}