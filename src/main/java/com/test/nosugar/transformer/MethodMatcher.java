package com.test.nosugar.transformer;

import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodMatcher {

    private final String owner;
    private final String obfName;
    private final String mappedName;
    private final String desc;

    public MethodMatcher(String owner, String obfName, String mappedName, String desc) {
        this.owner = owner;
        this.obfName = obfName;
        this.mappedName = mappedName;
        this.desc = desc;
    }

    public boolean matches(MethodInsnNode insn) {
        if (!desc.equals(insn.desc)) return false;
        if (!obfName.equals(insn.name) && !mappedName.equals(insn.name)) return false;
        return owner.equals(insn.owner);
    }

    public boolean matches(MethodNode method) {
        //System.out.println("desc: " + method.desc + " name: " + method.name);
        if (!desc.equals(method.desc)) return false;
        if (!obfName.equals(method.name) && !mappedName.equals(method.name)) return false;
        return true;
    }

    public static MethodMatcher of(String owner, String obfName, String mappedName, String desc) {
        return new MethodMatcher(owner, obfName, mappedName, desc);
    }
}