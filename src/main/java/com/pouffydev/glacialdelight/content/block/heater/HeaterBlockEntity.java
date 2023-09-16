package com.pouffydev.glacialdelight.content.block.heater;

import com.pouffydev.glacialdelight.content.block.heater.data.FuelUtil;
import com.pouffydev.glacialdelight.content.block.util.GDBlockEntity;
import com.pouffydev.glacialdelight.content.block.util.HeaterLevel;
import com.pouffydev.glacialdelight.foundation.block_entity.behaviour.BlockEntityBehaviour;
import com.pouffydev.glacialdelight.foundation.util.VecHelper;
import com.pouffydev.glacialdelight.init.GDBlockEntities;
import com.pouffydev.glacialdelight.init.GDRecipeTypes;
import com.pouffydev.glacialdelight.init.GDTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemStackHandler;
import vectorwing.farmersdelight.common.mixin.accessor.RecipeManagerAccessor;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import java.util.List;
import java.util.Optional;

import static com.pouffydev.glacialdelight.content.block.heater.HeaterBlock.heatLevel;
import static com.pouffydev.glacialdelight.content.block.heater.HeaterBlockEntity.FuelType.NORMAL;
import static com.pouffydev.glacialdelight.content.block.heater.HeaterBlockEntity.FuelType.SPECIAL;
import static com.pouffydev.glacialdelight.content.block.util.HeaterLevel.*;

public class HeaterBlockEntity extends GDBlockEntity {
    private static final VoxelShape GRILLING_AREA = Block.box(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
    private static final int INVENTORY_SLOT_COUNT = 6;
    private final ItemStackHandler inventory = this.createHandler();
    private int[] cookingTimes = new int[6];
    private int[] cookingTimesTotal = new int[6];
    private final ResourceLocation[] lastRecipeIDs = new ResourceLocation[6];
    
    public HeaterBlockEntity(BlockPos pos, BlockState state) {
        super(GDBlockEntities.heater.get(), pos, state);
        activeFuel = FuelType.NONE;
        remainingBurnTime = 0;
    }
    
    //public void load(CompoundTag compound) {
    //    super.load(compound);
    //    if (compound.contains("Inventory")) {
    //        this.inventory.deserializeNBT(compound.getCompound("Inventory"));
    //    } else {
    //        this.inventory.deserializeNBT(compound);
    //    }
    //
    //    int[] arrayCookingTimesTotal;
    //    if (compound.contains("CookingTimes", 11)) {
    //        arrayCookingTimesTotal = compound.getIntArray("CookingTimes");
    //        System.arraycopy(arrayCookingTimesTotal, 0, this.cookingTimes, 0, Math.min(this.cookingTimesTotal.length, arrayCookingTimesTotal.length));
    //    }
    //
    //    if (compound.contains("CookingTotalTimes", 11)) {
    //        arrayCookingTimesTotal = compound.getIntArray("CookingTotalTimes");
    //        System.arraycopy(arrayCookingTimesTotal, 0, this.cookingTimesTotal, 0, Math.min(this.cookingTimesTotal.length, arrayCookingTimesTotal.length));
    //    }
    //
    //}
    public HeaterLevel heaterLevel(HeaterLevel hLevel, Level world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof HeaterBlock ? world.getBlockState(pos).getValue(heatLevel) : hLevel;
    }
    private void smokingRecipe() {
        if (this.level != null) {
            boolean didInventoryChange = false;
            
            for(int i = 0; i < this.inventory.getSlots(); ++i) {
                ItemStack stoveStack = this.inventory.getStackInSlot(i);
                if (!stoveStack.isEmpty()) {
                    int var10002 = this.cookingTimes[i]++;
                    if (this.cookingTimes[i] >= this.cookingTimesTotal[i]) {
                        Container inventoryWrapper = new SimpleContainer(stoveStack);
                        Optional<CampfireCookingRecipe> recipe = this.getCampfireRecipe(inventoryWrapper, i);
                        if (recipe.isPresent()) {
                            ItemStack resultStack = recipe.get().getResultItem();
                            if (!resultStack.isEmpty()) {
                                ItemUtils.spawnItemEntity(this.level, resultStack.copy(), (double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 1.0, (double)this.worldPosition.getZ() + 0.5, this.level.random.nextGaussian() * 0.009999999776482582, 0.10000000149011612, this.level.random.nextGaussian() * 0.009999999776482582);
                            }
                        }
                        
                        this.inventory.setStackInSlot(i, ItemStack.EMPTY);
                        didInventoryChange = true;
                    }
                }
            }
            
            if (didInventoryChange) {
                this.inventoryChanged();
            }
            
        }
    }
    private void freezingRecipe() {
        if (this.level != null) {
            boolean didInventoryChange = false;
            
            for(int i = 0; i < this.inventory.getSlots(); ++i) {
                ItemStack stoveStack = this.inventory.getStackInSlot(i);
                if (!stoveStack.isEmpty()) {
                    int var10002 = this.cookingTimes[i]++;
                    if (this.cookingTimes[i] >= this.cookingTimesTotal[i]) {
                        Container inventoryWrapper = new SimpleContainer(stoveStack);
                        Optional<HeaterFreezingRecipe> recipe = this.getFreezingRecipe(inventoryWrapper, i);
                        if (recipe.isPresent()) {
                            ItemStack resultStack = recipe.get().getResultItem();
                            if (!resultStack.isEmpty()) {
                                ItemUtils.spawnItemEntity(this.level, resultStack.copy(), (double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 1.0, (double)this.worldPosition.getZ() + 0.5, this.level.random.nextGaussian() * 0.009999999776482582, 0.10000000149011612, this.level.random.nextGaussian() * 0.009999999776482582);
                            }
                        }
                        
                        this.inventory.setStackInSlot(i, ItemStack.EMPTY);
                        didInventoryChange = true;
                    }
                }
            }
            
            if (didInventoryChange) {
                this.inventoryChanged();
            }
            
        }
    }
    public static void generalTick(Level level, BlockPos pos, BlockState state, HeaterBlockEntity heater) {
        int fuelTime = HeaterBlock.fuelDuration;
        HeaterLevel heaterLevel = state.getValue(heatLevel);
        HeaterLevel heatLevel = FuelUtil.decreaseLevel(heaterLevel);
        heater.tick();
        cookingTick(level, pos, state, heater);
    }
    public static void cookingTick(Level level, BlockPos pos, BlockState state, HeaterBlockEntity heater) {
        HeaterLevel heaterLevel = state.getValue(heatLevel);
        if (heater.isHeaterBlockedAbove()) {
            if (!ItemUtils.isInventoryEmpty(heater.inventory)) {
                ItemUtils.dropItems(level, pos, heater.inventory);
                heater.inventoryChanged();
            }
        } else if (heaterLevel == FREEZING) {
            heater.freezingRecipe();
        } else if (heaterLevel.isAtLeast(SMOULDERING)) {
            heater.smokingRecipe();
        }else {
            for(int i = 0; i < heater.inventory.getSlots(); ++i) {
                if (heater.cookingTimes[i] > 0) {
                    heater.cookingTimes[i] = Mth.clamp(heater.cookingTimes[i] - 2, 0, heater.cookingTimesTotal[i]);
                }
            }
        }
        
    }
    
    public static void animationTick(Level level, BlockPos pos, BlockState state, HeaterBlockEntity stove) {
        for(int i = 0; i < stove.inventory.getSlots(); ++i) {
            if (!stove.inventory.getStackInSlot(i).isEmpty() && level.random.nextFloat() < 0.2F) {
                Vec2 stoveItemVector = stove.getStoveItemOffset(i);
                Direction direction = state.getValue(HeaterBlock.facing);
                HeaterLevel heaterLevel = state.getValue(heatLevel);
                int directionIndex = direction.get2DDataValue();
                Vec2 offset = directionIndex % 2 == 0 ? stoveItemVector : new Vec2(stoveItemVector.y, stoveItemVector.x);
                double x = (double)pos.getX() + 0.5 - (double)((float)direction.getStepX() * offset.x) + (double)((float)direction.getClockWise().getStepX() * offset.x);
                double y = (double)pos.getY() + 1.0;
                double z = (double)pos.getZ() + 0.5 - (double)((float)direction.getStepZ() * offset.y) + (double)((float)direction.getClockWise().getStepZ() * offset.y);
                if (heaterLevel == KINDLED || heaterLevel == SMOULDERING || heaterLevel == SEETHING) {
                    for(int k = 0; k < 3; ++k) {
                        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 5.0E-4, 0.0);
                    }
                }
                if (heaterLevel == FREEZING) {
                    for(int k = 0; k < 3; ++k) {
                        level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0.0, 5.0E-4, 0.0);
                    }
                }
            }
        }
        
    }
    
    public int getNextEmptySlot() {
        for(int i = 0; i < this.inventory.getSlots(); ++i) {
            ItemStack slotStack = this.inventory.getStackInSlot(i);
            if (slotStack.isEmpty()) {
                return i;
            }
        }
        
        return -1;
    }
    
    public boolean addItem(ItemStack itemStackIn, CampfireCookingRecipe recipe, int slot) {
        if (0 <= slot && slot < this.inventory.getSlots()) {
            ItemStack slotStack = this.inventory.getStackInSlot(slot);
            if (slotStack.isEmpty()) {
                this.cookingTimesTotal[slot] = recipe.getCookingTime();
                this.cookingTimes[slot] = 0;
                this.inventory.setStackInSlot(slot, itemStackIn.split(1));
                this.lastRecipeIDs[slot] = recipe.getId();
                this.inventoryChanged();
                return true;
            }
        }
        
        return false;
    }
    public boolean addItem(ItemStack itemStackIn, HeaterFreezingRecipe recipe, int slot) {
        if (0 <= slot && slot < this.inventory.getSlots()) {
            ItemStack slotStack = this.inventory.getStackInSlot(slot);
            if (slotStack.isEmpty()) {
                this.cookingTimesTotal[slot] = recipe.getCookingTime();
                this.cookingTimes[slot] = 0;
                this.inventory.setStackInSlot(slot, itemStackIn.split(1));
                this.lastRecipeIDs[slot] = recipe.getId();
                this.inventoryChanged();
                return true;
            }
        }
        
        return false;
    }
    public Optional<CampfireCookingRecipe> getCampfireRecipe(Container recipeWrapper, int slot) {
        if (this.level == null) {
            return Optional.empty();
        } else {
            if (this.lastRecipeIDs[slot] != null) {
                Recipe<Container> recipe = ((RecipeManagerAccessor)this.level.getRecipeManager()).getRecipeMap(RecipeType.CAMPFIRE_COOKING).get(this.lastRecipeIDs[slot]);
                if (recipe instanceof CampfireCookingRecipe && recipe.matches(recipeWrapper, this.level)) {
                    return Optional.of((CampfireCookingRecipe)recipe);
                }
            }
            
            return this.level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, recipeWrapper, this.level);
        }
    }
    public Optional<HeaterFreezingRecipe> getFreezingRecipe(Container recipeWrapper, int slot) {
        if (this.level == null) {
            return Optional.empty();
        } else {
            if (this.lastRecipeIDs[slot] != null) {
                Recipe<Container> recipe = ((RecipeManagerAccessor)this.level.getRecipeManager()).getRecipeMap(GDRecipeTypes.freezing.get()).get(this.lastRecipeIDs[slot]);
                if (recipe instanceof HeaterFreezingRecipe && recipe.matches(recipeWrapper, this.level)) {
                    return Optional.of((HeaterFreezingRecipe)recipe);
                }
            }
            
            return this.level.getRecipeManager().getRecipeFor(GDRecipeTypes.freezing.get(), recipeWrapper, this.level);
        }
    }
    public ItemStackHandler getInventory() {
        return this.inventory;
    }
    
    public boolean isHeaterBlockedAbove() {
        if (this.level != null) {
            BlockState above = this.level.getBlockState(this.worldPosition.above());
            return Shapes.joinIsNotEmpty(GRILLING_AREA, above.getShape(this.level, this.worldPosition.above()), BooleanOp.AND);
        } else {
            return false;
        }
    }
    
    public Vec2 getStoveItemOffset(int index) {
        float X_OFFSET = 0.3F;
        float Y_OFFSET = 0.2F;
        Vec2[] OFFSETS = new Vec2[]{new Vec2(0.3F, 0.2F), new Vec2(0.0F, 0.2F), new Vec2(-0.3F, 0.2F), new Vec2(0.3F, -0.2F), new Vec2(0.0F, -0.2F), new Vec2(-0.3F, -0.2F)};
        return OFFSETS[index];
    }
    private CompoundTag writeItems(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("Inventory", this.inventory.serializeNBT());
        return compound;
    }
    public CompoundTag getUpdateTag() {
        return this.writeItems(new CompoundTag());
    }
    
    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }
    public static final int MAX_HEAT_CAPACITY = 10000;
    public static final int INSERTION_THRESHOLD = 500;
    
    protected FuelType activeFuel;
    protected int remainingBurnTime;
    public FuelType getActiveFuel() {
        return activeFuel;
    }
    public int getRemainingBurnTime() {
        return remainingBurnTime;
    }
    public void updateBlockState() {
        setBlockHeat(getHeatLevel());
    }
    public HeaterLevel getHeatLevelFromBlock() {
        return HeaterBlock.getHeatLevelOf(getBlockState());
    }
    protected void setBlockHeat(HeaterLevel heat) {
        HeaterLevel inBlockState = getHeatLevelFromBlock();
        if (inBlockState == heat)
            return;
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(heatLevel, heat));
        notifyUpdate();
    }
    protected HeaterLevel getHeatLevel() {
        HeaterLevel level = NONE;
        switch (activeFuel) {
            case SPECIAL:
                level = HeaterLevel.SEETHING;
                break;
            case NORMAL:
                boolean lowPercent = (double) remainingBurnTime / MAX_HEAT_CAPACITY < 0.0125;
                level = lowPercent ? SMOULDERING : HeaterLevel.KINDLED;
                break;
            case FREEZING:
                level = HeaterLevel.FREEZING;
                break;
            default:
            case NONE:
                break;
        }
        return level;
    }
    protected boolean tryUpdateFuel(ItemStack itemStack, boolean forceOverflow, boolean simulate) {
        FuelType newFuel = FuelType.NONE;
        int newBurnTime;
        
        if (GDTags.AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.matches(itemStack)) {
            newBurnTime = 3200;
            newFuel = SPECIAL;
        } else if (GDTags.AllItemTags.BLAZE_BURNER_FUEL_FREEZING.matches(itemStack)) {
            newBurnTime = 6400;
            newFuel = FuelType.FREEZING;
        } else {
            newBurnTime = ForgeHooks.getBurnTime(itemStack, null);
            if (newBurnTime > 0) {
                newFuel = NORMAL;
            } else if (GDTags.AllItemTags.BLAZE_BURNER_FUEL_REGULAR.matches(itemStack)) {
                newBurnTime = 1600; // Same as coal
                newFuel = NORMAL;
            }
        }
        
        if (newFuel == FuelType.NONE)
            return false;
        if (newFuel.ordinal() < activeFuel.ordinal())
            return false;
        
        if (newFuel == activeFuel) {
            if (remainingBurnTime <= INSERTION_THRESHOLD) {
                newBurnTime += remainingBurnTime;
            } else if (forceOverflow && newFuel == NORMAL) {
                if (remainingBurnTime < MAX_HEAT_CAPACITY) {
                    newBurnTime = Math.min(remainingBurnTime + newBurnTime, MAX_HEAT_CAPACITY);
                } else {
                    newBurnTime = remainingBurnTime;
                }
            } else {
                return false;
            }
        }
        
        if (simulate)
            return true;
        
        activeFuel = newFuel;
        remainingBurnTime = newBurnTime;
        
        if (level.isClientSide) {
            spawnParticleBurst(activeFuel == SPECIAL);
            return true;
        }
        
        HeaterLevel prev = getHeatLevelFromBlock();
        playSound();
        updateBlockState();
        
        if (prev != getHeatLevelFromBlock())
            level.playSound(null, worldPosition, SoundEvents.BLAZE_AMBIENT, SoundSource.BLOCKS,
                    .125f + level.random.nextFloat() * .125f, 1.15f - level.random.nextFloat() * .25f);
        
        return true;
    }
    protected void playSound() {
        level.playSound(null, worldPosition, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS,
                .125f + level.random.nextFloat() * .125f, .75f - level.random.nextFloat() * .25f);
    }
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}
    
    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("fuelLevel", activeFuel.ordinal());
        compound.putInt("burnTimeRemaining", remainingBurnTime);
        compound.putIntArray("CookingTimes", cookingTimes);
        compound.putIntArray("CookingTotalTimes", cookingTimesTotal);
        super.write(compound, clientPacket);
    }
    
    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        activeFuel = FuelType.values()[compound.getInt("fuelLevel")];
        remainingBurnTime = compound.getInt("burnTimeRemaining");
        cookingTimes = compound.getIntArray("CookingTimes");
        cookingTimesTotal = compound.getIntArray("CookingTotalTimes");
        super.read(compound, clientPacket);
    }
    protected void spawnParticles(HeaterLevel heatLevel, double burstMult) {
        if (level == null)
            return;
        if (heatLevel == HeaterLevel.NONE)
            return;
        
        RandomSource r = level.getRandom();
        
        Vec3 c = VecHelper.getCenterOf(worldPosition);
        Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .125f)
                .multiply(1, 0, 1));
        
        if (r.nextInt(4) != 0)
            return;
        
        boolean empty = level.getBlockState(worldPosition.above())
                .getCollisionShape(level, worldPosition.above())
                .isEmpty();
        
        if (empty || r.nextInt(8) == 0)
            level.addParticle(ParticleTypes.LARGE_SMOKE, v.x, v.y, v.z, 0, 0, 0);
        
        double yMotion = empty ? .0625f : r.nextDouble() * .0125f;
        Vec3 v2 = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .5f)
                        .multiply(1, .25f, 1)
                        .normalize()
                        .scale((empty ? .25f : .5) + r.nextDouble() * .125f))
                .add(0, .5, 0);
        
        if (heatLevel.isAtLeast(SEETHING)) {
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, v2.x, v2.y, v2.z, 0, yMotion, 0);
        } else if (heatLevel.isAtLeast(KINDLED)) {
            level.addParticle(ParticleTypes.FLAME, v2.x, v2.y, v2.z, 0, yMotion, 0);
        } else if (heatLevel.isAtLeast(FREEZING)) {
            level.addParticle(ParticleTypes.SNOWFLAKE, v2.x, v2.y, v2.z, 0, yMotion, 0);
        }
        return;
    }
    public void spawnParticleBurst(boolean soulFlame) {
        Vec3 c = VecHelper.getCenterOf(worldPosition);
        RandomSource r = level.random;
        for (int i = 0; i < 20; i++) {
            Vec3 offset = VecHelper.offsetRandomly(Vec3.ZERO, r, .5f)
                    .multiply(1, .25f, 1)
                    .normalize();
            Vec3 v = c.add(offset.scale(.5 + r.nextDouble() * .125f))
                    .add(0, .125, 0);
            Vec3 m = offset.scale(1 / 32f);
            
            level.addParticle(soulFlame ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, v.x, v.y, v.z, m.x, m.y,
                    m.z);
        }
    }
    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) {
            if (!isVirtual())
                spawnParticles(getHeatLevelFromBlock(), 1);
            return;
        }
        
        if (remainingBurnTime > 0)
            remainingBurnTime--;
        
        if (activeFuel == FuelType.NORMAL)
            updateBlockState();
        if (remainingBurnTime > 0)
            return;
        
        if (activeFuel == FuelType.SPECIAL) {
            activeFuel = FuelType.NORMAL;
            remainingBurnTime = MAX_HEAT_CAPACITY / 2;
        } else
            activeFuel = FuelType.NONE;
        
        updateBlockState();
    }
    public enum FuelType {
        NONE, NORMAL, SPECIAL, FREEZING
    }
    
}
