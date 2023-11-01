package com.pouffydev.glacialdelight.init;

import com.pouffydev.glacialdelight.content.block.heater.HeaterBlock;
import com.pouffydev.glacialdelight.content.block.pastry_board.PastryBoardBlock;
import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotBlock;
import com.pouffydev.glacialdelight.content.crop.kindleberry.KindleberryBushBlock;
import com.pouffydev.glacialdelight.content.crop.kindleberry.mutation.stage_two.ScorchingRootsBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import umpaz.farmersrespite.common.block.WitherRootsBlock;

import java.util.function.ToIntFunction;

import static com.pouffydev.glacialdelight.GlacialDelight.ID;

public class GDBlocks {
    // Create a Deferred Register to hold Blocks which will all be registered under the "glacialdelight" namespace
    public static final DeferredRegister<Block> blocks;
    public static final RegistryObject<HeaterBlock> heater;
    public static final RegistryObject<StewPotBlock> stewPot;
    public static final RegistryObject<PastryBoardBlock> pastryBoard;
    //public static final RegistryObject<SteamerBlock> steamer;
    public static final RegistryObject<Block> kindleberryBush;
    public static final RegistryObject<Block> scorchingRoots;
    public static final RegistryObject<Block> scorchingRootsPlant;
    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return (state) -> {
            return (Boolean)state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
        };
    }
    private static ToIntFunction<BlockState> kindleBerryEmission(int lightValue) {
        return (state) -> {
            return state.getValue(KindleberryBushBlock.bushAge) == 3 ?  lightValue : 0;
        };
    }
    private static ToIntFunction<BlockState> heaterEmission() {
        return HeaterBlock::getLight;
    }
    static {
        blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
        heater = blocks.register("heater", () -> new HeaterBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL).lightLevel(heaterEmission())));
        stewPot = blocks.register("stew_pot", () -> new StewPotBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL)));
        pastryBoard = blocks.register("pastry_board", () -> new PastryBoardBlock(BlockBehaviour.Properties.of(Material.WOOD)));
        //steamer = blocks.register("steamer", () -> new SteamerBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL)));
    }
    
    //Crops
    static {
        kindleberryBush = blocks.register("kindleberry_bush", () -> new KindleberryBushBlock(BlockBehaviour.Properties.of(Material.PLANT).sound(SoundType.GRASS).randomTicks().instabreak().noOcclusion().lightLevel(kindleBerryEmission(3))));
        scorchingRoots = blocks.register("scorching_roots", () -> {
            return new ScorchingRootsBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ));
        });
        scorchingRootsPlant= blocks.register("scorching_roots_plant", () -> {
            return new ScorchingRootsBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ));
        });
    }
    
}
