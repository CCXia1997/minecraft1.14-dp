package net.minecraft.client.gui;

import java.util.function.Supplier;
import java.util.function.BooleanSupplier;
import java.util.ListIterator;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Optional;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ParentElement extends Element
{
    List<? extends Element> children();
    
    default Optional<Element> hoveredElement(final double mouseX, final double mouseY) {
        for (final Element element6 : this.children()) {
            if (element6.isMouseOver(mouseX, mouseY)) {
                return Optional.<Element>of(element6);
            }
        }
        return Optional.<Element>empty();
    }
    
    default boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        for (final Element element7 : this.children()) {
            if (element7.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(element7);
                if (button == 0) {
                    this.setDragging(true);
                }
                return true;
            }
        }
        return false;
    }
    
    default boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        this.setDragging(false);
        return this.hoveredElement(mouseX, mouseY).filter(element6 -> element6.mouseReleased(mouseX, mouseY, button)).isPresent();
    }
    
    default boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return this.getFocused() != null && this.isDragging() && button == 0 && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    boolean isDragging();
    
    void setDragging(final boolean arg1);
    
    default boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return this.hoveredElement(mouseX, mouseY).filter(element7 -> element7.mouseScrolled(mouseX, mouseY, amount)).isPresent();
    }
    
    default boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        return this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers);
    }
    
    default boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        return this.getFocused() != null && this.getFocused().keyReleased(keyCode, scanCode, modifiers);
    }
    
    default boolean charTyped(final char chr, final int keyCode) {
        return this.getFocused() != null && this.getFocused().charTyped(chr, keyCode);
    }
    
    @Nullable
    Element getFocused();
    
    void setFocused(@Nullable final Element arg1);
    
    default void setInitialFocus(@Nullable final Element element) {
        this.setFocused(element);
    }
    
    default void focusOn(@Nullable final Element element) {
        this.setFocused(element);
    }
    
    default boolean changeFocus(final boolean lookForwards) {
        final Element element2 = this.getFocused();
        final boolean boolean3 = element2 != null;
        if (boolean3 && element2.changeFocus(lookForwards)) {
            return true;
        }
        final List<? extends Element> list4 = this.children();
        final int integer6 = list4.indexOf(element2);
        int integer7;
        if (boolean3 && integer6 >= 0) {
            integer7 = integer6 + (lookForwards ? 1 : 0);
        }
        else if (lookForwards) {
            integer7 = 0;
        }
        else {
            integer7 = list4.size();
        }
        final ListIterator<? extends Element> listIterator7 = list4.listIterator(integer7);
        final BooleanSupplier booleanSupplier8 = lookForwards ? listIterator7::hasNext : listIterator7::hasPrevious;
        final Supplier<? extends Element> supplier9 = (lookForwards ? listIterator7::next : listIterator7::previous);
        while (booleanSupplier8.getAsBoolean()) {
            final Element element3 = (Element)supplier9.get();
            if (element3.changeFocus(lookForwards)) {
                this.setFocused(element3);
                return true;
            }
        }
        this.setFocused(null);
        return false;
    }
}
