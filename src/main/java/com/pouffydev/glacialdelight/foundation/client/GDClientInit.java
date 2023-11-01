package com.pouffydev.glacialdelight.foundation.client;

//import com.pouffydev.glacialdelight.content.block.steamer.SteamerScreen;
import com.pouffydev.glacialdelight.content.block.pastry_board.PastryBoardScreen;
import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotScreen;
import com.pouffydev.glacialdelight.init.GDBlocks;
import com.pouffydev.glacialdelight.init.GDMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
@SuppressWarnings("removal")
public class GDClientInit {
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(GDMenuTypes.stewPot.get(), StewPotScreen::new));
        event.enqueueWork(() -> MenuScreens.register(GDMenuTypes.pastryBoard.get(), PastryBoardScreen::new));
        //event.enqueueWork(() -> MenuScreens.register(GDMenuTypes.steamer.get(), SteamerScreen::new));
        
        ItemBlockRenderTypes.setRenderLayer(GDBlocks.stewPot.get(), RenderType.cutout());
    
    }
}
