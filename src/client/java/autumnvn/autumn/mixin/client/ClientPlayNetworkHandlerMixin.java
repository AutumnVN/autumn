package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {

    @Unique
    private long lastTime;

    @Unique
    private long lastTick;

    // TPS
    @Inject(method = "handleSetTime", at = @At("HEAD"))
    private void handleSetTime(ClientboundSetTimePacket packet, CallbackInfo ci) {
        long tick = packet.gameTime();
        long time = System.nanoTime();
        if (lastTick != 0 && lastTime != 0) {
            long passedTick = tick - lastTick;
            long passedTime = time - lastTime;
            if (passedTick > 0 && passedTime > 0) {
                long mspt = passedTime / passedTick / 1000000;
                if (mspt > 0) {
                    AutumnClient.tps = Math.min(1000.0 / mspt, 20.0);
                }
            }
        }
        lastTick = tick;
        lastTime = time;
    }

    // FreeCam
    @Inject(method = "handleRespawn", at = @At("HEAD"))
    private void handleRespawn(ClientboundRespawnPacket packet, CallbackInfo ci) {
        AutumnClient.options.freeCam.set(false);
    }
}