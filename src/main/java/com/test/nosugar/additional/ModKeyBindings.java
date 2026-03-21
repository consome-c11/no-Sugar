package com.test.nosugar.additional;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static KeyMapping TOGGLE_RANGE;
    public static KeyMapping TOGGLE_SHOOT_MODE;
    public static KeyMapping RANGE_ATTACK;

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
        event.register(ModKeyBindings.TOGGLE_RANGE);
        event.register(ModKeyBindings.TOGGLE_SHOOT_MODE);
        event.register(ModKeyBindings.RANGE_ATTACK);
    }
}
