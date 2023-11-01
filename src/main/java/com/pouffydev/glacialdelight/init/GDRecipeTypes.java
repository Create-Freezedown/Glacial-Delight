package com.pouffydev.glacialdelight.init;

import com.pouffydev.glacialdelight.content.block.heater.HeaterFreezingRecipe;
import com.pouffydev.glacialdelight.content.block.pastry_board.PastryBoardRecipe;
import com.pouffydev.glacialdelight.content.block.stew_pot.SeethingStewPotRecipe;
import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotRecipe;
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
    public static final RegistryObject<RecipeType<StewPotRecipe>> stewing;
    public static final RegistryObject<RecipeType<PastryBoardRecipe>> pastryMaking;
    public static final RegistryObject<StewPotRecipe.Serializer> stewingSerializer;
    public static final RegistryObject<PastryBoardRecipe.Serializer> pastryMakingSerializer;
    public static final RegistryObject<RecipeType<SeethingStewPotRecipe>> seethingStewing;
    public static final RegistryObject<SeethingStewPotRecipe.Serializer> seethingStewingSerializer;
    //public static final RegistryObject<RecipeType<SteamingRecipe>> steaming;
    //public static final RegistryObject<SteamingRecipe.Serializer> steamingSerializer;
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
        stewingSerializer = recipeSerializers.register("stewing", StewPotRecipe.Serializer::new);
        seethingStewingSerializer = recipeSerializers.register("seething_stewing", SeethingStewPotRecipe.Serializer::new);
        pastryMakingSerializer = recipeSerializers.register("pastry_making", PastryBoardRecipe.Serializer::new);
        //steamingSerializer = recipeSerializers.register("steaming", SteamingRecipe.Serializer::new);
    }
    static {
        recipeTypes = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ID);
        
        freezing = recipeTypes.register("heater_freezing", () -> {
            return registerRecipeType("heater_freezing");
        });
        stewing = recipeTypes.register("stewing", () -> {
            return registerRecipeType("stewing");
        });
        seethingStewing = recipeTypes.register("seething_stewing", () -> {
            return registerRecipeType("seething_stewing");
        });
        pastryMaking = recipeTypes.register("pastry_making", () -> {
            return registerRecipeType("pastry_making");
        });
        //steaming = recipeTypes.register("steaming", () -> {
        //    return registerRecipeType("steaming");
        //});
    }
}
