package com.pouffydev.glacialdelight.content.block.base;

import com.pouffydev.glacialdelight.content.block.util.GDBlockEntity;
import com.pouffydev.glacialdelight.foundation.block_entity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class HeatableBlockEntity extends GDBlockEntity {
    public HeatableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
}
