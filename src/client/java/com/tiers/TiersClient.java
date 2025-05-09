package com.tiers;

import com.mojang.brigadier.context.CommandContext;
import com.tiers.misc.ColorControl;
import com.tiers.misc.ColorLoader;
import com.tiers.misc.Icons;
import com.tiers.misc.PlayerProfileQueue;
import com.tiers.profiles.GameMode;
import com.tiers.profiles.PlayerProfile;
import com.tiers.profiles.Status;
import com.tiers.profiles.types.BaseProfile;
import com.tiers.screens.PlayerSearchResultScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TiersClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(TiersClient.class);
    protected static final ArrayList<PlayerProfile> playerProfiles = new ArrayList<>();
    protected static final HashMap<String, Text> playerTexts = new HashMap<>();

    public static boolean toggleMod = true;
    public static boolean showIcons = true;
    public static boolean isSeparatorAdaptive = true;
    public static ModesTierDisplay displayMode = ModesTierDisplay.ADAPTIVE_HIGHEST;

    public static DisplayStatus mcTiersCOMPosition = DisplayStatus.LEFT;
    public static Modes activeMCTiersCOMMode = Modes.MCTIERSCOM_VANILLA;

    public static DisplayStatus mcTiersIOPosition = DisplayStatus.RIGHT;
    public static Modes activeMCTiersIOMode = Modes.MCTIERSIO_CRYSTAL;

    public static DisplayStatus subtiersNETPosition = DisplayStatus.OFF;
    public static Modes activeSubtiersNETMode = Modes.SUBTIERSNET_MINECART;

    @Override
    public void onInitializeClient() {
        ConfigManager.loadConfig();
        clearCache();
        CommandRegister.registerCommands();
        FabricLoader.getInstance().getModContainer("tiers").ifPresent(tiers -> ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of("tiers", "modern"), tiers, ResourcePackActivationType.ALWAYS_ENABLED));
        FabricLoader.getInstance().getModContainer("tiers").ifPresent(tiers -> ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of("tiers", "classic"), tiers, ResourcePackActivationType.NORMAL));
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ColorLoader());
        LOGGER.info("Tiers initialized");
    }

    public static Text getFullName(String originalName, Text originalNameText) {
        PlayerProfile profile = addGetPlayer(originalName, false);
        if (profile.status == Status.READY) {
            if (profile.originalNameText == null || profile.originalNameText != originalNameText)
                updatePlayerNametag(originalNameText, profile);
        }

        if (playerTexts.containsKey(originalName)) return playerTexts.get(originalName);

        return originalNameText;
    }

    public static void updateAllTags() {
        for (PlayerProfile profile : playerProfiles) {
            if (profile.status == Status.READY && profile.originalNameText != null)
                updatePlayerNametag(profile.originalNameText, profile);
        }
    }

    public static void updatePlayerNametag(Text originalNameText, PlayerProfile profile) {
        Text rightText = Text.literal("");
        Text leftText = Text.literal("");

        if (mcTiersCOMPosition == DisplayStatus.RIGHT) {
            rightText = updateProfileNameTagRight(profile.mcTiersCOMProfile, activeMCTiersCOMMode);
        } else if (mcTiersCOMPosition == DisplayStatus.LEFT) {
            leftText = updateProfileNameTagLeft(profile.mcTiersCOMProfile, activeMCTiersCOMMode);
        }
        if (mcTiersIOPosition == DisplayStatus.RIGHT) {
            rightText = updateProfileNameTagRight(profile.mcTiersIOProfile, activeMCTiersIOMode);
        } else if (mcTiersIOPosition == DisplayStatus.LEFT) {
            leftText = updateProfileNameTagLeft(profile.mcTiersIOProfile, activeMCTiersIOMode);
        }
        if (subtiersNETPosition == DisplayStatus.RIGHT) {
            rightText = updateProfileNameTagRight(profile.subtiersNETProfile, activeSubtiersNETMode);
        } else if (subtiersNETPosition == DisplayStatus.LEFT) {
            leftText = updateProfileNameTagLeft(profile.subtiersNETProfile, activeSubtiersNETMode);
        }

        playerTexts.put(profile.name, Text.literal("")
                .append(leftText)
                .append(originalNameText)
                .append(rightText));

        profile.originalNameText = originalNameText;
    }

    private static Text updateProfileNameTagRight(BaseProfile profile, Modes activeMode) {
        MutableText returnValue = Text.literal("");
        if (profile.status == Status.READY) {
            GameMode shown = profile.getGameMode(activeMode);
            if ((shown == null || shown.status == Status.SEARCHING) || (shown.status == Status.NOT_EXISTING && displayMode == ModesTierDisplay.SELECTED)) return returnValue;
            if (displayMode == ModesTierDisplay.ADAPTIVE_HIGHEST && shown.status == Status.NOT_EXISTING && profile.highest != null)
                shown = profile.highest;
            if (displayMode == ModesTierDisplay.HIGHEST && profile.highest != null && profile.highest.getTierPoints(false) > shown.getTierPoints(false))
                shown = profile.highest;
            if (shown == null || shown.status != Status.READY) return returnValue;
            MutableText separator = Text.literal(" | ").setStyle(isSeparatorAdaptive ? shown.displayedTier.getStyle() : Style.EMPTY.withColor(ColorControl.getColor("static_separator")));
            returnValue.append(Text.literal("").append(separator).append(shown.displayedTier));
            if (showIcons)
                returnValue.append(Text.literal(" ").append(shown.name.getIconTag()));
        }
        return returnValue;
    }

    private static Text updateProfileNameTagLeft(BaseProfile profile, Modes activeMode) {
        MutableText returnValue = Text.literal("");
        if (profile.status == Status.READY) {
            GameMode shown = profile.getGameMode(activeMode);
            if ((shown == null || shown.status == Status.SEARCHING) || (shown.status == Status.NOT_EXISTING && displayMode == ModesTierDisplay.SELECTED)) return returnValue;
            if (displayMode == ModesTierDisplay.ADAPTIVE_HIGHEST && shown.status == Status.NOT_EXISTING && profile.highest != null)
                shown = profile.highest;
            if (displayMode == ModesTierDisplay.HIGHEST && profile.highest != null && profile.highest.getTierPoints(false) > shown.getTierPoints(false))
                shown = profile.highest;
            if (shown == null || shown.status != Status.READY) return returnValue;
            MutableText separator = Text.literal(" | ").setStyle(isSeparatorAdaptive ? shown.displayedTier.getStyle() : Style.EMPTY.withColor(ColorControl.getColor("static_separator")));
            if (showIcons)
                returnValue = Text.literal("").append(shown.name.getIconTag()).append(" ");
            returnValue.append(Text.literal("").append(shown.displayedTier).append(separator));
        }
        return returnValue;
    }

    public static void restyleAllTexts() {
        for (PlayerProfile profile : playerProfiles) {
            if (profile.status == Status.READY) {
                if (profile.mcTiersCOMProfile.status == Status.READY)
                    profile.mcTiersCOMProfile.parseInfo(profile.mcTiersCOMProfile.originalJson);
                if (profile.mcTiersIOProfile.status == Status.READY)
                    profile.mcTiersIOProfile.parseInfo(profile.mcTiersIOProfile.originalJson);
                if (profile.subtiersNETProfile.status == Status.READY)
                    profile.subtiersNETProfile.parseInfo(profile.subtiersNETProfile.originalJson);
            }
        }
    }

    public static void sendMessageToPlayer(String chat_message, int color) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null)
            client.player.sendMessage((Text.literal(chat_message).setStyle(Style.EMPTY.withColor(color))), false);
    }

    protected static int toggleMod(CommandContext<FabricClientCommandSource> ignoredFabricClientCommandSourceCommandContext) {
        toggleMod = !toggleMod;
        ConfigManager.saveConfig();
        sendMessageToPlayer("Tiers is now " + (toggleMod ? "enabled" : "disabled"), (toggleMod ? ColorControl.getColor("green") : ColorControl.getColor("red")));
        return 1;
    }

    public static void toggleMod() {
        toggleMod = !toggleMod;
        ConfigManager.saveConfig();
    }

    public static PlayerProfile addGetPlayer(String name, boolean priority) {
        for (PlayerProfile profile : playerProfiles) {
            if (profile.name.equalsIgnoreCase(name)) {
                if (priority)
                    PlayerProfileQueue.changeToFirstInQueue(profile);
                return profile;
            }
        }
        PlayerProfile newProfile = new PlayerProfile(name);

        if (priority)
            PlayerProfileQueue.putFirstInQueue(newProfile);
        else
            PlayerProfileQueue.enqueue(newProfile);

        playerProfiles.add(newProfile);
        return newProfile;
    }

    private static void openPlayerSearchResultScreen(PlayerProfile profile) {
        MinecraftClient.getInstance().setScreen(new PlayerSearchResultScreen(profile));
    }

    protected static int searchPlayer(String name) {
        CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS)
                .execute(() -> MinecraftClient.getInstance().execute(() -> openPlayerSearchResultScreen(addGetPlayer(name, true))));
        return 1;
    }

    public static void clearCache() {
        playerProfiles.clear();
        playerTexts.clear();
        PlayerProfileQueue.clearQueue();
        try {
            FileUtils.deleteDirectory(new File(FabricLoader.getInstance().getConfigDir() + "/tiers-cache"));
        } catch (IOException e) {
            LOGGER.warn("Error deleting cache folder: {}", e.getMessage());
        }
    }

    public static void toggleSeparatorAdaptive() {
        isSeparatorAdaptive = !isSeparatorAdaptive;
        updateAllTags();
        ConfigManager.saveConfig();
    }

    public static void toggleShowIcons() {
        showIcons = !showIcons;
        updateAllTags();
        ConfigManager.saveConfig();
    }

    public static void cycleMCTiersCOMMode() {
        activeMCTiersCOMMode = cycleEnum(activeMCTiersCOMMode, Modes.getMCTiersCOMValues());
        updateAllTags();
        ConfigManager.saveConfig();
    }

    public static void cycleMCTiersIOMode() {
        activeMCTiersIOMode = cycleEnum(activeMCTiersIOMode, Modes.getMCTiersIOValues());
        updateAllTags();
        ConfigManager.saveConfig();
    }

    public static void cycleSubtiersNETMode() {
        activeSubtiersNETMode = cycleEnum(activeSubtiersNETMode, Modes.getSubtiersNETValues());
        updateAllTags();
        ConfigManager.saveConfig();
    }

    public static void cycleMCTiersCOMPosition() {
        mcTiersCOMPosition = cycleEnum(mcTiersCOMPosition, DisplayStatus.values());
        updateAllTags();
        ConfigManager.saveConfig();
    }

    public static void cycleMCTiersIOPosition() {
        mcTiersIOPosition = cycleEnum(mcTiersIOPosition, DisplayStatus.values());
        updateAllTags();
        ConfigManager.saveConfig();
    }

    public static void cycleSubtiersNETPosition() {
        subtiersNETPosition = cycleEnum(subtiersNETPosition, DisplayStatus.values());
        updateAllTags();
        ConfigManager.saveConfig();
    }

    public static void cycleDisplayMode() {
        displayMode = cycleEnum(displayMode, ModesTierDisplay.values());
        updateAllTags();
        ConfigManager.saveConfig();
    }

    private static <T extends Enum<T>> T cycleEnum(T current, T[] values) {
        return values[(current.ordinal() + 1) % values.length];
    }

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
        MCTIERSIO_ELYTRA(Icons.MCTIERSIO_ELYTRA, Icons.MCTIERSIO_ELYTRA_TAG, "mctiersio_elytra", "Elytra"),

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

        private final Text icon;
        private final Text iconTag;
        private final String color;
        private final String stringLabel;
        private Text label;

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

        public Text getIcon() {
            return icon;
        }

        public Text getIconTag() {
            return iconTag;
        }

        public Text getLabel() {
            return label;
        }

        public static Modes[] getMCTiersCOMValues() {
            Modes[] modesArray = new Modes[8];
            ArrayList<Modes> modes = new ArrayList<>();
            for (Modes mode : Modes.values()) {
                if (mode.toString().contains("MCTIERSCOM"))
                    modes.add(mode);
            }
            return modes.toArray(modesArray);
        }

        public static Modes[] getMCTiersIOValues() {
            Modes[] modesArray = new Modes[7];
            ArrayList<Modes> modes = new ArrayList<>();
            for (Modes mode : Modes.values()) {
                if (mode.toString().contains("MCTIERSIO"))
                    modes.add(mode);
            }
            return modes.toArray(modesArray);
        }

        public static Modes[] getSubtiersNETValues() {
            Modes[] modesArray = new Modes[9];
            ArrayList<Modes> modes = new ArrayList<>();
            for (Modes mode : Modes.values()) {
                if (mode.toString().contains("SUBTIERSNET"))
                    modes.add(mode);
            }
            return modes.toArray(modesArray);
        }
    }

    public enum ModesTierDisplay {
        HIGHEST,
        SELECTED,
        ADAPTIVE_HIGHEST;

        public String getIcon() {
            if (this.toString().equalsIgnoreCase("HIGHEST"))
                return "↑";
            else if (this.toString().equalsIgnoreCase("SELECTED"))
                return "●";
            return "↓";
        }
    }

    public enum DisplayStatus {
        RIGHT,
        LEFT,
        OFF;

        public String getIcon() {
            if (this.toString().equalsIgnoreCase("RIGHT"))
                return "→";
            else if (this.toString().equalsIgnoreCase("LEFT"))
                return "←";
            return "●";
        }
    }
}