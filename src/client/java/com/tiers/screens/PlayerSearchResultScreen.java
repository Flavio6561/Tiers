package com.tiers.screens;

import com.tiers.TiersClient;
import com.tiers.profile.types.MCTiersProfile;
import com.tiers.profile.types.PvPTiersProfile;
import com.tiers.profile.types.SubtiersProfile;
import com.tiers.textures.ColorControl;
import com.tiers.textures.Icons;
import com.tiers.profile.GameMode;
import com.tiers.profile.PlayerProfile;
import com.tiers.profile.Status;
import com.tiers.profile.types.SuperProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.io.FileInputStream;
import java.io.IOException;

import static com.tiers.TiersClient.LOGGER;

public class PlayerSearchResultScreen extends Screen {
    private final PlayerProfile playerProfile;
    private final Identifier playerAvatarTexture = Identifier.of("");

    ButtonWidget dimensionsWarning;

    private int separator;
    private boolean small;
    private boolean tooSmall;
    private boolean imageReady;
    private boolean toastShown;

    public PlayerSearchResultScreen(PlayerProfile playerProfile) {
        super(Text.of(playerProfile.name));
        this.playerProfile = playerProfile;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!playerProfile.isPlayerValid()) {
            this.close();
            return;
        }

        if (playerProfile.nameChanged && !toastShown) {
            MinecraftClient.getInstance().getToastManager().add(SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Possible name change (" + playerProfile.name + " to " + playerProfile.originalName + ")"), Text.of("The data shown should be accurate")));
            toastShown = true;
        }

        int centerX = width / 2;
        int listY = (int) (height / 2.65);
        separator = height / 23;
        small = width < 575 || height < 420;
        tooSmall = width < 430 || height < 262;
        int firstListX = (int) (centerX - width / 3.5) - 25;
        int thirdListX = (int) (centerX + width / 3.5) + 25;
        int avatarY = height / 55 + 12;

        super.render(context, mouseX, mouseY, delta);

        dimensionsWarning.visible = small;
        if (tooSmall) {
            dimensionsWarning.setMessage(Text.of("⚠"));
            dimensionsWarning.setTooltip(Tooltip.of(Text.of("Your window dimensions (" + width + "x" + height + ") are too small\nLower the GUI scale or make the window bigger! (min: 430x262)")));
        }

        if (playerProfile.status == Status.SEARCHING) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.of("Searching for " + playerProfile.name + "..."), centerX, listY, ColorControl.getColorMinecraftStandard("green"));
            return;
        }

        if (playerProfile.numberOfImageRequests == 0)
            playerProfile.savePlayerImage();

        drawPlayerAvatar(context, centerX, avatarY);
        if (!imageReady)
            context.drawCenteredTextWithShadow(this.textRenderer, Text.of("Loading " + playerProfile.name + "'s skin"), centerX, avatarY + 50, ColorControl.getColorMinecraftStandard("green"));

        context.drawCenteredTextWithShadow(this.textRenderer, playerProfile.getFullNametag(), centerX, height / 55, Colors.WHITE);

        drawCategoryList(context, MCTiersProfile.MCTIERS_IMAGE, playerProfile.profileMCTiers, firstListX, listY);
        drawCategoryList(context, PvPTiersProfile.PVPTIERS_IMAGE, playerProfile.profilePvPTiers, centerX, listY);
        drawCategoryList(context, SubtiersProfile.SUBTIERS_IMAGE, playerProfile.profileSubtiers, thirdListX, listY);
    }

    private void drawCategoryList(DrawContext context, Identifier image, SuperProfile superProfile, int x, int y) {
        if (superProfile == null) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Loading from API...", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("green"));
            return;
        }

        if (image == MCTiersProfile.MCTIERS_IMAGE)
            context.drawTexture(RenderPipelines.GUI_TEXTURED, image, x - 64, (int) (y + 2.4 * separator) + 4 - 38, 0, 0, 128, 24, 128, 24);
        else if (image == PvPTiersProfile.PVPTIERS_IMAGE)
            context.drawTexture(RenderPipelines.GUI_TEXTURED, image, x - 12, (int) (y + 2.4 * separator) + 4 - 38, 0, 0, 24, 24, 24, 24);
        else
            context.drawTexture(RenderPipelines.GUI_TEXTURED, image, (int) (x - 15.5), (int) (y + 2.4 * separator) - 38, 0, 0, 31, 31, 31, 31);

        if (superProfile.status == Status.SEARCHING) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Searching...", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("green"));
            return;
        } else if (superProfile.status == Status.NOT_EXISTING) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Unranked", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("red"));
            return;
        } else if (superProfile.status == Status.TIMEOUTED) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Search timeouted. Clear cache and retry", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("red"));
            return;
        } else if (superProfile.status == Status.API_ISSUE) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Search failed: API issue", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("red"));
            context.drawCenteredTextWithShadow(this.textRenderer, "Update Tiers or retry in a while", x, (int) (y + 2.8 * separator + 15), ColorControl.getColorMinecraftStandard("red"));
            return;
        }

        if (!superProfile.drawn) {
            TextWidget regionLabel = new TextWidget(Icons.colorText("Region", "region"), this.textRenderer);
            regionLabel.setPosition(x - 44, (int) (y + 2.4 * separator));
            this.addDrawableChild(regionLabel);

            TextWidget overallLabel = new TextWidget(Icons.colorText("Overall", "overall"), this.textRenderer);
            overallLabel.setPosition(x - 44, (int) (y + 2.4 * separator) + 16);
            this.addDrawableChild(overallLabel);

            TextWidget regionIcon = new TextWidget(Icons.GLOBE, this.textRenderer);
            regionIcon.setPosition(x - 64, (int) (y + 2.4 * separator + 2));
            regionIcon.setTextColor(ColorControl.getColor("region"));
            regionIcon.setTooltip(Tooltip.of(regionLabel.getMessage()));
            this.addDrawableChild(regionIcon);

            TextWidget overallIcon = new TextWidget(Icons.OVERALL, this.textRenderer);
            overallIcon.setPosition(x - 64, (int) (y + 2.4 * separator + 2) + 16);
            overallIcon.setTextColor(ColorControl.getColor("overall"));
            overallIcon.setTooltip(Tooltip.of(overallLabel.getMessage()));
            this.addDrawableChild(overallIcon);

            TextWidget region = new TextWidget(superProfile.displayedRegion, this.textRenderer);
            region.setPosition(x + 52 - (superProfile.displayedRegion.getString().length() - 2) * 3, (int) (y + 2.4 * separator));
            region.setTooltip(Tooltip.of(superProfile.regionTooltip));
            this.addDrawableChild(region);

            TextWidget overall = new TextWidget(superProfile.displayedOverall, this.textRenderer);
            overall.setPosition(x + 52 - (superProfile.displayedOverall.getString().length() - 2) * 3, (int) (y + 2.4 * separator) + 16);
            overall.setTooltip(Tooltip.of(superProfile.overallTooltip));
            this.addDrawableChild(overall);

            drawTierList(superProfile, x - 64, (int) (y + 2.4 * separator) + 40);

            superProfile.drawn = true;
        }
    }

    private void drawTierList(SuperProfile superProfile, int x, int y) {
        int originalX = x;
        if (small) {
            y -= 7;
            int count = 1;
            int stage = 0;
            for (GameMode gameMode : superProfile.gameModes) {
                if (drawGameModeTiers(gameMode, x + 5, y + stage * 36)) {
                    x += 35;
                    if (count % 4 == 0) {
                        stage++;
                        x = originalX;
                    }
                    count++;
                }
            }
        } else {
            for (GameMode gameMode : superProfile.gameModes)
                if (drawGameModeTiers(gameMode, x, y)) y += 15;
        }
    }

    private boolean drawGameModeTiers(GameMode mode, int x, int y) {
        if (mode.drawn || mode.status != Status.READY)
            return false;

        TextWidget icon = new TextWidget(mode.gamemode.getIcon(), this.textRenderer);
        icon.setPosition(x, y + 3);
        if (small)
            icon.setPosition(x, y + 3);
        icon.setTooltip(Tooltip.of(mode.gamemode.getTextLabel()));
        this.addDrawableChild(icon);

        TextWidget label = new TextWidget(mode.gamemode.getTextLabel(), this.textRenderer);
        label.setPosition(x + 20, y);
        if (!small)
            this.addDrawableChild(label);

        TextWidget tier = new TextWidget(mode.displayedTier, this.textRenderer);
        tier.setPosition(x + 114 - (mode.displayedTier.getString().length() - 3) * 3, y);
        if (small)
            tier.setPosition(x - 2 - (mode.displayedTier.getString().length() - 3) * 2, y + 14);
        tier.setTooltip(Tooltip.of(mode.tierTooltip));
        this.addDrawableChild(tier);

        if (mode.hasPeak && mode.peakTierTooltip.getStyle().getColor() != null) {
            TextWidget peakTier = new TextWidget(mode.displayedPeakTier, this.textRenderer);
            peakTier.setPosition(x + 136, y);
            if (small)
                peakTier.setPosition(x - 6, y + 24);
            peakTier.setTooltip(Tooltip.of(mode.peakTierTooltip));
            this.addDrawableChild(peakTier);
        }

        mode.drawn = true;

        return true;
    }

    private void drawPlayerAvatar(DrawContext context, int x, int y) {
        if (imageReady) {
            if (playerProfile.imageSaved == 1 || playerProfile.imageSaved == 2)
                context.drawTexture(RenderPipelines.GUI_TEXTURED, playerAvatarTexture, x - width / 32, y, 0, 0, width / 16, (int) (width / 6.666), width / 16, (int) (width / 6.666));
            else if (playerProfile.imageSaved < 6 && playerProfile.imageSaved > 2)
                context.drawTexture(RenderPipelines.GUI_TEXTURED, playerAvatarTexture, (int) (x - width / 22.5), y, 0, 0, (int) (width / 11.25), (int) (width / 6.666), (int) (width / 11.25), (int) (width / 6.666));
        } else if (playerProfile.imageSaved != 0) {
            loadPlayerAvatar();
        } else if (playerProfile.numberOfImageRequests == 6)
            context.drawCenteredTextWithShadow(this.textRenderer, Text.of(playerProfile.name + "'s skin failed to load. Clear cache and retry"), x, y + 50, ColorControl.getColorMinecraftStandard("red"));
    }

    private void loadPlayerAvatar() {
        if (imageReady)
            return;

        try (FileInputStream fileInputStream = new FileInputStream(FabricLoader.getInstance().getGameDir().resolve("cache/tiers/players/" + playerProfile.uuid + ".png").toFile())) {
            MinecraftClient.getInstance().getTextureManager().registerTexture(playerAvatarTexture, new NativeImageBackedTexture(null, NativeImage.read(fileInputStream)));
            imageReady = true;
        } catch (IOException ignored) {
            LOGGER.warn("Error loading player skin");
        }
    }

    @Override
    protected void init() {
        playerProfile.resetDrawnStatus();

        dimensionsWarning = ButtonWidget.builder(Text.of("ℹ"), (buttonWidget) -> {}).dimensions(width - 20 - 5, 5, 20, 20).tooltip(Tooltip.of(Text.of("Your window dimensions (" + width + "x" + height + ") are small\nLower the GUI scale or make the window bigger to have a better experience (min: 575x420)"))).build();
        dimensionsWarning.active = false;
        dimensionsWarning.visible = small;
        if (tooSmall) {
            dimensionsWarning.setMessage(Text.of("⚠"));
            dimensionsWarning.setTooltip(Tooltip.of(Text.of("Your window dimensions (" + width + "x" + height + ") are too small\nLower the GUI scale or make the window bigger! (min: 430x262)")));
        }

        this.addDrawableChild(dimensionsWarning);

        this.addDrawableChild(ButtonWidget.builder(Text.of("Update"), (buttonWidget) -> TiersClient.updatePlayerProfile(playerProfile)).dimensions(5, height - 20 - 5, 50, 20).tooltip(Tooltip.of(Text.of("Update the player profile"))).build());
    }
}