package com.pouffydev.glacialdelight.content.block.stew_pot;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.block.util.HeaterLevel;
import com.pouffydev.glacialdelight.foundation.util.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.utility.TextUtils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StewPotScreen extends AbstractContainerScreen<StewPotMenu> implements RecipeUpdateListener
{
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(GlacialDelight.ID, "textures/gui/stew_pot.png");
    private static final Rectangle heatIcon = new Rectangle(47, 55, 17, 15);
    private static final Rectangle PROGRESS_ARROW = new Rectangle(89, 25, 0, 17);
    
    private final StewPotRecipeBookComponent recipeBookComponent = new StewPotRecipeBookComponent();
    private boolean widthTooNarrow;
    
    public StewPotScreen(StewPotMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }
    
    @Override
    public void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.titleLabelX = 28;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        if (Configuration.ENABLE_RECIPE_BOOK_COOKING_POT.get()) {
            this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (button) ->
            {
                this.recipeBookComponent.toggleVisibility();
                this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
                ((ImageButton) button).setPosition(this.leftPos + 5, this.height / 2 - 49);
            }));
        } else {
            this.recipeBookComponent.hide();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        }
        this.addWidget(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
    }
    
    @Override
    protected void containerTick() {
        super.containerTick();
        this.recipeBookComponent.tick();
    }
    
    @Override
    public void render(PoseStack ms, final int mouseX, final int mouseY, float partialTicks) {
        this.renderBackground(ms);
        
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(ms, partialTicks, mouseX, mouseY);
            this.recipeBookComponent.render(ms, mouseX, mouseY, partialTicks);
        } else {
            this.recipeBookComponent.render(ms, mouseX, mouseY, partialTicks);
            super.render(ms, mouseX, mouseY, partialTicks);
            this.recipeBookComponent.renderGhostRecipe(ms, this.leftPos, this.topPos, false, partialTicks);
        }
        
        this.renderMealDisplayTooltip(ms, mouseX, mouseY);
        this.renderHeatIndicatorTooltip(ms, mouseX, mouseY);
        this.recipeBookComponent.renderTooltip(ms, this.leftPos, this.topPos, mouseX, mouseY);
    }
    
    private void renderHeatIndicatorTooltip(PoseStack ms, int mouseX, int mouseY) {
        if (this.isHovering(heatIcon.x, heatIcon.y, heatIcon.width, heatIcon.height, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            String key = "container.cooking_pot." + this.menu.getHeatLevel().getSerializedName();
            tooltip.add(Lang.translateDirect(key, menu));
            this.renderComponentTooltip(ms, tooltip, mouseX, mouseY);
        }
    }
    
    protected void renderMealDisplayTooltip(PoseStack ms, int mouseX, int mouseY) {
        if (this.minecraft != null && this.minecraft.player != null && this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.hoveredSlot.index == 6) {
                List<Component> tooltip = new ArrayList<>();
                
                ItemStack mealStack = this.hoveredSlot.getItem();
                tooltip.add(((MutableComponent) mealStack.getItem().getDescription()).withStyle(mealStack.getRarity().color));
                
                ItemStack containerStack = this.menu.blockEntity.getContainer();
                String container = !containerStack.isEmpty() ? containerStack.getItem().getDescription().getString() : "";
                
                tooltip.add(TextUtils.getTranslation("container.cooking_pot.served_on", container).withStyle(ChatFormatting.GRAY));
                
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
        
        // Render progress arrow
        int l = this.menu.getCookProgressionScaled();
        this.blit(ms, this.leftPos + PROGRESS_ARROW.x, this.topPos + PROGRESS_ARROW.y, 176, 15, l + 1, PROGRESS_ARROW.height);
    }
    
    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(x, y, width, height, mouseX, mouseY);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
        if (this.recipeBookComponent.mouseClicked(mouseX, mouseY, buttonId)) {
            this.setFocused(this.recipeBookComponent);
            return true;
        }
        return this.widthTooNarrow && this.recipeBookComponent.isVisible() || super.mouseClicked(mouseX, mouseY, buttonId);
    }
    
    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int x, int y, int buttonIdx) {
        boolean flag = mouseX < (double) x || mouseY < (double) y || mouseX >= (double) (x + this.imageWidth) || mouseY >= (double) (y + this.imageHeight);
        return flag && this.recipeBookComponent.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, buttonIdx);
    }
    
    @Override
    protected void slotClicked(Slot slot, int mouseX, int mouseY, ClickType clickType) {
        super.slotClicked(slot, mouseX, mouseY, clickType);
        this.recipeBookComponent.slotClicked(slot);
    }
    
    @Override
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }
    
    @Override
    public void removed() {
        this.recipeBookComponent.removed();
        super.removed();
    }
    
    @Override
    @Nonnull
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }
}
