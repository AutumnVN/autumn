package autumnvn.autumn.mixin.client;

import autumnvn.autumn.AutumnClient;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatHudMixin {

    // BetterChat
    @ModifyConstant(method = "addMessageToQueue", constant = @Constant(intValue = 100))
    private int maxVisibleChatLength(int original) {
        return AutumnClient.options.betterChat.get() ? 65536 : original;
    }

    @Inject(method = "clearMessages", at = @At("HEAD"), cancellable = true)
    private void clearMessages(CallbackInfo ci) {
        if (AutumnClient.options.betterChat.get()) {
            ci.cancel();
        }
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/chat/GuiMessage;<init>(ILnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V"), index = 4)
    private GuiMessageTag messageIndicator(GuiMessageTag original) {
        return AutumnClient.options.betterChat.get() ? null : original;
    }
}