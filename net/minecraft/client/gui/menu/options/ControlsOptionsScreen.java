package net.minecraft.client.gui.menu.options;

import net.minecraft.util.SystemUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.KeyBindingListWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.GameOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class ControlsOptionsScreen extends Screen
{
    private static final GameOption[] OPTIONS;
    private final Screen parent;
    private final GameOptions options;
    public KeyBinding focusedBinding;
    public long time;
    private KeyBindingListWidget keyBindingListWidget;
    private ButtonWidget resetButton;
    
    public ControlsOptionsScreen(final Screen parent, final GameOptions options) {
        super(new TranslatableTextComponent("controls.title", new Object[0]));
        this.parent = parent;
        this.options = options;
    }
    
    @Override
    protected void init() {
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, 18, 150, 20, I18n.translate("options.mouse_settings"), buttonWidget -> this.minecraft.openScreen(new MouseOptionsScreen(this))));
        this.<AbstractButtonWidget>addButton(GameOption.AUTO_JUMP.createOptionButton(this.minecraft.options, this.width / 2 - 155 + 160, 18, 150));
        this.keyBindingListWidget = new KeyBindingListWidget(this, this.minecraft);
        this.children.add(this.keyBindingListWidget);
        final KeyBinding[] keysAll;
        int length;
        int i = 0;
        KeyBinding keyBinding5;
        this.resetButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, I18n.translate("controls.resetAll"), buttonWidget -> {
            keysAll = this.minecraft.options.keysAll;
            for (length = keysAll.length; i < length; ++i) {
                keyBinding5 = keysAll[i];
                keyBinding5.setKeyCode(keyBinding5.getDefaultKeyCode());
            }
            KeyBinding.updateKeysByCode();
            return;
        }));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(this.parent)));
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (this.focusedBinding != null) {
            this.options.setKeyCode(this.focusedBinding, InputUtil.Type.c.createFromCode(button));
            this.focusedBinding = null;
            KeyBinding.updateKeysByCode();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (this.focusedBinding != null) {
            if (keyCode == 256) {
                this.options.setKeyCode(this.focusedBinding, InputUtil.UNKNOWN_KEYCODE);
            }
            else {
                this.options.setKeyCode(this.focusedBinding, InputUtil.getKeyCode(keyCode, scanCode));
            }
            this.focusedBinding = null;
            this.time = SystemUtil.getMeasuringTimeMs();
            KeyBinding.updateKeysByCode();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.keyBindingListWidget.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
        boolean boolean4 = false;
        for (final KeyBinding keyBinding8 : this.options.keysAll) {
            if (!keyBinding8.isDefault()) {
                boolean4 = true;
                break;
            }
        }
        this.resetButton.active = boolean4;
        super.render(mouseX, mouseY, delta);
    }
    
    static {
        OPTIONS = new GameOption[] { GameOption.INVERT_MOUSE, GameOption.SENSITIVITY, GameOption.TOUCHSCREEN, GameOption.AUTO_JUMP };
    }
}
