package com.pouffydev.glacialdelight.content.crop.kindleberry.mutation.stage_two;

import com.pouffydev.glacialdelight.init.GDBlocks;
import com.pouffydev.glacialdelight.init.GDEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ScorchingRootsBlock extends BushBlock implements BonemealableBlock {
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 14.0, 14.0);
    private static final VoxelShape SHAPE_SMALL = Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
    private static final VoxelShape WILD_COFFEE = Block.box(5.0, 0.0, 5.0, 11.0, 11.0, 11.0);
    public static final DamageSource scorchingRoots = new DamageSource("glacialdelight.scorchingRoots").setIsFire();
    
    public ScorchingRootsBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
    
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state == GDBlocks.scorchingRoots.get().defaultBlockState()) {
            return SHAPE_SMALL;
        } else {
            return SHAPE;
        }
    }
    
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        return this.mayPlaceOn(level.getBlockState(blockpos), level, blockpos);
    }
    
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND) || state.is(Blocks.BASALT) || state.is(Blocks.MAGMA_BLOCK);
    }
    
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        if (!state.canSurvive(world, pos)) {
            AreaEffectCloud cloud = new AreaEffectCloud((Level)world, (double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5);
            cloud.addEffect(new MobEffectInstance(GDEffects.scorching.get(), 100, 0));
            cloud.setDuration(600);
            cloud.setRadius(0.5F);
            cloud.setRadiusOnUse(-0.5F);
            world.addFreshEntity(cloud);
            return Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, facing, facingState, world, pos, facingPos);
        }
    }
    
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity) {
            entity.makeStuckInBlock(state, new Vec3(0.800000011920929, 0.75, 0.800000011920929));
            if (!level.isClientSide && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
                double d0 = Math.abs(entity.getX() - entity.xOld);
                double d1 = Math.abs(entity.getZ() - entity.zOld);
                if (d0 >= 0.003000000026077032 || d1 >= 0.003000000026077032) {
                    entity.hurt(scorchingRoots, 1.0F);
                    ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0));
                }
            }
        }
        
    }
    
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            AreaEffectCloud cloud = new AreaEffectCloud(level, (double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5);
            cloud.addEffect(new MobEffectInstance(GDEffects.scorching.get(), 200, 0));
            cloud.setDuration(600);
            cloud.setRadius(0.5F);
            cloud.setRadiusOnUse(-0.5F);
            cloud.setOwner(player);
            level.addFreshEntity(cloud);
        }
        
        super.playerWillDestroy(level, pos, state, player);
    }
    
    public boolean isPathfindable(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull PathComputationType path) {
        return false;
    }
    
    @Nullable
    public BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return BlockPathTypes.DAMAGE_OTHER;
    }
    
    @Nullable
    public BlockPathTypes getAdjacentBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, BlockPathTypes originalType) {
        return BlockPathTypes.DANGER_OTHER;
    }
    
    public boolean isValidBonemealTarget(BlockGetter p_50897_, BlockPos p_50898_, BlockState p_50899_, boolean p_50900_) {
        return false;
    }
    
    public boolean isBonemealSuccess(Level p_50901_, RandomSource p_50902_, BlockPos p_50903_, BlockState p_50904_) {
        return false;
    }
    
    public void performBonemeal(ServerLevel p_50893_, RandomSource p_50894_, BlockPos p_50895_, BlockState p_50896_) {
    }
    
    public static class ScorchedEffect extends MobEffect {
        public ScorchedEffect(MobEffectCategory category, int color) {
            super(category, color);
        }
        public int getColor() {
            return 0x565dbd;
        }
        public void applyEffectTick(LivingEntity entity, int pAmplifier) {
            if (entity.getHealth() > 1.0F) {
                entity.hurt(scorchingRoots, 1.0F);
            }
            
            super.applyEffectTick(entity, pAmplifier);
        }
        
        @Override
        public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
            return true;
        }
    }
}
