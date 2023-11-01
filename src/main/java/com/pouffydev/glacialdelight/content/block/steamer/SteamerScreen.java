package com.pouffydev.glacialdelight.content.block.steamer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.block.util.HeaterLevel;
import com.pouffydev.glacialdelight.foundation.util.lang.Lang;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*public class SteamerScreen extends AbstractContainerScreen<SteamerMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(GlacialDelight.ID, "textures/gui/steamer.png");
    private static final Rectangle heatIcon = new Rectangle(138, 39, 17, 15);
    private static final Rectangle waterBar10 = new Rectangle(135, 28, 26, 1);
    private static final Rectangle waterBar9 = new Rectangle(135, 29, 26, 1);
    private static final Rectangle waterBar8 = new Rectangle(135, 30, 26, 1);
    private static final Rectangle waterBar7 = new Rectangle(135, 31, 26, 1);
    private static final Rectangle waterBar6 = new Rectangle(135, 32, 26, 1);
    private static final Rectangle waterBar5 = new Rectangle(135, 33, 26, 1);
    private static final Rectangle waterBar4 = new Rectangle(135, 34, 26, 1);
    private static final Rectangle waterBar3 = new Rectangle(135, 35, 26, 1);
    private static final Rectangle waterBar2 = new Rectangle(135, 36, 26, 1);
    private static final Rectangle waterBar1 = new Rectangle(135, 37, 26, 1);
    private static final Rectangle steamBar10 = new Rectangle(135, 15, 26, 1);
    private static final Rectangle steamBar9 = new Rectangle(135, 16, 26, 1);
    private static final Rectangle steamBar8 = new Rectangle(135, 17, 26, 1);
    private static final Rectangle steamBar7 = new Rectangle(135, 18, 26, 1);
    private static final Rectangle steamBar6 = new Rectangle(135, 19, 26, 1);
    private static final Rectangle steamBar5 = new Rectangle(135, 20, 26, 1);
    private static final Rectangle steamBar4 = new Rectangle(135, 21, 26, 1);
    private static final Rectangle steamBar3 = new Rectangle(135, 22, 26, 1);
    private static final Rectangle steamBar2 = new Rectangle(135, 23, 26, 1);
    private static final Rectangle steamBar1 = new Rectangle(135, 24, 26, 1);
    public SteamerScreen(SteamerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    private void renderHeatIndicatorTooltip(PoseStack ms, int mouseX, int mouseY) {
        if (this.isHovering(heatIcon.x, heatIcon.y, heatIcon.width, heatIcon.height, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            String key = "container.cooking_pot." + this.menu.getHeatLevel().getSerializedName();
            tooltip.add(Lang.translateDirect(key, menu));
            this.renderComponentTooltip(ms, tooltip, mouseX, mouseY);
        }
    }
    private void renderWaterBarIndicatorTooltip(PoseStack ms, int mouseX, int mouseY) {
        if (this.isHovering(135, 28, 24, 10, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            MutableComponent key = null;
            if (this.menu.waterLevel() == 0) {
                key = Lang.translateDirect("container.steamer.no_water");
            }
            if (this.menu.waterLevel() > 0) {
                key = Lang.translateDirect("container.steamer.water_level", this.menu.waterLevel());
            }
            tooltip.add(key);
            this.renderComponentTooltip(ms, tooltip, mouseX, mouseY);
        }
        
    }
    private void renderSteamBarIndicatorTooltip(PoseStack ms, int mouseX, int mouseY) {
        if (this.isHovering(135, 15, 24, 10, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            MutableComponent key = null;
            if (this.menu.steamLevel() == 0) {
                key = Lang.translateDirect("container.steamer.no_steam");
            }
            if (this.menu.steamLevel() > 0) {
                key = Lang.translateDirect("container.steamer.steam_level", this.menu.steamLevel());
            }
            tooltip.add(key);
            this.renderComponentTooltip(ms, tooltip, mouseX, mouseY);
        }
        
    }
    
    @Override
    protected void renderBg(PoseStack ms, float partialTicks, int mouseX, int mouseY) {
        // Render UI background
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.minecraft == null)
            return;
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        this.blit(ms, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        
        // Render heat icon
        if (this.menu.getHeatLevel() == HeaterLevel.SMOULDERING) {
            this.blit(ms, this.leftPos + heatIcon.x, this.topPos + heatIcon.y, 176, 0, heatIcon.width, heatIcon.height);
        }
        if (this.menu.getHeatLevel() == HeaterLevel.KINDLED) {
            this.blit(ms, this.leftPos + heatIcon.x, this.topPos + heatIcon.y, 193, 0, heatIcon.width, heatIcon.height);
        }
        if (this.menu.getHeatLevel() == HeaterLevel.SEETHING) {
            this.blit(ms, this.leftPos + heatIcon.x, this.topPos + heatIcon.y, 210, 0, heatIcon.width, heatIcon.height);
        }
        if (this.menu.getHeatLevel() == HeaterLevel.FREEZING) {
            this.blit(ms, this.leftPos + heatIcon.x, this.topPos + heatIcon.y, 227, 0, heatIcon.width, heatIcon.height);
        }
        // Render water bar
        if (this.menu.waterLevel() > 0 && this.menu.waterLevel() <= 100) {
            this.blit(ms, this.leftPos + waterBar1.x, this.topPos + waterBar1.y, 176, 34, waterBar1.width, waterBar1.height);
        }
        if (this.menu.waterLevel() > 100 && this.menu.waterLevel() <= 200) {
            this.blit(ms, this.leftPos + waterBar1.x, this.topPos + waterBar1.y, 176, 34, waterBar1.width, waterBar1.height);
            this.blit(ms, this.leftPos + waterBar2.x, this.topPos + waterBar2.y, 176, 33, waterBar2.width, waterBar2.height);
        }
        if (this.menu.waterLevel() > 200 && this.menu.waterLevel() <= 300) {
            this.blit(ms, this.leftPos + waterBar1.x, this.topPos + waterBar1.y, 176, 34, waterBar1.width, waterBar1.height);
            this.blit(ms, this.leftPos + waterBar2.x, this.topPos + waterBar2.y, 176, 33, waterBar2.width, waterBar2.height);
            this.blit(ms, this.leftPos + waterBar3.x, this.topPos + waterBar3.y, 176, 32, waterBar3.width, waterBar3.height);
        }
        if (this.menu.waterLevel() > 300 && this.menu.waterLevel() <= 400) {
            this.blit(ms, this.leftPos + waterBar1.x, this.topPos + waterBar1.y, 176, 34, waterBar1.width, waterBar1.height);
            this.blit(ms, this.leftPos + waterBar2.x, this.topPos + waterBar2.y, 176, 33, waterBar2.width, waterBar2.height);
            this.blit(ms, this.leftPos + waterBar3.x, this.topPos + waterBar3.y, 176, 32, waterBar3.width, waterBar3.height);
            this.blit(ms, this.leftPos + waterBar4.x, this.topPos + waterBar4.y, 176, 31, waterBar4.width, waterBar4.height);
        }
        if (this.menu.waterLevel() > 400 && this.menu.waterLevel() <= 500) {
            this.blit(ms, this.leftPos + waterBar1.x, this.topPos + waterBar1.y, 176, 34, waterBar1.width, waterBar1.height);
            this.blit(ms, this.leftPos + waterBar2.x, this.topPos + waterBar2.y, 176, 33, waterBar2.width, waterBar2.height);
            this.blit(ms, this.leftPos + waterBar3.x, this.topPos + waterBar3.y, 176, 32, waterBar3.width, waterBar3.height);
            this.blit(ms, this.leftPos + waterBar4.x, this.topPos + waterBar4.y, 176, 31, waterBar4.width, waterBar4.height);
            this.blit(ms, this.leftPos + waterBar5.x, this.topPos + waterBar5.y, 176, 30, waterBar5.width, waterBar5.height);
        }
        if (this.menu.waterLevel() > 500 && this.menu.waterLevel() <= 600) {
            this.blit(ms, this.leftPos + waterBar1.x, this.topPos + waterBar1.y, 176, 34, waterBar1.width, waterBar1.height);
            this.blit(ms, this.leftPos + waterBar2.x, this.topPos + waterBar2.y, 176, 33, waterBar2.width, waterBar2.height);
            this.blit(ms, this.leftPos + waterBar3.x, this.topPos + waterBar3.y, 176, 32, waterBar3.width, waterBar3.height);
            this.blit(ms, this.leftPos + waterBar4.x, this.topPos + waterBar4.y, 176, 31, waterBar4.width, waterBar4.height);
            this.blit(ms, this.leftPos + waterBar5.x, this.topPos + waterBar5.y, 176, 30, waterBar5.width, waterBar5.height);
            this.blit(ms, this.leftPos + waterBar6.x, this.topPos + waterBar6.y, 176, 29, waterBar6.width, waterBar6.height);
        }
        if (this.menu.waterLevel() > 600 && this.menu.waterLevel() <= 700) {
            this.blit(ms, this.leftPos + waterBar1.x, this.topPos + waterBar1.y, 176, 34, waterBar1.width, waterBar1.height);
            this.blit(ms, this.leftPos + waterBar2.x, this.topPos + waterBar2.y, 176, 33, waterBar2.width, waterBar2.height);
            this.blit(ms, this.leftPos + waterBar3.x, this.topPos + waterBar3.y, 176, 32, waterBar3.width, waterBar3.height);
            this.blit(ms, this.leftPos + waterBar4.x, this.topPos + waterBar4.y, 176, 31, waterBar4.width, waterBar4.height);
            this.blit(ms, this.leftPos + waterBar5.x, this.topPos + waterBar5.y, 176, 30, waterBar5.width, waterBar5.height);
            this.blit(ms, this.leftPos + waterBar6.x, this.topPos + waterBar6.y, 176, 29, waterBar6.width, waterBar6.height);
            this.blit(ms, this.leftPos + waterBar7.x, this.topPos + waterBar7.y, 176, 28, waterBar7.width, waterBar7.height);
        }
        if (this.menu.waterLevel() > 700 && this.menu.waterLevel() <= 800) {
            this.blit(ms, this.leftPos + waterBar1.x, this.topPos + waterBar1.y, 176, 34, waterBar1.width, waterBar1.height);
            this.blit(ms, this.leftPos + waterBar2.x, this.topPos + waterBar2.y, 176, 33, waterBar2.width, waterBar2.height);
            this.blit(ms, this.leftPos + waterBar3.x, this.topPos + waterBar3.y, 176, 32, waterBar3.width, waterBar3.height);
            this.blit(ms, this.leftPos + waterBar4.x, this.topPos + waterBar4.y, 176, 31, waterBar4.width, waterBar4.height);
            this.blit(ms, this.leftPos + waterBar5.x, this.topPos + waterBar5.y, 176, 30, waterBar5.width, waterBar5.height);
            this.blit(ms, this.leftPos + waterBar6.x, this.topPos + waterBar6.y, 176, 29, waterBar6.width, waterBar6.height);
            this.blit(ms, this.leftPos + waterBar7.x, this.topPos + waterBar7.y, 176, 28, waterBar7.width, waterBar7.height);
            this.blit(ms, this.leftPos + waterBar8.x, this.topPos + waterBar8.y, 176, 27, waterBar8.width, waterBar8.height);
        }
        if (this.menu.waterLevel() > 800 && this.menu.waterLevel() <= 900) {
            this.blit(ms, this.leftPos + waterBar1.x, this.topPos + waterBar1.y, 176, 34, waterBar1.width, waterBar1.height);
            this.blit(ms, this.leftPos + waterBar2.x, this.topPos + waterBar2.y, 176, 33, waterBar2.width, waterBar2.height);
            this.blit(ms, this.leftPos + waterBar3.x, this.topPos + waterBar3.y, 176, 32, waterBar3.width, waterBar3.height);
            this.blit(ms, this.leftPos + waterBar4.x, this.topPos + waterBar4.y, 176, 31, waterBar4.width, waterBar4.height);
            this.blit(ms, this.leftPos + waterBar5.x, this.topPos + waterBar5.y, 176, 30, waterBar5.width, waterBar5.height);
            this.blit(ms, this.leftPos + waterBar6.x, this.topPos + waterBar6.y, 176, 29, waterBar6.width, waterBar6.height);
            this.blit(ms, this.leftPos + waterBar7.x, this.topPos + waterBar7.y, 176, 28, waterBar7.width, waterBar7.height);
            this.blit(ms, this.leftPos + waterBar8.x, this.topPos + waterBar8.y, 176, 27, waterBar8.width, waterBar8.height);
            this.blit(ms, this.leftPos + waterBar9.x, this.topPos + waterBar9.y, 176, 26, waterBar9.width, waterBar9.height);
        }
        if (this.menu.waterLevel() == 1000) {
            this.blit(ms, this.leftPos + waterBar1.x, this.topPos + waterBar1.y, 176, 34, waterBar1.width, waterBar1.height);
            this.blit(ms, this.leftPos + waterBar2.x, this.topPos + waterBar2.y, 176, 33, waterBar2.width, waterBar2.height);
            this.blit(ms, this.leftPos + waterBar3.x, this.topPos + waterBar3.y, 176, 32, waterBar3.width, waterBar3.height);
            this.blit(ms, this.leftPos + waterBar4.x, this.topPos + waterBar4.y, 176, 31, waterBar4.width, waterBar4.height);
            this.blit(ms, this.leftPos + waterBar5.x, this.topPos + waterBar5.y, 176, 30, waterBar5.width, waterBar5.height);
            this.blit(ms, this.leftPos + waterBar6.x, this.topPos + waterBar6.y, 176, 29, waterBar6.width, waterBar6.height);
            this.blit(ms, this.leftPos + waterBar7.x, this.topPos + waterBar7.y, 176, 28, waterBar7.width, waterBar7.height);
            this.blit(ms, this.leftPos + waterBar8.x, this.topPos + waterBar8.y, 176, 27, waterBar8.width, waterBar8.height);
            this.blit(ms, this.leftPos + waterBar9.x, this.topPos + waterBar9.y, 176, 26, waterBar9.width, waterBar9.height);
            this.blit(ms, this.leftPos + waterBar10.x, this.topPos + waterBar10.y, 176, 25, waterBar10.width, waterBar10.height);
        }
        // Render steam bar
        if (this.menu.steamLevel() > 0 && this.menu.steamLevel() <= 100) {
            this.blit(ms, this.leftPos + steamBar1.x, this.topPos + steamBar1.y, 176, 24, steamBar1.width, steamBar1.height);
        }
        if (this.menu.steamLevel() > 100 && this.menu.steamLevel() <= 200) {
            this.blit(ms, this.leftPos + steamBar1.x, this.topPos + steamBar1.y, 176, 24, steamBar1.width, steamBar1.height);
            this.blit(ms, this.leftPos + steamBar2.x, this.topPos + steamBar2.y, 176, 23, steamBar2.width, steamBar2.height);
        }
        if (this.menu.steamLevel() > 200 && this.menu.steamLevel() <= 300) {
            this.blit(ms, this.leftPos + steamBar1.x, this.topPos + steamBar1.y, 176, 24, steamBar1.width, steamBar1.height);
            this.blit(ms, this.leftPos + steamBar2.x, this.topPos + steamBar2.y, 176, 23, steamBar2.width, steamBar2.height);
            this.blit(ms, this.leftPos + steamBar3.x, this.topPos + steamBar3.y, 176, 22, steamBar3.width, steamBar3.height);
        }
        if (this.menu.steamLevel() > 300 && this.menu.steamLevel() <= 400) {
            this.blit(ms, this.leftPos + steamBar1.x, this.topPos + steamBar1.y, 176, 24, steamBar1.width, steamBar1.height);
            this.blit(ms, this.leftPos + steamBar2.x, this.topPos + steamBar2.y, 176, 23, steamBar2.width, steamBar2.height);
            this.blit(ms, this.leftPos + steamBar3.x, this.topPos + steamBar3.y, 176, 22, steamBar3.width, steamBar3.height);
            this.blit(ms, this.leftPos + steamBar4.x, this.topPos + steamBar4.y, 176, 21, steamBar4.width, steamBar4.height);
        }
        if (this.menu.steamLevel() > 400 && this.menu.steamLevel() <= 500) {
            this.blit(ms, this.leftPos + steamBar1.x, this.topPos + steamBar1.y, 176, 24, steamBar1.width, steamBar1.height);
            this.blit(ms, this.leftPos + steamBar2.x, this.topPos + steamBar2.y, 176, 23, steamBar2.width, steamBar2.height);
            this.blit(ms, this.leftPos + steamBar3.x, this.topPos + steamBar3.y, 176, 22, steamBar3.width, steamBar3.height);
            this.blit(ms, this.leftPos + steamBar4.x, this.topPos + steamBar4.y, 176, 21, steamBar4.width, steamBar4.height);
            this.blit(ms, this.leftPos + steamBar5.x, this.topPos + steamBar5.y, 176, 20, steamBar5.width, steamBar5.height);
        }
        if (this.menu.steamLevel() > 500 && this.menu.steamLevel() <= 600) {
            this.blit(ms, this.leftPos + steamBar1.x, this.topPos + steamBar1.y, 176, 24, steamBar1.width, steamBar1.height);
            this.blit(ms, this.leftPos + steamBar2.x, this.topPos + steamBar2.y, 176, 23, steamBar2.width, steamBar2.height);
            this.blit(ms, this.leftPos + steamBar3.x, this.topPos + steamBar3.y, 176, 22, steamBar3.width, steamBar3.height);
            this.blit(ms, this.leftPos + steamBar4.x, this.topPos + steamBar4.y, 176, 21, steamBar4.width, steamBar4.height);
            this.blit(ms, this.leftPos + steamBar5.x, this.topPos + steamBar5.y, 176, 20, steamBar5.width, steamBar5.height);
            this.blit(ms, this.leftPos + steamBar6.x, this.topPos + steamBar6.y, 176, 19, steamBar6.width, steamBar6.height);
        }
        if (this.menu.steamLevel() > 600 && this.menu.steamLevel() <= 700) {
            this.blit(ms, this.leftPos + steamBar1.x, this.topPos + steamBar1.y, 176, 24, steamBar1.width, steamBar1.height);
            this.blit(ms, this.leftPos + steamBar2.x, this.topPos + steamBar2.y, 176, 23, steamBar2.width, steamBar2.height);
            this.blit(ms, this.leftPos + steamBar3.x, this.topPos + steamBar3.y, 176, 22, steamBar3.width, steamBar3.height);
            this.blit(ms, this.leftPos + steamBar4.x, this.topPos + steamBar4.y, 176, 21, steamBar4.width, steamBar4.height);
            this.blit(ms, this.leftPos + steamBar5.x, this.topPos + steamBar5.y, 176, 20, steamBar5.width, steamBar5.height);
            this.blit(ms, this.leftPos + steamBar6.x, this.topPos + steamBar6.y, 176, 19, steamBar6.width, steamBar6.height);
            this.blit(ms, this.leftPos + steamBar7.x, this.topPos + steamBar7.y, 176, 18, steamBar7.width, steamBar7.height);
        }
        if (this.menu.steamLevel() > 700 && this.menu.steamLevel() <= 800) {
            this.blit(ms, this.leftPos + steamBar1.x, this.topPos + steamBar1.y, 176, 24, steamBar1.width, steamBar1.height);
            this.blit(ms, this.leftPos + steamBar2.x, this.topPos + steamBar2.y, 176, 23, steamBar2.width, steamBar2.height);
            this.blit(ms, this.leftPos + steamBar3.x, this.topPos + steamBar3.y, 176, 22, steamBar3.width, steamBar3.height);
            this.blit(ms, this.leftPos + steamBar4.x, this.topPos + steamBar4.y, 176, 21, steamBar4.width, steamBar4.height);
            this.blit(ms, this.leftPos + steamBar5.x, this.topPos + steamBar5.y, 176, 20, steamBar5.width, steamBar5.height);
            this.blit(ms, this.leftPos + steamBar6.x, this.topPos + steamBar6.y, 176, 19, steamBar6.width, steamBar6.height);
            this.blit(ms, this.leftPos + steamBar7.x, this.topPos + steamBar7.y, 176, 18, steamBar7.width, steamBar7.height);
            this.blit(ms, this.leftPos + steamBar8.x, this.topPos + steamBar8.y, 176, 17, steamBar8.width, steamBar8.height);
        }
        if (this.menu.steamLevel() > 800 && this.menu.steamLevel() <= 900) {
            this.blit(ms, this.leftPos + steamBar1.x, this.topPos + steamBar1.y, 176, 24, steamBar1.width, steamBar1.height);
            this.blit(ms, this.leftPos + steamBar2.x, this.topPos + steamBar2.y, 176, 23, steamBar2.width, steamBar2.height);
            this.blit(ms, this.leftPos + steamBar3.x, this.topPos + steamBar3.y, 176, 22, steamBar3.width, steamBar3.height);
            this.blit(ms, this.leftPos + steamBar4.x, this.topPos + steamBar4.y, 176, 21, steamBar4.width, steamBar4.height);
            this.blit(ms, this.leftPos + steamBar5.x, this.topPos + steamBar5.y, 176, 20, steamBar5.width, steamBar5.height);
            this.blit(ms, this.leftPos + steamBar6.x, this.topPos + steamBar6.y, 176, 19, steamBar6.width, steamBar6.height);
            this.blit(ms, this.leftPos + steamBar7.x, this.topPos + steamBar7.y, 176, 18, steamBar7.width, steamBar7.height);
            this.blit(ms, this.leftPos + steamBar8.x, this.topPos + steamBar8.y, 176, 17, steamBar8.width, steamBar8.height);
            this.blit(ms, this.leftPos + steamBar9.x, this.topPos + steamBar9.y, 176, 16, steamBar9.width, steamBar9.height);
        }
        if (this.menu.steamLevel() == 1000) {
            this.blit(ms, this.leftPos + steamBar1.x, this.topPos + steamBar1.y, 176, 24, steamBar1.width, steamBar1.height);
            this.blit(ms, this.leftPos + steamBar2.x, this.topPos + steamBar2.y, 176, 23, steamBar2.width, steamBar2.height);
            this.blit(ms, this.leftPos + steamBar3.x, this.topPos + steamBar3.y, 176, 22, steamBar3.width, steamBar3.height);
            this.blit(ms, this.leftPos + steamBar4.x, this.topPos + steamBar4.y, 176, 21, steamBar4.width, steamBar4.height);
            this.blit(ms, this.leftPos + steamBar5.x, this.topPos + steamBar5.y, 176, 20, steamBar5.width, steamBar5.height);
            this.blit(ms, this.leftPos + steamBar6.x, this.topPos + steamBar6.y, 176, 19, steamBar6.width, steamBar6.height);
            this.blit(ms, this.leftPos + steamBar7.x, this.topPos + steamBar7.y, 176, 18, steamBar7.width, steamBar7.height);
            this.blit(ms, this.leftPos + steamBar8.x, this.topPos + steamBar8.y, 176, 17, steamBar8.width, steamBar8.height);
            this.blit(ms, this.leftPos + steamBar9.x, this.topPos + steamBar9.y, 176, 16, steamBar9.width, steamBar9.height);
            this.blit(ms, this.leftPos + steamBar10.x, this.topPos + steamBar10.y, 176, 15, steamBar10.width, steamBar10.height);
        }
    }
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        this.renderHeatIndicatorTooltip(ms, mouseX, mouseY);
        this.renderWaterBarIndicatorTooltip(ms, mouseX, mouseY);
        this.renderSteamBarIndicatorTooltip(ms, mouseX, mouseY);
    }
    
}

 */
