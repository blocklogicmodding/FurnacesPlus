package com.blocklogic.furnacesplus.container.screen;

import com.blocklogic.furnacesplus.FurnacesPlus;
import com.blocklogic.furnacesplus.container.menu.FurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FurnaceScreen extends AbstractContainerScreen<FurnaceMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            FurnacesPlus.MODID, "textures/gui/furnace_gui.png");

    public FurnaceScreen(FurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 172;
        this.inventoryLabelY = this.imageHeight - 96;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        if (menu.isBurning()) {
            int burnProgress = menu.getBurnProgress();
            guiGraphics.blit(TEXTURE, x + 45, y + 37 + (14 - burnProgress),
                    176, 14 - burnProgress, 14, burnProgress);
        }

        int cookProgress = menu.getCookProgress();
        if (cookProgress > 0) {
            guiGraphics.blit(TEXTURE, x + 86, y + 38, 176, 14, cookProgress, 11);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);

        int relX = x - leftPos;
        int relY = y - topPos;

        if (relX >= 86 && relX <= 108 && relY >= 38 && relY <= 49) {
            int cookProgress = menu.getCookProgress();
            int maxProgress = 22;
            int percentage = maxProgress > 0 ? (cookProgress * 100) / maxProgress : 0;
            guiGraphics.renderTooltip(this.font,
                    Component.literal(percentage + "%"), x, y);
        }
    }
}