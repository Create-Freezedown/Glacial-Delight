package com.pouffydev.glacialdelight.content.crop.kindleberry;

import com.pouffydev.glacialdelight.init.GDItems;
import com.pouffydev.glacialdelight.init.GDTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import vectorwing.farmersdelight.common.block.TomatoVineBlock;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class KindleberryBushBlock extends CropBlock {
    public static final IntegerProperty bushAge;
    public static final BooleanProperty snowy = BlockStateProperties.SNOWY;
    public KindleberryBushBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), 0).setValue(snowy, false));
        
    }
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }
    
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isAreaLoaded(pos, 1)) {
            if (level.getRawBrightness(pos, 0) >= 9) {
                int age = this.getAge(state);
                if (age < this.getMaxAge()) {
                    float speed = getGrowthSpeed(this, level, pos);
                    if (ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt((int)(25.0F / speed) + 1) == 0)) {
                        level.setBlock(pos, (BlockState)state.setValue(this.getAgeProperty(), age + 1), 2);
                        ForgeHooks.onCropsGrowPost(level, pos, state);
                    }
                }
                //randomly reduce the age to 2 if the bush is mature
                if (age == 3) {
                    float speed = getGrowthSpeed(this, level, pos);
                    if (ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt((int)(25.0F / speed) + 1) == 0)) {
                        level.setBlock(pos, state.setValue(this.getAgeProperty(), 2), 2);
                        ForgeHooks.onCropsGrowPost(level, pos, state);
                    }
                }
            }
        }
    }
    public BlockState getStateForAge(int age) {
        return this.defaultBlockState().setValue(this.getAgeProperty(), age);
    }
    
    public IntegerProperty getAgeProperty() {
        return bushAge;
    }
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return blockShape(state);
    }
    public int getMaxAge() {
        return 3;
    }
    
    public boolean isMinAge(BlockState state) {
        return state.getValue(this.getAgeProperty()) == 0;
    }
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = pContext.getLevel().getBlockState(pContext.getClickedPos().above());
        return this.defaultBlockState().setValue(snowy, Boolean.valueOf(isSnowySetting(blockstate)));
    }
    private static boolean isSnowySetting(BlockState pState) {
        
        return pState.is(BlockTags.SNOW);
    }
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return state.is(BlockTags.DIRT);
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return isMinAge(state) ? Shapes.empty() : super.getCollisionShape(state, world, pos, ctx);
    }
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int age = state.getValue(this.getAgeProperty());
        boolean isMature = age == this.getMaxAge();
        boolean isSmouldering = age == 2;
        if (!isMature && player.getItemInHand(hand).is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        } else if (isMature) {
            int quantity = 1 + level.random.nextInt(2);
            popResource(level, pos, new ItemStack(GDItems.kindleBerry.get(), quantity));
            if ((double)level.random.nextFloat() < 0.05) {
                popResource(level, pos, new ItemStack(GDItems.scorchBerry.get()));
            }
            
            level.playSound(null, pos, ModSounds.ITEM_TOMATO_PICK_FROM_BUSH.get(), SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            level.setBlock(pos, state.setValue(this.getAgeProperty(), 1), 2);
            return InteractionResult.SUCCESS;
        } else if (isSmouldering) {
            int quantity = 1 + level.random.nextInt(2);
            popResource(level, pos, new ItemStack(GDItems.smoulderBerry.get(), quantity));
            if ((double)level.random.nextFloat() < 0.05) {
                popResource(level, pos, new ItemStack(GDItems.kindleBerry.get()));
            }
            
            level.playSound(null, pos, ModSounds.ITEM_TOMATO_PICK_FROM_BUSH.get(), SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            level.setBlock(pos, state.setValue(this.getAgeProperty(), 1), 2);
            return InteractionResult.SUCCESS;
        } else {
            return super.use(state, level, pos, player, hand, hit);
        }
    }
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (!state.canSurvive(level, currentPos)) {
            level.scheduleTick(currentPos, this, 1);
        }
        return facing == Direction.UP ? state.setValue(snowy, isSnowySetting(facingState)) : state;
    }
    protected ItemLike getBaseSeedId() {
        return GDItems.kindleBerrySeeds.get();
    }
    
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(bushAge).add(snowy);
    }
    
    protected int getBonemealAgeIncrease(Level level) {
        return super.getBonemealAgeIncrease(level) / 2;
    }
    
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int newAge = this.getAge(state) + this.getBonemealAgeIncrease(level);
        int maxAge = this.getMaxAge();
        if (newAge > maxAge) {
            newAge = maxAge;
        }
        
        level.setBlockAndUpdate(pos, state.setValue(this.getAgeProperty(), newAge));
        if (newAge == 2) {
            //create small smoke particles when the bush is fertilized
            level.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.25, 0.25, 0.25, 0.0);
        } else if (newAge == 3) {
            //create flame particles when the bush is fertilized
            level.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.25, 0.25, 0.25, 0.0);
        }
    }
    
    public static VoxelShape blockShape(BlockState state) {
        if (state.getValue(bushAge) == 0) {
            return Block.box(5.0, 0.0, 5.0, 11.0, 14.0, 11.0);
        } else {
            return Shapes.or(Block.box(5.0, 0.0, 5.0, 11.0, 7.0, 11.0), Block.box(0.0, 7.0, 0.0, 16.0, 16.0, 16.0));
        }
    }
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        return this.mayPlaceOn(level.getBlockState(blockpos), level, blockpos);
    }
    static {
        bushAge = BlockStateProperties.AGE_3;
    }
}
