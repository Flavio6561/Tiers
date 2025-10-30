package com.tiers.misc;

import com.google.gson.Gson;
import com.tiers.TiersClient;
import com.tiers.textures.Icons;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;

public class ConfigManager {
    private static Config config;
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("Tiers.json");

    private static class Config {
        boolean toggleMod;
        boolean toggleTab;
        boolean showIcons;
        boolean isSeparatorAdaptive;
        boolean autoKitDetect;
        TiersClient.ModesTierDisplay displayMode;
        Icons.Type activeIcons;

        TiersClient.DisplayStatus positionMCTiers;
        Mode activeMCTiersMode;

        TiersClient.DisplayStatus positionPvPTiers;
        Mode activePvPTiersMode;

        TiersClient.DisplayStatus positionSubtiers;
        Mode activeSubtiersMode;
    }

    public static void loadConfig() {
        Gson gson = new Gson();
        File file = CONFIG_PATH.toFile();
        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                config = gson.fromJson(fileReader, Config.class);
                if (config == null)
                    restoreFromClient();
            } catch (IOException ignored) {
                restoreFromClient();
            }
        } else
            restoreFromClient();

        TiersClient.toggleMod = config.toggleMod;
        TiersClient.toggleTab = config.toggleTab;
        TiersClient.showIcons = config.showIcons;
        TiersClient.isSeparatorAdaptive = config.isSeparatorAdaptive;
        TiersClient.autoKitDetect = config.autoKitDetect;

        if (Arrays.stream(TiersClient.ModesTierDisplay.values()).toList().contains(config.displayMode))
            TiersClient.displayMode = config.displayMode;

        if (Arrays.stream(Icons.Type.values()).toList().contains(config.activeIcons))
            TiersClient.activeIcons = config.activeIcons;

        if (Arrays.stream(TiersClient.DisplayStatus.values()).toList().contains(config.positionMCTiers))
            TiersClient.positionMCTiers = config.positionMCTiers;
        if (Arrays.stream(Mode.values()).toList().contains(config.activeMCTiersMode) && config.activeMCTiersMode.toString().contains("MCTIERS"))
            TiersClient.activeMCTiersMode = config.activeMCTiersMode;

        if (Arrays.stream(TiersClient.DisplayStatus.values()).toList().contains(config.positionPvPTiers))
            TiersClient.positionPvPTiers = config.positionPvPTiers;
        if (Arrays.stream(Mode.values()).toList().contains(config.activePvPTiersMode) && config.activePvPTiersMode.toString().contains("PVPTIERS"))
            TiersClient.activePvPTiersMode = config.activePvPTiersMode;

        if (Arrays.stream(TiersClient.DisplayStatus.values()).toList().contains(config.positionSubtiers))
            TiersClient.positionSubtiers = config.positionSubtiers;
        if (Arrays.stream(Mode.values()).toList().contains(config.activeSubtiersMode) && config.activeSubtiersMode.toString().contains("SUBTIERS"))
            TiersClient.activeSubtiersMode = config.activeSubtiersMode;

        saveConfig();
    }

    private static void restoreFromClient() {
        config = new Config();

        config.toggleMod = TiersClient.toggleMod;
        config.toggleTab = TiersClient.toggleTab;
        config.showIcons = TiersClient.showIcons;
        config.isSeparatorAdaptive = TiersClient.isSeparatorAdaptive;
        config.autoKitDetect = TiersClient.autoKitDetect;
        config.displayMode = TiersClient.displayMode;
        config.activeIcons = TiersClient.activeIcons;

        config.positionMCTiers = TiersClient.positionMCTiers;
        config.activeMCTiersMode = TiersClient.activeMCTiersMode;

        config.positionPvPTiers = TiersClient.positionPvPTiers;
        config.activePvPTiersMode = TiersClient.activePvPTiersMode;

        config.positionSubtiers = TiersClient.positionSubtiers;
        config.activeSubtiersMode = TiersClient.activeSubtiersMode;

        saveConfig();
    }

    public static void saveConfig() {
        Gson gson = new Gson();
        File file = CONFIG_PATH.toFile();
        Config config = new Config();

        config.toggleMod = TiersClient.toggleMod;
        config.toggleTab = TiersClient.toggleTab;
        config.showIcons = TiersClient.showIcons;
        config.isSeparatorAdaptive = TiersClient.isSeparatorAdaptive;
        config.autoKitDetect = TiersClient.autoKitDetect;
        config.displayMode = TiersClient.displayMode;
        config.activeIcons = TiersClient.activeIcons;

        config.positionMCTiers = TiersClient.positionMCTiers;
        config.activeMCTiersMode = TiersClient.activeMCTiersMode;

        config.positionPvPTiers = TiersClient.positionPvPTiers;
        config.activePvPTiersMode = TiersClient.activePvPTiersMode;

        config.positionSubtiers = TiersClient.positionSubtiers;
        config.activeSubtiersMode = TiersClient.activeSubtiersMode;

        try (FileWriter fileWriter = new FileWriter(file)) {
            gson.toJson(config, fileWriter);
        } catch (IOException ignored) {
            restoreFromClient();
        }
    }
}