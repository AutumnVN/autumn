package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerTabOverlay.class)
public class PlayerListHudMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    // PingNumber
    @Inject(method = "extractPingIcon", at = @At("HEAD"), cancellable = true)
    private void extractPingIcon(GuiGraphicsExtractor context, int width, int x, int y, PlayerInfo entry, CallbackInfo ci) {
        if (AutumnClient.options.pingNumber.get()) {
            int ping = entry.getLatency();
            context.text(minecraft.font, ping + "ms",
                    x + width - minecraft.font.width(ping + "ms"),
                    y,
                    ping > 1000 ? 0xffaa0000
                            : ping > 600 ? 0xffff5555
                            : ping > 300 ? 0xffffaa00
                            : ping > 150 ? 0xffffff55
                            : 0xff55ff55
            );
            ci.cancel();
        }
    }
}