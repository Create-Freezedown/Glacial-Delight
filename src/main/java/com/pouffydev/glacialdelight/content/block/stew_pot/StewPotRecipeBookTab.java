package com.pouffydev.glacialdelight.content.block.stew_pot;

public enum StewPotRecipeBookTab {
    stews("stews"),
    soups("soups"),
    drinks("drinks"),
    misc("misc");
    
    public final String name;
    
    StewPotRecipeBookTab(String name) {
        this.name = name;
    }
    
    public static StewPotRecipeBookTab findByName(String name) {
        for (StewPotRecipeBookTab value : values()) {
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
