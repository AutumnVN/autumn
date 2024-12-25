package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import autumnvn.autumn.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Objects;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    private LivingEntity getRiddenEntity() {
        return null;
    }

    @Shadow
    private int getHeartCount(LivingEntity entity) {
        return 0;
    }

    @Shadow
    private int getHeartRows(int heartCount) {
        return 0;
    }

    @Shadow
    private void renderHotbarItem(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed) {
    }

    // InfoHud
    @Inject(method = "render", at = @At("HEAD"))
    private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (AutumnClient.options.infoHud.getValue() && !client.options.hudHidden && !client.getDebugHud().shouldShowDebugHud() && client.player != null) {

            String[] direction = new String[]{"+Z", "-X+Z", "-X", "-X-Z", "-Z", "+X-Z", "+X", "+X+Z"};
            ArrayList<String> lines = new ArrayList<>();

            lines.add(String.format("%d fps", client.getCurrentFps()));

            lines.add(
                    String.format("%d %d %d %s",
                            client.player.getBlockX(),
                            client.player.getBlockY(),
                            client.player.getBlockZ(),
                            direction[(int) (client.player.getYaw() / 45 + 0.5) & 7]
                    )
            );

            lines.add(String.format("%.1f tps", AutumnClient.tps));

            if (Utils.getTargetedEntity() instanceof LivingEntity livingEntity) {
                float health = livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
                String ownerName = Utils.getOwnerName(livingEntity);
                String healthLine = String.format("%s%s %s%.0f",
                        ownerName != null ? ownerName + (ownerName.endsWith("s") ? "' " : "'s ") : "",
                        Objects.requireNonNull(livingEntity.getDisplayName()).getString(),
                        Utils.color(health, 0, livingEntity.getMaxHealth()),
                        health
                );
                lines.add(healthLine);
                int x = 2 + client.textRenderer.getWidth(healthLine) + 2;
                int y = 2 + (client.textRenderer.fontHeight + 2) * (lines.size() - 1);
                context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.ofVanilla("hud/heart/container"), x, y, 9, 9);
                context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.ofVanilla("hud/heart/full"), x, y, 9, 9);
            }

            if (Utils.getTargetedEntity() instanceof AbstractHorseEntity abstractHorseEntity) {
                double speed = abstractHorseEntity.getAttributeValue(EntityAttributes.MOVEMENT_SPEED) * 42.157787584d;
                double jump = abstractHorseEntity.getAttributeValue(EntityAttributes.JUMP_STRENGTH);
                double jumpHeight = -0.1817584952d * jump * jump * jump + 3.689713992d * jump * jump + 2.128599134d * jump - 0.343930367d;
                lines.add(
                        String.format("Speed %s%.1fm/s §rJump %s%.1fm",
                                Utils.color(speed, 4.742751103d, 14.228253309d),
                                speed,
                                Utils.color(jumpHeight, 1.08623d, 5.29262d),
                                jumpHeight
                        )
                );
            }

            lines.forEach(line -> context.drawText(client.textRenderer, line, 2, 2 + (client.textRenderer.fontHeight + 2) * lines.indexOf(line), 0xffffff, false));
        }

        // ArmorHud
        if (AutumnClient.options.infoHud.getValue() && client.player != null && client.interactionManager != null && client.interactionManager.hasStatusBars()) {
            int y = context.getScaledWindowHeight() - 55;
            if (client.player.getAir() < client.player.getMaxAir()) {
                y -= 10;
            }
            if (getRiddenEntity() != null) {
                y -= getHeartRows(getHeartCount(getRiddenEntity())) * 10;
            }
            for (int i = 0, x = 63; i < 4; i++, x -= 15) {
                renderHotbarItem(context, context.getScaledWindowWidth() / 2 + x, y, tickCounter, client.player, client.player.getInventory().getArmorStack(i), 1);
            }
        }
    }

    // MountHud
    @ModifyVariable(method = "renderMountHealth", at = @At("STORE"), ordinal = 2)
    private int k(int original) {
        return AutumnClient.options.infoHud.getValue() && client.interactionManager != null && client.interactionManager.hasStatusBars() ? original - 10 : original;
    }

    @ModifyVariable(method = "renderStatusBars", at = @At("STORE"), ordinal = 10)
    private int t(int original) {
        return AutumnClient.options.infoHud.getValue() ? 0 : original;
    }

    @ModifyVariable(method = "getAirBubbleY", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int heartCount(int original) {
        return AutumnClient.options.infoHud.getValue() && getRiddenEntity() != null ? getHeartCount(getRiddenEntity()) : original;
    }

    @ModifyVariable(method = "renderMainHud", at = @At("STORE"))
    private JumpingMount jumpingMount(JumpingMount original) {
        return AutumnClient.options.infoHud.getValue() && client.player != null && client.interactionManager != null && (!client.interactionManager.hasExperienceBar() || client.options.jumpKey.isPressed() || client.player.getMountJumpStrength() > 0) ? original : null;
    }

    @Inject(method = "shouldRenderExperience", at = @At("HEAD"), cancellable = true)
    private void shouldRenderExperience(CallbackInfoReturnable<Boolean> cir) {
        if (AutumnClient.options.infoHud.getValue() && client.player != null && client.interactionManager != null && client.interactionManager.hasExperienceBar() && !client.options.jumpKey.isPressed() && client.player.getMountJumpStrength() <= 0) {
            cir.setReturnValue(true);
        }
    }
}
