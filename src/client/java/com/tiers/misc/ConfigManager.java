package com.tiers.misc;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tiers.TiersClient;
import com.tiers.textures.Icons;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;

public class ConfigManager {
    private static Config config;
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("Tiers.json");
    private static String version;
    private static boolean toastShown;
    private static int launchTickCounter;

    static {
        FabricLoader.getInstance().getModContainer("tiers").ifPresent(tiers -> version = tiers.getMetadata().getVersion().getFriendlyString());
    }

    private static class Config {
        boolean toggleMod;
        boolean toggleIcons;
        boolean toggleTab;
        boolean toggleChat;
        boolean toggleAdaptiveSeparator;
        boolean toggleAutoKitDetect;
        TiersClient.ModesTierDisplay displayMode;
        Icons.Type activeIcons;

        TiersClient.DisplayStatus positionMCTiers;
        Mode activeMCTiersMode;

        TiersClient.DisplayStatus positionPvPTiers;
        Mode activePvPTiersMode;

        TiersClient.DisplayStatus positionSubtiers;
        Mode activeSubtiersMode;

        String version;
    }

    public static void loadConfig() {
        Gson gson = new Gson();
        File file = CONFIG_PATH.toFile();
        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                config = gson.fromJson(fileReader, Config.class);
                if (config == null)
                    restoreFromClient();
            } catch (IOException | JsonSyntaxException ignored) {
                restoreFromClient();
            }
        } else
            restoreFromClient();

        TiersClient.toggleMod = config.toggleMod;
        TiersClient.toggleIcons = config.toggleIcons;
        TiersClient.toggleTab = config.toggleTab;
        TiersClient.toggleChat = config.toggleChat;
        TiersClient.toggleAdaptiveSeparator = config.toggleAdaptiveSeparator;
        TiersClient.toggleAutoKitDetect = config.toggleAutoKitDetect;

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

        if (!version.equals(config.version)) {
            // Code below is reserved to the current version (not every version upgrade changes config values)

            ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
                if (toastShown)
                    return;

                if (minecraftClient.currentScreen instanceof TitleScreen) {
                    launchTickCounter++;

                    if (launchTickCounter >= 20) {
                        minecraftClient.getToastManager().add(SystemToast.create(minecraftClient, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Thanks for updating Tiers"), Text.of("Some settings may have changed")));
                        TiersClient.toggleMod = true;
                        TiersClient.toggleIcons = true;
                        TiersClient.toggleTab = true;
                        TiersClient.toggleChat = true;
                        TiersClient.toggleAdaptiveSeparator = true;
                        TiersClient.toggleAutoKitDetect = false;

                        saveConfig();

                        toastShown = true;
                    }
                }
            });
        }

        saveConfig();
    }

    private static void restoreFromClient() {
        config = new Config();

        config.toggleMod = TiersClient.toggleMod;
        config.toggleIcons = TiersClient.toggleIcons;
        config.toggleTab = TiersClient.toggleTab;
        config.toggleChat = TiersClient.toggleChat;
        config.toggleAdaptiveSeparator = TiersClient.toggleAdaptiveSeparator;
        config.toggleAutoKitDetect = TiersClient.toggleAutoKitDetect;
        config.displayMode = TiersClient.displayMode;
        config.activeIcons = TiersClient.activeIcons;

        config.positionMCTiers = TiersClient.positionMCTiers;
        config.activeMCTiersMode = TiersClient.activeMCTiersMode;

        config.positionPvPTiers = TiersClient.positionPvPTiers;
        config.activePvPTiersMode = TiersClient.activePvPTiersMode;

        config.positionSubtiers = TiersClient.positionSubtiers;
        config.activeSubtiersMode = TiersClient.activeSubtiersMode;

        config.version = version;

        TiersClient.LOGGER.info("Broken config file: Tiers has restored values from the client memory");

        saveConfig();
    }

    public static void saveConfig() {
        Gson gson = new Gson();
        File file = CONFIG_PATH.toFile();
        Config config = new Config();

        config.toggleMod = TiersClient.toggleMod;
        config.toggleIcons = TiersClient.toggleIcons;
        config.toggleTab = TiersClient.toggleTab;
        config.toggleChat = TiersClient.toggleChat;
        config.toggleAdaptiveSeparator = TiersClient.toggleAdaptiveSeparator;
        config.toggleAutoKitDetect = TiersClient.toggleAutoKitDetect;
        config.displayMode = TiersClient.displayMode;
        config.activeIcons = TiersClient.activeIcons;

        config.positionMCTiers = TiersClient.positionMCTiers;
        config.activeMCTiersMode = TiersClient.activeMCTiersMode;

        config.positionPvPTiers = TiersClient.positionPvPTiers;
        config.activePvPTiersMode = TiersClient.activePvPTiersMode;

        config.positionSubtiers = TiersClient.positionSubtiers;
        config.activeSubtiersMode = TiersClient.activeSubtiersMode;

        config.version = version;

        try (FileWriter fileWriter = new FileWriter(file)) {
            gson.toJson(config, fileWriter);
        } catch (IOException ignored) {
            restoreFromClient();
        } finally {
            TiersClient.updateAllTags();
        }
    }
}