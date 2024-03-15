package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import autumnvn.autumn.AutumnClient;
import net.minecraft.client.gui.screen.SplashOverlay;

@Mixin(SplashOverlay.class)
public class SplashOverlayMixin {

    @Shadow
    private long reloadCompleteTime;

    @Inject(method = "render", at = @At("HEAD"))
    private void render(CallbackInfo ci) {
        if (AutumnClient.options.noFade.getValue() && reloadCompleteTime > 0) {
            reloadCompleteTime = 0;
        }
    }

}
