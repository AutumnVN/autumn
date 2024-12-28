package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    // NoMineDelay
    @ModifyConstant(method = "updateBlockBreakingProgress", constant = @Constant(intValue = 5))
    private int blockBreakingCooldown(int original) {
        return AutumnClient.options.noMineDelay.getValue() ? 0 : original;
    }

    @Shadow
    private ItemStack selectedStack;

    // KeepMiningWhenSwap
    @ModifyVariable(method = "isCurrentlyBreaking", at = @At("STORE"))
    private ItemStack itemStack(ItemStack original) {
        return AutumnClient.options.keepMiningWhenSwap.getValue() ? this.selectedStack : original;
    }

    // FreeCam
    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (target.equals(AutumnClient.client.player)) {
            ci.cancel();
        }
    }
}
