package com.pouffydev.glacialdelight.content.block.heater.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pouffydev.glacialdelight.content.block.util.HeaterLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

import static com.pouffydev.glacialdelight.content.block.heater.data.FuelDataListener.heaterFuel;

public class FuelUtil {
    
    public static ItemStack isFuel(ItemStack stack) {
        for (JsonElement jsonElement : heaterFuel) {
            JsonObject json = jsonElement.getAsJsonObject();
            String fuelItem = json.get("fuel_item").getAsString();
            if (stack.getItem().getName(stack).toString().equals(fuelItem)) {
                return stack;
            }
        }
        return null;
    }
    public static ItemStack getFuelItem() {
        for (JsonElement jsonElement : heaterFuel) {
            JsonObject json = jsonElement.getAsJsonObject();
            String fuelItem = json.get("fuel_item").getAsString();
            return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(fuelItem))).getDefaultInstance();
        }
        return null;
    }
    public static int getFuelDuration() {
        for (JsonElement jsonElement : heaterFuel) {
            JsonObject json = jsonElement.getAsJsonObject();
            int fuelDuration = json.get("fuel_duration").getAsInt();
            return fuelDuration;
        }
        return 0;
    }
    public static int getHeatLevelAsInt() {
        for (JsonElement jsonElement : heaterFuel) {
            JsonObject json = jsonElement.getAsJsonObject();
            int heatLevel = json.get("heat_level").getAsInt();
            return heatLevel;
        }
        return 0;
    }
    public static HeaterLevel getHeatLevel(int lvl) {
        if (lvl <= 1) {
            return HeaterLevel.FREEZING;
        }
        if (lvl == 2) {
            return HeaterLevel.SMOULDERING;
        }
        if (lvl == 3) {
            return HeaterLevel.KINDLED;
        }
        if (lvl >= 4) {
            return HeaterLevel.SEETHING;
        }
        return HeaterLevel.NONE;
    }
    public static HeaterLevel decreaseLevel(HeaterLevel lvl) {
        if (lvl == HeaterLevel.FREEZING) {
            return HeaterLevel.NONE;
        }
        if (lvl == HeaterLevel.SMOULDERING) {
            return HeaterLevel.NONE;
        }
        if (lvl == HeaterLevel.KINDLED) {
            return HeaterLevel.SMOULDERING;
        }
        if (lvl == HeaterLevel.SEETHING) {
            return HeaterLevel.KINDLED;
        }
        return HeaterLevel.NONE;
    }
}
