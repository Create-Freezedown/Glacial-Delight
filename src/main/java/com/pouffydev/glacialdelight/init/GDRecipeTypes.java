package com.pouffydev.glacialdelight.init;

import com.pouffydev.glacialdelight.content.block.heater.HeaterFreezingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.pouffydev.glacialdelight.GlacialDelight.ID;

public class GDRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> recipeTypes;
    public static final DeferredRegister<RecipeSerializer<?>> recipeSerializers;
    public static final RegistryObject<RecipeType<HeaterFreezingRecipe>> freezing;
    public static final RegistryObject<HeaterFreezingRecipe.Serializer> freezingSerializer;
    public static <T extends Recipe<?>> RecipeType<T> registerRecipeType(final String identifier) {
        return new RecipeType<T>() {
            public String toString() {
                return ID + ":" + identifier;
            }
        };
    }
    static {
        recipeSerializers = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ID);
        
        freezingSerializer = recipeSerializers.register("heater_freezing", HeaterFreezingRecipe.Serializer::new);
    }
    static {
        recipeTypes = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ID);
        
        freezing = recipeTypes.register("heater_freezing", () -> {
            return registerRecipeType("heater_freezing");
        });
    }
}
