package com.pouffydev.glacialdelight.content.integration.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.block.heater.HeaterFreezingRecipe;
import com.pouffydev.glacialdelight.content.integration.jei.GDJEIRecipeTypes;
import com.pouffydev.glacialdelight.foundation.util.lang.Lang;
import com.pouffydev.glacialdelight.init.GDItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.utility.ClientRenderUtils;
import vectorwing.farmersdelight.common.utility.TextUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FreezingCategory implements IRecipeCategory<HeaterFreezingRecipe> {
    protected final IDrawableAnimated arrow;
    private final IDrawable background;
    private final IDrawable icon;
    protected final IDrawable timeIcon;
    protected final IDrawable heatIndicator;
    private final Component title = Lang.translateDirect("jei.freezing");
    public FreezingCategory(IGuiHelper helper) {
        ResourceLocation backgroundImage = new ResourceLocation(GlacialDelight.ID, "textures/gui/freezing.png");
        this.background = helper.createDrawable(backgroundImage, 29, 16, 116, 56);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(GDItems.heater.get()));
        this.heatIndicator = helper.createDrawable(backgroundImage, 176, 0, 17, 15);
        this.timeIcon = helper.createDrawable(backgroundImage, 176, 32, 8, 11);
        this.arrow = helper.drawableBuilder(backgroundImage, 176, 15, 24, 17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }
    public RecipeType<HeaterFreezingRecipe> getRecipeType() {
        return GDJEIRecipeTypes.freezing;
    }
    
    public Component getTitle() {
        return this.title;
    }
    
    public IDrawable getBackground() {
        return this.background;
    }
    
    public IDrawable getIcon() {
        return this.icon;
    }
    public void setRecipe(IRecipeLayoutBuilder builder, HeaterFreezingRecipe recipe, IFocusGroup focusGroup) {
        NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();
        ItemStack resultStack = recipe.getResultItem();
        int borderSlotSize = 18;
            builder.addSlot(RecipeIngredientRole.INPUT, borderSlotSize + 1, borderSlotSize + 1).addItemStacks(Arrays.asList(((Ingredient)recipeIngredients.get(1)).getItems()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 10).addItemStack(resultStack);
    }
    
    public void draw(HeaterFreezingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
        this.arrow.draw(poseStack, 60, 9);
        this.heatIndicator.draw(poseStack, 18, 39);
        this.timeIcon.draw(poseStack, 64, 2);
    }
    
    public List<Component> getTooltipStrings(HeaterFreezingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (ClientRenderUtils.isCursorInsideBounds(61, 2, 22, 28, mouseX, mouseY)) {
            List<Component> tooltipStrings = new ArrayList<>();
            int cookTime = recipe.getCookingTime();
            if (cookTime > 0) {
                int cookTimeSeconds = cookTime / 20;
                tooltipStrings.add(Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds));
            }
            
            float experience = recipe.getExperience();
            if (experience > 0.0F) {
                tooltipStrings.add(Component.translatable("gui.jei.category.smelting.experience", experience));
            }
            
            return tooltipStrings;
        } else {
            return Collections.emptyList();
        }
    }
}
