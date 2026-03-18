package com.test.nosugar.items;

import com.test.nosugar.gui.BagMenu;
import com.test.nosugar.network.PacketHandler;
import com.test.nosugar.network.packets.SyncBagPagesPacket;
import com.test.nosugar.utils.BagSavedData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.test.nosugar.utils.render.ColorUtils.makeWaveLine;

public class UltimaCanteen extends Item {
    private static final float SATURATION_TARGET = 7.0f;
    private static final int FOOD_LEVEL_TARGET = 20;

    public UltimaCanteen(Properties props) {
        super(props);
    }

    @Override
    public Component getName(ItemStack stack) {
        return makeWaveLine("Canteen", true);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            player.swing(hand, true);
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        } else {
            if (player.isShiftKeyDown()) {
                float maxHp = player.getMaxHealth();
                float healAmount = maxHp * 0.4f;
                float newHp = Math.min(player.getHealth() + healAmount, maxHp);
                player.setHealth(newHp);

                try {
                    int currentFood = player.getFoodData().getFoodLevel();
                    if (currentFood < FOOD_LEVEL_TARGET) {
                        int addFood = FOOD_LEVEL_TARGET - currentFood;
                        player.getFoodData().eat(addFood, SATURATION_TARGET);
                    }

                    float currentSat = player.getFoodData().getSaturationLevel();
                    if (currentSat < SATURATION_TARGET) {
                        float addSat = SATURATION_TARGET - currentSat;
                        player.getFoodData().eat(0, addSat);
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }

                level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0f, 1.0f);
                player.getCooldowns().addCooldown(this, 70);
            } else {
                ItemStack stack = player.getItemInHand(hand);
                ServerPlayer serverPlayer = (ServerPlayer) player;

                UUID bagId = getOrCreateBagId(stack);

                int totalPages = Integer.MAX_VALUE;
                int currentPage = 0;

                BagSavedData data = BagSavedData.get(level);
                List<ItemStack> prevPage = (currentPage > 0) ? data.getPage(bagId, currentPage - 1) : Collections.emptyList();
                List<ItemStack> currentPageItems = data.getPage(bagId, currentPage);
                List<ItemStack> nextPage = (currentPage < data.getTotalPages(bagId) - 1) ? data.getPage(bagId, currentPage + 1) : Collections.emptyList();

                PacketHandler.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new SyncBagPagesPacket(bagId, currentPage, prevPage, currentPageItems, nextPage)
                );

                NetworkHooks.openScreen(serverPlayer, new BagMenuProvider(bagId, currentPage, totalPages), buf -> {
                    buf.writeUUID(bagId);
                    buf.writeInt(currentPage);
                    buf.writeInt(totalPages);
                });
            }
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    private UUID getOrCreateBagId(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().hasUUID("bag_id")) {
            return stack.getTag().getUUID("bag_id");
        } else {
            UUID uuid = UUID.randomUUID();
            stack.getOrCreateTag().putUUID("bag_id", uuid);
            return uuid;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(1, makeWaveLine("Solve food problem!", 0xFFD700, 0xD4AF37));
    }

    public static class BagMenuProvider implements MenuProvider {
        private final UUID bagId;
        private final int page;
        private final int totalPages;

        public BagMenuProvider(UUID bagId, int page, int totalPages) {
            this.bagId = bagId;
            this.page = page;
            this.totalPages = totalPages;
        }

        @Override
        public Component getDisplayName() {
            return makeWaveLine("Canteen", false);
        }

        @Override
        public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
            return new BagMenu(windowId, inv, bagId, page, totalPages);
        }
    }
}