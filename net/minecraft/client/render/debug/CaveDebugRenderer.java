package net.minecraft.client.render.debug;

import java.util.Iterator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3i;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CaveDebugRenderer implements DebugRenderer.Renderer
{
    private final MinecraftClient a;
    private final Map<BlockPos, BlockPos> b;
    private final Map<BlockPos, Float> c;
    private final List<BlockPos> d;
    
    public CaveDebugRenderer(final MinecraftClient minecraftClient) {
        this.b = Maps.newHashMap();
        this.c = Maps.newHashMap();
        this.d = Lists.newArrayList();
        this.a = minecraftClient;
    }
    
    public void a(final BlockPos blockPos, final List<BlockPos> list2, final List<Float> list3) {
        for (int integer4 = 0; integer4 < list2.size(); ++integer4) {
            this.b.put(list2.get(integer4), blockPos);
            this.c.put(list2.get(integer4), list3.get(integer4));
        }
        this.d.add(blockPos);
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
        final BlockPos blockPos10 = new BlockPos(camera3.getPos().x, 0.0, camera3.getPos().z);
        final Tessellator tessellator11 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder12 = tessellator11.getBufferBuilder();
        bufferBuilder12.begin(5, VertexFormats.POSITION_COLOR);
        for (final Map.Entry<BlockPos, BlockPos> entry14 : this.b.entrySet()) {
            final BlockPos blockPos11 = entry14.getKey();
            final BlockPos blockPos12 = entry14.getValue();
            final float float17 = blockPos12.getX() * 128 % 256 / 256.0f;
            final float float18 = blockPos12.getY() * 128 % 256 / 256.0f;
            final float float19 = blockPos12.getZ() * 128 % 256 / 256.0f;
            final float float20 = this.c.get(blockPos11);
            if (blockPos10.isWithinDistance(blockPos11, 160.0)) {
                WorldRenderer.buildBox(bufferBuilder12, blockPos11.getX() + 0.5f - double4 - float20, blockPos11.getY() + 0.5f - double5 - float20, blockPos11.getZ() + 0.5f - double6 - float20, blockPos11.getX() + 0.5f - double4 + float20, blockPos11.getY() + 0.5f - double5 + float20, blockPos11.getZ() + 0.5f - double6 + float20, float17, float18, float19, 0.5f);
            }
        }
        for (final BlockPos blockPos13 : this.d) {
            if (blockPos10.isWithinDistance(blockPos13, 160.0)) {
                WorldRenderer.buildBox(bufferBuilder12, blockPos13.getX() - double4, blockPos13.getY() - double5, blockPos13.getZ() - double6, blockPos13.getX() + 1.0f - double4, blockPos13.getY() + 1.0f - double5, blockPos13.getZ() + 1.0f - double6, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        tessellator11.draw();
        GlStateManager.enableDepthTest();
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
}
