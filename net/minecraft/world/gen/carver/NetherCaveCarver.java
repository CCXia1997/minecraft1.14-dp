package net.minecraft.world.gen.carver;

import net.minecraft.fluid.Fluid;
import java.util.Set;
import net.minecraft.block.BlockState;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.util.math.BlockPos;
import java.util.BitSet;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluids;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.ProbabilityConfig;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class NetherCaveCarver extends CaveCarver
{
    public NetherCaveCarver(final Function<Dynamic<?>, ? extends ProbabilityConfig> function) {
        super(function, 128);
        this.alwaysCarvableBlocks = ImmutableSet.<Block>of(Blocks.b, Blocks.c, Blocks.e, Blocks.g, Blocks.j, Blocks.k, Blocks.l, Blocks.i, Blocks.cJ);
        this.carvableFluids = ImmutableSet.of(Fluids.LAVA, Fluids.WATER);
    }
    
    @Override
    protected int getMaxCaveCount() {
        return 10;
    }
    
    @Override
    protected float getTunnelSystemWidth(final Random random) {
        return (random.nextFloat() * 2.0f + random.nextFloat()) * 2.0f;
    }
    
    @Override
    protected double getTunnelSystemHeightWidthRatio() {
        return 5.0;
    }
    
    @Override
    protected int getCaveY(final Random random) {
        return random.nextInt(this.heightLimit);
    }
    
    @Override
    protected boolean carveAtPoint(final Chunk chunk, final BitSet mask, final Random random, final BlockPos.Mutable pos1, final BlockPos.Mutable pos2, final BlockPos.Mutable pos3, final int seaLevel, final int mainChunkX, final int mainChunkZ, final int x, final int z, final int relativeX, final int y, final int relativeZ, final AtomicBoolean atomicBoolean) {
        final int integer16 = relativeX | relativeZ << 4 | y << 8;
        if (mask.get(integer16)) {
            return false;
        }
        mask.set(integer16);
        pos1.set(x, y, z);
        if (this.canAlwaysCarveBlock(chunk.getBlockState(pos1))) {
            BlockState blockState17;
            if (y <= 31) {
                blockState17 = NetherCaveCarver.LAVA.getBlockState();
            }
            else {
                blockState17 = NetherCaveCarver.CAVE_AIR;
            }
            chunk.setBlockState(pos1, blockState17, false);
            return true;
        }
        return false;
    }
}
