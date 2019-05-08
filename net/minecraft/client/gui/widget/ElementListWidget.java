package net.minecraft.client.gui.widget;

import javax.annotation.Nullable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class ElementListWidget<E extends Entry<E>> extends EntryListWidget<E>
{
    public ElementListWidget(final MinecraftClient client, final int width, final int height, final int top, final int bottom, final int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
    }
    
    @Override
    public boolean changeFocus(final boolean lookForwards) {
        final boolean boolean2 = super.changeFocus(lookForwards);
        if (boolean2) {
            this.ensureVisible(this.getFocused());
        }
        return boolean2;
    }
    
    @Override
    protected boolean isSelectedItem(final int index) {
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class Entry<E extends Entry<E>> extends EntryListWidget.Entry<E> implements ParentElement
    {
        @Nullable
        private Element focused;
        private boolean dragging;
        
        @Override
        public boolean isDragging() {
            return this.dragging;
        }
        
        @Override
        public void setDragging(final boolean dragging) {
            this.dragging = dragging;
        }
        
        @Override
        public void setFocused(@Nullable final Element focused) {
            this.focused = focused;
        }
        
        @Nullable
        @Override
        public Element getFocused() {
            return this.focused;
        }
    }
}
