package com.pouffydev.glacialdelight.content.block.steamer;

import com.pouffydev.glacialdelight.content.block.util.HeaterLevel;
import com.pouffydev.glacialdelight.init.GDBlocks;
import com.pouffydev.glacialdelight.init.GDMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class SteamerMenu extends AbstractContainerMenu {
    
    public final SteamerBlockEntity blockEntity;
    public final ItemStackHandler inventory;
    private final ContainerData steamerData;
    private final ContainerLevelAccess canInteractWithCallable;
    protected final Level level;
    
    public SteamerMenu(final int windowId, final Inventory playerInventory, final SteamerBlockEntity blockEntity, ContainerData steamerDataIn) {
        super(GDMenuTypes.stewPot.get(), windowId);
        this.blockEntity = blockEntity;
        this.inventory = blockEntity.getInventory();
        this.steamerData = steamerDataIn;
        this.level = playerInventory.player.level;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        
        // Ingredient Slots - 2 Rows x 3 Columns
        int startX = 8;
        int startY = 18;
        int inputStartX = 12;
        int inputStartY = 14;
        int inputStartXRow2 = 12;
        int outputStartY = 44;
        int borderSlotSize = 18;
            for (int column = 0; column < 3; ++column) {
                this.addSlot(new SlotItemHandler(inventory, (3) + column, inputStartX + (column * borderSlotSize), inputStartY + (borderSlotSize)));
                this.addSlot(new SlotItemHandler(inventory, (3) + column, inputStartXRow2 + (column * borderSlotSize), inputStartY + (borderSlotSize)));
                this.addSlot(new SteamerResultSlot(playerInventory.player, blockEntity, inventory, (3) + column, inputStartX + (column * borderSlotSize), outputStartY + (borderSlotSize)));
                this.addSlot(new SteamerResultSlot(playerInventory.player, blockEntity, inventory, (3) + column, inputStartXRow2 + (column * borderSlotSize), outputStartY + (borderSlotSize)));
        }
        
        
        // Main Player Inventory
        int startPlayerInvY = startY * 4 + 12;
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, startX + (column * borderSlotSize),
                        startPlayerInvY + (row * borderSlotSize)));
            }
        }
        
        // Hotbar
        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, startX + (column * borderSlotSize), 142));
        }
        
        this.addDataSlots(steamerDataIn);
    }
    private static SteamerBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof SteamerBlockEntity) {
            return (SteamerBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }
    
    public SteamerMenu(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data), new SimpleContainerData(4));
    }
    
    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(canInteractWithCallable, playerIn, GDBlocks.steamer.get());
    }
    @OnlyIn(Dist.CLIENT)
    public int waterLevel() {
        BlockState state = this.blockEntity.getLevel().getBlockState(this.blockEntity.getBlockPos());
        int water_level = (Integer)state.getValue(SteamerBlock.waterLevel);
        return water_level;
    }
    @OnlyIn(Dist.CLIENT)
    public int steamLevel() {
        BlockState state = this.blockEntity.getLevel().getBlockState(this.blockEntity.getBlockPos());
        int steam_level = (Integer)state.getValue(SteamerBlock.steamLevel);
        return steam_level;
    }
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        int indexMealDisplay = 6;
        int indexContainerInput = 7;
        int indexOutput = 8;
        int startPlayerInv = indexOutput + 1;
        int endPlayerInv = startPlayerInv + 36;
        ItemStack slotStackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            slotStackCopy = slotStack.copy();
            if (index == indexOutput) {
                if (!this.moveItemStackTo(slotStack, startPlayerInv, endPlayerInv, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index > indexOutput) {
                if (!this.moveItemStackTo(slotStack, 0, indexMealDisplay, false)) {
                    return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(slotStack, indexContainerInput, indexOutput, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, startPlayerInv, endPlayerInv, false)) {
                return ItemStack.EMPTY;
            }
            
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            if (slotStack.getCount() == slotStackCopy.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(playerIn, slotStack);
        }
        return slotStackCopy;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getCookProgressionScaled() {
        int i = this.steamerData.get(0);
        int j = this.steamerData.get(1);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }
    
    @OnlyIn(Dist.CLIENT)
    public boolean isHeated() {
        BlockPos pos = this.blockEntity.getBlockPos();
        return blockEntity.isHeated(level, pos);
    }
    @OnlyIn(Dist.CLIENT)
    public HeaterLevel getHeatLevel() {
        BlockPos pos = this.blockEntity.getBlockPos();
        return blockEntity.getHeatLevel(level, pos);
    }
    
    //@Override
    //public void fillCraftSlotsStackedContents(StackedContents helper) {
    //    for (int i = 0; i < inventory.getSlots(); i++) {
    //        helper.accountSimpleStack(inventory.getStackInSlot(i));
    //    }
    //}
    //
    //@Override
    //public void clearCraftingContent() {
    //    for (int i = 0; i < 6; i++) {
    //        this.inventory.setStackInSlot(i, ItemStack.EMPTY);
    //    }
    //}
    //
    //@Override
    //public boolean recipeMatches(Recipe<? super RecipeWrapper> recipe) {
    //    return recipe.matches(new RecipeWrapper(inventory), level);
    //}
    //
    //@Override
    //public int getResultSlotIndex() {
    //    return 0;
    //}
    //
    //
    //@Override
    //public int getGridWidth() {
    //    return 6;
    //}
    //
    //@Override
    //public int getGridHeight() {
    //    return 1;
    //}
    //
    //@Override
    //public int getSize() {
    //    return 11;
    //}
    //
    //@Override
    //public RecipeBookType getRecipeBookType() {
    //    return GlacialDelight.steamingRecipeType;
    //}
    //
    //@Override
    //public boolean shouldMoveToInventory(int slot) {
    //    return slot < (getGridWidth() * getGridHeight());
    //}
}
