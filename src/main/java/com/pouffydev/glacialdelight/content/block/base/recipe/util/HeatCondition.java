package com.pouffydev.glacialdelight.content.block.base.recipe.util;

import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.block.util.HeaterLevel;
import com.pouffydev.glacialdelight.foundation.util.lang.Lang;

public enum HeatCondition {
    NONE(0x96a8a6), FREEZING(0x95b9b7), WARMED(0x97a0a9), HEATED(0xac9eae), SUPERHEATED(0xa89699),
    
    ;
    
    private int color;
    
    private HeatCondition(int color) {
        this.color = color;
    }
    
    public boolean testHeater(HeaterLevel level) {
        if (this == SUPERHEATED)
            return level == HeaterLevel.SEETHING;
        if (this == HEATED)
            return level == HeaterLevel.KINDLED;
        if (this == WARMED)
            return level == HeaterLevel.SMOULDERING;
        if (this == FREEZING)
            return level == HeaterLevel.FREEZING;
        if (this == NONE)
            return level == HeaterLevel.NONE;
        return true;
    }
    
    public HeaterLevel visualizeAsHeater() {
        if (this == SUPERHEATED)
            return HeaterLevel.SEETHING;
        if (this == HEATED)
            return HeaterLevel.KINDLED;
        if (this == WARMED)
            return HeaterLevel.SMOULDERING;
        if (this == FREEZING)
            return HeaterLevel.FREEZING;
        return HeaterLevel.NONE;
    }
    
    public String serialize() {
        return Lang.asId(name());
    }
    
    public String getTranslationKey() {
        return "recipe.heat_requirement." + serialize();
    }
    
    public static HeatCondition deserialize(String name) {
        for (HeatCondition heatCondition : values())
            if (heatCondition.serialize()
                    .equals(name))
                return heatCondition;
        GlacialDelight.LOGGER.warn("Tried to deserialize invalid heat condition: \"" + name + "\"");
        return NONE;
    }
    
    public int getColor() {
        return color;
    }
}
