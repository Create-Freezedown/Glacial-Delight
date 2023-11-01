package com.pouffydev.glacialdelight.content.item.salvage;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SalvageTin extends BasicSalvageItem {
    public SalvageTin(Properties pProperties, ResourceLocation lootTable) {
        super(pProperties, lootTable, SalvageContainers.tin);
    }
    public MutableComponent getItemName() {
        return SalvageContainers.getTranslation(type);
    }
    @Override
    public Component getName(ItemStack stack) {
        return getItemName();
    }
}
