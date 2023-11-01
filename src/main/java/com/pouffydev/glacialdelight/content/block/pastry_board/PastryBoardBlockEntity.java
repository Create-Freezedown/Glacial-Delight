package com.pouffydev.glacialdelight.content.block.pastry_board;

import com.google.common.collect.Lists;
import com.pouffydev.glacialdelight.content.block.util.GDBlockEntity;
import com.pouffydev.glacialdelight.foundation.block_entity.behaviour.BlockEntityBehaviour;
import com.pouffydev.glacialdelight.foundation.util.lang.Lang;
import com.pouffydev.glacialdelight.init.GDBlockEntities;
import com.pouffydev.glacialdelight.init.GDItems;
import com.pouffydev.glacialdelight.init.GDRecipeTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.mixin.accessor.RecipeManagerAccessor;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
@MethodsReturnNonnullByDefault
public class PastryBoardBlockEntity extends GDBlockEntity implements MenuProvider, Nameable, RecipeHolder {
    public static final int MEAL_DISPLAY_SLOT = 7;
    public static final int CONTAINER_SLOT = 6;
    public static final int OUTPUT_SLOT = 8;
    public static final int INVENTORY_SIZE = OUTPUT_SLOT + 1;
    public final ItemStackHandler inventory;
    private final LazyOptional<IItemHandler> inputHandler;
    private final LazyOptional<IItemHandler> outputHandler;
    private int cookTime;
    private int cookTimeTotal;
    private ItemStack mealContainerStack;
    private Component customName;
    
    protected final ContainerData pastryBoardData;
    private final Object2IntOpenHashMap<ResourceLocation> usedRecipeTracker;
    
    private ResourceLocation lastRecipeID;
    private boolean checkNewRecipe;
    public PastryBoardBlockEntity(BlockPos pos, BlockState state) {
        super(GDBlockEntities.pastryBoard.get(), pos, state);
        this.inventory = createHandler();
        this.inputHandler = LazyOptional.of(() -> new PastryBoardItemHandler(inventory, Direction.UP));
        this.outputHandler = LazyOptional.of(() -> new PastryBoardItemHandler(inventory, Direction.DOWN));
        this.mealContainerStack = ItemStack.EMPTY;
        this.pastryBoardData = createIntArray();
        this.usedRecipeTracker = new Object2IntOpenHashMap<>();
        this.checkNewRecipe = true;
    }
    public List<Recipe<?>> getUsedRecipesAndPopExperience(Level level, Vec3 pos) {
        List<Recipe<?>> list = Lists.newArrayList();
        
        for (Object2IntMap.Entry<ResourceLocation> entry : usedRecipeTracker.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                splitAndSpawnExperience((ServerLevel) level, pos, entry.getIntValue(), ((PastryBoardRecipe) recipe).getExperience());
            });
        }
        
        return list;
    }
    private static void splitAndSpawnExperience(ServerLevel level, Vec3 pos, int craftedAmount, float experience) {
        int expTotal = Mth.floor((float) craftedAmount * experience);
        float expFraction = Mth.frac((float) craftedAmount * experience);
        if (expFraction != 0.0F && Math.random() < (double) expFraction) {
            ++expTotal;
        }
        
        ExperienceOrb.award(level, pos, expTotal);
    }
    public static ItemStack getMealFromItem(ItemStack cookingPotStack) {
        if (!cookingPotStack.is(GDItems.pastryBoard.get())) {
            return ItemStack.EMPTY;
        }
        
        CompoundTag compound = cookingPotStack.getTagElement("BlockEntityTag");
        if (compound != null) {
            CompoundTag inventoryTag = compound.getCompound("Inventory");
            if (inventoryTag.contains("Items", 9)) {
                ItemStackHandler handler = new ItemStackHandler();
                handler.deserializeNBT(inventoryTag);
                return handler.getStackInSlot(6);
            }
        }
        
        return ItemStack.EMPTY;
    }
    public static void takeServingFromItem(ItemStack cookingPotStack) {
        if (!cookingPotStack.is(GDItems.pastryBoard.get())) {
            return;
        }
        
        CompoundTag compound = cookingPotStack.getTagElement("BlockEntityTag");
        if (compound != null) {
            CompoundTag inventoryTag = compound.getCompound("Inventory");
            if (inventoryTag.contains("Items", 9)) {
                ItemStackHandler handler = new ItemStackHandler();
                handler.deserializeNBT(inventoryTag);
                ItemStack newMealStack = handler.getStackInSlot(6);
                newMealStack.shrink(1);
                compound.remove("Inventory");
                compound.put("Inventory", handler.serializeNBT());
            }
        }
    }
    public static ItemStack getContainerFromItem(ItemStack cookingPotStack) {
        if (!cookingPotStack.is(GDItems.pastryBoard.get())) {
            return ItemStack.EMPTY;
        }
        
        CompoundTag compound = cookingPotStack.getTagElement("BlockEntityTag");
        if (compound != null) {
            return ItemStack.of(compound.getCompound("Pastry"));
        }
        
        return ItemStack.EMPTY;
    }
    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        inventory.deserializeNBT(compound.getCompound("Inventory"));
        cookTime = compound.getInt("CookTime");
        cookTimeTotal = compound.getInt("CookTimeTotal");
        mealContainerStack = ItemStack.of(compound.getCompound("Pastry"));
        if (compound.contains("CustomName", 8)) {
            customName = Component.Serializer.fromJson(compound.getString("CustomName"));
        }
        CompoundTag compoundRecipes = compound.getCompound("RecipesUsed");
        for (String key : compoundRecipes.getAllKeys()) {
            usedRecipeTracker.put(new ResourceLocation(key), compoundRecipes.getInt(key));
        }
    }
    
    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("CookTime", cookTime);
        compound.putInt("CookTimeTotal", cookTimeTotal);
        compound.put("Pastry", mealContainerStack.serializeNBT());
        if (customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(customName));
        }
        compound.put("Inventory", inventory.serializeNBT());
        CompoundTag compoundRecipes = new CompoundTag();
        usedRecipeTracker.forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
        compound.put("RecipesUsed", compoundRecipes);
    }
    
    private CompoundTag writeItems(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("Pastry", mealContainerStack.serializeNBT());
        compound.put("Inventory", inventory.serializeNBT());
        return compound;
    }
    
    public CompoundTag writeMeal(CompoundTag compound) {
        if (getMeal().isEmpty()) return compound;
        
        ItemStackHandler drops = new ItemStackHandler(INVENTORY_SIZE);
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            drops.setStackInSlot(i, i == MEAL_DISPLAY_SLOT ? inventory.getStackInSlot(i) : ItemStack.EMPTY);
        }
        if (customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(customName));
        }
        compound.put("Container", mealContainerStack.serializeNBT());
        compound.put("Inventory", drops.serializeNBT());
        return compound;
    }
    public static void potTick(Level level, BlockPos pos, BlockState state, PastryBoardBlockEntity cookingPot) {
        cookingTick(level, pos, state, cookingPot);
    }
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    
    }
    
    @Override
    public Component getName() {
        return customName != null ? customName : (Component) Lang.translateDirect("container.stew_pot");
    }
    
    @Override
    public Component getDisplayName() {
        return getName();
    }
    
    @Override
    @javax.annotation.Nullable
    public Component getCustomName() {
        return customName;
    }
    
    public void setCustomName(Component name) {
        customName = name;
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new PastryBoardMenu(pContainerId, pPlayerInventory, this, pastryBoardData);
    }
    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }
    private boolean doesMealHaveContainer(ItemStack meal) {
        return !mealContainerStack.isEmpty() || meal.hasCraftingRemainingItem();
    }
    protected boolean canCook(PastryBoardRecipe recipe) {
        if (hasInput()) {
            ItemStack resultStack = recipe.getResultItem();
            if (resultStack.isEmpty()) {
                return false;
            } else {
                ItemStack storedMealStack = inventory.getStackInSlot(MEAL_DISPLAY_SLOT);
                if (storedMealStack.isEmpty()) {
                    return true;
                } else if (!storedMealStack.sameItem(resultStack)) {
                    return false;
                } else if (storedMealStack.getCount() + resultStack.getCount() <= inventory.getSlotLimit(MEAL_DISPLAY_SLOT)) {
                    return true;
                } else {
                    return storedMealStack.getCount() + resultStack.getCount() <= resultStack.getMaxStackSize();
                }
            }
        } else {
            return false;
        }
    }
    private void moveMealToOutput() {
        ItemStack mealStack = inventory.getStackInSlot(MEAL_DISPLAY_SLOT);
        ItemStack outputStack = inventory.getStackInSlot(OUTPUT_SLOT);
        int mealCount = Math.min(mealStack.getCount(), mealStack.getMaxStackSize() - outputStack.getCount());
        if (outputStack.isEmpty()) {
            inventory.setStackInSlot(OUTPUT_SLOT, mealStack.split(mealCount));
        } else if (outputStack.getItem() == mealStack.getItem()) {
            mealStack.shrink(mealCount);
            outputStack.grow(mealCount);
        }
    }
    public NonNullList<ItemStack> getDroppableInventory() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            if (i != MEAL_DISPLAY_SLOT) {
                drops.add(inventory.getStackInSlot(i));
            }
        }
        return drops;
    }
    @Override
    public CompoundTag getUpdateTag() {
        return writeItems(new CompoundTag());
    }
    private boolean hasInput() {
        for (int i = 0; i < MEAL_DISPLAY_SLOT; ++i) {
            if (!inventory.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }
    public static void cookingTick(Level level, BlockPos pos, BlockState state, PastryBoardBlockEntity pastryBoard) {
        boolean didInventoryChange = false;
        if (pastryBoard.hasInput()) {
            Optional<PastryBoardRecipe> recipe = pastryBoard.getMatchingRecipe(new RecipeWrapper(pastryBoard.inventory));
            if (recipe.isPresent() && pastryBoard.canCook(recipe.get())) {
                didInventoryChange = pastryBoard.processCooking(recipe.get(), pastryBoard);
            } else {
                pastryBoard.cookTime = 0;
            }
        } else if (pastryBoard.cookTime > 0) {
                pastryBoard.cookTime = Mth.clamp(pastryBoard.cookTime - 4, 0, pastryBoard.cookTimeTotal);
        }
        
        ItemStack mealStack = pastryBoard.getMeal();
        if (!mealStack.isEmpty()) {
            if (!pastryBoard.doesMealHaveContainer(mealStack)) {
                pastryBoard.moveMealToOutput();
                didInventoryChange = true;
            } else if (!pastryBoard.inventory.getStackInSlot(CONTAINER_SLOT).isEmpty()) {
                didInventoryChange = true;
            }
        }
        
        if (didInventoryChange) {
            pastryBoard.inventoryChanged();
        }
    }
    public ItemStack getContainer() {
        ItemStack mealStack = getMeal();
        if (!mealStack.isEmpty() && !mealContainerStack.isEmpty()) {
            return mealContainerStack;
        } else {
            return mealStack.getCraftingRemainingItem();
        }
    }
    private boolean processCooking(PastryBoardRecipe recipe, PastryBoardBlockEntity cookingPot) {
        if (level == null) return false;
        
        ++cookTime;
        cookTimeTotal = 0;
        if (cookTime < cookTimeTotal) {
            return false;
        }
        
        cookTime = 0;
        mealContainerStack = recipe.getPastry();
        ItemStack resultStack = recipe.getResultItem();
        ItemStack storedMealStack = inventory.getStackInSlot(MEAL_DISPLAY_SLOT);
        if (storedMealStack.isEmpty()) {
            inventory.setStackInSlot(MEAL_DISPLAY_SLOT, resultStack.copy());
        } else if (storedMealStack.sameItem(resultStack)) {
            storedMealStack.grow(resultStack.getCount());
        }
        cookingPot.setRecipeUsed(recipe);
        return true;
    }
    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.ITEM_HANDLER)) {
            if (side == null || side.equals(Direction.UP)) {
                return inputHandler.cast();
            } else {
                return outputHandler.cast();
            }
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void setRemoved() {
        super.setRemoved();
        inputHandler.invalidate();
        outputHandler.invalidate();
    }
    @Override
    public void setRecipeUsed(@javax.annotation.Nullable Recipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation recipeID = recipe.getId();
            usedRecipeTracker.addTo(recipeID, 1);
        }
    }
    public ItemStack getMeal() {
        return inventory.getStackInSlot(MEAL_DISPLAY_SLOT);
    }
    public ItemStackHandler getInventory() {
        return inventory;
    }
    private Optional<PastryBoardRecipe> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
        if (level == null) return Optional.empty();
        
        if (lastRecipeID != null) {
            Recipe<RecipeWrapper> recipe = ((RecipeManagerAccessor) level.getRecipeManager())
                    .getRecipeMap(GDRecipeTypes.stewing.get())
                    .get(lastRecipeID);
            if (recipe instanceof PastryBoardRecipe) {
                if (recipe.matches(inventoryWrapper, level)) {
                    return Optional.of((PastryBoardRecipe) recipe);
                }
                if (recipe.getResultItem().sameItem(getMeal())) {
                    return Optional.empty();
                }
            }
        }
        
        if (checkNewRecipe) {
            Optional<PastryBoardRecipe> recipe = level.getRecipeManager().getRecipeFor(GDRecipeTypes.pastryMaking.get(), inventoryWrapper, level);
            if (recipe.isPresent()) {
                lastRecipeID = recipe.get().getId();
                return recipe;
            }
        }
        
        checkNewRecipe = false;
        return Optional.empty();
    }
    private ItemStackHandler createHandler() {
        return new ItemStackHandler(INVENTORY_SIZE)
        {
            @Override
            protected void onContentsChanged(int slot) {
                if (slot >= 0 && slot < MEAL_DISPLAY_SLOT) {
                    checkNewRecipe = true;
                }
                inventoryChanged();
            }
        };
    }
    private ContainerData createIntArray() {
        return new ContainerData()
        {
            @Override
            public int get(int pIndex) {
                return 0;
            }
            
            @Override
            public void set(int pIndex, int pValue) {
            
            }
            
            @Override
            public int getCount() {
                return 2;
            }
        };
    }
}
