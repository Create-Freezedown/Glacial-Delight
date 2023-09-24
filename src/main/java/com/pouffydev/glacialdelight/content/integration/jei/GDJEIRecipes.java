package com.pouffydev.glacialdelight.content.integration.jei;

import com.pouffydev.glacialdelight.content.block.heater.HeaterFreezingRecipe;
import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotRecipe;
import com.pouffydev.glacialdelight.init.GDRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class GDJEIRecipes {
    private final RecipeManager recipeManager;
    
    public GDJEIRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level != null) {
            this.recipeManager = level.getRecipeManager();
        } else {
            throw new NullPointerException("minecraft world must not be null.");
        }
    }
    
    public List<HeaterFreezingRecipe> getFreezingRecipes() {
        return this.recipeManager.getAllRecipesFor(GDRecipeTypes.freezing.get()).stream().toList();
    }
    public List<StewPotRecipe> getStewingRecipes() {
        ArrayList<StewPotRecipe> stewPotRecipes = new ArrayList<>();
        stewPotRecipes.addAll(this.recipeManager.getAllRecipesFor(GDRecipeTypes.stewing.get()));
        stewPotRecipes.addAll(this.recipeManager.getAllRecipesFor(GDRecipeTypes.seethingStewing.get()));
        return stewPotRecipes.stream().toList();
    }
}
