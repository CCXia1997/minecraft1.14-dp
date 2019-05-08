package net.minecraft.client.gui.menu.options;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class MouseOptionsScreen extends Screen
{
    private final Screen parent;
    private ButtonListWidget buttonList;
    private static final GameOption[] OPTIONS;
    
    public MouseOptionsScreen(final Screen screen) {
        super(new TranslatableTextComponent("options.mouse_settings.title", new Object[0]));
        this.parent = screen;
    }
    
    @Override
    protected void init() {
        (this.buttonList = new ButtonListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25)).addAll(MouseOptionsScreen.OPTIONS);
        this.children.add(this.buttonList);
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, I18n.translate("gui.done"), buttonWidget -> {
            this.minecraft.options.write();
            this.minecraft.openScreen(this.parent);
        }));
    }
    
    @Override
    public void removed() {
        this.minecraft.options.write();
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.buttonList.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 5, 16777215);
        super.render(mouseX, mouseY, delta);
    }
    
    static {
        OPTIONS = new GameOption[] { GameOption.SENSITIVITY, GameOption.INVERT_MOUSE, GameOption.MOUSE_WHEEL_SENSITIVITY, GameOption.DISCRETE_MOUSE_SCROLL, GameOption.TOUCHSCREEN };
    }
}
