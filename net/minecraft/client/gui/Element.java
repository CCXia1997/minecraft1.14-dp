package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Element
{
    default void mouseMoved(final double mouseX, final double mouseY) {
    }
    
    default boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return false;
    }
    
    default boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return false;
    }
    
    default boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return false;
    }
    
    default boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return false;
    }
    
    default boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }
    
    default boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        return false;
    }
    
    default boolean charTyped(final char chr, final int keyCode) {
        return false;
    }
    
    default boolean changeFocus(final boolean lookForwards) {
        return false;
    }
    
    default boolean isMouseOver(final double mouseX, final double mouseY) {
        return false;
    }
}
