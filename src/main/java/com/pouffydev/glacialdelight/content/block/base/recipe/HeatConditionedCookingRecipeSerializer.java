package com.pouffydev.glacialdelight.content.block.base.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.pouffydev.glacialdelight.content.block.base.recipe.util.HeatCondition;
import com.pouffydev.glacialdelight.content.block.util.HeaterLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeatConditionedCookingRecipeSerializer <T extends HeatConditionedCookingRecipe<?>> implements RecipeSerializer<T> {
    
    protected void writeToJson(JsonObject json, T recipe) {
        JsonArray jsonIngredients = new JsonArray();
        JsonArray jsonOutputs = new JsonArray();
        recipe.getIngredients().forEach(i -> jsonIngredients.add(i.toJson()));
        recipe.results.forEach(o -> jsonOutputs.add(o.serialize()));
    
    }
    protected T readFromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            String groupIn = GsonHelper.getAsString(pJson, "group", "");
            NonNullList<Ingredient> inputItemsIn = readIngredients(GsonHelper.getAsJsonArray(pJson, "ingredients"));
            if (inputItemsIn.isEmpty()) {
                throw new JsonParseException("No ingredients for cooking recipe");
            } else if (inputItemsIn.size() > INPUT_SLOTS) {
                throw new JsonParseException("Too many ingredients for cooking recipe! The max is " + INPUT_SLOTS);
            } else {
                ItemStack outputIn = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pJson, "result"), true);
                ItemStack container = GsonHelper.isValidNode(pJson, "container") ? CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pJson, "container"), true) : ItemStack.EMPTY;
                float experienceIn = GsonHelper.getAsFloat(pJson, "experience", 0.0F);
                int cookTimeIn = GsonHelper.getAsInt(pJson, "cookingtime", 200);
                HeatCondition requiredHeatIn = HeaterLevel.findByName(GsonHelper.getAsString(pJson, "required_heat", "none"));
                return new HeatConditionedCookingRecipe(pRecipeId, groupIn, inputItemsIn, outputIn, container, experienceIn, cookTimeIn, requiredHeatIn);
            }
    }
    @Override
    public T fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
        return null;
    }
    
    @Override
    public @Nullable T fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        return null;
    }
    
    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
    
    }
}
