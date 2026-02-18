package com.test.nosugar.coremod.transformers;

import com.test.nosugar.NoSugar;
import com.test.sugarlib.api.SugarTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;
import java.util.Collections;

public class LivingEntityGetHealthTransformer extends SugarTransformer {

    public LivingEntityGetHealthTransformer() {
        super(NoSugar.MODID + ".LivingEntityGetHealthTransformer");
    }

    @Override
    public Set<Target> targets() {
        return Collections.singleton(Target.targetClass("net.minecraft.world.entity.LivingEntity"));
    }

    @Override
    protected void applyTransformations(ClassNode node) {
        for (MethodNode method : node.methods) {
            if ("m_21223_".equals(method.name) && "()F".equals(method.desc)) {
                System.out.println("[NoSugar CoreMod] Transforming getHealth method (obfuscated: " + method.name + ") in " + node.name);
                injectGetHealthLogic(method, node.name);
                break;
            }
        }
    }

    private void injectGetHealthLogic(MethodNode method, String ownerClassName) {
        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new TypeInsnNode(Opcodes.INSTANCEOF, "com/test/nosugar/utils/interfaces/ILivingEntity"));
        LabelNode labelSkip = new LabelNode();
        insnList.add(new JumpInsnNode(Opcodes.IFEQ, labelSkip));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new TypeInsnNode(Opcodes.CHECKCAST, "com/test/nosugar/utils/interfaces/ILivingEntity"));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ownerClassName, "getUUID", "()Ljava/util/UUID;", false));
        insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "com/test/nosugar/utils/interfaces/ILivingEntity", "isErased", "(Ljava/util/UUID;)Z", true));
        insnList.add(new JumpInsnNode(Opcodes.IFEQ, labelSkip));
        insnList.add(new LdcInsnNode(0.0F));
        insnList.add(new InsnNode(Opcodes.FRETURN));
        insnList.add(labelSkip);
        method.instructions.insert(insnList);
    }
}