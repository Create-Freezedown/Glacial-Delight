package com.pouffydev.glacialdelight.init;

import com.pouffydev.glacialdelight.content.block.heater.HeaterBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.ToIntFunction;

import static com.pouffydev.glacialdelight.GlacialDelight.ID;

public class GDBlocks {
    // Create a Deferred Register to hold Blocks which will all be registered under the "glacialdelight" namespace
    public static final DeferredRegister<Block> blocks;
    public static final RegistryObject<HeaterBlock> heater;
    
    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return (state) -> {
            return (Boolean)state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
        };
    }
    private static ToIntFunction<BlockState> heaterEmission() {
        return HeaterBlock::getLight;
    }
    static {
        blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
        heater = blocks.register("heater", () -> new HeaterBlock(BlockBehaviour.Properties.of(Material.STONE).lightLevel(heaterEmission())));
    }
    
}
