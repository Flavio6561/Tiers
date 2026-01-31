package com.tiers.misc;

import com.tiers.textures.Icons;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public enum Mode {
    MCTIERS_VANILLA(Category.MCTIERS, "\uF000", "Vanilla"),
    MCTIERS_UHC(Category.MCTIERS, "\uF001", "UHC"),
    MCTIERS_POT(Category.MCTIERS, "\uF002", "Pot"),
    MCTIERS_NETH_OP(Category.MCTIERS, "\uF003", "Neth Op"),
    MCTIERS_SMP(Category.MCTIERS, "\uF004", "Smp"),
    MCTIERS_SWORD(Category.MCTIERS, "\uF005", "Sword"),
    MCTIERS_AXE(Category.MCTIERS, "\uF006", "Axe"),
    MCTIERS_MACE(Category.MCTIERS, "\uF007", "Mace"),

    PVPTIERS_CRYSTAL(Category.PVPTIERS, "\uF000", "Crystal"),
    PVPTIERS_SWORD(Category.PVPTIERS, "\uF005", "Sword"),
    PVPTIERS_UHC(Category.PVPTIERS, "\uF001", "UHC"),
    PVPTIERS_POT(Category.PVPTIERS, "\uF002", "Pot"),
    PVPTIERS_NETH_POT(Category.PVPTIERS, "\uF003", "Neth Pot"),
    PVPTIERS_SMP(Category.PVPTIERS, "\uF004", "Smp"),
    PVPTIERS_AXE(Category.PVPTIERS, "\uF006", "Axe"),
    PVPTIERS_MACE(Category.PVPTIERS, "\uF007", "Mace"),

    SUBTIERS_MINECART(Category.SUBTIERS, "\uF000", "Minecart"),
    SUBTIERS_DIAMOND_SURVIVAL(Category.SUBTIERS, "\uF001", "Diamond Survival"),
    SUBTIERS_DEBUFF(Category.SUBTIERS, "\uF002", "DeBuff"),
    SUBTIERS_ELYTRA(Category.SUBTIERS, "\uF003", "Elytra"),
    SUBTIERS_SPEED(Category.SUBTIERS, "\uF004", "Speed"),
    SUBTIERS_CREEPER(Category.SUBTIERS, "\uF005", "Creeper"),
    SUBTIERS_MANHUNT(Category.SUBTIERS, "\uF006", "Manhunt"),
    SUBTIERS_DIAMOND_SMP(Category.SUBTIERS, "\uF007", "Diamond Smp"),
    SUBTIERS_BOW(Category.SUBTIERS, "\uF008", "Bow"),
    SUBTIERS_BED(Category.SUBTIERS, "\uF009", "Bed"),
    SUBTIERS_OG_VANILLA(Category.SUBTIERS, "\uF00A", "OG Vanilla"),
    SUBTIERS_TRIDENT(Category.SUBTIERS, "\uF00B", "Trident");

    private final Category category;
    private final String unicode;
    private final String label;

    Mode(Category category, String unicode, String label) {
        this.category = category;
        this.unicode = unicode;
        this.label = label;
    }

    public enum Category {
        MCTIERS,
        PVPTIERS,
        SUBTIERS
    }

    public Text getIcon() {
        Identifier identifier = switch (category) {
            case MCTIERS -> Icons.identifierMCTiers;
            case PVPTIERS -> Icons.identifierPvPTiers;
            case SUBTIERS -> Icons.identifierSubtiers;
        };
        return Text.literal(unicode).setStyle(Style.EMPTY.withFont(new StyleSpriteSource.Font(identifier)));
    }

    public Text getIconTag() {
        Identifier identifier = switch (category) {
            case MCTIERS -> Icons.identifierMCTiersTags;
            case PVPTIERS -> Icons.identifierPvPTiersTags;
            case SUBTIERS -> Icons.identifierSubtiersTags;
        };
        return Text.literal(unicode).setStyle(Style.EMPTY.withFont(new StyleSpriteSource.Font(identifier)));
    }

    public Text getTextLabel() {
        return Icons.colorText(label, name().toLowerCase());
    }

    public static Mode[] getMCTiersValues() {
        Mode[] modeArray = new Mode[8];
        ArrayList<Mode> modeArrayList = new ArrayList<>();
        for (Mode mode : values())
            if (mode.toString().contains("MCTIERS"))
                modeArrayList.add(mode);
        return modeArrayList.toArray(modeArray);
    }

    public static Mode[] getPvPTiersValues() {
        Mode[] modeArray = new Mode[7];
        ArrayList<Mode> modeArrayList = new ArrayList<>();
        for (Mode mode : values())
            if (mode.toString().contains("PVPTIERS"))
                modeArrayList.add(mode);
        return modeArrayList.toArray(modeArray);
    }

    public static Mode[] getSubtiersValues() {
        Mode[] modeArray = new Mode[9];
        ArrayList<Mode> modeArrayList = new ArrayList<>();
        for (Mode mode : values())
            if (mode.toString().contains("SUBTIERS"))
                modeArrayList.add(mode);
        return modeArrayList.toArray(modeArray);
    }
}