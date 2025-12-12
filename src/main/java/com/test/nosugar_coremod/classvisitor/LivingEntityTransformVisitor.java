package com.test.nosugar_coremod.classvisitor;

import com.test.nosugar_coremod.methodvisitor.GetHealthModifyMethodVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LivingEntityTransformVisitor extends ClassVisitor {

    private String targetClassName;

    public LivingEntityTransformVisitor(ClassVisitor cv, String targetClassName) {
        super(Opcodes.ASM9, cv);
        this.targetClassName = targetClassName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        //System.out.println("[NoSugar CoreMod] Found method: " + name + desc);
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ("m_21223_".equals(name) && "()F".equals(desc)) {
            System.out.println("[NoSugar CoreMod] Transforming getHealth method (obfuscated: " + name + ") in " + targetClassName);
            if(mv != null) {
                System.out.println("[NoSugar CoreMod] Transforming getHealth method real sigma");
                return new GetHealthModifyMethodVisitor(mv, targetClassName);
            }
            else System.out.println("[NoSugar CoreMod] super.visitMethod is null!");
        }

        return mv;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println("[NoSugar CoreMod] Visiting class: " + name +
                ", Super: " + superName +
                ", Interfaces: " + java.util.Arrays.toString(interfaces));

        super.visit(version, access, name, signature, superName, interfaces);
    }
}