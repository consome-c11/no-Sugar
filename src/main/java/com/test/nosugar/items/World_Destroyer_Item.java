package com.test.nosugar.items;

import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.additional.ModTiers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

import static com.test.nosugar.utils.render.ColorUtils.makeWaveLine;

@Mod.EventBusSubscriber(modid = NoSugar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class World_Destroyer_Item extends PickaxeItem {
    public World_Destroyer_Item(Properties props) {
        super(ModTiers.WORLD_DESTROYER_TIER, 1, 21.0F, props.stacksTo(1).fireResistant());
    }

    @Override
    public Component getName(ItemStack stack) {
        String text = Component.translatable("item.nosuger.world_destroyer").getString();
        return makeWaveLine(text, true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(1, makeWaveLine(Component.translatable("item.nosugar.world_destroyer.desc").getString(), 0xFFAAAAAA, 0xFFFFFFFF));
        tooltip.add(2, makeWaveLine("Snack is World", 0xFFD700, 0xD4AF37));
        tooltip.add(3, makeWaveLine("Fortune VII"));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event){
        if(event.getSource().getEntity() instanceof LivingEntity attacker &&
                attacker.getMainHandItem().getItem() == ModItems.WORLD_DESTROYER.get()){
            event.setAmount(Float.POSITIVE_INFINITY);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            ItemStack cookie = new ItemStack(Items.COOKIE, 1);
            if (player.isShiftKeyDown()) {
                cookie = new ItemStack(Items.COOKIE, 64);
            }
            if (!player.addItem(cookie)) {
                player.drop(cookie, false);
            }
        }

        player.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }
}