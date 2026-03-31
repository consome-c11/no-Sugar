package com.test.nosugar.events;

import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.additional.ModKeyBindings;
import com.test.nosugar.client.utils.RenderQueue;
import com.test.nosugar.items.SugarSword_Item;
import com.test.nosugar.network.PacketHandler;
import com.test.nosugar.network.packets.*;
import com.test.nosugar.transformer.event.AbilitiesFieldEvent;
import com.test.nosugar.utils.DestroyMode;
import com.test.nosugar.utils.Res;
import com.test.nosugar.utils.ShootMode;
import com.test.nosugar.utils.entity.EntityUtils;
import com.test.nosugar.utils.entity.FlyManager;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import com.test.nosugar.utils.item.BlessingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.function.Predicate;

import static com.test.nosugar.utils.DestroyBlock.QueueRenderBreakBlock;
import static com.test.nosugar.utils.render.RenderUtils.renderBlockList;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {
    public static int renderTime = 0;
    public static final List<Entity> erasedEntities = new ArrayList<>();
    private static final Map<UUID, Long> lastUpdate = new HashMap<>();
    private static int tick = 0;

    public static boolean hasBlessedTool() {
        Minecraft mc = Minecraft.getInstance();

        if (mc == null || mc.player == null || mc.level == null) {
            return false;
        }

        return isBlessedTool(mc.player.getMainHandItem()) ||
                isBlessedTool(mc.player.getOffhandItem());
    }

    private static boolean isBlessedTool(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        boolean isTool = stack.getItem() instanceof PickaxeItem ||
                stack.getItem() instanceof AxeItem ||
                stack.getItem() instanceof ShovelItem ||
                stack.getItem() instanceof HoeItem;

        if (!isTool) {
            return false;
        }
        CompoundTag tag = stack.getTag();
        return tag != null &&
                tag.contains("Blessing_of_Sugar", net.minecraft.nbt.Tag.TAG_BYTE) &&
                tag.getBoolean("Blessing_of_Sugar");
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc == null || mc.player == null || mc.level == null) return;
        HitResult hit = mc.hitResult;
        BlockPos pos = getPlayerLookingAt(mc.player, 5).getBlockPos();

        boolean same_id = DestroyMode.getMode(mc.player.getMainHandItem()) == DestroyMode.SAME_ID || DestroyMode.getMode(mc.player.getMainHandItem()) == DestroyMode.SAME_ID_ORE;
        Predicate<BlockState> accept = state -> !state.isAir();
        BlockState LookBlockState = mc.level.getBlockState(pos);
        if (DestroyMode.getMode(mc.player.getMainHandItem()) == DestroyMode.SAME_ID_ORE) {
            TagKey<Block> FORGE_ORES = BlockTags.create(Res.getResource("forge", "ores"));
            accept = state -> state.is(FORGE_ORES) || state.is(BlockTags.LOGS);
        } else if (DestroyMode.getMode(mc.player.getMainHandItem()) == DestroyMode.SAME_ID) {
            accept = state -> state.is(LookBlockState.getBlock());
        }
        if (DestroyMode.getMode(mc.player.getMainHandItem()) == DestroyMode.NORMAL || mc.player.getMainHandItem().getItem() != ModItems.WORLD_DESTROYER.get()) {
            RenderQueue.clear();
        } else
            QueueRenderBreakBlock(mc.level, mc.player, pos, DestroyMode.getMode(mc.player.getMainHandItem()), same_id, 128, accept);
        ItemStack stack = mc.player.getMainHandItem();
        if (stack.getItem() == ModItems.SUGAR_SWORD.get()) {
            if (mc.options.keyShift.isDown()) {
                double radius = 10.0;
                AABB area = mc.player.getBoundingBox().inflate(radius);

                List<LivingEntity> targets = mc.player.level().getEntitiesOfClass(
                        LivingEntity.class,
                        area,
                        e -> e != mc.player
                );
                //if(targets.isEmpty()) return;
                for (LivingEntity target : targets) {
                    //((Entity)target).setGlowingTag(true);
                    //System.out.println("Target: " + target.isCurrentlyGlowing());
                }
            }
        }

        if (isInGameWorld()&&
                (stack.getItem() == ModItems.WORLD_DESTROYER.get() || BlessingUtils.hasBlessedItem(BlessingUtils.ItemType.TOOL))) {
            if(!mc.options.keyAttack.isDown() || hit.getType() == HitResult.Type.MISS) {
                tick = 8;
                return;
            }
            if (tick < 7) {
                tick++;
                return;
            }
            tick = 0;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;

            if (hit.getType() == HitResult.Type.ENTITY) return;

            DestroyMode mode = DestroyMode.getMode(player.getMainHandItem());

            PacketHandler.CHANNEL.sendToServer(new DestroyBlockPacket(pos, mode));
            player.swing(InteractionHand.MAIN_HAND);
        }
    }

    public static BlockHitResult getPlayerLookingAt(Player player, int reach) {
        Level level = player.level();

        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookVector = player.getLookAngle().scale(reach);
        Vec3 endPosition = eyePosition.add(lookVector);

        ClipContext context = new ClipContext(
                eyePosition,
                endPosition,
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.ANY,
                player
        );

        return level.clip(context);
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            if (player == null) return;
            ItemStack stack = serverPlayer.getMainHandItem();
            if(stack.getItem() == ModItems.UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU.get()) {
                PacketHandler.CHANNEL.sendToServer(new DestroyBlockPacket(event.getPos(), DestroyMode.NORMAL));
                return;
            }
            if (isInGameWorld() && (stack.getItem() == ModItems.WORLD_DESTROYER.get())
                    || BlessingUtils.hasBlessedItem(BlessingUtils.ItemType.TOOL)) {
                if (!player.isShiftKeyDown()) {
                    PacketHandler.CHANNEL.sendToServer(new DestroyBlockPacket(event.getPos(), DestroyMode.NORMAL));
                } else {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onInput(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        ItemStack stack = mc.player.getMainHandItem();
        /*if (event.getButton() == 1 && event.getAction() == 1 && mc.player.isShiftKeyDown() && stack.getItem() == ModItems.SUGAR_SWORD.get()) {
            PacketHandler.CHANNEL.sendToServer(new EraserRangeAttackPacket());
            mc.player.swing(mc.player.getUsedItemHand());
        }*/
        if (isInGameWorld() && event.getButton() == 0 && event.getAction() == 1 && stack.getItem() == ModItems.WORLD_DESTROYER.get()) {

            /*HitResult hit = mc.hitResult;
            if (hit != null && hit.getType() == HitResult.Type.ENTITY && stack.getItem() == ModItems.WORLD_DESTROYER.get()) {
                EntityHitResult entityHit = (EntityHitResult) hit;
                int id = entityHit.getEntity().getId();
                PacketHandler.CHANNEL.sendToServer(new RayCastPacket(id));
            } else {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) return;

                BlockPos pos = getPlayerLookingAt(mc.player, 5).getBlockPos();
                DestroyMode mode = DestroyMode.getMode(player.getMainHandItem());

                PacketHandler.CHANNEL.sendToServer(new DestroyBlockPacket(pos, mode));
            }*/
        }
        if (isInGameWorld() && event.getButton() == 0 && event.getAction() == 1 && BlessingUtils.hasBlessedItem(BlessingUtils.ItemType.SWORD)) {
            HitResult hit = mc.hitResult;
            if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHit = (EntityHitResult) hit;
                int id = entityHit.getEntity().getId();
                PacketHandler.CHANNEL.sendToServer(new RayCastPacket(id));
            }
        }
        if (isInGameWorld() && event.getButton() == 0 && event.getAction() == 1 && BlessingUtils.hasBlessedItem(BlessingUtils.ItemType.TOOL)) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;

            BlockPos pos = getPlayerLookingAt(mc.player, 5).getBlockPos();
            DestroyMode mode = DestroyMode.getMode(player.getMainHandItem());

            PacketHandler.CHANNEL.sendToServer(new DestroyBlockPacket(pos, mode));
        }
    }

    @SubscribeEvent
    public static void onStartDestroyBlock(PlayerEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        ItemStack stack = mc.player.getMainHandItem();


    }

    @SubscribeEvent
    public static void onInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack stack = mc.player.getMainHandItem();
        ItemStack offstack = mc.player.getOffhandItem();

        if (ModKeyBindings.RANGE_ATTACK.consumeClick() && (stack.getItem() == ModItems.SUGAR_SWORD.get() || offstack.getItem() == ModItems.SUGAR_SWORD.get())) {
            if(!SugarSword_Item.isOnCustomCooldown(mc.player.getMainHandItem()) && !SugarSword_Item.isOnCustomCooldown(mc.player.getOffhandItem())) {
                PacketHandler.CHANNEL.sendToServer(new EraserRangeAttackPacket());
                if(stack.getItem() == ModItems.SUGAR_SWORD.get())SugarSword_Item.startRangeAttackCooldown(stack, 10);
                else SugarSword_Item.startRangeAttackCooldown(offstack, 10);
                mc.player.swing(InteractionHand.MAIN_HAND);
            }
        }

        if (stack.getItem() == ModItems.WORLD_DESTROYER.get()) {
            if (ModKeyBindings.TOGGLE_RANGE.consumeClick()) {
                DestroyMode current = DestroyMode.getMode(mc.player.getMainHandItem());

                DestroyMode next = DestroyMode.values()[(current.ordinal() + 1) % DestroyMode.values().length];
                ItemStack held = mc.player.getMainHandItem();

                if (mc.player.isShiftKeyDown()) {
                    boolean nextSilk = !DestroyMode.isSilkTouchEnabled(held);

                    PacketHandler.CHANNEL.sendToServer(new WorldDestroyerChangeModePacket(current, nextSilk));

                } else {
                    boolean silk = DestroyMode.isSilkTouchEnabled(held);

                    PacketHandler.CHANNEL.sendToServer(new WorldDestroyerChangeModePacket(next, silk));
                }
            }
        }

        if (stack.getItem() == ModItems.SUGAR_BOW.get()) {
            if (ModKeyBindings.TOGGLE_SHOOT_MODE.consumeClick()) {
                ShootMode current = ShootMode.getMode(mc.player.getMainHandItem());
                ShootMode next = ShootMode.values()[(current.ordinal() + 1) % ShootMode.values().length];
                PacketHandler.CHANNEL.sendToServer(new SugarBowSetModePacket(next));
            }
        }

        if (ModKeyBindings.HALO_TIMESTOP.consumeClick()) {
            if (EntityUtils.hasHaloOfSugar(mc.player)) {
                PacketHandler.CHANNEL.sendToServer(new TimeStopPacket());
            }
        }
        if (ModKeyBindings.HALO_STRAGE.consumeClick()) {
            if (EntityUtils.hasHaloOfSugar(mc.player)) {
                PacketHandler.CHANNEL.sendToServer(new OpenHaloStragePacket());
            }
        }
    }

    public static boolean isInGameWorld() {
        return Minecraft.getInstance().screen == null;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            Minecraft mc = Minecraft.getInstance();

            renderBlockList(event.getPoseStack(), event.getCamera().getPosition(), RenderQueue.getPositions(), 0xFFFFFFFF);
            ItemStack stack = mc.player.getMainHandItem();

        }
    }

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ILivingEntity living) {
            if (living.isErased()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc == null || mc.player == null || mc.level == null) {
                }

                //System.out.println(Component.literal("[NoSugar] Prevented joining erased entity to level: " + event.getEntity().toString()));
                //event.setCanceled(true);
            }
        }
    }


    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !Minecraft.getInstance().isPaused()) {
            renderTime++;
        }
    }

    @SubscribeEvent
    public static void onAbilitiesField(AbilitiesFieldEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null ||
                (event.getType() != AbilitiesFieldEvent.FieldType.IS_FLYING &&
                        event.getType() != AbilitiesFieldEvent.FieldType.MAY_FLY)) {
            return;
        }
        boolean nextValue = (boolean) event.getNewValue();
        if (EntityUtils.hasHaloOfSugar(mc.player) && !FlyManager.isCanDisableFly() && !nextValue) {
            //NoSugar.LOGGER.info("nextValue: " + nextValue);
            event.setNewValue(mc.player.getAbilities().flying);
            PacketHandler.CHANNEL.sendToServer(new HaloFlyPacket(mc.player.getAbilities().flying));
        }
    }
}

