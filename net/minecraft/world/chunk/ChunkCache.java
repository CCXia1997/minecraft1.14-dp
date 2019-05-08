package net.minecraft.world.chunk;

import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.ExtendedBlockView;

public class ChunkCache implements ExtendedBlockView
{
    protected final int minX;
    protected final int minZ;
    protected final Chunk[][] chunks;
    protected boolean empty;
    protected final World world;
    
    public ChunkCache(final World world, final BlockPos minPos, final BlockPos maxPos) {
        this.world = world;
        this.minX = minPos.getX() >> 4;
        this.minZ = minPos.getZ() >> 4;
        final int integer4 = maxPos.getX() >> 4;
        final int integer5 = maxPos.getZ() >> 4;
        this.chunks = new Chunk[integer4 - this.minX + 1][integer5 - this.minZ + 1];
        this.empty = true;
        for (int integer6 = this.minX; integer6 <= integer4; ++integer6) {
            for (int integer7 = this.minZ; integer7 <= integer5; ++integer7) {
                this.chunks[integer6 - this.minX][integer7 - this.minZ] = world.getChunk(integer6, integer7, ChunkStatus.FULL, false);
            }
        }
        for (int integer6 = minPos.getX() >> 4; integer6 <= maxPos.getX() >> 4; ++integer6) {
            for (int integer7 = minPos.getZ() >> 4; integer7 <= maxPos.getZ() >> 4; ++integer7) {
                final Chunk chunk8 = this.chunks[integer6 - this.minX][integer7 - this.minZ];
                if (chunk8 != null && !chunk8.a(minPos.getY(), maxPos.getY())) {
                    this.empty = false;
                    return;
                }
            }
        }
    }
    
    @Nullable
    private Chunk e(final BlockPos blockPos) {
        final int integer2 = (blockPos.getX() >> 4) - this.minX;
        final int integer3 = (blockPos.getZ() >> 4) - this.minZ;
        if (integer2 < 0 || integer2 >= this.chunks.length || integer3 < 0 || integer3 >= this.chunks[integer2].length) {
            return null;
        }
        return this.chunks[integer2][integer3];
    }
    
    @Nullable
    @Override
    public BlockEntity getBlockEntity(final BlockPos pos) {
        final Chunk chunk2 = this.e(pos);
        if (chunk2 == null) {
            return null;
        }
        return chunk2.getBlockEntity(pos);
    }
    
    @Override
    public BlockState getBlockState(final BlockPos pos) {
        if (World.isHeightInvalid(pos)) {
            return Blocks.AIR.getDefaultState();
        }
        final Chunk chunk2 = this.e(pos);
        if (chunk2 != null) {
            return chunk2.getBlockState(pos);
        }
        return Blocks.z.getDefaultState();
    }
    
    @Override
    public FluidState getFluidState(final BlockPos pos) {
        if (World.isHeightInvalid(pos)) {
            return Fluids.EMPTY.getDefaultState();
        }
        final Chunk chunk2 = this.e(pos);
        if (chunk2 != null) {
            return chunk2.getFluidState(pos);
        }
        return Fluids.EMPTY.getDefaultState();
    }
    
    @Override
    public Biome getBiome(final BlockPos blockPos) {
        final Chunk chunk2 = this.e(blockPos);
        if (chunk2 == null) {
            return Biomes.c;
        }
        return chunk2.getBiome(blockPos);
    }
    
    @Override
    public int getLightLevel(final LightType type, final BlockPos blockPos) {
        return this.world.getLightLevel(type, blockPos);
    }
}
