package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {

    // DeathCoord
    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        if (AutumnClient.options.deathCoord.get()) {
            if (AutumnClient.minecraft.player != null && AutumnClient.minecraft.level != null) {
                AutumnClient.minecraft.player.sendSystemMessage(Component.literal(
                        String.format("You died at §a%d %d %d §rin §a%s",
                                AutumnClient.minecraft.player.getBlockX(),
                                AutumnClient.minecraft.player.getBlockY(),
                                AutumnClient.minecraft.player.getBlockZ(),
                                AutumnClient.minecraft.level.dimension().identifier().getPath())
                ));
            }
        }
    }
}