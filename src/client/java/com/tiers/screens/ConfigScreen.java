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

    private final Identifier MCTIERS_IMAGE = Identifier.of("minecraft", "textures/mctiers_logo.png");
    private final Identifier PVPTIERS_IMAGE = Identifier.of("minecraft", "textures/pvptiers_logo.png");
    private final Identifier SUBTIERS_IMAGE = Identifier.of("minecraft", "textures/subtiers_logo.png");

    private ButtonWidget toggleModWidget;
    private ButtonWidget toggleShowIcons;
    private ButtonWidget toggleSeparatorMode;
    private ButtonWidget cycleDisplayMode;
    private ButtonWidget clearPlayerCache;
    private ButtonWidget enableOwnProfile;
    private ButtonWidget positionMCTiers;
    private ButtonWidget positionPvPTiers;
    private ButtonWidget positionSubtiers;

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

        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Tiers config"), centerX, height / 50, ColorControl.getColorMinecraftStandard("text"));

        drawPlayerAvatar(context, centerX, height - 10 - (int) (width / 6.666));
        context.drawCenteredTextWithShadow(this.textRenderer, TiersClient.getNametag(useOwnProfile ? ownProfile : defaultProfile), centerX, height - 24 - (int) (width / 6.666), ColorControl.getColorMinecraftStandard("text"));

        drawCategoryList(context, MCTIERS_IMAGE, centerX - 100, distance + 110);
        drawCategoryList(context, PVPTIERS_IMAGE, centerX, distance + 110);
        drawCategoryList(context, SUBTIERS_IMAGE, centerX + 100, distance + 110);

        context.drawTextWithShadow(this.textRenderer, TiersClient.getRightIcon(), centerX + 90 + 33, distance + 75 + 8, ColorControl.getColorMinecraftStandard("text"));
        context.drawTextWithShadow(this.textRenderer, TiersClient.getLeftIcon(), centerX - 90 - 33 - 12, distance + 75 + 8, ColorControl.getColorMinecraftStandard("text"));

        checkUpdates();
    }

    private void drawCategoryList(DrawContext context, Identifier image, int x, int y) {
        if (image == MCTIERS_IMAGE)
            context.drawTexture(image, x - 64, y + 4, 0, 0, 128, 24, 128, 24);
        else if (image == PVPTIERS_IMAGE)
            context.drawTexture(image, x - 12, y + 4, 0, 0, 24, 24, 24, 24);
        else
            context.drawTexture(image, (int) (x - 15.5), y, 0, 0, 31, 31, 31, 31);
    }

    private void checkUpdates() {
        toggleModWidget.setPosition(width / 2 - 88 - 2, distance);
        toggleShowIcons.setPosition(width / 2 + 2, distance);
        toggleSeparatorMode.setPosition(width / 2 - 90, distance + 25);
        cycleDisplayMode.setPosition(width / 2 - 90, distance + 50);
        enableOwnProfile.setPosition(width / 2 - 90, distance + 75);
        clearPlayerCache.setPosition(width - 88 - 5, height - 20 - 5);

        positionMCTiers.setPosition(centerX - 58 - 100 + 29, distance + 145);
        positionPvPTiers.setPosition(centerX - 29, distance + 145);
        positionSubtiers.setPosition(centerX + 100 - 29, distance + 145);

        activeRightMode.setPosition(centerX + 90 + 5, distance + 75);
        activeLeftMode.setPosition(centerX - 90 - 20 - 5, distance + 75);

        if (updateButtons == 0) {
            if (TiersClient.positionPvPTiers == TiersClient.positionMCTiers) {
                TiersClient.positionPvPTiers = TiersClient.DisplayStatus.OFF;
                positionPvPTiers.setMessage(Text.of(TiersClient.positionPvPTiers.getStatus()));
            }
            if (TiersClient.positionSubtiers == TiersClient.positionMCTiers) {
                TiersClient.positionSubtiers = TiersClient.DisplayStatus.OFF;
                positionSubtiers.setMessage(Text.of(TiersClient.positionSubtiers.getStatus()));
            }
            updateButtons = -1;
        } else if (updateButtons == 1) {
            if (TiersClient.positionMCTiers == TiersClient.positionPvPTiers) {
                TiersClient.positionMCTiers = TiersClient.DisplayStatus.OFF;
                positionMCTiers.setMessage(Text.of(TiersClient.positionMCTiers.getStatus()));
            }
            if (TiersClient.positionSubtiers == TiersClient.positionPvPTiers) {
                TiersClient.positionSubtiers = TiersClient.DisplayStatus.OFF;
                positionSubtiers.setMessage(Text.of(TiersClient.positionSubtiers.getStatus()));
            }
            updateButtons = -1;
        } else if (updateButtons == 2) {
            if (TiersClient.positionMCTiers == TiersClient.positionSubtiers) {
                TiersClient.positionMCTiers = TiersClient.DisplayStatus.OFF;
                positionMCTiers.setMessage(Text.of(TiersClient.positionMCTiers.getStatus()));
            }
            if (TiersClient.positionPvPTiers == TiersClient.positionSubtiers) {
                TiersClient.positionPvPTiers = TiersClient.DisplayStatus.OFF;
                positionPvPTiers.setMessage(Text.of(TiersClient.positionPvPTiers.getStatus()));
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
            enableOwnProfile.setTooltip(Tooltip.of(Text.of("Can't switch profiles: Tiers version not supported. Update to 1.20.4+ versions")));
        }

        clearPlayerCache = ButtonWidget.builder(Text.literal("Clear cache").setStyle(Style.EMPTY.withColor(ColorControl.getColor("text"))), (buttonWidget) -> TiersClient.clearCache(false)).dimensions(width - 88 - 10, height - 20 - 10, 88, 20).build();
        clearPlayerCache.setTooltip(Tooltip.of(Text.of("Clear all player cache")));

        positionMCTiers = ButtonWidget.builder(Text.of(TiersClient.positionMCTiers.getStatus()), (buttonWidget) -> {
            TiersClient.cycleMCTiersPosition();
            updateButtons = 0;
            buttonWidget.setMessage(Text.of(TiersClient.positionMCTiers.getStatus()));
        }).dimensions(centerX - 58 - 100 + 29, distance + 145, 58, 20).build();

        positionPvPTiers = ButtonWidget.builder(Text.of(TiersClient.positionPvPTiers.getStatus()), (buttonWidget) -> {
            TiersClient.cyclePvPTiersPosition();
            updateButtons = 1;
            buttonWidget.setMessage(Text.of(TiersClient.positionPvPTiers.getStatus()));
        }).dimensions(centerX - 29, distance + 145, 58, 20).build();

        positionSubtiers = ButtonWidget.builder(Text.of(TiersClient.positionSubtiers.getStatus()), (buttonWidget) -> {
            TiersClient.cycleSubtiersPosition();
            updateButtons = 2;
            buttonWidget.setMessage(Text.of(TiersClient.positionSubtiers.getStatus()));
        }).dimensions(centerX + 100 - 29, distance + 145, 58, 20).build();

        positionMCTiers.setTooltip(Tooltip.of(Text.of("""
                Right: MCTiers tiers will be displayed on the right of the nametag
                
                Left: MCTiers tiers will be displayed on the left of the nametag
                
                Off: MCTiers will be disabled""")));
        positionPvPTiers.setTooltip(Tooltip.of(Text.of("""
                Right: PvPTiers tiers will be displayed on the right of the nametag
                
                Left: PvPTiers tiers will be displayed on the left of the nametag
                
                Off: PvPTiers will be disabled""")));
        positionSubtiers.setTooltip(Tooltip.of(Text.of("""
                Right: Subtiers tiers will be displayed on the right of the nametag
                
                Left: Subtiers tiers will be displayed on the left of the nametag
                
                Off: Subtiers will be disabled""")));

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
        this.addDrawableChild(positionMCTiers);
        this.addDrawableChild(positionPvPTiers);
        this.addDrawableChild(positionSubtiers);
        this.addDrawableChild(activeRightMode);
        this.addDrawableChild(activeLeftMode);
    }

    private void drawPlayerAvatar(DrawContext context, int x, int y) {
        if (playerAvatarTexture != null && imageReady)
            context.drawTexture(playerAvatarTexture, x - width / 32, y, 0, 0, width / 16, (int) (width / 6.666), width / 16, (int) (width / 6.666));
        else if (ownProfile.numberOfImageRequests > 4)
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(ownProfile.name + "'s skin failed to load. Clear cache and retry"), x, y + 40, ColorControl.getColorMinecraftStandard("red"));
        else
            loadPlayerAvatar();
    }

    private void loadPlayerAvatar() {
        File avatarFile = FabricLoader.getInstance().getGameDir().resolve("cache/tiers/" + (useOwnProfile ? ownProfile.uuid : defaultProfile.uuid) + ".png").toFile();
        if (!avatarFile.exists())
            return;

        try (FileInputStream stream = new FileInputStream(avatarFile)) {
            playerAvatarTexture = Identifier.of("tiers", (useOwnProfile ? ownProfile.uuid : defaultProfile.uuid));
            MinecraftClient.getInstance().getTextureManager().registerTexture(playerAvatarTexture, new NativeImageBackedTexture(NativeImage.read(stream)));
            imageReady = true;
        } catch (IOException ignored) {
        }
    }

    public static Screen getConfigScreen(Screen ignoredScreen) {
        return new ConfigScreen();
    }
}