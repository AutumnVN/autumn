package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    // NoInvisible
    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    private void isInvisible(CallbackInfoReturnable<Boolean> cir) {
        if (AutumnClient.options.noInvisible.get()) {
            cir.setReturnValue(false);
        }
    }

    // FreeCam
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void turn(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if (AutumnClient.options.freeCam.get() && (Object) this == AutumnClient.minecraft.player) {
            AutumnClient.options.freeCamEntity.turn(cursorDeltaX, cursorDeltaY);
            ci.cancel();
        }
    }

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void pushAwayFrom(Entity entity, CallbackInfo ci) {
        if (AutumnClient.options.freeCam.get() && ((Object) this == AutumnClient.options.freeCamEntity || entity == AutumnClient.options.freeCamEntity)) {
            ci.cancel();
        }
    }
}