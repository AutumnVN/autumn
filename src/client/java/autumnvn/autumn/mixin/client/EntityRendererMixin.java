package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import autumnvn.autumn.AutumnClient;
import autumnvn.autumn.Utils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    @Shadow
    private void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {}

    // BetterNametag
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (AutumnClient.options.betterNametag.getValue() && entity instanceof LivingEntity livingEntity && (livingEntity instanceof PlayerEntity || livingEntity == Utils.getTargetedEntity(tickDelta))) {
            float health = livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
            renderLabelIfPresent(entity, Text.of(String.format("%s %s%.0f§c❤%s",
                    livingEntity.getDisplayName().getString(),
                    Utils.color(health, 0, livingEntity.getMaxHealth()),
                    health,
                    livingEntity instanceof PlayerEntity playerEntity ? (playerEntity.isCreative() ? " §r[C]" : playerEntity.isSpectator() ? " §r[S]" : "") : "")), matrices, vertexConsumers, light);
            ci.cancel();
        }
    }

    @ModifyVariable(method = "renderLabelIfPresent", at = @At("STORE"), ordinal = 0)
    private double d(double original) {
        return AutumnClient.options.betterNametag.getValue() ? 0 : original;
    }

    @ModifyVariable(method = "renderLabelIfPresent", at = @At("STORE"), ordinal = 0)
    private boolean bl(boolean original) {
        return AutumnClient.options.betterNametag.getValue() ? true : original;
    }

    @ModifyArgs(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"))
    private void draw(Args args) {
        if (AutumnClient.options.betterNametag.getValue()) {
            args.set(3, 0xffffffff);
        }
    }
}
