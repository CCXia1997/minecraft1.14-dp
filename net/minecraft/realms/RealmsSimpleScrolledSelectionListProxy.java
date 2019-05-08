package net.minecraft.realms;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ListWidget;

@Environment(EnvType.CLIENT)
public class RealmsSimpleScrolledSelectionListProxy extends ListWidget
{
    private final RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList;
    
    public RealmsSimpleScrolledSelectionListProxy(final RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        super(MinecraftClient.getInstance(), integer2, integer3, integer4, integer5, integer6);
        this.realmsSimpleScrolledSelectionList = realmsSimpleScrolledSelectionList;
    }
    
    public int getItemCount() {
        return this.realmsSimpleScrolledSelectionList.getItemCount();
    }
    
    public boolean selectItem(final int index, final int button, final double mouseX, final double mouseY) {
        return this.realmsSimpleScrolledSelectionList.selectItem(index, button, mouseX, mouseY);
    }
    
    public boolean isSelectedItem(final int index) {
        return this.realmsSimpleScrolledSelectionList.isSelectedItem(index);
    }
    
    public void renderBackground() {
        this.realmsSimpleScrolledSelectionList.renderBackground();
    }
    
    public void renderItem(final int index, final int y, final int integer3, final int integer4, final int integer5, final int integer6, final float float7) {
        this.realmsSimpleScrolledSelectionList.renderItem(index, y, integer3, integer4, integer5, integer6);
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getMaxPosition() {
        return this.realmsSimpleScrolledSelectionList.getMaxPosition();
    }
    
    public int getScrollbarPosition() {
        return this.realmsSimpleScrolledSelectionList.getScrollbarPosition();
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        if (!this.visible) {
            return;
        }
        this.renderBackground();
        final int integer4 = this.getScrollbarPosition();
        final int integer5 = integer4 + 6;
        this.capYPosition();
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        final Tessellator tessellator6 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder7 = tessellator6.getBufferBuilder();
        final int integer6 = this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
        final int integer7 = this.y0 + 4 - (int)this.yo;
        if (this.renderHeader) {
            this.renderHeader(integer6, integer7, tessellator6);
        }
        this.renderList(integer6, integer7, mouseX, mouseY, delta);
        GlStateManager.disableDepthTest();
        this.renderHoleBackground(0, this.y0, 255, 255);
        this.renderHoleBackground(this.y1, this.height, 255, 255);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlphaTest();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture();
        final int integer8 = this.getMaxScroll();
        if (integer8 > 0) {
            int integer9 = (this.y1 - this.y0) * (this.y1 - this.y0) / this.getMaxPosition();
            integer9 = MathHelper.clamp(integer9, 32, this.y1 - this.y0 - 8);
            int integer10 = (int)this.yo * (this.y1 - this.y0 - integer9) / integer8 + this.y0;
            if (integer10 < this.y0) {
                integer10 = this.y0;
            }
            bufferBuilder7.begin(7, VertexFormats.POSITION_UV_COLOR);
            bufferBuilder7.vertex(integer4, this.y1, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
            bufferBuilder7.vertex(integer5, this.y1, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
            bufferBuilder7.vertex(integer5, this.y0, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder7.vertex(integer4, this.y0, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
            tessellator6.draw();
            bufferBuilder7.begin(7, VertexFormats.POSITION_UV_COLOR);
            bufferBuilder7.vertex(integer4, integer10 + integer9, 0.0).texture(0.0, 1.0).color(128, 128, 128, 255).next();
            bufferBuilder7.vertex(integer5, integer10 + integer9, 0.0).texture(1.0, 1.0).color(128, 128, 128, 255).next();
            bufferBuilder7.vertex(integer5, integer10, 0.0).texture(1.0, 0.0).color(128, 128, 128, 255).next();
            bufferBuilder7.vertex(integer4, integer10, 0.0).texture(0.0, 0.0).color(128, 128, 128, 255).next();
            tessellator6.draw();
            bufferBuilder7.begin(7, VertexFormats.POSITION_UV_COLOR);
            bufferBuilder7.vertex(integer4, integer10 + integer9 - 1, 0.0).texture(0.0, 1.0).color(192, 192, 192, 255).next();
            bufferBuilder7.vertex(integer5 - 1, integer10 + integer9 - 1, 0.0).texture(1.0, 1.0).color(192, 192, 192, 255).next();
            bufferBuilder7.vertex(integer5 - 1, integer10, 0.0).texture(1.0, 0.0).color(192, 192, 192, 255).next();
            bufferBuilder7.vertex(integer4, integer10, 0.0).texture(0.0, 0.0).color(192, 192, 192, 255).next();
            tessellator6.draw();
        }
        this.renderDecorations(mouseX, mouseY);
        GlStateManager.enableTexture();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlphaTest();
        GlStateManager.disableBlend();
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return this.realmsSimpleScrolledSelectionList.mouseScrolled(mouseX, mouseY, amount) || super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return this.realmsSimpleScrolledSelectionList.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return this.realmsSimpleScrolledSelectionList.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        return this.realmsSimpleScrolledSelectionList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
