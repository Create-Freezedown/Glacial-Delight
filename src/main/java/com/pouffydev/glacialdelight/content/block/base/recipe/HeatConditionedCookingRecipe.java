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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModRecipeSerializers;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HeatConditionedCookingRecipe implements Recipe<RecipeWrapper> {
    public static final int INPUT_SLOTS = 6;
    private final ResourceLocation id;
    private final String group;
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ItemStack container;
    private final float experience;
    private final int cookTime;
    protected static HeatCondition requiredHeat;
    
    public HeatConditionedCookingRecipe(ResourceLocation id, String group, NonNullList<Ingredient> inputItems, ItemStack output, ItemStack container, float experience, int cookTime, HeatCondition requiredHeat) {
        this.id = id;
        this.group = group;
        this.inputItems = inputItems;
        this.output = output;
        if (!container.isEmpty()) {
            this.container = container;
        } else if (!output.getCraftingRemainingItem().isEmpty()) {
            this.container = output.getCraftingRemainingItem();
        } else {
            this.container = ItemStack.EMPTY;
        }
        
        this.experience = experience;
        this.cookTime = cookTime;
        this.requiredHeat = requiredHeat;
    }
    public ResourceLocation getId() {
        return this.id;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public NonNullList<Ingredient> getIngredients() {
        return this.inputItems;
    }
    
    public ItemStack getResultItem() {
        return this.output;
    }
    
    public ItemStack getOutputContainer() {
        return this.container;
    }
    
    public ItemStack assemble(RecipeWrapper inv) {
        return this.output.copy();
    }
    
    public float getExperience() {
        return this.experience;
    }
    
    public int getCookTime() {
        return this.cookTime;
    }
    
    public boolean matches(RecipeWrapper inv, Level level) {
        List<ItemStack> inputs = new ArrayList();
        int i = 0;
        
        for(int j = 0; j < 6; ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                inputs.add(itemstack);
            }
        }
        
        return i == this.inputItems.size() && RecipeMatcher.findMatches(inputs, this.inputItems) != null;
    }
    
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.inputItems.size();
    }
    
    public RecipeSerializer<?> getSerializer() {
        return (RecipeSerializer) ModRecipeSerializers.COOKING.get();
    }
    
    public RecipeType<?> getType() {
        return (RecipeType) ModRecipeTypes.COOKING.get();
    }
    
    public ItemStack getToastSymbol() {
        return new ItemStack((ItemLike) ModItems.COOKING_POT.get());
    }
    
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            HeatConditionedCookingRecipe that = (HeatConditionedCookingRecipe)o;
            if (Float.compare(that.getExperience(), this.getExperience()) != 0) {
                return false;
            } else if (this.getCookTime() != that.getCookTime()) {
                return false;
            } else if (!this.getId().equals(that.getId())) {
                return false;
            } else if (!this.getGroup().equals(that.getGroup())) {
                return false;
            } else if (!this.inputItems.equals(that.inputItems)) {
                return false;
            } else {
                return this.output.equals(that.output) && this.container.equals(that.container);
            }
        } else {
            return false;
        }
    }
    
    public int hashCode() {
        int result = this.getId().hashCode();
        result = 31 * result + this.getGroup().hashCode();
        result = 31 * result + this.inputItems.hashCode();
        result = 31 * result + this.output.hashCode();
        result = 31 * result + this.container.hashCode();
        result = 31 * result + (this.getExperience() != 0.0F ? Float.floatToIntBits(this.getExperience()) : 0);
        result = 31 * result + this.getCookTime();
        return result;
    }
    public HeatCondition getRequiredHeat() {
        return requiredHeat;
    }
    public static class Serializer implements RecipeSerializer<HeatConditionedCookingRecipe> {
        public Serializer() {
        }
        
        public @NotNull HeatConditionedCookingRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            String groupIn = GsonHelper.getAsString(json, "group", "");
            NonNullList<Ingredient> inputItemsIn = readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (inputItemsIn.isEmpty()) {
                throw new JsonParseException("No ingredients for cooking recipe");
            } else if (inputItemsIn.size() > INPUT_SLOTS) {
                throw new JsonParseException("Too many ingredients for cooking recipe! The max is " + INPUT_SLOTS);
            } else {
                
                ItemStack outputIn = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
                ItemStack container = GsonHelper.isValidNode(json, "container") ? CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "container"), true) : ItemStack.EMPTY;
                float experienceIn = GsonHelper.getAsFloat(json, "experience", 0.0F);
                int cookTimeIn = GsonHelper.getAsInt(json, "cookingtime", 200);
                String requiredHeatString = GsonHelper.getAsString(json, "required_heat", "warmed");
                HeatCondition requiredHeatIn = HeatCondition.valueOf(requiredHeatString);
                return new HeatConditionedCookingRecipe(recipeId, groupIn, inputItemsIn, outputIn, container, experienceIn, cookTimeIn, requiredHeatIn);
            }
        }
        
        private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();
            
            for(int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (!ingredient.isEmpty()) {
                    nonnulllist.add(ingredient);
                }
            }
            
            return nonnulllist;
        }
        
        @Nullable
        public HeatConditionedCookingRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String groupIn = buffer.readUtf();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(i, Ingredient.EMPTY);
            
            inputItemsIn.replaceAll(ignored -> Ingredient.fromNetwork(buffer));
            
            ItemStack outputIn = buffer.readItem();
            ItemStack container = buffer.readItem();
            float experienceIn = buffer.readFloat();
            int cookTimeIn = buffer.readVarInt();
            HeaterLevel requiredHeatIn = HeaterLevel.findByName(buffer.readUtf());
            return new HeatConditionedCookingRecipe(recipeId, groupIn, inputItemsIn, outputIn, container, experienceIn, cookTimeIn, requiredHeatIn);
        }
        
        public void toNetwork(FriendlyByteBuf buffer, HeatConditionedCookingRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.inputItems.size());
            
            for (Ingredient ingredient : recipe.inputItems) {
                ingredient.toNetwork(buffer);
            }
            
            buffer.writeItem(recipe.output);
            buffer.writeItem(recipe.container);
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.cookTime);
            buffer.writeUtf(requiredHeat.toString());
        }
    }
}
