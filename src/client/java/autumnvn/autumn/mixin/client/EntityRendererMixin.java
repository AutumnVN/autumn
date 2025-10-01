package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import autumnvn.autumn.Utils;
import autumnvn.autumn.interfaces.IEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Shadow
    protected abstract void renderLabelIfPresent(S state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState);

    // BetterNametag
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(S renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState, CallbackInfo ci) {
        Entity entity = ((IEntityRenderState) renderState).autumn$getEntity();
        if (AutumnClient.options.betterNametag.getValue() && entity instanceof LivingEntity livingEntity && (livingEntity instanceof PlayerEntity || livingEntity == Utils.getTargetedEntity())) {
            if (renderState.leashDatas != null) {
                for (EntityRenderState.LeashData leashData : renderState.leashDatas) {
                    queue.submitLeash(matrices, leashData);
                }
            }

            float health = livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
            String ownerName = Utils.getOwnerName(livingEntity);
            renderState.nameLabelPos = entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getLerpedYaw(AutumnClient.client.getRenderTickCounter().getTickProgress(true)));
            renderState.displayName = Text.of(
                    String.format("%s%s %s%.0f§c❤%s",
                            ownerName != null ? ownerName + (ownerName.endsWith("s") ? "' " : "'s ") : "",
                            Objects.requireNonNull(livingEntity.getDisplayName()).getString(),
                            Utils.color(health, 0, livingEntity.getMaxHealth()),
                            health,
                            livingEntity instanceof PlayerEntity playerEntity ? (playerEntity.isCreative() ? " §r[C]" : playerEntity.isSpectator() ? " §r[S]" : "") : ""
                    )
            );
            renderLabelIfPresent(renderState, matrices, queue, cameraState);
            ci.cancel();
        }
    }

    @Inject(method = "updateRenderState", at = @At("HEAD"))
    private void updateRenderState(T entity, S state, float tickDelta, CallbackInfo ci) {
        if (AutumnClient.options.betterNametag.getValue()) {
            ((IEntityRenderState) state).autumn$setEntity(entity);
        }
    }
}
