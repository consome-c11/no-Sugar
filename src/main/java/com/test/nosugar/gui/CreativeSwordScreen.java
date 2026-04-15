package com.test.nosugar.gui;

import com.test.nosugar.items.CreativeSword;
import com.test.nosugar.network.PacketHandler;
import com.test.nosugar.network.packets.SyncCreativeSwordPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreativeSwordScreen extends Screen {
    private final Player player;

    private Button leftClickButton;
    private Button rightClickButton;
    private Button invulnerableButton;
    private Button aggroImmuneButton;

    public CreativeSwordScreen(Player player, ItemStack swordStack) {
        super(Component.translatable("screen.nosugar.creative_sword_menu"));
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 25;

        this.leftClickButton = Button.builder(
                        getLeftClickText(),
                        this::onLeftClickPressed)
                .bounds(centerX - buttonWidth / 2, centerY - spacing * 2, buttonWidth, buttonHeight)
                .build();
        this.addRenderableWidget(this.leftClickButton);

        this.rightClickButton = Button.builder(
                        getRightClickText(),
                        this::onRightClickPressed)
                .bounds(centerX - buttonWidth / 2, centerY - spacing, buttonWidth, buttonHeight)
                .build();
        this.addRenderableWidget(this.rightClickButton);

        this.invulnerableButton = Button.builder(
                        getInvulnerableText(),
                        this::onInvulnerablePressed)
                .bounds(centerX - buttonWidth / 2, centerY + spacing, buttonWidth, buttonHeight)
                .build();
        this.addRenderableWidget(this.invulnerableButton);

        this.aggroImmuneButton = Button.builder(
                        getAggroImmuneText(),
                        this::onAggroImmunePressed)
                .bounds(centerX - buttonWidth / 2, centerY + spacing * 2, buttonWidth, buttonHeight)
                .build();
        this.addRenderableWidget(this.aggroImmuneButton);
    }

    private ItemStack getSwordStack() {
        ItemStack main = this.player.getMainHandItem();
        ItemStack off = this.player.getOffhandItem();
        if (main.getItem() == com.test.nosugar.additional.ModItems.CREATIVE_SWORD.get()) return main;
        if (off != null && off.getItem() == com.test.nosugar.additional.ModItems.CREATIVE_SWORD.get()) return off;
        return ItemStack.EMPTY;
    }

    private void onLeftClickPressed(Button button) {
        ItemStack swordStack = getSwordStack();
        if (swordStack.isEmpty()) return;
        int current = CreativeSword.getLeftClickAction(swordStack);
        int next = (current + 1) % 2;
        CreativeSword.setLeftClickAction(swordStack, next);
        button.setMessage(getLeftClickText());
        syncSettings();
    }

    private void onRightClickPressed(Button button) {
        ItemStack swordStack = getSwordStack();
        if (swordStack.isEmpty()) return;
        int current = CreativeSword.getRightClickAction(swordStack);
        int next = (current + 1) % 2;
        CreativeSword.setRightClickAction(swordStack, next);
        button.setMessage(getRightClickText());
        syncSettings();
    }

    private void onInvulnerablePressed(Button button) {
        ItemStack swordStack = getSwordStack();
        if (swordStack.isEmpty()) return;
        boolean current = CreativeSword.isInvulnerable(swordStack);
        boolean next = !current;
        CreativeSword.setInvulnerable(swordStack, next);
        button.setMessage(getInvulnerableText());
        syncSettings();
    }

    private void onAggroImmunePressed(Button button) {
        ItemStack swordStack = getSwordStack();
        if (swordStack.isEmpty()) return;
        boolean current = CreativeSword.isAggroImmune(swordStack);
        boolean next = !current;
        CreativeSword.setAggroImmune(swordStack, next);
        button.setMessage(getAggroImmuneText());
        syncSettings();
    }

    private void syncSettings() {
        ItemStack swordStack = getSwordStack();
        if (swordStack.isEmpty()) return;

        boolean isMainHand = this.player.getMainHandItem() == swordStack;
        PacketHandler.CHANNEL.sendToServer(new SyncCreativeSwordPacket(
                isMainHand,
                CreativeSword.getLeftClickAction(swordStack),
                CreativeSword.getRightClickAction(swordStack),
                CreativeSword.isInvulnerable(swordStack),
                CreativeSword.isAggroImmune(swordStack)
        ));
    }

    private Component getLeftClickText() {
        ItemStack swordStack = getSwordStack();
        if (swordStack.isEmpty()) return Component.literal("");
        int action = CreativeSword.getLeftClickAction(swordStack);
        String key = action == 0 ? "screen.nosugar.creative_sword.left_click.kill"
                : "screen.nosugar.creative_sword.left_click.remove";
        return Component.translatable("screen.nosugar.creative_sword.left_click",
                Component.translatable(key));
    }

    private Component getRightClickText() {
        ItemStack swordStack = getSwordStack();
        if (swordStack.isEmpty()) return Component.literal("");
        int action = CreativeSword.getRightClickAction(swordStack);
        String key = action == 0 ? "screen.nosugar.creative_sword.right_click.kill_all"
                : "screen.nosugar.creative_sword.right_click.remove_all";
        return Component.translatable("screen.nosugar.creative_sword.right_click",
                Component.translatable(key));
    }

    private Component getInvulnerableText() {
        ItemStack swordStack = getSwordStack();
        if (swordStack.isEmpty()) return Component.literal("");
        boolean value = CreativeSword.isInvulnerable(swordStack);
        Component state = value ? Component.translatable("screen.nosugar.creative_sword.toggle.on")
                : Component.translatable("screen.nosugar.creative_sword.toggle.off");
        return Component.translatable("screen.nosugar.creative_sword.invulnerable", state);
    }

    private Component getAggroImmuneText() {
        ItemStack swordStack = getSwordStack();
        if (swordStack.isEmpty()) return Component.literal("");
        boolean value = CreativeSword.isAggroImmune(swordStack);
        Component state = value ? Component.translatable("screen.nosugar.creative_sword.toggle.on")
                : Component.translatable("screen.nosugar.creative_sword.toggle.off");
        return Component.translatable("screen.nosugar.creative_sword.aggro_immune", state);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        if (this.minecraft != null && this.minecraft.level != null) {
            guiGraphics.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
