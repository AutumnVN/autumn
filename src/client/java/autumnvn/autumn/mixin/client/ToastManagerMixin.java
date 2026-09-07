package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastManager.class)
public class ToastManagerMixin {

    // NoToast
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void extractRenderState(GuiGraphicsExtractor extractor, CallbackInfo ci) {
        if (AutumnClient.options.noToast.get()) {
            ci.cancel();
        }
    }

    @Inject(method = "addToast", at = @At("HEAD"), cancellable = true)
    public void addToast(Toast toast, CallbackInfo ci) {
        if (AutumnClient.options.noToast.get()) {
            ci.cancel();
        }
    }

    @Mixin(targets = "net.minecraft.client.gui.components.toasts.ToastManager$ToastInstance")
    static class Entry {
        @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
        public void extractRenderState(GuiGraphicsExtractor extractor, int x, CallbackInfo ci) {
            if (AutumnClient.options.noToast.get()) {
                ci.cancel();
            }
        }
    }
}