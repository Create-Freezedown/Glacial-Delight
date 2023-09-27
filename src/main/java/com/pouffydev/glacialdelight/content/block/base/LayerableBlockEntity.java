package com.pouffydev.glacialdelight.content.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Small Interface that checks for a block's "layer" blockState property for recipe and GUI interaction.
 */
public interface LayerableBlockEntity {
    default int getLayer(Level level, BlockPos pos) {
        return level.getBlockState(pos).getValue(LayerableBlock.layer);
    }
}
