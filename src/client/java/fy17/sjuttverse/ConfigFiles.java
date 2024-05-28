package fy17.sjuttverse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fy17.sjuttverse.renderhud.RenderHUD;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance();
                JsonSchema schema = schemaFactory.getSchema(new FileReader("path/to/your/schema.json"));

                // Load the JSON data you want to validate
                FileReader fileReader = new FileReader("path/to/your/json/file.json");
                // Parse the JSON data into a JSON object
                JsonElement jsonElement = JsonParser.parseReader(fileReader);

                // Validate the JSON data against the schema
                Set<ValidationMessage> validationResult = schema.validate(jsonElement);
            } catch (Exception e) {
                LOGGER.error("Failed to load element (" + file.getName() + ")! If you don't know what's wrong, please seek help in Sjuttverse discord server! Common reasons include extra / missing commas, missing quotation marks or other invalid json formats. Error:");
                LOGGER.error(String.valueOf(e));
            }
        }

        LOGGER.info("All elements have been reloaded.");
    }
}