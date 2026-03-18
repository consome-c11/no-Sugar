package com.test.nosugar.items;

import com.test.nosugar.additional.ModTiers;
import com.test.nosugar.client.renderer.SugarSwordItemRenderProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

import static com.test.nosugar.utils.item.Eraser_Utils.killIfParentFound;
import static com.test.nosugar.utils.render.ColorUtils.makeWaveLine;

public class SugarSword_Item extends SwordItem {
    public SugarSword_Item(Properties props) {
        super(ModTiers.ERASER_TIER, 10, 7.F, props.stacksTo(1).fireResistant());
    }

    public static BlockHitResult getPlayerLookingAt(Player player, int reach) {
        Level level = player.level();

        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle().scale(reach);
        Vec3 endPosition = eyePosition.add(lookVector);

        ClipContext context = new ClipContext(
                eyePosition,
                endPosition,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                player
        );

        return level.clip(context);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
        killIfParentFound(target, player, 32);
        if (!(target instanceof LivingEntity)) target.kill();
        return true;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            if (player.isSleeping()) {
                return false;
            }
            HitResult hitResult = player.pick(5, 1.0f, true);

            if (!player.level().getBlockState(getPlayerLookingAt(player, 7).getBlockPos()).isAir() || hitResult.getType() == HitResult.Type.ENTITY)
                return false;
            List<Entity> entities = findEntitiesInCone(player, 3.5, 45.0);

            for (Entity ent : entities) {
                if (ent instanceof LivingEntity living) {
                    killIfParentFound(living, player, 32);
                }
            }
            player.sweepAttack();
            player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS,
                    1.0F, 1.0F);
            return true;
        }
        return false;
    }

    private List<Entity> findEntitiesInCone(Player player, double radius, double angle) {
        Level level = player.level();
        return level.getEntities(player, player.getBoundingBox().inflate(radius), entity -> {
            if (entity == player) return false;

            double distanceSq = player.distanceToSqr(entity);
            if (distanceSq > radius * radius) return false;

            Vec3 playerDir = player.getLookAngle().normalize();
            Vec3 toEntity = new Vec3(
                    entity.getX() - player.getX(),
                    entity.getY() - player.getY(),
                    entity.getZ() - player.getZ()
            );

            double dotProduct = playerDir.dot(toEntity);
            dotProduct = Math.max(-1.0, Math.min(1.0, dotProduct));
            double angleBetween = Math.acos(dotProduct) * 180 / Math.PI;

            return angleBetween <= angle;
        });
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        killIfParentFound(target, attacker, 32);
        return true;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new SugarSwordItemRenderProperties());
    }

    @Override
    public Component getName(ItemStack stack) {
        String text = Component.translatable("item.nosugar.sugar_sword.name").getString();
        return makeWaveLine(text, true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String desc = Component.translatable("item.nosuger.sugar_sword.desc").getString();
        String desc2 = "Fortune VII";

        tooltip.add(1, makeWaveLine(desc, 0xFFAAAAAA, 0xFFFFFFFF));
        tooltip.add(2, makeWaveLine(desc2, 0xFFAAAAAA, 0xFFFFFFFF));
    }
}