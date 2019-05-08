package net.minecraft.realms;

import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ListWidget;

@Environment(EnvType.CLIENT)
public class RealmsClickableScrolledSelectionListProxy extends ListWidget
{
    private final RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList;
    
    public RealmsClickableScrolledSelectionListProxy(final RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        super(MinecraftClient.getInstance(), integer2, integer3, integer4, integer5, integer6);
        this.realmsClickableScrolledSelectionList = realmsClickableScrolledSelectionList;
    }
    
    public int getItemCount() {
        return this.realmsClickableScrolledSelectionList.getItemCount();
    }
    
    public boolean selectItem(final int index, final int button, final double mouseX, final double mouseY) {
        return this.realmsClickableScrolledSelectionList.selectItem(index, button, mouseX, mouseY);
    }
    
    public boolean isSelectedItem(final int index) {
        return this.realmsClickableScrolledSelectionList.isSelectedItem(index);
    }
    
    public void renderBackground() {
        this.realmsClickableScrolledSelectionList.renderBackground();
    }
    
    public void renderItem(final int index, final int y, final int integer3, final int integer4, final int integer5, final int integer6, final float float7) {
        this.realmsClickableScrolledSelectionList.renderItem(index, y, integer3, integer4, integer5, integer6);
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getMaxPosition() {
        return this.realmsClickableScrolledSelectionList.getMaxPosition();
    }
    
    public int getScrollbarPosition() {
        return this.realmsClickableScrolledSelectionList.getScrollbarPosition();
    }
    
    public void itemClicked(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        this.realmsClickableScrolledSelectionList.itemClicked(integer1, integer2, integer3, integer4, integer5);
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return this.realmsClickableScrolledSelectionList.mouseScrolled(mouseX, mouseY, amount) || super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return this.realmsClickableScrolledSelectionList.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return this.realmsClickableScrolledSelectionList.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return this.realmsClickableScrolledSelectionList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    public void renderSelected(final int integer1, final int integer2, final int integer3, final Tezzelator tezzelator) {
        this.realmsClickableScrolledSelectionList.renderSelected(integer1, integer2, integer3, tezzelator);
    }
    
    public void renderList(final int x, final int y, final int mouseX, final int mouseY, final float float5) {
        for (int integer6 = this.getItemCount(), integer7 = 0; integer7 < integer6; ++integer7) {
            final int integer8 = y + integer7 * this.itemHeight + this.headerHeight;
            final int integer9 = this.itemHeight - 4;
            if (integer8 > this.y1 || integer8 + integer9 < this.y0) {
                this.updateItemPosition(integer7, x, integer8, float5);
            }
            if (this.renderSelection && this.isSelectedItem(integer7)) {
                this.renderSelected(this.width, integer8, integer9, Tezzelator.instance);
            }
            this.renderItem(integer7, x, integer8, integer9, mouseX, mouseY, float5);
        }
    }
    
    public int y0() {
        return this.y0;
    }
    
    public int y1() {
        return this.y1;
    }
    
    public int headerHeight() {
        return this.headerHeight;
    }
    
    public double yo() {
        return this.yo;
    }
    
    public int itemHeight() {
        return this.itemHeight;
    }
}
