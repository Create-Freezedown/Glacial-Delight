package com.pouffydev.glacialdelight.foundation.util;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class GDFoodValues {
    public static final int BRIEF_DURATION = 600;
    public static final int SHORT_DURATION = 1200;
    public static final int MEDIUM_DURATION = 3600;
    public static final int LONG_DURATION = 6000;
    public static final FoodProperties steamedCarrot = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.9F).build();
    public static final FoodProperties smoulderBerry = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.3F).effect(() -> {
        return new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0);
    }, 0.5F).build();
    public static final FoodProperties kindleBerry = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.4F).effect(() -> {
        return new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0);
    }, 0.75F).build();
    public static final FoodProperties scorchBerry = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.5F).effect(() -> {
        return new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0);
    }, 1.0F).build();
}
