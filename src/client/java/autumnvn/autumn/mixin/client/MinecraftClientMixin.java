package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import autumnvn.autumn.AutumnClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.tutorial.TutorialManager;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Unique
    private boolean delayFirstUse = false;

    // NoUseDelay
    @ModifyConstant(method = "doItemUse", constant = @Constant(intValue = 4))
    private int itemUseCooldown(int original) {
        if (AutumnClient.options.noUseDelay.getValue()) {
            if (!delayFirstUse) {
                delayFirstUse = true;
                return original;
            }
            return 0;
        }
        return original;
    }

    @Inject(method = "handleInputEvents", at = @At("TAIL"))
    private void onHandleInputEvents(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        if (!client.options.useKey.isPressed()) {
            delayFirstUse = false;
        }
    }

    @Final
    @Shadow
    private TutorialManager tutorialManager;

    @Shadow
    public void setScreen(Screen screen) {}

    // PlayerInventoryWhileRiding
    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;openRidingInventory()V"))
    public void openRidingInventory(ClientPlayerEntity player) {
        if (AutumnClient.client.options.sprintKey.isPressed()) {
            tutorialManager.onInventoryOpened();
            setScreen(new InventoryScreen(player));
        } else {
            player.openRidingInventory();
        }
    }
}
