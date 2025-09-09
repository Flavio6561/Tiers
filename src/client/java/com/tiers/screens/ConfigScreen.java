package com.tiers.screens;

import com.tiers.TiersClient;
import com.tiers.misc.ConfigManager;
import com.tiers.profile.PlayerProfile;
import com.tiers.profile.Status;
import com.tiers.profile.types.MCTiersProfile;
import com.tiers.profile.types.PvPTiersProfile;
import com.tiers.profile.types.SubtiersProfile;
import com.tiers.textures.ColorControl;
import com.tiers.textures.Icons;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.io.FileInputStream;
import java.io.IOException;

import static com.tiers.TiersClient.LOGGER;

public class ConfigScreen extends Screen {
    public static PlayerProfile ownProfile;
    public static PlayerProfile defaultProfile;

    private boolean useOwnProfile;

    private Identifier playerAvatarTexture;
    private boolean imageReady;

    private ButtonWidget toggleMod;
    private ButtonWidget toggleShowIcons;
    private ButtonWidget toggleSeparatorMode;
    private ButtonWidget cycleDisplayMode;
    private ButtonWidget clearPlayerCache;
    private ButtonWidget enableOwnProfile;
    private ButtonWidget leftMCTiers;
    private ButtonWidget centerMCTiers;
    private ButtonWidget rightMCTiers;
    private ButtonWidget leftPvPTiers;
    private ButtonWidget centerPvPTiers;
    private ButtonWidget rightPvPTiers;
    private ButtonWidget leftSubtiers;
    private ButtonWidget centerSubtiers;
    private ButtonWidget rightSubtiers;
    private ButtonWidget activeRightMode;
    private ButtonWidget activeLeftMode;

    private int centerX;
    private int distance;

    private ConfigScreen() {
        super(Text.of("Tiers config"));
        loadPlayerAvatar();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        centerX = width / 2;
        distance = height / 14;

        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, Text.of("Tiers config"), centerX, height / 50, Colors.WHITE);

        drawIconShowcase(context);

        if (!useOwnProfile)
            context.drawTexture(RenderPipelines.GUI_TEXTURED, playerAvatarTexture, centerX - height / 10 / 2, height - (int) (height / 4.166) - height / 54, 0, 0, height / 10, (int) (height / 4.166), height / 10, (int) (height / 4.166));
        else
            drawPlayerAvatar(context, centerX, height - (int) (height / 4.166) - height / 54);

        context.drawCenteredTextWithShadow(this.textRenderer, useOwnProfile ? ownProfile.getFullNametag() : defaultProfile.getFullNametag() , centerX, height - (int) (height / 4.166) - height / 54 - 12, Colors.WHITE);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, MCTiersProfile.MCTIERS_IMAGE, centerX - 120 - 64, distance + 110 + 4, 0, 0, 128, 24, 128, 24);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, PvPTiersProfile.PVPTIERS_IMAGE, centerX - 12, distance + 110 + 4, 0, 0, 24, 24, 24, 24);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, SubtiersProfile.SUBTIERS_IMAGE, centerX + 120 - 15, distance + 110, 0, 0, 30, 30, 30, 30);

        context.drawTextWithShadow(this.textRenderer, TiersClient.getRightIcon(), centerX + 90 + 32, distance + 75 + 8, Colors.WHITE);
        context.drawTextWithShadow(this.textRenderer, TiersClient.getLeftIcon(), centerX - 90 - 32 - 12, distance + 75 + 8, Colors.WHITE);

        checkUpdates();
    }

    private void drawIconShowcase(DrawContext context) {
        for (int i = 0; i < 8; i++) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(String.valueOf((char) (0xF000 + i))).setStyle(Style.EMPTY.withFont(Identifier.of("minecraft", "gamemodes/classic-medium"))), 34 + 14 * i, 13, Colors.WHITE);
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(String.valueOf((char) (0xF000 + i))).setStyle(Style.EMPTY.withFont(Identifier.of("minecraft", "gamemodes/pvptiers-medium"))), 34 + 14 * i, 38, Colors.WHITE);
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(String.valueOf((char) (0xF000 + i))).setStyle(Style.EMPTY.withFont(Identifier.of("minecraft", "gamemodes/mctiers-medium"))), 34 + 14 * i, 63, Colors.WHITE);
        }
    }

    private void checkUpdates() {
        toggleMod.setPosition(width / 2 - 88 - 2, distance);
        toggleShowIcons.setPosition(width / 2 + 2, distance);
        toggleSeparatorMode.setPosition(width / 2 - 90, distance + 25);
        cycleDisplayMode.setPosition(width / 2 - 90, distance + 50);
        enableOwnProfile.setPosition(width / 2 - 90, distance + 75);
        clearPlayerCache.setPosition(width - 88 - 5, height - 20 - 5);
        leftMCTiers.setPosition(centerX - 120 - 10 - 24, distance + 145);
        centerMCTiers.setPosition(centerX - 120 - 10, distance + 145);
        rightMCTiers.setPosition(centerX - 120 - 10 + 24, distance + 145);
        leftPvPTiers.setPosition(centerX - 10 - 24, distance + 145);
        centerPvPTiers.setPosition(centerX - 10, distance + 145);
        rightPvPTiers.setPosition(centerX - 10 + 24, distance + 145);
        leftSubtiers.setPosition(centerX + 120 - 10 - 24, distance + 145);
        centerSubtiers.setPosition(centerX + 120 - 10, distance + 145);
        rightSubtiers.setPosition(centerX + 120 - 10 + 24, distance + 145);
        activeRightMode.setPosition(centerX + 90 + 4, distance + 75);
        activeLeftMode.setPosition(centerX - 90 - 20 - 4, distance + 75);

        activeRightMode.visible = TiersClient.positionMCTiers == TiersClient.DisplayStatus.RIGHT || TiersClient.positionPvPTiers == TiersClient.DisplayStatus.RIGHT || TiersClient.positionSubtiers == TiersClient.DisplayStatus.RIGHT;
        activeLeftMode.visible = TiersClient.positionMCTiers == TiersClient.DisplayStatus.LEFT || TiersClient.positionPvPTiers == TiersClient.DisplayStatus.LEFT || TiersClient.positionSubtiers == TiersClient.DisplayStatus.LEFT;
    }

    @Override
    protected void init() {
        toggleMod = ButtonWidget.builder(Text.of(TiersClient.toggleMod ? "Disable Tiers" : "Enable Tiers"), (buttonWidget) -> {
            TiersClient.toggleMod();
            buttonWidget.setMessage(Text.of(TiersClient.toggleMod ? "Disable Tiers" : "Enable Tiers"));
            buttonWidget.setTooltip(Tooltip.of(Text.of(TiersClient.toggleMod ? "Disable Tiers" : "Enable Tiers")));
        }).dimensions(width / 2 - 88 - 2, distance, 88, 20).tooltip(Tooltip.of(Text.of(TiersClient.toggleMod ? "Disable the mod" : "Enable the mod"))).build();

        toggleShowIcons = ButtonWidget.builder(Text.of(TiersClient.showIcons ? "Disable Icons" : "Enable Icons"), (buttonWidget) -> {
            TiersClient.toggleShowIcons();
            buttonWidget.setMessage(Text.of(TiersClient.showIcons ? "Disable Icons" : "Enable Icons"));
            buttonWidget.setTooltip(Tooltip.of(Text.of(TiersClient.showIcons ? "Disable the gamemode icon next to the tier" : "Enable the gamemode icon next to the tier")));
        }).dimensions(width / 2 + 2, distance, 88, 20).tooltip(Tooltip.of(Text.of(TiersClient.showIcons ? "Disable the gamemode icon next to the tier" : "Enable the gamemode icon next to the tier"))).build();

        toggleSeparatorMode = ButtonWidget.builder(Text.of(TiersClient.isSeparatorAdaptive ? "Disable Dynamic Separator" : "Enable Dynamic Separator"), (buttonWidget) -> {
            TiersClient.toggleSeparatorAdaptive();
            buttonWidget.setMessage(Text.of(TiersClient.isSeparatorAdaptive ? "Disable Dynamic Separator" : "Enable Dynamic Separator"));
            buttonWidget.setTooltip(Tooltip.of(Text.of(TiersClient.isSeparatorAdaptive ? "Make the Tiers separator gray" : "Make the Tiers separator match the tier color")));
        }).dimensions(width / 2 - 90, distance + 25, 180, 20).tooltip(Tooltip.of(Text.of(TiersClient.isSeparatorAdaptive ? "Make the Tiers separator gray" : "Make the Tiers separator match the tier color"))).build();

        cycleDisplayMode = ButtonWidget.builder(Text.of(TiersClient.displayMode.getCurrentMode()), (buttonWidget) -> {
            TiersClient.cycleDisplayMode();
            buttonWidget.setMessage(Text.of(TiersClient.displayMode.getCurrentMode()));
        }).dimensions(width / 2 - 90, distance + 50, 180, 20).tooltip(Tooltip.of(Text.of(("""
                Selected: only the selected tier will be displayed
                
                Highest: only the highest tier will be displayed
                
                Adaptive Highest: the highest tier will be displayed if selected does not exist""")))).build();

        if (ownProfile.status == Status.READY) {
            enableOwnProfile = ButtonWidget.builder(Text.of(useOwnProfile ? "Preview default" : "Preview " + ownProfile.name), (buttonWidget) -> {
                useOwnProfile = !useOwnProfile;

                imageReady = false;
                playerAvatarTexture = null;
                loadPlayerAvatar();

                buttonWidget.setMessage(Text.of(useOwnProfile ? "Preview default" : "Preview " + ownProfile.name));
                buttonWidget.setTooltip(Tooltip.of(Text.of(useOwnProfile ? "Preview the default profile (" + defaultProfile.name + ")" : "Preview your player profile (" + ownProfile.name + ")")));
            }).dimensions(width / 2 - 90, distance + 75, 180, 20).tooltip(Tooltip.of(Text.of(useOwnProfile ? "Preview the default profile (" + defaultProfile.name + ")" : "Preview your player profile (" + ownProfile.name + ")"))).build();
        } else
            enableOwnProfile = ButtonWidget.builder(Text.of("Cannot switch profiles"), (buttonWidget) -> {}).dimensions(width / 2 - 90, distance + 75, 180, 20).tooltip(Tooltip.of(Text.of("Can't switch profiles: " + ownProfile.name + " is not found or fetched yet. Restart game to retry"))).build();

        clearPlayerCache = ButtonWidget.builder(Text.of("Clear cache"), (buttonWidget) -> TiersClient.clearCache(false)).dimensions(width - 88 - 5, height - 20 - 5, 88, 20).tooltip(Tooltip.of(Text.of("Clear all player cache"))).build();

        leftMCTiers = ButtonWidget.builder(Text.of("←"), (buttonWidget) -> {
            TiersClient.positionMCTiers = TiersClient.DisplayStatus.LEFT;
            if (TiersClient.positionPvPTiers == TiersClient.DisplayStatus.LEFT) {
                TiersClient.positionPvPTiers = TiersClient.DisplayStatus.OFF;
                leftPvPTiers.active = true;
                centerPvPTiers.active = false;
            }
            if (TiersClient.positionSubtiers == TiersClient.DisplayStatus.LEFT) {
                TiersClient.positionSubtiers = TiersClient.DisplayStatus.OFF;
                leftSubtiers.active = true;
                centerSubtiers.active = false;
            }
            buttonWidget.active = false;
            centerMCTiers.active = true;
            rightMCTiers.active = true;
            ConfigManager.saveConfig();
            TiersClient.updateAllTags();
        }).dimensions(centerX - 120 - 10 - 24, distance + 145, 20, 20).tooltip(Tooltip.of(Text.of("Display MCTiers on the left"))).build();

        centerMCTiers = ButtonWidget.builder(Text.of("●"), (buttonWidget) -> {
            TiersClient.positionMCTiers = TiersClient.DisplayStatus.OFF;
            leftMCTiers.active = true;
            buttonWidget.active = false;
            rightMCTiers.active = true;
            ConfigManager.saveConfig();
            TiersClient.updateAllTags();
        }).dimensions(centerX - 120 - 10, distance + 145, 20, 20).tooltip(Tooltip.of(Text.of("Disable MCTiers"))).build();

        rightMCTiers = ButtonWidget.builder(Text.of("→"), (buttonWidget) -> {
            TiersClient.positionMCTiers = TiersClient.DisplayStatus.RIGHT;
            if (TiersClient.positionPvPTiers == TiersClient.DisplayStatus.RIGHT) {
                TiersClient.positionPvPTiers = TiersClient.DisplayStatus.OFF;
                centerPvPTiers.active = false;
                rightPvPTiers.active = true;
            }
            if (TiersClient.positionSubtiers == TiersClient.DisplayStatus.RIGHT) {
                TiersClient.positionSubtiers = TiersClient.DisplayStatus.OFF;
                centerSubtiers.active = false;
                rightSubtiers.active = true;
            }
            leftMCTiers.active = true;
            centerMCTiers.active = true;
            buttonWidget.active = false;
            ConfigManager.saveConfig();
            TiersClient.updateAllTags();
        }).dimensions(centerX - 120 - 10 + 24, distance + 145, 20, 20).tooltip(Tooltip.of(Text.of("Display MCTiers on the right"))).build();

        leftPvPTiers = ButtonWidget.builder(Text.of("←"), (buttonWidget) -> {
            TiersClient.positionPvPTiers = TiersClient.DisplayStatus.LEFT;
            if (TiersClient.positionMCTiers == TiersClient.DisplayStatus.LEFT) {
                TiersClient.positionMCTiers = TiersClient.DisplayStatus.OFF;
                leftMCTiers.active = true;
                centerMCTiers.active = false;
            }
            if (TiersClient.positionSubtiers == TiersClient.DisplayStatus.LEFT) {
                TiersClient.positionSubtiers = TiersClient.DisplayStatus.OFF;
                leftSubtiers.active = true;
                centerSubtiers.active = false;
            }
            buttonWidget.active = false;
            centerPvPTiers.active = true;
            rightPvPTiers.active = true;
            ConfigManager.saveConfig();
            TiersClient.updateAllTags();
        }).dimensions(centerX - 10 - 24, distance + 145, 20, 20).tooltip(Tooltip.of(Text.of("Display PvPTiers on the left"))).build();

        centerPvPTiers = ButtonWidget.builder(Text.of("●"), (buttonWidget) -> {
            TiersClient.positionPvPTiers = TiersClient.DisplayStatus.OFF;
            leftPvPTiers.active = true;
            buttonWidget.active = false;
            rightPvPTiers.active = true;
            ConfigManager.saveConfig();
            TiersClient.updateAllTags();
        }).dimensions(centerX - 10, distance + 145, 20, 20).tooltip(Tooltip.of(Text.of("Disable PvPTiers"))).build();

        rightPvPTiers = ButtonWidget.builder(Text.of("→"), (buttonWidget) -> {
            TiersClient.positionPvPTiers = TiersClient.DisplayStatus.RIGHT;
            if (TiersClient.positionMCTiers == TiersClient.DisplayStatus.RIGHT) {
                TiersClient.positionMCTiers = TiersClient.DisplayStatus.OFF;
                centerMCTiers.active = false;
                rightMCTiers.active = true;
            }
            if (TiersClient.positionSubtiers == TiersClient.DisplayStatus.RIGHT) {
                TiersClient.positionSubtiers = TiersClient.DisplayStatus.OFF;
                centerSubtiers.active = false;
                rightSubtiers.active = true;
            }
            leftPvPTiers.active = true;
            centerPvPTiers.active = true;
            buttonWidget.active = false;
            ConfigManager.saveConfig();
            TiersClient.updateAllTags();
        }).dimensions(centerX - 10 + 24, distance + 145, 20, 20).tooltip(Tooltip.of(Text.of("Display PvPTiers on the right"))).build();

        leftSubtiers = ButtonWidget.builder(Text.of("←"), (buttonWidget) -> {
            TiersClient.positionSubtiers = TiersClient.DisplayStatus.LEFT;
            if (TiersClient.positionMCTiers == TiersClient.DisplayStatus.LEFT) {
                TiersClient.positionMCTiers = TiersClient.DisplayStatus.OFF;
                leftMCTiers.active = true;
                centerMCTiers.active = false;
            }
            if (TiersClient.positionPvPTiers == TiersClient.DisplayStatus.LEFT) {
                TiersClient.positionPvPTiers = TiersClient.DisplayStatus.OFF;
                leftPvPTiers.active = true;
                centerPvPTiers.active = false;
            }
            buttonWidget.active = false;
            centerSubtiers.active = true;
            rightSubtiers.active = true;
            ConfigManager.saveConfig();
            TiersClient.updateAllTags();
        }).dimensions(centerX + 120 - 10 - 24, distance + 145, 20, 20).tooltip(Tooltip.of(Text.of("Display Subtiers on the left"))).build();

        centerSubtiers = ButtonWidget.builder(Text.of("●"), (buttonWidget) -> {
            TiersClient.positionSubtiers = TiersClient.DisplayStatus.OFF;
            leftSubtiers.active = true;
            buttonWidget.active = false;
            rightSubtiers.active = true;
            ConfigManager.saveConfig();
            TiersClient.updateAllTags();
        }).dimensions(centerX + 120 - 10, distance + 145, 20, 20).tooltip(Tooltip.of(Text.of("Disable Subtiers"))).build();

        rightSubtiers = ButtonWidget.builder(Text.of("→"), (buttonWidget) -> {
            TiersClient.positionSubtiers = TiersClient.DisplayStatus.RIGHT;
            if (TiersClient.positionMCTiers == TiersClient.DisplayStatus.RIGHT) {
                TiersClient.positionMCTiers = TiersClient.DisplayStatus.OFF;
                centerMCTiers.active = false;
                rightMCTiers.active = true;
            }
            if (TiersClient.positionPvPTiers == TiersClient.DisplayStatus.RIGHT) {
                TiersClient.positionPvPTiers = TiersClient.DisplayStatus.OFF;
                centerPvPTiers.active = false;
                rightPvPTiers.active = true;
            }
            leftSubtiers.active = true;
            centerSubtiers.active = true;
            buttonWidget.active = false;
            ConfigManager.saveConfig();
            TiersClient.updateAllTags();
        }).dimensions(centerX + 120 - 10 + 24, distance + 145, 20, 20).tooltip(Tooltip.of(Text.of("Display Subtiers on the right"))).build();

        switch (TiersClient.positionMCTiers) {
            case RIGHT -> rightMCTiers.active = false;
            case OFF -> centerMCTiers.active = false;
            case LEFT -> leftMCTiers.active = false;
        }

        switch (TiersClient.positionPvPTiers) {
            case RIGHT -> rightPvPTiers.active = false;
            case OFF -> centerPvPTiers.active = false;
            case LEFT -> leftPvPTiers.active = false;
        }

        switch (TiersClient.positionSubtiers) {
            case RIGHT -> rightSubtiers.active = false;
            case OFF -> centerSubtiers.active = false;
            case LEFT -> leftSubtiers.active = false;
        }

        activeRightMode = ButtonWidget.builder(Icons.CYCLE, (buttonWidget) -> TiersClient.cycleRightMode()).dimensions(centerX + 90 + 4, distance + 75, 20, 20).tooltip(Tooltip.of(Text.of("Cycle active right gamemode"))).build();

        activeLeftMode = ButtonWidget.builder(Icons.CYCLE, (buttonWidget) -> TiersClient.cycleLeftMode()).dimensions(centerX - 90 - 20 - 4, distance + 75, 20, 20).tooltip(Tooltip.of(Text.of("Cycle active left gamemode"))).build();

        ButtonWidget useClassicIcons = ButtonWidget.builder(TiersClient.activeIcons == Icons.Type.CLASSIC ? Text.of("●") : Text.empty(), (buttonWidget) -> {
            buttonWidget.setMessage(TiersClient.activeIcons == Icons.Type.CLASSIC ? Text.of("●") : Text.empty());
            TiersClient.changeIcons(Icons.Type.CLASSIC, true);
        }).dimensions(5, 5, 20, 20).tooltip(Tooltip.of(Text.of("Use classic styled icons and colors"))).build();

        ButtonWidget usePvPTiersIcons = ButtonWidget.builder(TiersClient.activeIcons == Icons.Type.PVPTIERS ? Text.of("●") : Text.empty(), (buttonWidget) -> {
            buttonWidget.setMessage(TiersClient.activeIcons == Icons.Type.PVPTIERS ? Text.of("●") : Text.empty());
            TiersClient.changeIcons(Icons.Type.PVPTIERS, true);
        }).dimensions(5, 30, 20, 20).tooltip(Tooltip.of(Text.of("Use PvPTiers styled icons and colors"))).build();

        ButtonWidget useMCTiersIcons = ButtonWidget.builder(TiersClient.activeIcons == Icons.Type.MCTIERS ? Text.of("●") : Text.empty(), (buttonWidget) -> {
            buttonWidget.setMessage(TiersClient.activeIcons == Icons.Type.MCTIERS ? Text.of("●") : Text.empty());
            TiersClient.changeIcons(Icons.Type.MCTIERS, true);
        }).dimensions(5, 55, 20, 20).tooltip(Tooltip.of(Text.of("Use MCTiers styled icons and colors"))).build();

        switch (TiersClient.activeIcons) {
            case CLASSIC -> useClassicIcons.active = false;
            case PVPTIERS -> usePvPTiersIcons.active = false;
            case MCTIERS -> useMCTiersIcons.active = false;
        }

        activeRightMode.visible = TiersClient.positionMCTiers == TiersClient.DisplayStatus.RIGHT || TiersClient.positionPvPTiers == TiersClient.DisplayStatus.RIGHT || TiersClient.positionSubtiers == TiersClient.DisplayStatus.RIGHT;
        activeLeftMode.visible = TiersClient.positionMCTiers == TiersClient.DisplayStatus.LEFT || TiersClient.positionPvPTiers == TiersClient.DisplayStatus.LEFT || TiersClient.positionSubtiers == TiersClient.DisplayStatus.LEFT;

        this.addDrawableChild(toggleMod);
        this.addDrawableChild(toggleShowIcons);
        this.addDrawableChild(toggleSeparatorMode);
        this.addDrawableChild(cycleDisplayMode);
        this.addDrawableChild(enableOwnProfile);
        this.addDrawableChild(clearPlayerCache);
        this.addDrawableChild(leftMCTiers);
        this.addDrawableChild(centerMCTiers);
        this.addDrawableChild(rightMCTiers);
        this.addDrawableChild(leftPvPTiers);
        this.addDrawableChild(centerPvPTiers);
        this.addDrawableChild(rightPvPTiers);
        this.addDrawableChild(leftSubtiers);
        this.addDrawableChild(centerSubtiers);
        this.addDrawableChild(rightSubtiers);
        this.addDrawableChild(activeRightMode);
        this.addDrawableChild(activeLeftMode);
        this.addDrawableChild(useClassicIcons);
        this.addDrawableChild(usePvPTiersIcons);
        this.addDrawableChild(useMCTiersIcons);
    }

    private void drawPlayerAvatar(DrawContext context, int x, int y) {
        if (imageReady) {
            if (ownProfile.imageSaved == 1 || ownProfile.imageSaved == 2)
                context.drawTexture(RenderPipelines.GUI_TEXTURED, playerAvatarTexture, x - height / 10 / 2, y, 0, 0, height / 10, (int) (height / 4.166), height / 10, (int) (height / 4.166));
            else if (ownProfile.imageSaved < 6 && ownProfile.imageSaved > 2)
                context.drawTexture(RenderPipelines.GUI_TEXTURED, playerAvatarTexture, x - height / 7 / 2, y, 0, 0, height / 7, (int) (height / 4.145), height / 7, (int) (height / 4.145));
        } else if (ownProfile.imageSaved != 0) {
            loadPlayerAvatar();
        } else if (ownProfile.numberOfImageRequests == 6)
            context.drawCenteredTextWithShadow(this.textRenderer, Text.of(ownProfile.name + "'s skin failed to load. Restart game to retry"), x, y + 50, ColorControl.getColorMinecraftStandard("red"));
    }

    private void loadPlayerAvatar() {
        if (playerAvatarTexture != null) return;

        try (FileInputStream fileInputStream = new FileInputStream(FabricLoader.getInstance().getGameDir().resolve("cache/tiers/" + (useOwnProfile ? ownProfile.uuid : defaultProfile.uuid) + ".png").toFile())) {
            playerAvatarTexture = Identifier.of("tiers", (useOwnProfile ? ownProfile.uuid : defaultProfile.uuid));
            MinecraftClient.getInstance().getTextureManager().registerTexture(playerAvatarTexture, new NativeImageBackedTexture(null, NativeImage.read(fileInputStream)));
            imageReady = true;
        } catch (IOException ignored) {
            LOGGER.warn("Error loading player skin");
        }
    }

    public static Screen getConfigScreen(Screen ignoredScreen) {
        return new ConfigScreen();
    }
}