package net.minecraft.client.gui;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class DrawableHelper
{
    public static final Identifier BACKGROUND_LOCATION;
    public static final Identifier STATS_ICON_LOCATION;
    public static final Identifier GUI_ICONS_LOCATION;
    protected int blitOffset;
    
    protected void hLine(int xStart, int xEnd, final int y, final int color) {
        if (xEnd < xStart) {
            final int integer5 = xStart;
            xStart = xEnd;
            xEnd = integer5;
        }
        fill(xStart, y, xEnd + 1, y + 1, color);
    }
    
    protected void vLine(final int x, int yStart, int yEnd, final int color) {
        if (yEnd < yStart) {
            final int integer5 = yStart;
            yStart = yEnd;
            yEnd = integer5;
        }
        fill(x, yStart + 1, x + 1, yEnd, color);
    }
    
    public static void fill(int left, int top, int right, int bottom, final int color) {
        if (left < right) {
            final int integer6 = left;
            left = right;
            right = integer6;
        }
        if (top < bottom) {
            final int integer6 = top;
            top = bottom;
            bottom = integer6;
        }
        final float float6 = (color >> 24 & 0xFF) / 255.0f;
        final float float7 = (color >> 16 & 0xFF) / 255.0f;
        final float float8 = (color >> 8 & 0xFF) / 255.0f;
        final float float9 = (color & 0xFF) / 255.0f;
        final Tessellator tessellator10 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder11 = tessellator10.getBufferBuilder();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(float7, float8, float9, float6);
        bufferBuilder11.begin(7, VertexFormats.POSITION);
        bufferBuilder11.vertex(left, bottom, 0.0).next();
        bufferBuilder11.vertex(right, bottom, 0.0).next();
        bufferBuilder11.vertex(right, top, 0.0).next();
        bufferBuilder11.vertex(left, top, 0.0).next();
        tessellator10.draw();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
    
    protected void fillGradient(final int top, final int left, final int right, final int bottom, final int color1, final int color2) {
        final float float7 = (color1 >> 24 & 0xFF) / 255.0f;
        final float float8 = (color1 >> 16 & 0xFF) / 255.0f;
        final float float9 = (color1 >> 8 & 0xFF) / 255.0f;
        final float float10 = (color1 & 0xFF) / 255.0f;
        final float float11 = (color2 >> 24 & 0xFF) / 255.0f;
        final float float12 = (color2 >> 16 & 0xFF) / 255.0f;
        final float float13 = (color2 >> 8 & 0xFF) / 255.0f;
        final float float14 = (color2 & 0xFF) / 255.0f;
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator15 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder16 = tessellator15.getBufferBuilder();
        bufferBuilder16.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder16.vertex(right, left, this.blitOffset).color(float8, float9, float10, float7).next();
        bufferBuilder16.vertex(top, left, this.blitOffset).color(float8, float9, float10, float7).next();
        bufferBuilder16.vertex(top, bottom, this.blitOffset).color(float12, float13, float14, float11).next();
        bufferBuilder16.vertex(right, bottom, this.blitOffset).color(float12, float13, float14, float11).next();
        tessellator15.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
    }
    
    public void drawCenteredString(final TextRenderer textRenderer, final String str, final int centerX, final int y, final int color) {
        textRenderer.drawWithShadow(str, (float)(centerX - textRenderer.getStringWidth(str) / 2), (float)y, color);
    }
    
    public void drawRightAlignedString(final TextRenderer textRenderer, final String str, final int rightX, final int y, final int color) {
        textRenderer.drawWithShadow(str, (float)(rightX - textRenderer.getStringWidth(str)), (float)y, color);
    }
    
    public void drawString(final TextRenderer textRenderer, final String str, final int x, final int y, final int color) {
        textRenderer.drawWithShadow(str, (float)x, (float)y, color);
    }
    
    public static void blit(final int x, final int y, final int z, final int width, final int height, final Sprite sprite) {
        innerBlit(x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }
    
    public void blit(final int x, final int y, final int u, final int v, final int width, final int height) {
        blit(x, y, this.blitOffset, (float)u, (float)v, width, height, 256, 256);
    }
    
    public static void blit(final int x, final int y, final int z, final float u, final float v, final int width, final int height, final int texHeight, final int texWidth) {
        innerBlit(x, x + width, y, y + height, z, width, height, u, v, texWidth, texHeight);
    }
    
    public static void blit(final int x, final int y, final int width, final int height, final float u, final float v, final int uWidth, final int vHeight, final int texWidth, final int texHeight) {
        innerBlit(x, x + width, y, y + height, 0, uWidth, vHeight, u, v, texWidth, texHeight);
    }
    
    public static void blit(final int x, final int y, final float u, final float v, final int width, final int height, final int texWidth, final int texHeight) {
        blit(x, y, width, height, u, v, width, height, texWidth, texHeight);
    }
    
    private static void innerBlit(final int xStart, final int xEnd, final int yStart, final int yEnd, final int z, final int width, final int height, final float u, final float v, final int texWidth, final int texHeight) {
        innerBlit(xStart, xEnd, yStart, yEnd, z, (u + 0.0f) / texWidth, (u + width) / texWidth, (v + 0.0f) / texHeight, (v + height) / texHeight);
    }
    
    protected static void innerBlit(final int xStart, final int xEnd, final int yStart, final int yEnd, final int z, final float uStart, final float uEnd, final float vStart, final float vEnd) {
        final Tessellator tessellator10 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder11 = tessellator10.getBufferBuilder();
        bufferBuilder11.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder11.vertex(xStart, yEnd, z).texture(uStart, vEnd).next();
        bufferBuilder11.vertex(xEnd, yEnd, z).texture(uEnd, vEnd).next();
        bufferBuilder11.vertex(xEnd, yStart, z).texture(uEnd, vStart).next();
        bufferBuilder11.vertex(xStart, yStart, z).texture(uStart, vStart).next();
        tessellator10.draw();
    }
    
    static {
        BACKGROUND_LOCATION = new Identifier("textures/gui/options_background.png");
        STATS_ICON_LOCATION = new Identifier("textures/gui/container/stats_icons.png");
        GUI_ICONS_LOCATION = new Identifier("textures/gui/icons.png");
    }
}
