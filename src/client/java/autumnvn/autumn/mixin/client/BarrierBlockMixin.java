package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BarrierBlock.class)
public class BarrierBlockMixin {

    // VisibleBarrier
    @Inject(method = "getRenderShape", at = @At("HEAD"), cancellable = true)
    private void getRenderShape(BlockState state, CallbackInfoReturnable<RenderShape> cir) {
        if (AutumnClient.options.visibleBarrier.get()) {
            cir.setReturnValue(RenderShape.MODEL);
        }
    }
}