package com.tiers.misc;

import com.tiers.textures.ColorControl;
import com.tiers.textures.Icons;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;

public enum Modes {
    MCTIERS_VANILLA(Icons.MCTIERS_VANILLA, Icons.MCTIERS_VANILLA_TAG, "mctiers_vanilla", "Vanilla"),
    MCTIERS_UHC(Icons.MCTIERS_UHC, Icons.MCTIERS_UHC_TAG, "mctiers_uhc", "UHC"),
    MCTIERS_POT(Icons.MCTIERS_POT, Icons.MCTIERS_POT_TAG, "mctiers_pot", "Pot"),
    MCTIERS_NETH_OP(Icons.MCTIERS_NETH_OP, Icons.MCTIERS_NETH_OP_TAG, "mctiers_neth_op", "Neth Op"),
    MCTIERS_SMP(Icons.MCTIERS_SMP, Icons.MCTIERS_SMP_TAG, "mctiers_smp", "Smp"),
    MCTIERS_SWORD(Icons.MCTIERS_SWORD, Icons.MCTIERS_SWORD_TAG, "mctiers_sword", "Sword"),
    MCTIERS_AXE(Icons.MCTIERS_AXE, Icons.MCTIERS_AXE_TAG, "mctiers_axe", "Axe"),
    MCTIERS_MACE(Icons.MCTIERS_MACE, Icons.MCTIERS_MACE_TAG, "mctiers_mace", "Mace"),

    PVPTIERS_CRYSTAL(Icons.PVPTIERS_CRYSTAL, Icons.PVPTIERS_CRYSTAL_TAG, "pvptiers_crystal", "Crystal"),
    PVPTIERS_SWORD(Icons.PVPTIERS_SWORD, Icons.PVPTIERS_SWORD_TAG, "pvptiers_sword", "Sword"),
    PVPTIERS_UHC(Icons.PVPTIERS_UHC, Icons.PVPTIERS_UHC_TAG, "pvptiers_uhc", "UHC"),
    PVPTIERS_POT(Icons.PVPTIERS_POT, Icons.PVPTIERS_POT_TAG, "pvptiers_pot", "Pot"),
    PVPTIERS_NETH_POT(Icons.PVPTIERS_NETH_POT, Icons.PVPTIERS_NETH_POT_TAG, "pvptiers_neth_pot", "Neth Pot"),
    PVPTIERS_SMP(Icons.PVPTIERS_SMP, Icons.PVPTIERS_SMP_TAG, "pvptiers_smp", "Smp"),
    PVPTIERS_AXE(Icons.PVPTIERS_AXE, Icons.PVPTIERS_AXE_TAG, "pvptiers_axe", "Axe"),
    PVPTIERS_MACE(Icons.PVPTIERS_MACE, Icons.PVPTIERS_MACE_TAG, "pvptiers_mace", "Mace"),

    SUBTIERS_MINECART(Icons.SUBTIERS_MINECART, Icons.SUBTIERS_MINECART_TAG, "subtiers_minecart", "Minecart"),
    SUBTIERS_DIAMOND_SURVIVAL(Icons.SUBTIERS_DIAMOND_SURVIVAL, Icons.SUBTIERS_DIAMOND_SURVIVAL_TAG, "subtiers_diamond_survival", "Diamond Survival"),
    SUBTIERS_DEBUFF(Icons.SUBTIERS_DEBUFF, Icons.SUBTIERS_DEBUFF_TAG, "subtiers_debuff", "DeBuff"),
    SUBTIERS_ELYTRA(Icons.SUBTIERS_ELYTRA, Icons.SUBTIERS_ELYTRA_TAG, "subtiers_elytra", "Elytra"),
    SUBTIERS_SPEED(Icons.SUBTIERS_SPEED, Icons.SUBTIERS_SPEED_TAG, "subtiers_speed", "Speed"),
    SUBTIERS_CREEPER(Icons.SUBTIERS_CREEPER, Icons.SUBTIERS_CREEPER_TAG, "subtiers_creeper", "Creeper"),
    SUBTIERS_MANHUNT(Icons.SUBTIERS_MANHUNT, Icons.SUBTIERS_MANHUNT_TAG, "subtiers_manhunt", "Manhunt"),
    SUBTIERS_DIAMOND_SMP(Icons.SUBTIERS_DIAMOND_SMP, Icons.SUBTIERS_DIAMOND_SMP_TAG, "subtiers_diamond_smp", "Diamond Smp"),
    SUBTIERS_BOW(Icons.SUBTIERS_BOW, Icons.SUBTIERS_BOW_TAG, "subtiers_bow", "Bow"),
    SUBTIERS_BED(Icons.SUBTIERS_BED, Icons.SUBTIERS_BED_TAG, "subtiers_bed", "Bed"),
    SUBTIERS_OG_VANILLA(Icons.SUBTIERS_OG_VANILLA, Icons.SUBTIERS_OG_VANILLA_TAG, "subtiers_og_vanilla", "OG Vanilla"),
    SUBTIERS_TRIDENT(Icons.SUBTIERS_TRIDENT, Icons.SUBTIERS_TRIDENT_TAG, "subtiers_trident", "Trident");

    public final Text icon;
    public final Text iconTag;
    private final String color;
    private final String stringLabel;
    public Text label;

    Modes(Text icon, Text iconTag, String color, String label) {
        this.icon = icon;
        this.iconTag = iconTag;
        this.color = color;
        this.stringLabel = label;
        this.label = Text.literal(label).setStyle(Style.EMPTY.withColor(ColorControl.getColor(color)));
    }

    public static void updateColors() {
        for (Modes mode : values())
            mode.label = Text.literal(mode.stringLabel).setStyle(Style.EMPTY.withColor(ColorControl.getColor(mode.color)));
    }

    public static Modes[] getMCTiersValues() {
        Modes[] modesArray = new Modes[8];
        ArrayList<Modes> modes = new ArrayList<>();
        for (Modes mode : Modes.values())
            if (mode.toString().contains("MCTIERS"))
                modes.add(mode);
        return modes.toArray(modesArray);
    }

    public static Modes[] getPvPTiersValues() {
        Modes[] modesArray = new Modes[7];
        ArrayList<Modes> modes = new ArrayList<>();
        for (Modes mode : Modes.values())
            if (mode.toString().contains("PVPTIERS"))
                modes.add(mode);
        return modes.toArray(modesArray);
    }

    public static Modes[] getSubtiersValues() {
        Modes[] modesArray = new Modes[9];
        ArrayList<Modes> modes = new ArrayList<>();
        for (Modes mode : Modes.values())
            if (mode.toString().contains("SUBTIERS"))
                modes.add(mode);
        return modes.toArray(modesArray);
    }
}