package com.tiers;

import com.mojang.brigadier.context.CommandContext;
import com.tiers.misc.*;
import com.tiers.profile.GameMode;
import com.tiers.profile.PlayerProfile;
import com.tiers.profile.Status;
import com.tiers.profile.types.SuperProfile;
import com.tiers.screens.ConfigScreen;
import com.tiers.screens.PlayerSearchResultScreen;
import com.tiers.textures.ColorControl;
import com.tiers.textures.ColorLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TiersClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(TiersClient.class);
    public static String userAgent = "Tiers";
    private static final ArrayList<PlayerProfile> playerProfiles = new ArrayList<>();
    private static final HashMap<String, Text> playerTexts = new HashMap<>();

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

    private static KeyBinding autoDetectKey;
    private static KeyBinding cycleRightKey;
    private static KeyBinding cycleLeftKey;

    @Override
    public void onInitializeClient() {
        ConfigManager.loadConfig();
        clearCache(true);
        CommandRegister.registerCommands();

        Optional<ModContainer> fabricLoader = FabricLoader.getInstance().getModContainer("tiers");
        if (fabricLoader.isPresent()) {
            fabricLoader.ifPresent(tiers -> ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of("tiers", "modern"), tiers, ResourcePackActivationType.ALWAYS_ENABLED));
            fabricLoader.ifPresent(tiers -> ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of("tiers", "classic"), tiers, ResourcePackActivationType.NORMAL));
            userAgent = "Tiers " + fabricLoader.get().getMetadata().getVersion().getFriendlyString() + " on " + MinecraftClient.getInstance().getGameVersion() + " played by " + MinecraftClient.getInstance().getGameProfile().getName();
        } else
            LOGGER.warn("Error initializing Tiers. Please report this issue: https://github.com/Flavio6561/Tiers/issues");

        autoDetectKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Auto Detect Kit", GLFW.GLFW_KEY_Y, "Tiers"));
        cycleRightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Cycle Right Gamemodes", GLFW.GLFW_KEY_I, "Tiers"));
        cycleLeftKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Cycle Left Gamemodes", GLFW.GLFW_KEY_U, "Tiers"));

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ColorLoader());
        ClientTickEvents.END_CLIENT_TICK.register(TiersClient::tickUtils);

        LOGGER.info("Tiers initialized | User agent: {}", userAgent);
    }

    public static Text getModifiedNametag(String originalName, Text originalNameText) {
        PlayerProfile profile = addGetPlayer(originalName, false);
        if (profile.status == Status.READY)
            if (profile.originalNameText == null || profile.originalNameText != originalNameText)
                updatePlayerNametag(originalNameText, profile);

        if (playerTexts.containsKey(originalName)) return playerTexts.get(originalName);

        return originalNameText;
    }

    public static Text getNametag(PlayerProfile profile) {
        if (!toggleMod || profile.status != Status.READY) return profile.originalNameText;

        Text rightText = Text.literal("");
        Text leftText = Text.literal("");

        if (mcTiersCOMPosition == DisplayStatus.RIGHT)
            rightText = updateProfileNameTagRight(profile.mcTiersCOMProfile, activeMCTiersCOMMode);
        else if (mcTiersCOMPosition == DisplayStatus.LEFT)
            leftText = updateProfileNameTagLeft(profile.mcTiersCOMProfile, activeMCTiersCOMMode);

        if (mcTiersIOPosition == DisplayStatus.RIGHT)
            rightText = updateProfileNameTagRight(profile.mcTiersIOProfile, activeMCTiersIOMode);
        else if (mcTiersIOPosition == DisplayStatus.LEFT)
            leftText = updateProfileNameTagLeft(profile.mcTiersIOProfile, activeMCTiersIOMode);

        if (subtiersNETPosition == DisplayStatus.RIGHT)
            rightText = updateProfileNameTagRight(profile.subtiersNETProfile, activeSubtiersNETMode);
        else if (subtiersNETPosition == DisplayStatus.LEFT)
            leftText = updateProfileNameTagLeft(profile.subtiersNETProfile, activeSubtiersNETMode);

        return Text.literal("")
                .append(leftText)
                .append(profile.originalNameText)
                .append(rightText);
    }

    private static void updateAllTags() {
        for (PlayerProfile profile : playerProfiles)
            if (profile.status == Status.READY && profile.originalNameText != null)
                updatePlayerNametag(profile.originalNameText, profile);

        if (ConfigScreen.ownProfile.status == Status.READY)
            updatePlayerNametag(ConfigScreen.ownProfile.originalNameText, ConfigScreen.ownProfile);
        updatePlayerNametag(ConfigScreen.defaultProfile.originalNameText, ConfigScreen.defaultProfile);
    }

    private static void updatePlayerNametag(Text originalNameText, PlayerProfile profile) {
        Text rightText = Text.literal("");
        Text leftText = Text.literal("");

        if (mcTiersCOMPosition == DisplayStatus.RIGHT)
            rightText = updateProfileNameTagRight(profile.mcTiersCOMProfile, activeMCTiersCOMMode);
        else if (mcTiersCOMPosition == DisplayStatus.LEFT)
            leftText = updateProfileNameTagLeft(profile.mcTiersCOMProfile, activeMCTiersCOMMode);

        if (mcTiersIOPosition == DisplayStatus.RIGHT)
            rightText = updateProfileNameTagRight(profile.mcTiersIOProfile, activeMCTiersIOMode);
        else if (mcTiersIOPosition == DisplayStatus.LEFT)
            leftText = updateProfileNameTagLeft(profile.mcTiersIOProfile, activeMCTiersIOMode);

        if (subtiersNETPosition == DisplayStatus.RIGHT)
            rightText = updateProfileNameTagRight(profile.subtiersNETProfile, activeSubtiersNETMode);
        else if (subtiersNETPosition == DisplayStatus.LEFT)
            leftText = updateProfileNameTagLeft(profile.subtiersNETProfile, activeSubtiersNETMode);

        playerTexts.put(profile.name, Text.literal("")
                .append(leftText)
                .append(originalNameText)
                .append(rightText));

        profile.originalNameText = originalNameText;
    }

    private static Text updateProfileNameTagRight(SuperProfile profile, Modes activeMode) {
        MutableText returnValue = Text.literal("");
        if (profile.status == Status.READY) {
            GameMode shown = profile.getGameMode(activeMode);

            if ((shown == null || shown.status == Status.SEARCHING) || (shown.status == Status.NOT_EXISTING && displayMode == ModesTierDisplay.SELECTED))
                return returnValue;

            if (displayMode == ModesTierDisplay.ADAPTIVE_HIGHEST && shown.status == Status.NOT_EXISTING && profile.highest != null)
                shown = profile.highest;

            if (displayMode == ModesTierDisplay.HIGHEST && profile.highest != null && profile.highest.getTierPoints(false) > shown.getTierPoints(false))
                shown = profile.highest;

            if (shown == null || shown.status != Status.READY)
                return returnValue;

            MutableText separator = Text.literal(" | ").setStyle(isSeparatorAdaptive ? shown.displayedTier.getStyle() : Style.EMPTY.withColor(ColorControl.getColor("static_separator")));
            returnValue.append(Text.literal("").append(separator).append(shown.displayedTier));

            if (showIcons)
                returnValue.append(Text.literal(" ").append(shown.name.iconTag));
        }
        return returnValue;
    }

    private static Text updateProfileNameTagLeft(SuperProfile profile, Modes activeMode) {
        MutableText returnValue = Text.literal("");
        if (profile.status == Status.READY) {
            GameMode shown = profile.getGameMode(activeMode);

            if ((shown == null || shown.status == Status.SEARCHING) || (shown.status == Status.NOT_EXISTING && displayMode == ModesTierDisplay.SELECTED))
                return returnValue;

            if (displayMode == ModesTierDisplay.ADAPTIVE_HIGHEST && shown.status == Status.NOT_EXISTING && profile.highest != null)
                shown = profile.highest;

            if (displayMode == ModesTierDisplay.HIGHEST && profile.highest != null && profile.highest.getTierPoints(false) > shown.getTierPoints(false))
                shown = profile.highest;

            if (shown == null || shown.status != Status.READY)
                return returnValue;

            MutableText separator = Text.literal(" | ").setStyle(isSeparatorAdaptive ? shown.displayedTier.getStyle() : Style.EMPTY.withColor(ColorControl.getColor("static_separator")));

            if (showIcons)
                returnValue = Text.literal("").append(shown.name.iconTag).append(" ");
            returnValue.append(Text.literal("").append(shown.displayedTier).append(separator));
        }
        return returnValue;
    }

    public static void restyleAllTexts() {
        for (PlayerProfile profile : playerProfiles) {
            if (profile.status == Status.READY) {
                if (profile.mcTiersCOMProfile.status == Status.READY)
                    profile.mcTiersCOMProfile.parseJson(profile.mcTiersCOMProfile.originalJson);
                if (profile.mcTiersIOProfile.status == Status.READY)
                    profile.mcTiersIOProfile.parseJson(profile.mcTiersIOProfile.originalJson);
                if (profile.subtiersNETProfile.status == Status.READY)
                    profile.subtiersNETProfile.parseJson(profile.subtiersNETProfile.originalJson);
            }
        }
    }

    private static void tickUtils(MinecraftClient client) {
        if (!client.isFinishedLoading()) return;

        if (ConfigScreen.defaultProfile == null) {
            ConfigScreen.ownProfile = new PlayerProfile(client.getGameProfile().getName(), false);
            PlayerProfileQueue.enqueue(ConfigScreen.ownProfile);

            ConfigScreen.defaultProfile = new PlayerProfile("{\"id\":\"3b653c04f2d9422a87e7ccf8b146c350\",\"name\":\"TheRandomizer\"}",
                    "{\"uuid\":\"3b653c04f2d9422a87e7ccf8b146c350\",\"name\":\"TheRandomizer\",\"rankings\":{\"axe\":{\"tier\":1,\"pos\":0,\"peak_tier\":1,\"peak_pos\":0,\"attained\":1701927844,\"retired\":true},\"smp\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1712888458,\"retired\":false},\"sword\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1733880489,\"retired\":false},\"uhc\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1709746421,\"retired\":false},\"vanilla\":{\"tier\":4,\"pos\":0,\"peak_tier\":4,\"peak_pos\":0,\"attained\":1713570014,\"retired\":false},\"nethop\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1710301241,\"retired\":false},\"pot\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1723349754,\"retired\":false},\"mace\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1737168704,\"retired\":false}},\"region\":\"NA\",\"points\":116,\"overall\":28,\"badges\":[{\"title\":\"Axe Champion\",\"desc\":\"Attained T1+ in Axe for any amount of time\"},{\"title\":\"Axe Expert\",\"desc\":\"Attained T2+ in Axe for any amount of time\"},{\"title\":\"Adventurous\",\"desc\":\"Attained a tier on every present current gamemode\"},{\"title\":\"Holding The Crown\",\"desc\":\"T1 category of 1 gamemode for 30 days or more\"}],\"combat_master\":false}",
                    "{\"uuid\":\"3b653c04f2d9422a87e7ccf8b146c350\",\"name\":\"TheRandomizer\",\"rankings\":{\"pot\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1723349754,\"retired\":false},\"crystal\":{\"tier\":4,\"pos\":0,\"peak_tier\":4,\"peak_pos\":0,\"attained\":1713570014,\"retired\":false},\"uhc\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1709746421,\"retired\":false},\"sword\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1733880489,\"retired\":false},\"smp\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1712888458,\"retired\":false},\"neth_pot\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1710301241,\"retired\":false},\"axe\":{\"tier\":1,\"pos\":1,\"peak_tier\":1,\"peak_pos\":1,\"attained\":1744884400,\"retired\":true}},\"region\":\"EU\",\"points\":90,\"overall\":44,\"badges\":[{\"title\":\"Axe Champion\",\"desc\":\"Attained T1+ in Axe for any amount of time\"},{\"title\":\"Axe Expert\",\"desc\":\"Attained T2+ in Axe for any amount of time\"},{\"title\":\"Adventurous\",\"desc\":\"Attained a tier on every present current gamemode\"},{\"title\":\"Holding The Crown\",\"desc\":\"T1 category of 1 gamemode for 30 days or more\"}]}",
                    "{\"uuid\":\"3b653c04f2d9422a87e7ccf8b146c350\",\"name\":\"TheRandomizer\",\"rankings\":{\"bed\":{\"tier\":4,\"pos\":0,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1748753398,\"retired\":false},\"speed\":{\"tier\":1,\"pos\":1,\"peak_tier\":1,\"peak_pos\":1,\"attained\":1747584669,\"retired\":false},\"dia_smp\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1734383100,\"retired\":false},\"og_vanilla\":{\"tier\":2,\"pos\":0,\"peak_tier\":2,\"peak_pos\":0,\"attained\":1746316718,\"retired\":true},\"bow\":{\"tier\":4,\"pos\":1,\"peak_tier\":4,\"peak_pos\":1,\"attained\":1747630846,\"retired\":false},\"debuff\":{\"tier\":2,\"pos\":1,\"peak_tier\":2,\"peak_pos\":1,\"attained\":1746668477,\"retired\":false},\"manhunt\":{\"tier\":2,\"pos\":1,\"peak_tier\":2,\"peak_pos\":1,\"attained\":1736014926,\"retired\":true},\"trident\":{\"tier\":3,\"pos\":0,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1741136796,\"retired\":false},\"elytra\":{\"tier\":2,\"pos\":1,\"peak_tier\":2,\"peak_pos\":1,\"attained\":1747112500,\"retired\":false},\"dia_crystal\":{\"tier\":3,\"pos\":1,\"peak_tier\":3,\"peak_pos\":1,\"attained\":1747589669,\"retired\":false},\"minecart\":{\"tier\":3,\"pos\":0,\"peak_tier\":3,\"peak_pos\":0,\"attained\":1747032612,\"retired\":false},\"creeper\":{\"tier\":1,\"pos\":0,\"peak_tier\":1,\"peak_pos\":0,\"attained\":1748398520,\"retired\":false}},\"region\":\"NA\",\"points\":236,\"overall\":1,\"badges\":[{\"title\":\"Speed Expert\",\"desc\":\"Attained T2+ in Speed for any amount of time\"},{\"title\":\"Creeper Expert\",\"desc\":\"Attained T2+ in Creeper for any amount of time\"},{\"title\":\"OG Vanilla Expert\",\"desc\":\"Attained T2+ in OG Vanilla for any amount of time\"},{\"title\":\"Manhunt Expert\",\"desc\":\"Attained T2+ in Manhunt for any amount of time\"},{\"title\":\"Adventurous\",\"desc\":\"Attained a tier on every present current gamemode\"},{\"title\":\"Creeper Champion\",\"desc\":\"Attained T1+ in Creeper for any amount of time\"},{\"title\":\"DeBuff Expert\",\"desc\":\"Attained T2+ in DeBuff for any amount of time\"},{\"title\":\"Holding The Crown\",\"desc\":\"T1 category of 1 gamemode for 30 days or more\"},{\"title\":\"Elytra Expert\",\"desc\":\"Attained T2+ in Elytra for any amount of time\"},{\"title\":\"Speed Champion\",\"desc\":\"Attained T1+ in Speed for any amount of time\"}],\"combat_master\":false}");
        }

        if (autoDetectKey.wasPressed())
            InventoryChecker.checkInventory(client);

        if (cycleRightKey.wasPressed()) {
            Text message = cycleRightMode();

            if (message != null)
                sendMessageToPlayer(message, true);
            else
                sendMessageToPlayer(Text.literal("There's nothing on the right display").setStyle(Style.EMPTY.withColor(ColorControl.getColor("red"))), true);
        }

        if (cycleLeftKey.wasPressed()) {
            Text message = cycleLeftMode();

            if (message != null)
                sendMessageToPlayer(message, true);
            else
                sendMessageToPlayer(Text.literal("There's nothing on the left display").setStyle(Style.EMPTY.withColor(ColorControl.getColor("red"))), true);
        }
    }

    public static Text cycleRightMode() {
        if (mcTiersCOMPosition.toString().equalsIgnoreCase("RIGHT"))
            return Text.literal("Right (MCTiersCOM) is now displaying ").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))).append(cycleMCTiersCOMMode());

        if (mcTiersIOPosition.toString().equalsIgnoreCase("RIGHT"))
            return Text.literal("Right (MCTiersIO) is now displaying ").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))).append(cycleMCTiersIOMode());

        if (subtiersNETPosition.toString().equalsIgnoreCase("RIGHT"))
            return Text.literal("Right (SubtiersNET) is now displaying ").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))).append(cycleSubtiersNETMode());

        return null;
    }

    public static Text cycleLeftMode() {
        if (mcTiersCOMPosition.toString().equalsIgnoreCase("LEFT"))
            return Text.literal("Left (MCTiersCOM) is now displaying ").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))).append(cycleMCTiersCOMMode());

        if (mcTiersIOPosition.toString().equalsIgnoreCase("LEFT"))
            return Text.literal("Left (MCTiersIO) is now displaying ").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))).append(cycleMCTiersIOMode());

        if (subtiersNETPosition.toString().equalsIgnoreCase("LEFT"))
            return Text.literal("Left (SubtiersNET) is now displaying ").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))).append(cycleSubtiersNETMode());

        return null;
    }

    public static Text getRightIcon() {
        if (mcTiersCOMPosition.toString().equalsIgnoreCase("RIGHT"))
            return activeMCTiersCOMMode.icon;

        if (mcTiersIOPosition.toString().equalsIgnoreCase("RIGHT"))
            return activeMCTiersIOMode.icon;

        if (subtiersNETPosition.toString().equalsIgnoreCase("RIGHT"))
            return activeSubtiersNETMode.icon;

        return Text.of("");
    }

    public static Text getLeftIcon() {
        if (mcTiersCOMPosition.toString().equalsIgnoreCase("LEFT"))
            return activeMCTiersCOMMode.icon;

        if (mcTiersIOPosition.toString().equalsIgnoreCase("LEFT"))
            return activeMCTiersIOMode.icon;

        if (subtiersNETPosition.toString().equalsIgnoreCase("LEFT"))
            return activeSubtiersNETMode.icon;

        return Text.of("");
    }

    public static void sendMessageToPlayer(String message, int color, boolean overlay) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null)
            client.player.sendMessage((Text.literal(message).setStyle(Style.EMPTY.withColor(color))), overlay);
    }

    public static void sendMessageToPlayer(Text message, boolean overlay) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null)
            client.player.sendMessage(message, overlay);
    }

    public static int toggleMod(CommandContext<FabricClientCommandSource> ignoredFabricClientCommandSourceCommandContext) {
        toggleMod = !toggleMod;
        ConfigManager.saveConfig();
        sendMessageToPlayer("Tiers is now " + (toggleMod ? "enabled" : "disabled"), (toggleMod ? ColorControl.getColor("green") : ColorControl.getColor("red")), true);
        return 1;
    }

    public static void toggleMod() {
        toggleMod = !toggleMod;
        ConfigManager.saveConfig();
    }

    private static PlayerProfile addGetPlayer(String name, boolean priority) {
        for (PlayerProfile profile : playerProfiles) {
            if (profile.name.equalsIgnoreCase(name)) {
                if (priority)
                    PlayerProfileQueue.changeToFirstInQueue(profile);
                return profile;
            }
        }
        PlayerProfile newProfile = new PlayerProfile(name, true);

        if (priority)
            PlayerProfileQueue.putFirstInQueue(newProfile);
        else
            PlayerProfileQueue.enqueue(newProfile);

        playerProfiles.add(newProfile);
        return newProfile;
    }

    private static void openPlayerSearchResultScreen(PlayerProfile profile) {
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new PlayerSearchResultScreen(profile)));
    }

    private static void openConfigScreen() {
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(ConfigScreen.getConfigScreen(null)));
    }

    public static int searchPlayer(String name) {
        if (name.equalsIgnoreCase("toggle"))
            toggleMod(null);
        else if (name.equalsIgnoreCase("config"))
            CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS).execute(TiersClient::openConfigScreen);
        else
            CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS).execute(() -> openPlayerSearchResultScreen(addGetPlayer(name, true)));
        return 1;
    }

    public static void clearCache(boolean start) {
        playerProfiles.clear();
        playerTexts.clear();
        PlayerProfileQueue.clearQueue();
        try {
            FileUtils.deleteDirectory(new File(FabricLoader.getInstance().getGameDir() + (start ? "/cache/tiers" : "/cache/tiers/players")));
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

    public static Text cycleMCTiersCOMMode() {
        activeMCTiersCOMMode = cycleEnum(activeMCTiersCOMMode, Modes.getMCTiersCOMValues());
        updateAllTags();
        ConfigManager.saveConfig();
        return activeMCTiersCOMMode.label;
    }

    public static Text cycleMCTiersIOMode() {
        activeMCTiersIOMode = cycleEnum(activeMCTiersIOMode, Modes.getMCTiersIOValues());
        updateAllTags();
        ConfigManager.saveConfig();
        return activeMCTiersIOMode.label;
    }

    public static Text cycleSubtiersNETMode() {
        activeSubtiersNETMode = cycleEnum(activeSubtiersNETMode, Modes.getSubtiersNETValues());
        updateAllTags();
        ConfigManager.saveConfig();
        return activeSubtiersNETMode.label;
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

    public enum ModesTierDisplay {
        HIGHEST,
        SELECTED,
        ADAPTIVE_HIGHEST;

        public String getCurrentMode() {
            if (this.toString().equalsIgnoreCase("HIGHEST"))
                return "Displayed Tiers: Highest";
            else if (this.toString().equalsIgnoreCase("SELECTED"))
                return "Displayed Tiers: Selected";
            return "Displayed Tiers: Adaptive Highest";
        }
    }

    public enum DisplayStatus {
        RIGHT,
        LEFT,
        OFF;

        public String getStatus() {
            if (this.toString().equalsIgnoreCase("RIGHT"))
                return "Right";
            else if (this.toString().equalsIgnoreCase("LEFT"))
                return "Left";
            return "Off";
        }
    }
}