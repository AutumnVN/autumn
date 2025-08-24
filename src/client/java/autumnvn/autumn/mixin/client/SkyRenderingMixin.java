package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.render.SkyRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRendering.class)
public class SkyRenderingMixin {

    // NoFog
    @Inject(method = "renderTopSky", at = @At("HEAD"), cancellable = true)
    private static void renderTopSky(CallbackInfo ci) {
        if (AutumnClient.options.noFog.getValue()) {
            ci.cancel();
        }
    }
}
