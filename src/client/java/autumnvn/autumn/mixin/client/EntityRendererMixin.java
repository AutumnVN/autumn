package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import autumnvn.autumn.Utils;
import autumnvn.autumn.interfaces.IEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Shadow
    protected abstract void submitNameDisplay(S state, PoseStack matrices, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState);

    // BetterNametag
    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    private void submit(S renderState, PoseStack matrices, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        Entity entity = ((IEntityRenderState) renderState).autumn$getEntity();
        if (AutumnClient.options.betterNametag.get() && entity instanceof LivingEntity livingEntity && (livingEntity instanceof Player || livingEntity == Utils.getTargetedEntity())) {
            if (renderState.leashStates != null) {
                for (EntityRenderState.LeashState leashState : renderState.leashStates) {
                    submitNodeCollector.submitLeash(matrices, leashState);
                }
            }

            float health = livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
            String ownerName = Utils.getOwnerName(livingEntity);
            renderState.nameTagAttachment = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getYRot(AutumnClient.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true)));
            renderState.nameTag = Component.literal(
                    String.format("%s%s %s%.0f§c❤%s",
                            ownerName != null ? ownerName + (ownerName.endsWith("s") ? "' " : "'s ") : "",
                            Objects.requireNonNull(livingEntity.getDisplayName()).getString(),
                            Utils.color(health, 0, livingEntity.getMaxHealth()),
                            health,
                            livingEntity instanceof Player playerEntity ? (playerEntity.isCreative() ? " §r[C]" : playerEntity.isSpectator() ? " §r[S]" : "") : ""
                    )
            );
            renderState.isDiscrete = false;
            submitNameDisplay(renderState, matrices, submitNodeCollector, cameraRenderState);
            ci.cancel();
        }
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V", at = @At("HEAD"))
    private void extractRenderState(T entity, S state, float tickDelta, CallbackInfo ci) {
        if (AutumnClient.options.betterNametag.get()) {
            ((IEntityRenderState) state).autumn$setEntity(entity);
        }
    }
}