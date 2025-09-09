package com.tiers.textures;

import com.google.gson.JsonObject;

import java.util.HashMap;

public class ColorControl {
    private static final HashMap<String, Integer> colors = new HashMap<>();

    public static void updateColors(JsonObject jsonObject) {
        colors.clear();
        for (String key : jsonObject.keySet())
            colors.put(key, Integer.parseUnsignedInt(jsonObject.get(key).getAsString().replace("#", ""), 16));
    }

    public static int getColor(String colorName) {
        return colors.getOrDefault(colorName, 0xaaaaaa);
    }

    public static int getColorMinecraftStandard(String colorName) {
        return 0xff000000 | (colors.getOrDefault(colorName, 0xaaaaaa) & 0x00ffffff);
    }
}