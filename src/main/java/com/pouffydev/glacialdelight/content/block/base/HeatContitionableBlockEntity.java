package com.pouffydev.glacialdelight.content.block.base;

import com.pouffydev.glacialdelight.content.block.heater.HeaterBlock;
import com.pouffydev.glacialdelight.content.block.util.HeaterLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static com.pouffydev.glacialdelight.content.block.heater.HeaterBlock.heatLevel;

public interface HeatContitionableBlockEntity {
    /**
     * Checks for heat sources below the block. If it can, it also checks for conducted heat.
     */
    default boolean isHeated(Level level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (stateBelow.hasProperty(heatLevel)) {
            HeaterLevel heaterLevel = stateBelow.getValue(heatLevel);
            if (heaterLevel.isAtLeast(HeaterLevel.SMOULDERING) && stateBelow.hasProperty(heatLevel)) {
                return true;
            }
            if (!this.requiresDirectHeat() && heaterLevel.isAtLeast(HeaterLevel.SMOULDERING)) {
                BlockState stateFurtherBelow = level.getBlockState(pos.below(2));
                HeaterLevel heaterLevelFurtherBelow = stateFurtherBelow.getValue(heatLevel);
                return heaterLevelFurtherBelow.isAtLeast(HeaterLevel.SMOULDERING);
            }
        }
        if (!stateBelow.hasProperty(heatLevel)) {
            level.getBlockState(pos).setValue(heatLevel, HeaterLevel.NONE);
        }
        
        
        return false;
    }
    default HeaterLevel getHeatLevel(Level level, BlockPos pos) {
        if (isFrozen(level, pos))
            return HeaterLevel.FREEZING;
        if (smouldering(level, pos))
            return HeaterLevel.SMOULDERING;
        if (kindled(level, pos))
            return HeaterLevel.KINDLED;
        if (seething(level, pos))
            return HeaterLevel.SEETHING;
        return HeaterLevel.NONE;
    }
    default boolean isFrozen(Level level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (stateBelow.hasProperty(heatLevel)) {
            HeaterLevel heaterLevel = stateBelow.getValue(heatLevel);
            if (heaterLevel == HeaterLevel.FREEZING) {
                level.getBlockState(pos).setValue(heatLevel, HeaterLevel.FREEZING);
            }
            return heaterLevel.equals(HeaterLevel.FREEZING);
        }
        return false;
    }
    default boolean smouldering(Level level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (stateBelow.hasProperty(heatLevel)) {
            HeaterLevel heaterLevel = stateBelow.getValue(heatLevel);
            if (heaterLevel == HeaterLevel.SMOULDERING) {
                level.getBlockState(pos).setValue(heatLevel, HeaterLevel.SMOULDERING);
            }
            return heaterLevel.equals(HeaterLevel.SMOULDERING);
        }
        return false;
    }
    default boolean kindled(Level level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (stateBelow.hasProperty(heatLevel)) {
            HeaterLevel heaterLevel = stateBelow.getValue(heatLevel);
            if (heaterLevel == HeaterLevel.KINDLED) {
                level.getBlockState(pos).setValue(heatLevel, HeaterLevel.KINDLED);
            }
            return heaterLevel.equals(HeaterLevel.KINDLED);
        }
        return false;
    }
    default boolean seething(Level level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (stateBelow.hasProperty(heatLevel)) {
            HeaterLevel heaterLevel = stateBelow.getValue(heatLevel);
            if (heaterLevel == HeaterLevel.SEETHING) {
                level.getBlockState(pos).setValue(heatLevel, HeaterLevel.SEETHING);
            }
            return heaterLevel.equals(HeaterLevel.SEETHING);
        }
        return false;
    }
    default boolean none(Level level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (stateBelow.hasProperty(heatLevel)) {
            HeaterLevel heaterLevel = stateBelow.getValue(heatLevel);
            if (heaterLevel == HeaterLevel.NONE) {
                level.getBlockState(pos).setValue(heatLevel, HeaterLevel.NONE);
            }
            return heaterLevel.equals(HeaterLevel.NONE);
        }
        return false;
    }
    
    /**
     * Determines if this block can only be heated directly, excluding conductors.
     */
    default boolean requiresDirectHeat() {
        return false;
    }
}
