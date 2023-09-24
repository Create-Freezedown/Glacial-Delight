package com.pouffydev.glacialdelight.content.block.base;

import com.pouffydev.glacialdelight.content.block.heater.HeaterBlock;
import com.pouffydev.glacialdelight.content.block.heater.HeaterBlockEntity;
import com.pouffydev.glacialdelight.content.block.util.GDBlockEntity;
import com.pouffydev.glacialdelight.content.block.util.HeaterLevel;
import com.pouffydev.glacialdelight.foundation.block_entity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static com.pouffydev.glacialdelight.content.block.heater.HeaterBlock.heatLevel;

public class HeatableBlockEntity extends GDBlockEntity implements HeatContitionableBlockEntity{
    public HeatableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
    public static void generalTick(Level level, BlockPos pos, BlockState state, HeatableBlockEntity be) {
        if (level.getBlockState(pos.below()).getBlock() instanceof HeaterBlock) {
            BlockState heater = level.getBlockEntity(pos.below()).getBlockState();
            HeaterLevel heatLevel = heater.getValue(HeaterBlock.heatLevel);
            if (heatLevel == HeaterLevel.FREEZING)
                level.setBlockAndUpdate(pos, state.setValue(HeatableBlock.heatLevel, HeaterLevel.FREEZING));
            else if (heatLevel == HeaterLevel.SMOULDERING)
                level.setBlockAndUpdate(pos, state.setValue(HeatableBlock.heatLevel, HeaterLevel.SMOULDERING));
            else if (heatLevel == HeaterLevel.KINDLED)
                level.setBlockAndUpdate(pos, state.setValue(HeatableBlock.heatLevel, HeaterLevel.KINDLED));
            else if (heatLevel == HeaterLevel.SEETHING)
                level.setBlockAndUpdate(pos, state.setValue(HeatableBlock.heatLevel, HeaterLevel.SEETHING));
            else if (heatLevel == HeaterLevel.NONE)
                level.setBlockAndUpdate(pos, state.setValue(HeatableBlock.heatLevel, HeaterLevel.NONE));
        }
    }
}
