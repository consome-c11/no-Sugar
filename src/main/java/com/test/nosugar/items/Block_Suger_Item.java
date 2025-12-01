package com.test.nosugar.items;

import com.test.nosugar.entity.BlockSugerEntity;
import com.test.nosugar.utils.render.ColorUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class Block_Suger_Item extends Item {
    public Block_Suger_Item(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);

        if (!level.isClientSide()) {
            BlockSugerEntity entity = new BlockSugerEntity(level, player);
            entity.setItem(itemStack);
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(entity);
        }

        itemStack.shrink(1);
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public Component getName(ItemStack stack) {
        String text = Component.translatable("item.nosuger.block_suger.name").getString();
        var result = Component.empty();
        long time = System.currentTimeMillis() / 50;

        for (int i = 0; i < text.length(); i++) {
            int color = ColorUtils.waveGrayWhiteColor(time, i, 5.0);
            result = result.append(Component.literal(String.valueOf(text.charAt(i)))
                    .withStyle(style -> style.withColor(color)));
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        long gameTime = (level != null) ? level.getGameTime() : 0;
        String desc = Component.translatable("item.nosuger.block_suger.desc").getString();
        var result = Component.empty();
        long time = System.currentTimeMillis() / 50;

        for (int i = 0; i < desc.length(); i++) {
            int color = ColorUtils.waveGrayWhiteColor(time, i, 5.0);
            result = result.append(Component.literal(String.valueOf(desc.charAt(i)))
                    .withStyle(style -> style.withColor(color)));
        }
        tooltip.add(1, result);
    }
}
