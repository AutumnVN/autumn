package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public class SkyRenderingMixin {

    // NoFog
    @Inject(method = "renderSkyDisc", at = @At("HEAD"), cancellable = true)
    private void renderSkyDisc(int color, CallbackInfo ci) {
        if (AutumnClient.options.noFog.get()) {
            ci.cancel();
        }
    }
}