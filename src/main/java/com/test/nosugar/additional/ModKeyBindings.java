package com.test.nosugar.additional;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static KeyMapping TOGGLE_RANGE;
    public static KeyMapping TOGGLE_SHOOT_MODE;

    public static void init() {
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
    }
}
