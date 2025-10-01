package autumnvn.autumn.mixin.client;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

//    @Final
//    @Shadow
//    private MinecraftClient client;
//
//    // FreeCam
//    @Inject(method = "renderEntities", at = @At("HEAD"))
//    private void renderEntities(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, Camera camera, RenderTickCounter tickCounter, List<Entity> entities, CallbackInfo ci) {
//        if (AutumnClient.options.freeCam.getValue()) {
//            entities.add(client.player);
//        }
//    }
}
