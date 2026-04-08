package com.test.nosugar.items;

import com.test.nosugar.additional.ModTiers;
import com.test.nosugar.client.renderer.SugarSwordItemRenderProperties;
import com.test.nosugar.utils.entity.EntityUtils;
import com.test.nosugar.utils.render.ColorUtils;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.test.nosugar.utils.item.Eraser_Utils.killIfParentFound;
import static com.test.nosugar.utils.render.ColorUtils.makeWaveLine;

//これ見てる人へ v2で実装予定だから他の人には言わんでくれ
public class NoSugar_Item extends SwordItem {

    public NoSugar_Item(Properties props) {
        super(ModTiers.ERASER_TIER, 10, 7.F, props.stacksTo(1).fireResistant());
    }

    public static HitResult getPlayerLookingAt(Player player, int reach) {
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

            if (!player.level().getBlockState(BlockPos.containing(getPlayerLookingAt(player, 7).getLocation())).isAir() || hitResult.getType() == HitResult.Type.ENTITY)
                return false;
            List<Entity> entities = findEntitiesInCone(player, 3.5, 45.0);

            for (Entity ent : entities) {
                if (ent instanceof LivingEntity living) {
                    killIfParentFound(living, player, 32);
                }
            }
            if(hitResult.getType() != HitResult.Type.BLOCK) {
                player.sweepAttack();
                player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            return true;
        }
        return false;
    }

    private List<Entity> findEntitiesInCone(Player player, double radius, double angle) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return List.of();

        List<Entity> allEntities = EntityUtils.getEntities(serverLevel);
        List<Entity> result = new ArrayList<>();

        Vec3 playerPos = player.position();
        Vec3 playerDir = player.getLookAngle().normalize();

        for (Entity entity : allEntities) {
            if (entity == player) {
                continue;
            }
            double distanceSq = playerPos.distanceToSqr(entity.position());
            if (distanceSq > radius * radius) {
                continue;
            }
            Vec3 toEntity = entity.position().subtract(playerPos);
            double dotProduct = playerDir.dot(toEntity.normalize());
            dotProduct = Math.max(-1.0, Math.min(1.0, dotProduct));
            double angleBetween = Math.acos(dotProduct) * 180 / Math.PI;

            if (angleBetween <= angle) {
                result.add(entity);
            }
        }

        return result;
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
        String text = Component.literal("NoSugar").getString();
        return makeWaveLine(text, true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String desc = Component.literal("そなたが破壊すると言うならば、私は創造をしよう。").getString();
        String desc2 = "Fortune VII";

        tooltip.add(1, makeWaveLine(desc, 0xFFAAAAAA, 0xFFFFFFFF));
        tooltip.add(2, makeWaveLine(desc2, 0xFFAAAAAA, 0xFFFFFFFF));
    }

}