package com.tiers.textures;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tiers.TiersClient;
import com.tiers.profile.PlayerProfile;
import com.tiers.screens.ConfigScreen;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static com.tiers.TiersClient.LOGGER;

public class ColorLoader implements SimpleSynchronousResourceReloadListener {
    public static Identifier identifier = Identifier.of("minecraft", "colors/pvptiers.json");

    @Override
    public Identifier getFabricId() {
        return Identifier.of("tiers", "color_loader");
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        if (resourceManager.getResource(identifier).isPresent()) {
            try {
                ColorControl.updateColors(JsonHelper.deserialize(new Gson(), new InputStreamReader(resourceManager.getResource(identifier).get().getInputStream(), StandardCharsets.UTF_8), JsonObject.class));
                TiersClient.restyleAllTexts(TiersClient.playerProfiles);
                TiersClient.updateAllTags();
            } catch (IOException ignored) {
                LOGGER.warn("Error loading colors info");
            }
        }

        if (ConfigScreen.ownProfile == null) {
            ConfigScreen.ownProfile = new PlayerProfile(MinecraftClient.getInstance().getGameProfile().getName(), false);
            ConfigScreen.ownProfile.buildRequest();

            String defaultProfileMojang = loadStringFromResources("json/defaultProfileMojang.json");
            String defaultProfileMCTiers = loadStringFromResources("json/defaultProfileMCTiers.json");
            String defaultProfilePvPTiers = loadStringFromResources("json/defaultProfilePvPTiers.json");
            String defaultProfileSubtiers = loadStringFromResources("json/defaultProfileSubtiers.json");

            LOGGER.info(defaultProfileMojang);
            LOGGER.info(defaultProfileMCTiers);

            ConfigScreen.defaultProfile = new PlayerProfile(defaultProfileMojang,
                    defaultProfileMCTiers,
                    defaultProfilePvPTiers,
                    defaultProfileSubtiers);

        } else {
            ArrayList<PlayerProfile> configProfiles = new ArrayList<>();
            configProfiles.add(ConfigScreen.defaultProfile);
            configProfiles.add(ConfigScreen.ownProfile);
            TiersClient.restyleAllTexts(configProfiles);
        }
    }

    private static String loadStringFromResources(String path) {
        try (InputStream inputStream = ColorLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream != null) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                        stringBuilder.append(System.lineSeparator());
                    }

                    return stringBuilder.toString();
                }
            }
        } catch (IOException ignored) {
            LOGGER.warn("Error loading default jsons");
        }

        return "";
    }
}