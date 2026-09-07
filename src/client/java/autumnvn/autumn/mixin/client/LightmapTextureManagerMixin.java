package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Lightmap.class)
public class LightmapTextureManagerMixin {

    // FullBright
    @Inject(method = "render", at = @At("HEAD"))
    private void render(LightmapRenderState renderState, CallbackInfo ci) {
        if (AutumnClient.options.fullBright.get()) {
            renderState.brightness = 100.0F;
        }
    }
}