package net.minecraft.client.render.debug;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WorldGenAttemptDebugRenderer implements DebugRenderer.Renderer
{
    private final MinecraftClient a;
    private final List<BlockPos> b;
    private final List<Float> c;
    private final List<Float> d;
    private final List<Float> e;
    private final List<Float> f;
    private final List<Float> g;
    
    public WorldGenAttemptDebugRenderer(final MinecraftClient minecraftClient) {
        this.b = Lists.newArrayList();
        this.c = Lists.newArrayList();
        this.d = Lists.newArrayList();
        this.e = Lists.newArrayList();
        this.f = Lists.newArrayList();
        this.g = Lists.newArrayList();
        this.a = minecraftClient;
    }
    
    public void a(final BlockPos blockPos, final float float2, final float float3, final float float4, final float float5, final float float6) {
        this.b.add(blockPos);
        this.c.add(float2);
        this.d.add(float6);
        this.e.add(float3);
        this.f.add(float4);
        this.g.add(float5);
    }
    
    @Override
    public void render(final long long1) {
        final Camera camera3 = this.a.gameRenderer.getCamera();
        final double double4 = camera3.getPos().x;
        final double double5 = camera3.getPos().y;
        final double double6 = camera3.getPos().z;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        final Tessellator tessellator10 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder11 = tessellator10.getBufferBuilder();
        bufferBuilder11.begin(5, VertexFormats.POSITION_COLOR);
        for (int integer12 = 0; integer12 < this.b.size(); ++integer12) {
            final BlockPos blockPos13 = this.b.get(integer12);
            final Float float14 = this.c.get(integer12);
            final float float15 = float14 / 2.0f;
            WorldRenderer.buildBox(bufferBuilder11, blockPos13.getX() + 0.5f - float15 - double4, blockPos13.getY() + 0.5f - float15 - double5, blockPos13.getZ() + 0.5f - float15 - double6, blockPos13.getX() + 0.5f + float15 - double4, blockPos13.getY() + 0.5f + float15 - double5, blockPos13.getZ() + 0.5f + float15 - double6, this.e.get(integer12), this.f.get(integer12), this.g.get(integer12), this.d.get(integer12));
        }
        tessellator10.draw();
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
}
