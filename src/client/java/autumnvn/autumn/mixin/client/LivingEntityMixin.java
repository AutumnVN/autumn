package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import autumnvn.autumn.AutumnClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Vec3d;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    // HorseSwim
    @Inject(method = "travelControlled", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.BEFORE))
    private void travelControlled(PlayerEntity controllingPlayer, Vec3d movementInput, CallbackInfo ci) {
        if (AutumnClient.options.horseSwim.getValue() && (Object) this instanceof AbstractHorseEntity horse && horse.getFluidHeight(FluidTags.WATER) > horse.getSwimHeight()) {
            horse.addVelocity(0, 0.1, 0);
        }
    }
}
