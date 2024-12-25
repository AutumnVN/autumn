package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {

    // DeathCoord
    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        if (AutumnClient.options.deathCoord.getValue()) {
            if (AutumnClient.client.player != null && AutumnClient.client.world != null) {
                AutumnClient.client.player.sendMessage(Text.of(
                        String.format("You died at §a%d %d %d §rin §a%s",
                                AutumnClient.client.player.getBlockX(),
                                AutumnClient.client.player.getBlockY(),
                                AutumnClient.client.player.getBlockZ(),
                                AutumnClient.client.world.getRegistryKey().getValue().toString().split(":")[1])
                ), false);
            }
        }
    }

}
