package com.tiers.misc;

import com.google.gson.JsonObject;

import java.util.HashMap;

public class ColorControl {
    private static final HashMap<String, Integer> colors = new HashMap<>();

    public static void updateColors(JsonObject json) {
        colors.clear();
        for (String key : json.keySet())
            colors.put(key, Integer.parseUnsignedInt(json.get(key).getAsString().replace("#", ""), 16));
    }

    public static int getColor(String name) {
        return colors.getOrDefault(name, 0xaaaaaa);
    }
}