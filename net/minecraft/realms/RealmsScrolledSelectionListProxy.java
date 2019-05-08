package net.minecraft.realms;

import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ListWidget;

@Environment(EnvType.CLIENT)
public class RealmsScrolledSelectionListProxy extends ListWidget
{
    private final RealmsScrolledSelectionList realmsScrolledSelectionList;
    
    public RealmsScrolledSelectionListProxy(final RealmsScrolledSelectionList realmsScrolledSelectionList, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        super(MinecraftClient.getInstance(), integer2, integer3, integer4, integer5, integer6);
        this.realmsScrolledSelectionList = realmsScrolledSelectionList;
    }
    
    public int getItemCount() {
        return this.realmsScrolledSelectionList.getItemCount();
    }
    
    public boolean selectItem(final int index, final int button, final double mouseX, final double mouseY) {
        return this.realmsScrolledSelectionList.selectItem(index, button, mouseX, mouseY);
    }
    
    public boolean isSelectedItem(final int index) {
        return this.realmsScrolledSelectionList.isSelectedItem(index);
    }
    
    public void renderBackground() {
        this.realmsScrolledSelectionList.renderBackground();
    }
    
    public void renderItem(final int index, final int y, final int integer3, final int integer4, final int integer5, final int integer6, final float float7) {
        this.realmsScrolledSelectionList.renderItem(index, y, integer3, integer4, integer5, integer6);
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getMaxPosition() {
        return this.realmsScrolledSelectionList.getMaxPosition();
    }
    
    public int getScrollbarPosition() {
        return this.realmsScrolledSelectionList.getScrollbarPosition();
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return this.realmsScrolledSelectionList.mouseScrolled(mouseX, mouseY, amount) || super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return this.realmsScrolledSelectionList.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return this.realmsScrolledSelectionList.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return this.realmsScrolledSelectionList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
