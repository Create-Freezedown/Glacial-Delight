package com.pouffydev.glacialdelight.content.crop.kindleberry;

import com.pouffydev.glacialdelight.foundation.util.lang.Lang;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class KindleberrySeedsItem extends ItemNameBlockItem {
    
    public final int mutationLvl;
    public KindleberrySeedsItem(Block pBlock, Properties pProperties, int mutationLvl) {
        super(pBlock, pProperties);
        this.mutationLvl = mutationLvl;
    }
    private static ItemStack getItemFromID(String modid, String id) {
        return ForgeRegistries.ITEMS.getValue(new net.minecraft.resources.ResourceLocation(modid, id)).asItem().getDefaultInstance();
    }
    public int getMutationLvl() {
        return mutationLvl;
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Component mutationLvl_tt = Lang.translateDirect("crops.mutation_level", getMutationLvl());
        //Component predictedYield = Lang.translateDirect("crops.predicted_yield.kindleberry" + getMutationLvl());
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(mutationLvl_tt);
        //tooltip.add(predictedYield);
    }
    
    private static ItemStack[] getMainCropYield(int mutationLvl) {
        switch (mutationLvl) {
            case 0:
                return new ItemStack[]{getItemFromID("glacialdelight","smoulderberry"), getItemFromID("glacialdelight","kindleberry")};
            case 1:
                return new ItemStack[]{getItemFromID("glacialdelight","kindleberry")};
            case 2:
                return new ItemStack[]{getItemFromID("glacialdelight","kindleberry")};
            case 3:
                return new ItemStack[]{getItemFromID("glacialdelight","scorchberry")};
            default:
                return new ItemStack[]{getItemFromID("glacialdelight","kindleberry")};
        }
    }
    @SuppressWarnings("DuplicateBranchesInSwitch")
    private static ItemStack[] getSecondaryCropYield(int mutationLvl) {
        switch (mutationLvl) {
            case 0:
                return new ItemStack[]{getItemFromID("glacialdelight","scorchberry")};
            case 1:
                return new ItemStack[]{getItemFromID("glacialdelight","kindleberry"), getItemFromID("glacialdelight","scorchberry")};
            case 2:
                return new ItemStack[]{getItemFromID("glacialdelight","scorchberry")};
            case 3:
                return new ItemStack[]{};
            default:
                return new ItemStack[]{getItemFromID("glacialdelight","scorchberry")};
        }
    }
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        Component mainCropYield_DN = Lang.translateDirect("crops.main_crop_yield");
        Component secondaryCropYield_DN = Lang.translateDirect("crops.secondary_crop_yield");
        //Make the NonNullList<ItemStack> items and add the mainCropYield and secondaryCropYield to it
        NonNullList<ItemStack> items = NonNullList.create();
        for (ItemStack itemStack : getMainCropYield(mutationLvl)) {
            items.add(new ItemStack(itemStack.getItem()).setHoverName(mainCropYield_DN));
        }
        for (ItemStack itemStack : getSecondaryCropYield(mutationLvl)) {
            items.add(new ItemStack(itemStack.getItem()).setHoverName(secondaryCropYield_DN));
        }
                
        return Optional.of(new BundleTooltip(items, 0));
    }
}
