package com.pouffydev.glacialdelight.content.block.pastry_board;

import com.pouffydev.glacialdelight.init.GDTags;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PastryBoardItemHandler implements IItemHandler {
    private static final int SLOTS_INPUT = 4;
    private static final int SLOT_SUGAR_INPUT = 5;
    private static final int SLOT_DOUGH_INPUT = 6;
    private static final int SLOT_MEAL_OUTPUT = 8;
    private final IItemHandler itemHandler;
    private final Direction side;
    
    public PastryBoardItemHandler(IItemHandler itemHandler, @Nullable Direction side) {
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
        if (this.side != null && !this.side.equals(Direction.UP) && stack.getItem() == Items.SUGAR) {
            return slot == SLOT_SUGAR_INPUT ? this.itemHandler.insertItem(slot, stack, simulate) : stack;
        } else if (this.side != null && !this.side.equals(Direction.UP) && stack.is(GDTags.AllItemTags.PASTRY.tag)) {
            return slot == SLOT_DOUGH_INPUT ? this.itemHandler.insertItem(slot, stack, simulate) : stack;
        } else {
            return slot < SLOTS_INPUT ? this.itemHandler.insertItem(slot, stack, simulate) : stack;
        }
    }
    
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.side != null && !this.side.equals(Direction.UP)) {
            return slot == SLOT_MEAL_OUTPUT ? this.itemHandler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        } else {
            return slot < SLOTS_INPUT ? this.itemHandler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }
    }
    
    public int getSlotLimit(int slot) {
        return this.itemHandler.getSlotLimit(slot);
    }
}
