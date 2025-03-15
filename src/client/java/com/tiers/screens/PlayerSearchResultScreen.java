package com.tiers.screens;

import com.tiers.Icons;
import com.tiers.TiersClient;
import com.tiers.profiles.*;
import com.tiers.profiles.types.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PlayerSearchResultScreen extends Screen {
    private final PlayerProfile playerProfile;
    private Identifier playerAvatarTexture;
    private boolean imageReady = false;

    private final Identifier MCTIERS_COM_IMAGE = Identifier.of("minecraft", "textures/mctiers_com_logo.png");
    private final Identifier MCTIERS_IO_IMAGE = Identifier.of("minecraft", "textures/mctiers_io_logo.png");
    private final Identifier SUBTIERS_NET_IMAGE = Identifier.of("minecraft", "textures/subtiers_net_logo.png");

    int centerX;
    int listY;
    int separator;
    int firstListX;
    int thirdListX;
    int distance;
    int avatarY;

    public PlayerSearchResultScreen(PlayerProfile playerProfile) {
        super(Text.literal(playerProfile.name));
        this.playerProfile = playerProfile;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (playerProfile.status == Status.NOT_EXISTING) {
            this.close();
            TiersClient.sendMessageToPlayer(playerProfile.name + " was not found or isn't a premium account", 0xdd0000);
            return;
        } else if (playerProfile.status == Status.TIMEOUTED) {
            this.close();
            TiersClient.sendMessageToPlayer(playerProfile.name + "'s search was timeouted. Clear cache and retry", 0xdd0000);
            return;
        }

        centerX = width / 2;
        listY = (int) (height / 2.8);
        separator = height / 23;
        firstListX = (int) (centerX - width / 4.3);
        thirdListX = (int) (centerX + width / 4.3);
        distance = (int) (height / 7.5);
        avatarY = height / 18;

        super.render(context, mouseX, mouseY, delta);

        if (playerProfile.status == Status.SEARCHING) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Searching for " + playerProfile.name + "..."), centerX, listY, 0x00aa00);
            return;
        }

        drawPlayerAvatar(context, centerX, avatarY);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(playerProfile.name + "'s profile"), centerX, height / 70, 0xdddddd);

        drawCategoryList(context, MCTIERS_COM_IMAGE, playerProfile.mcTiersCOMProfile, firstListX, listY);
        drawCategoryList(context, MCTIERS_IO_IMAGE, playerProfile.mcTiersIOProfile, centerX, listY);
        drawCategoryList(context, SUBTIERS_NET_IMAGE, playerProfile.subtiersNETProfile, thirdListX, listY);
    }

    private void drawCategoryList(DrawContext context, Identifier image, BaseProfile profile, int x, int y) {
        if (profile == null) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Loading from API...", x, (int) (y + 2.8 * separator), 0x00dd00);
            return;
        }

        if (image == MCTIERS_COM_IMAGE)
            context.drawTexture(RenderLayer::getGuiTextured, image, x - 56, y + 5, 0, 0, 112, 21, 112, 21);
        else
            context.drawTexture(RenderLayer::getGuiTextured, image, x - 13, y, 0, 0, 26, 26, 26, 26);

        if (profile.status == Status.SEARCHING) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Searching...", x, (int) (y + 2.8 * separator), 0x00dd00);
            return;
        }
        else if (profile.status == Status.NOT_EXISTING) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Unranked", x, (int) (y + 2.8 * separator), 0xdd0000);
            return;
        }
        else if (profile.status == Status.TIMEOUTED) {
            context.drawCenteredTextWithShadow(this.textRenderer, "Search timeouted. Clear cache and retry", x, (int) (y + 2.8 * separator), 0xdd0000);
            return;
        }

        if (!profile.drawn) {
            TextWidget regionLabel = new TextWidget(Text.of("Region"), this.textRenderer);
            regionLabel.setPosition(x - 42, (int) (y + 2.4 * separator));
            regionLabel.setTextColor(0xcdf7f6);
            this.addDrawableChild(regionLabel);

            TextWidget overallLabel = new TextWidget(Text.of("Overall"), this.textRenderer);
            overallLabel.setPosition(x - 42, (int) (y + 3.2 * separator));
            overallLabel.setTextColor(0x8fb8de);
            this.addDrawableChild(overallLabel);

            TextWidget regionIcon = new TextWidget(Icons.globe, this.textRenderer);
            regionIcon.setPosition(x - 62, (int) (y + 2.4 * separator + 2));
            regionIcon.setTextColor(0xcdf7f6);
            this.addDrawableChild(regionIcon);

            TextWidget overallIcon = new TextWidget(Icons.overall, this.textRenderer);
            overallIcon.setPosition(x - 62, (int) (y + 3.2 * separator + 2));
            overallIcon.setTextColor(0x8fb8de);
            this.addDrawableChild(overallIcon);

            TextWidget region = new TextWidget(profile.displayedRegion, this.textRenderer);
            region.setPosition(x + 45, (int) (y + 2.4 * separator));
            region.setTooltip(Tooltip.of(profile.regionTooltip));
            this.addDrawableChild(region);

            TextWidget overall = new TextWidget(profile.displayedOverall, this.textRenderer);
            overall.setPosition(x + 45 - (profile.displayedOverall.getString().length() - 2) * 3, (int) (y + 3.2 * separator));
            overall.setTooltip(Tooltip.of(profile.overallTooltip));
            this.addDrawableChild(overall);

            drawTierList(profile, x - 62, (int) (y + height / 5.4));

            profile.drawn = true;
        }
    }

    private void drawTierList(BaseProfile profile, int x, int y) {
        switch (profile) {
            case MCTiersCOMProfile mcTiersCOMProfile -> {
                if (drawGameModeTiers(mcTiersCOMProfile.vanilla, x, y)) y += 15;
                if (drawGameModeTiers(mcTiersCOMProfile.uhc, x, y)) y += 15;
                if (drawGameModeTiers(mcTiersCOMProfile.pot, x, y)) y += 15;
                if (drawGameModeTiers(mcTiersCOMProfile.netherite_op, x, y)) y += 15;
                if (drawGameModeTiers(mcTiersCOMProfile.smp, x, y)) y += 15;
                if (drawGameModeTiers(mcTiersCOMProfile.sword, x, y)) y += 15;
                if (drawGameModeTiers(mcTiersCOMProfile.axe, x, y)) y += 15;
                drawGameModeTiers(mcTiersCOMProfile.mace, x, y);
            }
            case MCTiersIOProfile mcTiersIOProfile -> {
                if (drawGameModeTiers(mcTiersIOProfile.vanilla, x, y)) y += 15;
                if (drawGameModeTiers(mcTiersIOProfile.uhc, x, y)) y += 15;
                if (drawGameModeTiers(mcTiersIOProfile.pot, x, y)) y += 15;
                if (drawGameModeTiers(mcTiersIOProfile.netherite_pot, x, y)) y += 15;
                if (drawGameModeTiers(mcTiersIOProfile.smp, x, y)) y += 15;
                if (drawGameModeTiers(mcTiersIOProfile.sword, x, y)) y += 15;
                if (drawGameModeTiers(mcTiersIOProfile.axe, x, y)) y += 15;
                drawGameModeTiers(mcTiersIOProfile.elytra, x, y);
            }
            case SubtiersNETProfile subtiersNETProfile -> {
                if (drawGameModeTiers(subtiersNETProfile.minecart, x, y)) y += 15;
                if (drawGameModeTiers(subtiersNETProfile.diamond_crystal, x, y)) y += 15;
                if (drawGameModeTiers(subtiersNETProfile.iron_pot, x, y)) y += 15;
                if (drawGameModeTiers(subtiersNETProfile.elytra, x, y)) y += 15;
                if (drawGameModeTiers(subtiersNETProfile.speed, x, y)) y += 15;
                if (drawGameModeTiers(subtiersNETProfile.creeper, x, y)) y += 15;
                if (drawGameModeTiers(subtiersNETProfile.manhunt, x, y)) y += 15;
                if (drawGameModeTiers(subtiersNETProfile.diamond_smp, x, y)) y += 15;
                if (drawGameModeTiers(subtiersNETProfile.bow, x, y)) y += 15;
                if (drawGameModeTiers(subtiersNETProfile.bed, x, y)) y += 15;
                if (drawGameModeTiers(subtiersNETProfile.og_vanilla, x, y)) y += 15;
                drawGameModeTiers(subtiersNETProfile.trident, x, y);
            }
            default -> {}
        }
    }

    private boolean drawGameModeTiers(GameMode mode, int x, int y) {
        if (mode.drawn || mode.status == Status.SEARCHING || mode.displayedTier == Text.of("N/A"))
            return false;

        TextWidget icon = new TextWidget(mode.name.getIcon(), this.textRenderer);
        icon.setPosition(x, y + 3);
        this.addDrawableChild(icon);

        TextWidget label = new TextWidget(mode.name.getLabel(), this.textRenderer);
        label.setPosition(x + 20, y);
        this.addDrawableChild(label);

        TextWidget tier = new TextWidget(mode.displayedTier, this.textRenderer);
        tier.setPosition(x + 105 - (mode.displayedTier.getString().length() - 3) * 3, y);
        tier.setTooltip(Tooltip.of(mode.tierTooltip));
        this.addDrawableChild(tier);

        if (mode.displayedPeakTier != Text.of("N/A") && mode.peakTierTooltip.getStyle().getColor() != null) {
            TextWidget peakTier = new TextWidget(mode.displayedPeakTier, this.textRenderer);
            peakTier.setPosition(x + 142, y);
            peakTier.setTooltip(Tooltip.of(mode.peakTierTooltip));
            this.addDrawableChild(peakTier);

            TextWidget peakIcon = new TextWidget(Icons.peak, this.textRenderer);
            peakIcon.setPosition(x + 130, y);
            peakIcon.setTooltip(Tooltip.of(mode.peakTierTooltip));
            peakIcon.setTextColor(mode.peakTierTooltip.getStyle().getColor().getRgb());
            this.addDrawableChild(peakIcon);
        }

        mode.drawn = true;

        return true;
    }

    private void drawPlayerAvatar(DrawContext context, int x, int y) {
        if (playerAvatarTexture != null && imageReady)
            context.drawTexture(RenderLayer::getGuiTextured, playerAvatarTexture, x - width / 32, y, 0, 0, width / 16, (int) (width / 6.666), width / 16, (int) (width / 6.666));
        else if (playerProfile.imageSaved) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Loading " + playerProfile.name + "'s image..."), x, y + 20, 0x00dd00);
            loadPlayerAvatar();
        } else if (playerProfile.numberOfImageRequests == 5)
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(playerProfile.name + "'s image failed to load. Clear cache and retry"), x, y + 20, 0xdd0000);
    }

    private void loadPlayerAvatar() {
        File avatarFile = FabricLoader.getInstance().getConfigDir().resolve("tiers-cache/" + playerProfile.uuid + ".png").toFile();
        if (!avatarFile.exists())
            return;

        try (FileInputStream stream = new FileInputStream(avatarFile)) {
            playerAvatarTexture = Identifier.of("tiers-cache", playerProfile.uuid);
            MinecraftClient.getInstance().getTextureManager().registerTexture(playerAvatarTexture, new NativeImageBackedTexture(NativeImage.read(stream)));
            imageReady = true;
        } catch (IOException ignored) {}
    }

    @Override
    protected void init() {
        playerProfile.resetDrawnStatus();
    }
}