package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import autumnvn.autumn.Utils;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
                int armor = livingEntity.getArmor();
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
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, Identifier.ofVanilla("hud/heart/container"), x, y - 1, 9, 9);
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, Identifier.ofVanilla("hud/heart/full"), x, y - 1, 9, 9);

                if (armor > 0) {
                    int x2 = x + 9 + 2;
                    String armorLine = String.format(" %d", armor);
                    context.drawText(client.textRenderer, armorLine, x2, y, 0xffffffff, false);
                    int x3 = x2 + client.textRenderer.getWidth(armorLine) + 2;
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, Identifier.ofVanilla("hud/armor_full"), x3, y - 1, 9, 9);
                }
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

            lines.forEach(line -> context.drawText(client.textRenderer, line, 2, 2 + (client.textRenderer.fontHeight + 2) * lines.indexOf(line), 0xffffffff, false));
        }

        // ArmorHud
        if (AutumnClient.options.infoHud.getValue() && client.player != null && client.interactionManager != null && client.interactionManager.hasStatusBars()) {
            int y = context.getScaledWindowHeight() - 55;
            EquipmentSlot[] armorSlots = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
            if (client.player.getAir() < client.player.getMaxAir()) {
                y -= 10;
            }
            if (getRiddenEntity() != null) {
                y -= getHeartRows(getHeartCount(getRiddenEntity())) * 10;
            }
            for (int i = 0, x = 63; i < 4; i++, x -= 15) {
                renderHotbarItem(context, context.getScaledWindowWidth() / 2 + x, y, tickCounter, client.player, client.player.getEquippedStack(armorSlots[i]), 1);
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

    @ModifyVariable(method = "getCurrentBarType", at = @At("STORE"), ordinal = 1)
    private boolean getJumpingMountIsntNull(boolean original) {
        return AutumnClient.options.infoHud.getValue() && client.player != null && client.interactionManager != null && (!client.interactionManager.hasExperienceBar() || client.options.jumpKey.isPressed() || client.player.getMountJumpStrength() > 0) && original;
    }

    @Inject(method = "shouldShowExperienceBar", at = @At("HEAD"), cancellable = true)
    private void shouldShowExperienceBar(CallbackInfoReturnable<Boolean> cir) {
        if (AutumnClient.options.infoHud.getValue() && client.player != null && client.interactionManager != null && client.interactionManager.hasExperienceBar() && !client.options.jumpKey.isPressed() && client.player.getMountJumpStrength() <= 0) {
            cir.setReturnValue(true);
        }
    }

    // EffectHud
    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIIII)V", shift = At.Shift.AFTER))
    private void renderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci, @Local StatusEffectInstance statusEffectInstance, @Local(ordinal = 2) int x, @Local(ordinal = 3) int y) {
        if (AutumnClient.options.infoHud.getValue()) {
            String duration = durationString(statusEffectInstance);
            context.drawText(client.textRenderer, duration, x + 2, y + 24 - client.textRenderer.fontHeight, 0xffffffff, false);
            String amplifier = amplipierString(statusEffectInstance.getAmplifier());
            context.drawText(client.textRenderer, amplifier, x + 23 - client.textRenderer.getWidth(amplifier), y + 2, 0xffffffff, false);
        }
    }

    @Unique
    private String durationString(StatusEffectInstance statusEffectInstance) {
        if (statusEffectInstance.isInfinite()) return I18n.translate("effect.duration.infinite");

        int second = statusEffectInstance.getDuration() / 20;

        if (second >= 60 * 60 * 24 * 365) return second / (60 * 60 * 24 * 365) + "y";
        else if (second >= 60 * 60 * 24 * 30) return second / (60 * 60 * 24 * 30) + "mo";
        else if (second >= 60 * 60 * 24) return second / (60 * 60 * 24) + "d";
        else if (second >= 60 * 60) return second / (60 * 60) + "h";
        else if (second >= 60 * 10) return second / 60 + "m";
        else if (second >= 60) return second / 60 + ":" + second % 60;
        else return String.valueOf(second);
    }

    @Unique
    private String amplipierString(int amplifier) {
        if (amplifier == 0) return "";

        String key = "enchantment.level." + (amplifier + 1);

        return I18n.hasTranslation(key) ? I18n.translate(key) : String.valueOf(amplifier + 1);
    }

    // FreeCam
    @Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
    private void getCameraPlayer(CallbackInfoReturnable<PlayerEntity> cir) {
        if (AutumnClient.options.freeCam.getValue()) {
            cir.setReturnValue(AutumnClient.client.player);
        }
    }
}
