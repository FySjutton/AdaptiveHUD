package ahud.adaptivehud;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ahud.adaptivehud.adaptivehud.LOGGER;

public class ConfigFiles {
    public static List<JsonElement> elementArray = new ArrayList<>();
    public static JsonElement configFile;

    private void generateConfigFile() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path targetPath = Paths.get(configDir + "/adaptivehud/config.json");
        try {
            URL resource = ConfigFiles.class.getResource("/assets/adaptivehud/premade/default_setup/config.json");
            Path resourceFile = Paths.get(resource.toURI());
            Files.copy(resourceFile, targetPath);
        } catch (Exception e) {
            LOGGER.error("AdaptiveHUD - Could not generate a new config.json file, the program will now close. This error should not normally occur, and if you need help, please join our discord server. This error indicates that there's something wrong with the jar file, or the program doesn't have access to write files.");
            LOGGER.error("Shutting down minecraft..."); // Should just inactivate mod instead?
            LOGGER.error(e.toString());
            MinecraftClient.getInstance().stop();
        }
    }
    private void generateElementFolder() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path targetDir = Paths.get(configDir + "/adaptivehud/elements");
        try {
            URL resource = ConfigFiles.class.getResource("/assets/adaptivehud/premade/default_setup/elements");
            File resourceFile = Paths.get(resource.toURI()).toFile();
            FileUtils.copyDirectory(resourceFile, targetDir.toFile());
        } catch (Exception e) {
            LOGGER.error("AdaptiveHUD - Could not generate a new element folder, the program will now close. This error should not normally occur, and if you need help, please join our discord server. This error indicates that there's something wrong with the jar file, or the program doesn't have access to write files.");
            LOGGER.error(e.getMessage());
            MinecraftClient.getInstance().stop();
        }
    }

    public void CheckDefaultConfigs() {
        Path configDir = FabricLoader.getInstance().getConfigDir();

        if (Files.notExists(configDir.resolve("adaptivehud"))) {
            LOGGER.warn("Configuration folder not found - generating folder and example files.");
            new File(configDir + "/adaptivehud").mkdir();

            generateElementFolder();
            generateConfigFile();
        } else {
            if (Files.notExists(configDir.resolve("adaptivehud/config.json"))) {
                LOGGER.warn("Partial corrupted configuration setup found! Will now generate a new config file.");
                generateConfigFile();
            }
            if (Files.notExists(configDir.resolve("adaptivehud/elements"))) {
                LOGGER.warn("Partial corrupted configuration setup found! Will now generate a new element folder with default files.");
                generateElementFolder();
            }
        }
    }

    public void GenerateElementArray() {
        elementArray.clear();
        File[] files = new File(FabricLoader.getInstance().getConfigDir() + "/adaptivehud/elements").listFiles();
        int fails = 0;
        List<String> names = new ArrayList<>();

        List<List<String>> problems = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                List<String> problemsFile = new ArrayList<>();
                try {
                    FileReader fileReader = new FileReader(file);
                    JsonElement elm = JsonParser.parseReader(fileReader);
                    fileReader.close();

                    String validated = new jsonValidator().validateElement(elm.getAsJsonObject());
                    if (validated == null) {
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
                        problemsFile.add(validated);
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
        } else {
            LOGGER.warn("No element files detected!");
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

        try {
            File config = new File(configDir + "/adaptivehud/config.json");
            FileReader fileReader = new FileReader(config);
            JsonElement elm = JsonParser.parseReader(fileReader);
            fileReader.close();
            String validated = new jsonValidator().validateConfig(elm.getAsJsonObject());
            if (validated != null) {
                LOGGER.error(validated);
                LOGGER.error("[CRITICAL] - Configuration file could not be read properly. This is most likely because of a missing key or similar, the file does not follow the required format. For help, please seek help in adaptivehud discord server.");
                LOGGER.error("The game will now close because of the error above, invalid configuration file.");
                MinecraftClient.getInstance().stop();
                return;
            }
            configFile = elm;
        } catch (Exception e) {
            LOGGER.error("AdaptiveHUD - Could not load configuration file, this is most likely because of the file not following proper json syntax, like a missing comma or similar. For help, please seek help in adaptivehud discord server.");
            LOGGER.error(e.getMessage());
            MinecraftClient.getInstance().stop();
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