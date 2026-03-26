package com.test.nosugar.client.renderer;

import com.test.nosugar.utils.Res;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.Nullable;

public class SugarBowBakedModel extends SugarBakedModel {

    private static final ModelResourceLocation MODEL_PULLING_0 = new ModelResourceLocation(Res.getResource("nosugar", "sugar_bow_pulling_0"), "inventory");
    private static final ModelResourceLocation MODEL_PULLING_1 = new ModelResourceLocation(Res.getResource("nosugar", "sugar_bow_pulling_1"), "inventory");
    private static final ModelResourceLocation MODEL_PULLING_2 = new ModelResourceLocation(Res.getResource("nosugar", "sugar_bow_pulling_2"), "inventory");

    public SugarBowBakedModel(BakedModel templateModel) {
        super(templateModel);
    }

    @Override
    public ItemOverrides getOverrides() {
        return new SugarBowOverrides();
    }

    private static class SugarBowOverrides extends ItemOverrides {

        @Nullable
        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            if (entity != null && entity.getUseItem() == stack) {
                int useDuration = stack.getUseDuration() - entity.getUseItemRemainingTicks();
                float pullProgress = BowItem.getPowerForTime(useDuration);

                int pullingStage = (int) (pullProgress * 3); // 0, 1, 2, 3 -> 0, 1, 2 (clamped)
                pullingStage = Math.max(0, Math.min(2, pullingStage));

                ModelResourceLocation targetModelLocation;
                switch (pullingStage) {
                    case 0:
                        targetModelLocation = MODEL_PULLING_0;
                        break;
                    case 1:
                        targetModelLocation = MODEL_PULLING_1;
                        break;
                    case 2:
                        targetModelLocation = MODEL_PULLING_2;
                        break;
                    default:
                        return originalModel;
                }

                BakedModel targetModel = Minecraft.getInstance().getModelManager().getModel(targetModelLocation);
                if (targetModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                    return new SugarBakedModel(targetModel);
                }
            }
            return originalModel;
        }
    }
}