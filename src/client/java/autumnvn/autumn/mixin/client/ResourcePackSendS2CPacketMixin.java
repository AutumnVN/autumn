package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;

@Mixin(ResourcePackSendS2CPacket.class)
public class ResourcePackSendS2CPacketMixin {

    @Shadow
    public UUID id() {
        return null;
    }

    // ServerResourcePackSpoofing
    @Inject(method = "apply*", at = @At("HEAD"), cancellable = true)
    private void apply(CallbackInfo ci) {
        if (Objects.requireNonNull(AutumnClient.client.getCurrentServerEntry()).getResourcePackPolicy() == ServerInfo.ResourcePackPolicy.DISABLED) {
            Objects.requireNonNull(AutumnClient.client.getNetworkHandler()).sendPacket(new ResourcePackStatusC2SPacket(id(), ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
            ci.cancel();
        }
    }
}
