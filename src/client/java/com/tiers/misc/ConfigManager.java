package com.tiers.misc;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tiers.TiersClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;

public class ConfigManager {
    private static Config config;
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("Tiers.json");

    private static class Config {
        boolean toggleMod = true;
        boolean showIcons = true;
        boolean isSeparatorAdaptive = true;
        TiersClient.ModesTierDisplay displayMode;

        TiersClient.DisplayStatus positionMCTiers;
        Modes activeMCTiersMode;

        TiersClient.DisplayStatus positionPvPTiers;
        Modes activePvPTiersMode;

        TiersClient.DisplayStatus positionSubtiers;
        Modes activeSubtiersMode;

        boolean anonymousUserAgent;
    }

    public static void loadConfig() {
        Gson gson = new Gson();
        File configFile = CONFIG_PATH.toFile();
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                config = gson.fromJson(reader, Config.class);
                if (config == null)
                    restoreFromClient();
            } catch (JsonSyntaxException | IOException exception) {
                restoreFromClient();
            }
        } else
            restoreFromClient();

        TiersClient.toggleMod = config.toggleMod;
        TiersClient.showIcons = config.showIcons;
        TiersClient.isSeparatorAdaptive = config.isSeparatorAdaptive;

        if (Arrays.stream(TiersClient.ModesTierDisplay.values()).toList().contains(config.displayMode))
            TiersClient.displayMode = config.displayMode;

        if (Arrays.stream(TiersClient.DisplayStatus.values()).toList().contains(config.positionMCTiers))
            TiersClient.positionMCTiers = config.positionMCTiers;
        if (Arrays.stream(Modes.values()).toList().contains(config.activeMCTiersMode) && config.activeMCTiersMode.toString().contains("MCTIERS"))
            TiersClient.activeMCTiersMode = config.activeMCTiersMode;

        if (Arrays.stream(TiersClient.DisplayStatus.values()).toList().contains(config.positionPvPTiers))
            TiersClient.positionPvPTiers = config.positionPvPTiers;
        if (Arrays.stream(Modes.values()).toList().contains(config.activePvPTiersMode) && config.activePvPTiersMode.toString().contains("PVPTIERS"))
            TiersClient.activePvPTiersMode = config.activePvPTiersMode;

        if (Arrays.stream(TiersClient.DisplayStatus.values()).toList().contains(config.positionSubtiers))
            TiersClient.positionSubtiers = config.positionSubtiers;
        if (Arrays.stream(Modes.values()).toList().contains(config.activeSubtiersMode) && config.activeSubtiersMode.toString().contains("SUBTIERS"))
            TiersClient.activeSubtiersMode = config.activeSubtiersMode;

        TiersClient.anonymousUserAgent = config.anonymousUserAgent;

        saveConfig();
    }

    private static void restoreFromClient() {
        config = new Config();

        config.toggleMod = TiersClient.toggleMod;
        config.showIcons = TiersClient.showIcons;
        config.isSeparatorAdaptive = TiersClient.isSeparatorAdaptive;
        config.displayMode = TiersClient.displayMode;

        config.positionMCTiers = TiersClient.positionMCTiers;
        config.activeMCTiersMode = TiersClient.activeMCTiersMode;

        config.positionPvPTiers = TiersClient.positionPvPTiers;
        config.activePvPTiersMode = TiersClient.activePvPTiersMode;

        config.positionSubtiers = TiersClient.positionSubtiers;
        config.activeSubtiersMode = TiersClient.activeSubtiersMode;

        config.anonymousUserAgent = TiersClient.anonymousUserAgent;

        saveConfig();
    }

    public static void saveConfig() {
        Gson gson = new Gson();
        File configFile = CONFIG_PATH.toFile();
        Config currentConfig = new Config();

        currentConfig.toggleMod = TiersClient.toggleMod;
        currentConfig.showIcons = TiersClient.showIcons;
        currentConfig.isSeparatorAdaptive = TiersClient.isSeparatorAdaptive;
        currentConfig.displayMode = TiersClient.displayMode;

        currentConfig.positionMCTiers = TiersClient.positionMCTiers;
        currentConfig.activeMCTiersMode = TiersClient.activeMCTiersMode;

        currentConfig.positionPvPTiers = TiersClient.positionPvPTiers;
        currentConfig.activePvPTiersMode = TiersClient.activePvPTiersMode;

        currentConfig.positionSubtiers = TiersClient.positionSubtiers;
        currentConfig.activeSubtiersMode = TiersClient.activeSubtiersMode;

        currentConfig.anonymousUserAgent = TiersClient.anonymousUserAgent;

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(currentConfig, writer);
        } catch (IOException exception) {
            restoreFromClient();
        }
    }
}