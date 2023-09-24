package com.pouffydev.glacialdelight.init;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotRecipe;
import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotRecipeBookTab;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import vectorwing.farmersdelight.common.item.DrinkableItem;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.function.Supplier;

public class GDRecipeCategories {
    public static final Supplier<RecipeBookCategories> STEWING_SEARCH = Suppliers.memoize(() -> RecipeBookCategories.create("STEWING_SEARCH", new ItemStack(Items.COMPASS)));
    public static final Supplier<RecipeBookCategories> STEWING_STEWS = Suppliers.memoize(() -> RecipeBookCategories.create("STEWING_STEWS", new ItemStack(ModItems.BEEF_STEW.get())));
    public static final Supplier<RecipeBookCategories> STEWING_SOUPS = Suppliers.memoize(() -> RecipeBookCategories.create("STEWING_SOUPS", new ItemStack(ModItems.PUMPKIN_SOUP.get())));
    public static final Supplier<RecipeBookCategories> STEWING_DRINKS = Suppliers.memoize(() -> RecipeBookCategories.create("STEWING_DRINKS", new ItemStack(ModItems.MILK_BOTTLE.get())));
    public static final Supplier<RecipeBookCategories> STEWING_MISC = Suppliers.memoize(() -> RecipeBookCategories.create("STEWING_MISC", new ItemStack(ModItems.FISH_STEW.get()), new ItemStack(ModItems.VEGETABLE_SOUP.get())));
    
    public static void stewingInit(RegisterRecipeBookCategoriesEvent event) {
        event.registerBookCategories(GlacialDelight.stewingRecipeType, ImmutableList.of(STEWING_SEARCH.get(), STEWING_STEWS.get(), STEWING_SOUPS.get(), STEWING_DRINKS.get(), STEWING_MISC.get()));
        event.registerAggregateCategory(STEWING_SEARCH.get(), ImmutableList.of(STEWING_STEWS.get(), STEWING_SOUPS.get(), STEWING_DRINKS.get(), STEWING_MISC.get()));
        event.registerRecipeCategoryFinder(GDRecipeTypes.stewing.get(), recipe ->
        {
            if (recipe instanceof StewPotRecipe cookingRecipe) {
                StewPotRecipeBookTab tab = cookingRecipe.getRecipeBookTab();
                if (tab != null) {
                    return switch (tab) {
                        case stews -> STEWING_STEWS.get();
                        case soups -> STEWING_SOUPS.get();
                        case drinks -> STEWING_DRINKS.get();
                        case misc -> STEWING_MISC.get();
                    };
                }
            }
            
            // If no tab is specified in recipe, this fallback organizes them instead
            if (recipe.getResultItem().getItem() instanceof DrinkableItem) {
                return STEWING_DRINKS.get();
            }
            return STEWING_MISC.get();
        });
    }
}
