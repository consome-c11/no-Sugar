package com.test.nosugar.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsmUtil {
    private static final Map<Integer, String> OPCODE_NAMES = new HashMap<>();

    static {
        // Constants
        OPCODE_NAMES.put(Opcodes.NOP, "NOP");
        OPCODE_NAMES.put(Opcodes.ACONST_NULL, "ACONST_NULL");
        OPCODE_NAMES.put(Opcodes.ICONST_M1, "ICONST_M1");
        OPCODE_NAMES.put(Opcodes.ICONST_0, "ICONST_0");
        OPCODE_NAMES.put(Opcodes.ICONST_1, "ICONST_1");
        OPCODE_NAMES.put(Opcodes.ICONST_2, "ICONST_2");
        OPCODE_NAMES.put(Opcodes.ICONST_3, "ICONST_3");
        OPCODE_NAMES.put(Opcodes.ICONST_4, "ICONST_4");
        OPCODE_NAMES.put(Opcodes.ICONST_5, "ICONST_5");
        OPCODE_NAMES.put(Opcodes.LCONST_0, "LCONST_0");
        OPCODE_NAMES.put(Opcodes.LCONST_1, "LCONST_1");
        OPCODE_NAMES.put(Opcodes.FCONST_0, "FCONST_0");
        OPCODE_NAMES.put(Opcodes.FCONST_1, "FCONST_1");
        OPCODE_NAMES.put(Opcodes.FCONST_2, "FCONST_2");
        OPCODE_NAMES.put(Opcodes.DCONST_0, "DCONST_0");
        OPCODE_NAMES.put(Opcodes.DCONST_1, "DCONST_1");
        OPCODE_NAMES.put(Opcodes.BIPUSH, "BIPUSH");
        OPCODE_NAMES.put(Opcodes.SIPUSH, "SIPUSH");
        OPCODE_NAMES.put(Opcodes.LDC, "LDC");

        // Loads
        OPCODE_NAMES.put(Opcodes.ILOAD, "ILOAD");
        OPCODE_NAMES.put(Opcodes.LLOAD, "LLOAD");
        OPCODE_NAMES.put(Opcodes.FLOAD, "FLOAD");
        OPCODE_NAMES.put(Opcodes.DLOAD, "DLOAD");
        OPCODE_NAMES.put(Opcodes.ALOAD, "ALOAD");
        OPCODE_NAMES.put(Opcodes.IALOAD, "IALOAD");
        OPCODE_NAMES.put(Opcodes.LALOAD, "LALOAD");
        OPCODE_NAMES.put(Opcodes.FALOAD, "FALOAD");
        OPCODE_NAMES.put(Opcodes.DALOAD, "DALOAD");
        OPCODE_NAMES.put(Opcodes.AALOAD, "AALOAD");
        OPCODE_NAMES.put(Opcodes.BALOAD, "BALOAD");
        OPCODE_NAMES.put(Opcodes.CALOAD, "CALOAD");
        OPCODE_NAMES.put(Opcodes.SALOAD, "SALOAD");

        // Stores
        OPCODE_NAMES.put(Opcodes.ISTORE, "ISTORE");
        OPCODE_NAMES.put(Opcodes.LSTORE, "LSTORE");
        OPCODE_NAMES.put(Opcodes.FSTORE, "FSTORE");
        OPCODE_NAMES.put(Opcodes.DSTORE, "DSTORE");
        OPCODE_NAMES.put(Opcodes.ASTORE, "ASTORE");
        OPCODE_NAMES.put(Opcodes.IASTORE, "IASTORE");
        OPCODE_NAMES.put(Opcodes.LASTORE, "LASTORE");
        OPCODE_NAMES.put(Opcodes.FASTORE, "FASTORE");
        OPCODE_NAMES.put(Opcodes.DASTORE, "DASTORE");
        OPCODE_NAMES.put(Opcodes.AASTORE, "AASTORE");
        OPCODE_NAMES.put(Opcodes.BASTORE, "BASTORE");
        OPCODE_NAMES.put(Opcodes.CASTORE, "CASTORE");
        OPCODE_NAMES.put(Opcodes.SASTORE, "SASTORE");

        // Stack
        OPCODE_NAMES.put(Opcodes.POP, "POP");
        OPCODE_NAMES.put(Opcodes.POP2, "POP2");
        OPCODE_NAMES.put(Opcodes.DUP, "DUP");
        OPCODE_NAMES.put(Opcodes.DUP_X1, "DUP_X1");
        OPCODE_NAMES.put(Opcodes.DUP_X2, "DUP_X2");
        OPCODE_NAMES.put(Opcodes.DUP2, "DUP2");
        OPCODE_NAMES.put(Opcodes.DUP2_X1, "DUP2_X1");
        OPCODE_NAMES.put(Opcodes.DUP2_X2, "DUP2_X2");
        OPCODE_NAMES.put(Opcodes.SWAP, "SWAP");

        // Math
        OPCODE_NAMES.put(Opcodes.IADD, "IADD");
        OPCODE_NAMES.put(Opcodes.LADD, "LADD");
        OPCODE_NAMES.put(Opcodes.FADD, "FADD");
        OPCODE_NAMES.put(Opcodes.DADD, "DADD");
        OPCODE_NAMES.put(Opcodes.ISUB, "ISUB");
        OPCODE_NAMES.put(Opcodes.LSUB, "LSUB");
        OPCODE_NAMES.put(Opcodes.FSUB, "FSUB");
        OPCODE_NAMES.put(Opcodes.DSUB, "DSUB");
        OPCODE_NAMES.put(Opcodes.IMUL, "IMUL");
        OPCODE_NAMES.put(Opcodes.LMUL, "LMUL");
        OPCODE_NAMES.put(Opcodes.FMUL, "FMUL");
        OPCODE_NAMES.put(Opcodes.DMUL, "DMUL");
        OPCODE_NAMES.put(Opcodes.IDIV, "IDIV");
        OPCODE_NAMES.put(Opcodes.LDIV, "LDIV");
        OPCODE_NAMES.put(Opcodes.FDIV, "FDIV");
        OPCODE_NAMES.put(Opcodes.DDIV, "DDIV");
        OPCODE_NAMES.put(Opcodes.IREM, "IREM");
        OPCODE_NAMES.put(Opcodes.LREM, "LREM");
        OPCODE_NAMES.put(Opcodes.FREM, "FREM");
        OPCODE_NAMES.put(Opcodes.DREM, "DREM");
        OPCODE_NAMES.put(Opcodes.INEG, "INEG");
        OPCODE_NAMES.put(Opcodes.LNEG, "LNEG");
        OPCODE_NAMES.put(Opcodes.FNEG, "FNEG");
        OPCODE_NAMES.put(Opcodes.DNEG, "DNEG");

        // Shifts
        OPCODE_NAMES.put(Opcodes.ISHL, "ISHL");
        OPCODE_NAMES.put(Opcodes.LSHL, "LSHL");
        OPCODE_NAMES.put(Opcodes.ISHR, "ISHR");
        OPCODE_NAMES.put(Opcodes.LSHR, "LSHR");
        OPCODE_NAMES.put(Opcodes.IUSHR, "IUSHR");
        OPCODE_NAMES.put(Opcodes.LUSHR, "LUSHR");

        // Bit
        OPCODE_NAMES.put(Opcodes.IAND, "IAND");
        OPCODE_NAMES.put(Opcodes.LAND, "LAND");
        OPCODE_NAMES.put(Opcodes.IOR, "IOR");
        OPCODE_NAMES.put(Opcodes.LOR, "LOR");
        OPCODE_NAMES.put(Opcodes.IXOR, "IXOR");
        OPCODE_NAMES.put(Opcodes.LXOR, "LXOR");

        // Conversions
        OPCODE_NAMES.put(Opcodes.I2L, "I2L");
        OPCODE_NAMES.put(Opcodes.I2F, "I2F");
        OPCODE_NAMES.put(Opcodes.I2D, "I2D");
        OPCODE_NAMES.put(Opcodes.L2I, "L2I");
        OPCODE_NAMES.put(Opcodes.L2F, "L2F");
        OPCODE_NAMES.put(Opcodes.L2D, "L2D");
        OPCODE_NAMES.put(Opcodes.F2I, "F2I");
        OPCODE_NAMES.put(Opcodes.F2L, "F2L");
        OPCODE_NAMES.put(Opcodes.F2D, "F2D");
        OPCODE_NAMES.put(Opcodes.D2I, "D2I");
        OPCODE_NAMES.put(Opcodes.D2L, "D2L");
        OPCODE_NAMES.put(Opcodes.D2F, "D2F");
        OPCODE_NAMES.put(Opcodes.I2B, "I2B");
        OPCODE_NAMES.put(Opcodes.I2C, "I2C");
        OPCODE_NAMES.put(Opcodes.I2S, "I2S");

        // Comparisons
        OPCODE_NAMES.put(Opcodes.LCMP, "LCMP");
        OPCODE_NAMES.put(Opcodes.FCMPL, "FCMPL");
        OPCODE_NAMES.put(Opcodes.FCMPG, "FCMPG");
        OPCODE_NAMES.put(Opcodes.DCMPL, "DCMPL");
        OPCODE_NAMES.put(Opcodes.DCMPG, "DCMPG");

        // Control
        OPCODE_NAMES.put(Opcodes.IFEQ, "IFEQ");
        OPCODE_NAMES.put(Opcodes.IFNE, "IFNE");
        OPCODE_NAMES.put(Opcodes.IFLT, "IFLT");
        OPCODE_NAMES.put(Opcodes.IFGE, "IFGE");
        OPCODE_NAMES.put(Opcodes.IFGT, "IFGT");
        OPCODE_NAMES.put(Opcodes.IFLE, "IFLE");
        OPCODE_NAMES.put(Opcodes.IF_ICMPEQ, "IF_ICMPEQ");
        OPCODE_NAMES.put(Opcodes.IF_ICMPNE, "IF_ICMPNE");
        OPCODE_NAMES.put(Opcodes.IF_ICMPLT, "IF_ICMPLT");
        OPCODE_NAMES.put(Opcodes.IF_ICMPGE, "IF_ICMPGE");
        OPCODE_NAMES.put(Opcodes.IF_ICMPGT, "IF_ICMPGT");
        OPCODE_NAMES.put(Opcodes.IF_ICMPLE, "IF_ICMPLE");
        OPCODE_NAMES.put(Opcodes.IF_ACMPEQ, "IF_ACMPEQ");
        OPCODE_NAMES.put(Opcodes.IF_ACMPNE, "IF_ACMPNE");
        OPCODE_NAMES.put(Opcodes.GOTO, "GOTO");
        OPCODE_NAMES.put(Opcodes.JSR, "JSR");
        OPCODE_NAMES.put(Opcodes.RET, "RET");
        OPCODE_NAMES.put(Opcodes.TABLESWITCH, "TABLESWITCH");
        OPCODE_NAMES.put(Opcodes.LOOKUPSWITCH, "LOOKUPSWITCH");
        OPCODE_NAMES.put(Opcodes.IRETURN, "IRETURN");
        OPCODE_NAMES.put(Opcodes.LRETURN, "LRETURN");
        OPCODE_NAMES.put(Opcodes.FRETURN, "FRETURN");
        OPCODE_NAMES.put(Opcodes.DRETURN, "DRETURN");
        OPCODE_NAMES.put(Opcodes.ARETURN, "ARETURN");
        OPCODE_NAMES.put(Opcodes.RETURN, "RETURN");

        // Fields
        OPCODE_NAMES.put(Opcodes.GETSTATIC, "GETSTATIC");
        OPCODE_NAMES.put(Opcodes.PUTSTATIC, "PUTSTATIC");
        OPCODE_NAMES.put(Opcodes.GETFIELD, "GETFIELD");
        OPCODE_NAMES.put(Opcodes.PUTFIELD, "PUTFIELD");

        // Methods
        OPCODE_NAMES.put(Opcodes.INVOKEVIRTUAL, "INVOKEVIRTUAL");
        OPCODE_NAMES.put(Opcodes.INVOKESPECIAL, "INVOKESPECIAL");
        OPCODE_NAMES.put(Opcodes.INVOKESTATIC, "INVOKESTATIC");
        OPCODE_NAMES.put(Opcodes.INVOKEINTERFACE, "INVOKEINTERFACE");
        OPCODE_NAMES.put(Opcodes.INVOKEDYNAMIC, "INVOKEDYNAMIC");

        // Objects
        OPCODE_NAMES.put(Opcodes.NEW, "NEW");
        OPCODE_NAMES.put(Opcodes.NEWARRAY, "NEWARRAY");
        OPCODE_NAMES.put(Opcodes.ANEWARRAY, "ANEWARRAY");
        OPCODE_NAMES.put(Opcodes.ARRAYLENGTH, "ARRAYLENGTH");
        OPCODE_NAMES.put(Opcodes.ATHROW, "ATHROW");
        OPCODE_NAMES.put(Opcodes.CHECKCAST, "CHECKCAST");
        OPCODE_NAMES.put(Opcodes.INSTANCEOF, "INSTANCEOF");

        // Monitor
        OPCODE_NAMES.put(Opcodes.MONITORENTER, "MONITORENTER");
        OPCODE_NAMES.put(Opcodes.MONITOREXIT, "MONITOREXIT");

        // Others
        OPCODE_NAMES.put(Opcodes.MULTIANEWARRAY, "MULTIANEWARRAY");
        OPCODE_NAMES.put(Opcodes.IFNULL, "IFNULL");
        OPCODE_NAMES.put(Opcodes.IFNONNULL, "IFNONNULL");
    }

    public static String getOpcodeName(int opcode) {
        if (opcode < 0) return null;
        return OPCODE_NAMES.getOrDefault(opcode, "OPCODE_" + opcode);
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
