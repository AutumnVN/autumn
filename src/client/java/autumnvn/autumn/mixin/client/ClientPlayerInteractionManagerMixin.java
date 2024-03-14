package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import autumnvn.autumn.AutumnClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    // NoMineDelay
    @ModifyConstant(method = "updateBlockBreakingProgress", constant = @Constant(intValue = 5))
    private int updateBlockBreakingProgress(int original) {
        return AutumnClient.options.noMineDelay.getValue() ? 0 : original;
    }
}
