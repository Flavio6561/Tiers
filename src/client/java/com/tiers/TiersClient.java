package com.tiers;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.context.CommandContext;
import com.tiers.misc.*;
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
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.ClickEvent;
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
import java.net.URI;
import java.util.*;

public class TiersClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(TiersClient.class);
    public static String userAgent = "Tiers (modrinth.com/mod/tiers)";
    public static final ArrayList<PlayerProfile> playerProfiles = new ArrayList<>();

    public static boolean toggleMod = true;
    public static boolean toggleIcons = true;
    public static boolean toggleTab = true;
    public static boolean toggleChat = true;
    public static boolean toggleAdaptiveSeparator = true;
    public static boolean toggleAutoKitDetect = false;
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

    @Override
    public void onInitializeClient() {
        ConfigManager.loadConfig();
        changeIcons(activeIcons, false);
        clearCache(true);
        CommandRegister.registerCommands();

        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer("tiers");

        modContainer.ifPresent(tiers -> {
            ResourceLoader.registerBuiltinPack(Identifier.of("resourcepacks", "tiers-resources"), tiers, Text.of("Resources for Tiers"), PackActivationType.ALWAYS_ENABLED);
            userAgent += " v" + tiers.getMetadata().getVersion().getFriendlyString();
        });

        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("tiers"));
        autoDetectKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Auto Detect Kit", GLFW.GLFW_KEY_Y, category));
        openClosestPlayerProfile = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open Closest Player Profile", GLFW.GLFW_KEY_H, category));
        cycleRightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Cycle Right Gamemodes", GLFW.GLFW_KEY_I, category));
        cycleLeftKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Cycle Left Gamemodes", GLFW.GLFW_KEY_U, category));

        ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(Identifier.of("tiers"), new ColorLoader());
        ClientTickEvents.END_CLIENT_TICK.register(TiersClient::checkKeys);
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            if (toggleAutoKitDetect)
                InventoryChecker.checkInventory(minecraftClient, false);
        });

        LOGGER.info("Tiers initialized | User agent: {}", userAgent);
    }

    public static PlayerProfile addGetPlayer(String playerName, boolean priority) {
        for (PlayerProfile playerProfile : playerProfiles) {
            if (playerProfile.name.equalsIgnoreCase(playerName) || playerProfile.inGameName.equalsIgnoreCase(playerName)) {
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
        if (self == null || self.getEntityWorld() == null)
            return null;

        PlayerEntity playerEntity = self.getEntityWorld().getPlayers().stream()
                .filter(player -> player != self)
                .filter(player -> self.distanceTo(player) < MinecraftClient.getInstance().gameRenderer.getViewDistanceBlocks())
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
                tiersCommand(nearestPlayerName);
            else
                sendMessageToPlayer(Icons.colorText("No players in render distance", "red"), true);
        }

        if (cycleRightKey.wasPressed()) {
            Text message = cycleRightMode();

            sendMessageToPlayer(message != null ? message : Icons.colorText("There's nothing on the right display", "red"), true);
        }

        if (cycleLeftKey.wasPressed()) {
            Text message = cycleLeftMode();

            sendMessageToPlayer(message != null ? message : Icons.colorText("There's nothing on the left display", "red"), true);
        }
    }

    public static Text cycleRightMode() {
        if (toggleAutoKitDetect) {
            toggleAutoKitDetect = false;
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
        if (toggleAutoKitDetect) {
            toggleAutoKitDetect = false;
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

    public static void toggleIcons() {
        toggleIcons = !toggleIcons;
        ConfigManager.saveConfig();
    }

    public static void toggleTab() {
        toggleTab = !toggleTab;
        ConfigManager.saveConfig();
    }

    public static void toggleChat() {
        toggleChat = !toggleChat;
        ConfigManager.saveConfig();
    }

    public static void toggleAdaptiveSeparator() {
        toggleAdaptiveSeparator = !toggleAdaptiveSeparator;
        ConfigManager.saveConfig();
    }

    public static void toggleAutoKitDetect() {
        toggleAutoKitDetect = !toggleAutoKitDetect;
        ConfigManager.saveConfig();
    }

    public static void tiersCommand(String playerName) {
        if (playerName.equalsIgnoreCase("-toggle"))
            toggleMod(null);
        else if (playerName.equalsIgnoreCase("-config"))
            setScreen(ConfigScreen.getConfigScreen(null));
        else if (playerName.equalsIgnoreCase("-help")) {
            sendMessageToPlayer(Icons.colorText("--- Tiers help ---", Colors.YELLOW), false);
            sendMessageToPlayer(Text.literal("- General contact: ").append(Text.literal("flavio6561 on Discord").styled(style -> style.withUnderline(true).withClickEvent(new ClickEvent.OpenUrl(URI.create("https://discordapp.com/users/715189608085716992"))))), false);
            sendMessageToPlayer(Text.literal("- Report a bug: ").append(Text.literal("Tiers GitHub issues").styled(style -> style.withUnderline(true).withClickEvent(new ClickEvent.OpenUrl(URI.create("https://github.com/Flavio6561/Tiers/issues"))))), false);
            sendMessageToPlayer(Text.literal("- It's not advisable to create tickets in PvPTiers support"), false);
            sendMessageToPlayer(Text.literal("- ").append(Text.literal("Changelogs").styled(style -> style.withUnderline(true).withClickEvent(new ClickEvent.OpenUrl(URI.create("https://github.com/Flavio6561/Tiers/wiki/Version-changelogs"))))), false);
            sendMessageToPlayer(Text.literal("- ").append(Text.literal("Modrinth page").styled(style -> style.withUnderline(true).withClickEvent(new ClickEvent.OpenUrl(URI.create("https://modrinth.com/mod/tiers"))))), false);

            String[] debugInfo = getDebugInfo();
            sendMessageToPlayer(Icons.colorText("\n" + debugInfo[0], Colors.LIGHT_YELLOW), false);
            MinecraftClient.getInstance().keyboard.setClipboard(debugInfo[1]);
            sendMessageToPlayer(Icons.colorText("A complete debug log has been copied to the clipboard", "green"), false);
        } else if (playerName.equalsIgnoreCase("-clear")) {
            clearCache(false);
            sendMessageToPlayer(Icons.colorText("Cleared player cache", "green"), true);
        } else if (playerName.startsWith("-")) {
            sendMessageToPlayer(Icons.colorText("Not a valid command. Here's a list of valid commands:", "red"), false);
            sendMessageToPlayer(Icons.colorText("/tiers -toggle", Colors.YELLOW), false);
            sendMessageToPlayer(Icons.colorText("/tiers -config", Colors.YELLOW), false);
            sendMessageToPlayer(Icons.colorText("/tiers -help", Colors.YELLOW), false);
            sendMessageToPlayer(Icons.colorText("/tiers -clear", Colors.YELLOW), false);
        } else {
            PlayerProfile playerProfile = addGetPlayer(playerName, true);
            if (playerProfile.isPlayerValid())
                setScreen(new PlayerSearchResultScreen(playerProfile));
        }
    }

    public static void setScreen(Screen screen) {
        MinecraftClient.getInstance().executeAsync(ignored -> MinecraftClient.getInstance().setScreen(screen));
    }

    public static String[] getDebugInfo() {
        String[] debugInfo = new String[2];

        final String[] version = new String[1];
        FabricLoader.getInstance().getModContainer("tiers").ifPresent(tiers -> version[0] = "Tiers version: " + tiers.getMetadata().getVersion().getFriendlyString());
        debugInfo[0] = version[0] + "\n";
        debugInfo[1] = debugInfo[0];
        debugInfo[1] += "Launcher brand: " + MinecraftClient.getLauncherBrand() + "\n";
        debugInfo[1] += "Game version: " + MinecraftClient.getInstance().getGameVersion() + " | " + FabricLoader.getInstance().getRawGameVersion() + "\n";
        debugInfo[1] += "Version type: " + MinecraftClient.getInstance().getVersionType() + "\n";
        debugInfo[1] += "Instance name: " + MinecraftClient.getInstance().getName() + "\n";
        debugInfo[1] += "Game profile name: " + MinecraftClient.getInstance().getGameProfile().name() + "\n";
        debugInfo[1] += "OS info:\n\t" + System.getProperty("os.name") + "\n\t" + System.getProperty("os.version") + "\n\t" + System.getProperty("os.arch") + "\n";
        debugInfo[1] += "CPU info: " + GLX._getCpuInfo() + "\n";
        Runtime runtime = Runtime.getRuntime();
        debugInfo[1] += "RAM info (MB):\n\tMax: " + runtime.maxMemory() / (1024 * 1024) + "\n\tTotal: " + runtime.totalMemory() / (1024 * 1024) + "\n\tFree: " + runtime.freeMemory() / (1024 * 1024) + "\n\tIn use: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "\n";
        GpuDevice gpuDevice = RenderSystem.getDevice();
        debugInfo[1] += "GPU info:\n\t" + gpuDevice.getBackendName() + "\n\t" + gpuDevice.getImplementationInformation() + "\n\t" + gpuDevice.getRenderer() + "\n\t" + gpuDevice.getVersion() + "\n";
        debugInfo[1] += "Java version: " + System.getProperty("java.version") + "\n";
        debugInfo[1] += "Launch args: " + Arrays.toString(FabricLoader.getInstance().getLaunchArguments(false)) + "\n";
        debugInfo[1] += "All Fabric mods: " + FabricLoader.getInstance().getAllMods() + "\n";
        debugInfo[1] += "Resource packs: " + ResourcePackManager.listPacks(MinecraftClient.getInstance().getResourcePackManager().getEnabledProfiles()) + "\n";

        return debugInfo;
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

        tiersCommand(playerProfile.nameChanged ? playerProfile.inGameName : playerProfile.name);
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
                addGetPlayer(playerEntity.getNameForScoreboard(), false);
    }

    public static void updateTextDisplayEntities() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.world == null)
            return;

        for (Entity entity : minecraftClient.world.getEntities()) {
            if (entity instanceof DisplayEntity.TextDisplayEntity textDisplay) {
                Text text = textDisplay.getText();
                textDisplay.setText(Text.literal(" ").append(text));
                textDisplay.setText(text);
            }
        }
    }

    public static Text cycleMCTiersMode() {
        activeMCTiersMode = cycleEnum(activeMCTiersMode, Mode.getMCTiersValues());
        ConfigManager.saveConfig();
        return activeMCTiersMode.getTextLabel();
    }

    public static Text cyclePvPTiersMode() {
        activePvPTiersMode = cycleEnum(activePvPTiersMode, Mode.getPvPTiersValues());
        ConfigManager.saveConfig();
        return activePvPTiersMode.getTextLabel();
    }

    public static Text cycleSubtiersMode() {
        activeSubtiersMode = cycleEnum(activeSubtiersMode, Mode.getSubtiersValues());
        ConfigManager.saveConfig();
        return activeSubtiersMode.getTextLabel();
    }

    public static void cycleDisplayMode() {
        displayMode = cycleEnum(displayMode, ModesTierDisplay.values());
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
            if (toString().equalsIgnoreCase("HIGHEST"))
                return "Displayed Tiers: Highest";
            else if (toString().equalsIgnoreCase("SELECTED"))
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
