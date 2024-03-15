package autumnvn.autumn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import autumnvn.autumn.AutumnClient;
import net.minecraft.block.BarrierBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;

@Mixin(BarrierBlock.class)
public class BarrierBlockMixin {

    // VisibleBarrier
    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void getRenderType(CallbackInfoReturnable<BlockRenderType> cir) {
        if (AutumnClient.options.visibleBarrier.getValue()) {
            cir.setReturnValue(BlockRenderType.MODEL);
        }
    }

    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return AutumnClient.options.visibleBarrier.getValue() && stateFrom.isOf(Blocks.BARRIER);
    }
}
