package fy17.adaptivehud;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
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

import static fy17.adaptivehud.adaptivehud.LOGGER;

public class ConfigFiles {
    public static List<JsonElement> elementArray = new ArrayList<>();
    public static JsonElement configFile;
    public void CheckDefaultConfigs() {
        Path configDir = FabricLoader.getInstance().getConfigDir();

        if (Files.notExists(configDir.resolve("adaptivehud"))) {
            LOGGER.warn("Configuration folder not found - generating folder and example files.");
            new File(configDir + "/adaptivehud").mkdir();
            Path sourceDir = Paths.get("../src/client/resources/premade/default_setup");
            Path targetDir = Paths.get(configDir + "/adaptivehud");

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
        File[] files = new File(FabricLoader.getInstance().getConfigDir() + "/adaptivehud/elements").listFiles();

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
                FileReader fileReader = new FileReader(file);
                JsonElement elm = JsonParser.parseReader(fileReader);
                fileReader.close();
                JsonNode jsonNode = mapper.readTree(file);
                Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
                if (errors.isEmpty()) {
                    String name = elm.getAsJsonObject().get("name").getAsString();
                    if (names.contains(name.toLowerCase())) {
                        problemsFile.add(file.getName());
                        problemsFile.add("Key name must be unique!");
                        LOGGER.error("Failed to load element file " + file.getName() + "! The identifier (\"name\" key) must be unique!");
                        fails += 1;
                    } else {
                        elementArray.add(elm);
                        names.add(name.toLowerCase());
                    }
                } else {
                    LOGGER.error("Failed to load element file " + file.getName() + "! If you don't know what's wrong, please seek help in adaptivehud discord server! This is most likely because of you having manually edited the file. Please use the in game editor if you don't know what you're doing. Error Code: 51");
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
                LOGGER.error("Failed to load element file " + file.getName() + "! If you don't know what's wrong, please seek help in adaptivehud discord server! This might be caused by a missing comma or similar. This is most likely because of you having manually edited the file. Please use the in game editor if you don't know what you're doing. Error Code: 52");
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
                sendToast("§c" + problem.get(0), "§f" + problem.get(1));
            }
            sendToast("§aElements Reloaded!", "§e" + (files.length - fails) + "/" + files.length + " elements have successfully been reloaded.");
        }
    }

    public void generateConfigArray() {
        Path configDir = FabricLoader.getInstance().getConfigDir();

        ObjectMapper mapper = new ObjectMapper();
        File schemaFile = new File("../src/client/resources/premade/config_schema.json");
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema jsonSchema = factory.getSchema(schemaFile.toURI());

        try {
            File config = new File(configDir + "/adaptivehud/config.json");
            FileReader fileReader = new FileReader(config);
            JsonElement elm = JsonParser.parseReader(fileReader);
            fileReader.close();
            JsonNode jsonNode = mapper.readTree(config);
            Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);

            if (!errors.isEmpty()) {
                LOGGER.error("[CRITICAL] - Configuration file could not be read properly. This is most likely because of a missing key or similar, the file does not follow the required format. For help, please seek help in adaptivehud discord server. Exiting minecraft.");
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

    public void saveElementFiles(List<JsonElement> elmArray, List<String> deletedFiles) {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        int fails = 0;
        for (String fileName : deletedFiles) {
            try {
                File delFile = new File(configDir + "/adaptivehud/elements/" + fileName + ".json");
                if (delFile.exists()) {
                    Files.delete(delFile.toPath());
                } else {
                    LOGGER.warn("Could not delete element file " + fileName + ".json, because the file doesn't exist.");
                    fails++;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to delete element file " + fileName + ".json! For help, please join our discord. Error:");
                LOGGER.error(String.valueOf(e));
                sendToast("§cFailed to delete file!", "§fCheck console for further information");
                fails++;
            }
        }

        for (JsonElement elm : elmArray) {
            try {
                JsonObject obj = elm.getAsJsonObject();
                String fileName = obj.get("name").getAsString().toLowerCase() + ".json";
                File elmFile = new File(configDir + "/adaptivehud/elements/" + fileName);
                if (elmFile.exists()) {
                    FileReader fileReader = new FileReader(elmFile);
                    JsonElement parsed = JsonParser.parseReader(fileReader);
                    fileReader.close();
                    if (!parsed.equals(elm)) {
                        FileWriter writer = new FileWriter(elmFile);
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
                }
            } catch (Exception e) {
                LOGGER.error("Error saving element file: " + e.getMessage());
                sendToast("§cFailed to save file!", "§f" + e.getMessage());
                fails++;
            }
        }
        sendToast("§aChanges have been saved!", "§e" + fails + " errors encountered!");
    }

    public boolean validateJson(JsonElement elm) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File schemaFile = new File("../src/client/resources/premade/verify_schema.json");
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
            JsonSchema jsonSchema = factory.getSchema(schemaFile.toURI());

            JsonNode jsonNode = mapper.readTree(elm.toString());
            Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
            return errors.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // Should not be in this file
    public void sendToast(String title, String description) {
        try {
            MinecraftClient.getInstance().getToastManager().add(
                    new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,
                            Text.literal(title),
                            Text.literal(description)
                    )
            );
        } catch (Exception e) {
            LOGGER.warn("Failed to display toast! Error:");
            LOGGER.error(String.valueOf(e));
        }
    }
}