package com.tiers;

import com.mojang.brigadier.context.CommandContext;
import com.tiers.profiles.GameMode;
import com.tiers.profiles.PlayerProfile;
import com.tiers.profiles.Status;
import com.tiers.screens.PlayerSearchResultScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TiersClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(TiersClient.class);
    protected static final List<PlayerProfile> playerProfiles = new ArrayList<>();

    public static boolean toggleMod = true;
    public static boolean showIcons = true;
    public static boolean isSeparatorAdaptive = true;
    public static ModesTierDisplay displayMode = ModesTierDisplay.ADAPTIVE_HIGHEST;

    public static DisplayStatus mcTiersCOMPosition = DisplayStatus.LEFT;
    public static Modes activeMCTiersCOMMode = Modes.MCTIERSCOM_VANILLA;

    public static DisplayStatus mcTiersIOPosition = DisplayStatus.RIGHT;
    public static Modes activeMCTiersIOMode = Modes.MCTIERSIO_AXE;

    public static DisplayStatus subtiersNETPosition = DisplayStatus.OFF;
    public static Modes activeSubtiersNETMode = Modes.SUBTIERSNET_MINECART;

    @Override
    public void onInitializeClient() {
        ConfigManager.loadConfig();
        deleteCacheDir();
        CommandRegister.registerCommands();
        FabricLoader.getInstance().getModContainer("tiers").ifPresent(tiers -> ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of("tiers", "custom-icons"), tiers, ResourcePackActivationType.ALWAYS_ENABLED));
        LOGGER.info("Tiers initialized");
    }

    private static void deleteCacheDir() {
        try {
            FileUtils.deleteDirectory(new File(FabricLoader.getInstance().getConfigDir() + "/tiers-cache"));
        } catch (IOException e) {
            LOGGER.warn("Error deleting cache folder: {}", e.getMessage());
        }
    }

    public static Text getFullName(String originalName, Text originalNameText) {
        PlayerProfile profile = addGetPlayer(originalName);

        if (profile.status != Status.FOUND
                || profile.mcTiersCOMProfile == null || profile.mcTiersCOMProfile.status == Status.SEARCHING || profile.mcTiersCOMProfile.status == Status.TIMEOUTED
                || profile.mcTiersIOProfile == null || profile.mcTiersIOProfile.status == Status.SEARCHING || profile.mcTiersIOProfile.status == Status.TIMEOUTED
                || profile.subtiersNETProfile == null || profile.subtiersNETProfile.status == Status.SEARCHING || profile.subtiersNETProfile.status == Status.TIMEOUTED) {
            return originalNameText;
        }

        MutableText rightText = Text.literal("");
        MutableText leftText = Text.literal("");
        MutableText iconRight = Text.literal("");
        MutableText iconLeft = Text.literal("");
        MutableText separatorRight = Text.literal("");
        MutableText separatorLeft = Text.literal("");
        String spaceRight = "";
        String spaceLeft = "";

        if (profile.mcTiersCOMProfile.status == Status.FOUND) {
            GameMode shown = profile.mcTiersCOMProfile.getGameMode(activeMCTiersCOMMode.toString());
            if (shown == null)
                return originalNameText;

            if (displayMode == ModesTierDisplay.HIGHEST) {
                shown = profile.mcTiersCOMProfile.highest;
            } else if (displayMode == ModesTierDisplay.ADAPTIVE_HIGHEST && shown.tier.equalsIgnoreCase("N/A")) {
                shown = profile.mcTiersCOMProfile.highest;
            }

            if (mcTiersCOMPosition == DisplayStatus.RIGHT && !shown.tier.equalsIgnoreCase("N/A")) {
                if (showIcons) {
                    iconRight = (MutableText) shown.name.getIconTag();
                    spaceRight = " ";
                }
                rightText = (MutableText) shown.displayedTier;
                separatorRight = Text.literal(" | ");
            }
            if (mcTiersCOMPosition == DisplayStatus.LEFT && !shown.tier.equalsIgnoreCase("N/A")) {
                if (showIcons) {
                    iconLeft = (MutableText) shown.name.getIconTag();
                    spaceLeft = " ";
                }
                leftText = (MutableText) shown.displayedTier;
                separatorLeft = Text.literal(" | ");
            }
        }

        if (profile.mcTiersIOProfile.status == Status.FOUND) {
            GameMode shown = profile.mcTiersIOProfile.getGameMode(activeMCTiersIOMode.toString());
            if (shown == null)
                return originalNameText;

            if (displayMode == ModesTierDisplay.HIGHEST) {
                shown = profile.mcTiersIOProfile.highest;
            } else if (displayMode == ModesTierDisplay.ADAPTIVE_HIGHEST && shown.tier.equalsIgnoreCase("N/A")) {
                shown = profile.mcTiersIOProfile.highest;
            }

            if (mcTiersIOPosition == DisplayStatus.RIGHT && !shown.tier.equalsIgnoreCase("N/A")) {
                if (showIcons) {
                    iconRight = (MutableText) shown.name.getIconTag();
                    spaceRight = " ";
                }
                rightText = (MutableText) shown.displayedTier;
                separatorRight = Text.literal(" | ");
            }
            if (mcTiersIOPosition == DisplayStatus.LEFT && !shown.tier.equalsIgnoreCase("N/A")) {
                if (showIcons) {
                    iconLeft = (MutableText) shown.name.getIconTag();
                    spaceLeft = " ";
                }
                leftText = (MutableText) shown.displayedTier;
                separatorLeft = Text.literal(" | ");
            }
        }

        if (profile.subtiersNETProfile.status == Status.FOUND) {
            GameMode shown = profile.subtiersNETProfile.getGameMode(activeSubtiersNETMode.toString());
            if (shown == null)
                return originalNameText;

            if (displayMode == ModesTierDisplay.HIGHEST) {
                shown = profile.subtiersNETProfile.highest;
            } else if (displayMode == ModesTierDisplay.ADAPTIVE_HIGHEST && shown.tier.equalsIgnoreCase("N/A")) {
                shown = profile.subtiersNETProfile.highest;
            }

            if (subtiersNETPosition == DisplayStatus.RIGHT && !shown.tier.equalsIgnoreCase("N/A")) {
                if (showIcons) {
                    iconRight = (MutableText) shown.name.getIconTag();
                    spaceRight = " ";
                }
                rightText = (MutableText) shown.displayedTier;
                separatorRight = Text.literal(" | ");
            }
            if (subtiersNETPosition == DisplayStatus.LEFT && !shown.tier.equalsIgnoreCase("N/A")) {
                if (showIcons) {
                    iconLeft = (MutableText) shown.name.getIconTag();
                    spaceLeft = " ";
                }
                leftText = (MutableText) shown.displayedTier;
                separatorLeft = Text.literal(" | ");
            }
        }

        if (isSeparatorAdaptive) {
            separatorRight.setStyle(rightText.getStyle());
            separatorLeft.setStyle(leftText.getStyle());
        } else {
            separatorRight.setStyle(Style.EMPTY.withColor(0xaaaaaa));
            separatorLeft.setStyle(Style.EMPTY.withColor(0xaaaaaa));
        }

        return Text.literal("")
                .append(iconLeft)
                .append(spaceLeft)
                .append(leftText)
                .append(separatorLeft)
                .append(originalNameText)
                .append(separatorRight)
                .append(rightText)
                .append(spaceRight)
                .append(iconRight);
    }

    public static void sendMessageToPlayer(String chat_message, int color) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null)
            client.player.sendMessage((Text.literal(chat_message).setStyle(Style.EMPTY.withColor(color))), false);
    }

    protected static int toggleMod(CommandContext<FabricClientCommandSource> ignoredFabricClientCommandSourceCommandContext) {
        toggleMod = !toggleMod;
        ConfigManager.saveConfig();
        sendMessageToPlayer("Mod is now " + (toggleMod ? "enabled" : "disabled"), (toggleMod ? 0x00ff00 : 0xff0000));
        return 1;
    }

    public static void toggleMod() {
        toggleMod = !toggleMod;
        ConfigManager.saveConfig();
    }

    public static PlayerProfile addGetPlayer(String name) {
        for (PlayerProfile profile : playerProfiles) {
            if (profile.name.equalsIgnoreCase(name))
                return profile;
        }
        PlayerProfile newProfile = new PlayerProfile(name);
        playerProfiles.add(newProfile);
        return newProfile;
    }

    private static void openPlayerSearchResultScreen(PlayerProfile profile) {
        MinecraftClient.getInstance().setScreen(new PlayerSearchResultScreen(profile));
    }

    protected static int searchPlayer(String name) {
        CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS)
                .execute(() -> MinecraftClient.getInstance().execute(() -> openPlayerSearchResultScreen(addGetPlayer(name))));
        return 1;
    }

    public static void clearPlayerCache() {
        playerProfiles.clear();
        deleteCacheDir();
    }

    public static void toggleSeparatorAdaptive() {
        isSeparatorAdaptive = !isSeparatorAdaptive;
        ConfigManager.saveConfig();
    }

    public static void toggleShowIcons() {
        showIcons = !showIcons;
        ConfigManager.saveConfig();
    }

    public static void cycleMCTiersCOMMode() {
        activeMCTiersCOMMode = cycleEnum(activeMCTiersCOMMode, Modes.getMCTiersCOMValues());
        ConfigManager.saveConfig();
    }

    public static void cycleMCTiersIOMode() {
        activeMCTiersIOMode = cycleEnum(activeMCTiersIOMode, Modes.getMCTiersIOValues());
        ConfigManager.saveConfig();
    }

    public static void cycleSubtiersNETMode() {
        activeSubtiersNETMode = cycleEnum(activeSubtiersNETMode, Modes.getSubtiersNETValues());
        ConfigManager.saveConfig();
    }

    public static void cycleMCTiersCOMPosition() {
        mcTiersCOMPosition = cycleEnum(mcTiersCOMPosition, DisplayStatus.values());
        ConfigManager.saveConfig();
    }

    public static void cycleMCTiersIOPosition() {
        mcTiersIOPosition = cycleEnum(mcTiersIOPosition, DisplayStatus.values());
        ConfigManager.saveConfig();
    }

    public static void cycleSubtiersNETPosition() {
        subtiersNETPosition = cycleEnum(subtiersNETPosition, DisplayStatus.values());
        ConfigManager.saveConfig();
    }

    public static void cycleDisplayMode() {
        displayMode = cycleEnum(displayMode, ModesTierDisplay.values());
        ConfigManager.saveConfig();
    }

    private static <T extends Enum<T>> T cycleEnum(T current, T[] values) {
        return values[(current.ordinal() + 1) % values.length];
    }

    public enum Modes {
        MCTIERSCOM_VANILLA(Icons.vanilla, Icons.vanillaTag, Text.literal("Vanilla").setStyle(Style.EMPTY.withColor(0xc889e6))),
        MCTIERSCOM_UHC(Icons.uhc, Icons.uhcTag, Text.literal("UHC").setStyle(Style.EMPTY.withColor(0xd44e50))),
        MCTIERSCOM_POT(Icons.pot, Icons.potTag, Text.literal("Pot").setStyle(Style.EMPTY.withColor(0xd65474))),
        MCTIERSCOM_NETHERITE_OP(Icons.netherite, Icons.netheriteTag, Text.literal("Netherite Op").setStyle(Style.EMPTY.withColor(0x8f72a5))),
        MCTIERSCOM_SMP(Icons.smp, Icons.smpTag, Text.literal("Smp").setStyle(Style.EMPTY.withColor(0x0f5045))),
        MCTIERSCOM_SWORD(Icons.sword, Icons.swordTag, Text.literal("Sword").setStyle(Style.EMPTY.withColor(0x71bfdd))),
        MCTIERSCOM_AXE(Icons.axe, Icons.axeTag, Text.literal("Axe").setStyle(Style.EMPTY.withColor(0x6195d9))),
        MCTIERSCOM_MACE(Icons.mace, Icons.maceTag, Text.literal("Mace").setStyle(Style.EMPTY.withColor(0x4c4c5b))),
        MCTIERSIO_VANILLA(Icons.vanilla, Icons.vanillaTag, Text.literal("Vanilla").setStyle(Style.EMPTY.withColor(0xc889e6))),
        MCTIERSIO_UHC(Icons.uhc, Icons.uhcTag, Text.literal("UHC").setStyle(Style.EMPTY.withColor(0xd44e50))),
        MCTIERSIO_POT(Icons.pot, Icons.potTag, Text.literal("Pot").setStyle(Style.EMPTY.withColor(0xd65474))),
        MCTIERSIO_NETHERITE_POT(Icons.netherite, Icons.netheriteTag, Text.literal("Netherite Pot").setStyle(Style.EMPTY.withColor(0x8f72a5))),
        MCTIERSIO_SMP(Icons.smp, Icons.smpTag, Text.literal("Smp").setStyle(Style.EMPTY.withColor(0x0f5045))),
        MCTIERSIO_SWORD(Icons.sword, Icons.swordTag, Text.literal("Sword").setStyle(Style.EMPTY.withColor(0x71bfdd))),
        MCTIERSIO_AXE(Icons.axe, Icons.axeTag, Text.literal("Axe").setStyle(Style.EMPTY.withColor(0x6195d9))),
        MCTIERSIO_ELYTRA(Icons.elytra, Icons.elytraTag, Text.literal("Elytra").setStyle(Style.EMPTY.withColor(0x533d4e))),
        SUBTIERSNET_MINECART(Icons.minecart, Icons.minecartTag, Text.literal("Minecart").setStyle(Style.EMPTY.withColor(0xdb441a))),
        SUBTIERSNET_DIAMOND_CRYSTAL(Icons.diamond_crystal, Icons.diamond_crystalTag, Text.literal("Diamond Crystal").setStyle(Style.EMPTY.withColor(0x66c4ff))),
        SUBTIERSNET_IRON_POT(Icons.iron_pot, Icons.iron_potTag, Text.literal("Iron Pot").setStyle(Style.EMPTY.withColor(0xbbbbbb))),
        SUBTIERSNET_ELYTRA(Icons.subtiers_elytra, Icons.subtiers_elytraTag, Text.literal("Elytra").setStyle(Style.EMPTY.withColor(0x8b8cc8))),
        SUBTIERSNET_SPEED(Icons.speed, Icons.speedTag, Text.literal("Speed").setStyle(Style.EMPTY.withColor(0x6dc4cd))),
        SUBTIERSNET_CREEPER(Icons.creeper, Icons.creeperTag, Text.literal("Creeper").setStyle(Style.EMPTY.withColor(0x89df89))),
        SUBTIERSNET_MANHUNT(Icons.manhunt, Icons.manhuntTag, Text.literal("Manhunt").setStyle(Style.EMPTY.withColor(0x424242))),
        SUBTIERSNET_DIAMOND_SMP(Icons.diamond_smp, Icons.diamond_smpTag, Text.literal("Diamond Smp").setStyle(Style.EMPTY.withColor(0x8e658c))),
        SUBTIERSNET_BOW(Icons.bow, Icons.bowTag, Text.literal("Bow").setStyle(Style.EMPTY.withColor(0x91705c))),
        SUBTIERSNET_BED(Icons.bed, Icons.bedTag, Text.literal("Bed").setStyle(Style.EMPTY.withColor(0xb12f28))),
        SUBTIERSNET_OG_VANILLA(Icons.og_vanilla, Icons.og_vanillaTag, Text.literal("OG Vanilla").setStyle(Style.EMPTY.withColor(0xe9b750))),
        SUBTIERSNET_TRIDENT(Icons.trident, Icons.tridentTag, Text.literal("Trident").setStyle(Style.EMPTY.withColor(0x42957e)));

        private final Text icon;
        private final Text iconTag;
        private final Text label;

        Modes(Text icon, Text iconTag, Text label) {
            this.icon = icon;
            this.iconTag = iconTag;
            this.label = label;
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