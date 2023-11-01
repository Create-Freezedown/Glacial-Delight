package com.pouffydev.glacialdelight.content.item.salvage;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public class BasicSalvageItem extends Item {
    public final ResourceLocation lootTable;
    public final SalvageContainers type;
    public BasicSalvageItem(Properties pProperties, ResourceLocation lootTable, SalvageContainers type) {
        super(pProperties);
        this.lootTable = lootTable;
        this.type = type;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            LootTable table = world.getServer().getLootTables().get(lootTable);
            LootContext context = (new LootContext.Builder((ServerLevel) world)).withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player).withParameter(LootContextParams.TOOL, itemstack).withParameter(LootContextParams.ORIGIN, player.position()).create(LootContextParamSets.GIFT);
            List<ItemStack> loot = table.getRandomItems(context);
            NonNullList<ItemStack> filteredLoot = NonNullList.create();
            for (ItemStack itemStack : loot) {
                boolean shouldGet = true;
                if (shouldGet) {
                    filteredLoot.add(itemStack);
                }
                for (ItemStack stack : filteredLoot) {
                    giveItem(player, stack);
                }
            }
            itemstack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, world.isClientSide());
    }
    public boolean giveItem(Player player, ItemStack itemStack) {
        if (player.getInventory().getFreeSlot() >= 0) {
            player.addItem(itemStack);
            return true;
        } else {
            player.drop(itemStack, true);
            return false;
        }
    }
}
