package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    // NoFog
    @Inject(method = "getFogColor", at = @At("RETURN"), cancellable = true)
    private void getFogColor(Camera camera, float tickProgress, ClientWorld world, int viewDistance, float skyDarkness, boolean thick, CallbackInfoReturnable<Vector4f> cir) {
        if (AutumnClient.options.noFog.getValue()) {
            Vector4f fogColor = cir.getReturnValue();
            cir.setReturnValue(new Vector4f(fogColor.x, fogColor.y, fogColor.z, 0.0F));
        }
    }
}
