package com.test.nosugar.items;

import com.test.nosugar.NoSugar;
import com.test.nosugar.mixin.sugar_sword.EntityLookupAccessor;
import com.test.nosugar.mixin.sugar_sword.LevelEntityGetterAdapterAccessor;
import com.test.nosugar.mixin.sugar_sword.PersistentEntitySectionManagerAccessor;
import com.test.nosugar.mixin.sugar_sword.ServerLevelAccessor;
import com.test.nosugar.utils.item.Eraser_Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.jetbrains.annotations.NotNull;


public class UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU extends Item{
    public UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU(Item.Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);

        if (level.isClientSide()) {
            return InteractionResultHolder.fail(itemStack);
        }
        //これで取得できなかったら泣くぞ
        ServerLevel serverLevel = (ServerLevel)level;
        PersistentEntitySectionManager<Entity> manager =
                ((ServerLevelAccessor) serverLevel).getEntityManager();
        PersistentEntitySectionManagerAccessor<Entity> acc =
                (PersistentEntitySectionManagerAccessor<Entity>) manager;

        LevelEntityGetter<Entity> getter = acc.getEntityGetter();
        EntityLookup<Entity> vis = ((LevelEntityGetterAdapterAccessor<Entity>) getter).getVisibleEntities();
        ((EntityLookupAccessor)vis).getById().values().stream().forEach((ent) -> {
            if(ent instanceof LivingEntity living) {
                if (living.getId() != player.getId()) Eraser_Utils.killIfParentFound(living, player);
            }
        });

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());

    }
}
