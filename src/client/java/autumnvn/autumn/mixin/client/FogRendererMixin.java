package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    // NoFog
    @Inject(method = "setupFog", at = @At("RETURN"), cancellable = true)
    private void setupFog(Camera camera, int viewDistance, DeltaTracker tickCounter, float partialTick, ClientLevel world, CallbackInfoReturnable<FogData> cir) {
        if (AutumnClient.options.noFog.get()) {
            cir.getReturnValue().color.w = 0.0F;
        }
    }
}