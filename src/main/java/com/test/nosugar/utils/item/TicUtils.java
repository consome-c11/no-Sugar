package com.test.nosugar.utils.item;

import com.test.nosugar.compat.tconstruct.Mods;
import com.test.nosugar.utils.Deets;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.test.nosugar.utils.Deets.TINKERSCONSTRUCT;

public class TicUtils {
    public static boolean hasSugarMod(ItemStack stack) {
        AtomicBoolean hassugar = new AtomicBoolean(false);
        Deets.require(TINKERSCONSTRUCT).run(() -> {
            if (stack.getItem() instanceof IModifiable) {
                ToolStack tool = ToolStack.from(stack);
                hassugar.set(!tool.isBroken() && (tool.getModifierLevel(Mods.SUGAR.getId()) > 0 || tool.getModifierLevel(Mods.TAIL_OF_NINE.getId()) > 0));
            }
        });
        return hassugar.get();
    }

}
