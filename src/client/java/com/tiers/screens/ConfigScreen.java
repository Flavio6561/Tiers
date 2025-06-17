package com.tiers.screens;

import com.tiers.TiersClient;
import com.tiers.profile.PlayerProfile;
import com.tiers.profile.Status;
import com.tiers.textures.ColorControl;
import com.tiers.textures.Icons;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ConfigScreen extends Screen {
    public static PlayerProfile ownProfile;
    public static PlayerProfile defaultProfile;

    private boolean useOwnProfile = false;

    private Identifier playerAvatarTexture;
    private boolean imageReady = false;

    private final Identifier MCTIERS_COM_IMAGE = Identifier.of("minecraft", "textures/mctiers_com_logo.png");
    private final Identifier MCTIERS_IO_IMAGE = Identifier.of("minecraft", "textures/mctiers_io_logo.png");
    private final Identifier SUBTIERS_NET_IMAGE = Identifier.of("minecraft", "textures/subtiers_net_logo.png");

    private ButtonWidget toggleModWidget;
    private ButtonWidget toggleShowIcons;
    private ButtonWidget toggleSeparatorMode;
    private ButtonWidget cycleDisplayMode;
    private ButtonWidget clearPlayerCache;
    private ButtonWidget enableOwnProfile;
    private ButtonWidget mcTiersCOMPosition;
    private ButtonWidget mcTiersIOPosition;
    private ButtonWidget subtiersNETPosition;

    private ButtonWidget activeRightMode;
    private ButtonWidget activeLeftMode;

    private int centerX;
    private int distance;
    private int updateButtons;

    private ConfigScreen() {
        super(Text.literal("Tiers config"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        centerX = width / 2;
        distance = height / 14;

        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Tiers config"), centerX, height / 50, ColorControl.getColor("text"));

        drawPlayerAvatar(context, centerX, height - 10 - (int) (width / 6.666));
        context.drawCenteredTextWithShadow(this.textRenderer, TiersClient.getNametag(useOwnProfile ? ownProfile : defaultProfile), centerX, height - 24 - (int) (width / 6.666), ColorControl.getColor("text"));

        drawCategoryList(context, MCTIERS_COM_IMAGE, centerX - 100, distance + 110);
        drawCategoryList(context, MCTIERS_IO_IMAGE, centerX, distance + 110);
        drawCategoryList(context, SUBTIERS_NET_IMAGE, centerX + 100, distance + 110);

        context.drawTextWithShadow(this.textRenderer, Text.of(TiersClient.getRightIcon()), centerX + 90 + 33, distance + 75 + 8, ColorControl.getColor("text"));
        context.drawTextWithShadow(this.textRenderer, Text.of(TiersClient.getLeftIcon()), centerX - 90 - 33 - 12, distance + 75 + 8, ColorControl.getColor("text"));

        checkUpdates();
    }

    private void drawCategoryList(DrawContext context, Identifier image, int x, int y) {
        if (image == MCTIERS_COM_IMAGE)
            context.drawTexture(RenderLayer::getGuiTextured, image, x - 56, y + 5, 0, 0, 112, 21, 112, 21);
        else
            context.drawTexture(RenderLayer::getGuiTextured, image, x - 13, y, 0, 0, 26, 26, 26, 26);
    }

    private void checkUpdates() {
        toggleModWidget.setPosition(width / 2 - 88 - 2, distance);
        toggleShowIcons.setPosition(width / 2 + 2, distance);
        toggleSeparatorMode.setPosition(width / 2 - 90, distance + 25);
        cycleDisplayMode.setPosition(width / 2 - 90, distance + 50);
        enableOwnProfile.setPosition(width / 2 - 90, distance + 75);
        clearPlayerCache.setPosition(width - 88 - 5, height - 20 - 5);

        mcTiersCOMPosition.setPosition(centerX - 58 - 100 + 29, distance + 145);
        mcTiersIOPosition.setPosition(centerX - 29, distance + 145);
        subtiersNETPosition.setPosition(centerX + 100 - 29, distance + 145);

        activeRightMode.setPosition(centerX + 90 + 5, distance + 75);
        activeLeftMode.setPosition(centerX - 90 - 20 - 5, distance + 75);

        if (updateButtons == 0) {
            if (TiersClient.mcTiersIOPosition == TiersClient.mcTiersCOMPosition) {
                TiersClient.mcTiersIOPosition = TiersClient.DisplayStatus.OFF;
                mcTiersIOPosition.setMessage(Text.of(TiersClient.mcTiersIOPosition.getStatus()));
            }
            if (TiersClient.subtiersNETPosition == TiersClient.mcTiersCOMPosition) {
                TiersClient.subtiersNETPosition = TiersClient.DisplayStatus.OFF;
                subtiersNETPosition.setMessage(Text.of(TiersClient.subtiersNETPosition.getStatus()));
            }
            updateButtons = -1;
        } else if (updateButtons == 1) {
            if (TiersClient.mcTiersCOMPosition == TiersClient.mcTiersIOPosition) {
                TiersClient.mcTiersCOMPosition = TiersClient.DisplayStatus.OFF;
                mcTiersCOMPosition.setMessage(Text.of(TiersClient.mcTiersCOMPosition.getStatus()));
            }
            if (TiersClient.subtiersNETPosition == TiersClient.mcTiersIOPosition) {
                TiersClient.subtiersNETPosition = TiersClient.DisplayStatus.OFF;
                subtiersNETPosition.setMessage(Text.of(TiersClient.subtiersNETPosition.getStatus()));
            }
            updateButtons = -1;
        } else if (updateButtons == 2) {
            if (TiersClient.mcTiersCOMPosition == TiersClient.subtiersNETPosition) {
                TiersClient.mcTiersCOMPosition = TiersClient.DisplayStatus.OFF;
                mcTiersCOMPosition.setMessage(Text.of(TiersClient.mcTiersCOMPosition.getStatus()));
            }
            if (TiersClient.mcTiersIOPosition == TiersClient.subtiersNETPosition) {
                TiersClient.mcTiersIOPosition = TiersClient.DisplayStatus.OFF;
                mcTiersIOPosition.setMessage(Text.of(TiersClient.mcTiersIOPosition.getStatus()));
            }
            updateButtons = -1;
        }
    }

    @Override
    protected void init() {
        toggleModWidget = ButtonWidget.builder(Text.literal(TiersClient.toggleMod ? "Disable Tiers" : "Enable Tiers").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> {
            TiersClient.toggleMod();
            buttonWidget.setMessage(Text.literal(TiersClient.toggleMod ? "Disable Tiers" : "Enable Tiers").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
            toggleModWidget.setTooltip(Tooltip.of(Text.of(TiersClient.toggleMod ? "Disable the mod" : "Enable the mod")));
        }).dimensions(width / 2 - 88 - 2, distance, 88, 20).build();
        toggleModWidget.setTooltip(Tooltip.of(Text.of(TiersClient.toggleMod ? "Disable the mod" : "Enable the mod")));

        toggleShowIcons = ButtonWidget.builder(Text.literal(TiersClient.showIcons ? "Disable Icons" : "Enable Icons").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> {
            TiersClient.toggleShowIcons();
            buttonWidget.setMessage(Text.literal(TiersClient.showIcons ? "Disable Icons" : "Enable Icons").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
            toggleShowIcons.setTooltip(Tooltip.of(Text.of(TiersClient.showIcons ? "Disable the gamemode icon next to the tier" : "Enable the gamemode icon next to the tier")));
        }).dimensions(width / 2 + 2, distance, 88, 20).build();
        toggleShowIcons.setTooltip(Tooltip.of(Text.of(TiersClient.showIcons ? "Disable the gamemode icon next to the tier" : "Enable the gamemode icon next to the tier")));

        toggleSeparatorMode = ButtonWidget.builder(Text.literal(TiersClient.isSeparatorAdaptive ? "Disable Dynamic Separator" : "Enable Dynamic Separator").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> {
            TiersClient.toggleSeparatorAdaptive();
            buttonWidget.setMessage(Text.literal(TiersClient.isSeparatorAdaptive ? "Disable Dynamic Separator" : "Enable Dynamic Separator").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
            toggleSeparatorMode.setTooltip(Tooltip.of(Text.of(TiersClient.isSeparatorAdaptive ? "Make the Tiers separator gray" : "Make the Tiers separator match the tier color")));
        }).dimensions(width / 2 - 90, distance + 25, 180, 20).build();
        toggleSeparatorMode.setTooltip(Tooltip.of(Text.of(TiersClient.isSeparatorAdaptive ? "Make the Tiers separator gray" : "Make the Tiers separator match the tier color")));

        cycleDisplayMode = ButtonWidget.builder(Text.literal(TiersClient.displayMode.getCurrentMode()).setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> {
            TiersClient.cycleDisplayMode();
            buttonWidget.setMessage(Text.literal(TiersClient.displayMode.getCurrentMode()).setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
        }).dimensions(width / 2 - 90, distance + 50, 180, 20).build();
        cycleDisplayMode.setTooltip(Tooltip.of(Text.of("""
                Selected: only the selected tier will be displayed
                
                Highest: only the highest tier will be displayed
                
                Adaptive Highest: the highest tier will be displayed if selected does not exist""")));

        if (ownProfile.status == Status.READY) {
            enableOwnProfile = ButtonWidget.builder(Text.literal(useOwnProfile ? "Preview default" : "Preview " + ownProfile.name).setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> {
                useOwnProfile = !useOwnProfile;

                loadPlayerAvatar();

                buttonWidget.setMessage(Text.literal(useOwnProfile ? "Preview default" : "Preview " + ownProfile.name).setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))));
                enableOwnProfile.setTooltip(Tooltip.of(Text.of(useOwnProfile ? "Preview the default profile (" + defaultProfile.name + ")" : "Preview your player profile (" + ownProfile.name + ")")));
            }).dimensions(width / 2 - 90, distance + 75, 180, 20).build();
            enableOwnProfile.setTooltip(Tooltip.of(Text.of(useOwnProfile ? "Preview the default profile (" + defaultProfile.name + ")" : "Preview your player profile (" + ownProfile.name + ")")));
        } else {
            enableOwnProfile = ButtonWidget.builder(Text.literal("Cannot switch profiles").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> {
            }).dimensions(width / 2 - 90, distance + 75, 180, 20).build();
            enableOwnProfile.setTooltip(Tooltip.of(Text.of("Can't switch profiles: " + ownProfile.name + " is not found or fetched yet")));
        }

        clearPlayerCache = ButtonWidget.builder(Text.literal("Clear cache").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> TiersClient.clearCache(false)).dimensions(width - 88 - 10, height - 20 - 10, 88, 20).build();
        clearPlayerCache.setTooltip(Tooltip.of(Text.of("Clear all player cache")));

        mcTiersCOMPosition = ButtonWidget.builder(Text.of(TiersClient.mcTiersCOMPosition.getStatus()), (buttonWidget) -> {
            TiersClient.cycleMCTiersCOMPosition();
            updateButtons = 0;
            buttonWidget.setMessage(Text.of(TiersClient.mcTiersCOMPosition.getStatus()));
        }).dimensions(centerX - 58 - 100 + 29, distance + 145, 58, 20).build();

        mcTiersIOPosition = ButtonWidget.builder(Text.of(TiersClient.mcTiersIOPosition.getStatus()), (buttonWidget) -> {
            TiersClient.cycleMCTiersIOPosition();
            updateButtons = 1;
            buttonWidget.setMessage(Text.of(TiersClient.mcTiersIOPosition.getStatus()));
        }).dimensions(centerX - 29, distance + 145, 58, 20).build();

        subtiersNETPosition = ButtonWidget.builder(Text.of(TiersClient.subtiersNETPosition.getStatus()), (buttonWidget) -> {
            TiersClient.cycleSubtiersNETPosition();
            updateButtons = 2;
            buttonWidget.setMessage(Text.of(TiersClient.subtiersNETPosition.getStatus()));
        }).dimensions(centerX + 100 - 29, distance + 145, 58, 20).build();

        mcTiersCOMPosition.setTooltip(Tooltip.of(Text.of("""
                Right: MCTiersCOM tiers will be displayed on the right of the nametag
                
                Left: MCTiersCOM tiers will be displayed on the left of the nametag
                
                Off: MCTiersCOM will be disabled""")));
        mcTiersIOPosition.setTooltip(Tooltip.of(Text.of("""
                Right: MCTiersIO tiers will be displayed on the right of the nametag
                
                Left: MCTiersIO tiers will be displayed on the left of the nametag
                
                Off: MCTiersIO will be disabled""")));
        subtiersNETPosition.setTooltip(Tooltip.of(Text.of("""
                Right: SubtiersNET tiers will be displayed on the right of the nametag
                
                Left: SubtiersNET tiers will be displayed on the left of the nametag
                
                Off: SubtiersNET will be disabled""")));

        activeRightMode = ButtonWidget.builder(Icons.CYCLE, (buttonWidget) -> TiersClient.cycleRightMode()).dimensions(centerX + 90 + 5, distance + 75, 20, 20).build();
        activeRightMode.setTooltip(Tooltip.of(Text.of("Cycle active right gamemode")));

        activeLeftMode = ButtonWidget.builder(Icons.CYCLE, (buttonWidget) -> TiersClient.cycleLeftMode()).dimensions(centerX - 90 - 20 - 5, distance + 75, 20, 20).build();
        activeLeftMode.setTooltip(Tooltip.of(Text.of("Cycle active left gamemode")));

        this.addDrawableChild(toggleModWidget);
        this.addDrawableChild(toggleShowIcons);
        this.addDrawableChild(toggleSeparatorMode);
        this.addDrawableChild(cycleDisplayMode);
        this.addDrawableChild(enableOwnProfile);
        this.addDrawableChild(clearPlayerCache);
        this.addDrawableChild(mcTiersCOMPosition);
        this.addDrawableChild(mcTiersIOPosition);
        this.addDrawableChild(subtiersNETPosition);
        this.addDrawableChild(activeRightMode);
        this.addDrawableChild(activeLeftMode);
    }

    private void drawPlayerAvatar(DrawContext context, int x, int y) {
        if (playerAvatarTexture != null && imageReady)
            context.drawTexture(RenderLayer::getGuiTextured, playerAvatarTexture, x - width / 32, y, 0, 0, width / 16, (int) (width / 6.666), width / 16, (int) (width / 6.666));
        else if (ownProfile.numberOfImageRequests > 4)
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(ownProfile.name + "'s image failed to load. Clear cache and retry"), x, y + 20, ColorControl.getColor("red"));
        else
            loadPlayerAvatar();
    }

    private void loadPlayerAvatar() {
        File avatarFile = FabricLoader.getInstance().getGameDir().resolve("cache/tiers/" + (useOwnProfile ? ownProfile.uuid : defaultProfile.uuid) + ".png").toFile();
        if (!avatarFile.exists())
            return;

        try (FileInputStream stream = new FileInputStream(avatarFile)) {
            playerAvatarTexture = Identifier.of("tiers", (useOwnProfile ? ownProfile.uuid : defaultProfile.uuid));
            MinecraftClient.getInstance().getTextureManager().registerTexture(playerAvatarTexture, new NativeImageBackedTexture(null, NativeImage.read(stream)));
            imageReady = true;
        } catch (IOException ignored) {
        }
    }

    public static Screen getConfigScreen(Screen ignoredScreen) {
        return new ConfigScreen();
    }
}