package com.pouffydev.glacialdelight.foundation.client;

import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotScreen;
import com.pouffydev.glacialdelight.init.GDBlocks;
import com.pouffydev.glacialdelight.init.GDMenuTypes;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
@SuppressWarnings("removal")
public class GDClientInit {
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(GDMenuTypes.stewPot.get(), StewPotScreen::new));
        
        ItemBlockRenderTypes.setRenderLayer(GDBlocks.stewPot.get(), RenderType.cutout());
    
    }
}
