package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import autumnvn.autumn.AutumnClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    // PingNumber
    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    private void renderLatencyIcon(DrawContext context, int width, int x, int y, PlayerListEntry entry, CallbackInfo ci) {
        if (AutumnClient.options.pingNumber.getValue()) {
            int ping = entry.getLatency();
            context.drawTextWithShadow(AutumnClient.client.textRenderer, ping + "ms", x + width - AutumnClient.client.textRenderer.getWidth(ping + "ms"), y,
                    ping > 1000 ? 0xaa0000
                            : ping > 600 ? 0xff5555
                                    : ping > 300 ? 0xffaa00
                                            : ping > 150 ? 0xffff55
                                                    : 0x55ff55);
            ci.cancel();
        }
    }

    @ModifyVariable(method = "render", at = @At(value = "STORE"), ordinal = 7)
    private int q(int original) {
        return AutumnClient.options.pingNumber.getValue() ? original + AutumnClient.client.textRenderer.getWidth("9999ms") : original;
    }
}
