package com.pouffydev.glacialdelight.content.block.pastry_board;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class PastryBoardMealSlot extends SlotItemHandler {
    public PastryBoardMealSlot(IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }
    
    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }
    
    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }
}
