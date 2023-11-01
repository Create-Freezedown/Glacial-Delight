package com.pouffydev.glacialdelight.content.block.steamer;

import com.pouffydev.glacialdelight.content.block.base.SupportableFrostableContainerBlock;
import com.pouffydev.glacialdelight.init.GDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import vectorwing.farmersdelight.common.registry.ModSounds;

import javax.annotation.Nullable;

/*@SuppressWarnings("deprecation")
public class SteamerBlock extends SupportableFrostableContainerBlock {
    public static final IntegerProperty waterLevel;
    public static final IntegerProperty steamLevel;
    
    public SteamerBlock(Properties pProperties) {
        super(pProperties);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof SteamerBlockEntity steamerBlockEntity && steamerBlockEntity.isHeated(level, pos)) {
            SoundEvent boilSound = ModSounds.BLOCK_COOKING_POT_BOIL.get();
            double x = (double) pos.getX() + 0.5D;
            double y = pos.getY();
            double z = (double) pos.getZ() + 0.5D;
            if (random.nextInt(10) == 0) {
                level.playLocalSound(x, y, z, boilSound, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.2F + 0.9F, false);
            }
        }
    }
    
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            int water_level = state.getValue(waterLevel);
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (heldStack.getItem() == Items.WATER_BUCKET && water_level == 0) {
                level.setBlockAndUpdate(pos, state.setValue(waterLevel, water_level + 1000));
                emptyContainer(level, pos, player, hand, heldStack, state, SoundEvents.BUCKET_EMPTY, Items.BUCKET);
            } else if (heldStack.getItem() == Items.POTION && water_level < 751 && PotionUtils.getPotion(heldStack) == Potions.WATER) {
                level.setBlockAndUpdate(pos, state.setValue(waterLevel, water_level + 250));
                emptyContainer(level, pos, player, hand, heldStack, state, SoundEvents.BOTTLE_EMPTY, Items.GLASS_BOTTLE);
            } else if (tileEntity instanceof SteamerBlockEntity steamerBlockEntity) {
                if (!player.isShiftKeyDown()) {
                    NetworkHooks.openScreen((ServerPlayer) player, steamerBlockEntity, pos);
                }
            }
        return InteractionResult.SUCCESS;
    } else {
        return InteractionResult.SUCCESS;
    }
}
    static InteractionResult emptyContainer(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockState state, SoundEvent sound, Item container) {
        if (!level.isClientSide) {
            Item item = stack.getItem();
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(container)));
            player.awardStat(Stats.ITEM_USED.get(item));
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        SteamerBlockEntity steamerBlockEntity = (SteamerBlockEntity)level.getBlockEntity(pos);
        if (steamerBlockEntity != null) {
            if (steamerBlockEntity.hasCustomName()) {
                stack.setHoverName(steamerBlockEntity.getCustomName());
            }
        }
        
        return stack;
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(waterLevel, steamLevel);
    }
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof SteamerBlockEntity) {
                ((SteamerBlockEntity)tileEntity).setCustomName(stack.getHoverName());
            }
        }
        
    }
    
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntity) {
        return level.isClientSide ? createTickerHelper(blockEntity, GDBlockEntities.steamer.get(), SteamerBlockEntity::animationTick) : createTickerHelper(blockEntity, GDBlockEntities.steamer.get(), SteamerBlockEntity::cookingTick);
    }
    static {
        waterLevel = IntegerProperty.create("water_level", 0, 1000);
        steamLevel = IntegerProperty.create("steam_level", 0, 1000);
    }
}

 */
