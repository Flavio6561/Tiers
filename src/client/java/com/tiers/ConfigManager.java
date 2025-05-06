package com.tiers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class ConfigManager {
    private static Config config;
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("Tiers.json");

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
        if (Arrays.stream(TiersClient.ModesTierDisplay.values()).toList().contains(config.displayMode))
            TiersClient.displayMode = config.displayMode;

        if (Arrays.stream(TiersClient.DisplayStatus.values()).toList().contains(config.mcTiersCOMPosition))
            TiersClient.mcTiersCOMPosition = config.mcTiersCOMPosition;
        if (Arrays.stream(TiersClient.Modes.values()).toList().contains(config.activeMCTiersCOMMode) && config.activeMCTiersCOMMode.toString().contains("MCTIERSCOM"))
            TiersClient.activeMCTiersCOMMode = config.activeMCTiersCOMMode;

        if (Arrays.stream(TiersClient.DisplayStatus.values()).toList().contains(config.mcTiersIOPosition))
            TiersClient.mcTiersIOPosition = config.mcTiersIOPosition;
        if (Arrays.stream(TiersClient.Modes.values()).toList().contains(config.activeMCTiersIOMode) && config.activeMCTiersIOMode.toString().contains("MCTIERSIO"))
            TiersClient.activeMCTiersIOMode = config.activeMCTiersIOMode;

        if (Arrays.stream(TiersClient.DisplayStatus.values()).toList().contains(config.subtiersNETPosition))
            TiersClient.subtiersNETPosition = config.subtiersNETPosition;
        if (Arrays.stream(TiersClient.Modes.values()).toList().contains(config.activeSubtiersNETMode) && config.activeSubtiersNETMode.toString().contains("SUBTIERSNET"))
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