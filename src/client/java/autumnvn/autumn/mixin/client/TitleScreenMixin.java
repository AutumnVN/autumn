package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Shadow
    @Mutable
    private boolean doBackgroundFade;

    // NoFade
    @Inject(method = "<init>(ZLnet/minecraft/client/gui/LogoDrawer;)V", at = @At("TAIL"))
    private void init(boolean doBackgroundFade, LogoDrawer logoDrawer, CallbackInfo ci) {
        if (AutumnClient.options.noFade.getValue()) {
            this.doBackgroundFade = false;
        }
    }
}
