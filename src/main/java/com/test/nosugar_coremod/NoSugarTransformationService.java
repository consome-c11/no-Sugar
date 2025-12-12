package com.test.nosugar_coremod;

import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IEnvironment;

import java.util.List;
import java.util.Set;

public class NoSugarTransformationService implements ITransformationService {

    @Override
    public String name() {
        return "nosugar-transformer";
    }

    @Override
    public void initialize(IEnvironment environment) {
        System.out.println("[NoSugar CoreMod-TransformationService] Service initialized.");
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {
        System.out.println("[NoSugar CoreMod-TransformationService] Service loaded.");
    }

    @Override
    public List<ITransformer> transformers() {
        return List.of(new NoSugarCoreMod());
    }
}