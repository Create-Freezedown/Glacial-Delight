package com.pouffydev.glacialdelight.foundation.client;

import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotScreen;
import com.pouffydev.glacialdelight.init.GDMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class GDClientInit {
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(GDMenuTypes.stewPot.get(), StewPotScreen::new));
    
    }
}
