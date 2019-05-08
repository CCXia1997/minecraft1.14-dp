package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.math.MathHelper;
import java.util.Objects;
import java.util.Collection;
import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.AbstractParentElement;

@Environment(EnvType.CLIENT)
public abstract class EntryListWidget<E extends Entry<E>> extends AbstractParentElement implements Drawable
{
    protected static final int DRAG_OUTSIDE = -2;
    protected final MinecraftClient minecraft;
    protected final int itemHeight;
    private final List<E> children;
    protected int width;
    protected int height;
    protected int top;
    protected int bottom;
    protected int right;
    protected int left;
    protected boolean centerListVertically;
    protected int yDrag;
    private double scrollAmount;
    protected boolean renderSelection;
    protected boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling;
    private E selected;
    
    public EntryListWidget(final MinecraftClient client, final int width, final int height, final int top, final int bottom, final int itemHeight) {
        this.children = new Entries();
        this.centerListVertically = true;
        this.yDrag = -2;
        this.renderSelection = true;
        this.minecraft = client;
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.itemHeight = itemHeight;
        this.left = 0;
        this.right = width;
    }
    
    public void setRenderSelection(final boolean renderSelection) {
        this.renderSelection = renderSelection;
    }
    
    protected void setRenderHeader(final boolean renderHeader, final int headerHeight) {
        this.renderHeader = renderHeader;
        this.headerHeight = headerHeight;
        if (!renderHeader) {
            this.headerHeight = 0;
        }
    }
    
    public int getRowWidth() {
        return 220;
    }
    
    @Nullable
    public E getSelected() {
        return this.selected;
    }
    
    public void setSelected(@Nullable final E entry) {
        this.selected = entry;
    }
    
    @Nullable
    @Override
    public E getFocused() {
        return (E)super.getFocused();
    }
    
    @Override
    public final List<E> children() {
        return this.children;
    }
    
    protected final void clearEntries() {
        this.children.clear();
    }
    
    protected void replaceEntries(final Collection<E> newEntries) {
        this.children.clear();
        this.children.addAll(newEntries);
    }
    
    protected E getEntry(final int index) {
        return this.children().get(index);
    }
    
    protected int addEntry(final E entry) {
        this.children.add(entry);
        return this.children.size() - 1;
    }
    
    protected int getItemCount() {
        return this.children().size();
    }
    
    protected boolean isSelectedItem(final int index) {
        return Objects.equals(this.getSelected(), this.children().get(index));
    }
    
    @Nullable
    protected final E getEntryAtPosition(final double x, final double y) {
        final int integer5 = this.getRowWidth() / 2;
        final int integer6 = this.left + this.width / 2;
        final int integer7 = integer6 - integer5;
        final int integer8 = integer6 + integer5;
        final int integer9 = MathHelper.floor(y - this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
        final int integer10 = integer9 / this.itemHeight;
        if (x < this.getScrollbarPosition() && x >= integer7 && x <= integer8 && integer10 >= 0 && integer9 >= 0 && integer10 < this.getItemCount()) {
            return this.children().get(integer10);
        }
        return null;
    }
    
    public void updateSize(final int width, final int height, final int top, final int bottom) {
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.left = 0;
        this.right = width;
    }
    
    public void setLeftPos(final int left) {
        this.left = left;
        this.right = left + this.width;
    }
    
    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }
    
    protected void clickedHeader(final int x, final int y) {
    }
    
    protected void renderHeader(final int x, final int y, final Tessellator tessellator) {
    }
    
    protected void renderBackground() {
    }
    
    protected void renderDecorations(final int mouseX, final int mouseY) {
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        final int integer4 = this.getScrollbarPosition();
        final int integer5 = integer4 + 6;
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        final Tessellator tessellator6 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder7 = tessellator6.getBufferBuilder();
        this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float float8 = 32.0f;
        bufferBuilder7.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder7.vertex(this.left, this.bottom, 0.0).texture(this.left / 32.0f, (this.bottom + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder7.vertex(this.right, this.bottom, 0.0).texture(this.right / 32.0f, (this.bottom + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder7.vertex(this.right, this.top, 0.0).texture(this.right / 32.0f, (this.top + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder7.vertex(this.left, this.top, 0.0).texture(this.left / 32.0f, (this.top + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        tessellator6.draw();
        final int integer6 = this.getRowLeft();
        final int integer7 = this.top + 4 - (int)this.getScrollAmount();
        if (this.renderHeader) {
            this.renderHeader(integer6, integer7, tessellator6);
        }
        this.renderList(integer6, integer7, mouseX, mouseY, delta);
        GlStateManager.disableDepthTest();
        this.renderHoleBackground(0, this.top, 255, 255);
        this.renderHoleBackground(this.bottom, this.height, 255, 255);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlphaTest();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture();
        final int integer8 = 4;
        bufferBuilder7.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder7.vertex(this.left, this.top + 4, 0.0).texture(0.0, 1.0).color(0, 0, 0, 0).next();
        bufferBuilder7.vertex(this.right, this.top + 4, 0.0).texture(1.0, 1.0).color(0, 0, 0, 0).next();
        bufferBuilder7.vertex(this.right, this.top, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
        bufferBuilder7.vertex(this.left, this.top, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
        tessellator6.draw();
        bufferBuilder7.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder7.vertex(this.left, this.bottom, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
        bufferBuilder7.vertex(this.right, this.bottom, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
        bufferBuilder7.vertex(this.right, this.bottom - 4, 0.0).texture(1.0, 0.0).color(0, 0, 0, 0).next();
        bufferBuilder7.vertex(this.left, this.bottom - 4, 0.0).texture(0.0, 0.0).color(0, 0, 0, 0).next();
        tessellator6.draw();
        final int integer9 = this.getMaxScroll();
        if (integer9 > 0) {
            int integer10 = (int)((this.bottom - this.top) * (this.bottom - this.top) / (float)this.getMaxPosition());
            integer10 = MathHelper.clamp(integer10, 32, this.bottom - this.top - 8);
            int integer11 = (int)this.getScrollAmount() * (this.bottom - this.top - integer10) / integer9 + this.top;
            if (integer11 < this.top) {
                integer11 = this.top;
            }
            bufferBuilder7.begin(7, VertexFormats.POSITION_UV_COLOR);
            bufferBuilder7.vertex(integer4, this.bottom, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
            bufferBuilder7.vertex(integer5, this.bottom, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
            bufferBuilder7.vertex(integer5, this.top, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder7.vertex(integer4, this.top, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
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
    
    protected void centerScrollOn(final E entry) {
        this.setScrollAmount(this.children().indexOf(entry) * this.itemHeight + this.itemHeight / 2 - (this.bottom - this.top) / 2);
    }
    
    protected void ensureVisible(final E entry) {
        final int integer2 = this.getRowTop(this.children().indexOf(entry));
        final int integer3 = integer2 - this.top - 4 - this.itemHeight;
        if (integer3 < 0) {
            this.scroll(integer3);
        }
        final int integer4 = this.bottom - integer2 - this.itemHeight - this.itemHeight;
        if (integer4 < 0) {
            this.scroll(-integer4);
        }
    }
    
    private void scroll(final int amount) {
        this.setScrollAmount(this.getScrollAmount() + amount);
        this.yDrag = -2;
    }
    
    public double getScrollAmount() {
        return this.scrollAmount;
    }
    
    public void setScrollAmount(final double amount) {
        this.scrollAmount = MathHelper.clamp(amount, 0.0, this.getMaxScroll());
    }
    
    private int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
    }
    
    public int getScrollBottom() {
        return (int)this.getScrollAmount() - this.height - this.headerHeight;
    }
    
    protected void updateScrollingState(final double double1, final double double3, final int integer5) {
        this.scrolling = (integer5 == 0 && double1 >= this.getScrollbarPosition() && double1 < this.getScrollbarPosition() + 6);
    }
    
    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        final E entry6 = this.getEntryAtPosition(mouseX, mouseY);
        if (entry6 != null) {
            if (entry6.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(entry6);
                this.setDragging(true);
                return true;
            }
        }
        else if (button == 0) {
            this.clickedHeader((int)(mouseX - (this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(mouseY - this.top) + (int)this.getScrollAmount() - 4);
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
        if (button != 0 || !this.scrolling) {
            return false;
        }
        if (mouseY < this.top) {
            this.setScrollAmount(0.0);
        }
        else if (mouseY > this.bottom) {
            this.setScrollAmount(this.getMaxScroll());
        }
        else {
            final double double10 = Math.max(1, this.getMaxScroll());
            final int integer12 = this.bottom - this.top;
            final int integer13 = MathHelper.clamp((int)(integer12 * integer12 / (float)this.getMaxPosition()), 32, integer12 - 8);
            final double double11 = Math.max(1.0, double10 / (integer12 - integer13));
            this.setScrollAmount(this.getScrollAmount() + deltaY * double11);
        }
        return true;
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        this.setScrollAmount(this.getScrollAmount() - amount * this.itemHeight / 2.0);
        return true;
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
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
    
    protected void moveSelection(final int amount) {
        if (!this.children().isEmpty()) {
            final int integer2 = this.children().indexOf(this.getSelected());
            final int integer3 = MathHelper.clamp(integer2 + amount, 0, this.getItemCount() - 1);
            final E entry4 = this.children().get(integer3);
            this.setSelected(entry4);
            this.ensureVisible(entry4);
        }
    }
    
    @Override
    public boolean isMouseOver(final double mouseX, final double mouseY) {
        return mouseY >= this.top && mouseY <= this.bottom && mouseX >= this.left && mouseX <= this.right;
    }
    
    protected void renderList(final int x, final int y, final int mouseX, final int mouseY, final float delta) {
        final int integer6 = this.getItemCount();
        final Tessellator tessellator7 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder8 = tessellator7.getBufferBuilder();
        for (int integer7 = 0; integer7 < integer6; ++integer7) {
            final int integer8 = this.getRowTop(integer7);
            final int integer9 = this.getRowBottom(integer7);
            if (integer9 >= this.top) {
                if (integer8 <= this.bottom) {
                    final int integer10 = y + integer7 * this.itemHeight + this.headerHeight;
                    final int integer11 = this.itemHeight - 4;
                    final E entry14 = this.getEntry(integer7);
                    final int integer12 = this.getRowWidth();
                    if (this.renderSelection && this.isSelectedItem(integer7)) {
                        final int integer13 = this.left + this.width / 2 - integer12 / 2;
                        final int integer14 = this.left + this.width / 2 + integer12 / 2;
                        GlStateManager.disableTexture();
                        final float float18 = this.isFocused() ? 1.0f : 0.5f;
                        GlStateManager.color4f(float18, float18, float18, 1.0f);
                        bufferBuilder8.begin(7, VertexFormats.POSITION);
                        bufferBuilder8.vertex(integer13, integer10 + integer11 + 2, 0.0).next();
                        bufferBuilder8.vertex(integer14, integer10 + integer11 + 2, 0.0).next();
                        bufferBuilder8.vertex(integer14, integer10 - 2, 0.0).next();
                        bufferBuilder8.vertex(integer13, integer10 - 2, 0.0).next();
                        tessellator7.draw();
                        GlStateManager.color4f(0.0f, 0.0f, 0.0f, 1.0f);
                        bufferBuilder8.begin(7, VertexFormats.POSITION);
                        bufferBuilder8.vertex(integer13 + 1, integer10 + integer11 + 1, 0.0).next();
                        bufferBuilder8.vertex(integer14 - 1, integer10 + integer11 + 1, 0.0).next();
                        bufferBuilder8.vertex(integer14 - 1, integer10 - 1, 0.0).next();
                        bufferBuilder8.vertex(integer13 + 1, integer10 - 1, 0.0).next();
                        tessellator7.draw();
                        GlStateManager.enableTexture();
                    }
                    final int integer13 = this.getRowLeft();
                    entry14.render(integer7, integer8, integer13, integer12, integer11, mouseX, mouseY, this.isMouseOver(mouseX, mouseY) && Objects.equals(this.getEntryAtPosition(mouseX, mouseY), entry14), delta);
                }
            }
        }
    }
    
    protected int getRowLeft() {
        return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
    }
    
    protected int getRowTop(final int index) {
        return this.top + 4 - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
    }
    
    private int getRowBottom(final int integer) {
        return this.getRowTop(integer) + this.itemHeight;
    }
    
    protected boolean isFocused() {
        return false;
    }
    
    protected void renderHoleBackground(final int top, final int bottom, final int alphaTop, final int alphaBottom) {
        final Tessellator tessellator5 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder6 = tessellator5.getBufferBuilder();
        this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float float7 = 32.0f;
        bufferBuilder6.begin(7, VertexFormats.POSITION_UV_COLOR);
        bufferBuilder6.vertex(this.left, bottom, 0.0).texture(0.0, bottom / 32.0f).color(64, 64, 64, alphaBottom).next();
        bufferBuilder6.vertex(this.left + this.width, bottom, 0.0).texture(this.width / 32.0f, bottom / 32.0f).color(64, 64, 64, alphaBottom).next();
        bufferBuilder6.vertex(this.left + this.width, top, 0.0).texture(this.width / 32.0f, top / 32.0f).color(64, 64, 64, alphaTop).next();
        bufferBuilder6.vertex(this.left, top, 0.0).texture(0.0, top / 32.0f).color(64, 64, 64, alphaTop).next();
        tessellator5.draw();
    }
    
    protected E remove(final int index) {
        final E entry2 = this.children.get(index);
        if (this.removeEntry(this.children.get(index))) {
            return entry2;
        }
        return null;
    }
    
    protected boolean removeEntry(final E entry) {
        final boolean boolean2 = this.children.remove(entry);
        if (boolean2 && entry == this.getSelected()) {
            this.setSelected(null);
        }
        return boolean2;
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class Entry<E extends Entry<E>> implements Element
    {
        @Deprecated
        EntryListWidget<E> list;
        
        public abstract void render(final int arg1, final int arg2, final int arg3, final int arg4, final int arg5, final int arg6, final int arg7, final boolean arg8, final float arg9);
        
        @Override
        public boolean isMouseOver(final double mouseX, final double mouseY) {
            return Objects.equals(this.list.getEntryAtPosition(mouseX, mouseY), this);
        }
    }
    
    @Environment(EnvType.CLIENT)
    class Entries extends AbstractList<E>
    {
        private final List<E> entries;
        
        private Entries() {
            this.entries = Lists.newArrayList();
        }
        
        public E a(final int index) {
            return this.entries.get(index);
        }
        
        @Override
        public int size() {
            return this.entries.size();
        }
        
        public E a(final int index, final E entry) {
            final E entry2 = this.entries.set(index, entry);
            entry.list = (EntryListWidget<E>)EntryListWidget.this;
            return entry2;
        }
        
        public void b(final int index, final E entry) {
            this.entries.add(index, entry);
            entry.list = (EntryListWidget<E>)EntryListWidget.this;
        }
        
        public E b(final int index) {
            return this.entries.remove(index);
        }
    }
}
