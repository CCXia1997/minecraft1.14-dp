package net.minecraft.client.util;

import net.minecraft.text.TextFormat;
import net.minecraft.client.gui.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.SharedConstants;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SelectionManager
{
    private final MinecraftClient client;
    private final TextRenderer fontRenderer;
    private final Supplier<String> stringSupplier;
    private final Consumer<String> stringConsumer;
    private final int e;
    private int selectionStart;
    private int selectionEnd;
    
    public SelectionManager(final MinecraftClient minecraftClient, final Supplier<String> supplier, final Consumer<String> consumer, final int integer) {
        this.client = minecraftClient;
        this.fontRenderer = minecraftClient.textRenderer;
        this.stringSupplier = supplier;
        this.stringConsumer = consumer;
        this.e = integer;
        this.moveCaretToEnd();
    }
    
    public boolean insert(final char character) {
        if (SharedConstants.isValidChar(character)) {
            this.insert(Character.toString(character));
        }
        return true;
    }
    
    private void insert(final String string) {
        if (this.selectionEnd != this.selectionStart) {
            this.deleteSelectedText();
        }
        final String string2 = this.stringSupplier.get();
        this.selectionStart = MathHelper.clamp(this.selectionStart, 0, string2.length());
        final String string3 = new StringBuilder(string2).insert(this.selectionStart, string).toString();
        if (this.fontRenderer.getStringWidth(string3) <= this.e) {
            this.stringConsumer.accept(string3);
            final int min = Math.min(string3.length(), this.selectionStart + string.length());
            this.selectionStart = min;
            this.selectionEnd = min;
        }
    }
    
    public boolean handleSpecialKey(final int keyCode) {
        String string2 = this.stringSupplier.get();
        if (Screen.isSelectAll(keyCode)) {
            this.selectionEnd = 0;
            this.selectionStart = string2.length();
            return true;
        }
        if (Screen.isCopy(keyCode)) {
            this.client.keyboard.setClipboard(this.getSelectedText());
            return true;
        }
        if (Screen.isPaste(keyCode)) {
            this.insert(SharedConstants.stripInvalidChars(TextFormat.stripFormatting(this.client.keyboard.getClipboard().replaceAll("\\r", ""))));
            this.selectionEnd = this.selectionStart;
            return true;
        }
        if (Screen.isCut(keyCode)) {
            this.client.keyboard.setClipboard(this.getSelectedText());
            this.deleteSelectedText();
            return true;
        }
        if (keyCode == 259) {
            if (!string2.isEmpty()) {
                if (this.selectionEnd != this.selectionStart) {
                    this.deleteSelectedText();
                }
                else if (this.selectionStart > 0) {
                    string2 = new StringBuilder(string2).deleteCharAt(Math.max(0, this.selectionStart - 1)).toString();
                    final int max = Math.max(0, this.selectionStart - 1);
                    this.selectionStart = max;
                    this.selectionEnd = max;
                    this.stringConsumer.accept(string2);
                }
            }
            return true;
        }
        if (keyCode == 261) {
            if (!string2.isEmpty()) {
                if (this.selectionEnd != this.selectionStart) {
                    this.deleteSelectedText();
                }
                else if (this.selectionStart < string2.length()) {
                    string2 = new StringBuilder(string2).deleteCharAt(Math.max(0, this.selectionStart)).toString();
                    this.stringConsumer.accept(string2);
                }
            }
            return true;
        }
        if (keyCode == 263) {
            final int integer3 = this.fontRenderer.isRightToLeft() ? 1 : -1;
            if (Screen.hasControlDown()) {
                this.selectionStart = this.fontRenderer.findWordEdge(string2, integer3, this.selectionStart, true);
            }
            else {
                this.selectionStart = Math.max(0, Math.min(string2.length(), this.selectionStart + integer3));
            }
            if (!Screen.hasShiftDown()) {
                this.selectionEnd = this.selectionStart;
            }
            return true;
        }
        if (keyCode == 262) {
            final int integer3 = this.fontRenderer.isRightToLeft() ? -1 : 1;
            if (Screen.hasControlDown()) {
                this.selectionStart = this.fontRenderer.findWordEdge(string2, integer3, this.selectionStart, true);
            }
            else {
                this.selectionStart = Math.max(0, Math.min(string2.length(), this.selectionStart + integer3));
            }
            if (!Screen.hasShiftDown()) {
                this.selectionEnd = this.selectionStart;
            }
            return true;
        }
        if (keyCode == 268) {
            this.selectionStart = 0;
            if (!Screen.hasShiftDown()) {
                this.selectionEnd = this.selectionStart;
            }
            return true;
        }
        if (keyCode == 269) {
            this.selectionStart = this.stringSupplier.get().length();
            if (!Screen.hasShiftDown()) {
                this.selectionEnd = this.selectionStart;
            }
            return true;
        }
        return false;
    }
    
    private String getSelectedText() {
        final String string1 = this.stringSupplier.get();
        final int integer2 = Math.min(this.selectionStart, this.selectionEnd);
        final int integer3 = Math.max(this.selectionStart, this.selectionEnd);
        return string1.substring(integer2, integer3);
    }
    
    private void deleteSelectedText() {
        if (this.selectionEnd == this.selectionStart) {
            return;
        }
        final String string1 = this.stringSupplier.get();
        final int integer2 = Math.min(this.selectionStart, this.selectionEnd);
        final int integer3 = Math.max(this.selectionStart, this.selectionEnd);
        final String string2 = string1.substring(0, integer2) + string1.substring(integer3);
        this.selectionStart = integer2;
        this.selectionEnd = this.selectionStart;
        this.stringConsumer.accept(string2);
    }
    
    public void moveCaretToEnd() {
        final int length = this.stringSupplier.get().length();
        this.selectionStart = length;
        this.selectionEnd = length;
    }
    
    public int getSelectionStart() {
        return this.selectionStart;
    }
    
    public int getSelectionEnd() {
        return this.selectionEnd;
    }
}
