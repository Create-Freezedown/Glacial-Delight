package com.pouffydev.glacialdelight.foundation.client.events;

import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.block.heater.HeaterRenderer;
import com.pouffydev.glacialdelight.init.GDBlockEntities;
import com.pouffydev.glacialdelight.init.GDBlocks;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = GlacialDelight.ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)
public class ClientSetupEvents {
    public ClientSetupEvents() {
    }
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block blockColors) {
        blockColors.register((BlockColor) BlockColors.createDefault().getColoringProperties(Blocks.WATER_CAULDRON), GDBlocks.stewPot.get());
    }
    
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GDBlockEntities.heater.get(), HeaterRenderer::new);
    }
}
