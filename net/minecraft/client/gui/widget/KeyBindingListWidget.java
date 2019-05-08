package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import net.minecraft.text.TextFormat;
import java.util.Collections;
import net.minecraft.client.gui.Element;
import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.options.ControlsOptionsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KeyBindingListWidget extends ElementListWidget<Entry>
{
    private final ControlsOptionsScreen gui;
    private int b;
    
    public KeyBindingListWidget(final ControlsOptionsScreen gui, final MinecraftClient minecraftClient) {
        super(minecraftClient, gui.width + 45, gui.height, 43, gui.height - 32, 20);
        this.gui = gui;
        final KeyBinding[] arr3 = (KeyBinding[])ArrayUtils.clone((Object[])minecraftClient.options.keysAll);
        Arrays.sort(arr3);
        String string4 = null;
        for (final KeyBinding keyBinding8 : arr3) {
            final String string5 = keyBinding8.getCategory();
            if (!string5.equals(string4)) {
                string4 = string5;
                ((EntryListWidget<CategoryEntry>)this).addEntry(new CategoryEntry(string5));
            }
            final int integer10 = minecraftClient.textRenderer.getStringWidth(I18n.translate(keyBinding8.getId()));
            if (integer10 > this.b) {
                this.b = integer10;
            }
            ((EntryListWidget<KeyBindingEntry>)this).addEntry(new KeyBindingEntry(keyBinding8));
        }
    }
    
    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15;
    }
    
    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends ElementListWidget.Entry<Entry>
    {
    }
    
    @Environment(EnvType.CLIENT)
    public class CategoryEntry extends Entry
    {
        private final String name;
        private final int nameWidth;
        
        public CategoryEntry(final String string) {
            this.name = I18n.translate(string);
            this.nameWidth = KeyBindingListWidget.this.minecraft.textRenderer.getStringWidth(this.name);
        }
        
        @Override
        public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
            final TextRenderer textRenderer = KeyBindingListWidget.this.minecraft.textRenderer;
            final String name = this.name;
            final float x = (float)(KeyBindingListWidget.this.minecraft.currentScreen.width / 2 - this.nameWidth / 2);
            final int n = integer2 + height;
            KeyBindingListWidget.this.minecraft.textRenderer.getClass();
            textRenderer.draw(name, x, (float)(n - 9 - 1), 16777215);
        }
        
        @Override
        public boolean changeFocus(final boolean lookForwards) {
            return false;
        }
        
        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }
    }
    
    @Environment(EnvType.CLIENT)
    public class KeyBindingEntry extends Entry
    {
        private final KeyBinding binding;
        private final String bindingName;
        private final ButtonWidget editButton;
        private final ButtonWidget resetButton;
        
        private KeyBindingEntry(final KeyBinding keyBinding) {
            this.binding = keyBinding;
            this.bindingName = I18n.translate(keyBinding.getId());
            this.editButton = new ButtonWidget(0, 0, 75, 20, this.bindingName, buttonWidget -> KeyBindingListWidget.this.gui.focusedBinding = keyBinding) {
                @Override
                protected String getNarrationMessage() {
                    if (keyBinding.isNotBound()) {
                        return I18n.translate("narrator.controls.unbound", KeyBindingEntry.this.bindingName);
                    }
                    return I18n.translate("narrator.controls.bound", KeyBindingEntry.this.bindingName, super.getNarrationMessage());
                }
            };
            this.resetButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset"), buttonWidget -> {
                KeyBindingListWidget.this.minecraft.options.setKeyCode(keyBinding, keyBinding.getDefaultKeyCode());
                KeyBinding.updateKeysByCode();
            }) {
                @Override
                protected String getNarrationMessage() {
                    return I18n.translate("narrator.controls.reset", KeyBindingEntry.this.bindingName);
                }
            };
        }
        
        @Override
        public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
            final boolean boolean10 = KeyBindingListWidget.this.gui.focusedBinding == this.binding;
            final TextRenderer textRenderer = KeyBindingListWidget.this.minecraft.textRenderer;
            final String bindingName = this.bindingName;
            final float x = (float)(integer3 + 90 - KeyBindingListWidget.this.b);
            final int n = integer2 + height / 2;
            KeyBindingListWidget.this.minecraft.textRenderer.getClass();
            textRenderer.draw(bindingName, x, (float)(n - 9 / 2), 16777215);
            this.resetButton.x = integer3 + 190;
            this.resetButton.y = integer2;
            this.resetButton.active = !this.binding.isDefault();
            this.resetButton.render(mouseX, mouseY, delta);
            this.editButton.x = integer3 + 105;
            this.editButton.y = integer2;
            this.editButton.setMessage(this.binding.getLocalizedName());
            boolean boolean11 = false;
            if (!this.binding.isNotBound()) {
                for (final KeyBinding keyBinding15 : KeyBindingListWidget.this.minecraft.options.keysAll) {
                    if (keyBinding15 != this.binding && this.binding.equals(keyBinding15)) {
                        boolean11 = true;
                        break;
                    }
                }
            }
            if (boolean10) {
                this.editButton.setMessage(TextFormat.p + "> " + TextFormat.o + this.editButton.getMessage() + TextFormat.p + " <");
            }
            else if (boolean11) {
                this.editButton.setMessage(TextFormat.m + this.editButton.getMessage());
            }
            this.editButton.render(mouseX, mouseY, delta);
        }
        
        @Override
        public List<? extends Element> children() {
            return ImmutableList.<ButtonWidget>of(this.editButton, this.resetButton);
        }
        
        @Override
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            return this.editButton.mouseClicked(mouseX, mouseY, button) || this.resetButton.mouseClicked(mouseX, mouseY, button);
        }
        
        @Override
        public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
            return this.editButton.mouseReleased(mouseX, mouseY, button) || this.resetButton.mouseReleased(mouseX, mouseY, button);
        }
    }
}
