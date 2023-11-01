package com.pouffydev.glacialdelight.content.block.pastry_board;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
import vectorwing.farmersdelight.common.registry.ModItems;

import javax.annotation.Nullable;

public class PastryBoardRecipe implements Recipe<RecipeWrapper>
{
    public static final int inputSlots = 3;
    public static final int bonusSlots = 2;
    private final ResourceLocation id;
    private final String group;
    private final NonNullList<Ingredient> inputItems;
    private final NonNullList<Ingredient> bonusItems;
    private final ItemStack output;
    private final ItemStack sugar;
    private final ItemStack pastry;
    private final float experience;
    
    public PastryBoardRecipe(ResourceLocation id, String group, NonNullList<Ingredient> inputItems,NonNullList<Ingredient> bonusItems, ItemStack output, ItemStack sugar, ItemStack pastry, float experience) {
        this.id = id;
        this.group = group;
        this.inputItems = inputItems;
        this.bonusItems = bonusItems;
        this.output = output;
        
        if (!sugar.isEmpty()) {
            this.sugar = sugar;
        } else if (!output.getCraftingRemainingItem().isEmpty()) {
            this.sugar = output.getCraftingRemainingItem();
        } else {
            this.sugar = ItemStack.EMPTY;
        }
        if (!pastry.isEmpty()) {
            this.pastry = pastry;
        } else if (!output.getCraftingRemainingItem().isEmpty()) {
            this.pastry = output.getCraftingRemainingItem();
        } else {
            this.pastry = ItemStack.EMPTY;
        }
        
        this.experience = experience;
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
    public NonNullList<Ingredient> getBonusIngredients() {
        return this.bonusItems;
    }
    
    @Override
    public ItemStack getResultItem() {
        return this.output;
    }
    
    public ItemStack getPastry() {
        return this.pastry;
    }
    public ItemStack getSugar() {
        return this.sugar;
    }
    @Override
    public ItemStack assemble(RecipeWrapper inv) {
        return this.output.copy();
    }
    
    public float getExperience() {
        return this.experience;
    }
    
    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;
        
        for (int j = 0; j < inputSlots; ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                inputs.add(itemstack);
            }
        }
        for (int j = 0; j < bonusSlots; ++j) {
            ItemStack itemstack = inv.getItem(j + inputSlots);
            if (!itemstack.isEmpty()) {
                ++i;
                inputs.add(itemstack);
            }
        }
        return i == this.inputItems.size() + this.bonusItems.size() && net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, this.inputItems) != null && net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, this.bonusItems) != null;
    }
    
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.inputItems.size() + this.bonusItems.size();
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return GDRecipeTypes.pastryMakingSerializer.get();
    }
    
    @Override
    public RecipeType<?> getType() {
        return GDRecipeTypes.pastryMaking.get();
    }
    
    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModItems.PIE_CRUST.get());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        PastryBoardRecipe that = (PastryBoardRecipe) o;
        
        if (Float.compare(that.getExperience(), getExperience()) != 0) return false;
        if (!getId().equals(that.getId())) return false;
        if (!getGroup().equals(that.getGroup())) return false;
        if (!inputItems.equals(that.inputItems)) return false;
        if (!bonusItems.equals(that.bonusItems)) return false;
        if (!output.equals(that.output)) return false;
        if (!sugar.equals(that.sugar)) return false;
        return pastry.equals(that.pastry);
    }
    
    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getGroup().hashCode();
        result = 31 * result + inputItems.hashCode();
        result = 31 * result + bonusItems.hashCode();
        result = 31 * result + output.hashCode();
        result = 31 * result + pastry.hashCode();
        result = 31 * result + sugar.hashCode();
        result = 31 * result + (getExperience() != +0.0f ? Float.floatToIntBits(getExperience()) : 0);
        return result;
    }
    
    public static class Serializer implements RecipeSerializer<PastryBoardRecipe>
    {
        public Serializer() {
        }
        
        @Override
        public PastryBoardRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            final String groupIn = GsonHelper.getAsString(json, "group", "");
            final NonNullList<Ingredient> inputItemsIn = readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            final NonNullList<Ingredient> bonusItemsIn = readIngredients(GsonHelper.getAsJsonArray(json, "bonus_ingredients"));
            if (inputItemsIn.isEmpty()) {
                throw new JsonParseException("No ingredients for cooking recipe");
            } else if (inputItemsIn.size() > PastryBoardRecipe.inputSlots) {
                throw new JsonParseException("Too many ingredients for pastry recipe! The max is " + PastryBoardRecipe.inputSlots);
            } else if (bonusItemsIn.isEmpty()) {
                throw new JsonParseException("No ingredients for cooking recipe");
            } else if (bonusItemsIn.size() > PastryBoardRecipe.bonusSlots) {
                throw new JsonParseException("Too many bonus ingredients for pastry recipe! The max is " + PastryBoardRecipe.bonusSlots);
            } else {
                final ItemStack outputIn = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
                ItemStack pastry = GsonHelper.isValidNode(json, "pastry") ? CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "pastry"), true) : ItemStack.EMPTY;
                ItemStack sugar = GsonHelper.isValidNode(json, "sugar") ? CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "sugar"), true) : ItemStack.EMPTY;
                final float experienceIn = GsonHelper.getAsFloat(json, "experience", 0.0F);
                return new PastryBoardRecipe(recipeId, groupIn, inputItemsIn, bonusItemsIn, outputIn, sugar, pastry, experienceIn);
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
        public PastryBoardRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String groupIn = buffer.readUtf();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(i, Ingredient.EMPTY);
            NonNullList<Ingredient> bonusItemsIn = NonNullList.withSize(i, Ingredient.EMPTY);
            
            for (int j = 0; j < inputItemsIn.size(); ++j) {
                inputItemsIn.set(j, Ingredient.fromNetwork(buffer));
            }
            for (int j = 0; j < bonusItemsIn.size(); ++j) {
                bonusItemsIn.set(j, Ingredient.fromNetwork(buffer));
            }
            
            ItemStack outputIn = buffer.readItem();
            ItemStack pastry = buffer.readItem();
            ItemStack sugar = buffer.readItem();
            float experienceIn = buffer.readFloat();
            return new PastryBoardRecipe(recipeId, groupIn, inputItemsIn, bonusItemsIn, outputIn, sugar, pastry, experienceIn);
        }
        
        @Override
        public void toNetwork(FriendlyByteBuf buffer, PastryBoardRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.inputItems.size());
            buffer.writeVarInt(recipe.bonusItems.size());
            
            for (Ingredient ingredient : recipe.inputItems) {
                ingredient.toNetwork(buffer);
            }
            for (Ingredient ingredient : recipe.bonusItems) {
                ingredient.toNetwork(buffer);
            }
            
            buffer.writeItem(recipe.output);
            buffer.writeItem(recipe.pastry);
            buffer.writeItem(recipe.sugar);
            buffer.writeFloat(recipe.experience);
        }
    }
}
