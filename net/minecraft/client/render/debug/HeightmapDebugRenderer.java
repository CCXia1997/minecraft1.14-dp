package net.minecraft.client.render.debug;

import java.util.Iterator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.world.IWorld;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.world.Heightmap;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.math.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class HeightmapDebugRenderer implements DebugRenderer.Renderer
{
    private final MinecraftClient client;
    
    public HeightmapDebugRenderer(final MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }
    
    @Override
    public void render(final long long1) {
        final Camera camera3 = this.client.gameRenderer.getCamera();
        final IWorld iWorld4 = this.client.world;
        final double double5 = camera3.getPos().x;
        final double double6 = camera3.getPos().y;
        final double double7 = camera3.getPos().z;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        final BlockPos blockPos11 = new BlockPos(camera3.getPos().x, 0.0, camera3.getPos().z);
        final Tessellator tessellator12 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder13 = tessellator12.getBufferBuilder();
        bufferBuilder13.begin(5, VertexFormats.POSITION_COLOR);
        for (final BlockPos blockPos12 : BlockPos.iterate(blockPos11.add(-40, 0, -40), blockPos11.add(40, 0, 40))) {
            final int integer16 = iWorld4.getTop(Heightmap.Type.a, blockPos12.getX(), blockPos12.getZ());
            if (iWorld4.getBlockState(blockPos12.add(0, integer16, 0).down()).isAir()) {
                WorldRenderer.buildBox(bufferBuilder13, blockPos12.getX() + 0.25f - double5, integer16 - double6, blockPos12.getZ() + 0.25f - double7, blockPos12.getX() + 0.75f - double5, integer16 + 0.09375 - double6, blockPos12.getZ() + 0.75f - double7, 0.0f, 0.0f, 1.0f, 0.5f);
            }
            else {
                WorldRenderer.buildBox(bufferBuilder13, blockPos12.getX() + 0.25f - double5, integer16 - double6, blockPos12.getZ() + 0.25f - double7, blockPos12.getX() + 0.75f - double5, integer16 + 0.09375 - double6, blockPos12.getZ() + 0.75f - double7, 0.0f, 1.0f, 0.0f, 0.5f);
            }
        }
        tessellator12.draw();
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
}
