package com.pouffydev.glacialdelight.content.block.heater;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pouffydev.glacialdelight.content.block.base.FrostableBlock;
import com.pouffydev.glacialdelight.content.block.util.HeaterLevel;
import com.pouffydev.glacialdelight.init.GDBlockEntities;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.utility.ItemUtils;
import vectorwing.farmersdelight.common.utility.MathUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;

import static com.pouffydev.glacialdelight.content.block.heater.data.FuelDataListener.heaterFuel;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class HeaterBlock extends FrostableBlock {
    public static final DamageSource heaterDamage = (new DamageSource("glacialdelight.heater")).setIsFire();
    public static final DamageSource heaterDamageFreeze = (new DamageSource("glacialdelight.heater.freezing")).bypassArmor();
    public static final DirectionProperty facing = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty lit = BlockStateProperties.LIT;
    public static final BooleanProperty hasFuel = BooleanProperty.create("has_fuel");
    public static final EnumProperty<HeaterLevel> heatLevel = EnumProperty.create("heat_level", HeaterLevel.class);
    public static int fuelDuration = 30 * 20;
    public HeaterBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(facing, Direction.NORTH).setValue(heatLevel, HeaterLevel.NONE).setValue(lit, false).setValue(hasFuel, false));
    }
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
    
    public void extinguish(BlockState state, Level level, BlockPos pos) {
        level.setBlock(pos, state.setValue(lit, false), 2);
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
        boolean isLit = level.getBlockState(pos).getValue(lit);
        HeaterLevel heaterLevel = state.getValue(heatLevel);
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
        builder.add(heatLevel, facing, lit, hasFuel);
    }
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(facing, context.getHorizontalDirection().getOpposite());
        return state.setValue(heatLevel, HeaterLevel.NONE).setValue(lit, false).setValue(hasFuel, false);
    }
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level level, BlockPos pos, RandomSource rand) {
        HeaterLevel heaterLevel = stateIn.getValue(heatLevel);
        if (stateIn.getValue(HeaterBlock.lit)) {
            double x = (double)pos.getX() + 0.5;
            double y = pos.getY();
            double z = (double)pos.getZ() + 0.5;
            
            Direction direction = stateIn.getValue(HorizontalDirectionalBlock.FACING);
            Direction.Axis direction$axis = direction.getAxis();
            double horizontalOffset = rand.nextDouble() * 0.6 - 0.3;
            double xOffset = direction$axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52 : horizontalOffset;
            double yOffset = rand.nextDouble() * 6.0 / 16.0;
            double zOffset = direction$axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52 : horizontalOffset;
            if (heaterLevel != HeaterLevel.NONE) {
                if (heaterLevel != HeaterLevel.FREEZING) {
                    if (rand.nextInt(10) == 0) {
                        level.playLocalSound(x, y, z, ModSounds.BLOCK_STOVE_CRACKLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
                    }
                    level.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0.0, 0.0, 0.0);
                    level.addParticle(ParticleTypes.FLAME, x + xOffset, y + yOffset, z + zOffset, 0.0, 0.0, 0.0);
                } else {
                    level.addParticle(ParticleTypes.SNOWFLAKE, x + xOffset, y + yOffset, z + zOffset, 0.0, 0.0, 0.0);
                    if (rand.nextInt(10) == 0) {
                        level.playLocalSound(x, y, z, SoundEvents.SNOW_STEP, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                    }
                }
            }
        }
        
    }
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ((BlockEntityType<?>) GDBlockEntities.heater.get()).create(pos, state);
    }
    
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        //return state.getValue(lit) ? createTickerHelper(blockEntityType, GDBlockEntities.heater.get(), level.isClientSide ? HeaterBlockEntity::animationTick : HeaterBlockEntity::cookingTick) : null;
        //also include generalTick
        return state.getValue(lit) ? createTickerHelper(blockEntityType, GDBlockEntities.heater.get(), level.isClientSide ? HeaterBlockEntity::animationTick : HeaterBlockEntity::generalTick) : null;
    }
    
    public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
        return (Boolean)state.getValue(lit) ? BlockPathTypes.DAMAGE_FIRE : null;
    }
    
    public @NotNull BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(facing, pRot.rotate(pState.getValue(facing)));
    }
    public HeaterLevel getHeatLevel(int lvl) {
        if (lvl <= 0) {
            return HeaterLevel.FREEZING;
        }
        if (lvl == 1) {
            return HeaterLevel.SMOULDERING;
        }
        if (lvl == 2) {
            return HeaterLevel.KINDLED;
        }
        if (lvl >= 3) {
            return HeaterLevel.SEETHING;
        }
        return HeaterLevel.NONE;
    }
    public @NotNull BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(facing)));
    }
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getItemInHand(hand);
        Item heldItem = heldStack.getItem();
        HeaterLevel heaterLevel = state.getValue(heatLevel);
        if (state.getValue(lit)) {
            if (heldStack.canPerformAction(ToolActions.SHOVEL_DIG) && heaterLevel != HeaterLevel.FREEZING && heaterLevel != HeaterLevel.NONE) {
                this.extinguish(state, level, pos);
                heldStack.hurtAndBreak(1, player, (action) -> {
                    action.broadcastBreakEvent(hand);
                });
                return InteractionResult.SUCCESS;
            }
            
            if (heldItem == Items.WATER_BUCKET && heaterLevel != HeaterLevel.FREEZING && heaterLevel != HeaterLevel.NONE) {
                if (!level.isClientSide()) {
                    level.playSound((Player)null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                
                this.extinguish(state, level, pos);
                if (!player.isCreative()) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                }
                
                return InteractionResult.SUCCESS;
            }
        } else {
            for (JsonElement jsonElement : heaterFuel) {
                JsonObject json = jsonElement.getAsJsonObject();
                String fuelItem = json.get("fuel_item").getAsString();
                int heatLevelInt = json.get("heat_level").getAsInt();
                int burnTime = json.get("fuel_duration").getAsInt();
                if (heldStack == Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(fuelItem))).getDefaultInstance()) {
                    level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (MathUtils.RAND.nextFloat() - MathUtils.RAND.nextFloat()) * 0.2F + 1.0F);
                    level.setBlock(pos, state.setValue(BlockStateProperties.LIT, Boolean.TRUE), 11);
                    fuelDuration = burnTime * 20;
                    state.setValue(hasFuel, true);
                    if (heaterLevel == HeaterLevel.NONE) {
                        level.setBlock(pos, state.setValue(heatLevel, getHeatLevel(heatLevelInt)), 11);
                    }
                    if (heaterLevel != HeaterLevel.NONE) {
                        if (heaterLevel == HeaterLevel.FREEZING && heatLevelInt <= 0) {
                            level.setBlock(pos, state.setValue(heatLevel, HeaterLevel.FREEZING), 11);
                        }
                        if (heaterLevel == HeaterLevel.FREEZING && heatLevelInt == 1 || heaterLevel == HeaterLevel.KINDLED && heatLevelInt <= 0) {
                            level.setBlock(pos, state.setValue(heatLevel, HeaterLevel.SMOULDERING), 11);
                        }
                        if (heaterLevel == HeaterLevel.FREEZING && heatLevelInt == 2 || heaterLevel == HeaterLevel.SMOULDERING && heatLevelInt == 1 || heaterLevel == HeaterLevel.SEETHING && heatLevelInt <= 0) {
                            level.setBlock(pos, state.setValue(heatLevel, HeaterLevel.KINDLED), 11);
                        }
                        if (heaterLevel == HeaterLevel.FREEZING && heatLevelInt == 3 || heaterLevel == HeaterLevel.SMOULDERING && heatLevelInt == 2 || heaterLevel == HeaterLevel.KINDLED && heatLevelInt == 1) {
                            level.setBlock(pos, state.setValue(heatLevel, HeaterLevel.SEETHING), 11);
                        }
                    }
                    level.setBlock(pos, state.setValue(heatLevel, getHeatLevel(heatLevelInt)), 11);
                    if (!player.isCreative()) {
                        heldStack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
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
        
        return InteractionResult.PASS;
    }
}
