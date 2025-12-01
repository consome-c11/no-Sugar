package com.test.nosugar.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.test.nosugar.client.renderer.ClientEntityCache;
import com.test.nosugar.client.renderer.PlayerModelDrawer;
import com.test.nosugar.client.utils.RenderQueue;
import com.test.nosugar.items.ModItems;
import com.test.nosugar.additional.ModKeyBindings;
import com.test.nosugar.utils.*;
import com.test.nosugar.network.PacketHandler;
import com.test.nosugar.network.packets.DestroyBlockPacket;
import com.test.nosugar.network.packets.EraserRangeAttackPacket;
import com.test.nosugar.network.packets.RayCastPacket;
import com.test.nosugar.network.packets.WorldDestroyerChangeModePacket;
import com.test.nosugar.utils.intercafes.ILivingEntity;
import com.test.nosugar.utils.item.BlessingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
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

        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            boolean same_id = DestroyMode.getMode(mc.player.getMainHandItem()) == DestroyMode.SAME_ID || DestroyMode.getMode(mc.player.getMainHandItem()) == DestroyMode.SAME_ID_ORE;
            Predicate<BlockState> accept = state -> !state.isAir();
            BlockState LookBlockState = mc.level.getBlockState(blockHit.getBlockPos());
            if (DestroyMode.getMode(mc.player.getMainHandItem()) == DestroyMode.SAME_ID_ORE) {
                TagKey<Block> FORGE_ORES = BlockTags.create(Res.getResource("forge", "ores"));
                accept = state -> state.is(FORGE_ORES) || state.is(BlockTags.LOGS);
            } else if (DestroyMode.getMode(mc.player.getMainHandItem()) == DestroyMode.SAME_ID) {
                accept = state -> state.is(LookBlockState.getBlock());
            }
            if (DestroyMode.getMode(mc.player.getMainHandItem()) == DestroyMode.NORMAL || mc.player.getMainHandItem().getItem() != ModItems.WORLD_DESTROYER.get()) {
                RenderQueue.clear();
            } else
                QueueRenderBreakBlock(mc.level, mc.player, blockHit.getBlockPos(), DestroyMode.getMode(mc.player.getMainHandItem()), same_id, 32, accept);
        } else RenderQueue.clear();
        erase();
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
        if (stack.getItem() == ModItems.WORLD_DESTROYER.get()) {
            if (ModKeyBindings.TOGGLE_RANGE.consumeClick()) {
                DestroyMode current = DestroyMode.getMode(mc.player.getMainHandItem());

                DestroyMode next = DestroyMode.values()[(current.ordinal() + 1) % DestroyMode.values().length];
                ItemStack held = mc.player.getMainHandItem();
                    /*mc.player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("Mode: " + next.name()),
                            true
                    );*/

                if (mc.player.isShiftKeyDown()) {
                    boolean nextSilk = !DestroyMode.isSilkTouchEnabled(held);

                    PacketHandler.CHANNEL.sendToServer(new WorldDestroyerChangeModePacket(current, nextSilk));

                } else {
                    boolean silk = DestroyMode.isSilkTouchEnabled(held);

                    PacketHandler.CHANNEL.sendToServer(new WorldDestroyerChangeModePacket(next, silk));
                }
            }
        }

        if (isInGameWorld() && mc.options.keyAttack.isDown() &&
                (stack.getItem() == ModItems.WORLD_DESTROYER.get() || BlessingUtils.hasBlessedItem(BlessingUtils.ItemType.TOOL))) {
            if (mc.options.keyShift.isDown() && tick < 7) {
                tick++;
                return;
            }
            tick = 0;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;

            if (hit.getType() == HitResult.Type.ENTITY) return;

            BlockPos pos = getPlayerLookingAt(mc.player, 5).getBlockPos();
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
            if (isInGameWorld() && (stack.getItem() == ModItems.WORLD_DESTROYER.get()) ||  BlessingUtils.hasBlessedItem(BlessingUtils.ItemType.TOOL)) {
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
        if (event.getButton() == 1 && event.getAction() == 1 && mc.player.isShiftKeyDown() && stack.getItem() == ModItems.SUGAR_SWORD.get()) {
            PacketHandler.CHANNEL.sendToServer(new EraserRangeAttackPacket());
            mc.player.swing(mc.player.getUsedItemHand());
        }
        if (isInGameWorld() && event.getButton() == 0 && event.getAction() == 1 && stack.getItem() == ModItems.WORLD_DESTROYER.get()) {

            HitResult hit = mc.hitResult;
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
            }
        }
        if(isInGameWorld() && event.getButton() == 0 && event.getAction() == 1 && BlessingUtils.hasBlessedItem(BlessingUtils.ItemType.SWORD)) {
            HitResult hit = mc.hitResult;
            if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHit = (EntityHitResult) hit;
                int id = entityHit.getEntity().getId();
                PacketHandler.CHANNEL.sendToServer(new RayCastPacket(id));
            }
        }
        if(isInGameWorld() && event.getButton() == 0 && event.getAction() == 1 && BlessingUtils.hasBlessedItem(BlessingUtils.ItemType.TOOL)) {
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
    /*@SubscribeEvent //shitty shield effect rendering
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

            ShieldEffectRenderer.render(poseStack, buffer, event.getPartialTick());

            buffer.endBatch();
        }
    }*/

    public static boolean isInGameWorld() {
        return Minecraft.getInstance().screen == null;
    }

    /*@SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<LivingEntity, ?> event) {
        LivingEntity entity = event.getEntity();
        UUID uuid = entity.getUUID();
        if (entity instanceof ILivingEntity living && living.isErased(entity.getUUID())) {
            entity.setDeltaMovement(new Vec3(0, 0, 0));
            long now = System.currentTimeMillis();
            long last = lastUpdate.getOrDefault(uuid, 0L);
            if (now - last >= 50) {//1tick?
                entity.deathTime++;
                entity.setPose(Pose.DYING);
                lastUpdate.put(uuid, now);
            }
            //if (entity.deathTime > 20) event.setCanceled(true);
        }
    }*/

    public static boolean erase() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        int[] ids = erasedEntities.stream()
                .mapToInt(Entity::getId)
                .toArray();

        for (int id : ids) {
            Entity e = level.getEntity(id);
            if (e != null) {
                ClientPacketListener connection = mc.getConnection();

                ClientboundRemoveEntitiesPacket packet =
                        new ClientboundRemoveEntitiesPacket(e.getId());

                packet.handle(connection);
                return true;
            }

        }
        return false;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            Minecraft mc = Minecraft.getInstance();
            PoseStack poseStack = event.getPoseStack();
            Vec3 camera = event.getCamera().getPosition();

            /*for (RenderQueue.RenderEntry entry : RenderQueue.getEntries()) {
                if(entry == null) break;
                RenderUtils.renderBlockBox(poseStack, camera, entry.pos, entry.color);
            }*/
            renderBlockList(event.getPoseStack(), event.getCamera().getPosition(), RenderQueue.getPositions(), 0xFFFFFFFF);

            //RenderQueue.clear();

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
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        PoseStack poseStack = event.getPoseStack();
        float partialTick = event.getPartialTick();

        for (var entry : ClientEntityCache.entityCache.entrySet()) {
            var data = entry.getValue();
            if (data.entity != null && System.currentTimeMillis() - data.lastUpdate < 5000) {
                PlayerModelDrawer.renderEntity(
                        data.entity,
                        poseStack,
                        0, 0, 0,
                        1.0f,
                        partialTick
                );
            }
        }
    }
}

