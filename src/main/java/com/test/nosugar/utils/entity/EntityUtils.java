package com.test.nosugar.utils.entity;

import com.test.nosugar.Config;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.mixin.sugar_sword.EntityLookupAccessor;
import com.test.nosugar.mixin.sugar_sword.LevelEntityGetterAdapterAccessor;
import com.test.nosugar.mixin.sugar_sword.PersistentEntitySectionManagerAccessor;
import com.test.nosugar.mixin.sugar_sword.ServerLevelAccessor;
import com.test.nosugar.utils.item.BlessingUtils;
import com.test.nosugar.utils.item.TicUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.phys.AABB;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EntityUtils {

    public static boolean hasHaloOfSugar(LivingEntity living) {
        return CuriosApi.getCuriosInventory(living)
                .map(inv -> inv.findFirstCurio(ModItems.HALO_OF_SUGAR.get()).isPresent())
                .orElse(false);
    }

    public static boolean enable_tag(DamageSource source, TagKey<DamageType> tag){
        if(!(source.getEntity() instanceof LivingEntity living)) return false;
        //if(source.getEntity() != null) NoSugar.LOGGER.info("Source Entity: " + source.getEntity().getName());

        boolean isNoSugarItem = (living.getMainHandItem().getItem() == ModItems.SUGAR_SWORD.get()
                || living.getMainHandItem().getItem() == ModItems.WORLD_DESTROYER.get()
                || living.getMainHandItem().getItem() == ModItems.TAIL_OF_NINE.get()
                || TicUtils.hasSugarMod(living.getMainHandItem())
                || BlessingUtils.isBlessed(living.getMainHandItem()));
        //NoSugar.LOGGER.info(tag.toString());

        return (isNoSugarItem || hasHaloOfSugar(living)) && Config.shouldBypassTag(tag);
    }

    /*public static boolean getretInvulnerable(DamageSource source){
        if(source.getEntity() instanceof LivingEntity attacker) {
            boolean isNoSugarItem = (attacker.getMainHandItem().getItem() == ModItems.SUGAR_SWORD.get()
                    || attacker.getMainHandItem().getItem() == ModItems.WORLD_DESTROYER.get()
                    || attacker.getMainHandItem().getItem() == ModItems.TAIL_OF_NINE.get()
                    || TicUtils.hasSugarMod(attacker.getMainHandItem())
                    || BlessingUtils.isBlessed(attacker.getMainHandItem()));
            NoSugar.LOGGER.info(attacker.getMainHandItem().getDisplayName().getString());
            if (isNoSugarItem && Config.shouldBypassTag(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                return false;
            }
        }
        return true;
    }*/

    public static ArrayList getEntities(ServerLevel level) {
        PersistentEntitySectionManager<Entity> manager =
                ((ServerLevelAccessor) level).getEntityManager();
        PersistentEntitySectionManagerAccessor<Entity> acc =
                (PersistentEntitySectionManagerAccessor<Entity>) manager;

        LevelEntityGetter<Entity> getter = acc.getEntityGetter();
        EntityLookup<Entity> vis = ((LevelEntityGetterAdapterAccessor<Entity>) getter).getVisibleEntities();
        return new ArrayList<>(((EntityLookupAccessor) vis).getById().values());
    }

    //PartEntityの処理無いの注意
    public static List<Entity> getEntitiesInAABB(
            ServerLevel level,
            @Nullable Entity exclude,
            AABB bounds,
            Predicate<? super Entity> predicate) {

        List<Entity> result = new ArrayList<>();
        List<Entity> allEntities = getEntities(level);

        for (Entity entity : allEntities) {
            if (entity == exclude) {
                continue;
            }
            if (!entity.getBoundingBox().intersects(bounds)) {
                continue;
            }

            if (!predicate.test(entity)) {
                continue;
            }

            result.add(entity);
        }
        return result;
    }
}
