package com.pouffydev.glacialdelight.init;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.block.steamer.SteamingRecipe;
import com.pouffydev.glacialdelight.content.block.steamer.SteamingRecipeBookTab;
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
    public static final Supplier<RecipeBookCategories> STEAMING_SEARCH = Suppliers.memoize(() -> RecipeBookCategories.create("STEAMING_SEARCH", new ItemStack(Items.COMPASS)));
    public static final Supplier<RecipeBookCategories> STEAMING_VEGETABLES = Suppliers.memoize(() -> RecipeBookCategories.create("STEAMING_VEGETABLES", new ItemStack(ModItems.CABBAGE_LEAF.get())));
    public static final Supplier<RecipeBookCategories> STEAMING_MEATS = Suppliers.memoize(() -> RecipeBookCategories.create("STEAMING_MEATS", new ItemStack(ModItems.MUTTON_CHOPS.get())));
    public static final Supplier<RecipeBookCategories> STEAMING_BUNS = Suppliers.memoize(() -> RecipeBookCategories.create("STEAMING_BUNS", new ItemStack(Items.BREAD)));
    public static final Supplier<RecipeBookCategories> STEAMING_MISC = Suppliers.memoize(() -> RecipeBookCategories.create("STEAMING_MISC", new ItemStack(Items.BREAD), new ItemStack(ModItems.CABBAGE_LEAF.get())));
    //public static void steamingInit(RegisterRecipeBookCategoriesEvent event) {
    //    event.registerBookCategories(GlacialDelight.steamingRecipeType, ImmutableList.of(STEAMING_SEARCH.get(), STEAMING_VEGETABLES.get(), STEAMING_MEATS.get(), STEAMING_BUNS.get(), STEAMING_MISC.get()));
    //    event.registerAggregateCategory(STEAMING_SEARCH.get(), ImmutableList.of(STEAMING_VEGETABLES.get(), STEAMING_MEATS.get(), STEAMING_BUNS.get(), STEAMING_MISC.get()));
    //    event.registerRecipeCategoryFinder(GDRecipeTypes.steaming.get(), recipe ->
    //    {
    //        if (recipe instanceof SteamingRecipe steamingRecipe) {
    //            SteamingRecipeBookTab tab = steamingRecipe.getRecipeBookTab();
    //            if (tab != null) {
    //                return switch (tab) {
    //                    case vegetables -> STEAMING_VEGETABLES.get();
    //                    case meats -> STEAMING_MEATS.get();
    //                    case buns -> STEAMING_BUNS.get();
    //                    case misc -> STEWING_MISC.get();
    //                };
    //            }
    //        }
    //        return STEAMING_MISC.get();
    //    });
    //}
}
