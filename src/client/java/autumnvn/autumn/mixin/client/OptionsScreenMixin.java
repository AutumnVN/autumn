package autumnvn.autumn.mixin.client;

import autumnvn.autumn.SettingsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    @Unique
    private ButtonWidget settingsButton;

    // SettingsButton
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {

        settingsButton = ButtonWidget.builder(Text.of("Autumn Settings..."), button -> {
            if (this.client != null) {
                this.client.setScreen(new SettingsScreen(this));
            }
        }).dimensions(this.width / 2 - 154, 54, 150, 20).build();
        this.addDrawableChild(settingsButton);
    }

    @Inject(method = "refreshWidgetPositions", at = @At("TAIL"))
    private void refreshWidgetPositions(CallbackInfo info) {
        if (settingsButton != null) {
            settingsButton.setPosition(this.width / 2 - 154, 54);
        }
    }

    public OptionsScreenMixin() {
        super(Text.translatable("options.title", new Object[0]));
    }
}
