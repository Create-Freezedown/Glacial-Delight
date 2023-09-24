package com.pouffydev.glacialdelight.content.integration.jei;

import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.integration.jei.category.FreezingCategory;
import com.pouffydev.glacialdelight.content.integration.jei.category.StewingCategory;
import com.pouffydev.glacialdelight.init.GDItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

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
    public ResourceLocation getPluginUid() {
        return ID;
    }
}
