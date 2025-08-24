package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import autumnvn.autumn.FreeCam;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Shadow
    public abstract boolean isOf(Block block);

    // FreeCam
    @Inject(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("HEAD"), cancellable = true)
    private void getCollisionShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof EntityShapeContext entityContext && entityContext.getEntity() instanceof FreeCam && AutumnClient.options.freeCam.getValue()) {
            cir.setReturnValue(VoxelShapes.empty());
        }
    }

    // VisibleBarrier
    @Inject(method = "isSideInvisible", at = @At("HEAD"), cancellable = true)
    private void isSideInvisible(BlockState state, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (AutumnClient.options.visibleBarrier.getValue() && this.isOf(Blocks.BARRIER) && state.isOf(Blocks.BARRIER)) {
            cir.setReturnValue(true);
        }
    }
}
