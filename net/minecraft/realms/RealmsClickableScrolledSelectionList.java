package net.minecraft.realms;

import net.minecraft.client.gui.Element;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class RealmsClickableScrolledSelectionList extends RealmsGuiEventListener
{
    private final RealmsClickableScrolledSelectionListProxy proxy;
    
    public RealmsClickableScrolledSelectionList(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        this.proxy = new RealmsClickableScrolledSelectionListProxy(this, integer1, integer2, integer3, integer4, integer5);
    }
    
    public void render(final int integer1, final int integer2, final float float3) {
        this.proxy.render(integer1, integer2, float3);
    }
    
    public int width() {
        return this.proxy.getWidth();
    }
    
    protected void renderItem(final int integer1, final int integer2, final int integer3, final int integer4, final Tezzelator tezzelator, final int integer6, final int integer7) {
    }
    
    public void renderItem(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        this.renderItem(integer1, integer2, integer3, integer4, Tezzelator.instance, integer5, integer6);
    }
    
    public int getItemCount() {
        return 0;
    }
    
    public boolean selectItem(final int integer1, final int integer2, final double double3, final double double5) {
        return true;
    }
    
    public boolean isSelectedItem(final int integer) {
        return false;
    }
    
    public void renderBackground() {
    }
    
    public int getMaxPosition() {
        return 0;
    }
    
    public int getScrollbarPosition() {
        return this.proxy.getWidth() / 2 + 124;
    }
    
    @Override
    public Element getProxy() {
        return this.proxy;
    }
    
    public void scroll(final int integer) {
        this.proxy.scroll(integer);
    }
    
    public int getScroll() {
        return this.proxy.getScroll();
    }
    
    protected void renderList(final int integer1, final int integer2, final int integer3, final int integer4) {
    }
    
    public void itemClicked(final int integer1, final int integer2, final double double3, final double double5, final int integer7) {
    }
    
    public void renderSelected(final int integer1, final int integer2, final int integer3, final Tezzelator tezzelator) {
    }
    
    public void setLeftPos(final int integer) {
        this.proxy.setLeftPos(integer);
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
    
    public double yo() {
        return this.proxy.yo();
    }
    
    public int itemHeight() {
        return this.proxy.itemHeight();
    }
    
    public boolean isVisible() {
        return this.proxy.isVisible();
    }
}
