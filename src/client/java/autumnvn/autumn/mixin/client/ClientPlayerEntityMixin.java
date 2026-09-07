package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin {

    @Shadow
    @Final
    protected Minecraft minecraft;

    // AutoSprint
    @Inject(method = "aiStep", at = @At("TAIL"))
    private void aiStep(CallbackInfo info) {
        if (AutumnClient.options.autoSprint.get() && minecraft.player != null) {
            minecraft.player.setSprinting(minecraft.options.keyUp.isDown());
        }
    }
}