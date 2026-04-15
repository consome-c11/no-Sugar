package com.test.nosugar.additional;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static KeyMapping TOGGLE_RANGE;
    public static KeyMapping TOGGLE_SHOOT_MODE;
    public static KeyMapping RANGE_ATTACK;
    public static KeyMapping HALO_TIMESTOP;
    public static KeyMapping HALO_STRAGE;
    public static KeyMapping CREATIVE_SWORD_MENU;

    public static void init(RegisterKeyMappingsEvent event) {
        TOGGLE_RANGE = new KeyMapping(
                "key.world_destroyer.toggle_range",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "key.categories.nosugar"
        );
        TOGGLE_SHOOT_MODE = new KeyMapping(
                "key.sugarbow.toggle_mode",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "key.categories.nosugar"
        );
        RANGE_ATTACK = new KeyMapping(
                "key.sugar.sword.range.attack",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "key.categories.nosugar"
        );
        HALO_TIMESTOP = new KeyMapping(
                "key.halo.of.sugar.time.stop",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "key.categories.nosugar"
        );
        HALO_STRAGE = new KeyMapping(
                "key.halo.of.sugar.open.strage",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "key.categories.nosugar"
        );
        CREATIVE_SWORD_MENU = new KeyMapping(
                "key.creative_sword.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "key.categories.nosugar"
        );
        event.register(ModKeyBindings.TOGGLE_RANGE);
        event.register(ModKeyBindings.TOGGLE_SHOOT_MODE);
        event.register(ModKeyBindings.RANGE_ATTACK);
        event.register(ModKeyBindings.HALO_TIMESTOP);
        event.register(ModKeyBindings.HALO_STRAGE);
        event.register(ModKeyBindings.CREATIVE_SWORD_MENU);
    }
}
