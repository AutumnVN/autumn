package autumnvn.autumn;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SettingsScreen extends Screen {
    Screen parent;
    ButtonListWidget list;
    OptionInstance<?>[] options = new OptionInstance<?>[]{
            AutumnClient.options.autoAttack,
            AutumnClient.options.ignorePlayer,
            AutumnClient.options.autoHitSwap,
            AutumnClient.options.autoSprint,
            AutumnClient.options.betterChat,
            AutumnClient.options.betterNametag,
            AutumnClient.options.boat360,
            AutumnClient.options.deathCoord,
            AutumnClient.options.freeCam,
            AutumnClient.options.fullBright,
            AutumnClient.options.infoHud,
            AutumnClient.options.instantSneak,
            AutumnClient.options.keepMiningWhenSwap,
            AutumnClient.options.noFade,
            AutumnClient.options.noFishingBobber,
            AutumnClient.options.noFog,
            AutumnClient.options.noInvisible,
            AutumnClient.options.noJumpDelay,
            AutumnClient.options.noMineDelay,
            AutumnClient.options.noToast,
            AutumnClient.options.noUseDelay,
            AutumnClient.options.pingNumber,
            AutumnClient.options.rightClickHarvest,
            AutumnClient.options.thirdPersonNoClip,
            AutumnClient.options.visibleBarrier
    };

    public SettingsScreen(Screen parent) {
        super(Component.literal("Autumn Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.list = new ButtonListWidget(this.minecraft, this.width, this.height - 64, 32, 25);
        for (int i = 0; i < options.length; i += 2) {
            if (i + 1 < options.length) {
                this.list.addOptionEntry(options[i], options[i + 1]);
            } else {
                this.list.addOptionEntry(options[i]);
            }
        }
        this.addRenderableWidget(this.list);

        Button doneButton = Button.builder(CommonComponents.GUI_DONE, button -> {
            AutumnClient.options.save();
            if (this.minecraft != null) {
                this.minecraft.gui.setScreen(parent);
            }
        }).bounds(this.width / 2 - 100, this.height - 26, 200, 20).build();
        this.addRenderableWidget(doneButton);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 12, 0xffffffff);
    }

    @Override
    public void removed() {
        AutumnClient.options.save();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.gui.setScreen(parent);
        }
    }
}