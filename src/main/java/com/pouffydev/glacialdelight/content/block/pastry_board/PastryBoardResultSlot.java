package com.pouffydev.glacialdelight.content.block.pastry_board;

import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotBlock;
import com.pouffydev.glacialdelight.content.block.stew_pot.StewPotBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import javax.annotation.Nonnull;

import static vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity.INGREDIENT_REMAINDER_OVERRIDES;

public class PastryBoardResultSlot extends SlotItemHandler
{
    public final PastryBoardBlockEntity tileEntity;
    private final Player player;
    private int removeCount;
    
    public PastryBoardResultSlot(Player player, PastryBoardBlockEntity tile, IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.tileEntity = tile;
        this.player = player;
    }
    
    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }
    
    @Override
    @Nonnull
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }
        
        return super.remove(amount);
    }
    
    @Override
    public void onTake(Player thePlayer, ItemStack stack) {
        this.checkTakeAchievements(stack);
        super.onTake(thePlayer, stack);
        //For each item removed from the slot, remove one from the input slots
        //Get amount of items removed from slot
        for (int i = 0; i < 7; i++) {
            ItemStack slotStack = tileEntity.getInventory().getStackInSlot(i);
            if (slotStack.hasCraftingRemainingItem()) {
                ejectIngredientRemainder(slotStack.getCraftingRemainingItem(), thePlayer);
            } else if (INGREDIENT_REMAINDER_OVERRIDES.containsKey(slotStack.getItem())) {
                ejectIngredientRemainder(INGREDIENT_REMAINDER_OVERRIDES.get(slotStack.getItem()).getDefaultInstance(), thePlayer);
            }
            if (!slotStack.isEmpty())
                this.tileEntity.getInventory().extractItem(i, 1, false);
        }
    }
    protected void ejectIngredientRemainder(ItemStack remainderStack, Player player) {
        double x = player.getOnPos().getX() + 0.5 + (0.25);
        double y = player.getOnPos().getY() + 0.7;
        double z = player.getOnPos().getZ() + 0.5 + (0.25);
        ItemUtils.spawnItemEntity(player.getLevel(), remainderStack, x, y, z, 0.08F, 0.25F, 0.08F);
    }
    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.checkTakeAchievements(stack);
    }
    
    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(this.player.level, this.player, this.removeCount);
        
        if (!this.player.level.isClientSide) {
            tileEntity.awardUsedRecipes(this.player);
        }
        
        this.removeCount = 0;
    }
}
