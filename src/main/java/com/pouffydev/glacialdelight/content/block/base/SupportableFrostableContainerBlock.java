package com.pouffydev.glacialdelight.content.block.base;

import com.pouffydev.glacialdelight.content.block.util.HeaterLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import vectorwing.farmersdelight.common.block.state.CookingPotSupport;
import vectorwing.farmersdelight.common.tag.ModTags;

public class SupportableFrostableContainerBlock extends HeatableBlock implements SimpleWaterloggedBlock {
    public static final EnumProperty<CookingPotSupport> support;
    public static final BooleanProperty waterlogged;
    public static final DirectionProperty facing;
    
    protected SupportableFrostableContainerBlock(Properties pProperties) {
        super(pProperties);
    }
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        FluidState fluid = level.getFluidState(context.getClickedPos());
        BlockState state = (BlockState)((BlockState)this.defaultBlockState().setValue(facing, context.getHorizontalDirection().getOpposite())).setValue(waterlogged, fluid.getType() == Fluids.WATER).setValue(heatLevel, HeaterLevel.NONE).setValue(frozen, false);
        return context.getClickedFace().equals(Direction.DOWN) ? (BlockState)state.setValue(support, CookingPotSupport.HANDLE) : (BlockState)state.setValue(support, this.getTrayState(level, pos));
    }
    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(waterlogged)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (facing.getAxis().equals(Direction.Axis.Y) && !state.getValue(support).equals(CookingPotSupport.HANDLE)) {
            return state.setValue(support, getTrayState(level, currentPos));
        }
        return state;
    }
    protected CookingPotSupport getTrayState(LevelAccessor level, BlockPos pos) {
        if (level.getBlockState(pos.below()).is(ModTags.TRAY_HEAT_SOURCES)) {
            return CookingPotSupport.TRAY;
        }
        return CookingPotSupport.NONE;
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(facing, support, waterlogged, frozen, heatLevel);
    }
    static {
        facing = BlockStateProperties.HORIZONTAL_FACING;
        support = EnumProperty.create("support", CookingPotSupport.class);
        waterlogged = BlockStateProperties.WATERLOGGED;
    }
}
