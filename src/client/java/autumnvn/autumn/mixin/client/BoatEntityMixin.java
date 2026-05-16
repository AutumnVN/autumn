package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import autumnvn.autumn.AutumnClient;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

@Mixin(BoatEntity.class)
public class BoatEntityMixin {

    // Boat360
    @Redirect(method = "clampPassengerYaw", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"))
    private float clamp(float value, float min, float max) {
        if (AutumnClient.options.boat360.getValue()) {
            return value;
        }
        return MathHelper.clamp(value, min, max);
    }
}
