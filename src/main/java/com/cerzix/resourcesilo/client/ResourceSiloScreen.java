package com.cerzix.resourcesilo.client;

import com.cerzix.resourcesilo.menu.ResourceSiloMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ResourceSiloScreen extends AbstractContainerScreen<ResourceSiloMenu> {

    private static final ResourceLocation BG =
            new ResourceLocation("cerzixresourcesilo", "textures/gui/resource_silo.png");

    public ResourceSiloScreen(ResourceSiloMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = -1000; // hide default title text
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        int stored = this.menu.getStoredCount();

        Component line = Component.literal("Current Supplies: " + stored);
        int x = (this.imageWidth - this.font.width(line)) / 2;
        int y = 26; // moved down by 20px from original placement

        g.drawString(this.font, line, x, y, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        g.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);
        this.renderTooltip(g, mouseX, mouseY);
    }
}
