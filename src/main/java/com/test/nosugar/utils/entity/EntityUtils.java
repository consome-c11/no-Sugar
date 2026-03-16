package com.test.nosugar.utils.entity;

import com.test.nosugar.Config;
import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.utils.item.TicUtils;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Unique;

public class EntityUtils {

    public static boolean enable_tag(DamageSource source, TagKey<DamageType> tag){
        //if(source.getEntity() != null) NoSugar.LOGGER.info("Source Entity: " + source.getEntity().getName());

        boolean isNoSugarItem = source.getEntity() instanceof LivingEntity living
                && (living.getMainHandItem().getItem() == ModItems.SUGAR_SWORD.get()
                || living.getMainHandItem().getItem() == ModItems.WORLD_DESTROYER.get()
                || living.getMainHandItem().getItem() == ModItems.TAIL_OF_NINE.get()
                || TicUtils.hasSugarMod(living.getMainHandItem()));
        //NoSugar.LOGGER.info(tag.toString());
        if (isNoSugarItem && Config.shouldBypassTag(tag)) {
            return true;
        }
        return false;
    }

    public static boolean getretInvulnerable(LivingEntity target, DamageSource source){
        if(source.getEntity() instanceof LivingEntity attacker) {
            boolean isNoSugarItem = (attacker.getMainHandItem().getItem() == ModItems.SUGAR_SWORD.get()
                    || attacker.getMainHandItem().getItem() == ModItems.WORLD_DESTROYER.get()
                    || attacker.getMainHandItem().getItem() == ModItems.TAIL_OF_NINE.get()
                    || TicUtils.hasSugarMod(attacker.getMainHandItem()));
            NoSugar.LOGGER.info(attacker.getMainHandItem().getDisplayName().getString());
            if (isNoSugarItem && Config.shouldBypassTag(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                return false;
            }
        }
        return true;
    }
}
