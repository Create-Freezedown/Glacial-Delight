package com.pouffydev.glacialdelight.content.block.heater;

import com.pouffydev.glacialdelight.content.block.base.FrostableBlock;
import com.pouffydev.glacialdelight.content.block.base.HeatableBlockEntity;
import com.pouffydev.glacialdelight.content.block.util.HeaterLevel;
import com.pouffydev.glacialdelight.foundation.block_entity.IBE;
import com.pouffydev.glacialdelight.init.GDBlockEntities;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("deprecation")
public class HeaterBlock extends FrostableBlock implements IBE<HeaterBlockEntity> {
    public static final DamageSource heaterDamage = (new DamageSource("glacialdelight.heater")).setIsFire();
    public static final DamageSource heaterDamageFreeze = (new DamageSource("glacialdelight.heater.freezing")).bypassArmor();
    public static final DirectionProperty facing = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty hasFuel = BooleanProperty.create("has_fuel");
    public static final EnumProperty<HeaterLevel> heatLevel = EnumProperty.create("heat_level", HeaterLevel.class);
    public static int fuelDuration = 30 * 20;
    public HeaterBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(facing, Direction.NORTH).setValue(heatLevel, HeaterLevel.NONE).setValue(hasFuel, false).setValue(frozen, false));
    }
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        if (world.isClientSide)
            return;
        BlockEntity blockEntity = world.getBlockEntity(pos.above());
        if (!(blockEntity instanceof HeatableBlockEntity))
            return;
        //HeatableBlockEntity heatableBlock = (HeatableBlockEntity) blockEntity;
        //heatableBlock.notifyChangeOfContents();
    }
    public void extinguish(BlockState state, Level level, BlockPos pos) {
        level.setBlock(pos, state.setValue(hasFuel, false), 2);
        level.setBlock(pos, state.setValue(heatLevel, HeaterLevel.NONE), 2);
        double x = (double)pos.getX() + 0.5;
        double y = pos.getY();
        double z = (double)pos.getZ() + 0.5;
        level.playLocalSound(x, y, z, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F, false);
    }
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof HeaterBlockEntity) {
                ItemUtils.dropItems(level, pos, ((HeaterBlockEntity)tileEntity).getInventory());
            }
            
            super.onRemove(state, level, pos, newState, isMoving);
        }
        
    }
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        HeaterLevel heaterLevel = state.getValue(heatLevel);
        boolean isLit = heaterLevel.isAtLeast(HeaterLevel.SMOULDERING);
        if (heaterLevel == HeaterLevel.FREEZING && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            entity.hurt(heaterDamageFreeze, 0.25F);
        }
        if (isLit && !entity.fireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            if (heaterLevel == HeaterLevel.KINDLED) {
                entity.hurt(heaterDamage, 1.0F);
            }
            if (heaterLevel == HeaterLevel.SMOULDERING) {
                entity.hurt(heaterDamage, 1.5F);
            }
            if (heaterLevel == HeaterLevel.SEETHING) {
                entity.hurt(heaterDamage, 2.0F);
            }
        }
        
        super.stepOn(level, pos, state, entity);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(heatLevel, facing, hasFuel);
    }
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(facing, context.getHorizontalDirection().getOpposite());
        return state.setValue(heatLevel, HeaterLevel.NONE).setValue(hasFuel, false).setValue(frozen, false);
    }
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level level, BlockPos pos, RandomSource rand) {
        HeaterLevel heaterLevel = stateIn.getValue(heatLevel);
        double x = (double)pos.getX() + 0.5;
        double y = pos.getY();
        double z = (double)pos.getZ() + 0.5;
        
        Direction direction = stateIn.getValue(HorizontalDirectionalBlock.FACING);
        Direction.Axis direction$axis = direction.getAxis();
        double horizontalOffset = rand.nextDouble() * 0.6 - 0.3;
        double xOffset = direction$axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52 : horizontalOffset;
        double yOffset = rand.nextDouble() * 6.0 / 16.0;
        double zOffset = direction$axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52 : horizontalOffset;
        if (heaterLevel == HeaterLevel.SMOULDERING) {
            level.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0.0, 0.0, 0.0);
            if (rand.nextInt(10) == 0) {
                level.playLocalSound(x, y, z, ModSounds.BLOCK_STOVE_CRACKLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }
        }
        if (heaterLevel == HeaterLevel.KINDLED) {
            level.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, x + xOffset, y + yOffset, z + zOffset, 0.0, 0.0, 0.0);
            if (rand.nextInt(10) == 0) {
                level.playLocalSound(x, y, z, ModSounds.BLOCK_STOVE_CRACKLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }
        }
        if (heaterLevel == HeaterLevel.SEETHING) {
            level.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x + xOffset, y + yOffset, z + zOffset, 0.0, 0.0, 0.0);
            if (rand.nextInt(10) == 0) {
                level.playLocalSound(x, y, z, ModSounds.BLOCK_STOVE_CRACKLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }
        }
        if (heaterLevel == HeaterLevel.FREEZING) {
                level.addParticle(ParticleTypes.SNOWFLAKE, x + xOffset, y + yOffset, z + zOffset, 0.0, 0.0, 0.0);
                if (rand.nextInt(10) == 0) {
                    level.playLocalSound(x, y, z, SoundEvents.SNOW_STEP, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                }
        }
    }
    
    @Override
    public Class<HeaterBlockEntity> getBlockEntityClass() {
        return HeaterBlockEntity.class;
    }
    
    @Override
    public BlockEntityType<? extends HeaterBlockEntity> getBlockEntityType() {
        return GDBlockEntities.heater.get();
    }
    
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ((BlockEntityType<?>) GDBlockEntities.heater.get()).create(pos, state);
    }
    
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        //return state.getValue(lit) ? createTickerHelper(blockEntityType, GDBlockEntities.heater.get(), level.isClientSide ? HeaterBlockEntity::animationTick : HeaterBlockEntity::cookingTick) : null;
        //also include generalTick
        HeaterLevel heaterLevel = state.getValue(heatLevel);
        return heaterLevel.isAtLeast(HeaterLevel.FREEZING) ? createTickerHelper(blockEntityType, GDBlockEntities.heater.get(), level.isClientSide ? HeaterBlockEntity::animationTick : HeaterBlockEntity::generalTick) : null;
    }
    
    public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
        HeaterLevel heaterLevel = state.getValue(heatLevel);
        return heaterLevel.isAtLeast(HeaterLevel.SMOULDERING) ? BlockPathTypes.DAMAGE_FIRE : null;
    }
    public static int getLight(BlockState state) {
        HeaterLevel level = state.getValue(heatLevel);
        return switch (level) {
            case NONE -> 0;
            case SMOULDERING -> 4;
            case KINDLED -> 10;
            default -> 15;
        };
    }
    @Override
    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }
    @Override
    public int getAnalogOutputSignal(BlockState state, Level p_180641_2_, BlockPos p_180641_3_) {
        return Math.max(0, state.getValue(heatLevel)
                .ordinal() - 1);
    }
    public @NotNull BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(facing, pRot.rotate(pState.getValue(facing)));
    }
    public @NotNull BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(facing)));
    }
    public static HeaterLevel getHeatLevelOf(BlockState blockState) {
        return blockState.hasProperty(heatLevel) ? blockState.getValue(heatLevel)
                : HeaterLevel.NONE;
    }
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getItemInHand(hand);
        Item heldItem = heldStack.getItem();
        HeaterLevel heaterLevel = state.getValue(heatLevel);
        if (heaterLevel.isAtLeast(HeaterLevel.SMOULDERING)) {
            if (heldStack.canPerformAction(ToolActions.SHOVEL_DIG)) {
                this.extinguish(state, level, pos);
                heldStack.hurtAndBreak(1, player, (action) -> {
                    action.broadcastBreakEvent(hand);
                });
                return InteractionResult.SUCCESS;
            }
        }
        HeaterLevel heat = state.getValue(heatLevel);
        
        if (heldStack.isEmpty() && heat != HeaterLevel.NONE)
            return onBlockEntityUse(level, pos, bbte -> {
                bbte.notifyUpdate();
                return InteractionResult.SUCCESS;
            });
        
        boolean doNotConsume = player.isCreative();
        boolean forceOverflow = !(player instanceof FakePlayer);
        
        InteractionResultHolder<ItemStack> res = tryInsert(state, level, pos, heldStack, doNotConsume, forceOverflow, false);
        ItemStack leftover = res.getObject();
        if (!level.isClientSide && !doNotConsume && !leftover.isEmpty()) {
            if (heldStack.isEmpty()) {
                player.setItemInHand(hand, leftover);
            } else if (!player.getInventory()
                    .add(leftover)) {
                player.drop(leftover, false);
            }
        }
        
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof HeaterBlockEntity heaterEntity) {
            int stoveSlot = heaterEntity.getNextEmptySlot();
            if (stoveSlot < 0 || heaterEntity.isHeaterBlockedAbove()) {
                return InteractionResult.PASS;
            }
            
            Optional<HeaterFreezingRecipe> recipeFreeze = heaterEntity.getFreezingRecipe(new SimpleContainer(heldStack), stoveSlot);
            Optional<CampfireCookingRecipe> recipeCook = heaterEntity.getCampfireRecipe(new SimpleContainer(heldStack), stoveSlot);
            if (recipeFreeze.isPresent()) {
                if (!level.isClientSide && heaterEntity.addItem(player.getAbilities().instabuild ? heldStack.copy() : heldStack, recipeFreeze.get(), stoveSlot)) {
                    return InteractionResult.SUCCESS;
                }
                
                return InteractionResult.CONSUME;
            }
            if (recipeCook.isPresent()) {
                if (!level.isClientSide && heaterEntity.addItem(player.getAbilities().instabuild ? heldStack.copy() : heldStack, recipeCook.get(), stoveSlot)) {
                    return InteractionResult.SUCCESS;
                }
                
                return InteractionResult.CONSUME;
            }
        }
        
        return res.getResult() == InteractionResult.SUCCESS ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
    public static InteractionResultHolder<ItemStack> tryInsert(BlockState state, Level world, BlockPos pos,
                                                               ItemStack stack, boolean doNotConsume, boolean forceOverflow, boolean simulate) {
        if (!state.hasBlockEntity())
            return InteractionResultHolder.fail(ItemStack.EMPTY);
        
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof HeaterBlockEntity))
            return InteractionResultHolder.fail(ItemStack.EMPTY);
        HeaterBlockEntity heaterBE = (HeaterBlockEntity) be;
        
        if (!heaterBE.tryUpdateFuel(stack, forceOverflow, simulate))
            return InteractionResultHolder.fail(ItemStack.EMPTY);
        
        if (!doNotConsume) {
            ItemStack container = stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY;
            if (!world.isClientSide) {
                stack.shrink(1);
            }
            return InteractionResultHolder.success(container);
        }
        return InteractionResultHolder.success(ItemStack.EMPTY);
    }
    
}
