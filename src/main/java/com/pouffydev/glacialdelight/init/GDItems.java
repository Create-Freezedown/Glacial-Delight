package com.pouffydev.glacialdelight.init;

import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.foundation.util.GDFoodValues;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.pouffydev.glacialdelight.GlacialDelight.ID;
import static com.pouffydev.glacialdelight.init.GDBlocks.heater;

public class GDItems {
    public static final DeferredRegister<Item> items;
    
    public static final RegistryObject<Item> heater;
    public static final RegistryObject<Item> stewPot;
    public static final RegistryObject<Item> steamer;
    
    public static final RegistryObject<Item> steamedCarrot;
    
    //BlockItem
    static {
        items = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
        heater = items.register("heater", () -> new BlockItem(GDBlocks.heater.get(), new Item.Properties().tab(GlacialDelight.tab)));
        stewPot = items.register("stew_pot", () -> new BlockItem(GDBlocks.stewPot.get(), new Item.Properties().tab(GlacialDelight.tab)));
        steamer = items.register("steamer", () -> new BlockItem(GDBlocks.steamer.get(), new Item.Properties().tab(GlacialDelight.tab)));
    }
    //Foods
    static {
        steamedCarrot = items.register("steamed_carrot", () -> new Item(new Item.Properties().tab(GlacialDelight.tab).food(GDFoodValues.steamedCarrot)));
    }
}
