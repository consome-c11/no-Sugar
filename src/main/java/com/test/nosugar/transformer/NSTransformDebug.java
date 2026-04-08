package com.test.nosugar.transformer;

import com.test.nosugar.NoSugar;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class NSTransformDebug {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

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
    private static final MethodMatcher IS_REMOVED = MethodMatcher.of(
            "net/minecraft/world/entity/Entity",
            "m_213877_", "isRemoved", "()Z", false
    );
    private static final MethodMatcher HURTTIME_FIELD = MethodMatcher.ofField(
            "net/minecraft/world/entity/LivingEntity",
            "f_20916_", "hurtTime", "I", false
    );

    private NSTransformDebug() {}

    public static void scanAndDump(ClassNode classNode) {
        //if (!isEnabled()) return;
        for (MethodNode method : classNode.methods) {
            scanMethod(classNode.name, method);
        }
    }

    private static void scanMethod(String className, MethodNode method) {
        if (GET_HEALTH.matches(method, className) ||
                IS_DEAD_OR_DYING.matches(method, className) ||
                IS_ALIVE.matches(method, className) ||
                IS_REMOVED.matches(method, className)) {

            for (AbstractInsnNode insn : method.instructions.toArray()) {
                if (insn.getOpcode() == Opcodes.FRETURN || insn.getOpcode() == Opcodes.IRETURN) {
                    dump(className, method, insn, "HOOK_DEF", method.name + "@RETURN");
                }
            }
        }

        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn instanceof MethodInsnNode minsn && isInvocation(minsn.getOpcode())) {
                if (GET_HEALTH.matchesCall(minsn) ||
                        IS_DEAD_OR_DYING.matchesCall(minsn) ||
                        IS_ALIVE.matchesCall(minsn) ||
                        IS_REMOVED.matchesCall(minsn)) {
                    dump(className, method, insn, "HOOK_CALL", minsn.name + "@AFTER");
                }
            }

            if (insn instanceof FieldInsnNode finsn && finsn.getOpcode() == Opcodes.PUTFIELD) {
                if (HURTTIME_FIELD.matchesCall(finsn)) {
                    dump(className, method, insn, "FIELD_WRITE", finsn.name + "@BEFORE");
                }
            }
        }
    }

    public static void dumpClass(String className, String phaseName) {
        if (!isEnabled()) return;
        NoSugar.LOGGER.info("[{}] CLASS_TRANSFORMED {} Class: {}",
                LocalDateTime.now().format(TIME_FORMAT), phaseName, className);
    }

    public static void dump(String className, MethodNode method, AbstractInsnNode insn, String tag, String detail) {
        if (!isEnabled()) return;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(LocalDateTime.now().format(TIME_FORMAT)).append("] ");
            sb.append(tag);
            if (detail != null) sb.append(" [").append(detail).append("]");
            sb.append("\n  Class: ").append(className);
            sb.append("\n  Method: ").append(method.name).append(method.desc);
            sb.append("\n  Insn: ").append(insnToString(insn));
            sb.append("\n  --- Context ---\n");
            sb.append(dumpInsnContext(method.instructions, insn));

            NoSugar.LOGGER.info(sb.toString());
        } catch (Exception e) {
            NoSugar.LOGGER.warn("[NSTransformDebug] dump failed: " + e.getMessage());
        }
    }

    private static boolean isEnabled() {
        return Boolean.getBoolean("nosugar.debug.transform");
    }

    private static boolean isInvocation(int opcode) {
        return opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKESPECIAL || opcode == Opcodes.INVOKEDYNAMIC;
    }

    private static String insnToString(AbstractInsnNode insn) {
        if (insn == null) return "null";
        int op = insn.getOpcode();
        String opName = (op >= 0 && op < 256) ? opcodeName(op) : "INSN";

        if (insn instanceof MethodInsnNode m) return String.format("METHOD[%s] %s.%s%s", opName, m.owner, m.name, m.desc);
        if (insn instanceof FieldInsnNode f) return String.format("FIELD[%s] %s.%s:%s", opName, f.owner, f.name, f.desc);
        if (insn instanceof VarInsnNode v) return String.format("VAR[%s] %d", opName, v.var);
        return opName;
    }

    private static String opcodeName(int opcode) {
        for (java.lang.reflect.Field f : Opcodes.class.getFields()) {
            try {
                if (f.getType() == int.class && f.getInt(null) == opcode) return f.getName();
            } catch (Exception ignored) {}
        }
        return String.valueOf(opcode);
    }

    private static String dumpInsnContext(InsnList insns, AbstractInsnNode target) {
        AbstractInsnNode[] arr = insns.toArray();
        int idx = -1;
        for (int i = 0; i < arr.length; i++) { if (arr[i] == target) { idx = i; break; } }
        if (idx < 0) return "  (not found)";

        StringBuilder sb = new StringBuilder();
        for (int i = Math.max(0, idx - 20); i <= Math.min(arr.length - 1, idx + 20); i++) {
            sb.append(i == idx ? " > " : "   ").append(String.format("%3d: %s\n", i, insnToString(arr[i])));
        }
        return sb.toString();
    }
}