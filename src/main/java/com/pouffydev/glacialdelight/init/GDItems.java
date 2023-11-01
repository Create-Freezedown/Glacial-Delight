package com.pouffydev.glacialdelight.init;

import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.crop.kindleberry.KindleberrySeedsItem;
import com.pouffydev.glacialdelight.content.item.salvage.SalvageTin;
import com.pouffydev.glacialdelight.foundation.util.GDFoodValues;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.registry.ModBlocks;

import java.util.Map;

import static com.pouffydev.glacialdelight.GlacialDelight.ID;

public class GDItems {
    public static final DeferredRegister<Item> items;
    
    public static final RegistryObject<Item> heater;
    public static final RegistryObject<Item> stewPot;
    public static final RegistryObject<Item> pastryBoard;
    //public static final RegistryObject<Item> steamer;
    
    public static final RegistryObject<SalvageTin> vegSalvageTin;
    public static final RegistryObject<Item> steamedCarrot;
    public static final RegistryObject<Item> smoulderBerry;
    public static final RegistryObject<Item> kindleBerry;
    public static final RegistryObject<Item> scorchBerry;
    public static final RegistryObject<Item> kindleBerrySeeds;
    public static final RegistryObject<Item> tallKindleBerrySeeds;
    public static final RegistryObject<Item> hungryKindleberrySeeds;
    public static final RegistryObject<Item> scorchberrySeeds;
    //BlockItem
    static {
        items = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
        heater = items.register("heater", () -> new BlockItem(GDBlocks.heater.get(), new Item.Properties().tab(GlacialDelight.tab)));
        stewPot = items.register("stew_pot", () -> new BlockItem(GDBlocks.stewPot.get(), new Item.Properties().tab(GlacialDelight.tab)));
        pastryBoard = items.register("pastry_board", () -> new BlockItem(GDBlocks.pastryBoard.get(), new Item.Properties().tab(GlacialDelight.tab)));
        //steamer = items.register("steamer", () -> new BlockItem(GDBlocks.steamer.get(), new Item.Properties().tab(GlacialDelight.tab)));
        
        //Kindleberry Seeds
        kindleBerrySeeds = items.register("kindleberry_seeds", () -> new KindleberrySeedsItem(GDBlocks.kindleberryBush.get(), new Item.Properties().tab(GlacialDelight.tab), 0));
        tallKindleBerrySeeds = items.register("tall_kindleberry_seeds", () -> new KindleberrySeedsItem(GDBlocks.kindleberryBush.get(), new Item.Properties().tab(GlacialDelight.tab), 1));
        hungryKindleberrySeeds = items.register("hungry_kindleberry_seeds", () -> new KindleberrySeedsItem(GDBlocks.kindleberryBush.get(), new Item.Properties().tab(GlacialDelight.tab), 2));
        scorchberrySeeds = items.register("scorchberry_seeds", () -> new KindleberrySeedsItem(GDBlocks.kindleberryBush.get(), new Item.Properties().tab(GlacialDelight.tab), 3));
    }
    //Foods
    static {
        steamedCarrot = items.register("steamed_carrot", () -> new Item(new Item.Properties().tab(GlacialDelight.tab).food(GDFoodValues.steamedCarrot)));
        smoulderBerry = items.register("smoulderberry", () -> new Item(new Item.Properties().tab(GlacialDelight.tab).food(GDFoodValues.smoulderBerry)));
        kindleBerry = items.register("kindleberry", () -> new Item(new Item.Properties().tab(GlacialDelight.tab).food(GDFoodValues.kindleBerry)));
        scorchBerry = items.register("scorchberry", () -> new Item(new Item.Properties().tab(GlacialDelight.tab).food(GDFoodValues.scorchBerry)));
    }
    //Salvage Items
    static {
        vegSalvageTin = items.register("veg_salvage_tin", () -> new SalvageTin(new Item.Properties().tab(GlacialDelight.tab), GDLootTables.vegetableTinSalvage));
    }
}
