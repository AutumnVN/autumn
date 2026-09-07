package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

    @Unique
    private Double defaultMouseSen;

    // ThirdPersonNoClip
    @Inject(method = "getMaxZoom", at = @At("HEAD"), cancellable = true)
    private void getMaxZoom(float desiredDistance, CallbackInfoReturnable<Float> cir) {
        if (AutumnClient.options.thirdPersonNoClip.get()) {
            cir.setReturnValue(desiredDistance);
        }
    }

    @Shadow
    private float eyeHeight;

    @Shadow
    private Entity entity;

    @Shadow
    @Final
    private Minecraft minecraft;

    // InstantSneak
    @Inject(method = "alignWithEntity", at = @At("HEAD"))
    private void alignWithEntity(float partialTick, CallbackInfo ci) {
        if (AutumnClient.options.instantSneak.get() && entity != null) {
            eyeHeight = entity.getEyeHeight();
        }
    }

    // Zoom
    @Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
    private void calculateFov(float partialTick, CallbackInfoReturnable<Float> cir) {
        OptionInstance<Double> mouseSen = minecraft.options.sensitivity();
        if (AutumnClient.zoomKey.isDown()) {
            if (defaultMouseSen == null) {
                defaultMouseSen = mouseSen.get();
            }
            mouseSen.set(defaultMouseSen / 4);
            cir.setReturnValue(cir.getReturnValue() / 4);
        } else if (defaultMouseSen != null) {
            mouseSen.set(defaultMouseSen);
            defaultMouseSen = null;
        }
    }
}