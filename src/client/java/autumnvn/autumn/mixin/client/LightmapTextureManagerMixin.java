package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import autumnvn.autumn.AutumnClient;
import net.minecraft.client.render.LightmapTextureManager;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    // FullBright
    @Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
    private static void getBrightness(CallbackInfoReturnable<Float> ci) {
        if (AutumnClient.options.fullBright.getValue()) {
            ci.setReturnValue(1.0f);
        }
    }
}
