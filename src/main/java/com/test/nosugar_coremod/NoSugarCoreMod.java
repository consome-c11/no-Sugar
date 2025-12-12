package com.test.nosugar_coremod;

import com.test.nosugar.NoSugar;
import com.test.nosugar_coremod.classvisitor.LivingEntityTransformVisitor;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import net.minecraftforge.fml.common.Mod;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collections;
import java.util.Set;

public class NoSugarCoreMod implements ITransformer<ClassNode> {

    @Override
    public TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public Set<Target> targets() {
        return Collections.singleton(Target.targetClass("net.minecraft.world.entity.LivingEntity"));
    }

    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        System.out.println("[NoSugar CoreMod main] Transforming: " + input.name);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        LivingEntityTransformVisitor cv = new LivingEntityTransformVisitor(cw, input.name.replace('/', '.'));
        ClassNode original = input;
        if(cv == null) {
            System.out.println("[NoSugar CoreMod main] Transforming failed!: LivingEntityTransformVisitor is null");
            return input;
        }
        original.accept(cv);

        ClassReader cr = new ClassReader(cw.toByteArray());
        ClassNode newNode = new ClassNode();
        cr.accept(newNode, 0);
        return newNode;
        //return input;//test
    }
}