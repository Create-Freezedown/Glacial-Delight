package com.pouffydev.glacialdelight.init;

import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.content.block.base.HeatableBlockEntity;
import com.pouffydev.glacialdelight.content.block.heater.HeaterBlockEntity;
import com.pouffydev.glacialdelight.content.block.steamer.SteamerBlockEntity;
import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GDBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> tiles;
    public static final RegistryObject<BlockEntityType<HeaterBlockEntity>> heater;
    public static final RegistryObject<BlockEntityType<StewPotBlockEntity>> stewPot;
    public static final RegistryObject<BlockEntityType<SteamerBlockEntity>> steamer;
    public GDBlockEntities() {
    }
    
    static {
        tiles = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GlacialDelight.ID);
        heater = tiles.register("heater", () -> BlockEntityType.Builder.of(HeaterBlockEntity::new, new Block[]{GDBlocks.heater.get()}).build(null));
        stewPot = tiles.register("stew_pot", () -> BlockEntityType.Builder.of(StewPotBlockEntity::new, new Block[]{GDBlocks.stewPot.get()}).build(null));
        steamer = tiles.register("steamer", () -> BlockEntityType.Builder.of(SteamerBlockEntity::new, new Block[]{GDBlocks.steamer.get()}).build(null));
    }
}
