package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import autumnvn.autumn.AutumnClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    // NoFog
    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void applyFog(Camera camera, FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        if (AutumnClient.options.noFog.getValue() && fogType == FogType.FOG_TERRAIN) {
            RenderSystem.setShaderFogStart(32766);
            RenderSystem.setShaderFogEnd(32767);
        }
    }
}
