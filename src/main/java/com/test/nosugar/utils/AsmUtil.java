package com.test.nosugar.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.List;

public class AsmUtil {

    public static void recalculateMaxStack(MethodNode method) {
        int maxStack = 0;
        int currentStack = 0;

        for (AbstractInsnNode insn : method.instructions) {
            int opcode = insn.getOpcode();
            if (opcode == -1) continue;

            currentStack += getStackChange(opcode, insn);
            maxStack = Math.max(maxStack, currentStack);
        }

        method.maxStack = Math.max(method.maxStack, maxStack);
    }

    //スパゲッティすぎる
    private static int getStackChange(int opcode, AbstractInsnNode insn) {
        switch (opcode) {
            case Opcodes.NOP:
                return 0;
            case Opcodes.ACONST_NULL:
            case Opcodes.ICONST_M1:
            case Opcodes.ICONST_0:
            case Opcodes.ICONST_1:
            case Opcodes.ICONST_2:
            case Opcodes.ICONST_3:
            case Opcodes.ICONST_4:
            case Opcodes.ICONST_5:
            case Opcodes.FCONST_0:
            case Opcodes.FCONST_1:
            case Opcodes.FCONST_2:
            case Opcodes.BIPUSH:
            case Opcodes.SIPUSH:
            case Opcodes.LDC:
                return 1;
            case Opcodes.LCONST_0:
            case Opcodes.LCONST_1:
            case Opcodes.DCONST_0:
            case Opcodes.DCONST_1:
                return 2;
            case Opcodes.ILOAD:
            case Opcodes.FLOAD:
            case Opcodes.ALOAD:
                return 1;
            case Opcodes.LLOAD:
            case Opcodes.DLOAD:
                return 2;
            case Opcodes.ISTORE:
            case Opcodes.FSTORE:
            case Opcodes.ASTORE:
                return -1;
            case Opcodes.LSTORE:
            case Opcodes.DSTORE:
                return -2;
            case Opcodes.IALOAD:
            case Opcodes.FALOAD:
            case Opcodes.AALOAD:
            case Opcodes.BALOAD:
            case Opcodes.CALOAD:
            case Opcodes.SALOAD:
                return -1; // 2 pop, 1 push
            case Opcodes.LALOAD:
            case Opcodes.DALOAD:
                return 0; // 2 pop, 2 push
            case Opcodes.IASTORE:
            case Opcodes.FASTORE:
            case Opcodes.AASTORE:
            case Opcodes.BASTORE:
            case Opcodes.CASTORE:
            case Opcodes.SASTORE:
                return -3; // 3 pop
            case Opcodes.LASTORE:
            case Opcodes.DASTORE:
                return -4; // 4 pop
            case Opcodes.POP:
            case Opcodes.IRETURN:
            case Opcodes.FRETURN:
            case Opcodes.ARETURN:
                return -1;
            case Opcodes.POP2:
            case Opcodes.LRETURN:
            case Opcodes.DRETURN:
                return -2;
            case Opcodes.DUP:
                return 1;
            case Opcodes.DUP_X1:
            case Opcodes.DUP_X2:
                return 0;
            case Opcodes.DUP2:
                return 2;
            case Opcodes.DUP2_X1:
            case Opcodes.DUP2_X2:
                return 0;
            case Opcodes.SWAP:
                return 0;
            case Opcodes.IADD:
            case Opcodes.FADD:
            case Opcodes.ISUB:
            case Opcodes.FSUB:
            case Opcodes.IMUL:
            case Opcodes.FMUL:
            case Opcodes.IDIV:
            case Opcodes.FDIV:
            case Opcodes.IREM:
            case Opcodes.FREM:
            case Opcodes.ISHL:
            case Opcodes.ISHR:
            case Opcodes.IUSHR:
            case Opcodes.IAND:
            case Opcodes.IOR:
            case Opcodes.IXOR:
            case Opcodes.LCMP:
            case Opcodes.FCMPL:
            case Opcodes.FCMPG:
            case Opcodes.IFEQ:
            case Opcodes.IFNE:
            case Opcodes.IFLT:
            case Opcodes.IFGE:
            case Opcodes.IFGT:
            case Opcodes.IFLE:
            case Opcodes.IF_ICMPEQ:
            case Opcodes.IF_ICMPNE:
            case Opcodes.IF_ICMPLT:
            case Opcodes.IF_ICMPGE:
            case Opcodes.IF_ICMPGT:
            case Opcodes.IF_ICMPLE:
            case Opcodes.IF_ACMPEQ:
            case Opcodes.IF_ACMPNE:
            case Opcodes.GOTO:
            case Opcodes.JSR:
            case Opcodes.IFNULL:
            case Opcodes.IFNONNULL:
                return -1; // 2 pop (or 1 pop for GOTO)
            case Opcodes.LADD:
            case Opcodes.DADD:
            case Opcodes.LSUB:
            case Opcodes.DSUB:
            case Opcodes.LMUL:
            case Opcodes.DMUL:
            case Opcodes.LDIV:
            case Opcodes.DDIV:
            case Opcodes.LREM:
            case Opcodes.DREM:
                return -2; // 4 pop, 2 push
            case Opcodes.LSHL:
            case Opcodes.LSHR:
            case Opcodes.LUSHR:
            case Opcodes.LAND:
            case Opcodes.LOR:
            case Opcodes.LXOR:
                return -1; // 3 pop, 2 push
            case Opcodes.INEG:
            case Opcodes.FNEG:
            case Opcodes.LNEG:
            case Opcodes.DNEG:
            case Opcodes.I2F:
            case Opcodes.I2L:
            case Opcodes.I2D:
            case Opcodes.F2I:
            case Opcodes.F2L:
            case Opcodes.F2D:
            case Opcodes.L2I:
            case Opcodes.L2F:
            case Opcodes.L2D:
            case Opcodes.D2I:
            case Opcodes.D2F:
            case Opcodes.D2L:
            case Opcodes.I2B:
            case Opcodes.I2C:
            case Opcodes.I2S:
                return 0; // 1 pop, 1 push
            case Opcodes.TABLESWITCH:
            case Opcodes.LOOKUPSWITCH:
                return -1;
            case Opcodes.ATHROW:
                return -1;
            case Opcodes.MONITORENTER:
            case Opcodes.MONITOREXIT:
                return -1;
            case Opcodes.INVOKEVIRTUAL:
            case Opcodes.INVOKESPECIAL:
            case Opcodes.INVOKESTATIC:
            case Opcodes.INVOKEINTERFACE:
                return getInvokeStackChange((MethodInsnNode) insn);
            case Opcodes.INVOKEDYNAMIC:
                return getInvokeDynamicStackChange((InvokeDynamicInsnNode) insn);
            case Opcodes.GETSTATIC:
                return getFieldStackChange((FieldInsnNode) insn, 1);
            case Opcodes.PUTSTATIC:
                return getFieldStackChange((FieldInsnNode) insn, -1);
            case Opcodes.GETFIELD:
                return getFieldStackChange((FieldInsnNode) insn, 0);
            case Opcodes.PUTFIELD:
                return getFieldStackChange((FieldInsnNode) insn, -2);
            case Opcodes.NEW:
                return 1;
            case Opcodes.NEWARRAY:
            case Opcodes.ANEWARRAY:
            case Opcodes.ARRAYLENGTH:
            case Opcodes.CHECKCAST:
            case Opcodes.INSTANCEOF:
                return 0;
            case Opcodes.MULTIANEWARRAY:
                MultiANewArrayInsnNode multi = (MultiANewArrayInsnNode) insn;
                return 1 - multi.dims;
            default:
                return 0;
        }
    }

    private static int getInvokeStackChange(MethodInsnNode insn) {
        Type methodType = Type.getMethodType(insn.desc);
        int argsSize = 0;
        for (Type argType : methodType.getArgumentTypes()) {
            argsSize += argType.getSize();
        }
        int returnTypeSize = methodType.getReturnType().getSize();

        if (insn.getOpcode() != Opcodes.INVOKESTATIC) {
            argsSize += 1;
        }

        return returnTypeSize - argsSize;
    }

    private static int getInvokeDynamicStackChange(InvokeDynamicInsnNode insn) {
        Type methodType = Type.getMethodType(insn.desc);
        int argsSize = 0;
        for (Type argType : methodType.getArgumentTypes()) {
            argsSize += argType.getSize();
        }
        int returnTypeSize = methodType.getReturnType().getSize();
        return returnTypeSize - argsSize;
    }

    private static int getFieldStackChange(FieldInsnNode insn, int baseChange) {
        Type fieldType = Type.getType(insn.desc);
        return baseChange + fieldType.getSize();
    }

    public static void insertAtHead(MethodNode method, InsnList insns) {
        AbstractInsnNode first = method.instructions.getFirst();
        if (first != null) {
            method.instructions.insertBefore(first, insns);
        } else {
            method.instructions.add(insns);
        }
    }

    public static void insertBeforeReturn(MethodNode method, InsnList insns) {
        for (AbstractInsnNode insn : method.instructions) {
            int opcode = insn.getOpcode();
            if (opcode == Opcodes.IRETURN || opcode == Opcodes.FRETURN ||
                    opcode == Opcodes.ARETURN || opcode == Opcodes.LRETURN ||
                    opcode == Opcodes.DRETURN || opcode == Opcodes.RETURN) {
                method.instructions.insertBefore(insn, insns);
            }
        }
    }

    public static List<AbstractInsnNode> findInstructionsByOpcode(MethodNode method, int opcode) {
        List<AbstractInsnNode> result = new java.util.ArrayList<>();
        for (AbstractInsnNode insn : method.instructions) {
            if (insn.getOpcode() == opcode) {
                result.add(insn);
            }
        }
        return result;
    }

    public static List<MethodInsnNode> findMethodCalls(MethodNode method, String owner, String name, String desc) {
        List<MethodInsnNode> result = new java.util.ArrayList<>();
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof MethodInsnNode methodInsn) {
                if (owner.equals(methodInsn.owner) &&
                        (name.equals(methodInsn.name)) &&
                        desc.equals(methodInsn.desc)) {
                    result.add(methodInsn);
                }
            }
        }
        return result;
    }

    public static int getLoadOpcode(Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                return Opcodes.ILOAD;
            case Type.FLOAT:
                return Opcodes.FLOAD;
            case Type.LONG:
                return Opcodes.LLOAD;
            case Type.DOUBLE:
                return Opcodes.DLOAD;
            case Type.ARRAY:
            case Type.OBJECT:
            default:
                return Opcodes.ALOAD;
        }
    }

    public static int getStoreOpcode(Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                return Opcodes.ISTORE;
            case Type.FLOAT:
                return Opcodes.FSTORE;
            case Type.LONG:
                return Opcodes.LSTORE;
            case Type.DOUBLE:
                return Opcodes.DSTORE;
            case Type.ARRAY:
            case Type.OBJECT:
            default:
                return Opcodes.ASTORE;
        }
    }

    public static int getReturnOpcode(Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                return Opcodes.IRETURN;
            case Type.FLOAT:
                return Opcodes.FRETURN;
            case Type.LONG:
                return Opcodes.LRETURN;
            case Type.DOUBLE:
                return Opcodes.DRETURN;
            case Type.VOID:
                return Opcodes.RETURN;
            case Type.ARRAY:
            case Type.OBJECT:
            default:
                return Opcodes.ARETURN;
        }
    }

    public static AbstractInsnNode loadConstant(Object value) {
        if (value == null) {
            return new InsnNode(Opcodes.ACONST_NULL);
        } else if (value instanceof Integer) {
            int i = (Integer) value;
            if (i >= -1 && i <= 5) {
                return new InsnNode(Opcodes.ICONST_0 + i);
            } else if (i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE) {
                return new IntInsnNode(Opcodes.BIPUSH, i);
            } else if (i >= Short.MIN_VALUE && i <= Short.MAX_VALUE) {
                return new IntInsnNode(Opcodes.SIPUSH, i);
            } else {
                return new LdcInsnNode(i);
            }
        } else if (value instanceof Float) {
            float f = (Float) value;
            if (f == 0.0f) {
                return new InsnNode(Opcodes.FCONST_0);
            } else if (f == 1.0f) {
                return new InsnNode(Opcodes.FCONST_1);
            } else if (f == 2.0f) {
                return new InsnNode(Opcodes.FCONST_2);
            } else {
                return new LdcInsnNode(f);
            }
        } else if (value instanceof Long) {
            long l = (Long) value;
            if (l == 0L) {
                return new InsnNode(Opcodes.LCONST_0);
            } else if (l == 1L) {
                return new InsnNode(Opcodes.LCONST_1);
            } else {
                return new LdcInsnNode(l);
            }
        } else if (value instanceof Double) {
            double d = (Double) value;
            if (d == 0.0) {
                return new InsnNode(Opcodes.DCONST_0);
            } else if (d == 1.0) {
                return new InsnNode(Opcodes.DCONST_1);
            } else {
                return new LdcInsnNode(d);
            }
        } else if (value instanceof String) {
            return new LdcInsnNode(value);
        } else {
            return new LdcInsnNode(value);
        }
    }

    public static void updateMaxStack(MethodNode method, int additional) {
        method.maxStack = Math.max(method.maxStack, method.maxStack + additional);
    }
}
