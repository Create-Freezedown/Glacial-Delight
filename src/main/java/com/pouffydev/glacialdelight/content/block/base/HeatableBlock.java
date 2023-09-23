package com.pouffydev.glacialdelight.content.block.base;

import com.pouffydev.glacialdelight.content.block.heater.HeaterBlockEntity;
import com.pouffydev.glacialdelight.content.block.util.HeaterLevel;
import com.pouffydev.glacialdelight.init.GDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class HeatableBlock extends FrostableBlock{
    public static final EnumProperty<HeaterLevel> heatLevel = EnumProperty.create("heat_level", HeaterLevel.class);
    
    protected HeatableBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(heatLevel, HeaterLevel.NONE).setValue(frozen, false));
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(heatLevel);
    }
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        return state.setValue(heatLevel, HeaterLevel.NONE).setValue(frozen, false);
    }
    @Override
    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }
    @Override
    public int getAnalogOutputSignal(BlockState state, Level p_180641_2_, BlockPos p_180641_3_) {
        return Math.max(0, state.getValue(heatLevel)
                .ordinal() - 1);
    }
    
}
