package com.test.nosugar_coremod.methodvisitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class GetHealthModifyMethodVisitor extends MethodVisitor {

    private final String targetClassName;

    public GetHealthModifyMethodVisitor(MethodVisitor mv, String targetClassName) {
        super(Opcodes.ASM9, mv);
        this.targetClassName = targetClassName;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        //injectConditionCheck();

    }

    private void injectConditionCheck() {
        mv.visitVarInsn(Opcodes.ALOAD, 0);

        // instanceof ILivingEntity
        mv.visitTypeInsn(Opcodes.INSTANCEOF, "com/test/nosugar/utils/interfaces/ILivingEntity");

        Label skipLabel = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, skipLabel);

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/world/entity/LivingEntity");

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "com/test/nosugar/utils/interfaces/ILivingEntity");

        // iliving.isErased(self.getUUID())
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, targetClassName, "getUUID", "()Ljava/util/UUID;", false);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/test/nosugar/utils/interfaces/ILivingEntity", "isErased", "(Ljava/util/UUID;)Z", true);

        mv.visitJumpInsn(Opcodes.IFEQ, skipLabel);

        // return 0.0F
        mv.visitLdcInsn(0.0F);
        mv.visitInsn(Opcodes.FRETURN);

        mv.visitLabel(skipLabel);
    }
}