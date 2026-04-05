package com.test.nosugar.agent;

import com.test.nosugar.agent.transformer.AsmUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class NSTransformer implements ClassFileTransformer {
    private static final NSAgentLogger LOGGER = new NSAgentLogger("NoSugar Transformer");
    private static final Set<String> transformedClasses = ConcurrentHashMap.newKeySet();
    private static final AtomicBoolean isTransforming = new AtomicBoolean(false);

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (!"cpw/mods/cl/ModuleClassLoader".equals(className)) return null;
        LOGGER.info("Found Transform class " + className);
        if (!transformedClasses.add(className)) {
            //LOGGER.info("Already transformed, skipping: " + className);
            return null;
        }

        if (!isTransforming.compareAndSet(false, true)) {
            //LOGGER.warn("Transformation already in progress, skipping");
            transformedClasses.remove(className);
            return null;
        }

        try {
            LOGGER.info("Found Class Transformer: " + className);
            byte[] result = transformClass(classfileBuffer, loader);
            if (result != null && result != classfileBuffer) {
                //LOGGER.info("Successfully transformed: " + className);
                return result;
            } else {
                transformedClasses.remove(className);
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Transform failed", e);
            transformedClasses.remove(className);
            return null;
        } finally {
            isTransforming.set(false);
        }
    }

    private byte[] transformClass(byte[] bytes, ClassLoader loader) {
        LOGGER.info("transformClass entry, bytes length: " + (bytes == null ? "null" : bytes.length));

        if (bytes == null || bytes.length == 0) {
            LOGGER.warn("Empty class bytes, skipping");
            return bytes;
        }

        try {
            ClassReader cr = new ClassReader(bytes);

            ClassNode cn = new ClassNode(Opcodes.ASM9);
            cr.accept(cn, ClassReader.EXPAND_FRAMES);
            boolean modified = false;

            if (cn.methods != null) {
                for (MethodNode mn : cn.methods) {
                    LOGGER.info("method name, {}, desc= {}",  mn.name, mn.desc);
                    if ("getClassBytes".equals(mn.name) &&
                            "(Ljava/lang/module/ModuleReader;Ljava/lang/module/ModuleReference;Ljava/lang/String;)[B".equals(mn.desc)) {
                        LOGGER.info("Found target method, injecting...");
                        injectHook(mn);
                        modified = true;
                        break;
                    }
                }
            }

            if (!modified) {
                LOGGER.warn("No target method found, returning original");
                return bytes;
            }

            ClassWriter cw = new SafeClassWriter(cr, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS, loader);
            cn.accept(cw);
            return cw.toByteArray();

        } catch (Throwable t) {
            LOGGER.error("Transform failed: " + t.getClass().getName() + " - " + t.getMessage(), t);
            return bytes;
        }
    }

    private void injectHook(MethodNode mn) {
        AbstractInsnNode returnNode = null;
        for (AbstractInsnNode insn : mn.instructions) {
            if (insn.getOpcode() == Opcodes.ARETURN) {
                returnNode = insn;
                InsnList il = new InsnList();
                il.add(new VarInsnNode(Opcodes.ALOAD, 3));
                il.add(new InsnNode(Opcodes.SWAP));
                il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/test/nosugar/agent/transformer/TransformerCore",
                        "transformForByte", "(Ljava/lang/String;[B)[B", false));

                mn.instructions.insertBefore(returnNode, il);

                LOGGER.info("=== Dumping modified method: " + mn.name + mn.desc + " ===");
                for (AbstractInsnNode node : mn.instructions) {
                    //wtf hard coded ahh
                    AsmUtil.dumpInsnContext("cpw/mods/cl/ModuleClassLoader", mn, node,
                            "HOOK_DEF: " + "transformForByte" + "@" + AsmUtil.getOpcodeName(node.getOpcode()));
                }
                LOGGER.info("=== End of dump ===");
            }
        }
    }

    static class SafeClassWriter extends ClassWriter {
        private final ClassLoader cl;

        SafeClassWriter(ClassReader cr, int flags, ClassLoader loader) {
            super(cr, flags);
            this.cl = loader;
        }

        @Override
        protected String getCommonSuperClass(String type1, String type2) {
            try {
                return super.getCommonSuperClass(type1, type2);
            } catch (Exception e) {
                LOGGER.warn("Failed to get common superclass for " + type1 + " and " + type2 + ", defaulting to Object");
                return "java/lang/Object";
            }
        }

        @Override
        protected ClassLoader getClassLoader() {
            return cl != null ? cl : super.getClassLoader();
        }
    }
}