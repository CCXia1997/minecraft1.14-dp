package net.minecraft.client.gui;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.Matrix4f;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CubeMapRenderer
{
    private final Identifier[] faces;
    
    public CubeMapRenderer(final Identifier faces) {
        this.faces = new Identifier[6];
        for (int integer2 = 0; integer2 < 6; ++integer2) {
            this.faces[integer2] = new Identifier(faces.getNamespace(), faces.getPath() + '_' + integer2 + ".png");
        }
    }
    
    public void draw(final MinecraftClient client, final float x, final float y, final float alpha) {
        final Tessellator tessellator5 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder6 = tessellator5.getBufferBuilder();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.multMatrix(Matrix4f.a(85.0, client.window.getFramebufferWidth() / (float)client.window.getFramebufferHeight(), 0.05f, 10.0f));
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        final int integer7 = 2;
        for (int integer8 = 0; integer8 < 4; ++integer8) {
            GlStateManager.pushMatrix();
            final float float9 = (integer8 % 2 / 2.0f - 0.5f) / 256.0f;
            final float float10 = (integer8 / 2 / 2.0f - 0.5f) / 256.0f;
            final float float11 = 0.0f;
            GlStateManager.translatef(float9, float10, 0.0f);
            GlStateManager.rotatef(x, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(y, 0.0f, 1.0f, 0.0f);
            for (int integer9 = 0; integer9 < 6; ++integer9) {
                client.getTextureManager().bindTexture(this.faces[integer9]);
                bufferBuilder6.begin(7, VertexFormats.POSITION_UV_COLOR);
                final int integer10 = Math.round(255.0f * alpha) / (integer8 + 1);
                if (integer9 == 0) {
                    bufferBuilder6.vertex(-1.0, -1.0, 1.0).texture(0.0, 0.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(-1.0, 1.0, 1.0).texture(0.0, 1.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(1.0, 1.0, 1.0).texture(1.0, 1.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(1.0, -1.0, 1.0).texture(1.0, 0.0).color(255, 255, 255, integer10).next();
                }
                if (integer9 == 1) {
                    bufferBuilder6.vertex(1.0, -1.0, 1.0).texture(0.0, 0.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(1.0, 1.0, 1.0).texture(0.0, 1.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(1.0, 1.0, -1.0).texture(1.0, 1.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(1.0, -1.0, -1.0).texture(1.0, 0.0).color(255, 255, 255, integer10).next();
                }
                if (integer9 == 2) {
                    bufferBuilder6.vertex(1.0, -1.0, -1.0).texture(0.0, 0.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(1.0, 1.0, -1.0).texture(0.0, 1.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(-1.0, 1.0, -1.0).texture(1.0, 1.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(-1.0, -1.0, -1.0).texture(1.0, 0.0).color(255, 255, 255, integer10).next();
                }
                if (integer9 == 3) {
                    bufferBuilder6.vertex(-1.0, -1.0, -1.0).texture(0.0, 0.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(-1.0, 1.0, -1.0).texture(0.0, 1.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(-1.0, 1.0, 1.0).texture(1.0, 1.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(-1.0, -1.0, 1.0).texture(1.0, 0.0).color(255, 255, 255, integer10).next();
                }
                if (integer9 == 4) {
                    bufferBuilder6.vertex(-1.0, -1.0, -1.0).texture(0.0, 0.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(-1.0, -1.0, 1.0).texture(0.0, 1.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(1.0, -1.0, 1.0).texture(1.0, 1.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(1.0, -1.0, -1.0).texture(1.0, 0.0).color(255, 255, 255, integer10).next();
                }
                if (integer9 == 5) {
                    bufferBuilder6.vertex(-1.0, 1.0, 1.0).texture(0.0, 0.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(-1.0, 1.0, -1.0).texture(0.0, 1.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(1.0, 1.0, -1.0).texture(1.0, 1.0).color(255, 255, 255, integer10).next();
                    bufferBuilder6.vertex(1.0, 1.0, 1.0).texture(1.0, 0.0).color(255, 255, 255, integer10).next();
                }
                tessellator5.draw();
            }
            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }
        bufferBuilder6.setOffset(0.0, 0.0, 0.0);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepthTest();
    }
    
    public CompletableFuture<Void> loadTexturesAsync(final TextureManager textureManager, final Executor executor) {
        final CompletableFuture<?>[] arr3 = new CompletableFuture[6];
        for (int integer4 = 0; integer4 < arr3.length; ++integer4) {
            arr3[integer4] = textureManager.loadTextureAsync(this.faces[integer4], executor);
        }
        return CompletableFuture.allOf(arr3);
    }
}
