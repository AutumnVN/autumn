package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.render.command.LabelCommandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(LabelCommandRenderer.Commands.class)
interface LabelCommandRendererCommandsAccessor {
    @Accessor("seethroughLabels")
    List<?> getSeethroughLabels();

    @Accessor("normalLabels")
    List<?> getNormalLabels();
}

@Mixin(LabelCommandRenderer.Commands.class)
public class LabelCommandRendererCommandsMixin {

    // BetterNametag
    @Redirect(method = "add", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/command/LabelCommandRenderer$Commands;normalLabels:Ljava/util/List;"))
    private List<?> normalLabels(LabelCommandRenderer.Commands instance) {
        return AutumnClient.options.betterNametag.getValue()
                ? ((LabelCommandRendererCommandsAccessor) instance).getSeethroughLabels()
                : ((LabelCommandRendererCommandsAccessor) instance).getNormalLabels();
    }
}
