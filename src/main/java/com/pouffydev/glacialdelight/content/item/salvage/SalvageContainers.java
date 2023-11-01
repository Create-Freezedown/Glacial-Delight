package com.pouffydev.glacialdelight.content.item.salvage;

import com.pouffydev.glacialdelight.foundation.util.lang.Lang;
import net.minecraft.network.chat.MutableComponent;

public enum SalvageContainers {
    box("box"),
    tin("tin"),
    can("can")
    ;
    
    private final String name;
    
    SalvageContainers(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public static MutableComponent getTranslation(SalvageContainers container) {
        return Lang.translate("salvage_" + container.getName()).component();
    }
    
}
