package net.minecraft.realms;

import net.minecraft.client.gui.Element;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class RealmsSimpleScrolledSelectionList extends RealmsGuiEventListener
{
    private final RealmsSimpleScrolledSelectionListProxy proxy;
    
    public RealmsSimpleScrolledSelectionList(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        this.proxy = new RealmsSimpleScrolledSelectionListProxy(this, integer1, integer2, integer3, integer4, integer5);
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
}
