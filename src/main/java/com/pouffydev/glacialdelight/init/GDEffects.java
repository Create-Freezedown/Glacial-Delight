package com.pouffydev.glacialdelight.init;

import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.crop.kindleberry.mutation.stage_two.ScorchingRootsBlock;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.awt.*;
import java.awt.color.ColorSpace;

public class GDEffects {
    public static final DeferredRegister<MobEffect> effects;
    public static final RegistryObject<MobEffect> scorching;
    
    static {
        effects = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, GlacialDelight.ID);
        
        scorching = effects.register("scorching",
                () -> new ScorchingRootsBlock.ScorchedEffect(MobEffectCategory.HARMFUL, 0x565dbd));
    }
}
