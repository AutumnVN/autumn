package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import autumnvn.autumn.AutumnClient;
import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
public class CameraMixin {

    // ThirdPersonNoClip
    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    private void clipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> cir) {
        if (AutumnClient.options.thirdPersonNoClip.getValue()) {
            cir.setReturnValue(desiredCameraDistance);
        }
    }
}
