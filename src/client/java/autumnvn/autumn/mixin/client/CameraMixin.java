package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import autumnvn.autumn.AutumnClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;

@Mixin(Camera.class)
public class CameraMixin {

    // ThirdPersonNoClip
    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    private void clipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> cir) {
        if (AutumnClient.options.thirdPersonNoClip.getValue()) {
            cir.setReturnValue(desiredCameraDistance);
        }
    }

    @Shadow
    float cameraY;

    @Shadow
    Entity focusedEntity;

    // InstantSneak
    @Inject(method = "updateEyeHeight", at = @At("HEAD"))
    private void updateEyeHeight(CallbackInfo ci) {
        if (AutumnClient.options.instantSneak.getValue() && focusedEntity != null) {
            cameraY = focusedEntity.getStandingEyeHeight();
        }
    }
}
