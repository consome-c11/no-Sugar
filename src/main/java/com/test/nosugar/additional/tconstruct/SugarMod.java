package com.test.nosugar.additional.tconstruct;

import com.mochi_753.eraser.util.EraserHandler;
import com.test.nosugar.utils.ColorUtils;
import com.test.nosugar.utils.Eraser_Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

public class SugarMod extends NoLevelsModifier implements MeleeHitModifierHook, ProjectileHitModifierHook {
    @Override
    public @NotNull Component getDisplayName() {
        return ColorUtils.makeWaveLine("Sugar");
    }

    @Override
    public @NotNull Component getDisplayName(int Level) {
        return this.getDisplayName();
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT);
    }

    @Override
    public float beforeMeleeHit(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        if (!context.getLevel().isClientSide() && context.getAttacker() instanceof Player player && tool.hasTag(TinkerTags.Items.MELEE_PRIMARY)) {
            LivingEntity target = context.getLivingTarget();
            if (target != null) {
                Eraser_Utils.killIfParentFound(target,player,16);
            }

        }
        return MeleeHitModifierHook.super.beforeMeleeHit(tool, modifier, context, damage, baseKnockback, knockback);
    }

    @Override
    public boolean onProjectileHitEntity(@NotNull ModifierNBT modifiers, @NotNull ModDataNBT persistentData, @NotNull ModifierEntry modifier, @NotNull Projectile projectile, @NotNull EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (!attacker.level().isClientSide()) {
            if (hit.getEntity() instanceof LivingEntity entity && attacker instanceof Player player) {
                Eraser_Utils.killIfParentFound(target,player,16);
            }
        }
        return ProjectileHitModifierHook.super.onProjectileHitEntity(modifiers, persistentData, modifier, projectile, hit, attacker, target);
    }
}