package com.pouffydev.glacialdelight.content.block.pastry_board;

import com.pouffydev.glacialdelight.content.block.base.GDEntityBlock;
import com.pouffydev.glacialdelight.init.GDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class PastryBoardBlock extends GDEntityBlock {
    protected static final VoxelShape shape = Block.box(1.0D, 0.0D, 1.0D, 2.0D, 10.0D, 2.0D);
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }
    public PastryBoardBlock(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof PastryBoardBlockEntity pastryBoardEntity) {
                    NetworkHooks.openScreen((ServerPlayer) player, pastryBoardEntity, pos);
                }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }
    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        PastryBoardBlockEntity pastryBoardEntity = (PastryBoardBlockEntity) level.getBlockEntity(pos);
        if (pastryBoardEntity != null) {
            CompoundTag nbt = pastryBoardEntity.writeMeal(new CompoundTag());
            if (!nbt.isEmpty()) {
                stack.addTagElement("BlockEntityTag", nbt);
            }
            if (pastryBoardEntity.hasCustomName()) {
                stack.setHoverName(pastryBoardEntity.getCustomName());
            }
        }
        return stack;
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof PastryBoardBlockEntity pastryBoardEntity) {
                Containers.dropContents(level, pos, pastryBoardEntity.getDroppableInventory());
                pastryBoardEntity.getUsedRecipesAndPopExperience(level, Vec3.atCenterOf(pos));
                level.updateNeighbourForOutputSignal(pos, this);
            }
            
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return GDBlockEntities.pastryBoard.get().create(pos, state);
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntity) {
        //if (level.isClientSide) {
        //    return createTickerHelper(blockEntity, GDBlockEntities.pastryBoard.get(), PastryBoardBlockEntity::animationTick);
        //}
        return createTickerHelper(blockEntity, GDBlockEntities.pastryBoard.get(), PastryBoardBlockEntity::potTick);
    }
}
