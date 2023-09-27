package com.pouffydev.glacialdelight.foundation.util;

import net.minecraft.world.food.FoodProperties;

public class GDFoodValues {
    public static final int BRIEF_DURATION = 600;
    public static final int SHORT_DURATION = 1200;
    public static final int MEDIUM_DURATION = 3600;
    public static final int LONG_DURATION = 6000;
    public static final FoodProperties steamedCarrot = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.9F).build();
}
