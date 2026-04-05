package com.test.nosugar.utils;

import com.test.nosugar.NoSugar;
import com.sun.tools.attach.VirtualMachine;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class NSAgentLoader {

    private static volatile boolean loaded = false;

    public static boolean load() {
        if (loaded) return true;
        try {
            UnsafeUtils.allowAttachSelf();

            File agentJar = extractAgent();
            if (agentJar == null) {
                NoSugar.LOGGER.error("[NoSugar] Failed to extract embedded agent");
                return false;
            }
            agentJar.deleteOnExit();

            File transformerJar = getTransformerJar();

            System.setProperty("nosugar.agent.jar", agentJar.getAbsolutePath());
            if (transformerJar != null) {
                System.setProperty("nosugar.transformer.jar", transformerJar.getAbsolutePath());
                NoSugar.LOGGER.info("[NoSugar] Transformer JAR: " + transformerJar.getAbsolutePath());
            } else {
                NoSugar.LOGGER.warn("[NoSugar] Transformer JAR not found, some features may not work");
            }
            //NSBootstrap.setTransformerClassLoader(TransformerCore.class.getClassLoader());
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(agentJar.getAbsolutePath());
            vm.detach();

            NoSugar.LOGGER.info("[NoSugar] Agent loaded successfully");

            loaded = true;
            return true;
        } catch (Throwable t) {
            NoSugar.LOGGER.error("[NoSugar] Failed to load agent", t);
            return false;
        }
    }

    private static File getTransformerJar() {
        try {
            URL location = NSAgentLoader.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation();

            String path = location.toURI().getSchemeSpecificPart();

            if (path.contains("!")) {
                path = path.substring(0, path.indexOf("!"));
            }

            if (path.contains("#")) {
                path = path.substring(0, path.indexOf("#"));
            }

            path = path.replaceFirst("^file:", "");

            if (path.matches("^/[A-Za-z]:.*")) {
                path = path.substring(1);
            }

            path = URLDecoder.decode(path, StandardCharsets.UTF_8);

            File file = new File(path);

            if (file.exists() && file.isFile() && file.getName().endsWith(".jar")) {
                NoSugar.LOGGER.info("[NoSugar] Found transformer JAR: " + file.getAbsolutePath());
                return file;
            } else {
                NoSugar.LOGGER.warn("[NoSugar] Not running from JAR, path: " + file.getAbsolutePath() + ", exists: " + file.exists());
                return null;
            }
        } catch (Exception e) {
            NoSugar.LOGGER.error("[NoSugar] Failed to get transformer JAR path", e);
            return null;
        }
    }

    private static File extractAgent() throws IOException {
        InputStream is = NSAgentLoader.class.getResourceAsStream("/META-INF/jarjar/nosugar-agent.jar");
        if (is == null) {
            NoSugar.LOGGER.error("[NoSugar] Agent JAR not found in resources");
            return null;
        }
        File tmp = File.createTempFile("nosugar-agent", ".jar");
        tmp.deleteOnExit();
        try (FileOutputStream os = new FileOutputStream(tmp)) {
            is.transferTo(os);
        }
        NoSugar.LOGGER.info("[NoSugar] Extracted agent to: " + tmp.getAbsolutePath());
        return tmp;
    }
}