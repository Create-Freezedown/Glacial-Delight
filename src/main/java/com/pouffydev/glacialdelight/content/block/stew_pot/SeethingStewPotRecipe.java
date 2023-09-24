package com.pouffydev.glacialdelight.content.block.stew_pot;

import com.pouffydev.glacialdelight.init.GDRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;

public class SeethingStewPotRecipe extends StewPotRecipe
{
    public SeethingStewPotRecipe(ResourceLocation id, String group, @Nullable StewPotRecipeBookTab tab, NonNullList<Ingredient> inputItems, ItemStack output, ItemStack container, float experience, int cookTime) {
        super(id, group, tab, inputItems, output, container, experience, cookTime);
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return GDRecipeTypes.seethingStewingSerializer.get();
    }
    @Override
    public RecipeType<?> getType() {
        return GDRecipeTypes.seethingStewing.get();
    }
}
