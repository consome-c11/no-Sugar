package com.test.nosugar;

import com.mojang.logging.LogUtils;
import com.test.nosugar.compat.slashblade.SERegister;
import com.test.nosugar.compat.spells.ModSpells;
import com.test.nosugar.compat.tconstruct.TConstruct;
import com.test.nosugar.client.ModCreativeTabs;
import com.test.nosugar.entity.ModEntities;
import com.test.nosugar.gui.ModMenus;
import com.test.nosugar.items.ModItems;
import com.test.nosugar.network.ModPackets;
import com.test.nosugar.utils.item.InventorySpecialItemsHolder;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.objectweb.asm.*;
import org.slf4j.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.test.nosugar.utils.Deets.*;

@SuppressWarnings("removal")
@Mod(NoSugar.MODID)
public class NoSugar {
    public static final String MODID = "nosugar";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NoSugar() {
        MinecraftForge.EVENT_BUS.register(this);

        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModItems.ITEMS.register(modEventBus);
        ModItems.ADDON_ITEMS.register(modEventBus);
        ModItems.DUMMY_ITEMS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);//深夜テンションの時にコード書いてはダメだな
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        require(IRONS_SPELLBOOKS).run(() -> {
            ModSpells.register(modEventBus);
        });
        require(SLASHBLADE).run(() -> {
           ModItems.SLASH_BLADE_ITEMS.register(modEventBus);
            SERegister.register(modEventBus);
        });
        new TConstruct(modEventBus,FMLJavaModLoadingContext.get());
        System.out.println("[NoSugar] loaded");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        List<Item> modItems = ModItems.getAllItems();
        Set<Item> itemSet = new HashSet<>(modItems);
        InventorySpecialItemsHolder.setSpecialItems(itemSet);
        ModPackets.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }

    public static class LivingEntityTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            if ("net/minecraft/world/entity/LivingEntity".equals(className)) {
                System.out.println("[NoSugar] Transforming: " + className);
                return modifyLivingEntityClass(classfileBuffer);
            }
            return classfileBuffer;
        }

        private byte[] modifyLivingEntityClass(byte[] original) {
            ClassReader cr = new ClassReader(original);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new LivingEntityClassVisitor(cw);
            cr.accept(cv, 0);
            return cw.toByteArray();
        }
    }

    public static class LivingEntityClassVisitor extends ClassVisitor {
        public LivingEntityClassVisitor(ClassVisitor cv) {
            super(Opcodes.ASM9, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if ("m_21223_".equals(name) && "()F".equals(desc)) { // getHealth
                System.out.println("[NoSugar] Modifying method: " + name);
                if (mv != null) {
                    return new GetHealthModifyMethodVisitor(mv);
                }
            }
            return mv;
        }
    }

    public static class GetHealthModifyMethodVisitor extends MethodVisitor {
        public GetHealthModifyMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM9, mv);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            injectConditionCheck();
        }

        private void injectConditionCheck() {
            mv.visitVarInsn(Opcodes.ALOAD, 0);

            mv.visitTypeInsn(Opcodes.INSTANCEOF, "com/test/nosugar/utils/interfaces/ILivingEntity");

            Label skipLabel = new Label();
            mv.visitJumpInsn(Opcodes.IFEQ, skipLabel);

            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/world/entity/LivingEntity");

            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitTypeInsn(Opcodes.CHECKCAST, "com/test/nosugar/utils/interfaces/ILivingEntity");

            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/LivingEntity", "getUUID", "()Ljava/util/UUID;", false);
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/test/nosugar/utils/interfaces/ILivingEntity", "isErased", "(Ljava/util/UUID;)Z", true);

            mv.visitJumpInsn(Opcodes.IFEQ, skipLabel);

            mv.visitLdcInsn(0.0F);
            mv.visitInsn(Opcodes.FRETURN);

            mv.visitLabel(skipLabel);
        }
    }
}
