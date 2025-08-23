package com.tiers.screens;

import com.tiers.TiersClient;
import com.tiers.textures.ColorControl;
import com.tiers.textures.Icons;
import com.tiers.profile.GameMode;
import com.tiers.profile.PlayerProfile;
import com.tiers.profile.Status;
import com.tiers.profile.types.SuperProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.tiers.TiersClient.sendMessageToPlayer;

public class PlayerSearchResultScreen extends Screen {
    private final Identifier MCTIERS_IMAGE = Identifier.of("minecraft", "textures/mctiers_logo.png");
    private final Identifier PVPTIERS_IMAGE = Identifier.of("minecraft", "textures/pvptiers_logo.png");
    private final Identifier SUBTIERS_IMAGE = Identifier.of("minecraft", "textures/subtiers_logo.png");

    private final PlayerProfile playerProfile;
    private Identifier playerAvatarTexture;

    private int separator;
    private boolean imageReady = false;

    public PlayerSearchResultScreen(PlayerProfile playerProfile) {
        super(Text.literal(playerProfile.name));
        this.playerProfile = playerProfile;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (playerProfile.status == Status.NOT_EXISTING) {
            this.close();
            sendMessageToPlayer(playerProfile.name + " was not found or isn't a premium account", ColorControl.getColor("red"), false);
            return;
        } else if (playerProfile.status == Status.TIMEOUTED) {
            this.close();
            sendMessageToPlayer(playerProfile.name + "'s search was timeouted. Clear cache and retry", ColorControl.getColor("red"), false);
            return;
        } else if (playerProfile.status == Status.API_ISSUE) {
            this.close();
            sendMessageToPlayer(playerProfile.name + "'s search failed. This is likely a Mojang issue. " + "Contact flavio6561 on Discord for support", ColorControl.getColor("red"), false);
            return;
        }

        int centerX = width / 2;
        int listY = (int) (height / 2.8);
        separator = height / 23;
        int firstListX = (int) (centerX - width / 3.5) - 25;
        int thirdListX = (int) (centerX + width / 3.5) + 25;
        int avatarY = height / 55 + 14;

        super.render(context, mouseX, mouseY, delta);

        if (playerProfile.status == Status.SEARCHING) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Searching for " + playerProfile.name + "..."), centerX, listY, ColorControl.getColorMinecraftStandard("green"));
            return;
        }

        if (playerProfile.numberOfImageRequests == 0)
            playerProfile.savePlayerImage();

        drawPlayerAvatar(context, centerX, avatarY);
        context.drawCenteredTextWithShadow(this.textRenderer, TiersClient.getNametag(playerProfile), centerX, height / 55, ColorControl.getColorMinecraftStandard("text"));

        drawCategoryList(context, MCTIERS_IMAGE, playerProfile.profileMCTiers, firstListX, listY);
        drawCategoryList(context, PVPTIERS_IMAGE, playerProfile.profilePvPTiers, centerX, listY);
        drawCategoryList(context, SUBTIERS_IMAGE, playerProfile.profileSubtiers, thirdListX, listY);
    }

    private void drawCategoryList(DrawContext context, Identifier image, SuperProfile profile, int x, int y) {
        if (profile == null) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Loading from API...", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("green"));
            return;
        }

        if (image == MCTIERS_IMAGE)
            context.drawTexture(RenderLayer::getGuiTextured, image, x - 64, y + 4, 0, 0, 128, 24, 128, 24);
        else if (image == PVPTIERS_IMAGE)
            context.drawTexture(RenderLayer::getGuiTextured, image, x - 12, y + 4, 0, 0, 24, 24, 24, 24);
        else
            context.drawTexture(RenderLayer::getGuiTextured, image, (int) (x - 15.5), y, 0, 0, 31, 31, 31, 31);

        if (profile.status == Status.SEARCHING) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Searching...", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("green"));
            return;
        } else if (profile.status == Status.NOT_EXISTING) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Unranked", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("red"));
            return;
        } else if (profile.status == Status.TIMEOUTED) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Search timeouted. Clear cache and retry", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("red"));
            return;
        } else if (profile.status == Status.API_ISSUE) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Search failed. This is likely an API issue", x, (int) (y + 2.8 * separator), ColorControl.getColorMinecraftStandard("red"));
            context.drawCenteredTextWithShadow(this.textRenderer, "Contact flavio6561 on Discord for support", x, (int) (y + 2.8 * separator + 15), ColorControl.getColorMinecraftStandard("red"));
            return;
        }

        if (!profile.drawn) {
            TextWidget regionLabel = new TextWidget(Text.of("Region"), this.textRenderer);
            regionLabel.setPosition(x - 42, (int) (y + 2.4 * separator));
            regionLabel.setTextColor(ColorControl.getColor("region"));
            this.addDrawableChild(regionLabel);

            TextWidget overallLabel = new TextWidget(Text.of("Overall"), this.textRenderer);
            overallLabel.setPosition(x - 42, (int) (y + 2.4 * separator) + 16);
            overallLabel.setTextColor(ColorControl.getColor("overall"));
            this.addDrawableChild(overallLabel);

            TextWidget regionIcon = new TextWidget(Icons.GLOBE, this.textRenderer);
            regionIcon.setPosition(x - 62, (int) (y + 2.4 * separator + 2));
            regionIcon.setTextColor(ColorControl.getColor("region"));
            this.addDrawableChild(regionIcon);

            TextWidget overallIcon = new TextWidget(Icons.OVERALL, this.textRenderer);
            overallIcon.setPosition(x - 62, (int) (y + 2.4 * separator + 2) + 16);
            overallIcon.setTextColor(ColorControl.getColor("overall"));
            this.addDrawableChild(overallIcon);

            TextWidget region = new TextWidget(profile.displayedRegion, this.textRenderer);
            region.setPosition(x + 45 - (profile.displayedRegion.getString().length() - 2) * 3, (int) (y + 2.4 * separator));
            region.setTooltip(Tooltip.of(profile.regionTooltip));
            this.addDrawableChild(region);

            TextWidget overall = new TextWidget(profile.displayedOverall, this.textRenderer);
            overall.setPosition(x + 45 - (profile.displayedOverall.getString().length() - 2) * 3, (int) (y + 2.4 * separator) + 16);
            overall.setTooltip(Tooltip.of(profile.overallTooltip));
            this.addDrawableChild(overall);

            drawTierList(profile, x - 62, (int) (y + 2.4 * separator) + 40);

            profile.drawn = true;
        }
    }

    private void drawTierList(SuperProfile profile, int x, int y) {
        for (GameMode gameMode : profile.gameModes)
            if (drawGameModeTiers(gameMode, x, y)) y += 15;
    }

    private boolean drawGameModeTiers(GameMode mode, int x, int y) {
        if (mode.drawn || mode.status != Status.READY)
            return false;

        TextWidget icon = new TextWidget(mode.name.icon, this.textRenderer);
        icon.setPosition(x, y + 3);
        this.addDrawableChild(icon);

        TextWidget label = new TextWidget(mode.name.label, this.textRenderer);
        label.setPosition(x + 20, y);
        this.addDrawableChild(label);

        TextWidget tier = new TextWidget(mode.displayedTier, this.textRenderer);
        tier.setPosition(x + 105 - (mode.displayedTier.getString().length() - 3) * 3, y);
        tier.setTooltip(Tooltip.of(mode.tierTooltip));
        this.addDrawableChild(tier);

        if (mode.hasPeak && mode.peakTierTooltip.getStyle().getColor() != null) {
            TextWidget peakTier = new TextWidget(mode.displayedPeakTier, this.textRenderer);
            peakTier.setPosition(x + 128, y);
            peakTier.setTooltip(Tooltip.of(mode.peakTierTooltip));
            this.addDrawableChild(peakTier);
        }

        mode.drawn = true;

        return true;
    }

    private void drawPlayerAvatar(DrawContext context, int x, int y) {
        if (playerAvatarTexture != null && imageReady)
            context.drawTexture(RenderLayer::getGuiTextured, playerAvatarTexture, x - width / 32, y, 0, 0, width / 16, (int) (width / 6.666), width / 16, (int) (width / 6.666));
        else if (playerProfile.imageSaved)
            loadPlayerAvatar();
        else if (playerProfile.numberOfImageRequests == 5)
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(playerProfile.name + "'s skin failed to load. Clear cache and retry"), x, y + 50, ColorControl.getColorMinecraftStandard("red"));
        else
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Loading " + playerProfile.name + "'s skin"), x, y + 50, ColorControl.getColorMinecraftStandard("green"));
    }

    private void loadPlayerAvatar() {
        File avatarFile = FabricLoader.getInstance().getGameDir().resolve("cache/tiers/players/" + playerProfile.uuid + ".png").toFile();
        if (!avatarFile.exists())
            return;

        try (FileInputStream stream = new FileInputStream(avatarFile)) {
            playerAvatarTexture = Identifier.of("players", playerProfile.uuid);
            MinecraftClient.getInstance().getTextureManager().registerTexture(playerAvatarTexture, new NativeImageBackedTexture(NativeImage.read(stream)));
            imageReady = true;
        } catch (IOException ignored) {
        }
    }

    @Override
    protected void init() {
        playerProfile.resetDrawnStatus();
    }
}