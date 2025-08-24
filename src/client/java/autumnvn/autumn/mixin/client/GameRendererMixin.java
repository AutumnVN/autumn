package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Final
    @Shadow
    private MinecraftClient client;

    @Unique
    private Double defaultMouseSen;

    // FreeCam
    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void renderHand(float tickProgress, boolean sleeping, Matrix4f positionMatrix, CallbackInfo ci) {
        if (AutumnClient.options.freeCam.getValue()) {
            ci.cancel();
        }
    }

    // Zoom
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        SimpleOption<Double> mouseSen = client.options.getMouseSensitivity();
        if (AutumnClient.zoomKey.isPressed()) {
            mouseSen.setValue(defaultMouseSen / 4);
            cir.setReturnValue(cir.getReturnValueF() / 4);
        } else if (defaultMouseSen == null) {
            defaultMouseSen = mouseSen.getValue();
        } else {
            mouseSen.setValue(defaultMouseSen);
        }
    }
}
