package com.tiers;

import com.mojang.brigadier.context.CommandContext;
import com.tiers.misc.CommandRegister;
import com.tiers.misc.ConfigManager;
import com.tiers.misc.Mode;
import com.tiers.mixin.client.TextDisplayEntityInvokerClientMixin;
import com.tiers.profile.PlayerProfile;
import com.tiers.profile.Status;
import com.tiers.screens.ConfigScreen;
import com.tiers.screens.PlayerSearchResultScreen;
import com.tiers.textures.ColorLoader;
import com.tiers.textures.Icons;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TiersClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(TiersClient.class);
    public static String userAgent = "Tiers (modrinth.com/mod/tiers)";
    public static final ArrayList<PlayerProfile> playerProfiles = new ArrayList<>();

    public static boolean toggleMod = true;
    public static boolean toggleTab = false;
    public static boolean showIcons = true;
    public static boolean isSeparatorAdaptive = true;
    public static boolean autoKitDetect = false;
    public static ModesTierDisplay displayMode = ModesTierDisplay.ADAPTIVE_HIGHEST;
    public static Icons.Type activeIcons = Icons.Type.PVPTIERS;

    public static DisplayStatus positionMCTiers = DisplayStatus.OFF;
    public static Mode activeMCTiersMode = Mode.MCTIERS_VANILLA;

    public static DisplayStatus positionPvPTiers = DisplayStatus.LEFT;
    public static Mode activePvPTiersMode = Mode.PVPTIERS_CRYSTAL;

    public static DisplayStatus positionSubtiers = DisplayStatus.RIGHT;
    public static Mode activeSubtiersMode = Mode.SUBTIERS_MINECART;

    public static KeyBinding autoDetectKey;
    public static KeyBinding openClosestPlayerProfile;
    public static KeyBinding cycleRightKey;
    public static KeyBinding cycleLeftKey;

    public static boolean isOnLunar;

    @Override
    public void onInitializeClient() {
        ConfigManager.loadConfig();
        changeIcons(activeIcons, false);
        clearCache(true);
        CommandRegister.registerCommands();

        isOnLunar = ClientBrandRetriever.getClientModName().contains("lunarclient");

        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer("tiers");

        modContainer.ifPresent(tiers -> {
            ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of("resourcepacks", "tiers-resources"), tiers, Text.of("Resources for Tiers"), ResourcePackActivationType.ALWAYS_ENABLED);
            userAgent += " v" + tiers.getMetadata().getVersion().getFriendlyString();
        });

        autoDetectKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Auto Detect Kit", GLFW.GLFW_KEY_Y, "Tiers"));
        openClosestPlayerProfile = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open Closest Player Profile", GLFW.GLFW_KEY_H, "Tiers"));
        cycleRightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Cycle Right Gamemodes", GLFW.GLFW_KEY_I, "Tiers"));
        cycleLeftKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Cycle Left Gamemodes", GLFW.GLFW_KEY_U, "Tiers"));

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ColorLoader());
        ClientTickEvents.END_CLIENT_TICK.register(TiersClient::checkKeys);
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if (autoKitDetect)
                InventoryChecker.checkInventory(minecraftClient, false);
        });

        LOGGER.info("Tiers initialized | User agent: {}", userAgent);
    }

    public static PlayerProfile addGetPlayer(String playerName, boolean priority) {
        for (PlayerProfile playerProfile : playerProfiles) {
            if (playerProfile.name.equalsIgnoreCase(playerName) || playerProfile.originalName.equalsIgnoreCase(playerName)) {
                if (priority)
                    PlayerProfileQueue.changeToFirstInQueue(playerProfile);
                return playerProfile;
            }
        }
        PlayerProfile newProfile = new PlayerProfile(playerName, true);

        if (priority)
            PlayerProfileQueue.putFirstInQueue(newProfile);
        else
            PlayerProfileQueue.enqueue(newProfile);

        playerProfiles.add(newProfile);
        return newProfile;
    }

    public static void updateAllTags() {
        for (PlayerProfile playerProfile : playerProfiles)
            playerProfile.updateAppendingText();

        if (ConfigScreen.ownProfile != null && ConfigScreen.defaultProfile != null) {
            ConfigScreen.ownProfile.updateAppendingText();
            ConfigScreen.defaultProfile.updateAppendingText();
        }
    }

    public static void restyleAllTexts(ArrayList<PlayerProfile> playerProfiles) {
        for (PlayerProfile playerProfile : playerProfiles) {
            if (playerProfile.status == Status.READY) {
                if (playerProfile.profileMCTiers.status == Status.READY)
                    playerProfile.profileMCTiers.parseJson(playerProfile.profileMCTiers.originalJson);
                if (playerProfile.profilePvPTiers.status == Status.READY)
                    playerProfile.profilePvPTiers.parseJson(playerProfile.profilePvPTiers.originalJson);
                if (playerProfile.profileSubtiers.status == Status.READY)
                    playerProfile.profileSubtiers.parseJson(playerProfile.profileSubtiers.originalJson);
            }
        }
    }

    public static String getNearestPlayerName() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        PlayerEntity self = minecraftClient.player;
        if (self == null || self.getWorld() == null)
            return null;

        PlayerEntity playerEntity = self.getWorld().getPlayers().stream()
                .filter(player -> player != self)
                .filter(player -> self.distanceTo(player) < MinecraftClient.getInstance().gameRenderer.getViewDistance())
                .min(Comparator.comparingDouble(self::distanceTo))
                .orElse(null);

        if (playerEntity != null)
            return playerEntity.getNameForScoreboard();
        return null;
    }

    private static void checkKeys(MinecraftClient minecraftClient) {
        if (autoDetectKey.wasPressed())
            InventoryChecker.checkInventory(minecraftClient, true);

        if (openClosestPlayerProfile.wasPressed()) {
            String nearestPlayerName = getNearestPlayerName();
            if (nearestPlayerName != null)
                searchPlayer(nearestPlayerName);
            else
                sendMessageToPlayer(Icons.colorText("No players in render distance", "red"), true);
        }

        if (cycleRightKey.wasPressed()) {
            Text message = cycleRightMode();

            if (message != null)
                sendMessageToPlayer(message, true);
            else
                sendMessageToPlayer(Icons.colorText("There's nothing on the right display", "red"), true);
        }

        if (cycleLeftKey.wasPressed()) {
            Text message = cycleLeftMode();

            if (message != null)
                sendMessageToPlayer(message, true);
            else
                sendMessageToPlayer(Icons.colorText("There's nothing on the left display", "red"), true);
        }
    }

    public static Text cycleRightMode() {
        if (autoKitDetect) {
            autoKitDetect = false;
            sendMessageToPlayer(Icons.colorText("Auto kit detect has been disabled due to manual gamemode changes", "red"), false);
        }

        if (positionMCTiers.toString().equalsIgnoreCase("RIGHT"))
            return Text.literal("Right (MCTiers) is now displaying ").setStyle(Style.EMPTY.withColor(Colors.WHITE)).append(cycleMCTiersMode());

        if (positionPvPTiers.toString().equalsIgnoreCase("RIGHT"))
            return Text.literal("Right (PvPTiers) is now displaying ").setStyle(Style.EMPTY.withColor(Colors.WHITE)).append(cyclePvPTiersMode());

        if (positionSubtiers.toString().equalsIgnoreCase("RIGHT"))
            return Text.literal("Right (Subtiers) is now displaying ").setStyle(Style.EMPTY.withColor(Colors.WHITE)).append(cycleSubtiersMode());

        return null;
    }

    public static Text cycleLeftMode() {
        if (autoKitDetect) {
            autoKitDetect = false;
            sendMessageToPlayer(Icons.colorText("Auto kit detect has been disabled due to manual gamemode changes", "red"), false);
        }

        if (positionMCTiers.toString().equalsIgnoreCase("LEFT"))
            return Text.literal("Left (MCTiers) is now displaying ").setStyle(Style.EMPTY.withColor(Colors.WHITE)).append(cycleMCTiersMode());

        if (positionPvPTiers.toString().equalsIgnoreCase("LEFT"))
            return Text.literal("Left (PvPTiers) is now displaying ").setStyle(Style.EMPTY.withColor(Colors.WHITE)).append(cyclePvPTiersMode());

        if (positionSubtiers.toString().equalsIgnoreCase("LEFT"))
            return Text.literal("Left (Subtiers) is now displaying ").setStyle(Style.EMPTY.withColor(Colors.WHITE)).append(cycleSubtiersMode());

        return null;
    }

    public static Text getRightIcon() {
        if (positionMCTiers.toString().equalsIgnoreCase("RIGHT"))
            return activeMCTiersMode.getIcon();

        if (positionPvPTiers.toString().equalsIgnoreCase("RIGHT"))
            return activePvPTiersMode.getIcon();

        if (positionSubtiers.toString().equalsIgnoreCase("RIGHT"))
            return activeSubtiersMode.getIcon();

        return Text.empty();
    }

    public static Text getLeftIcon() {
        if (positionMCTiers.toString().equalsIgnoreCase("LEFT"))
            return activeMCTiersMode.getIcon();

        if (positionPvPTiers.toString().equalsIgnoreCase("LEFT"))
            return activePvPTiersMode.getIcon();

        if (positionSubtiers.toString().equalsIgnoreCase("LEFT"))
            return activeSubtiersMode.getIcon();

        return Text.empty();
    }

    public static void sendMessageToPlayer(Text message, boolean overlay) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.player != null)
            minecraftClient.player.sendMessage(message, overlay);
    }

    public static void toggleMod(CommandContext<FabricClientCommandSource> ignoredFabricClientCommandSourceCommandContext) {
        toggleMod = !toggleMod;
        ConfigManager.saveConfig();
        sendMessageToPlayer(Icons.colorText("Tiers is now " + (toggleMod ? "enabled" : "disabled"), toggleMod ? "green" : "red"), true);
    }

    public static void toggleMod() {
        toggleMod = !toggleMod;
        ConfigManager.saveConfig();
    }

    public static void toggleTab() {
        toggleTab = !toggleTab;
        ConfigManager.saveConfig();
    }

    public static void toggleAutoKitDetect() {
        autoKitDetect = !autoKitDetect;
        ConfigManager.saveConfig();
    }

    public static void searchPlayer(String playerName) {
        if (playerName.equalsIgnoreCase("toggle"))
            toggleMod(null);
        else if (playerName.equalsIgnoreCase("config"))
            CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS).execute(() -> MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(ConfigScreen.getConfigScreen(null))));        else {
            PlayerProfile playerProfile = addGetPlayer(playerName, true);
            if (playerProfile.isPlayerValid())
                CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS).execute(() -> MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new PlayerSearchResultScreen(playerProfile))));        }
    }

    public static void changeIcons(Icons.Type iconType, boolean reload) {
        Icons.identifierMCTiers = Identifier.of("minecraft", "gamemodes/" + iconType.name().toLowerCase());
        Icons.identifierPvPTiers = Identifier.of("minecraft", "gamemodes/" + iconType.name().toLowerCase());
        Icons.identifierMCTiersTags = Identifier.of("minecraft", "gamemodes/" + iconType.name().toLowerCase() + "-tags");
        Icons.identifierPvPTiersTags = Identifier.of("minecraft", "gamemodes/" + iconType.name().toLowerCase() + "-tags");
        ColorLoader.identifier = Identifier.of("minecraft", "colors/" + iconType.name().toLowerCase() + ".json");

        if (reload)
            MinecraftClient.getInstance().reloadResources();

        activeIcons = iconType;
        ConfigManager.saveConfig();
    }

    public static void updatePlayerProfile(PlayerProfile playerProfile) {
        playerProfiles.remove(playerProfile);
        PlayerProfileQueue.removeFromQueue(playerProfile);

        searchPlayer(playerProfile.nameChanged ? playerProfile.originalName : playerProfile.name);
    }

    public static void clearCache(boolean start) {
        playerProfiles.clear();
        PlayerProfileQueue.clearQueue();

        try {
            FileUtils.deleteDirectory(new File(FabricLoader.getInstance().getGameDir() + (start ? "/cache/tiers" : "/cache/tiers/players")));
        } catch (IOException ignored) {
            LOGGER.warn("Error deleting cache folder");
        }

        if (toggleMod && MinecraftClient.getInstance().world != null)
            for (PlayerEntity playerEntity : MinecraftClient.getInstance().world.getPlayers())
                TiersClient.addGetPlayer(playerEntity.getNameForScoreboard(), false);
    }

    public static void updateTextDisplayEntities() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.world == null)
            return;

        for (Entity entity : minecraftClient.world.getEntities()) {
            if (entity instanceof DisplayEntity.TextDisplayEntity) {
                TextDisplayEntityInvokerClientMixin inv = (TextDisplayEntityInvokerClientMixin) entity;
                Text text = inv.invokeGetText();
                inv.invokeSetText(Text.literal(" ").append(text));
                inv.invokeSetText(text);
            }
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

    public static Text cycleMCTiersMode() {
        activeMCTiersMode = cycleEnum(activeMCTiersMode, Mode.getMCTiersValues());
        updateAllTags();
        ConfigManager.saveConfig();
        return activeMCTiersMode.getTextLabel();
    }

    public static Text cyclePvPTiersMode() {
        activePvPTiersMode = cycleEnum(activePvPTiersMode, Mode.getPvPTiersValues());
        updateAllTags();
        ConfigManager.saveConfig();
        return activePvPTiersMode.getTextLabel();
    }

    public static Text cycleSubtiersMode() {
        activeSubtiersMode = cycleEnum(activeSubtiersMode, Mode.getSubtiersValues());
        updateAllTags();
        ConfigManager.saveConfig();
        return activeSubtiersMode.getTextLabel();
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
        OFF
    }
}
