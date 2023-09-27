package com.pouffydev.glacialdelight.content.integration.jei;

import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotMenu;
import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotScreen;
import com.pouffydev.glacialdelight.content.integration.jei.category.FreezingCategory;
import com.pouffydev.glacialdelight.content.integration.jei.category.StewingCategory;
import com.pouffydev.glacialdelight.init.GDItems;
import com.pouffydev.glacialdelight.init.GDMenuTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.client.gui.CookingPotScreen;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;
import vectorwing.farmersdelight.common.registry.ModMenuTypes;
import vectorwing.farmersdelight.integration.jei.FDRecipeTypes;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@JeiPlugin
public class GlacialJEI implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(GlacialDelight.ID, "jei_plugin");
    public GlacialJEI() {
    }
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new FreezingCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new StewingCategory(registry.getJeiHelpers().getGuiHelper()));
    }
    public void registerRecipes(IRecipeRegistration registration) {
        GDJEIRecipes modRecipes = new GDJEIRecipes();
        registration.addRecipes(GDJEIRecipeTypes.freezing, modRecipes.getFreezingRecipes());
        registration.addRecipes(GDJEIRecipeTypes.stewing, modRecipes.getStewingRecipes());
    }
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(GDItems.heater.get()), RecipeTypes.CAMPFIRE_COOKING, RecipeTypes.FUELING, GDJEIRecipeTypes.freezing);
        registration.addRecipeCatalyst(new ItemStack(GDItems.stewPot.get()), GDJEIRecipeTypes.stewing);
    }
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(StewPotScreen.class, 89, 25, 24, 17, GDJEIRecipeTypes.stewing);
    }
    
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(StewPotMenu.class, GDMenuTypes.stewPot.get(), GDJEIRecipeTypes.stewing, 0, 6, 9, 36);
    }
    public ResourceLocation getPluginUid() {
        return ID;
    }
}
