package com.tiers;

import com.tiers.misc.ConfigManager;
import com.tiers.misc.Modes;
import com.tiers.textures.ColorControl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.Set;

public class InventoryChecker {
    private static boolean issuesAlertShown = false;

    public static void checkInventory(MinecraftClient client) {
        if (client.player == null) return;

        Modes detected = null;

        PlayerInventory inventory = client.player.getInventory();

        if (checkVanilla(inventory)) {
            TiersClient.activeMCTiersCOMMode = Modes.MCTIERSCOM_VANILLA;
            TiersClient.activeMCTiersIOMode = Modes.MCTIERSIO_CRYSTAL;
            detected = Modes.MCTIERSCOM_VANILLA;
        }

        if (checkSword(inventory)) {
            TiersClient.activeMCTiersCOMMode = Modes.MCTIERSCOM_SWORD;
            TiersClient.activeMCTiersIOMode = Modes.MCTIERSIO_SWORD;
            detected = Modes.MCTIERSCOM_SWORD;
        }

        if (checkUhc(inventory)) {
            TiersClient.activeMCTiersCOMMode = Modes.MCTIERSCOM_UHC;
            TiersClient.activeMCTiersIOMode = Modes.MCTIERSIO_UHC;
            detected = Modes.MCTIERSCOM_UHC;
        }

        if (checkPot(inventory)) {
            TiersClient.activeMCTiersCOMMode = Modes.MCTIERSCOM_POT;
            TiersClient.activeMCTiersIOMode = Modes.MCTIERSIO_POT;
            detected = Modes.MCTIERSCOM_POT;
        }

        if (checkNethPot(inventory)) {
            TiersClient.activeMCTiersCOMMode = Modes.MCTIERSCOM_NETHERITE_OP;
            TiersClient.activeMCTiersIOMode = Modes.MCTIERSIO_NETHERITE_POT;
            detected = Modes.MCTIERSCOM_NETHERITE_OP;
        }

        if (checkSmp(inventory)) {
            TiersClient.activeMCTiersCOMMode = Modes.MCTIERSCOM_SMP;
            TiersClient.activeMCTiersIOMode = Modes.MCTIERSIO_SMP;
            detected = Modes.MCTIERSCOM_SMP;
        }

        if (checkAxe(inventory)) {
            TiersClient.activeMCTiersCOMMode = Modes.MCTIERSCOM_AXE;
            TiersClient.activeMCTiersIOMode = Modes.MCTIERSIO_AXE;
            detected = Modes.MCTIERSCOM_AXE;
        }

        if (checkMace(inventory)) {
            TiersClient.activeMCTiersCOMMode = Modes.MCTIERSCOM_MACE;
            detected = Modes.MCTIERSCOM_MACE;
        }

        if (checkElytra(inventory)) {
            TiersClient.activeMCTiersIOMode = Modes.MCTIERSIO_ELYTRA;
            detected = Modes.MCTIERSIO_ELYTRA;
        }

        if (checkMinecart(inventory)) {
            TiersClient.activeSubtiersNETMode = Modes.SUBTIERSNET_MINECART;
            detected = Modes.SUBTIERSNET_MINECART;
        }

        if (checkDiamondVanilla(inventory)) {
            TiersClient.activeSubtiersNETMode = Modes.SUBTIERSNET_DIAMOND_CRYSTAL;
            detected = Modes.SUBTIERSNET_DIAMOND_CRYSTAL;
        }

        if (checkDeBuff(inventory)) {
            TiersClient.activeSubtiersNETMode = Modes.SUBTIERSNET_DEBUFF;
            detected = Modes.SUBTIERSNET_DEBUFF;
        }

        if (checkSubtiersElytra(inventory)) {
            TiersClient.activeSubtiersNETMode = Modes.SUBTIERSNET_ELYTRA;
            detected = Modes.SUBTIERSNET_ELYTRA;
        }

        if (checkSpeed(inventory)) {
            TiersClient.activeSubtiersNETMode = Modes.SUBTIERSNET_SPEED;
            detected = Modes.SUBTIERSNET_SPEED;
        }

        if (checkCreeper(inventory)) {
            TiersClient.activeSubtiersNETMode = Modes.SUBTIERSNET_CREEPER;
            detected = Modes.SUBTIERSNET_CREEPER;
        }

        if (checkManhunt(inventory)) {
            TiersClient.activeSubtiersNETMode = Modes.SUBTIERSNET_MANHUNT;
            detected = Modes.SUBTIERSNET_MANHUNT;
        }

        if (checkDiamondSmp(inventory)) {
            TiersClient.activeSubtiersNETMode = Modes.SUBTIERSNET_DIAMOND_SMP;
            detected = Modes.SUBTIERSNET_DIAMOND_SMP;
        }

        if (checkBow(inventory)) {
            TiersClient.activeSubtiersNETMode = Modes.SUBTIERSNET_BOW;
            detected = Modes.SUBTIERSNET_BOW;
        }

        if (checkBed(inventory)) {
            TiersClient.activeSubtiersNETMode = Modes.SUBTIERSNET_BED;
            detected = Modes.SUBTIERSNET_BED;
        }

        if (checkOgVanilla(inventory)) {
            TiersClient.activeSubtiersNETMode = Modes.SUBTIERSNET_OG_VANILLA;
            detected = Modes.SUBTIERSNET_OG_VANILLA;
        }

        if (checkTrident(inventory)) {
            TiersClient.activeSubtiersNETMode = Modes.SUBTIERSNET_TRIDENT;
            detected = Modes.SUBTIERSNET_TRIDENT;
        }

        if (detected != null) {
            client.player.sendMessage(Text.literal("").append(detected.label).append(Text.literal(" was detected").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text")))), true);
        } else {
            if (!issuesAlertShown) {
                TiersClient.sendMessageToPlayer("Have issue with auto-detect? Report the issue to flavio6561 on Discord", ColorControl.getColor("red"), false);
                issuesAlertShown = true;
            }
            client.player.sendMessage(Text.literal("No gamemode detected").setStyle(Style.EMPTY.withColor(ColorControl.getColor("red"))), true);
        }

        ConfigManager.saveConfig();
    }

    private static boolean checkVanilla(PlayerInventory inventory) {
        boolean hasObsidian = false;
        boolean hasCrystal = false;
        boolean hasAnchor = false;
        boolean hasGlowstone = false;
        boolean hasSword = false;
        boolean hasHelmet = false;
        boolean hasChestplate = false;
        boolean hasLeggings = false;
        boolean hasBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasObsidian |= hasItem(stack, Items.OBSIDIAN);
            hasCrystal |= hasItem(stack, Items.END_CRYSTAL);
            hasAnchor |= hasItem(stack, Items.RESPAWN_ANCHOR);
            hasGlowstone |= hasItem(stack, Items.GLOWSTONE);
            hasSword |= hasItem(stack, Items.NETHERITE_SWORD, true);
            hasHelmet |= hasItem(stack, Items.NETHERITE_HELMET, true);
            hasChestplate |= hasItem(stack, Items.NETHERITE_CHESTPLATE, true);
            hasLeggings |= hasItem(stack, Items.NETHERITE_LEGGINGS, true);
            hasBoots |= hasItem(stack, Items.NETHERITE_BOOTS, true);
        }

        return hasObsidian && hasCrystal && hasAnchor && hasGlowstone && hasSword && hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }

    private static boolean checkSword(PlayerInventory inventory) {
        boolean hasSword = false;
        boolean hasHelmet = false;
        boolean hasChestplate = false;
        boolean hasLeggings = false;
        boolean hasBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasSword |= hasItem(stack, Items.DIAMOND_SWORD);
            hasHelmet |= hasItem(stack, Items.DIAMOND_HELMET);
            hasChestplate |= hasItem(stack, Items.DIAMOND_CHESTPLATE);
            hasLeggings |= hasItem(stack, Items.DIAMOND_LEGGINGS);
            hasBoots |= hasItem(stack, Items.DIAMOND_BOOTS);

            if (SWORD_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasSword && hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }

    private static boolean checkUhc(PlayerInventory inventory) {
        boolean hasShield = false;
        boolean hasGaps = false;
        boolean hasLava = false;
        boolean hasWater = false;
        boolean hasCobwebs = false;
        boolean hasEnchantedBow = false;
        boolean hasEnchantedCrossbow = false;
        boolean hasEnchantedSword = false;
        boolean hasEnchantedAxe = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasShield |= hasItem(stack, Items.SHIELD);
            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasLava |= hasItem(stack, Items.LAVA_BUCKET);
            hasWater |= hasItem(stack, Items.WATER_BUCKET);
            hasCobwebs |= hasItem(stack, Items.COBWEB);
            hasEnchantedBow |= hasItem(stack, Items.BOW, true);
            hasEnchantedCrossbow |= hasItem(stack, Items.CROSSBOW, true);
            hasEnchantedSword |= hasItem(stack, Items.DIAMOND_SWORD, true);
            hasEnchantedAxe |= hasItem(stack, Items.DIAMOND_AXE, true);
            hasEnchantedHelmet |= hasItem(stack, Items.DIAMOND_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.DIAMOND_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.DIAMOND_LEGGINGS, true) || hasItem(stack, Items.IRON_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.DIAMOND_BOOTS, true);

            if (UHC_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasShield && hasGaps && hasLava && hasWater && hasCobwebs && hasEnchantedBow && hasEnchantedCrossbow && hasEnchantedSword &&
                hasEnchantedAxe && hasEnchantedHelmet && hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkPot(PlayerInventory inventory) {
        boolean hasSteak = false;
        boolean hasPotions = false;
        boolean hasEnchantedSword = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasSteak |= hasItem(stack, Items.COOKED_BEEF);
            hasPotions |= hasItem(stack, Items.SPLASH_POTION);
            hasEnchantedSword |= hasItem(stack, Items.DIAMOND_SWORD, true);
            hasEnchantedHelmet |= hasItem(stack, Items.DIAMOND_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.DIAMOND_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.DIAMOND_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.DIAMOND_BOOTS, true);

            if (POT_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasSteak && hasPotions && hasEnchantedSword && hasEnchantedHelmet && hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkNethPot(PlayerInventory inventory) {
        boolean hasGaps = false;
        boolean hasPotions = false;
        boolean hasTotem = false;
        boolean hasXp = false;
        boolean hasEnchantedSword = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasPotions |= hasItem(stack, Items.SPLASH_POTION);
            hasTotem |= hasItem(stack, Items.TOTEM_OF_UNDYING);
            hasXp |= hasItem(stack, Items.EXPERIENCE_BOTTLE);
            hasEnchantedSword |= hasItem(stack, Items.NETHERITE_SWORD, true);
            hasEnchantedHelmet |= hasItem(stack, Items.NETHERITE_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.NETHERITE_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.NETHERITE_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.NETHERITE_BOOTS, true);

            if (NETHPOT_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasGaps && hasPotions && hasTotem && hasXp && hasEnchantedSword && hasEnchantedHelmet &&
                hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkSmp(PlayerInventory inventory) {
        boolean hasGaps = false;
        boolean hasPotions = false;
        boolean hasTotem = false;
        boolean hasXp = false;
        boolean hasPearls = false;
        boolean hasShield = false;
        boolean hasEnchantedSword = false;
        boolean hasEnchantedAxe = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasPotions |= hasItem(stack, Items.SPLASH_POTION);
            hasTotem |= hasItem(stack, Items.TOTEM_OF_UNDYING);
            hasXp |= hasItem(stack, Items.EXPERIENCE_BOTTLE);
            hasPearls |= hasItem(stack, Items.ENDER_PEARL);
            hasShield |= hasItem(stack, Items.SHIELD);
            hasEnchantedSword |= hasItem(stack, Items.NETHERITE_SWORD, true);
            hasEnchantedAxe |= hasItem(stack, Items.NETHERITE_AXE, true);
            hasEnchantedHelmet |= hasItem(stack, Items.NETHERITE_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.NETHERITE_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.NETHERITE_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.NETHERITE_BOOTS, true);

            if (SMP_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasGaps && hasPotions && hasTotem && hasXp && hasPearls && hasShield && hasEnchantedAxe &&
                hasEnchantedSword && hasEnchantedHelmet && hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkAxe(PlayerInventory inventory) {
        boolean hasBow = false;
        boolean hasCrossbow = false;
        boolean hasShield = false;
        boolean hasSword = false;
        boolean hasAxe = false;
        boolean hasHelmet = false;
        boolean hasChestplate = false;
        boolean hasLeggings = false;
        boolean hasBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasBow |= hasItem(stack, Items.BOW, false);
            hasCrossbow |= hasItem(stack, Items.CROSSBOW, false);
            hasShield |= hasItem(stack, Items.SHIELD, false);
            hasSword |= hasItem(stack, Items.DIAMOND_SWORD, false);
            hasAxe |= hasItem(stack, Items.DIAMOND_AXE, false);
            hasHelmet |= hasItem(stack, Items.DIAMOND_HELMET, false);
            hasChestplate |= hasItem(stack, Items.DIAMOND_CHESTPLATE, false);
            hasLeggings |= hasItem(stack, Items.DIAMOND_LEGGINGS, false);
            hasBoots |= hasItem(stack, Items.DIAMOND_BOOTS, false);

            if (AXE_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasBow && hasCrossbow && hasShield && hasSword && hasAxe && hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }

    private static boolean checkMace(PlayerInventory inventory) {
        boolean hasGaps = false;
        boolean hasPotions = false;
        boolean hasTotem = false;
        boolean hasPearls = false;
        boolean hasWindCharge = false;
        boolean hasElytra = false;
        boolean hasShield = false;
        boolean hasEnchantedMace = false;
        boolean hasEnchantedSword = false;
        boolean hasEnchantedAxe = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasPotions |= hasItem(stack, Items.SPLASH_POTION);
            hasTotem |= hasItem(stack, Items.TOTEM_OF_UNDYING);
            hasPearls |= hasItem(stack, Items.ENDER_PEARL);
            hasWindCharge |= hasItem(stack, Items.WIND_CHARGE);
            hasElytra |= hasItem(stack, Items.ELYTRA);
            hasShield |= hasItem(stack, Items.SHIELD);
            hasEnchantedMace |= hasItem(stack, Items.MACE, true);
            hasEnchantedSword |= hasItem(stack, Items.NETHERITE_SWORD, true);
            hasEnchantedAxe |= hasItem(stack, Items.NETHERITE_AXE, true);
            hasEnchantedHelmet |= hasItem(stack, Items.NETHERITE_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.NETHERITE_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.NETHERITE_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.NETHERITE_BOOTS, true);

            if (MACE_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasGaps && hasPotions && hasTotem && hasPearls && hasWindCharge && hasElytra && hasShield && hasEnchantedMace &&
                hasEnchantedSword && hasEnchantedAxe && hasEnchantedHelmet && hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkElytra(PlayerInventory inventory) {
        boolean hasGaps = false;
        boolean hasGoldenCarrots = false;
        boolean hasArrows = false;
        boolean hasFireworks = false;
        boolean hasCrossbow = false;
        boolean hasEnchantedSword = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedElytra = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasGoldenCarrots |= hasItem(stack, Items.GOLDEN_CARROT);
            hasArrows |= hasItem(stack, Items.ARROW);
            hasFireworks |= hasItem(stack, Items.FIREWORK_ROCKET);
            hasCrossbow |= hasItem(stack, Items.CROSSBOW);
            hasEnchantedSword |= hasItem(stack, Items.NETHERITE_SWORD, true);
            hasEnchantedHelmet |= hasItem(stack, Items.NETHERITE_HELMET, true);
            hasEnchantedElytra |= hasItem(stack, Items.ELYTRA, true);
            hasEnchantedLeggings |= hasItem(stack, Items.NETHERITE_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.NETHERITE_BOOTS, true);

            if (ELYTRA_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasGaps && hasGoldenCarrots && hasArrows && hasFireworks && hasCrossbow &&
                hasEnchantedSword && hasEnchantedHelmet && hasEnchantedElytra && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkMinecart(PlayerInventory inventory) {
        boolean hasCart = false;
        boolean hasCobwebs = false;
        boolean hasPotions = false;
        boolean hasRails = false;
        boolean hasEnchantedAxe = false;
        boolean hasEnchantedSword = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasCart |= hasItem(stack, Items.TNT_MINECART);
            hasCobwebs |= hasItem(stack, Items.COBWEB);
            hasPotions |= hasItem(stack, Items.SPLASH_POTION);
            hasRails |= hasItem(stack, Items.RAIL);
            hasEnchantedAxe |= hasItem(stack, Items.NETHERITE_AXE, true);
            hasEnchantedSword |= hasItem(stack, Items.NETHERITE_SWORD, true);
            hasEnchantedHelmet |= hasItem(stack, Items.NETHERITE_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.NETHERITE_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.NETHERITE_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.NETHERITE_BOOTS, true);

            if (MINECART_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasCart && hasCobwebs && hasPotions && hasRails && hasEnchantedAxe && hasEnchantedSword && hasEnchantedHelmet &&
                hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkDiamondVanilla(PlayerInventory inventory) {
        boolean hasObsidian = false;
        boolean hasCrystal = false;
        boolean hasAnchor = false;
        boolean hasGlowstone = false;
        boolean hasSword = false;
        boolean hasHelmet = false;
        boolean hasChestplate = false;
        boolean hasLeggings = false;
        boolean hasBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasObsidian |= hasItem(stack, Items.OBSIDIAN);
            hasCrystal |= hasItem(stack, Items.END_CRYSTAL);
            hasAnchor |= hasItem(stack, Items.RESPAWN_ANCHOR);
            hasGlowstone |= hasItem(stack, Items.GLOWSTONE);
            hasSword |= hasItem(stack, Items.DIAMOND_SWORD, true);
            hasHelmet |= hasItem(stack, Items.DIAMOND_HELMET, true);
            hasChestplate |= hasItem(stack, Items.DIAMOND_CHESTPLATE, true);
            hasLeggings |= hasItem(stack, Items.DIAMOND_LEGGINGS, true);
            hasBoots |= hasItem(stack, Items.DIAMOND_BOOTS, true);

            if (DIAMOND_VANILLA_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasObsidian && hasCrystal && hasAnchor && hasGlowstone && hasSword && hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }

    private static boolean checkDeBuff(PlayerInventory inventory) {
        boolean hasPots = false;
        boolean hasGoldenCarrots = false;
        boolean hasPearls = false;
        boolean hasCrossbow = false;
        boolean hasSword = false;
        boolean hasHelmet = false;
        boolean hasChestplate = false;
        boolean hasLeggings = false;
        boolean hasBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasPots |= hasItem(stack, Items.SPLASH_POTION);
            hasGoldenCarrots |= hasItem(stack, Items.GOLDEN_CARROT);
            hasPearls |= hasItem(stack, Items.ENDER_PEARL);
            hasCrossbow |= hasItem(stack, Items.CROSSBOW);
            hasSword |= hasItem(stack, Items.STONE_SWORD, false);
            hasHelmet |= hasItem(stack, Items.LEATHER_HELMET, true);
            hasChestplate |= hasItem(stack, Items.LEATHER_CHESTPLATE, true);
            hasLeggings |= hasItem(stack, Items.LEATHER_LEGGINGS, true);
            hasBoots |= hasItem(stack, Items.LEATHER_BOOTS, true);

            if (DEBUFF_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasPots && hasGoldenCarrots && hasPearls && hasCrossbow && hasSword && hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }

    private static boolean checkSubtiersElytra(PlayerInventory inventory) {
        boolean hasCobweb = false;
        boolean hasPots = false;
        boolean hasGaps = false;
        boolean hasTotem = false;
        boolean hasWindcharge = false;
        boolean hasPearls = false;
        boolean hasCrossbow = false;
        boolean hasBow = false;
        boolean hasElytra = false;
        boolean hasMace = false;
        boolean hasSword = false;
        boolean hasAxe = false;
        boolean hasChestplate = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasCobweb |= hasItem(stack, Items.COBWEB);
            hasPots |= hasItem(stack, Items.SPLASH_POTION);
            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasTotem |= hasItem(stack, Items.TOTEM_OF_UNDYING);
            hasWindcharge |= hasItem(stack, Items.WIND_CHARGE);
            hasPearls |= hasItem(stack, Items.ENDER_PEARL);
            hasCrossbow |= hasItem(stack, Items.CROSSBOW, true);
            hasBow |= hasItem(stack, Items.BOW, true);
            hasElytra |= hasItem(stack, Items.ELYTRA);
            hasMace |= hasItem(stack, Items.MACE);
            hasSword |= hasItem(stack, Items.NETHERITE_SWORD, true);
            hasAxe |= hasItem(stack, Items.NETHERITE_AXE, true);
            hasChestplate |= hasItem(stack, Items.LEATHER_CHESTPLATE);

            if (SUBTIERS_ELYTRA_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasCobweb && hasPots && hasGaps && hasTotem && hasWindcharge && hasPearls && hasCrossbow &&
                hasBow && hasElytra && hasMace && hasSword && hasAxe && hasChestplate;
    }

    private static boolean checkSpeed(PlayerInventory inventory) {
        boolean hasSword = false;
        boolean hasHelmet = false;
        boolean hasChestplate = false;
        boolean hasLeggings = false;
        boolean hasBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasSword |= hasItem(stack, Items.DIAMOND_SWORD, false);
            hasHelmet |= hasItem(stack, Items.DIAMOND_HELMET, true);
            hasChestplate |= hasItem(stack, Items.DIAMOND_CHESTPLATE, true);
            hasLeggings |= hasItem(stack, Items.IRON_LEGGINGS, true);
            hasBoots |= hasItem(stack, Items.DIAMOND_BOOTS, true);

            if (SPEED_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasSword && hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }

    private static boolean checkCreeper(PlayerInventory inventory) {
        boolean hasCobweb = false;
        boolean hasCreeper = false;
        boolean hasWater = false;
        boolean hasTnt = false;
        boolean hasGaps = false;
        boolean hasTotem = false;
        boolean hasPearls = false;
        boolean hasCrossbow = false;
        boolean hasBow = false;
        boolean hasShield = false;
        boolean hasSword = false;
        boolean hasAxe = false;
        boolean hasHelmet = false;
        boolean hasChestplate = false;
        boolean hasLeggings = false;
        boolean hasBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasCobweb |= hasItem(stack, Items.COBWEB);
            hasCreeper |= hasItem(stack, Items.CREEPER_SPAWN_EGG);
            hasWater |= hasItem(stack, Items.WATER_BUCKET);
            hasTnt |= hasItem(stack, Items.TNT);
            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasTotem |= hasItem(stack, Items.TOTEM_OF_UNDYING);
            hasPearls |= hasItem(stack, Items.ENDER_PEARL);
            hasCrossbow |= hasItem(stack, Items.CROSSBOW, true);
            hasBow |= hasItem(stack, Items.BOW, true);
            hasShield |= hasItem(stack, Items.SHIELD);
            hasSword |= hasItem(stack, Items.STONE_SWORD, true);
            hasAxe |= hasItem(stack, Items.STONE_AXE);
            hasHelmet |= hasItem(stack, Items.CHAINMAIL_HELMET, true);
            hasChestplate |= hasItem(stack, Items.CHAINMAIL_CHESTPLATE, true);
            hasLeggings |= hasItem(stack, Items.CHAINMAIL_LEGGINGS, true);
            hasBoots |= hasItem(stack, Items.CHAINMAIL_BOOTS, true);

            if (CREEPER_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasCobweb && hasCreeper && hasWater && hasTnt && hasGaps && hasTotem && hasPearls && hasCrossbow &&
                hasBow && hasShield && hasSword && hasAxe && hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }

    private static boolean checkManhunt(PlayerInventory inventory) {
        boolean hasCobweb = false;
        boolean hasWater = false;
        boolean hasLava = false;
        boolean hasSponge = false;
        boolean hasTnt = false;
        boolean hasGaps = false;
        boolean hasSlime = false;
        boolean hasFlint = false;
        boolean hasPearls = false;
        boolean hasCrossbow = false;
        boolean hasBow = false;
        boolean hasShield = false;
        boolean hasSword = false;
        boolean hasAxe = false;
        boolean hasHelmet = false;
        boolean hasChestplate = false;
        boolean hasLeggings = false;
        boolean hasBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasCobweb |= hasItem(stack, Items.COBWEB);
            hasWater |= hasItem(stack, Items.WATER_BUCKET);
            hasLava |= hasItem(stack, Items.LAVA_BUCKET);
            hasSponge |= hasItem(stack, Items.SPONGE);
            hasTnt |= hasItem(stack, Items.TNT);
            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasSlime |= hasItem(stack, Items.SLIME_BLOCK);
            hasFlint |= hasItem(stack, Items.FLINT_AND_STEEL);
            hasPearls |= hasItem(stack, Items.ENDER_PEARL);
            hasCrossbow |= hasItem(stack, Items.CROSSBOW, false);
            hasBow |= hasItem(stack, Items.BOW, false);
            hasShield |= hasItem(stack, Items.SHIELD);
            hasSword |= hasItem(stack, Items.IRON_SWORD, true);
            hasAxe |= hasItem(stack, Items.IRON_AXE);
            hasHelmet |= hasItem(stack, Items.IRON_HELMET, true);
            hasChestplate |= hasItem(stack, Items.DIAMOND_CHESTPLATE, true);
            hasLeggings |= hasItem(stack, Items.DIAMOND_LEGGINGS, true);
            hasBoots |= hasItem(stack, Items.IRON_BOOTS, true);

            if (MANHUNT_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasCobweb && hasWater && hasLava && hasSponge && hasTnt && hasGaps && hasSlime && hasFlint && hasPearls &&
                hasCrossbow && hasBow && hasShield && hasSword && hasAxe && hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }

    private static boolean checkDiamondSmp(PlayerInventory inventory) {
        boolean hasGaps = false;
        boolean hasWater = false;
        boolean hasCobweb = false;
        boolean hasChorus = false;
        boolean hasPotions = false;
        boolean hasTotem = false;
        boolean hasXp = false;
        boolean hasPearls = false;
        boolean hasShield = false;
        boolean hasEnchantedSword = false;
        boolean hasEnchantedAxe = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasWater |= hasItem(stack, Items.WATER_BUCKET);
            hasCobweb |= hasItem(stack, Items.COBWEB);
            hasChorus |= hasItem(stack, Items.CHORUS_FRUIT);
            hasPotions |= hasItem(stack, Items.SPLASH_POTION);
            hasTotem |= hasItem(stack, Items.TOTEM_OF_UNDYING);
            hasXp |= hasItem(stack, Items.EXPERIENCE_BOTTLE);
            hasPearls |= hasItem(stack, Items.ENDER_PEARL);
            hasShield |= hasItem(stack, Items.SHIELD);
            hasEnchantedSword |= hasItem(stack, Items.DIAMOND_SWORD, true);
            hasEnchantedAxe |= hasItem(stack, Items.DIAMOND_AXE, true);
            hasEnchantedHelmet |= hasItem(stack, Items.DIAMOND_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.DIAMOND_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.DIAMOND_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.DIAMOND_BOOTS, true);

            if (DIAMOND_SMP_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasGaps && hasWater && hasCobweb && hasChorus && hasPotions && hasTotem && hasXp && hasPearls && hasShield && hasEnchantedAxe &&
                hasEnchantedSword && hasEnchantedHelmet && hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkBow(PlayerInventory inventory) {
        boolean hasBow = false;
        boolean hasArrow = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasBow |= hasItem(stack, Items.BOW, true);
            hasArrow |= hasItem(stack, Items.ARROW);
            hasEnchantedHelmet |= hasItem(stack, Items.IRON_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.IRON_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.IRON_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.IRON_BOOTS, true);

            if (BOW_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasBow && hasArrow && hasEnchantedHelmet && hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkBed(PlayerInventory inventory) {
        boolean hasTotem = false;
        boolean hasXp = false;
        boolean hasPearls = false;
        boolean hasGaps = false;
        boolean hasBed = false;
        boolean hasObsidian = false;
        boolean hasSword = false;
        boolean hasPickaxe = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasTotem |= hasItem(stack, Items.TOTEM_OF_UNDYING);
            hasXp |= hasItem(stack, Items.EXPERIENCE_BOTTLE);
            hasPearls |= hasItem(stack, Items.ENDER_PEARL);
            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasBed |= (hasItem(stack, Items.BLACK_BED) || hasItem(stack, Items.BLUE_BED) || hasItem(stack, Items.ORANGE_BED) || hasItem(stack, Items.BROWN_BED) ||
                    hasItem(stack, Items.RED_BED) || hasItem(stack, Items.WHITE_BED) || hasItem(stack, Items.GREEN_BED) || hasItem(stack, Items.YELLOW_BED));
            hasObsidian |= hasItem(stack, Items.OBSIDIAN);
            hasSword |= hasItem(stack, Items.NETHERITE_SWORD, true);
            hasPickaxe |= hasItem(stack, Items.NETHERITE_PICKAXE, true);
            hasEnchantedHelmet |= hasItem(stack, Items.NETHERITE_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.NETHERITE_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.NETHERITE_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.NETHERITE_BOOTS, true);

            if (BED_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasTotem && hasXp && hasPearls && hasGaps && hasBed && hasObsidian && hasSword && hasPickaxe && hasEnchantedHelmet &&
                hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkOgVanilla(PlayerInventory inventory) {
        boolean hasSteak = false;
        boolean hasGaps = false;
        boolean hasPotions = false;
        boolean hasPearls = false;
        boolean hasEnchantedBow = false;
        boolean hasEnchantedSword = false;
        boolean hasEnchantedPickaxe = false;
        boolean hasEnchantedHelmet = false;
        boolean hasEnchantedChestplate = false;
        boolean hasEnchantedLeggings = false;
        boolean hasEnchantedBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasSteak |= hasItem(stack, Items.COOKED_BEEF);
            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasPotions |= hasItem(stack, Items.SPLASH_POTION);
            hasPearls |= hasItem(stack, Items.ENDER_PEARL);
            hasEnchantedBow |= hasItem(stack, Items.BOW, true);
            hasEnchantedSword |= hasItem(stack, Items.DIAMOND_SWORD, true);
            hasEnchantedPickaxe |= hasItem(stack, Items.DIAMOND_PICKAXE, true);
            hasEnchantedHelmet |= hasItem(stack, Items.DIAMOND_HELMET, true);
            hasEnchantedChestplate |= hasItem(stack, Items.DIAMOND_CHESTPLATE, true);
            hasEnchantedLeggings |= hasItem(stack, Items.DIAMOND_LEGGINGS, true);
            hasEnchantedBoots |= hasItem(stack, Items.DIAMOND_BOOTS, true);

            if (OG_VANILLA_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasSteak && hasGaps && hasPotions && hasPearls && hasEnchantedBow && hasEnchantedSword && hasEnchantedPickaxe && hasEnchantedHelmet && hasEnchantedChestplate && hasEnchantedLeggings && hasEnchantedBoots;
    }

    private static boolean checkTrident(PlayerInventory inventory) {
        boolean hasCobweb = false;
        boolean hasWater = false;
        boolean hasGaps = false;
        boolean hasSponge = false;
        boolean hasTotem = false;
        boolean hasShears = false;
        boolean hasTrident = false;
        boolean hasHelmet = false;
        boolean hasChestplate = false;
        boolean hasLeggings = false;
        boolean hasBoots = false;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            hasCobweb |= hasItem(stack, Items.COBWEB);
            hasWater |= hasItem(stack, Items.WATER_BUCKET);
            hasGaps |= hasItem(stack, Items.GOLDEN_APPLE);
            hasSponge |= hasItem(stack, Items.SPONGE);
            hasTotem |= hasItem(stack, Items.TOTEM_OF_UNDYING);
            hasShears |= hasItem(stack, Items.SHEARS, true);
            hasTrident |= hasItem(stack, Items.TRIDENT, true);
            hasHelmet |= hasItem(stack, Items.TURTLE_HELMET, false);
            hasChestplate |= hasItem(stack, Items.GOLDEN_CHESTPLATE, true);
            hasLeggings |= hasItem(stack, Items.GOLDEN_LEGGINGS, true);
            hasBoots |= hasItem(stack, Items.GOLDEN_BOOTS, true);

            if (TRIDENT_NON_ALLOWED.contains(stack.getItem())) return false;
        }

        return hasCobweb && hasWater && hasGaps && hasSponge && hasTotem && hasShears &&
                hasTrident && hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }

    private static boolean hasItem(ItemStack itemStack, Item item, boolean needEnchant) {
        return itemStack.getItem() == item && (needEnchant == itemStack.hasEnchantments());
    }

    private static boolean hasItem(ItemStack itemStack, Item item) {
        return itemStack.getItem() == item;
    }

    private static final Set<Item> SWORD_NON_ALLOWED = Set.of(
            Items.DIAMOND_AXE,
            Items.COBWEB,
            Items.SHIELD,
            Items.ENDER_PEARL,
            Items.EXPERIENCE_BOTTLE,
            Items.SPLASH_POTION,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> UHC_NON_ALLOWED = Set.of(
            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> POT_NON_ALLOWED = Set.of(
            Items.DIAMOND_AXE,
            Items.GOLDEN_APPLE,
            Items.SHIELD,
            Items.COBWEB,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> NETHPOT_NON_ALLOWED = Set.of(
            Items.ENDER_PEARL,
            Items.SHIELD,

            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,

            Items.DIAMOND_SWORD,
            Items.DIAMOND_AXE,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> SMP_NON_ALLOWED = Set.of(
            Items.DIAMOND_SWORD,
            Items.DIAMOND_AXE,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,

            Items.NETHERITE_PICKAXE,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> AXE_NON_ALLOWED = Set.of(
            Items.ENDER_PEARL,
            Items.COBWEB,
            Items.GOLDEN_APPLE,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> MACE_NON_ALLOWED = Set.of(
            Items.EXPERIENCE_BOTTLE,

            Items.NETHERITE_PICKAXE,

            Items.DIAMOND_SWORD,
            Items.DIAMOND_AXE,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> ELYTRA_NON_ALLOWED = Set.of(
            Items.ENDER_PEARL,
            Items.SHIELD,
            Items.SPLASH_POTION,
            Items.TOTEM_OF_UNDYING,
            Items.EXPERIENCE_BOTTLE,
            Items.NETHERITE_CHESTPLATE,

            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,

            Items.DIAMOND_SWORD,
            Items.DIAMOND_AXE,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> MINECART_NON_ALLOWED = Set.of(
            Items.SHIELD,
            Items.MACE,

            Items.NETHERITE_PICKAXE,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> DIAMOND_VANILLA_NON_ALLOWED = Set.of(
            Items.NETHERITE_SWORD,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS
    );

    private static final Set<Item> DEBUFF_NON_ALLOWED = Set.of(
            Items.SHIELD,
            Items.TOTEM_OF_UNDYING,
            Items.EXPERIENCE_BOTTLE,

            Items.DIAMOND_SWORD,
            Items.DIAMOND_AXE,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> SUBTIERS_ELYTRA_NON_ALLOWED = Set.of(
            Items.EXPERIENCE_BOTTLE,

            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> SPEED_NON_ALLOWED = Set.of(
            Items.DIAMOND_AXE,
            Items.COBWEB,
            Items.SHIELD,
            Items.ENDER_PEARL,
            Items.EXPERIENCE_BOTTLE,
            Items.SPLASH_POTION,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> CREEPER_NON_ALLOWED = Set.of(
            Items.ELYTRA,
            Items.WIND_CHARGE,
            Items.EXPERIENCE_BOTTLE,
            Items.SPLASH_POTION,

            Items.DIAMOND_SWORD,
            Items.DIAMOND_AXE,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> MANHUNT_NON_ALLOWED = Set.of(
            Items.TOTEM_OF_UNDYING,
            Items.WIND_CHARGE,
            Items.EXPERIENCE_BOTTLE,
            Items.SPLASH_POTION,

            Items.DIAMOND_SWORD,
            Items.DIAMOND_AXE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_BOOTS,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> DIAMOND_SMP_NON_ALLOWED = Set.of(
            Items.WIND_CHARGE,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> BOW_NON_ALLOWED = Set.of(
            Items.ELYTRA,
            Items.WIND_CHARGE,
            Items.EXPERIENCE_BOTTLE,
            Items.SPLASH_POTION,
            Items.ENDER_PEARL,
            Items.SHIELD,
            Items.TOTEM_OF_UNDYING,

            Items.DIAMOND_SWORD,
            Items.DIAMOND_AXE,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> BED_NON_ALLOWED = Set.of(
            Items.WIND_CHARGE,
            Items.MACE,
            Items.SHIELD,

            Items.END_CRYSTAL,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> OG_VANILLA_NON_ALLOWED = Set.of(
            Items.ELYTRA,
            Items.WIND_CHARGE,
            Items.CROSSBOW,
            Items.COBWEB,
            Items.WATER_BUCKET,
            Items.LAVA_BUCKET,
            Items.SHIELD,
            Items.TOTEM_OF_UNDYING,

            Items.DIAMOND_AXE,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );

    private static final Set<Item> TRIDENT_NON_ALLOWED = Set.of(
            Items.ELYTRA,
            Items.WIND_CHARGE,
            Items.CROSSBOW,
            Items.LAVA_BUCKET,
            Items.SHIELD,

            Items.DIAMOND_SWORD,
            Items.DIAMOND_AXE,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,

            Items.NETHERITE_SWORD,
            Items.NETHERITE_AXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,

            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE
    );
}