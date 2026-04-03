package com.test.nosugar.transformer;

import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileModLocator;
import net.minecraftforge.fml.loading.moddiscovery.AbstractModProvider;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.stream.Stream;

public class NoSugarModLocator extends AbstractJarFileModLocator {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String RESOURCE_PATH = "/META-INF/jars/nosugar-main.jar.embed";
    private Path tempJarPath;

    @Override
    public Stream<Path> scanCandidates() {
        try {
            InputStream is = getClass().getResourceAsStream(RESOURCE_PATH);
            if (is == null) {
                LOGGER.error("Embedded JAR not found: {}", RESOURCE_PATH);
                return Stream.empty();
            }
            this.tempJarPath = Files.createTempFile("nosugar-main-", ".jar");
            this.tempJarPath.toFile().deleteOnExit();
            Files.copy(is, tempJarPath, StandardCopyOption.REPLACE_EXISTING);
            is.close();

            LOGGER.info("NoSugar successfully extracted to: {}", tempJarPath);
            return Stream.of(tempJarPath);

        } catch (IOException e) {
            LOGGER.error("Failed to extract embedded mod", e);
            return Stream.empty();
        }
    }

    @Override
    public String name() {
        return "nosugar";
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {
    }
}