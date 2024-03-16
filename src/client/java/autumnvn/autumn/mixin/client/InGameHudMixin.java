package autumnvn.autumn.mixin.client;

import java.util.ArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import autumnvn.autumn.AutumnClient;
import autumnvn.autumn.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    private String[] direction = new String[] {"+Z", "-X+Z", "-X", "-X-Z", "-Z", "+X-Z", "+X", "+X+Z"};

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
    private void renderHotbarItem(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed) {}

    // InfoHud
    @Inject(method = "render", at = @At("TAIL"))
    private void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (AutumnClient.options.infoHud.getValue() && !AutumnClient.client.options.hudHidden && !AutumnClient.client.getDebugHud().shouldShowDebugHud()) {
            ArrayList<String> lines = new ArrayList<String>();
            lines.add(String.format("%d fps", AutumnClient.client.getCurrentFps()));
            lines.add(String.format("%d %d %d %s",
                    AutumnClient.client.player.getBlockX(),
                    AutumnClient.client.player.getBlockY(),
                    AutumnClient.client.player.getBlockZ(),
                    direction[(int) (AutumnClient.client.player.getYaw() / 45 + 0.5) & 7]));
            lines.add(String.format("%.1f tps", AutumnClient.tps));
            if (Utils.getTargetedEntity(tickDelta) instanceof LivingEntity livingEntity) {
                float health = livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
                String healthLine = String.format("%s %s%.0f",
                        livingEntity.getDisplayName().getString(),
                        Utils.color(health, 0, livingEntity.getMaxHealth()),
                        health);
                lines.add(healthLine);
                int x = 2 + AutumnClient.client.textRenderer.getWidth(healthLine) + 2;
                int y = 2 + (AutumnClient.client.textRenderer.fontHeight + 2) * (lines.size() - 1);
                context.drawGuiTexture(new Identifier("hud/heart/container"), x, y, 9, 9);
                context.drawGuiTexture(new Identifier("hud/heart/full"), x, y, 9, 9);
            }
            if (Utils.getTargetedEntity(tickDelta) instanceof AbstractHorseEntity abstractHorseEntity) {
                double speed = abstractHorseEntity.getAttributes().getValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 42.157787584d;
                double jump = abstractHorseEntity.getJumpStrength();
                double jumpHeight = -0.1817584952d * jump * jump * jump + 3.689713992d * jump * jump + 2.128599134d * jump + -0.343930367d;
                lines.add(String.format("Speed %s%.1fm/s §rJump %s%.1fm",
                        Utils.color(speed, 4.742751103d, 14.228253309d), speed,
                        Utils.color(jumpHeight, 1.08623d, 5.29262d), jumpHeight));
            }
            lines.forEach(line -> {
                context.drawText(AutumnClient.client.textRenderer, line, 2, 2 + (AutumnClient.client.textRenderer.fontHeight + 2) * lines.indexOf(line), 0xffffff, true);
            });
        }

        // ArmorHud
        if (AutumnClient.options.infoHud.getValue() && AutumnClient.client.interactionManager.hasStatusBars()) {
            int y = context.getScaledWindowHeight() - 55;
            if (AutumnClient.client.player.getAir() < AutumnClient.client.player.getMaxAir()) {
                y -= 10;
            }
            if (getRiddenEntity() != null) {
                y -= getHeartRows(getHeartCount(getRiddenEntity())) * 10;
            }
            for (int i = 0, x = 68; i < 4; i++, x -= 15) {
                renderHotbarItem(context, context.getScaledWindowWidth() / 2 + x, y, tickDelta, AutumnClient.client.player, AutumnClient.client.player.getInventory().getArmorStack(i), 1);
            }
        }
    }

    // MountHud
    @ModifyVariable(method = "renderMountHealth", at = @At("STORE"), ordinal = 2)
    private int k(int original) {
        return AutumnClient.options.infoHud.getValue() && AutumnClient.client.interactionManager.hasStatusBars() ? original - 10 : original;
    }

    @ModifyVariable(method = "renderStatusBars", at = @At(value = "STORE", ordinal = 1), ordinal = 13)
    private int x(int original) {
        return AutumnClient.options.infoHud.getValue() ? 0 : original;
    }

    @ModifyVariable(method = "renderStatusBars", at = @At(value = "STORE", ordinal = 1), ordinal = 10)
    private int t(int original) {
        return original -= AutumnClient.options.infoHud.getValue() && getRiddenEntity() != null ? getHeartRows(getHeartCount(getRiddenEntity())) * 10 : 0;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getJumpingMount()Lnet/minecraft/entity/JumpingMount;"))
    private JumpingMount getJumpingMount(ClientPlayerEntity player) {
        return AutumnClient.options.infoHud.getValue() && (!AutumnClient.client.interactionManager.hasExperienceBar() || AutumnClient.client.options.jumpKey.isPressed() || player.getMountJumpStrength() > 0) ? player.getJumpingMount() : null;
    }
}
