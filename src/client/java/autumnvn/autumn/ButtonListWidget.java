package autumnvn.autumn;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ButtonListWidget extends ContainerObjectSelectionList<ButtonListWidget.ButtonEntry> {
    public ButtonListWidget(Minecraft client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
    }

    void addOptionEntry(OptionInstance<?> options) {
        this.addEntry(ButtonEntry.create(this.width, options));
    }

    void addOptionEntry(OptionInstance<?> firstOption, OptionInstance<?> secondOption) {
        this.addEntry(ButtonEntry.create(this.width, firstOption, secondOption));
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    static class ButtonEntry extends ContainerObjectSelectionList.Entry<ButtonEntry> {
        List<AbstractWidget> buttons;

        ButtonEntry(Map<OptionInstance<?>, AbstractWidget> buttonMap) {
            this.buttons = ImmutableList.copyOf(buttonMap.values());
        }

        static ButtonEntry create(int width, OptionInstance<?> option) {
            return new ButtonEntry(ImmutableMap.of(option, option.createButton(AutumnClient.minecraft.options, width / 2 - 155, 0, 150)));
        }

        static ButtonEntry create(int width, OptionInstance<?> firstOption, OptionInstance<?> secondOption) {
            return new ButtonEntry(ImmutableMap.of(firstOption, firstOption.createButton(AutumnClient.minecraft.options, width / 2 - 155, 0, 150), secondOption, secondOption.createButton(AutumnClient.minecraft.options, width / 2 + 5, 0, 150)));
        }

        @Override
        public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            this.buttons.forEach(button -> {
                button.setY(getContentY());
                button.extractRenderState(context, mouseX, mouseY, deltaTicks);
            });
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.buttons;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.buttons;
        }

        @Override
        public void visitWidgets(Consumer<AbstractWidget> consumer) {
            this.buttons.forEach(consumer);
        }
    }

}