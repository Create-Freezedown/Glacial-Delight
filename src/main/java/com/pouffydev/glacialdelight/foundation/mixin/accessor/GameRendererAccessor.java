package com.pouffydev.glacialdelight.foundation.mixin.accessor;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Invoker("getFov")
    double create$callGetFov(Camera camera, float partialTicks, boolean useFOVSetting);
}
