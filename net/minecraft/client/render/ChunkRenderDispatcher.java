package net.minecraft.client.render;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.chunk.ChunkRendererFactory;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChunkRenderDispatcher
{
    protected final WorldRenderer renderer;
    protected final World world;
    protected int sizeY;
    protected int sizeX;
    protected int sizeZ;
    public ChunkRenderer[] renderers;
    
    public ChunkRenderDispatcher(final World world, final int integer, final WorldRenderer renderer, final ChunkRendererFactory chunkRendererFactory) {
        this.renderer = renderer;
        this.world = world;
        this.a(integer);
        this.createChunks(chunkRendererFactory);
    }
    
    protected void createChunks(final ChunkRendererFactory chunkRendererFactory) {
        final int integer2 = this.sizeX * this.sizeY * this.sizeZ;
        this.renderers = new ChunkRenderer[integer2];
        for (int integer3 = 0; integer3 < this.sizeX; ++integer3) {
            for (int integer4 = 0; integer4 < this.sizeY; ++integer4) {
                for (int integer5 = 0; integer5 < this.sizeZ; ++integer5) {
                    final int integer6 = this.getChunkIndex(integer3, integer4, integer5);
                    (this.renderers[integer6] = chunkRendererFactory.create(this.world, this.renderer)).a(integer3 * 16, integer4 * 16, integer5 * 16);
                }
            }
        }
    }
    
    public void delete() {
        for (final ChunkRenderer chunkRenderer4 : this.renderers) {
            chunkRenderer4.delete();
        }
    }
    
    private int getChunkIndex(final int integer1, final int integer2, final int integer3) {
        return (integer3 * this.sizeY + integer2) * this.sizeX + integer1;
    }
    
    protected void a(final int integer) {
        final int integer2 = integer * 2 + 1;
        this.sizeX = integer2;
        this.sizeY = 16;
        this.sizeZ = integer2;
    }
    
    public void updateCameraPosition(final double double1, final double double3) {
        final int integer5 = MathHelper.floor(double1) - 8;
        final int integer6 = MathHelper.floor(double3) - 8;
        final int integer7 = this.sizeX * 16;
        for (int integer8 = 0; integer8 < this.sizeX; ++integer8) {
            final int integer9 = this.b(integer5, integer7, integer8);
            for (int integer10 = 0; integer10 < this.sizeZ; ++integer10) {
                final int integer11 = this.b(integer6, integer7, integer10);
                for (int integer12 = 0; integer12 < this.sizeY; ++integer12) {
                    final int integer13 = integer12 * 16;
                    final ChunkRenderer chunkRenderer14 = this.renderers[this.getChunkIndex(integer8, integer12, integer10)];
                    chunkRenderer14.a(integer9, integer13, integer11);
                }
            }
        }
    }
    
    private int b(final int integer1, final int integer2, final int integer3) {
        final int integer4 = integer3 * 16;
        int integer5 = integer4 - integer1 + integer2 / 2;
        if (integer5 < 0) {
            integer5 -= integer2 - 1;
        }
        return integer4 - integer5 / integer2 * integer2;
    }
    
    public void scheduleChunkRender(final int x, final int y, final int z, final boolean boolean4) {
        final int integer5 = Math.floorMod(x, this.sizeX);
        final int integer6 = Math.floorMod(y, this.sizeY);
        final int integer7 = Math.floorMod(z, this.sizeZ);
        final ChunkRenderer chunkRenderer8 = this.renderers[this.getChunkIndex(integer5, integer6, integer7)];
        chunkRenderer8.scheduleRender(boolean4);
    }
    
    @Nullable
    protected ChunkRenderer getChunkRenderer(final BlockPos pos) {
        int integer2 = MathHelper.floorDiv(pos.getX(), 16);
        final int integer3 = MathHelper.floorDiv(pos.getY(), 16);
        int integer4 = MathHelper.floorDiv(pos.getZ(), 16);
        if (integer3 < 0 || integer3 >= this.sizeY) {
            return null;
        }
        integer2 = MathHelper.floorMod(integer2, this.sizeX);
        integer4 = MathHelper.floorMod(integer4, this.sizeZ);
        return this.renderers[this.getChunkIndex(integer2, integer3, integer4)];
    }
}
