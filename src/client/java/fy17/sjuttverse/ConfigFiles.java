package fy17.sjuttverse;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

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

        ObjectMapper mapper = new ObjectMapper();
        File schemaFile = new File("../src/client/resources/premade/verify_schema.json");
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema jsonSchema = factory.getSchema(schemaFile.toURI());

        int fails = 0;
        for (File file : files) {
            try {
                JsonElement elm = JsonParser.parseReader(new FileReader(file));
                JsonNode jsonNode = mapper.readTree(file);
                Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
                if (errors.isEmpty()) {
                    elementArray.add(elm);
                } else {
                    LOGGER.error("Failed to load element file " + file.getName() + "! If you don't know what's wrong, please seek help in Sjuttverse discord server! This is most likely because of you having manually edited the file. Please use the in game editor if you don't know what you're doing. Error Code: 51");
                    for (ValidationMessage error : errors) {
                        LOGGER.error(error.getMessage());
                    }
                    fails += 1;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load element file " + file.getName() + "! If you don't know what's wrong, please seek help in Sjuttverse discord server! This might be caused by a missing comma or similar. This is most likely because of you having manually edited the file. Please use the in game editor if you don't know what you're doing. Error Code: 52");
                fails += 1;
            }
        }

        LOGGER.info(files.length - fails + "/" + files.length + " elements have successfully been reloaded.");
    }
}