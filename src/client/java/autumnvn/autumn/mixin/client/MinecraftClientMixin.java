package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import autumnvn.autumn.AutumnClient;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    private int itemUseCooldown;

    // NoUseDelay
    @ModifyConstant(method = "doItemUse", constant = @Constant(intValue = 4))
    private int noUseDelay(int original) {
        return AutumnClient.options.noUseDelay.getValue() ? 0 : original;
    }
}
