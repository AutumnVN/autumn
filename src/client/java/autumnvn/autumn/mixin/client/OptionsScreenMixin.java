package autumnvn.autumn.mixin.client;

import autumnvn.autumn.SettingsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    @Unique
    private Button settingsButton;

    // SettingsButton
    @Inject(method = "init()V", at = @At("TAIL"))
    private void init(CallbackInfo info) {

        settingsButton = Button.builder(Component.literal("Autumn Settings..."), button -> {
            if (this.minecraft != null) {
                minecraft.gui.setScreen(new SettingsScreen(this));
            }
        }).bounds(this.width / 2 - 154, 54, 150, 20).build();
        this.addRenderableWidget(settingsButton);
    }

    @Inject(method = "repositionElements()V", at = @At("TAIL"))
    private void repositionElements(CallbackInfo info) {
        if (settingsButton != null) {
            settingsButton.setX(this.width / 2 - 154);
            settingsButton.setY(54);
        }
    }

    public OptionsScreenMixin() {
        super(Component.translatable("options.title"));
    }
}