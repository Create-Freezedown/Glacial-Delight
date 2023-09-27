package com.pouffydev.glacialdelight.content.block.steamer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.init.GDItems;
import com.pouffydev.glacialdelight.init.GDRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SteamingRecipe implements Recipe<RecipeWrapper> {
    
    private final ResourceLocation id;
    private final String group;
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final float experience;
    private final int cookTime;
    
    public SteamingRecipe(ResourceLocation id, String group, NonNullList<Ingredient> inputItems, ItemStack output, float experience, int cookTime) {
        this.id = id;
        this.group = group;
        this.inputItems = inputItems;
        this.output = output;
        this.experience = experience;
        this.cookTime = cookTime;
    }
    @Override
    public ResourceLocation getId() {
        return this.id;
    }
    
    @Override
    public String getGroup() {
        return this.group;
    }
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.inputItems;
    }
    
    @Override
    public ItemStack getResultItem() {
        return this.output;
    }
    @Override
    public ItemStack assemble(RecipeWrapper inv) {
        return this.output.copy();
    }
    
    public float getExperience() {
        return this.experience;
    }
    
    public int getCookTime() {
        return this.cookTime;
    }
    
    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;
        
        for (int j = 0; j < 1; ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                inputs.add(itemstack);
            }
        }
        return i == this.inputItems.size() && net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, this.inputItems) != null;
    }
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.inputItems.size();
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return GDRecipeTypes.steamingSerializer.get();
    }
    
    @Override
    public RecipeType<?> getType() {
        return GDRecipeTypes.steaming.get();
    }
    
    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(GDItems.steamer.get());
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        SteamingRecipe that = (SteamingRecipe) o;
        
        if (Float.compare(that.getExperience(), getExperience()) != 0) return false;
        if (getCookTime() != that.getCookTime()) return false;
        if (!getId().equals(that.getId())) return false;
        if (!getGroup().equals(that.getGroup())) return false;
        if (!inputItems.equals(that.inputItems)) return false;
        return output.equals(that.output);
    }
    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getGroup().hashCode();
        result = 31 * result + inputItems.hashCode();
        result = 31 * result + output.hashCode();
        result = 31 * result + (getExperience() != +0.0f ? Float.floatToIntBits(getExperience()) : 0);
        result = 31 * result + getCookTime();
        return result;
    }
    public static class Serializer implements RecipeSerializer<SteamingRecipe>
    {
        public Serializer() {
        }
        
        @Override
        public SteamingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            final String groupIn = GsonHelper.getAsString(json, "group", "");
            final NonNullList<Ingredient> inputItemsIn = readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (inputItemsIn.isEmpty()) {
                throw new JsonParseException("No ingredients for steaming recipe");
            } else if (inputItemsIn.size() > 1) {
                throw new JsonParseException("Too many ingredients for cooking recipe! The max is " + 1);
            } else {
                final ItemStack outputIn = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
                final float experienceIn = GsonHelper.getAsFloat(json, "experience", 0.0F);
                final int cookTimeIn = GsonHelper.getAsInt(json, "steamingtime", 200);
                return new SteamingRecipe(recipeId, groupIn, inputItemsIn, outputIn, experienceIn, cookTimeIn);
            }
        }
        
        private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();
            
            for (int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (!ingredient.isEmpty()) {
                    nonnulllist.add(ingredient);
                }
            }
            
            return nonnulllist;
        }
        
        @Nullable
        @Override
        public SteamingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String groupIn = buffer.readUtf();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(i, Ingredient.EMPTY);
            
            for (int j = 0; j < inputItemsIn.size(); ++j) {
                inputItemsIn.set(j, Ingredient.fromNetwork(buffer));
            }
            
            ItemStack outputIn = buffer.readItem();
            float experienceIn = buffer.readFloat();
            int cookTimeIn = buffer.readVarInt();
            return new SteamingRecipe(recipeId, groupIn, inputItemsIn, outputIn, experienceIn, cookTimeIn);
        }
        
        @Override
        public void toNetwork(FriendlyByteBuf buffer, SteamingRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.inputItems.size());
            
            for (Ingredient ingredient : recipe.inputItems) {
                ingredient.toNetwork(buffer);
            }
            
            buffer.writeItem(recipe.output);
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.cookTime);
        }
    }
}
