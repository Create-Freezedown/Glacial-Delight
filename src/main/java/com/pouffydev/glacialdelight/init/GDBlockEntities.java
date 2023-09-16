package com.pouffydev.glacialdelight.init;

import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.block.heater.HeaterBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GDBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> tiles;
    public static final RegistryObject<BlockEntityType<HeaterBlockEntity>> heater;
    public GDBlockEntities() {
    }
    
    static {
        tiles = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GlacialDelight.ID);
        heater = tiles.register("heater", () -> BlockEntityType.Builder.of(HeaterBlockEntity::new, new Block[]{GDBlocks.heater.get()}).build(null));
    }
}
