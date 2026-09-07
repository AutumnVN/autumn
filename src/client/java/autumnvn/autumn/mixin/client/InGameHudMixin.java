package autumnvn.autumn.mixin.client;

import java.util.ArrayList;
import java.util.Objects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.sugar.Local;
import autumnvn.autumn.AutumnClient;
import autumnvn.autumn.Utils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(Hud.class)
public class InGameHudMixin {

    @Shadow
    private boolean isHidden;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private void extractSlot(GuiGraphicsExtractor extractor, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed) {}

    @Shadow
    private LivingEntity getPlayerVehicleWithHealth() {
        return null;
    }

    @Shadow
    private int getVehicleMaxHearts(LivingEntity entity) {
        return 0;
    }

    // InfoHud / ArmorHud / MountHud
    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker, CallbackInfo ci) {

        // InfoHud
        if (AutumnClient.options.infoHud.get() && !isHidden && !minecraft.getDebugOverlay().showDebugScreen() && minecraft.player != null) {

            String[] direction = new String[] {"+Z", "-X+Z", "-X", "-X-Z", "-Z", "+X-Z", "+X", "+X+Z"};
            ArrayList<String> lines = new ArrayList<>();

            lines.add(String.format("%d fps", minecraft.getFps()));

            lines.add(
                    String.format("%d %d %d %s",
                            minecraft.player.getBlockX(),
                            minecraft.player.getBlockY(),
                            minecraft.player.getBlockZ(),
                            direction[(int) (minecraft.player.getYRot() / 45 + 0.5) & 7]));

            lines.add(String.format("%.1f tps", AutumnClient.tps));

            if (Utils.getTargetedEntity() instanceof LivingEntity livingEntity) {
                float health = livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
                int armor = livingEntity.getArmorValue();
                String ownerName = Utils.getOwnerName(livingEntity);
                String healthLine = String.format("%s%s %s%.0f",
                        ownerName != null ? ownerName + (ownerName.endsWith("s") ? "' " : "'s ") : "",
                        Objects.requireNonNull(livingEntity.getDisplayName()).getString(),
                        Utils.color(health, 0, livingEntity.getMaxHealth()),
                        health);
                lines.add(healthLine);
                int x = 2 + minecraft.font.width(healthLine) + 2;
                int y = 2 + (minecraft.font.lineHeight + 2) * (lines.size() - 1);
                extractor.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath("minecraft", "hud/heart/container"), x, y - 1, 9, 9);
                extractor.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath("minecraft", "hud/heart/full"), x, y - 1, 9, 9);

                if (armor > 0) {
                    int x2 = x + 9 + 2;
                    String armorLine = String.format(" %d", armor);
                    extractor.text(minecraft.font, armorLine, x2, y, 0xffffffff, false);
                    int x3 = x2 + minecraft.font.width(armorLine) + 2;
                    extractor.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath("minecraft", "hud/armor_full"), x3, y - 1, 9, 9);
                }
            }

            if (Utils.getTargetedEntity() instanceof AbstractHorse abstractHorseEntity) {
                double speed = abstractHorseEntity.getAttributeValue(Attributes.MOVEMENT_SPEED) * 42.157787584d;
                double jump = abstractHorseEntity.getAttributeValue(Attributes.JUMP_STRENGTH);
                double jumpHeight = -0.1817584952d * jump * jump * jump + 3.689713992d * jump * jump + 2.128599134d * jump - 0.343930367d;
                lines.add(
                        String.format("Speed %s%.1fm/s §rJump %s%.1fm",
                                Utils.color(speed, 4.742751103d, 14.228253309d),
                                speed,
                                Utils.color(jumpHeight, 1.08623d, 5.29262d),
                                jumpHeight));
            }

            lines.forEach(line -> extractor.text(minecraft.font, line, 2, 2 + (minecraft.font.lineHeight + 2) * lines.indexOf(line), 0xffffffff, false));
        }

        // ArmorHud
        if (AutumnClient.options.infoHud.get() && minecraft.player != null && !isHidden) {
            int y = extractor.guiHeight() - 55;
            EquipmentSlot[] armorSlots = new EquipmentSlot[] {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
            if (minecraft.player.getAirSupply() < minecraft.player.getMaxAirSupply()) {
                y -= 10;
            }
            LivingEntity vehicle = getPlayerVehicleWithHealth();
            if (vehicle != null) {
                y -= (getVehicleMaxHearts(vehicle) + 9) / 10 * 10;
            }
            for (int i = 0, x = 63; i < 4; i++, x -= 15) {
                extractSlot(extractor, extractor.guiWidth() / 2 + x, y, deltaTracker, minecraft.player, minecraft.player.getItemBySlot(armorSlots[i]), 1);
            }
        }
    }

    // MountHud
    @ModifyVariable(method = "extractVehicleHealth", at = @At(value = "STORE"), name = "yLine1")
    private int mountHud$moveMountHealthUp(int y) {
        if (AutumnClient.options.infoHud.get() && minecraft.gameMode != null && minecraft.gameMode.canHurtPlayer()) {
            y -= 10;
        }
        return y;
    }

    @Redirect(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "getVehicleMaxHearts"))
    private int mountHud$alwaysRenderFood(Hud instance, LivingEntity entity) {
        if (AutumnClient.options.infoHud.get()) {
            return 0;
        }
        return getVehicleMaxHearts(entity);
    }

    @ModifyVariable(method = "getAirBubbleYLine", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private int mountHud$moveAirUp(int heartCount) {
        if (AutumnClient.options.infoHud.get()) {
            LivingEntity entity = getPlayerVehicleWithHealth();
            if (entity != null) {
                return getVehicleMaxHearts(entity);
            }
        }
        return heartCount;
    }

    @Redirect(method = "nextContextualInfoState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;jumpableVehicle()Lnet/minecraft/world/entity/PlayerRideableJumping;"))
    private PlayerRideableJumping mountHud$switchBar(LocalPlayer player) {
        if (AutumnClient.options.infoHud.get()) {
            var jumpableVehicle = player.jumpableVehicle();
            if (!minecraft.gameMode.hasExperience() || minecraft.options.keyJump.isDown()
                    || player.getJumpRidingScale() > 0)
                return jumpableVehicle;
            return null;
        }
        return player.jumpableVehicle();
    }

    @Redirect(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasExperience()Z"))
    private boolean mountHud$renderExperienceLevel(MultiPlayerGameMode gameMode) {
        if (AutumnClient.options.infoHud.get()) {
            return gameMode.hasExperience() &&
                    ((minecraft.player.jumpableVehicle() != null
                            && !minecraft.options.keyJump.isDown()
                            && minecraft.player.getJumpRidingScale() <= 0)
                            || minecraft.player.jumpableVehicle() == null);
        }
        return gameMode.hasExperience();
    }

    // EffectHud
    @Inject(method = "extractEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V", shift = At.Shift.AFTER))
    private void extractEffects(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker, CallbackInfo ci, @Local MobEffectInstance statusEffectInstance, @Local(ordinal = 2) int x, @Local(ordinal = 3) int y) {
        if (AutumnClient.options.infoHud.get()) {
            String duration = durationString(statusEffectInstance);
            extractor.text(minecraft.font, duration, x + 2, y + 24 - minecraft.font.lineHeight, 0xffffffff, false);
            String amplifier = amplipierString(statusEffectInstance.getAmplifier());
            extractor.text(minecraft.font, amplifier, x + 23 - minecraft.font.width(amplifier), y + 2, 0xffffffff, false);
        }
    }

    @Unique
    private String durationString(MobEffectInstance statusEffectInstance) {
        if (statusEffectInstance.isInfiniteDuration())
            return I18n.get("effect.duration.infinite");

        int second = statusEffectInstance.getDuration() / 20;

        if (second >= 60 * 60 * 24 * 365)
            return second / (60 * 60 * 24 * 365) + "y";
        else if (second >= 60 * 60 * 24 * 30)
            return second / (60 * 60 * 24 * 30) + "mo";
        else if (second >= 60 * 60 * 24)
            return second / (60 * 60 * 24) + "d";
        else if (second >= 60 * 60)
            return second / (60 * 60) + "h";
        else if (second >= 60 * 10)
            return second / 60 + "m";
        else if (second >= 60)
            return second / 60 + ":" + second % 60;
        else
            return String.valueOf(second);
    }

    @Unique
    private String amplipierString(int amplifier) {
        if (amplifier == 0)
            return "";

        String key = "enchantment.level." + (amplifier + 1);

        String translation = I18n.get(key);
        return translation.equals(key) ? String.valueOf(amplifier + 1) : translation;
    }

    // FreeCam
    @Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
    private void getCameraPlayer(CallbackInfoReturnable<Player> cir) {
        if (AutumnClient.options.freeCam.get()) {
            cir.setReturnValue(minecraft.player);
        }
    }
}
