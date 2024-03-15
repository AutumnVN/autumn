package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
}
