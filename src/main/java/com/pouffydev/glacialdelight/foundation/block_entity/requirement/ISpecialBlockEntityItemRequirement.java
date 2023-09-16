package com.pouffydev.glacialdelight.foundation.block_entity.requirement;

import net.minecraft.world.level.block.state.BlockState;

public interface ISpecialBlockEntityItemRequirement {
    public ItemRequirement getRequiredItems(BlockState state);
}
