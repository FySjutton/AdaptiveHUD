package fy17.sjuttverse;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static fy17.sjuttverse.Sjuttverse.LOGGER;

public class ConfigFiles {
    public static List<JsonElement> elementArray = new ArrayList<>();
    public void CheckDefaultConfigs() {
        Path configDir = FabricLoader.getInstance().getConfigDir();

        if (Files.notExists(configDir.resolve("Sjuttverse"))) {
            LOGGER.warn("Configuration folder not found - generating folder and example files.");
            new File(configDir + "/Sjuttverse").mkdir();
            Path sourceDir = Paths.get("../src/client/resources/premade/default_setup");
            Path targetDir = Paths.get(configDir + "/Sjuttverse");

            try {
                FileUtils.copyDirectory(sourceDir.toFile(), targetDir.toFile());
            } catch (IOException e) {
                LOGGER.error("[CRITICAL] - CONFIGURATION LOADING FAILED.");
                throw new RuntimeException(e);
            }
        }
    }

    public void GenerateElementArray() {
        elementArray.clear();
        File[] files = new File(FabricLoader.getInstance().getConfigDir() + "/Sjuttverse/elements").listFiles();

        for (File file : files) {
            JsonElement jsonElement = null;
            try {
                jsonElement = JsonParser.parseReader(new FileReader(file));
                elementArray.add(jsonElement);
            } catch (Exception e) {
                LOGGER.error("Failed to load element (" + file.getName() + ")! If you don't know what's wrong, please seek help in Sjuttverse discord server! Common reasons include extra / missing commas, missing quotation marks or other invalid json formats. Error:");
                LOGGER.error(String.valueOf(e));
            }
        }

        LOGGER.info("All elements have been reloaded.");
    }
}