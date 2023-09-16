package com.pouffydev.glacialdelight.foundation.block_entity.requirement;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface ISpecialBlockItemRequirement {
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity blockEntity);
}
