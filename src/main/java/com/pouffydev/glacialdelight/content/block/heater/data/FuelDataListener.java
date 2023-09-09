package com.pouffydev.glacialdelight.content.block.heater.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FuelDataListener extends SimpleJsonResourceReloadListener {
    public static List<JsonElement> heaterFuel;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final FuelDataListener instance = new FuelDataListener();
    public FuelDataListener() {
        super(GSON, "heater_fuel");
        heaterFuel = new ArrayList<>();
        LOGGER.info("do you work?");
    }
    
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        files.forEach((resourceLocation, jsonElement) -> {
            LOGGER.info("%s ||| %s".formatted(resourceLocation, jsonElement));
            heaterFuel.add(jsonElement);
        });
    }
}
