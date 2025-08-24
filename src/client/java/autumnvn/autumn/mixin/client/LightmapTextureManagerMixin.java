package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    // FullBright
    @ModifyVariable(method = "update", at = @At("STORE"), ordinal = 9)
    private float getGamma(float original) {
        return AutumnClient.options.fullBright.getValue() ? 100.0f : original;
    }
}
