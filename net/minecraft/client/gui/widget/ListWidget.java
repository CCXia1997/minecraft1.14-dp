package net.minecraft.client.gui.widget;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.Tessellator;
import java.util.Collections;
import net.minecraft.client.gui.Element;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.AbstractParentElement;

@Environment(EnvType.CLIENT)
public abstract class ListWidget extends AbstractParentElement implements Drawable
{
    protected static final int NO_DRAG = -1;
    protected static final int DRAG_OUTSIDE = -2;
    protected final MinecraftClient minecraft;
    protected int width;
    protected int height;
    protected int y0;
    protected int y1;
    protected int x1;
    protected int x0;
    protected final int itemHeight;
    protected boolean centerListVertically;
    protected int yDrag;
    protected double yo;
    protected boolean visible;
    protected boolean renderSelection;
    protected boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling;
    
    public ListWidget(final MinecraftClient client, final int width, final int height, final int top, final int bottom, final int itemHeight) {
        this.centerListVertically = true;
        this.yDrag = -2;
        this.visible = true;
        this.renderSelection = true;
        this.minecraft = client;
        this.width = width;
        this.height = height;
        this.y0 = top;
        this.y1 = bottom;
        this.itemHeight = itemHeight;
        this.x0 = 0;
        this.x1 = width;
    }
    
    public void updateSize(final int width, final int height, final int y, final int bottom) {
        this.width = width;
        this.height = height;
        this.y0 = y;
        this.y1 = bottom;
        this.x0 = 0;
        this.x1 = width;
    }
    
    public void setRenderSelection(final boolean boolean1) {
        this.renderSelection = boolean1;
    }
    
    protected void setRenderHeader(final boolean boolean1, final int integer) {
        this.renderHeader = boolean1;
        this.headerHeight = integer;
        if (!boolean1) {
            this.headerHeight = 0;
        }
    }
    
    public void setVisible(final boolean boolean1) {
        this.visible = boolean1;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    protected abstract int getItemCount();
    
    @Override
    public List<? extends Element> children() {
        return Collections.emptyList();
    }
    
    protected boolean selectItem(final int index, final int button, final double mouseX, final double mouseY) {
        return true;
    }
    
    protected abstract boolean isSelectedItem(final int arg1);
    
    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }
    
    protected abstract void renderBackground();
    
    protected void updateItemPosition(final int index, final int integer2, final int integer3, final float float4) {
    }
    
    protected abstract void renderItem(final int arg1, final int arg2, final int arg3, final int arg4, final int arg5, final int arg6, final float arg7);
    
    protected void renderHeader(final int integer1, final int integer2, final Tessellator tessellator) {
    }
    
    protected void clickedHeader(final int integer1, final int integer2) {
    }
    
    protected void renderDecorations(final int integer1, final int integer2) {
    }
    
    public int getItemAtPosition(final double double1, final double double3) {
        final int integer5 = this.x0 + this.width / 2 - this.getRowWidth() / 2;
        final int integer6 = this.x0 + this.width / 2 + this.getRowWidth() / 2;
        final int integer7 = MathHelper.floor(double3 - this.y0) - this.headerHeight + (int)this.yo - 4;
        final int integer8 = integer7 / this.itemHeight;
        if (double1 < this.getScrollbarPosition() && double1 >= integer5 && double1 <= integer6 && integer8 >= 0 && integer7 >= 0 && integer8 < this.getItemCount()) {
            return integer8;
        }
        return -1;
    }
    
    protected void capYPosition() {
        this.yo = MathHelper.clamp(this.yo, 0.0, this.getMaxScroll());
    }
    
    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
    }
    
    public void centerScrollOn(final int integer) {
        this.yo = integer * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2;
        this.capYPosition();
    }
    
    public int getScroll() {
        return (int)this.yo;
    }
    
    public boolean isMouseInList(final double mouseX, final double mouseY) {
        return mouseY >= this.y0 && mouseY <= this.y1 && mouseX >= this.x0 && mouseX <= this.x1;
    }
    
    public int getScrollBottom() {
        return (int)this.yo - this.height - this.headerHeight;
    }
    
    public void scroll(final int amount) {
        this.yo += amount;
        this.capYPosition();
        this.yDrag = -2;
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
        this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float float8 = 32.0f;
        bufferBuilder7.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder7.vertex(this.x0, this.y1, 0.0).texture(this.x0 / 32.0f, (this.y1 + (int)this.yo) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder7.vertex(this.x1, this.y1, 0.0).texture(this.x1 / 32.0f, (this.y1 + (int)this.yo) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder7.vertex(this.x1, this.y0, 0.0).texture(this.x1 / 32.0f, (this.y0 + (int)this.yo) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder7.vertex(this.x0, this.y0, 0.0).texture(this.x0 / 32.0f, (this.y0 + (int)this.yo) / 32.0f).color(32, 32, 32, 255).next();
        tessellator6.draw();
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
        final int integer8 = 4;
        bufferBuilder7.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder7.vertex(this.x0, this.y0 + 4, 0.0).texture(0.0, 1.0).color(0, 0, 0, 0).next();
        bufferBuilder7.vertex(this.x1, this.y0 + 4, 0.0).texture(1.0, 1.0).color(0, 0, 0, 0).next();
        bufferBuilder7.vertex(this.x1, this.y0, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
        bufferBuilder7.vertex(this.x0, this.y0, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
        tessellator6.draw();
        bufferBuilder7.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder7.vertex(this.x0, this.y1, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
        bufferBuilder7.vertex(this.x1, this.y1, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
        bufferBuilder7.vertex(this.x1, this.y1 - 4, 0.0).texture(1.0, 0.0).color(0, 0, 0, 0).next();
        bufferBuilder7.vertex(this.x0, this.y1 - 4, 0.0).texture(0.0, 0.0).color(0, 0, 0, 0).next();
        tessellator6.draw();
        final int integer9 = this.getMaxScroll();
        if (integer9 > 0) {
            int integer10 = (int)((this.y1 - this.y0) * (this.y1 - this.y0) / (float)this.getMaxPosition());
            integer10 = MathHelper.clamp(integer10, 32, this.y1 - this.y0 - 8);
            int integer11 = (int)this.yo * (this.y1 - this.y0 - integer10) / integer9 + this.y0;
            if (integer11 < this.y0) {
                integer11 = this.y0;
            }
            bufferBuilder7.begin(7, VertexFormats.POSITION_UV_COLOR);
            bufferBuilder7.vertex(integer4, this.y1, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
            bufferBuilder7.vertex(integer5, this.y1, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
            bufferBuilder7.vertex(integer5, this.y0, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder7.vertex(integer4, this.y0, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
            tessellator6.draw();
            bufferBuilder7.begin(7, VertexFormats.POSITION_UV_COLOR);
            bufferBuilder7.vertex(integer4, integer11 + integer10, 0.0).texture(0.0, 1.0).color(128, 128, 128, 255).next();
            bufferBuilder7.vertex(integer5, integer11 + integer10, 0.0).texture(1.0, 1.0).color(128, 128, 128, 255).next();
            bufferBuilder7.vertex(integer5, integer11, 0.0).texture(1.0, 0.0).color(128, 128, 128, 255).next();
            bufferBuilder7.vertex(integer4, integer11, 0.0).texture(0.0, 0.0).color(128, 128, 128, 255).next();
            tessellator6.draw();
            bufferBuilder7.begin(7, VertexFormats.POSITION_UV_COLOR);
            bufferBuilder7.vertex(integer4, integer11 + integer10 - 1, 0.0).texture(0.0, 1.0).color(192, 192, 192, 255).next();
            bufferBuilder7.vertex(integer5 - 1, integer11 + integer10 - 1, 0.0).texture(1.0, 1.0).color(192, 192, 192, 255).next();
            bufferBuilder7.vertex(integer5 - 1, integer11, 0.0).texture(1.0, 0.0).color(192, 192, 192, 255).next();
            bufferBuilder7.vertex(integer4, integer11, 0.0).texture(0.0, 0.0).color(192, 192, 192, 255).next();
            tessellator6.draw();
        }
        this.renderDecorations(mouseX, mouseY);
        GlStateManager.enableTexture();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlphaTest();
        GlStateManager.disableBlend();
    }
    
    protected void updateScrollingState(final double double1, final double double3, final int integer5) {
        this.scrolling = (integer5 == 0 && double1 >= this.getScrollbarPosition() && double1 < this.getScrollbarPosition() + 6);
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isVisible() || !this.isMouseInList(mouseX, mouseY)) {
            return false;
        }
        final int integer6 = this.getItemAtPosition(mouseX, mouseY);
        if (integer6 == -1 && button == 0) {
            this.clickedHeader((int)(mouseX - (this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(mouseY - this.y0) + (int)this.yo - 4);
            return true;
        }
        if (integer6 != -1 && this.selectItem(integer6, button, mouseX, mouseY)) {
            if (this.children().size() > integer6) {
                this.setFocused((Element)this.children().get(integer6));
            }
            this.setDragging(true);
            return true;
        }
        return this.scrolling;
    }
    
    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        if (!this.isVisible() || button != 0 || !this.scrolling) {
            return false;
        }
        if (mouseY < this.y0) {
            this.yo = 0.0;
        }
        else if (mouseY > this.y1) {
            this.yo = this.getMaxScroll();
        }
        else {
            double double10 = this.getMaxScroll();
            if (double10 < 1.0) {
                double10 = 1.0;
            }
            int integer12 = (int)((this.y1 - this.y0) * (this.y1 - this.y0) / (float)this.getMaxPosition());
            integer12 = MathHelper.clamp(integer12, 32, this.y1 - this.y0 - 8);
            double double11 = double10 / (this.y1 - this.y0 - integer12);
            if (double11 < 1.0) {
                double11 = 1.0;
            }
            this.yo += deltaY * double11;
            this.capYPosition();
        }
        return true;
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        if (!this.isVisible()) {
            return false;
        }
        this.yo -= amount * this.itemHeight / 2.0;
        return true;
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (!this.isVisible()) {
            return false;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 264) {
            this.moveSelection(1);
            return true;
        }
        if (keyCode == 265) {
            this.moveSelection(-1);
            return true;
        }
        return false;
    }
    
    protected void moveSelection(final int integer) {
    }
    
    @Override
    public boolean charTyped(final char chr, final int keyCode) {
        return this.isVisible() && super.charTyped(chr, keyCode);
    }
    
    @Override
    public boolean isMouseOver(final double mouseX, final double mouseY) {
        return this.isMouseInList(mouseX, mouseY);
    }
    
    public int getRowWidth() {
        return 220;
    }
    
    protected void renderList(final int x, final int y, final int mouseX, final int mouseY, final float float5) {
        final int integer6 = this.getItemCount();
        final Tessellator tessellator7 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder8 = tessellator7.getBufferBuilder();
        for (int integer7 = 0; integer7 < integer6; ++integer7) {
            final int integer8 = y + integer7 * this.itemHeight + this.headerHeight;
            final int integer9 = this.itemHeight - 4;
            if (integer8 > this.y1 || integer8 + integer9 < this.y0) {
                this.updateItemPosition(integer7, x, integer8, float5);
            }
            if (this.renderSelection && this.isSelectedItem(integer7)) {
                final int integer10 = this.x0 + this.width / 2 - this.getRowWidth() / 2;
                final int integer11 = this.x0 + this.width / 2 + this.getRowWidth() / 2;
                GlStateManager.disableTexture();
                final float float6 = this.isFocused() ? 1.0f : 0.5f;
                GlStateManager.color4f(float6, float6, float6, 1.0f);
                bufferBuilder8.begin(7, VertexFormats.POSITION);
                bufferBuilder8.vertex(integer10, integer8 + integer9 + 2, 0.0).next();
                bufferBuilder8.vertex(integer11, integer8 + integer9 + 2, 0.0).next();
                bufferBuilder8.vertex(integer11, integer8 - 2, 0.0).next();
                bufferBuilder8.vertex(integer10, integer8 - 2, 0.0).next();
                tessellator7.draw();
                GlStateManager.color4f(0.0f, 0.0f, 0.0f, 1.0f);
                bufferBuilder8.begin(7, VertexFormats.POSITION);
                bufferBuilder8.vertex(integer10 + 1, integer8 + integer9 + 1, 0.0).next();
                bufferBuilder8.vertex(integer11 - 1, integer8 + integer9 + 1, 0.0).next();
                bufferBuilder8.vertex(integer11 - 1, integer8 - 1, 0.0).next();
                bufferBuilder8.vertex(integer10 + 1, integer8 - 1, 0.0).next();
                tessellator7.draw();
                GlStateManager.enableTexture();
            }
            this.renderItem(integer7, x, integer8, integer9, mouseX, mouseY, float5);
        }
    }
    
    protected boolean isFocused() {
        return false;
    }
    
    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }
    
    protected void renderHoleBackground(final int integer1, final int integer2, final int integer3, final int integer4) {
        final Tessellator tessellator5 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder6 = tessellator5.getBufferBuilder();
        this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float float7 = 32.0f;
        bufferBuilder6.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder6.vertex(this.x0, integer2, 0.0).texture(0.0, integer2 / 32.0f).color(64, 64, 64, integer4).next();
        bufferBuilder6.vertex(this.x0 + this.width, integer2, 0.0).texture(this.width / 32.0f, integer2 / 32.0f).color(64, 64, 64, integer4).next();
        bufferBuilder6.vertex(this.x0 + this.width, integer1, 0.0).texture(this.width / 32.0f, integer1 / 32.0f).color(64, 64, 64, integer3).next();
        bufferBuilder6.vertex(this.x0, integer1, 0.0).texture(0.0, integer1 / 32.0f).color(64, 64, 64, integer3).next();
        tessellator5.draw();
    }
    
    public void setLeftPos(final int x) {
        this.x0 = x;
        this.x1 = x + this.width;
    }
    
    public int getItemHeight() {
        return this.itemHeight;
    }
}
