package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractBoatEntity.class)
public class AbstractBoatEntityMixin {

    // Boat360
    @Redirect(method = "clampPassengerYaw", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"))
    private float clamp(float value, float min, float max) {
        if (AutumnClient.options.boat360.getValue()) {
            return value;
        }
        return MathHelper.clamp(value, min, max);
    }
}
