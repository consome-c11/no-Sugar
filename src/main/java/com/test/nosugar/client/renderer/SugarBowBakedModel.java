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

public class SugarBowBakedModel extends BakedModelWrapper<BakedModel> {

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
            //System.out.println("[SugarBow] resolve called.");

            if (entity != null && entity.getUseItem() == stack) {
                //System.out.println("[SugarBow] Entity is using this item.");
                int useDuration = stack.getUseDuration() - entity.getUseItemRemainingTicks();
                float pullProgress = BowItem.getPowerForTime(useDuration);
                //System.out.println("[SugarBow] UseDuration: " + useDuration + ", PullProgress: " + pullProgress);

                int pullingStage = (int) (pullProgress * 3); // 0, 1, 2, 3 -> 0, 1, 2 (clamped)
                pullingStage = Math.max(0, Math.min(2, pullingStage));
                //System.out.println("[SugarBow] PullingStage: " + pullingStage + " (PullProgress: " + pullProgress + ", UseDuration: " + useDuration + ", RemainingTicks: " + entity.getUseItemRemainingTicks() + ")");

                ModelResourceLocation targetModelLocation;
                switch (pullingStage) {
                    case 0:
                        targetModelLocation = MODEL_PULLING_0;
                        //System.out.println("[SugarBow] Target Model: MODEL_PULLING_0 (" + targetModelLocation + ")");
                        break;
                    case 1:
                        targetModelLocation = MODEL_PULLING_1;
                        //System.out.println("[SugarBow] Target Model: MODEL_PULLING_1 (" + targetModelLocation + ")");
                        break;
                    case 2:
                        targetModelLocation = MODEL_PULLING_2;
                        //System.out.println("[SugarBow] Target Model: MODEL_PULLING_2 (" + targetModelLocation + ")");
                        break;
                    default:
                        //System.out.println("[SugarBow] Unexpected pulling stage: " + pullingStage);
                        return originalModel;
                }

                BakedModel targetModel = Minecraft.getInstance().getModelManager().getModel(targetModelLocation);
                if (targetModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
                    //System.out.println("[SugarBow] Successfully retrieved target model: " + targetModelLocation);
                    return targetModel;
                } else {
                    //System.out.println("[SugarBow] Target model not found, returning original model: " + targetModelLocation);
                }
            } else {
                //System.out.println("[SugarBow] Entity is NOT using this item, returning original model.");
            }
            return originalModel;
        }
    }
}