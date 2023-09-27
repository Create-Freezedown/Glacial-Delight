package com.pouffydev.glacialdelight.content.block.steamer;


public enum SteamingRecipeBookTab {
    vegetables("vegetables"),
    meats("meats"),
    buns("buns"),
    misc("misc");
    
    public final String name;
    
    SteamingRecipeBookTab(String name) {
        this.name = name;
    }
    
    public static SteamingRecipeBookTab findByName(String name) {
        for (SteamingRecipeBookTab value : values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
