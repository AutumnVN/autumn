package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import autumnvn.autumn.Utils;
import autumnvn.autumn.interfaces.EntityRenderState2;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Shadow
    private static void renderLeash(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState.LeashData leashData) {
    }

    @Shadow
    protected void renderLabelIfPresent(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    }

    // BetterNametag
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(S state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        Entity entity = ((EntityRenderState2) state).autumn$getEntity();
        if (AutumnClient.options.betterNametag.getValue() && entity instanceof LivingEntity livingEntity && (livingEntity instanceof PlayerEntity || livingEntity == Utils.getTargetedEntity())) {
            EntityRenderState.LeashData leashData = state.leashData;
            if (leashData != null) {
                renderLeash(matrices, vertexConsumers, leashData);
            }

            float health = livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
            String ownerName = Utils.getOwnerName(livingEntity);
            renderLabelIfPresent(state,
                    Text.of(
                            String.format("%s%s %s%.0f§c❤%s",
                                    ownerName != null ? ownerName + (ownerName.endsWith("s") ? "' " : "'s ") : "",
                                    Objects.requireNonNull(livingEntity.getDisplayName()).getString(),
                                    Utils.color(health, 0, livingEntity.getMaxHealth()),
                                    health,
                                    livingEntity instanceof PlayerEntity playerEntity ? (playerEntity.isCreative() ? " §r[C]" : playerEntity.isSpectator() ? " §r[S]" : "") : ""
                            )
                    ), matrices, vertexConsumers, light);
            ci.cancel();
        }
    }

    @ModifyVariable(method = "renderLabelIfPresent", at = @At("STORE"))
    private Vec3d labelNamePos(Vec3d original, S state) {
        if (AutumnClient.options.betterNametag.getValue() && original == null) {
            Entity entity = ((EntityRenderState2) state).autumn$getEntity();
            if (entity instanceof LivingEntity livingEntity && (livingEntity instanceof PlayerEntity || livingEntity == Utils.getTargetedEntity())) {
                return entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getLerpedYaw(AutumnClient.client.getRenderTickCounter().getTickProgress(true)));
            }
        }

        return original;
    }

    @ModifyArgs(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", ordinal = 0))
    private void draw(Args args) {
        if (AutumnClient.options.betterNametag.getValue()) {
            args.set(3, 0xaaffffff);
            args.set(7, TextRenderer.TextLayerType.SEE_THROUGH);
        }
    }

    @Inject(method = "updateRenderState", at = @At("HEAD"))
    private void updateRenderState(T entity, S state, float tickDelta, CallbackInfo ci) {
        if (AutumnClient.options.betterNametag.getValue()) {
            ((EntityRenderState2) state).autumn$setEntity(entity);
        }
    }
}
