package com.tiers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigManager {
    private static Config config;
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("tiers.json");

    private static class Config {
        boolean toggleMod = true;
        boolean showIcons = true;
        boolean isSeparatorAdaptive = true;
        TiersClient.ModesTierDisplay displayMode;

        TiersClient.DisplayStatus mcTiersCOMPosition;
        TiersClient.Modes activeMCTiersCOMMode;

        TiersClient.DisplayStatus mcTiersIOPosition;
        TiersClient.Modes activeMCTiersIOMode;

        TiersClient.DisplayStatus subtiersNETPosition;
        TiersClient.Modes activeSubtiersNETMode;
    }

    public static void loadConfig() {
        Gson gson = new Gson();
        File configFile = configPath.toFile();
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                config = gson.fromJson(reader, Config.class);
                if (config == null)
                    restoreDefaultConfig();
            } catch (JsonSyntaxException | IOException exception) {
                restoreDefaultConfig();
            }
        } else
            restoreDefaultConfig();
        applyConfig();
    }

    private static void restoreDefaultConfig() {
        config = new Config();

        config.toggleMod = TiersClient.toggleMod;
        config.showIcons = TiersClient.showIcons;
        config.isSeparatorAdaptive = TiersClient.isSeparatorAdaptive;
        config.displayMode = TiersClient.displayMode;

        config.mcTiersCOMPosition = TiersClient.mcTiersCOMPosition;
        config.activeMCTiersCOMMode = TiersClient.activeMCTiersCOMMode;

        config.mcTiersIOPosition = TiersClient.mcTiersIOPosition;
        config.activeMCTiersIOMode = TiersClient.activeMCTiersIOMode;

        config.subtiersNETPosition = TiersClient.subtiersNETPosition;
        config.activeSubtiersNETMode = TiersClient.activeSubtiersNETMode;

        saveConfig();
    }

    private static void applyConfig() {
        TiersClient.toggleMod = config.toggleMod;
        TiersClient.showIcons = config.showIcons;
        TiersClient.isSeparatorAdaptive = config.isSeparatorAdaptive;
        TiersClient.displayMode = config.displayMode;

        TiersClient.mcTiersCOMPosition = config.mcTiersCOMPosition;
        TiersClient.activeMCTiersCOMMode = config.activeMCTiersCOMMode;

        TiersClient.mcTiersIOPosition = config.mcTiersIOPosition;
        TiersClient.activeMCTiersIOMode = config.activeMCTiersIOMode;

        TiersClient.subtiersNETPosition = config.subtiersNETPosition;
        TiersClient.activeSubtiersNETMode = config.activeSubtiersNETMode;

        saveConfig();
    }

    protected static void saveConfig() {
        Gson gson = new Gson();
        File configFile = configPath.toFile();
        Config currentConfig = new Config();

        currentConfig.toggleMod = TiersClient.toggleMod;
        currentConfig.showIcons = TiersClient.showIcons;
        currentConfig.isSeparatorAdaptive = TiersClient.isSeparatorAdaptive;
        currentConfig.displayMode = TiersClient.displayMode;

        currentConfig.mcTiersCOMPosition = TiersClient.mcTiersCOMPosition;
        currentConfig.activeMCTiersCOMMode = TiersClient.activeMCTiersCOMMode;

        currentConfig.mcTiersIOPosition = TiersClient.mcTiersIOPosition;
        currentConfig.activeMCTiersIOMode = TiersClient.activeMCTiersIOMode;

        currentConfig.subtiersNETPosition = TiersClient.subtiersNETPosition;
        currentConfig.activeSubtiersNETMode = TiersClient.activeSubtiersNETMode;

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(currentConfig, writer);
        } catch (IOException exception) {
            restoreDefaultConfig();
        }
    }
}