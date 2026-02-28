package com.test.nosugar.mixin.sugar_sword;

import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.world.level.entity.PersistentEntitySectionManager$Callback")
public interface PersistentEntitySectionManagerCallbackAccessor<T extends EntityAccess> {

    @Accessor("entity")
    T getEntity();

    @Accessor("currentSectionKey")
    long getCurrentSectionKey();

    @Accessor("currentSection")
    EntitySection<T> getCurrentSection();

}
