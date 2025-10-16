package com.tiers.textures;

import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Icons {
    public static Identifier identifierMCTiers = Identifier.of("minecraft", "gamemodes/pvptiers");
    public static Identifier identifierPvPTiers = Identifier.of("minecraft", "gamemodes/pvptiers");
    public static final Identifier identifierSubtiers = Identifier.of("minecraft", "gamemodes/subtiers");

    public static Identifier identifierMCTiersTags = Identifier.of("minecraft", "gamemodes/pvptiers-tags");
    public static Identifier identifierPvPTiersTags = Identifier.of("minecraft", "gamemodes/pvptiers-tags");
    public static final Identifier identifierSubtiersTags = Identifier.of("minecraft", "gamemodes/subtiers-tags");

    public static final Text GLOBE = Text.literal("\uF000").setStyle(Style.EMPTY.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "misc"))));
    public static final Text OVERALL = Text.literal("\uF001").setStyle(Style.EMPTY.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "misc"))));
    public static final Text CYCLE = Text.literal("\uF002").setStyle(Style.EMPTY.withFont(new StyleSpriteSource.Font(Identifier.of("minecraft", "misc"))));

    public enum Type {
        CLASSIC,
        PVPTIERS,
        MCTIERS
    }

    public static Text colorText(String string, String color) {
        return Text.literal(string).setStyle(Style.EMPTY.withColor(ColorControl.getColor(color)));
    }
}