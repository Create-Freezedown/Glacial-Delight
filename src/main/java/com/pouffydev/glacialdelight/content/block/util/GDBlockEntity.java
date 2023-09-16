package com.pouffydev.glacialdelight.content.block.util;

import com.pouffydev.glacialdelight.foundation.block_entity.SmartBlockEntity;
import com.pouffydev.glacialdelight.foundation.block_entity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public abstract class GDBlockEntity extends SmartBlockEntity {
    
    public GDBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    public boolean isClient() {
        return this.getLevel() != null && this.getLevel().isClientSide;
    }
    
    /**
     * Marks the chunk dirty without performing comparator updates (twice!!) or block state checks
     * Used since most of our markDirty calls only adjust TE data
     */
    @SuppressWarnings("deprecation")
    public void setChangedFast() {
        if (level != null) {
            if (level.hasChunkAt(worldPosition)) {
                level.getChunkAt(worldPosition).setUnsaved(true);
            }
        }
    }
    
    
    /* Syncing */
    
    /**
     * If true, this TE syncs when {@link net.minecraft.world.level.Level#blockUpdated(BlockPos, Block) is called
     * TODO: right method?
     * Syncs data from {@link #saveSynced(CompoundTag)}
     */
    protected boolean shouldSyncOnUpdate() {
        return false;
    }
    
    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        // number is just used for vanilla, -1 ensures it skips all instanceof checks as its not a vanilla TE
        return shouldSyncOnUpdate() ? ClientboundBlockEntityDataPacket.create(this) : null;
    }
    
    /**
     * Write to NBT that is synced to the client in {@link #getUpdateTag()} and in {@link #saveAdditional(CompoundTag)}
     * @param nbt  NBT
     */
    protected void saveSynced(CompoundTag nbt) {}
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        saveSynced(nbt);
        return nbt;
    }
}
