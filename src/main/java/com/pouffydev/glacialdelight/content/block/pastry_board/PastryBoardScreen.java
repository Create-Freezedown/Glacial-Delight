package com.pouffydev.glacialdelight.content.block.pastry_board;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.pouffydev.glacialdelight.GlacialDelight;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.utility.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class PastryBoardScreen extends AbstractContainerScreen<PastryBoardMenu>
{
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(GlacialDelight.ID, "textures/gui/pastry_board.png");
    private boolean widthTooNarrow;
    
    public PastryBoardScreen(PastryBoardMenu screenContainer, Inventory inv, net.minecraft.network.chat.Component titleIn) {
        super(screenContainer, inv, titleIn);
    }
    
    @Override
    public void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.titleLabelX = 28;
    }
    
    @Override
    protected void containerTick() {
        super.containerTick();
    }
    
    @Override
    public void render(PoseStack ms, final int mouseX, final int mouseY, float partialTicks) {
        this.renderBackground(ms);
        
        this.renderMealDisplayTooltip(ms, mouseX, mouseY);
    }
    
    
    protected void renderMealDisplayTooltip(PoseStack ms, int mouseX, int mouseY) {
        if (this.minecraft != null && this.minecraft.player != null && this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.hoveredSlot.index == 6) {
                List<Component> tooltip = new ArrayList<>();
                
                ItemStack mealStack = this.hoveredSlot.getItem();
                tooltip.add(((MutableComponent) mealStack.getItem().getDescription()).withStyle(mealStack.getRarity().color));
                
                ItemStack containerStack = this.menu.blockEntity.getContainer();
                String pastry = !containerStack.isEmpty() ? containerStack.getItem().getDescription().getString() : "";
                
                tooltip.add(TextUtils.getTranslation("container.pastry_board.made_with", pastry).withStyle(ChatFormatting.GRAY));
                
                this.renderComponentTooltip(ms, tooltip, mouseX, mouseY);
            } else {
                this.renderTooltip(ms, this.hoveredSlot.getItem(), mouseX, mouseY);
            }
        }
    }
    
    @Override
    protected void renderLabels(PoseStack ms, int mouseX, int mouseY) {
        super.renderLabels(ms, mouseX, mouseY);
        this.font.draw(ms, this.playerInventoryTitle, 8.0f, (float) (this.imageHeight - 96 + 2), 4210752);
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
        // Render progress arrow
    }
    
    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!this.widthTooNarrow && super.isHovering(x, y, width, height, mouseX, mouseY));
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
        return this.widthTooNarrow || super.mouseClicked(mouseX, mouseY, buttonId);
    }
    
    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int x, int y, int buttonIdx) {
        boolean flag = mouseX < (double) x || mouseY < (double) y || mouseX >= (double) (x + this.imageWidth) || mouseY >= (double) (y + this.imageHeight);
        return flag;
    }
    
    @Override
    protected void slotClicked(Slot slot, int mouseX, int mouseY, ClickType clickType) {
        super.slotClicked(slot, mouseX, mouseY, clickType);
    }
}
