package com.test.nosugar.coremod;

import com.test.nosugar.coremod.transformers.LivingEntityGetHealthTransformer;
import com.test.sugarlib.api.SugarTransformer;
import com.test.sugarlib.api.SugarTransformerService;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoSugarTransformerService implements SugarTransformerService {

    private final List<SugarTransformer> childTransformers;

    public NoSugarTransformerService() {
        this.childTransformers = new ArrayList<>();
        this.childTransformers.add(new LivingEntityGetHealthTransformer());
    }

    @Override
    public String getServiceName() {
        return "NoSugarTransformerService";
    }

    @Override
    public Set<Target> targets() {
        Set<Target> allTargets = new HashSet<>();
        for (SugarTransformer t : childTransformers) {
            allTargets.addAll(t.targets());
        }
        return allTargets;
    }

    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        System.out.println("[NoSugar Transformer] loaded");
        String className = context.getClassName();
        for (SugarTransformer transformer : childTransformers) {
            if (transformer.targets().stream().anyMatch(t -> t.getClassName().equals(className))) {
                input = transformer.transform(input, context);
            }
        }
        return input;
    }
}