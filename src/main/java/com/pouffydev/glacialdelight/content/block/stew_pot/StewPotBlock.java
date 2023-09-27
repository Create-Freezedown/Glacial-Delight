package com.pouffydev.glacialdelight.content.block.stew_pot;

import com.pouffydev.glacialdelight.content.block.base.SupportableFrostableContainerBlock;
import com.pouffydev.glacialdelight.init.GDBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import vectorwing.farmersdelight.common.block.state.CookingPotSupport;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.utility.TextUtils;

import javax.annotation.Nullable;
import java.util.List;

public class StewPotBlock extends SupportableFrostableContainerBlock implements BlockColor {
    protected static final VoxelShape shape = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 10.0D, 14.0D);
    protected static final VoxelShape shapeWithTray = Shapes.or(shape, Block.box(0.0D, -1.0D, 0.0D, 16.0D, 0.0D, 16.0D));
    public StewPotBlock(Properties pProperties) {
        super(pProperties);
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof StewPotBlockEntity stewPotEntity && stewPotEntity.isHeated(level, pos)) {
            SoundEvent boilSound = !stewPotEntity.getMeal().isEmpty()
                    ? ModSounds.BLOCK_COOKING_POT_BOIL_SOUP.get()
                    : ModSounds.BLOCK_COOKING_POT_BOIL.get();
            double x = (double) pos.getX() + 0.5D;
            double y = pos.getY();
            double z = (double) pos.getZ() + 0.5D;
            if (random.nextInt(10) == 0) {
                level.playLocalSound(x, y, z, boilSound, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.2F + 0.9F, false);
            }
        }
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult result) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (heldStack.isEmpty() && player.isShiftKeyDown()) {
            level.setBlockAndUpdate(pos, state.setValue(support, state.getValue(support).equals(CookingPotSupport.HANDLE)
                    ? getTrayState(level, pos) : CookingPotSupport.HANDLE));
            level.playSound(null, pos, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F);
        } else if (!level.isClientSide) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof StewPotBlockEntity cookingPotEntity) {
                ItemStack servingStack = cookingPotEntity.useHeldItemOnMeal(heldStack);
                if (servingStack != ItemStack.EMPTY) {
                    if (!player.getInventory().add(servingStack)) {
                        player.drop(servingStack, false);
                    }
                    level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
                } else {
                    NetworkHooks.openScreen((ServerPlayer) player, cookingPotEntity, pos);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }
    
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(support).equals(CookingPotSupport.TRAY) ? shapeWithTray : shape;
    }
    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        StewPotBlockEntity cookingPotEntity = (StewPotBlockEntity) level.getBlockEntity(pos);
        if (cookingPotEntity != null) {
            CompoundTag nbt = cookingPotEntity.writeMeal(new CompoundTag());
            if (!nbt.isEmpty()) {
                stack.addTagElement("BlockEntityTag", nbt);
            }
            if (cookingPotEntity.hasCustomName()) {
                stack.setHoverName(cookingPotEntity.getCustomName());
            }
        }
        return stack;
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof StewPotBlockEntity cookingPotEntity) {
                Containers.dropContents(level, pos, cookingPotEntity.getDroppableInventory());
                cookingPotEntity.getUsedRecipesAndPopExperience(level, Vec3.atCenterOf(pos));
                level.updateNeighbourForOutputSignal(pos, this);
            }
            
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        ItemStack mealStack = StewPotBlockEntity.getMealFromItem(stack);
        
        if (!mealStack.isEmpty()) {
            MutableComponent textServingsOf = mealStack.getCount() == 1
                    ? TextUtils.getTranslation("tooltip.cooking_pot.single_serving")
                    : TextUtils.getTranslation("tooltip.cooking_pot.many_servings", mealStack.getCount());
            tooltip.add(textServingsOf.withStyle(ChatFormatting.GRAY));
            MutableComponent textMealName = mealStack.getHoverName().copy();
            tooltip.add(textMealName.withStyle(mealStack.getRarity().color));
        } else {
            MutableComponent textEmpty = TextUtils.getTranslation("tooltip.cooking_pot.empty");
            tooltip.add(textEmpty.withStyle(ChatFormatting.GRAY));
        }
    }
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof StewPotBlockEntity) {
                ((StewPotBlockEntity) tileEntity).setCustomName(stack.getHoverName());
            }
        }
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return GDBlockEntities.stewPot.get().create(pos, state);
    }
    
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntity) {
        if (level.isClientSide) {
            return createTickerHelper(blockEntity, GDBlockEntities.stewPot.get(), StewPotBlockEntity::animationTick);
        }
        return createTickerHelper(blockEntity, GDBlockEntities.stewPot.get(), StewPotBlockEntity::potTick);
    }
    
    @Override
    public int getColor(BlockState pState, @Nullable BlockAndTintGetter pLevel, @Nullable BlockPos pPos, int pTintIndex) {
        return BlockColors.createDefault().getColor(pState, pLevel, pPos, BiomeColors.getAverageWaterColor(pLevel, pPos));
    }
}
