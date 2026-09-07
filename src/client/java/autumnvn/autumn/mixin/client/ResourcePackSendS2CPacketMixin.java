package autumnvn.autumn.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonPacketListenerImpl.class)
public class ResourcePackSendS2CPacketMixin {

    @Shadow
    @Final
    protected Minecraft minecraft;

    // ServerResourcePackSpoofing
    @Inject(method = "handleResourcePackPush", at = @At("HEAD"), cancellable = true)
    private void apply(ClientboundResourcePackPushPacket packet, CallbackInfo ci) {
        if (minecraft.getCurrentServer() != null
                && minecraft.getCurrentServer().getResourcePackStatus() == ServerData.ServerPackStatus.DISABLED
                && minecraft.getConnection() != null) {
            minecraft.getConnection().getConnection().send(new ServerboundResourcePackPacket(packet.id(), ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED));
            ci.cancel();
        }
    }
}