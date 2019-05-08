package net.minecraft.realms;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Element;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class RealmsObjectSelectionList<E extends RealmListEntry> extends RealmsGuiEventListener
{
    private final RealmsObjectSelectionListProxy proxy;
    
    public RealmsObjectSelectionList(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        this.proxy = new RealmsObjectSelectionListProxy(this, integer1, integer2, integer3, integer4, integer5);
    }
    
    public void render(final int integer1, final int integer2, final float float3) {
        this.proxy.render(integer1, integer2, float3);
    }
    
    public void addEntry(final E realmListEntry) {
        this.proxy.addEntry(realmListEntry);
    }
    
    public void remove(final int integer) {
        this.proxy.remove(integer);
    }
    
    public void clear() {
        this.proxy.clear();
    }
    
    public boolean removeEntry(final E realmListEntry) {
        return this.proxy.removeEntry(realmListEntry);
    }
    
    public int width() {
        return this.proxy.getWidth();
    }
    
    protected void renderItem(final int integer1, final int integer2, final int integer3, final int integer4, final Tezzelator tezzelator, final int integer6, final int integer7) {
    }
    
    public void setLeftPos(final int integer) {
        this.proxy.setLeftPos(integer);
    }
    
    public void renderItem(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        this.renderItem(integer1, integer2, integer3, integer4, Tezzelator.instance, integer5, integer6);
    }
    
    public void setSelected(final int integer) {
        this.proxy.setSelectedItem(integer);
    }
    
    public void itemClicked(final int integer1, final int integer2, final double double3, final double double5, final int integer7) {
    }
    
    public int getItemCount() {
        return this.proxy.getItemCount();
    }
    
    public void renderBackground() {
    }
    
    public int getMaxPosition() {
        return 0;
    }
    
    public int getScrollbarPosition() {
        return this.proxy.getRowLeft() + this.proxy.getRowWidth();
    }
    
    public int y0() {
        return this.proxy.y0();
    }
    
    public int y1() {
        return this.proxy.y1();
    }
    
    public int headerHeight() {
        return this.proxy.headerHeight();
    }
    
    public int itemHeight() {
        return this.proxy.itemHeight();
    }
    
    public void scroll(final int integer) {
        this.proxy.setScrollAmount(integer);
    }
    
    public int getScroll() {
        return (int)this.proxy.getScrollAmount();
    }
    
    @Override
    public Element getProxy() {
        return this.proxy;
    }
    
    public int getRowWidth() {
        return (int)(this.width() * 0.6);
    }
    
    public abstract boolean isFocused();
    
    public void selectItem(final int integer) {
        this.setSelected(integer);
    }
    
    @Nullable
    public E getSelected() {
        return (E)this.proxy.getSelected();
    }
    
    public List<E> children() {
        return (List<E>)this.proxy.children();
    }
    
    public void replaceEntries(final Collection<E> collection) {
        this.proxy.replaceEntries(collection);
    }
    
    public int getRowTop(final int integer) {
        return this.proxy.getRowTop(integer);
    }
    
    public int getRowLeft() {
        return this.proxy.getRowLeft();
    }
}
