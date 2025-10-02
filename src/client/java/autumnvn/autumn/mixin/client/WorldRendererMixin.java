package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    private EntityRenderState getAndUpdateRenderState(Entity entity, float tickProgress) {
        return null;
    }

    // FreeCam
    @Inject(method = "fillEntityRenderStates", at = @At("HEAD"))
    private void injectFakePlayer(Camera camera, Frustum frustum, RenderTickCounter tickCounter, WorldRenderState renderStates, CallbackInfo ci) {
        if (AutumnClient.options.freeCam.getValue()) {
            renderStates.entityRenderStates.add(getAndUpdateRenderState(client.player, tickCounter.getTickProgress(true)));
        }
    }
}
