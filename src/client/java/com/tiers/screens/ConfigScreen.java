package com.tiers.screens;

import com.tiers.TiersClient;
import com.tiers.misc.ColorControl;
import com.tiers.misc.Icons;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ConfigScreen extends Screen {
    private final Identifier MCTIERS_COM_IMAGE = Identifier.of("minecraft", "textures/mctiers_com_logo.png");
    private final Identifier MCTIERS_IO_IMAGE = Identifier.of("minecraft", "textures/mctiers_io_logo.png");
    private final Identifier SUBTIERS_NET_IMAGE = Identifier.of("minecraft", "textures/subtiers_net_logo.png");

    ButtonWidget toggleModWidget;
    ButtonWidget toggleShowIcons;
    ButtonWidget toggleSeparatorMode;
    ButtonWidget cycleDisplayMode;
    ButtonWidget mcTiersCOMPosition;
    ButtonWidget mcTiersIOPosition;
    ButtonWidget subtiersNETPosition;
    ButtonWidget activeMCTiersCOMMode;
    ButtonWidget activeMCTiersIOMode;
    ButtonWidget activeSubtiersNETMode;
    ButtonWidget clearPlayerCache;

    int centerX;
    int listY;
    int separator;
    int firstListX;
    int thirdListX;
    int distance;
    private static int updateButtons;

    public ConfigScreen() {
        super(Text.literal("Tiers Config"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        centerX = width / 2;
        listY = (int) (height / 2.8);
        separator = height / 23;
        firstListX = (int) (centerX - width / 5.1);
        thirdListX = (int) (centerX + width / 5.1);
        distance = (int) (height / 7.5);

        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Tiers config"), centerX, 10, ColorControl.getColor("text"));

        drawCategoryList(context, MCTIERS_COM_IMAGE, firstListX, listY);
        drawCategoryList(context, MCTIERS_IO_IMAGE, centerX, listY);
        drawCategoryList(context, SUBTIERS_NET_IMAGE, thirdListX, listY);

        context.drawTextWithShadow(this.textRenderer, Text.of("Enable Tiers"), centerX - 19, distance, ColorControl.getColor("text"));
        context.drawTextWithShadow(this.textRenderer, Text.of("Enable Icons"), centerX - 19, distance + separator, ColorControl.getColor("text"));
        context.drawTextWithShadow(this.textRenderer, Text.of("Dynamic Separator Color"), centerX - 48, distance + 2 * separator, ColorControl.getColor("text"));
        context.drawTextWithShadow(this.textRenderer, Text.of("Displayed Tier"), centerX - 25, distance + 3 * separator, ColorControl.getColor("text"));

        context.drawTextWithShadow(this.textRenderer, Text.of("Position"), firstListX - 7, listY + 2 * separator, ColorControl.getColor("text"));
        context.drawTextWithShadow(this.textRenderer, Text.of("Position"), centerX - 7, listY + 2 * separator, ColorControl.getColor("text"));
        context.drawTextWithShadow(this.textRenderer, Text.of("Position"), thirdListX - 7, listY + 2 * separator, ColorControl.getColor("text"));

        context.drawTextWithShadow(this.textRenderer, Text.of(TiersClient.activeMCTiersCOMMode.getIcon()), firstListX - 6, listY + 3 * separator + 4, ColorControl.getColor("text"));
        context.drawTextWithShadow(this.textRenderer, Text.of(TiersClient.activeMCTiersIOMode.getIcon()), centerX - 6, listY + 3 * separator + 4, ColorControl.getColor("text"));
        context.drawTextWithShadow(this.textRenderer, Text.of(TiersClient.activeSubtiersNETMode.getIcon()), thirdListX - 6, listY + 3 * separator + 4, ColorControl.getColor("text"));

        checkUpdates();
    }

    private void drawCategoryList(DrawContext context, Identifier image, int x, int y) {
        if (image == MCTIERS_COM_IMAGE)
            context.drawTexture(RenderLayer::getGuiTextured, image, x - 56, y + 5, 0, 0, 112, 21, 112, 21);
        else
            context.drawTexture(RenderLayer::getGuiTextured, image, x - 13, y, 0, 0, 26, 26, 26, 26);
    }

    private void checkUpdates() {
        toggleModWidget.setPosition(width / 2 - 40, distance - 4);
        toggleShowIcons.setPosition(width / 2 - 40, distance + separator - 4);
        toggleSeparatorMode.setPosition(width / 2 - 69, distance + 2 * separator - 4);
        cycleDisplayMode.setPosition(width / 2 - 46, distance + 3 * separator - 4);

        mcTiersCOMPosition.setPosition(firstListX - 28, listY + 2 * separator - 4);
        mcTiersIOPosition.setPosition(centerX - 28, listY + 2 * separator - 4);
        subtiersNETPosition.setPosition(thirdListX - 28, listY + 2 * separator - 4);

        activeMCTiersCOMMode.setPosition(firstListX - 28, listY + 3 * separator - 4);
        activeMCTiersIOMode.setPosition(centerX - 28, listY + 3 * separator - 4);
        activeSubtiersNETMode.setPosition(thirdListX - 28, listY + 3 * separator - 4);

        if (updateButtons == 0) {
            if (TiersClient.mcTiersIOPosition == TiersClient.mcTiersCOMPosition) {
                TiersClient.mcTiersIOPosition = TiersClient.DisplayStatus.OFF;
                mcTiersIOPosition.setMessage(Text.of(TiersClient.mcTiersIOPosition.getIcon()));
            }
            if (TiersClient.subtiersNETPosition == TiersClient.mcTiersCOMPosition) {
                TiersClient.subtiersNETPosition = TiersClient.DisplayStatus.OFF;
                subtiersNETPosition.setMessage(Text.of(TiersClient.subtiersNETPosition.getIcon()));
            }
            updateButtons = -1;
        } else if (updateButtons == 1) {
            if (TiersClient.mcTiersCOMPosition == TiersClient.mcTiersIOPosition) {
                TiersClient.mcTiersCOMPosition = TiersClient.DisplayStatus.OFF;
                mcTiersCOMPosition.setMessage(Text.of(TiersClient.mcTiersCOMPosition.getIcon()));
            }
            if (TiersClient.subtiersNETPosition == TiersClient.mcTiersIOPosition) {
                TiersClient.subtiersNETPosition = TiersClient.DisplayStatus.OFF;
                subtiersNETPosition.setMessage(Text.of(TiersClient.subtiersNETPosition.getIcon()));
            }
            updateButtons = -1;
        } else if (updateButtons == 2) {
            if (TiersClient.mcTiersCOMPosition == TiersClient.subtiersNETPosition) {
                TiersClient.mcTiersCOMPosition = TiersClient.DisplayStatus.OFF;
                mcTiersCOMPosition.setMessage(Text.of(TiersClient.mcTiersCOMPosition.getIcon()));
            }
            if (TiersClient.mcTiersIOPosition == TiersClient.subtiersNETPosition) {
                TiersClient.mcTiersIOPosition = TiersClient.DisplayStatus.OFF;
                mcTiersIOPosition.setMessage(Text.of(TiersClient.mcTiersIOPosition.getIcon()));
            }
            updateButtons = -1;
        }
    }

    @Override
    protected void init() {
        toggleModWidget = ButtonWidget.builder(Text.literal(TiersClient.toggleMod ? "✔" : " ").setStyle(Style.EMPTY.withColor(ColorControl.getColor("green"))), (buttonWidget) -> {
            TiersClient.toggleMod();
            buttonWidget.setMessage(Text.literal(TiersClient.toggleMod ? "✔" : " ").setStyle(Style.EMPTY.withColor(ColorControl.getColor("green"))));
        }).dimensions(width / 2 - 40, distance - 4, 16, 16).build();
        toggleModWidget.setTooltip(Tooltip.of(Text.of("✔ - Mod is enabled")));

        toggleShowIcons = ButtonWidget.builder(Text.literal(TiersClient.showIcons ? "✔" : " ").setStyle(Style.EMPTY.withColor(ColorControl.getColor("green"))), (buttonWidget) -> {
            TiersClient.toggleShowIcons();
            buttonWidget.setMessage(Text.literal(TiersClient.showIcons ? "✔" : " ").setStyle(Style.EMPTY.withColor(ColorControl.getColor("green"))));
        }).dimensions(width / 2 - 40, distance + separator - 4, 16, 16).build();
        toggleShowIcons.setTooltip(Tooltip.of(Text.of("✔ - Icons will be showed next to tier")));

        toggleSeparatorMode = ButtonWidget.builder(Text.literal(TiersClient.isSeparatorAdaptive ? "✔" : " ").setStyle(Style.EMPTY.withColor(ColorControl.getColor("green"))), (buttonWidget) -> {
            TiersClient.toggleSeparatorAdaptive();
            buttonWidget.setMessage(Text.literal(TiersClient.isSeparatorAdaptive ? "✔" : " ").setStyle(Style.EMPTY.withColor(ColorControl.getColor("green"))));
        }).dimensions(width / 2 - 69, distance + 2 * separator - 4, 16, 16).build();
        toggleSeparatorMode.setTooltip(Tooltip.of(Text.of("✔ - The separator will be the same color as the tier instead of gray")));

        cycleDisplayMode = ButtonWidget.builder(Text.of(TiersClient.displayMode.getIcon()), (buttonWidget) -> {
            TiersClient.cycleDisplayMode();
            buttonWidget.setMessage(Text.of(TiersClient.displayMode.getIcon()));
        }).dimensions(width / 2 - 46, distance + 3 * separator - 4, 16, 16).build();
        cycleDisplayMode.setTooltip(Tooltip.of(Text.of("""
                ● - Selected tier: only the selected tier will be displayed
                ↑ - Highest: only the highest tier will be displayed
                ↓ - Adaptive Highest: the highest tier will be displayed if selected does not exist""")));

        mcTiersCOMPosition = ButtonWidget.builder(Text.of(TiersClient.mcTiersCOMPosition.getIcon()), (buttonWidget) -> {
            TiersClient.cycleMCTiersCOMPosition();
            updateButtons = 0;
            buttonWidget.setMessage(Text.of(TiersClient.mcTiersCOMPosition.getIcon()));
        }).dimensions(firstListX - 28, listY + 2 * separator - 4, 16, 16).build();

        mcTiersIOPosition = ButtonWidget.builder(Text.of(TiersClient.mcTiersIOPosition.getIcon()), (buttonWidget) -> {
            TiersClient.cycleMCTiersIOPosition();
            updateButtons = 1;
            buttonWidget.setMessage(Text.of(TiersClient.mcTiersIOPosition.getIcon()));
        }).dimensions(centerX - 28, listY + 2 * separator - 4, 16, 16).build();

        subtiersNETPosition = ButtonWidget.builder(Text.of(TiersClient.subtiersNETPosition.getIcon()), (buttonWidget) -> {
            TiersClient.cycleSubtiersNETPosition();
            updateButtons = 2;
            buttonWidget.setMessage(Text.of(TiersClient.subtiersNETPosition.getIcon()));
        }).dimensions(thirdListX - 28, listY + 2 * separator - 4, 16, 16).build();

        mcTiersCOMPosition.setTooltip(Tooltip.of(Text.of("""
                → - The tier will be displayed on the right of the nametag
                ← - The tier will be displayed on the left of the nametag
                ● - Off""")));
        mcTiersIOPosition.setTooltip(Tooltip.of(Text.of("""
                → - The tier will be displayed on the right of the nametag
                ← - The tier will be displayed on the left of the nametag
                ● - Off""")));
        subtiersNETPosition.setTooltip(Tooltip.of(Text.of("""
                → - The tier will be displayed on the right of the nametag
                ← - The tier will be displayed on the left of the nametag
                ● - Off""")));

        activeMCTiersCOMMode = ButtonWidget.builder(Icons.cycle, (buttonWidget) -> TiersClient.cycleMCTiersCOMMode()).dimensions(firstListX - 28, listY + 3 * separator - 4, 16, 16).build();
        activeMCTiersCOMMode.setTooltip(Tooltip.of(Text.of("Cycle active MCTiers.com gamemode")));

        activeMCTiersIOMode = ButtonWidget.builder(Icons.cycle, (buttonWidget) -> TiersClient.cycleMCTiersIOMode()).dimensions(centerX - 28, listY + 3 * separator - 4, 16, 16).build();
        activeMCTiersIOMode.setTooltip(Tooltip.of(Text.of("Cycle active MCTiers.io gamemode")));

        activeSubtiersNETMode = ButtonWidget.builder(Icons.cycle, (buttonWidget) -> TiersClient.cycleSubtiersNETMode()).dimensions(thirdListX - 28, listY + 3 * separator - 4, 16, 16).build();
        activeSubtiersNETMode.setTooltip(Tooltip.of(Text.of("Cycle active Subtiers gamemode")));

        clearPlayerCache = ButtonWidget.builder(Text.of("\uD83D\uDDD1"), (buttonWidget) -> TiersClient.clearCache()).dimensions(width - 20, height - 20, 16, 16).build();
        clearPlayerCache.setTooltip(Tooltip.of(Text.of("Clear player cache")));

        this.addDrawableChild(toggleModWidget);
        this.addDrawableChild(toggleShowIcons);
        this.addDrawableChild(toggleSeparatorMode);
        this.addDrawableChild(cycleDisplayMode);
        this.addDrawableChild(mcTiersCOMPosition);
        this.addDrawableChild(mcTiersIOPosition);
        this.addDrawableChild(subtiersNETPosition);
        this.addDrawableChild(activeMCTiersCOMMode);
        this.addDrawableChild(activeMCTiersIOMode);
        this.addDrawableChild(activeSubtiersNETMode);
        this.addDrawableChild(clearPlayerCache);
    }

    public static Screen getConfigScreen(Screen ignoredScreen) {
        return new ConfigScreen();
    }
}