package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    @Final
    @Shadow
    private MinecraftClient client;

    // PingNumber
    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    private void renderLatencyIcon(DrawContext context, int width, int x, int y, PlayerListEntry entry, CallbackInfo ci) {
        if (AutumnClient.options.pingNumber.getValue()) {
            int ping = entry.getLatency();
            context.drawTextWithShadow(client.textRenderer,
                    ping + "ms",
                    x + width - client.textRenderer.getWidth(ping + "ms"),
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

    @ModifyVariable(method = "render", at = @At(value = "STORE"), ordinal = 7)
    private int q(int original) {
        return AutumnClient.options.pingNumber.getValue() ? original + client.textRenderer.getWidth("9999ms") : original;
    }
}
