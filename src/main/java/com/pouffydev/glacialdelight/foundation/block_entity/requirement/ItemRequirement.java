package com.pouffydev.glacialdelight.foundation.block_entity.requirement;

import com.pouffydev.glacialdelight.foundation.util.NBTProcessors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemRequirement {
    public static final ItemRequirement NONE = new ItemRequirement(Collections.emptyList());
    public static final ItemRequirement INVALID = new ItemRequirement(Collections.emptyList());
    
    protected List<StackRequirement> requiredItems;
    
    public ItemRequirement(List<StackRequirement> requiredItems) {
        this.requiredItems = requiredItems;
    }
    
    public ItemRequirement(StackRequirement stackRequirement) {
        this(List.of(stackRequirement));
    }
    
    public ItemRequirement(ItemUseType usage, ItemStack stack) {
        this(new StackRequirement(stack, usage));
    }
    
    public ItemRequirement(ItemUseType usage, Item item) {
        this(usage, new ItemStack(item));
    }
    
    public ItemRequirement(ItemUseType usage, List<ItemStack> requiredItems) {
        this(requiredItems.stream()
                .map(req -> new StackRequirement(req, usage))
                .collect(Collectors.toList()));
    }
    
    public static ItemRequirement of(BlockState state, BlockEntity be) {
        Block block = state.getBlock();
        
        ItemRequirement requirement;
        if (block instanceof ISpecialBlockItemRequirement specialBlock) {
            requirement = specialBlock.getRequiredItems(state, be);
        } else {
            requirement = defaultOf(state, be);
        }
        
        if (be instanceof ISpecialBlockEntityItemRequirement specialBE)
            requirement = requirement.union(specialBE.getRequiredItems(state));
        
        return requirement;
    }
    
    private static ItemRequirement defaultOf(BlockState state, BlockEntity be) {
        Block block = state.getBlock();
        if (block == Blocks.AIR)
            return NONE;
        
        Item item = block.asItem();
        if (item == Items.AIR)
            return INVALID;
        
        // double slab needs two items
        if (state.hasProperty(BlockStateProperties.SLAB_TYPE)
                && state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.DOUBLE)
            return new ItemRequirement(ItemUseType.CONSUME, new ItemStack(item, 2));
        if (block instanceof TurtleEggBlock)
            return new ItemRequirement(ItemUseType.CONSUME, new ItemStack(item, state.getValue(TurtleEggBlock.EGGS)
                    .intValue()));
        if (block instanceof SeaPickleBlock)
            return new ItemRequirement(ItemUseType.CONSUME, new ItemStack(item, state.getValue(SeaPickleBlock.PICKLES)
                    .intValue()));
        if (block instanceof SnowLayerBlock)
            return new ItemRequirement(ItemUseType.CONSUME, new ItemStack(item, state.getValue(SnowLayerBlock.LAYERS)
                    .intValue()));
        if (block instanceof FarmBlock || block instanceof DirtPathBlock)
            return new ItemRequirement(ItemUseType.CONSUME, Items.DIRT);
        if (block instanceof AbstractBannerBlock && be instanceof BannerBlockEntity bannerBE)
            return new ItemRequirement(new StrictNbtStackRequirement(bannerBE.getItem(), ItemUseType.CONSUME));
        
        return new ItemRequirement(ItemUseType.CONSUME, item);
    }
    
    public static ItemRequirement of(Entity entity) {
        if (entity instanceof ISpecialEntityItemRequirement specialEntity)
            return specialEntity.getRequiredItems();
        
        if (entity instanceof ItemFrame itemFrame) {
            ItemStack frame = new ItemStack(Items.ITEM_FRAME);
            ItemStack displayedItem = NBTProcessors.withUnsafeNBTDiscarded(itemFrame.getItem());
            if (displayedItem.isEmpty())
                return new ItemRequirement(ItemUseType.CONSUME, Items.ITEM_FRAME);
            return new ItemRequirement(List.of(new ItemRequirement.StackRequirement(frame, ItemUseType.CONSUME),
                    new ItemRequirement.StrictNbtStackRequirement(displayedItem, ItemUseType.CONSUME)));
        }
        
        if (entity instanceof ArmorStand armorStand) {
            List<StackRequirement> requirements = new ArrayList<>();
            requirements.add(new StackRequirement(new ItemStack(Items.ARMOR_STAND), ItemUseType.CONSUME));
            armorStand.getAllSlots()
                    .forEach(s -> requirements
                            .add(new StrictNbtStackRequirement(NBTProcessors.withUnsafeNBTDiscarded(s), ItemUseType.CONSUME)));
            return new ItemRequirement(requirements);
        }
        
        return INVALID;
    }
    
    public boolean isEmpty() {
        return NONE == this;
    }
    
    public boolean isInvalid() {
        return INVALID == this;
    }
    
    public List<StackRequirement> getRequiredItems() {
        return requiredItems;
    }
    
    public ItemRequirement union(ItemRequirement other) {
        if (this.isInvalid() || other.isInvalid())
            return INVALID;
        if (this.isEmpty())
            return other;
        if (other.isEmpty())
            return this;
        
        return new ItemRequirement(Stream.concat(requiredItems.stream(), other.requiredItems.stream())
                .collect(Collectors.toList()));
    }
    
    public enum ItemUseType {
        CONSUME, DAMAGE
    }
    
    public static class StackRequirement {
        public final ItemStack stack;
        public final ItemUseType usage;
        
        public StackRequirement(ItemStack stack, ItemUseType usage) {
            this.stack = stack;
            this.usage = usage;
        }
        
        public boolean matches(ItemStack other) {
            return stack.sameItem(other);
        }
    }
    
    public static class StrictNbtStackRequirement extends StackRequirement {
        public StrictNbtStackRequirement(ItemStack stack, ItemUseType usage) {
            super(stack, usage);
        }
        
        @Override
        public boolean matches(ItemStack other) {
            return ItemStack.isSameItemSameTags(stack, other);
        }
    }
}
