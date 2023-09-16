package com.pouffydev.glacialdelight.foundation.block_entity.behaviour;

import net.minecraft.nbt.CompoundTag;

public interface IPartialSafeNBT {
    /** This method always runs on the logical server. */
    public void writeSafe(CompoundTag compound);
}
