package com.test.nosugar.entity;

import com.mojang.authlib.GameProfile;
import com.test.nosugar.entity.goal.AiTaskSelector;
import com.test.nosugar.entity.goal.FindTargetTask;
import com.test.nosugar.entity.goal.FlyToTargetTask;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class Valine3xg extends Player {
    private static final ItemStack EMPTY_ITEM = ItemStack.EMPTY;
    public final float MAX_HEALTH = 20.f;
    public float HP = 20.f;
    private AiTaskSelector taskSelector;

    public Valine3xg(BlockPos pos, Level level, GameProfile profile) {
        super(level,pos, 1.f, profile);

        createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, 9999.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.5D)
                .add(Attributes.FLYING_SPEED, 200.0D)
                .add(Attributes.ARMOR, Integer.MAX_VALUE)
                .add(Attributes.ARMOR_TOUGHNESS, Integer.MAX_VALUE)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);

        initAiSystem();
        this.setPos(pos.getX(), pos.getY(), pos.getZ());
    }

    private void initAiSystem() {
        this.taskSelector = new AiTaskSelector(this);
        this.taskSelector.addTask(0, new FindTargetTask());
        this.taskSelector.addTask(1, new FlyToTargetTask());
    }

    @Override
    public void tick() {
        super.tick();
        this.taskSelector.tick();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return EMPTY_ITEM;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    public void addDamage(float damage) {
        HP -= damage;
        this.hurtTime = 10;
    }
}