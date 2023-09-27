package com.pouffydev.glacialdelight.content.block.steamer;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SteamerItemHandler implements IItemHandler {
    private final IItemHandler itemHandler;
    private final Direction side;
    
    public SteamerItemHandler(IItemHandler itemHandler, @Nullable Direction side) {
        this.itemHandler = itemHandler;
        this.side = side;
    }
    
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return this.itemHandler.isItemValid(slot, stack);
    }
    
    public int getSlots() {
        return this.itemHandler.getSlots();
    }
    
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return this.itemHandler.getStackInSlot(slot);
    }
    
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (this.side != null && !this.side.equals(Direction.UP)) {
            return slot == 0 ? this.itemHandler.insertItem(slot, stack, simulate) : stack;
        } else {
            return slot < 6 ? this.itemHandler.insertItem(slot, stack, simulate) : stack;
        }
    }
    
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.side != null && !this.side.equals(Direction.UP)) {
            return slot == 8 ? this.itemHandler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        } else {
            return slot < 6 ? this.itemHandler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }
    }
    
    public int getSlotLimit(int slot) {
        return this.itemHandler.getSlotLimit(slot);
    }
}
