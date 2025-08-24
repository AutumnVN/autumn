package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Shadow
    private int jumpingCooldown;

    // HorseSwim
    @Inject(method = "travelControlled", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V"))
    private void travelControlled(PlayerEntity controllingPlayer, Vec3d movementInput, CallbackInfo ci) {
        if (AutumnClient.options.horseSwim.getValue()) {
            if ((Object) this instanceof AbstractHorseEntity horse) {
                if (horse.getFluidHeight(FluidTags.WATER) > 0.4 || horse.getFluidHeight(FluidTags.LAVA) > 0.4) {
                    if (horse.isTouchingWater()) {
                        horse.addVelocity(0, horse.getFluidHeight(FluidTags.WATER) > 0.8 ? 0.04 : 0.01, 0);
                    } else if (horse.isInLava()) {
                        horse.addVelocity(0, horse.getFluidHeight(FluidTags.LAVA) > 0.8 ? 0.11 : 0.0275, 0);
                    }
                }
            }
        }
    }

    @Shadow
    public float getHealth() {
        return 0;
    }

    // FreeCam
    @Inject(method = "setHealth", at = @At("HEAD"))
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    private void setHealth(float health, CallbackInfo ci) {
        if (AutumnClient.options.freeCam.getValue() && this.equals(AutumnClient.client.player) && getHealth() > health) {
            AutumnClient.options.freeCam.setValue(false);

        }
    }

    // NoJumpDelay
    @Inject(method = "tickMovement", at = @At("HEAD"))
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    private void tickMovement(CallbackInfo ci) {
        if (AutumnClient.options.noJumpDelay.getValue() && this.equals(AutumnClient.client.player)) {
            this.jumpingCooldown = 0;
        }
    }
}
