package com.pouffydev.glacialdelight.foundation.client.events;

import com.pouffydev.glacialdelight.content.block.heater.HeaterRenderer;
import com.pouffydev.glacialdelight.init.GDBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = "glacialdelight",
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)
public class ClientSetupEvents {
    public ClientSetupEvents() {
    }
    
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GDBlockEntities.heater.get(), HeaterRenderer::new);
    }
}
