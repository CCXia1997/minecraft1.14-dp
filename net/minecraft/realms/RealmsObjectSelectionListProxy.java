package net.minecraft.realms;

import net.minecraft.client.gui.widget.EntryListWidget;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.menu.AlwaysSelectedEntryListWidget;

@Environment(EnvType.CLIENT)
public class RealmsObjectSelectionListProxy<E extends Entry<E>> extends AlwaysSelectedEntryListWidget<E>
{
    private final RealmsObjectSelectionList realmsObjectSelectionList;
    
    public RealmsObjectSelectionListProxy(final RealmsObjectSelectionList realmsObjectSelectionList, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        super(MinecraftClient.getInstance(), integer2, integer3, integer4, integer5, integer6);
        this.realmsObjectSelectionList = realmsObjectSelectionList;
    }
    
    public int getItemCount() {
        return super.getItemCount();
    }
    
    public void clear() {
        super.clearEntries();
    }
    
    public boolean isFocused() {
        return this.realmsObjectSelectionList.isFocused();
    }
    
    protected void setSelectedItem(final int integer) {
        if (integer == -1) {
            super.setSelected(null);
        }
        else if (super.getItemCount() != 0) {
            final E entry2 = super.getEntry(integer);
            super.setSelected(entry2);
        }
    }
    
    @Override
    public void setSelected(@Nullable final E entry) {
        super.setSelected(entry);
        this.realmsObjectSelectionList.selectItem(super.children().indexOf(entry));
    }
    
    public void renderBackground() {
        this.realmsObjectSelectionList.renderBackground();
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getMaxPosition() {
        return this.realmsObjectSelectionList.getMaxPosition();
    }
    
    public int getScrollbarPosition() {
        return this.realmsObjectSelectionList.getScrollbarPosition();
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return this.realmsObjectSelectionList.mouseScrolled(mouseX, mouseY, amount) || super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Override
    public int getRowWidth() {
        return this.realmsObjectSelectionList.getRowWidth();
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return this.realmsObjectSelectionList.mouseClicked(mouseX, mouseY, button) || EntryListWidget.this.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return this.realmsObjectSelectionList.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return this.realmsObjectSelectionList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    protected final int addEntry(final E entry) {
        return super.addEntry(entry);
    }
    
    public E remove(final int index) {
        return super.remove(index);
    }
    
    public boolean removeEntry(final E entry) {
        return super.removeEntry(entry);
    }
    
    @Override
    public void setScrollAmount(final double amount) {
        super.setScrollAmount(amount);
    }
    
    public int y0() {
        return this.top;
    }
    
    public int y1() {
        return this.bottom;
    }
    
    public int headerHeight() {
        return this.headerHeight;
    }
    
    public int itemHeight() {
        return this.itemHeight;
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers) || this.realmsObjectSelectionList.keyPressed(keyCode, scanCode, modifiers);
    }
    
    public void replaceEntries(final Collection<E> newEntries) {
        super.replaceEntries(newEntries);
    }
    
    public int getRowTop(final int index) {
        return super.getRowTop(index);
    }
    
    public int getRowLeft() {
        return super.getRowLeft();
    }
}
