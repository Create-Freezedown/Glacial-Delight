package com.pouffydev.glacialdelight.content.block.base;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

public interface LayerableBlock {
    public static final IntegerProperty layer = IntegerProperty.create("layer", 0, 1);
}
