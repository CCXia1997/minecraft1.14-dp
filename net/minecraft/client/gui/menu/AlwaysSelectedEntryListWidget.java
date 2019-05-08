package net.minecraft.client.gui.menu;

import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.EntryListWidget;

@Environment(EnvType.CLIENT)
public abstract class AlwaysSelectedEntryListWidget<E extends EntryListWidget.Entry<E>> extends EntryListWidget<E>
{
    private boolean inFocus;
    
    public AlwaysSelectedEntryListWidget(final MinecraftClient client, final int width, final int height, final int top, final int bottom, final int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
    }
    
    @Override
    public boolean changeFocus(final boolean lookForwards) {
        if (!this.inFocus && this.getItemCount() == 0) {
            return false;
        }
        this.inFocus = !this.inFocus;
        if (this.inFocus && this.getSelected() == null && this.getItemCount() > 0) {
            this.moveSelection(1);
        }
        else if (this.inFocus && this.getSelected() != null) {
            this.moveSelection(0);
        }
        return this.inFocus;
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class Entry<E extends Entry<E>> extends EntryListWidget.Entry<E>
    {
        @Override
        public boolean changeFocus(final boolean lookForwards) {
            return false;
        }
    }
}
