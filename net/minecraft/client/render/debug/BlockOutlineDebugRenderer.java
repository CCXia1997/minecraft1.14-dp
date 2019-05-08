package net.minecraft.client.render.debug;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import net.minecraft.world.BlockView;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockOutlineDebugRenderer implements DebugRenderer.Renderer
{
    private final MinecraftClient client;
    
    public BlockOutlineDebugRenderer(final MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }
    
    @Override
    public void render(final long long1) {
        final Camera camera3 = this.client.gameRenderer.getCamera();
        final double double4 = camera3.getPos().x;
        final double double5 = camera3.getPos().y;
        final double double6 = camera3.getPos().z;
        final BlockView blockView10 = this.client.player.world;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.lineWidth(2.0f);
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        final BlockPos blockPos11 = new BlockPos(camera3.getPos());
        for (final BlockPos blockPos12 : BlockPos.iterate(blockPos11.add(-6, -6, -6), blockPos11.add(6, 6, 6))) {
            final BlockState blockState14 = blockView10.getBlockState(blockPos12);
            if (blockState14.getBlock() == Blocks.AIR) {
                continue;
            }
            final VoxelShape voxelShape15 = blockState14.getOutlineShape(blockView10, blockPos12);
            for (final BoundingBox boundingBox17 : voxelShape15.getBoundingBoxes()) {
                final BoundingBox boundingBox18 = boundingBox17.offset(blockPos12).expand(0.002).offset(-double4, -double5, -double6);
                final double double7 = boundingBox18.minX;
                final double double8 = boundingBox18.minY;
                final double double9 = boundingBox18.minZ;
                final double double10 = boundingBox18.maxX;
                final double double11 = boundingBox18.maxY;
                final double double12 = boundingBox18.maxZ;
                final float float31 = 1.0f;
                final float float32 = 0.0f;
                final float float33 = 0.0f;
                final float float34 = 0.5f;
                if (Block.isSolidFullSquare(blockState14, blockView10, blockPos12, Direction.WEST)) {
                    final Tessellator tessellator35 = Tessellator.getInstance();
                    final BufferBuilder bufferBuilder36 = tessellator35.getBufferBuilder();
                    bufferBuilder36.begin(5, VertexFormats.POSITION_COLOR);
                    bufferBuilder36.vertex(double7, double8, double9).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double7, double8, double12).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double7, double11, double9).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double7, double11, double12).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    tessellator35.draw();
                }
                if (Block.isSolidFullSquare(blockState14, blockView10, blockPos12, Direction.SOUTH)) {
                    final Tessellator tessellator35 = Tessellator.getInstance();
                    final BufferBuilder bufferBuilder36 = tessellator35.getBufferBuilder();
                    bufferBuilder36.begin(5, VertexFormats.POSITION_COLOR);
                    bufferBuilder36.vertex(double7, double11, double12).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double7, double8, double12).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double10, double11, double12).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double10, double8, double12).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    tessellator35.draw();
                }
                if (Block.isSolidFullSquare(blockState14, blockView10, blockPos12, Direction.EAST)) {
                    final Tessellator tessellator35 = Tessellator.getInstance();
                    final BufferBuilder bufferBuilder36 = tessellator35.getBufferBuilder();
                    bufferBuilder36.begin(5, VertexFormats.POSITION_COLOR);
                    bufferBuilder36.vertex(double10, double8, double12).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double10, double8, double9).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double10, double11, double12).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double10, double11, double9).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    tessellator35.draw();
                }
                if (Block.isSolidFullSquare(blockState14, blockView10, blockPos12, Direction.NORTH)) {
                    final Tessellator tessellator35 = Tessellator.getInstance();
                    final BufferBuilder bufferBuilder36 = tessellator35.getBufferBuilder();
                    bufferBuilder36.begin(5, VertexFormats.POSITION_COLOR);
                    bufferBuilder36.vertex(double10, double11, double9).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double10, double8, double9).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double7, double11, double9).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double7, double8, double9).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    tessellator35.draw();
                }
                if (Block.isSolidFullSquare(blockState14, blockView10, blockPos12, Direction.DOWN)) {
                    final Tessellator tessellator35 = Tessellator.getInstance();
                    final BufferBuilder bufferBuilder36 = tessellator35.getBufferBuilder();
                    bufferBuilder36.begin(5, VertexFormats.POSITION_COLOR);
                    bufferBuilder36.vertex(double7, double8, double9).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double10, double8, double9).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double7, double8, double12).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double10, double8, double12).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    tessellator35.draw();
                }
                if (Block.isSolidFullSquare(blockState14, blockView10, blockPos12, Direction.UP)) {
                    final Tessellator tessellator35 = Tessellator.getInstance();
                    final BufferBuilder bufferBuilder36 = tessellator35.getBufferBuilder();
                    bufferBuilder36.begin(5, VertexFormats.POSITION_COLOR);
                    bufferBuilder36.vertex(double7, double11, double9).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double7, double11, double12).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double10, double11, double9).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder36.vertex(double10, double11, double12).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    tessellator35.draw();
                }
            }
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
}
