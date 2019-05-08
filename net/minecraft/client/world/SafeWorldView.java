package net.minecraft.client.world;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.LightType;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.ExtendedBlockView;

@Environment(EnvType.CLIENT)
public class SafeWorldView implements ExtendedBlockView
{
    protected final int chunkXOffset;
    protected final int chunkZOffset;
    protected final BlockPos offset;
    protected final int xSize;
    protected final int ySize;
    protected final int zSize;
    protected final WorldChunk[][] chunks;
    protected final BlockState[] blockStates;
    protected final FluidState[] fluidStates;
    protected final World world;
    
    @Nullable
    public static SafeWorldView create(final World world, final BlockPos startPos, final BlockPos endPos, final int chunkRadius) {
        final int integer5 = startPos.getX() - chunkRadius >> 4;
        final int integer6 = startPos.getZ() - chunkRadius >> 4;
        final int integer7 = endPos.getX() + chunkRadius >> 4;
        final int integer8 = endPos.getZ() + chunkRadius >> 4;
        final WorldChunk[][] arr9 = new WorldChunk[integer7 - integer5 + 1][integer8 - integer6 + 1];
        for (int integer9 = integer5; integer9 <= integer7; ++integer9) {
            for (int integer10 = integer6; integer10 <= integer8; ++integer10) {
                arr9[integer9 - integer5][integer10 - integer6] = world.getChunk(integer9, integer10);
            }
        }
        boolean boolean10 = true;
        for (int integer10 = startPos.getX() >> 4; integer10 <= endPos.getX() >> 4; ++integer10) {
            for (int integer11 = startPos.getZ() >> 4; integer11 <= endPos.getZ() >> 4; ++integer11) {
                final WorldChunk worldChunk13 = arr9[integer10 - integer5][integer11 - integer6];
                if (!worldChunk13.a(startPos.getY(), endPos.getY())) {
                    boolean10 = false;
                }
            }
        }
        if (boolean10) {
            return null;
        }
        int integer10 = 1;
        final BlockPos blockPos12 = startPos.add(-1, -1, -1);
        final BlockPos blockPos13 = endPos.add(1, 1, 1);
        return new SafeWorldView(world, integer5, integer6, arr9, blockPos12, blockPos13);
    }
    
    public SafeWorldView(final World world, final int chunkX, final int chunkZ, final WorldChunk[][] chunks, final BlockPos startPos, final BlockPos endPos) {
        this.world = world;
        this.chunkXOffset = chunkX;
        this.chunkZOffset = chunkZ;
        this.chunks = chunks;
        this.offset = startPos;
        this.xSize = endPos.getX() - startPos.getX() + 1;
        this.ySize = endPos.getY() - startPos.getY() + 1;
        this.zSize = endPos.getZ() - startPos.getZ() + 1;
        this.blockStates = new BlockState[this.xSize * this.ySize * this.zSize];
        this.fluidStates = new FluidState[this.xSize * this.ySize * this.zSize];
        for (final BlockPos blockPos8 : BlockPos.iterate(startPos, endPos)) {
            final int integer9 = (blockPos8.getX() >> 4) - chunkX;
            final int integer10 = (blockPos8.getZ() >> 4) - chunkZ;
            final WorldChunk worldChunk11 = chunks[integer9][integer10];
            final int integer11 = this.getIndex(blockPos8);
            this.blockStates[integer11] = worldChunk11.getBlockState(blockPos8);
            this.fluidStates[integer11] = worldChunk11.getFluidState(blockPos8);
        }
    }
    
    protected final int getIndex(final BlockPos pos) {
        return this.getIndex(pos.getX(), pos.getY(), pos.getZ());
    }
    
    protected int getIndex(final int x, final int y, final int z) {
        final int integer4 = x - this.offset.getX();
        final int integer5 = y - this.offset.getY();
        final int integer6 = z - this.offset.getZ();
        return integer6 * this.xSize * this.ySize + integer5 * this.xSize + integer4;
    }
    
    @Override
    public BlockState getBlockState(final BlockPos pos) {
        return this.blockStates[this.getIndex(pos)];
    }
    
    @Override
    public FluidState getFluidState(final BlockPos pos) {
        return this.fluidStates[this.getIndex(pos)];
    }
    
    @Override
    public int getLightLevel(final LightType type, final BlockPos blockPos) {
        return this.world.getLightLevel(type, blockPos);
    }
    
    @Override
    public Biome getBiome(final BlockPos blockPos) {
        final int integer2 = (blockPos.getX() >> 4) - this.chunkXOffset;
        final int integer3 = (blockPos.getZ() >> 4) - this.chunkZOffset;
        return this.chunks[integer2][integer3].getBiome(blockPos);
    }
    
    @Nullable
    @Override
    public BlockEntity getBlockEntity(final BlockPos pos) {
        return this.getBlockEntity(pos, WorldChunk.CreationType.a);
    }
    
    @Nullable
    public BlockEntity getBlockEntity(final BlockPos blockPos, final WorldChunk.CreationType creationType) {
        final int integer3 = (blockPos.getX() >> 4) - this.chunkXOffset;
        final int integer4 = (blockPos.getZ() >> 4) - this.chunkZOffset;
        return this.chunks[integer3][integer4].getBlockEntity(blockPos, creationType);
    }
}
