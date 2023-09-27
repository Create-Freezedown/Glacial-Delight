package com.pouffydev.glacialdelight.content.block.steamer;

import com.google.common.collect.Lists;
import com.pouffydev.glacialdelight.content.block.base.HeatableBlockEntity;
import com.pouffydev.glacialdelight.content.block.util.GDMessages;
import com.pouffydev.glacialdelight.content.block.util.ItemStackSyncS2CPacket;
import com.pouffydev.glacialdelight.foundation.util.lang.Lang;
import com.pouffydev.glacialdelight.init.GDBlockEntities;
import com.pouffydev.glacialdelight.init.GDRecipeTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.mixin.accessor.RecipeManagerAccessor;
import vectorwing.farmersdelight.common.registry.ModParticleTypes;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;


public class SteamerBlockEntity extends HeatableBlockEntity implements MenuProvider, Nameable, RecipeHolder {
    private final ItemStackHandler itemHandler = new ItemStackHandler(12) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                GDMessages.sendToClients(new ItemStackSyncS2CPacket(this, worldPosition));
            }
        }
        
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0, 1, 2, 3, 4, 5 -> true;
                case 6, 7, 8, 9, 10, 11 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };
    public void setHandler(ItemStackHandler itemStackHandler) {
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, itemStackHandler.getStackInSlot(i));
        }
    }
    public static final int inputSlot0 = 0;
    public static final int inputSlot1 = 1;
    public static final int inputSlot2 = 2;
    public static final int inputSlot3 = 3;
    public static final int inputSlot4 = 4;
    public static final int inputSlot5 = 5;
    public static final int outputSlot0 = 6;
    public static final int outputSlot1 = 7;
    public static final int outputSlot2 = 8;
    public static final int outputSlot3 = 9;
    public static final int outputSlot4 = 10;
    public static final int outputSlot5 = 11;
    public static final int inventorySize = outputSlot5 + 1;
    private final ItemStackHandler inventory;
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    
    
    private int steamTime;
    private int steamTimeTotal;
    private Component customName;
    
    protected final ContainerData steamerData;
    private final Object2IntOpenHashMap<ResourceLocation> usedRecipeTracker;
    
    private ResourceLocation lastRecipeID;
    private boolean checkNewRecipe;
    public SteamerBlockEntity(BlockPos pos, BlockState state) {
        super(GDBlockEntities.steamer.get(), pos, state);
        this.inventory = createHandler();
        this.steamerData = createIntArray();
        this.usedRecipeTracker = new Object2IntOpenHashMap<>();
        this.checkNewRecipe = true;
    }
    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        inventory.deserializeNBT(compound.getCompound("Inventory"));
        steamTime = compound.getInt("CookTime");
        steamTimeTotal = compound.getInt("CookTimeTotal");
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
        compound.putInt("CookTime", steamTime);
        compound.putInt("CookTimeTotal", steamTimeTotal);
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
        compound.put("Inventory", inventory.serializeNBT());
        return compound;
    }
    
    public static void steamerTick(Level level, BlockPos pos, BlockState state, SteamerBlockEntity steamer) {
        generalTick(level, pos, state, steamer);
        if (steamer.isHeated(level, pos)) {
            cookingTick(level, pos, state, steamer);
        } else if (steamer.isFrozen(level, pos)) {
            steamer.steamTime = 0;
        }
    }
    public static void cookingTick(Level level, BlockPos pos, BlockState state, SteamerBlockEntity steamer) {
        boolean isHeated = steamer.isHeated(level, pos);
        boolean isSmouldering = steamer.smouldering(level, pos);
        boolean isKindled = steamer.kindled(level, pos);
        boolean isSeething = steamer.seething(level, pos);
        boolean isFrozen = steamer.isFrozen(level, pos);
        boolean didInventoryChange = false;
        int waterLevel = state.getValue(SteamerBlock.waterLevel);
        int steamLevel = state.getValue(SteamerBlock.steamLevel);
        if (isHeated) {
            if (level.getGameTime() % 20 == 0) {
            if (isSmouldering) {
                if (waterLevel > 0) {
                    state.setValue(SteamerBlock.waterLevel, Mth.clamp(waterLevel - 1, 0, waterLevel));
                    state.setValue(SteamerBlock.steamLevel, Mth.clamp(steamLevel + 1, steamLevel, 1999));
                }
            }
            if (isKindled) {
                if (waterLevel > 0) {
                    state.setValue(SteamerBlock.waterLevel, Mth.clamp(waterLevel - 2, 0, waterLevel));
                    state.setValue(SteamerBlock.steamLevel, Mth.clamp(steamLevel + 2, steamLevel, 1999));
                }
            }
            if (isSeething) {
                if (waterLevel > 0) {
                    state.setValue(SteamerBlock.waterLevel, Mth.clamp(waterLevel - 4, 0, waterLevel));
                    state.setValue(SteamerBlock.steamLevel, Mth.clamp(steamLevel + 4, steamLevel, 1999));
                }
            }
            if (isFrozen) {
                if (waterLevel > 0) {
                    state.setValue(SteamerBlock.steamLevel, Mth.clamp(steamLevel - 1, 0, steamLevel));
                    state.setValue(SteamerBlock.waterLevel, Mth.clamp(waterLevel + 1, waterLevel, 1999));
                }
            
            }
        }
            Optional<SteamingRecipe> recipe = steamer.getMatchingRecipe(new RecipeWrapper(steamer.inventory));
            if (recipe.isPresent()) {
                didInventoryChange = steamer.processCooking(recipe.get(), steamer, state);
            } else {
                steamer.steamTime = 0;
            }
        } else if (steamer.steamTime > 0) {
            if (steamer.smouldering(level, pos))
                steamer.steamTime = Mth.clamp(steamer.steamTime - 1, 0, steamer.steamTime);
            else if (steamer.kindled(level, pos))
                steamer.steamTime = Mth.clamp(steamer.steamTime - 2, 0, steamer.steamTime);
            else if (steamer.seething(level, pos))
                steamer.steamTime = Mth.clamp(steamer.steamTime - 4, 0, steamer.steamTime);
        }
        
        if (didInventoryChange) {
            steamer.inventoryChanged();
        }
    }
    public static void animationTick(Level level, BlockPos pos, BlockState state, SteamerBlockEntity steamer) {
        if (steamer.isHeated(level, pos)) {
            RandomSource random = level.random;
            if (random.nextFloat() < 0.2F) {
                double x = (double) pos.getX() + 0.5D + (random.nextDouble() * 0.6D - 0.3D);
                double y = (double) pos.getY() + 0.7D;
                double z = (double) pos.getZ() + 0.5D + (random.nextDouble() * 0.6D - 0.3D);
                level.addParticle(ParticleTypes.BUBBLE_POP, x, y, z, 0.0D, 0.0D, 0.0D);
            }
            if (random.nextFloat() < 0.05F) {
                double x = (double) pos.getX() + 0.5D + (random.nextDouble() * 0.4D - 0.2D);
                double y = (double) pos.getY() + 0.5D;
                double z = (double) pos.getZ() + 0.5D + (random.nextDouble() * 0.4D - 0.2D);
                double motionY = random.nextBoolean() ? 0.015D : 0.005D;
                level.addParticle(ModParticleTypes.STEAM.get(), x, y, z, 0.0D, motionY, 0.0D);
            }
        } else if (steamer.isFrozen(level, pos)) {
            RandomSource random = level.random;
            if (random.nextFloat() < 0.2F) {
                double x = (double) pos.getX() + 0.5D + (random.nextDouble() * 0.6D - 0.3D);
                double y = (double) pos.getY() + 0.7D;
                double z = (double) pos.getZ() + 0.5D + (random.nextDouble() * 0.6D - 0.3D);
                level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
        
    }
    private Optional<SteamingRecipe> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
        if (level == null) return Optional.empty();
        
        if (lastRecipeID != null) {
            Recipe<RecipeWrapper> recipe = ((RecipeManagerAccessor) level.getRecipeManager())
                    .getRecipeMap(GDRecipeTypes.stewing.get())
                    .get(lastRecipeID);
            if (recipe instanceof SteamingRecipe) {
                if (recipe.matches(inventoryWrapper, level)) {
                    return Optional.of((SteamingRecipe) recipe);
                }
            }
        }
        
        if (checkNewRecipe) {
            Optional<SteamingRecipe> recipe = level.getRecipeManager().getRecipeFor(GDRecipeTypes.steaming.get(), inventoryWrapper, level);
            if (recipe.isPresent()) {
                ResourceLocation newRecipeID = recipe.get().getId();
                if (lastRecipeID != null && !lastRecipeID.equals(newRecipeID)) {
                    steamTime = 0;
                }
                lastRecipeID = newRecipeID;
                return recipe;
            }
        }
        
        checkNewRecipe = false;
        return Optional.empty();
    }
    
    private boolean processCooking(SteamingRecipe recipe, SteamerBlockEntity steamer, BlockState state) {
        if (level == null) return false;
        int waterLevel = state.getValue(SteamerBlock.waterLevel);
        int steamLevel = state.getValue(SteamerBlock.steamLevel);
        ++steamTime;
        steamTimeTotal = recipe.getCookTime();
        if (steamTime < steamTimeTotal) {
            return false;
        }
        
        steamTime = 0;
        ItemStack resultStack = recipe.getResultItem();
        //get all 6 input slots by thier ID and do the same for the output slots. then process the recipe for each slot linked to the output slot
        for (int i = 0; i < 6; ++i) {
            if (!inventory.getStackInSlot(i).isEmpty() && steamLevel >= 50) {
                //the input slot IDs are 0-5 and the output slot IDs are 6-11
                steamer.inventory.extractItem(i, 1, false);
                state.setValue(SteamerBlock.steamLevel, Mth.clamp(steamLevel - 50, 0, steamLevel));
                steamer.inventory.setStackInSlot(i + 6, new ItemStack(resultStack.getItem(),
                        steamer.inventory.getStackInSlot(i + 6).getCount() + 1));
                steamer.setRecipeUsed(recipe);
            }
        }
        return true;
    }
    
    @Override
    public void setRecipeUsed(@javax.annotation.Nullable Recipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation recipeID = recipe.getId();
            usedRecipeTracker.addTo(recipeID, 1);
        }
    }
    
    @javax.annotation.Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }
    
    @Override
    public void awardUsedRecipes(Player player) {
        List<Recipe<?>> usedRecipes = getUsedRecipesAndPopExperience(player.level, player.position());
        player.awardRecipes(usedRecipes);
        usedRecipeTracker.clear();
    }
    
    public List<Recipe<?>> getUsedRecipesAndPopExperience(Level level, Vec3 pos) {
        List<Recipe<?>> list = Lists.newArrayList();
        
        for (Object2IntMap.Entry<ResourceLocation> entry : usedRecipeTracker.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                splitAndSpawnExperience((ServerLevel) level, pos, entry.getIntValue(), ((SteamingRecipe) recipe).getExperience());
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
    
    public ItemStackHandler getInventory() {
        return inventory;
    }
    
    public NonNullList<ItemStack> getDroppableInventory() {
        NonNullList<ItemStack> drops = NonNullList.create();
            for (int i = 0; i < inventorySize; ++i) {
                    drops.add(inventory.getStackInSlot(i));
            }
        return drops;
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
    
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory player, Player entity) {
        return new SteamerMenu(id, player, this, steamerData);
    }
    
    @SuppressWarnings("removal")
    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.ITEM_HANDLER)) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void setRemoved() {
        super.setRemoved();
        lazyItemHandler.invalidate();
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        return writeItems(new CompoundTag());
    }
    
    private ItemStackHandler createHandler() {
        int size= inventorySize;
        
        return new ItemStackHandler(size)
        {
            @Override
            protected void onContentsChanged(int slot) {
                if (slot >= 0) {
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
            public int get(int index) {
                return switch (index) {
                    case 0 -> SteamerBlockEntity.this.steamTime;
                    case 1 -> SteamerBlockEntity.this.steamTimeTotal;
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SteamerBlockEntity.this.steamTime = value;
                    case 1 -> SteamerBlockEntity.this.steamTimeTotal = value;
                }
            }
            
            @Override
            public int getCount() {
                return 2;
            }
        };
    }
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }
}
