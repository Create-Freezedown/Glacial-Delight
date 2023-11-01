package com.pouffydev.glacialdelight.init;

import com.google.common.collect.Sets;
import com.pouffydev.glacialdelight.GlacialDelight;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Set;

public class GDLootTables {
    private static final Set<ResourceLocation> LOCATIONS = Sets.newHashSet();
    public static final ResourceLocation foodContainerSalvage = register("container_salvage/generic_food_container");
    public static final ResourceLocation tinSalvage = register("container_salvage/tin");
    public static final ResourceLocation vegetableTinSalvage = register("container_salvage/vegetable_tin");
    public static final ResourceLocation boxSalvage = register("container_salvage/box");
    private static final Set<ResourceLocation> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOCATIONS);
    
    private static ResourceLocation register(String location) {
        return register(new ResourceLocation(GlacialDelight.ID, location));
    }
    
    private static ResourceLocation register(ResourceLocation location) {
        if (LOCATIONS.add(location)) {
            return location;
        } else {
            throw new IllegalArgumentException(location + " is already a registered built-in loot table");
        }
    }
    private static ResourceLocation getLootTable() {
        return new ResourceLocation(GlacialDelight.ID, "container_salvage/generic_food_container");
    }
    
    public static Set<ResourceLocation> all() {
        return IMMUTABLE_LOCATIONS;
    }
}
