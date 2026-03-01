package com.test.nosugar.transformer;

import com.test.nosugar.NoSugar;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.InputStream;
import java.util.Objects;

public class MethodMatcher {

    private final String owner;
    private final String obfName;
    private final String mappedName;
    private final String desc;
    private final boolean isInterface;

    public MethodMatcher(String owner, String obfName, String mappedName, String desc, boolean isInterface) {
        this.owner = owner;
        this.obfName = obfName;
        this.mappedName = mappedName;
        this.desc = desc;
        this.isInterface = isInterface;
    }

    public boolean matches(MethodInsnNode insn) {
        if (!desc.equals(insn.desc)) return false;
        if (!obfName.equals(insn.name) && !mappedName.equals(insn.name)) return false;
        return owner.equals(insn.owner);
    }

    public boolean matches(MethodNode method, String classname) {
        if (!desc.equals(method.desc) ||
                !obfName.equals(method.name) && !mappedName.equals(method.name)) return false;
        return isSubclass(classname, owner)/* || isOverride(method, classname)*/;
    }

    //粉みかんさん感謝
    //あとチャッピー
    public static boolean isSubclass(String className, String superClass) {
        if (className.equals(superClass) || superClass.equals("java/lang/Object")) {
            return true;
        }

        if (className.equals("java/lang/Object")) {
            return false;
        }

        String currentName = className;

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        while (!currentName.equals("java/lang/Object")) {
            try (InputStream is = classloader.getResourceAsStream(currentName + ".class")) {
                if(is == null) continue;
                ClassReader classreader = new ClassReader(is);
                currentName = classreader.getSuperName();
                if (currentName.equals(superClass)) {
                    NoSugar.LOGGER.debug("[NoSugar] sub Class Found: " + superClass);
                    return true;
                }
            } catch (Throwable e) {
                return false;
            }
        }

        return false;
    }



    public static MethodMatcher of(String owner, String obfName, String mappedName, String desc, boolean isInterface) {
        return new MethodMatcher(owner, obfName, mappedName, desc, isInterface);
    }
}