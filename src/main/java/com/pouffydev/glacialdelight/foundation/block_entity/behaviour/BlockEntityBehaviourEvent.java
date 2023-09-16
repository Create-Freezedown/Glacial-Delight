package com.pouffydev.glacialdelight.foundation.block_entity.behaviour;

import java.lang.reflect.Type;
import com.pouffydev.glacialdelight.foundation.block_entity.SmartBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.GenericEvent;

import java.util.Map;

public class BlockEntityBehaviourEvent<T extends SmartBlockEntity> extends GenericEvent<T> {
    
    private T smartBlockEntity;
    private Map<BehaviourType<?>, BlockEntityBehaviour> behaviours;
    
    public BlockEntityBehaviourEvent(T blockEntity, Map<BehaviourType<?>, BlockEntityBehaviour> behaviours) {
        smartBlockEntity = blockEntity;
        this.behaviours = behaviours;
    }
    
    @Override
    public Type getGenericType() {
        return smartBlockEntity.getClass();
    }
    
    public void attach(BlockEntityBehaviour behaviour) {
        behaviours.put(behaviour.getType(), behaviour);
    }
    
    public BlockEntityBehaviour remove(BehaviourType<?> type) {
        return behaviours.remove(type);
    }
    
    public T getBlockEntity() {
        return smartBlockEntity;
    }
    
    public BlockState getBlockState() {
        return smartBlockEntity.getBlockState();
    }
    
}
