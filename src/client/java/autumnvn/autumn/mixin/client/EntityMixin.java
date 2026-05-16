package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import autumnvn.autumn.AutumnClient;
import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public class EntityMixin {

    // NoInvisible
    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    private void isInvisible(CallbackInfoReturnable<Boolean> ci) {
        if (AutumnClient.options.noInvisible.getValue()) {
            ci.setReturnValue(false);
        }
    }

    // FreeCam
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    @SuppressWarnings({"EqualsBetweenInconvertibleTypes", "unlikely-arg-type"})
    private void changeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if (AutumnClient.options.freeCam.getValue() && this.equals(AutumnClient.client.player)) {
            AutumnClient.options.freeCamEntity.changeLookDirection(cursorDeltaX, cursorDeltaY);
            ci.cancel();
        }
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    @SuppressWarnings({"EqualsBetweenInconvertibleTypes", "unlikely-arg-type"})
    private void pushAwayFrom(Entity entity, CallbackInfo ci) {
        if (AutumnClient.options.freeCam.getValue() && (this.equals(AutumnClient.options.freeCamEntity) || entity.equals(AutumnClient.options.freeCamEntity))) {
            ci.cancel();
        }
    }
}
