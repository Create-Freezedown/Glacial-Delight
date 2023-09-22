package com.pouffydev.glacialdelight.init;

import com.pouffydev.glacialdelight.GlacialDelight;
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
    
    static {
        items = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
        heater = items.register("heater", () -> new BlockItem(GDBlocks.heater.get(), new Item.Properties().tab(GlacialDelight.tab)));
        stewPot = items.register("stew_pot", () -> new BlockItem(GDBlocks.stewPot.get(), new Item.Properties().tab(GlacialDelight.tab)));
    }
}
