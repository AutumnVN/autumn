package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Shadow
    private int noJumpDelay;

    @Shadow
    public float getHealth() {
        return 0;
    }

    // FreeCam
    @Inject(method = "setHealth", at = @At("HEAD"))
    private void setHealth(float health, CallbackInfo ci) {
        if (AutumnClient.options.freeCam.get() && (Object) this == AutumnClient.minecraft.player && getHealth() > health) {
            AutumnClient.options.freeCam.set(false);
        }
    }

    // NoJumpDelay
    @Inject(method = "aiStep", at = @At("HEAD"))
    private void aiStep(CallbackInfo ci) {
        if (AutumnClient.options.noJumpDelay.get() && (Object) this == AutumnClient.minecraft.player) {
            this.noJumpDelay = 0;
        }
    }
}