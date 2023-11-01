package com.pouffydev.glacialdelight.init;

import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.block.pastry_board.PastryBoardMenu;
import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GDMenuTypes {
    public static final DeferredRegister<MenuType<?>> menuTypes = DeferredRegister.create(ForgeRegistries.MENU_TYPES, GlacialDelight.ID);
    
    public static final RegistryObject<MenuType<StewPotMenu>> stewPot = menuTypes.register("stew_pot", () -> IForgeMenuType.create(StewPotMenu::new));
    public static final RegistryObject<MenuType<PastryBoardMenu>> pastryBoard = menuTypes.register("pastry_board", () -> IForgeMenuType.create(PastryBoardMenu::new));
    //public static final RegistryObject<MenuType<SteamerMenu>> steamer = menuTypes
    //        .register("steamer", () -> IForgeMenuType.create(SteamerMenu::new));
}
