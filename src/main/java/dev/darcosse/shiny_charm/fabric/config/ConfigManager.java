package dev.darcosse.shiny_charm.fabric.config;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final String CONFIG_FILE = "shiny_charm_config.json";
    private static ShinyCharmConfig config;
    private static final Gson GSON = new Gson();

    public static void loadConfig() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE);

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                config = GSON.fromJson(reader, ShinyCharmConfig.class);
            } catch (IOException e) {
                config = new ShinyCharmConfig();
            }
        } else {
            config = new ShinyCharmConfig();
            saveConfig();
        }
    }

    public static void saveConfig() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getShinyChance() {
        if (config == null) loadConfig();
        return config.shinyCharmChance;
    }

    public static void reloadConfig() {
        config = null;
        loadConfig();
    }

    public static int getCurrentShinyChance() {
        if (config == null) loadConfig();
        return config.shinyCharmChance;
    }
}