package com.pouffydev.glacialdelight.content.integration.jei;

import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.block.heater.HeaterFreezingRecipe;
import mezz.jei.api.recipe.RecipeType;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

public class GDJEIRecipeTypes {
    public static final RecipeType<HeaterFreezingRecipe> freezing = RecipeType.create(GlacialDelight.ID, "heater_freezing", HeaterFreezingRecipe.class);
    public GDJEIRecipeTypes() {
    }
}
