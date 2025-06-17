package com.tiers.textures;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tiers.misc.Modes;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static com.tiers.TiersClient.restyleAllTexts;
import static com.tiers.TiersClient.LOGGER;

public class ColorLoader implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return Identifier.of("tiers", "color_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        if (manager.getResource(Identifier.of("minecraft", "colors/colors.json")).isPresent()) {
            Resource resource = manager.getResource(Identifier.of("minecraft", "colors/colors.json")).get();
            try {
                JsonObject colorData = JsonHelper.deserialize(new Gson(), new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                ColorControl.updateColors(colorData);
                Modes.updateColors();
                restyleAllTexts();
            } catch (IOException ignored) {
                LOGGER.warn("Error loading colors info");
            }
        }
    }
}