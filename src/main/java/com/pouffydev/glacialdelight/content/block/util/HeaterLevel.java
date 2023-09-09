package com.pouffydev.glacialdelight.content.block.util;

import com.pouffydev.glacialdelight.foundation.util.lang.Lang;
import net.minecraft.util.StringRepresentable;

public enum HeaterLevel implements StringRepresentable {
    FREEZING,
    NONE,
    SMOULDERING,
    KINDLED,
    SEETHING;
    
    public static HeaterLevel byIndex(int index) {
        return values()[index];
    }
    
    public HeaterLevel nextActiveLevel() {
        return byIndex(ordinal() % (values().length - 1) + 1);
    }
    public static HeaterLevel findByName(String name) {
        for (HeaterLevel heatLevel : values()) {
            if (heatLevel.name().equals(name)) {
                return heatLevel;
            }
        }
        return null;
    }
    
    public boolean isAtLeast(HeaterLevel heatLevel) {
        return this.ordinal() >= heatLevel.ordinal();
    }
    
    @Override
    public String getSerializedName() {
        return Lang.asId(name());
    }
}
