package fy17.sjuttverse;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;

import static fy17.sjuttverse.Sjuttverse.LOGGER;

public class ConfigFiles {
    public static List<JsonElement> elementArray = new ArrayList<>();
    public static JsonElement configFile;
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
        List<String> names = new ArrayList<>();

        List<List<String>> problems = new ArrayList<>();

        for (File file : files) {
            List<String> problemsFile = new ArrayList<>();
            try {
                JsonElement elm = JsonParser.parseReader(new FileReader(file));
                JsonNode jsonNode = mapper.readTree(file);
                Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
                if (errors.isEmpty()) {
                    String name = elm.getAsJsonObject().get("name").getAsString();
                    if (names.contains(name)) {
                        problemsFile.add(file.getName());
                        problemsFile.add("Key name must be unique!");
                        LOGGER.error("Failed to load element file " + file.getName() + "! The identifier (\"name\" key) must be unique!");
                        fails += 1;
                    } else {
                        elementArray.add(elm);
                        names.add(name);
                    }
                } else {
                    LOGGER.error("Failed to load element file " + file.getName() + "! If you don't know what's wrong, please seek help in Sjuttverse discord server! This is most likely because of you having manually edited the file. Please use the in game editor if you don't know what you're doing. Error Code: 51");
                    problemsFile.add(file.getName());
                    String errorMessage = "";
                    problemsFile.add("Key name must be unique!");
                    for (ValidationMessage error : errors) {
                        LOGGER.error(error.getMessage());
                        errorMessage += " " + error;
                    }
                    problemsFile.add(errorMessage);
                    fails += 1;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load element file " + file.getName() + "! If you don't know what's wrong, please seek help in Sjuttverse discord server! This might be caused by a missing comma or similar. This is most likely because of you having manually edited the file. Please use the in game editor if you don't know what you're doing. Error Code: 52");
                fails += 1;
                problemsFile.add(file.getName());
                problemsFile.add("Invalid json format or similar!");
            }
            if (problemsFile.size() > 0) {
                problems.add(problemsFile);
            }
        }

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (textRenderer != null) {
            for (List<String> problem : problems) {
                MinecraftClient.getInstance().getToastManager().add(
                        new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,
                                Text.literal("§c" + problem.get(0)),
                                Text.literal("§f" + problem.get(1))
                        )
                );
            }
            MinecraftClient.getInstance().getToastManager().add(
                    new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,
                            Text.literal("§aElements Reloaded!"),
                            Text.literal("§e" + (files.length - fails) + "/" + files.length + " elements have successfully been reloaded.")
                    )
            );
        }
    }

    public void generateConfigArray() {
        Path configDir = FabricLoader.getInstance().getConfigDir();

        ObjectMapper mapper = new ObjectMapper();
        File schemaFile = new File("../src/client/resources/premade/config_schema.json");
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema jsonSchema = factory.getSchema(schemaFile.toURI());

        try {
            File config = new File(configDir + "/Sjuttverse/config.json");
            JsonElement elm = JsonParser.parseReader(new FileReader(config));
            JsonNode jsonNode = mapper.readTree(config);
            Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);

            if (!errors.isEmpty()) {
                LOGGER.error("[CRITICAL] - Configuration file could not be read properly. This is most likely because of a missing key or similar, the file does not follow the required format. For help, please seek help in Sjuttverse discord server. Exiting minecraft.");
                for (ValidationMessage error : errors) {
                    LOGGER.error(error.getMessage());
                }
                MinecraftClient.getInstance().stop();
            }
            configFile = elm;
        } catch (Exception e) {
            LOGGER.error("[CRITICAL] - CONFIGURATION LOADING FAILED.");
            throw new RuntimeException(e);
        }
    }

    public void saveElementFiles(List<JsonElement> elmArray) {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        int fails = 0;
        for (JsonElement elm : elmArray) {
            try {
                JsonObject obj = elm.getAsJsonObject();
                String fileName = obj.get("name").getAsString() + ".json";
                File elmFile = new File(configDir + "/Sjuttverse/elements/" + fileName);
                if (elmFile.exists()) {
                    try (FileWriter writer = new FileWriter(elmFile)) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        JsonWriter jsonWriter = gson.newJsonWriter(writer);
                        gson.toJson(obj, jsonWriter);
                        jsonWriter.flush();
                    }
                } else {
                    Files.createDirectories(elmFile.getParentFile().toPath());
                    try (FileWriter writer = new FileWriter(elmFile)) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        JsonWriter jsonWriter = gson.newJsonWriter(writer);
                        gson.toJson(obj, jsonWriter);
                        jsonWriter.flush();
                    }
                    LOGGER.info("Created new element file: " + elmFile);
                }
            } catch (Exception e) {
                LOGGER.error("Error saving element file: " + e.getMessage());
                MinecraftClient.getInstance().getToastManager().add(
                        new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,
                                Text.literal("§cFailed to save file!"),
                                Text.literal("§f" + e.getMessage())
                        )
                );
                fails++;
            }
        }
        MinecraftClient.getInstance().getToastManager().add(
                new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,
                        Text.literal("§aChanges have been saved!"),
                        Text.literal("§e" + (elmArray.size() - fails) + "/" + elmArray.size() + " files successfully saved.")
                )
        );
    }
}