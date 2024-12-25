package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleOption.class)
public class SimpleOptionMixin {

    @Final
    @Shadow
    Text text;

    // FullBright
    @Inject(method = "getValue", at = @At("HEAD"), cancellable = true)
    private void getValue(CallbackInfoReturnable<Double> cir) {
        if (text.getString().equals(I18n.translate("options.gamma")) && AutumnClient.options.fullBright.getValue()) {
            cir.setReturnValue(10.0);
        }
    }
}
