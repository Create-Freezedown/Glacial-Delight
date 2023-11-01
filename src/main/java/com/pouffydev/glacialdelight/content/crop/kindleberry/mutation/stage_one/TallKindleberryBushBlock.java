package com.pouffydev.glacialdelight.content.crop.kindleberry.mutation.stage_one;

import com.pouffydev.glacialdelight.init.GDBlocks;
import com.pouffydev.glacialdelight.init.GDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;
import umpaz.farmersrespite.common.FRConfiguration;
import umpaz.farmersrespite.common.block.CoffeeStemBlock;
import umpaz.farmersrespite.common.block.state.WitherRootsUtil;
import umpaz.farmersrespite.common.registry.FRBlocks;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Random;

public class TallKindleberryBushBlock extends BushBlock implements BonemealableBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF;
    private static final VoxelShape SHAPE_LOWER;
    private static final VoxelShape SHAPE_UPPER;
    
    public TallKindleberryBushBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER));
    }
    
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? SHAPE_UPPER : SHAPE_LOWER;
    }
    
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.get("mutated");
    }
    
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return state.is(BlockTags.DIRT);
    }
    
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }
    
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(GDItems.tallKindleBerrySeeds.get());
    }
    
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (facing.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (facing == Direction.UP) || facingState.is(this) && facingState.getValue(HALF) != doubleblockhalf) {
            return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, pos, facingPos);
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }
    
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }
    
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        for(Iterator var5 = WitherRootsUtil.randomInSquare(random, pos, 2).iterator(); var5.hasNext(); ForgeHooks.onCropsGrowPost(level, pos, state)) {
            BlockPos neighborPos = (BlockPos)var5.next();
            BlockState neighborState = level.getBlockState(neighborPos);
            int witherRootRandom = random.nextInt(5);
            BlockState witherRootsState;
            if (witherRootRandom < 2) {
                witherRootsState = GDBlocks.scorchingRoots.get().defaultBlockState();
            } else {
                witherRootsState = GDBlocks.scorchingRootsPlant.get().defaultBlockState();
            }
            
            if (state.getValue(HALF) == DoubleBlockHalf.LOWER && level.isEmptyBlock(pos.above().above()) && ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt(2) == 0)) {
                if (neighborState.getBlock() instanceof CropBlock) {
                    level.setBlockAndUpdate(neighborPos, witherRootsState);
                    this.performBonemeal(level, random, pos, state);
                } else if (level.dimensionType().ultraWarm()) {
                    this.performBonemeal(level, random, pos, state);
                }
            }
        }
        
    }
    
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            preventCreativeDropFromBottomPart(level, pos, state, player);
        }
        
        super.playerWillDestroy(level, pos, state, player);
    }
    
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        return blockpos.getY() < 255 && context.getLevel().getBlockState(blockpos.above()).canBeReplaced(context) ? (BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER) : null;
    }
    
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), (BlockState)state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }
    
    protected static void preventCreativeDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf doubleblockhalf = (DoubleBlockHalf)state.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.getBlock() == state.getBlock() && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }
        
    }
    
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? this.mayPlaceOn(level.getBlockState(blockpos), level, blockpos) : blockstate.is(this);
    }
    
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
        return (Boolean) FRConfiguration.BONE_MEAL_COFFEE.get();
    }
    
    public boolean isBonemealSuccess(Level level, RandomSource rand, BlockPos pos, BlockState state) {
        return true;
    }
    
    public Direction getDirection(Random rand) {
        int i = rand.nextInt(4);
        if (i == 0) {
            return Direction.NORTH;
        } else if (i == 1) {
            return Direction.SOUTH;
        } else if (i == 2) {
            return Direction.EAST;
        } else {
            return i == 3 ? Direction.WEST : null;
        }
    }
    
    public void performBonemeal(ServerLevel level, RandomSource rand, BlockPos pos, BlockState state) {
        Random randDirection = new Random();
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER && level.isEmptyBlock(pos.above().above())) {
            level.setBlockAndUpdate(pos, ((Block) FRBlocks.COFFEE_STEM.get()).defaultBlockState().setValue(CoffeeStemBlock.FACING, this.getDirection(randDirection)));
            level.setBlockAndUpdate(pos.above(), ((Block)FRBlocks.COFFEE_BUSH_TOP.get()).defaultBlockState());
            level.setBlockAndUpdate(pos.above().above(), ((Block)FRBlocks.COFFEE_BUSH_TOP.get()).defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER));
        }
        
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER && level.isEmptyBlock(pos.above())) {
            level.setBlockAndUpdate(pos.below(), ((Block)FRBlocks.COFFEE_STEM.get()).defaultBlockState().setValue(CoffeeStemBlock.FACING, this.getDirection(randDirection)));
            level.setBlockAndUpdate(pos, ((Block)FRBlocks.COFFEE_BUSH_TOP.get()).defaultBlockState());
            level.setBlockAndUpdate(pos.above(), ((Block)FRBlocks.COFFEE_BUSH_TOP.get()).defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER));
        }
        
    }
    
    static {
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
        SHAPE_LOWER = Shapes.or(Block.box(0.0, 6.0, 0.0, 16.0, 18.0, 16.0), Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0));
        SHAPE_UPPER = Shapes.or(Block.box(0.0, -10.0, 0.0, 16.0, 2.0, 16.0), Block.box(5.0, -16.0, 5.0, 11.0, -10.0, 11.0));
    }
}
