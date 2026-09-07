package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import autumnvn.autumn.AutumnClient;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

@Mixin(AbstractBoat.class)
public class AbstractBoatEntityMixin {

    // Boat360
    @Redirect(method = "clampRotation(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float clamp(float value, float min, float max) {
        if (AutumnClient.options.boat360.get()) {
            return value;
        }
        return Mth.clamp(value, min, max);
    }
}
