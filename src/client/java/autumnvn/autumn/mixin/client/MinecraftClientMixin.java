package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Unique
    private boolean delayFirstUse = false;

    @Shadow
    private static Minecraft instance;

    // NoUseDelay
    @ModifyConstant(method = "startUseItem", constant = @Constant(intValue = 4))
    private int itemUseCooldown(int original) {
        if (AutumnClient.options.noUseDelay.get()) {
            if (!delayFirstUse) {
                delayFirstUse = true;
                return original;
            }
            return 0;
        }
        return original;
    }

    @Inject(method = "handleKeybinds", at = @At("TAIL"))
    private void onHandleKeybinds(CallbackInfo ci) {
        if (!instance.options.keyUse.isDown()) {
            delayFirstUse = false;
        }
    }
}