package com.pouffydev.glacialdelight.content.block.pastry_board;

import com.mojang.datafixers.util.Pair;
import com.pouffydev.glacialdelight.GlacialDelight;
import com.pouffydev.glacialdelight.init.GDBlocks;
import com.pouffydev.glacialdelight.init.GDMenuTypes;
import com.pouffydev.glacialdelight.init.GDTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class PastryBoardMenu extends AbstractContainerMenu {
    public static final ResourceLocation EMPTY_CONTAINER_SLOT_PASTRY = new ResourceLocation(GlacialDelight.ID, "item/empty_container_slot_pastry");
    public static final ResourceLocation EMPTY_CONTAINER_SLOT_SUGAR = new ResourceLocation(GlacialDelight.ID, "item/empty_container_slot_sugar");
    
    
    public final PastryBoardBlockEntity blockEntity;
    public final ItemStackHandler inventory;
    private final ContainerData cookingPotData;
    private final ContainerLevelAccess canInteractWithCallable;
    protected final Level level;
    
    public PastryBoardMenu(final int windowId, final Inventory playerInventory, final PastryBoardBlockEntity blockEntity, ContainerData cookingPotDataIn) {
        super(GDMenuTypes.pastryBoard.get(), windowId);
        this.blockEntity = blockEntity;
        this.inventory = blockEntity.getInventory();
        this.cookingPotData = cookingPotDataIn;
        this.level = playerInventory.player.level;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        
        // Ingredient Slots - 2 Rows x 3 Columns
        int startX = 8;
        int startY = 18;
        int inputStartX = 30;
        int inputStartY = 17;
        int borderSlotSize = 18;
        //Main Ingerdient Slots
        this.addSlot(new PastryBoardMealSlot(inventory, 0, 23, 11));
        this.addSlot(new PastryBoardMealSlot(inventory, 1, 41, 11));
        this.addSlot(new PastryBoardMealSlot(inventory, 2, 59, 11));
        
        //Bonus Ingredient Slots
        this.addSlot(new PastryBoardMealSlot(inventory, 3, 12, 36));
        this.addSlot(new PastryBoardMealSlot(inventory, 4, 30, 36));
        
        //Sugar Input
        this.addSlot(new SlotItemHandler(inventory, 5, 52, 36)
        {
            @OnlyIn(Dist.CLIENT)
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_CONTAINER_SLOT_SUGAR);
            }
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(GDTags.AllItemTags.SUGAR.tag);
            }
            
        });
        
        //Pastry Input
        this.addSlot(new SlotItemHandler(inventory, 6, 70, 36)
        {
            @OnlyIn(Dist.CLIENT)
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_CONTAINER_SLOT_PASTRY);
            }
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(GDTags.AllItemTags.PASTRY.tag);
            }
        });
        
        // Output Display
        this.addSlot(new PastryBoardMealSlot(inventory, 6, 124, 23));
        
        // Bowl Output
        this.addSlot(new PastryBoardResultSlot(playerInventory.player, blockEntity, inventory, 8, 124, 55));
        
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
        
        this.addDataSlots(cookingPotDataIn);
    }
    private static PastryBoardBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof PastryBoardBlockEntity) {
            return (PastryBoardBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }
    
    public PastryBoardMenu(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data), new SimpleContainerData(4));
    }
    
    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(canInteractWithCallable, playerIn, GDBlocks.pastryBoard.get());
    }
    
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        int indexSugarInput = 5;
        int indexPastryInput = 6;
        int indexMealDisplay = 7;
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
                boolean isSugar = slotStack.is(GDTags.AllItemTags.SUGAR.tag) || slotStack.is(blockEntity.getContainer().getItem());
                boolean isPastry = slotStack.is(GDTags.AllItemTags.PASTRY.tag) || slotStack.is(blockEntity.getContainer().getItem());
                if (isSugar && !this.moveItemStackTo(slotStack, indexSugarInput, indexSugarInput + 1, false)) {
                    return ItemStack.EMPTY;
                } else if (isPastry && !this.moveItemStackTo(slotStack, indexPastryInput, indexPastryInput + 1, false)) {
                    return ItemStack.EMPTY;
                }else if (!this.moveItemStackTo(slotStack, 0, indexMealDisplay, false)) {
                    return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(slotStack, indexSugarInput, indexOutput, false)) {
                    return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(slotStack, indexPastryInput, indexOutput, false)) {
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
}
