package com.tiers.misc;

import com.tiers.textures.ColorControl;
import com.tiers.textures.Icons;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;

public enum Modes {
    MCTIERSCOM_VANILLA(Icons.MCTIERSCOM_VANILLA, Icons.MCTIERSCOM_VANILLA_TAG, "mctierscom_vanilla", "Vanilla"),
    MCTIERSCOM_UHC(Icons.MCTIERSCOM_UHC, Icons.MCTIERSCOM_UHC_TAG, "mctierscom_uhc", "UHC"),
    MCTIERSCOM_POT(Icons.MCTIERSCOM_POT, Icons.MCTIERSCOM_POT_TAG, "mctierscom_pot", "Pot"),
    MCTIERSCOM_NETHERITE_OP(Icons.MCTIERSCOM_NETHERITE_OP, Icons.MCTIERSCOM_NETHERITE_OP_TAG, "mctierscom_netherite_op", "Netherite Op"),
    MCTIERSCOM_SMP(Icons.MCTIERSCOM_SMP, Icons.MCTIERSCOM_SMP_TAG, "mctierscom_smp", "Smp"),
    MCTIERSCOM_SWORD(Icons.MCTIERSCOM_SWORD, Icons.MCTIERSCOM_SWORD_TAG, "mctierscom_sword", "Sword"),
    MCTIERSCOM_AXE(Icons.MCTIERSCOM_AXE, Icons.MCTIERSCOM_AXE_TAG, "mctierscom_axe", "Axe"),
    MCTIERSCOM_MACE(Icons.MCTIERSCOM_MACE, Icons.MCTIERSCOM_MACE_TAG, "mctierscom_mace", "Mace"),

    MCTIERSIO_CRYSTAL(Icons.MCTIERSIO_CRYSTAL, Icons.MCTIERSIO_CRYSTAL_TAG, "mctiersio_crystal", "Crystal"),
    MCTIERSIO_SWORD(Icons.MCTIERSIO_SWORD, Icons.MCTIERSIO_SWORD_TAG, "mctiersio_sword", "Sword"),
    MCTIERSIO_UHC(Icons.MCTIERSIO_UHC, Icons.MCTIERSIO_UHC_TAG, "mctiersio_uhc", "UHC"),
    MCTIERSIO_POT(Icons.MCTIERSIO_POT, Icons.MCTIERSIO_POT_TAG, "mctiersio_pot", "Pot"),
    MCTIERSIO_NETHERITE_POT(Icons.MCTIERSIO_NETHERITE_POT, Icons.MCTIERSIO_NETHERITE_POT_TAG, "mctiersio_netherite_pot", "Netherite Pot"),
    MCTIERSIO_SMP(Icons.MCTIERSIO_SMP, Icons.MCTIERSIO_SMP_TAG, "mctiersio_smp", "Smp"),
    MCTIERSIO_AXE(Icons.MCTIERSIO_AXE, Icons.MCTIERSIO_AXE_TAG, "mctiersio_axe", "Axe"),

    SUBTIERSNET_MINECART(Icons.SUBTIERSNET_MINECART, Icons.SUBTIERSNET_MINECART_TAG, "subtiersnet_minecart", "Minecart"),
    SUBTIERSNET_DIAMOND_CRYSTAL(Icons.SUBTIERSNET_DIAMOND_CRYSTAL, Icons.SUBTIERSNET_DIAMOND_CRYSTAL_TAG, "subtiersnet_diamond_crystal", "Diamond Crystal"),
    SUBTIERSNET_DEBUFF(Icons.SUBTIERSNET_DEBUFF, Icons.SUBTIERSNET_DEBUFF_TAG, "subtiersnet_debuff", "DeBuff"),
    SUBTIERSNET_ELYTRA(Icons.SUBTIERSNET_ELYTRA, Icons.SUBTIERSNET_ELYTRA_TAG, "subtiersnet_elytra", "Elytra"),
    SUBTIERSNET_SPEED(Icons.SUBTIERSNET_SPEED, Icons.SUBTIERSNET_SPEED_TAG, "subtiersnet_speed", "Speed"),
    SUBTIERSNET_CREEPER(Icons.SUBTIERSNET_CREEPER, Icons.SUBTIERSNET_CREEPER_TAG, "subtiersnet_creeper", "Creeper"),
    SUBTIERSNET_MANHUNT(Icons.SUBTIERSNET_MANHUNT, Icons.SUBTIERSNET_MANHUNT_TAG, "subtiersnet_manhunt", "Manhunt"),
    SUBTIERSNET_DIAMOND_SMP(Icons.SUBTIERSNET_DIAMOND_SMP, Icons.SUBTIERSNET_DIAMOND_SMP_TAG, "subtiersnet_diamond_smp", "Diamond Smp"),
    SUBTIERSNET_BOW(Icons.SUBTIERSNET_BOW, Icons.SUBTIERSNET_BOW_TAG, "subtiersnet_bow", "Bow"),
    SUBTIERSNET_BED(Icons.SUBTIERSNET_BED, Icons.SUBTIERSNET_BED_TAG, "subtiersnet_bed", "Bed"),
    SUBTIERSNET_OG_VANILLA(Icons.SUBTIERSNET_OG_VANILLA, Icons.SUBTIERSNET_OG_VANILLA_TAG, "subtiersnet_og_vanilla", "OG Vanilla"),
    SUBTIERSNET_TRIDENT(Icons.SUBTIERSNET_TRIDENT, Icons.SUBTIERSNET_TRIDENT_TAG, "subtiersnet_trident", "Trident");

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

    public static Modes[] getMCTiersCOMValues() {
        Modes[] modesArray = new Modes[8];
        ArrayList<Modes> modes = new ArrayList<>();
        for (Modes mode : Modes.values())
            if (mode.toString().contains("MCTIERSCOM"))
                modes.add(mode);
        return modes.toArray(modesArray);
    }

    public static Modes[] getMCTiersIOValues() {
        Modes[] modesArray = new Modes[7];
        ArrayList<Modes> modes = new ArrayList<>();
        for (Modes mode : Modes.values())
            if (mode.toString().contains("MCTIERSIO"))
                modes.add(mode);
        return modes.toArray(modesArray);
    }

    public static Modes[] getSubtiersNETValues() {
        Modes[] modesArray = new Modes[9];
        ArrayList<Modes> modes = new ArrayList<>();
        for (Modes mode : Modes.values())
            if (mode.toString().contains("SUBTIERSNET"))
                modes.add(mode);
        return modes.toArray(modesArray);
    }
}