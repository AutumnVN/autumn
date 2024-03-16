package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import autumnvn.autumn.AutumnClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    private long lastTime;
    private long lastTick;

    // TPS
    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"))
    private void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        long tick = packet.getTime();
        long time = System.nanoTime();
        if (lastTick != 0 && lastTime != 0) {
            int passedTick = (int) (tick - lastTick);
            if (passedTick > 0) {
                AutumnClient.tps = Math.min(1000 / ((time - lastTime) / 1000000 / passedTick), 20);
            }
        }
        lastTick = tick;
        lastTime = time;
    }
}
