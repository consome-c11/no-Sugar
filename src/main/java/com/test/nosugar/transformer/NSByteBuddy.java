package com.test.nosugar.transformer;

import com.test.nosugar.transformer.transformers.ModLauncherAdvice;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import java.lang.instrument.Instrumentation;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;

public class NSByteBuddy {
    public static void run() {
        try {
            ClassLoader transformerCL = NSByteBuddy.class.getClassLoader();
            NoSugarAdviceBootstrap.setTransformerClassLoader(transformerCL);
            Instrumentation inst = ByteBuddyAgent.install();
            if (inst == null) {
                TransformerCore.LOGGER.error("Failed to install ByteBuddy Agent.");
                return;
            }

            injectToBootstrap(inst, TransformerCore.class);

            String targetClassName = "cpw.mods.modlauncher.ClassTransformer";

            new AgentBuilder.Default()
                    .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                    .with(AgentBuilder.RedefinitionStrategy.DiscoveryStrategy.Reiterating.INSTANCE)
                    .disableClassFormatChanges()

                    .type(ElementMatchers.named(targetClassName))
                    .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                        TransformerCore.LOGGER.info("[ByteBuddy] Applying transform to: {}", typeDescription.getName());
                        return builder.visit(ModLauncherAdvice.getAdvice());
                    })
                    .installOn(inst);

            TransformerCore.LOGGER.info("ByteBuddy Hook installed! Target: {}", targetClassName);

        } catch (Exception e) {
            TransformerCore.LOGGER.error("ByteBuddy Failed!", e);
            e.printStackTrace();
        }
    }

    private static void injectToBootstrap(Instrumentation inst, Class<?> clazz) {
        try {
            ProtectionDomain pd = clazz.getProtectionDomain();
            if (pd == null) {
                TransformerCore.LOGGER.warn("No ProtectionDomain for {}", clazz.getName());
                return;
            }

            CodeSource cs = pd.getCodeSource();
            if (cs == null) {
                TransformerCore.LOGGER.warn("No CodeSource for {}", clazz.getName());
                return;
            }

            URL location = cs.getLocation();
            if (location == null) {
                TransformerCore.LOGGER.warn("No location for {}", clazz.getName());
                return;
            }

            File jarFile = getJarFileFromUrl(location);
            if (jarFile != null && jarFile.exists()) {
                inst.appendToBootstrapClassLoaderSearch(new JarFile(jarFile));
                TransformerCore.LOGGER.info("Injected to Bootstrap: {}", jarFile.getAbsolutePath());
            } else {
                TransformerCore.LOGGER.warn("Could not resolve JAR file from: {}", location);
            }
        } catch (Exception e) {
            TransformerCore.LOGGER.error("Failed to inject to Bootstrap", e);
        }
    }

    private static File getJarFileFromUrl(URL url) {
        try {
            String protocol = url.getProtocol();
            String path = url.getPath();

            if ("union".equals(protocol)) {
                int hashIndex = path.indexOf("%23");
                if (hashIndex > 0) {
                    String jarPath = path.substring(0, hashIndex);
                    if (jarPath.startsWith("/") && jarPath.matches("^/[A-Za-z]:.*")) {
                        jarPath = jarPath.substring(1);
                    }
                    return new File(jarPath);
                }
                int exclamationIndex = path.indexOf('!');
                if (exclamationIndex > 0) {
                    String jarPath = path.substring(0, exclamationIndex);
                    if (jarPath.startsWith("/") && jarPath.matches("^/[A-Za-z]:.*")) {
                        jarPath = jarPath.substring(1);
                    }
                    return new File(jarPath);
                }
            }
            if ("jar".equals(protocol)) {
                int separator = path.indexOf('!');
                if (separator > 0) {
                    String jarPath = path.substring(0, separator);
                    URI jarUri = new URI(jarPath);
                    return new File(jarUri);
                }
            }
            if ("file".equals(protocol)) {
                return new File(url.toURI());
            }
            if ("jrt".equals(protocol) || "module".equals(protocol)) {
                TransformerCore.LOGGER.warn("Cannot inject from module/jrt URL: {}", url);
                return null;
            }
            URI uri = url.toURI();
            if ("file".equals(uri.getScheme())) {
                return new File(uri);
            }
        } catch (URISyntaxException e) {
            TransformerCore.LOGGER.error("URI syntax error: {}", url, e);
        } catch (Exception e) {
            TransformerCore.LOGGER.error("Error processing URL: {}", url, e);
        }
        return null;
    }
}