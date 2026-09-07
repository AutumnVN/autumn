package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelExtractor.class)
public class WorldRendererMixin {

    @Shadow
    private EntityRenderState extractEntity(Entity entity, float tickProgress) {
        return null;
    }

    @Shadow
    @Final
    private Minecraft minecraft;

    // FreeCam
    @Inject(method = "extractVisibleEntities", at = @At("HEAD"))
    private void injectFakePlayer(Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState levelRenderState, CallbackInfo ci) {
        if (AutumnClient.options.freeCam.get() && minecraft.player != null) {
            levelRenderState.entityRenderStates.add(extractEntity(minecraft.player, deltaTracker.getGameTimeDeltaPartialTick(true)));
        }
    }
}