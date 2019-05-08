package net.minecraft.client.render.debug;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChunkBorderDebugRenderer implements DebugRenderer.Renderer
{
    private final MinecraftClient client;
    
    public ChunkBorderDebugRenderer(final MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }
    
    @Override
    public void render(final long long1) {
        final Camera camera3 = this.client.gameRenderer.getCamera();
        final Tessellator tessellator4 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder5 = tessellator4.getBufferBuilder();
        final double double6 = camera3.getPos().x;
        final double double7 = camera3.getPos().y;
        final double double8 = camera3.getPos().z;
        final double double9 = 0.0 - double7;
        final double double10 = 256.0 - double7;
        GlStateManager.disableTexture();
        GlStateManager.disableBlend();
        final double double11 = (camera3.getFocusedEntity().chunkX << 4) - double6;
        final double double12 = (camera3.getFocusedEntity().chunkZ << 4) - double8;
        GlStateManager.lineWidth(1.0f);
        bufferBuilder5.begin(3, VertexFormats.POSITION_COLOR);
        for (int integer20 = -16; integer20 <= 32; integer20 += 16) {
            for (int integer21 = -16; integer21 <= 32; integer21 += 16) {
                bufferBuilder5.vertex(double11 + integer20, double9, double12 + integer21).color(1.0f, 0.0f, 0.0f, 0.0f).next();
                bufferBuilder5.vertex(double11 + integer20, double9, double12 + integer21).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                bufferBuilder5.vertex(double11 + integer20, double10, double12 + integer21).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                bufferBuilder5.vertex(double11 + integer20, double10, double12 + integer21).color(1.0f, 0.0f, 0.0f, 0.0f).next();
            }
        }
        for (int integer20 = 2; integer20 < 16; integer20 += 2) {
            bufferBuilder5.vertex(double11 + integer20, double9, double12).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder5.vertex(double11 + integer20, double9, double12).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder5.vertex(double11 + integer20, double10, double12).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder5.vertex(double11 + integer20, double10, double12).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder5.vertex(double11 + integer20, double9, double12 + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder5.vertex(double11 + integer20, double9, double12 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder5.vertex(double11 + integer20, double10, double12 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder5.vertex(double11 + integer20, double10, double12 + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        for (int integer20 = 2; integer20 < 16; integer20 += 2) {
            bufferBuilder5.vertex(double11, double9, double12 + integer20).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder5.vertex(double11, double9, double12 + integer20).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder5.vertex(double11, double10, double12 + integer20).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder5.vertex(double11, double10, double12 + integer20).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder5.vertex(double11 + 16.0, double9, double12 + integer20).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder5.vertex(double11 + 16.0, double9, double12 + integer20).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder5.vertex(double11 + 16.0, double10, double12 + integer20).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder5.vertex(double11 + 16.0, double10, double12 + integer20).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        for (int integer20 = 0; integer20 <= 256; integer20 += 2) {
            final double double13 = integer20 - double7;
            bufferBuilder5.vertex(double11, double13, double12).color(1.0f, 1.0f, 0.0f, 0.0f).next();
            bufferBuilder5.vertex(double11, double13, double12).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder5.vertex(double11, double13, double12 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder5.vertex(double11 + 16.0, double13, double12 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder5.vertex(double11 + 16.0, double13, double12).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder5.vertex(double11, double13, double12).color(1.0f, 1.0f, 0.0f, 1.0f).next();
            bufferBuilder5.vertex(double11, double13, double12).color(1.0f, 1.0f, 0.0f, 0.0f).next();
        }
        tessellator4.draw();
        GlStateManager.lineWidth(2.0f);
        bufferBuilder5.begin(3, VertexFormats.POSITION_COLOR);
        for (int integer20 = 0; integer20 <= 16; integer20 += 16) {
            for (int integer21 = 0; integer21 <= 16; integer21 += 16) {
                bufferBuilder5.vertex(double11 + integer20, double9, double12 + integer21).color(0.25f, 0.25f, 1.0f, 0.0f).next();
                bufferBuilder5.vertex(double11 + integer20, double9, double12 + integer21).color(0.25f, 0.25f, 1.0f, 1.0f).next();
                bufferBuilder5.vertex(double11 + integer20, double10, double12 + integer21).color(0.25f, 0.25f, 1.0f, 1.0f).next();
                bufferBuilder5.vertex(double11 + integer20, double10, double12 + integer21).color(0.25f, 0.25f, 1.0f, 0.0f).next();
            }
        }
        for (int integer20 = 0; integer20 <= 256; integer20 += 16) {
            final double double13 = integer20 - double7;
            bufferBuilder5.vertex(double11, double13, double12).color(0.25f, 0.25f, 1.0f, 0.0f).next();
            bufferBuilder5.vertex(double11, double13, double12).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder5.vertex(double11, double13, double12 + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder5.vertex(double11 + 16.0, double13, double12 + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder5.vertex(double11 + 16.0, double13, double12).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder5.vertex(double11, double13, double12).color(0.25f, 0.25f, 1.0f, 1.0f).next();
            bufferBuilder5.vertex(double11, double13, double12).color(0.25f, 0.25f, 1.0f, 0.0f).next();
        }
        tessellator4.draw();
        GlStateManager.lineWidth(1.0f);
        GlStateManager.enableBlend();
        GlStateManager.enableTexture();
    }
}
