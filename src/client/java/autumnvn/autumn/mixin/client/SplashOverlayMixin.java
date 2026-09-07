package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public class SplashOverlayMixin {

    @Shadow
    private long fadeOutStart;

    // NoFade
    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void extractRenderState(GuiGraphicsExtractor extractor, int x, int y, float partialTick, CallbackInfo ci) {
        if (AutumnClient.options.noFade.get() && fadeOutStart > 0) {
            fadeOutStart = 0;
        }
    }
}