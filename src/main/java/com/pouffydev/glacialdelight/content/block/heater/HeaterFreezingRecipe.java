package com.pouffydev.glacialdelight.content.block.heater;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pouffydev.glacialdelight.init.GDRecipeTypes;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class HeaterFreezingRecipe extends AbstractCookingRecipe {
    public HeaterFreezingRecipe(ResourceLocation pId, String pGroup, Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime) {
        super(GDRecipeTypes.freezing.get(), pId, pGroup, pIngredient, pResult, pExperience, pCookingTime);
    }
    
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(Blocks.ICE);
    }
    
    public @NotNull RecipeSerializer<?> getSerializer() {
        return GDRecipeTypes.freezingSerializer.get();
    }
    public static class Serializer implements RecipeSerializer<HeaterFreezingRecipe> {
        public Serializer() {
        }
        
        public HeaterFreezingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            String s = GsonHelper.getAsString(pJson, "group", "");
            JsonElement jsonelement = GsonHelper.isArrayNode(pJson, "ingredient") ? GsonHelper.getAsJsonArray(pJson, "ingredient") : GsonHelper.getAsJsonObject(pJson, "ingredient");
            Ingredient ingredient = Ingredient.fromJson(jsonelement);
            //Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
            if (!pJson.has("result")) throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
            ItemStack itemstack;
            if (pJson.get("result").isJsonObject()) itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
            else {
                String s1 = GsonHelper.getAsString(pJson, "result");
                ResourceLocation resourcelocation = new ResourceLocation(s1);
                itemstack = new ItemStack(Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
                    return new IllegalStateException("Item: " + s1 + " does not exist");
                }));
            }
            float f = GsonHelper.getAsFloat(pJson, "experience", 0.0F);
            int i = GsonHelper.getAsInt(pJson, "cookingtime", 100);
            return new HeaterFreezingRecipe(pRecipeId, s, ingredient, itemstack, f, i);
        }
        
        public HeaterFreezingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String s = pBuffer.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            ItemStack itemstack = pBuffer.readItem();
            float f = pBuffer.readFloat();
            int i = pBuffer.readVarInt();
            return new HeaterFreezingRecipe(pRecipeId, s, ingredient, itemstack, f, i);
        }
        
        public void toNetwork(FriendlyByteBuf pBuffer, HeaterFreezingRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.group);
            pRecipe.ingredient.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeFloat(pRecipe.experience);
            pBuffer.writeVarInt(pRecipe.cookingTime);
        }
    }
}
