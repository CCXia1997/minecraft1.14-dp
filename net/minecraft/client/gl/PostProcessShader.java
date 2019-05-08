package net.minecraft.client.gl;

import java.util.Iterator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import com.google.common.collect.Lists;
import net.minecraft.resource.ResourceManager;
import net.minecraft.client.util.math.Matrix4f;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PostProcessShader implements AutoCloseable
{
    private final JsonGlProgram program;
    public final GlFramebuffer input;
    public final GlFramebuffer output;
    private final List<Object> samplerValues;
    private final List<String> samplerNames;
    private final List<Integer> samplerWidths;
    private final List<Integer> samplerHeights;
    private Matrix4f projectionMatrix;
    
    public PostProcessShader(final ResourceManager resourceManager, final String programName, final GlFramebuffer input, final GlFramebuffer output) throws IOException {
        this.samplerValues = Lists.newArrayList();
        this.samplerNames = Lists.newArrayList();
        this.samplerWidths = Lists.newArrayList();
        this.samplerHeights = Lists.newArrayList();
        this.program = new JsonGlProgram(resourceManager, programName);
        this.input = input;
        this.output = output;
    }
    
    @Override
    public void close() {
        this.program.close();
    }
    
    public void addAuxTarget(final String name, final Object target, final int width, final int height) {
        this.samplerNames.add(this.samplerNames.size(), name);
        this.samplerValues.add(this.samplerValues.size(), target);
        this.samplerWidths.add(this.samplerWidths.size(), width);
        this.samplerHeights.add(this.samplerHeights.size(), height);
    }
    
    private void setGlState() {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.disableDepthTest();
        GlStateManager.disableAlphaTest();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableColorMaterial();
        GlStateManager.enableTexture();
        GlStateManager.bindTexture(0);
    }
    
    public void setProjectionMatrix(final Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }
    
    public void render(final float time) {
        this.setGlState();
        this.input.endWrite();
        final float float2 = (float)this.output.texWidth;
        final float float3 = (float)this.output.texHeight;
        GlStateManager.viewport(0, 0, (int)float2, (int)float3);
        this.program.bindSampler("DiffuseSampler", this.input);
        for (int integer4 = 0; integer4 < this.samplerValues.size(); ++integer4) {
            this.program.bindSampler(this.samplerNames.get(integer4), this.samplerValues.get(integer4));
            this.program.getUniformByNameOrDummy("AuxSize" + integer4).set(this.samplerWidths.get(integer4), this.samplerHeights.get(integer4));
        }
        this.program.getUniformByNameOrDummy("ProjMat").set(this.projectionMatrix);
        this.program.getUniformByNameOrDummy("InSize").set((float)this.input.texWidth, (float)this.input.texHeight);
        this.program.getUniformByNameOrDummy("OutSize").set(float2, float3);
        this.program.getUniformByNameOrDummy("Time").set(time);
        final MinecraftClient minecraftClient4 = MinecraftClient.getInstance();
        this.program.getUniformByNameOrDummy("ScreenSize").set((float)minecraftClient4.window.getFramebufferWidth(), (float)minecraftClient4.window.getFramebufferHeight());
        this.program.enable();
        this.output.clear(MinecraftClient.IS_SYSTEM_MAC);
        this.output.beginWrite(false);
        GlStateManager.depthMask(false);
        GlStateManager.colorMask(true, true, true, true);
        final Tessellator tessellator5 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder6 = tessellator5.getBufferBuilder();
        bufferBuilder6.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder6.vertex(0.0, 0.0, 500.0).color(255, 255, 255, 255).next();
        bufferBuilder6.vertex(float2, 0.0, 500.0).color(255, 255, 255, 255).next();
        bufferBuilder6.vertex(float2, float3, 500.0).color(255, 255, 255, 255).next();
        bufferBuilder6.vertex(0.0, float3, 500.0).color(255, 255, 255, 255).next();
        tessellator5.draw();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        this.program.disable();
        this.output.endWrite();
        this.input.endRead();
        for (final Object object8 : this.samplerValues) {
            if (object8 instanceof GlFramebuffer) {
                ((GlFramebuffer)object8).endRead();
            }
        }
    }
    
    public JsonGlProgram getProgram() {
        return this.program;
    }
}
