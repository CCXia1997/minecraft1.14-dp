package net.minecraft.world.gen.carver;

import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import java.util.BitSet;
import net.minecraft.world.chunk.Chunk;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.ProbabilityConfig;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class UnderwaterRavineCarver extends RavineCarver
{
    public UnderwaterRavineCarver(final Function<Dynamic<?>, ? extends ProbabilityConfig> function) {
        super(function);
        this.alwaysCarvableBlocks = ImmutableSet.<Block>of(Blocks.b, Blocks.c, Blocks.e, Blocks.g, Blocks.j, Blocks.k, Blocks.l, Blocks.i, Blocks.gJ, Blocks.fx, Blocks.fy, Blocks.fz, Blocks.fA, Blocks.fB, Blocks.fC, Blocks.fD, Blocks.fE, Blocks.fF, Blocks.fG, Blocks.fH, Blocks.fI, Blocks.fJ, Blocks.fK, Blocks.fL, Blocks.fM, Blocks.as, Blocks.hy, Blocks.dL, Blocks.cA, Blocks.C, Blocks.E, Blocks.A, Blocks.B, Blocks.bJ, Blocks.AIR, Blocks.kT);
    }
    
    @Override
    protected boolean isRegionUncarvable(final Chunk chunk, final int mainChunkX, final int mainChunkZ, final int relMinX, final int relMaxX, final int minY, final int maxY, final int relMinZ, final int relMaxZ) {
        return false;
    }
    
    @Override
    protected boolean carveAtPoint(final Chunk chunk, final BitSet mask, final Random random, final BlockPos.Mutable pos1, final BlockPos.Mutable pos2, final BlockPos.Mutable pos3, final int seaLevel, final int mainChunkX, final int mainChunkZ, final int x, final int z, final int relativeX, final int y, final int relativeZ, final AtomicBoolean atomicBoolean) {
        return UnderwaterCaveCarver.carveAtPoint(this, chunk, mask, random, pos1, seaLevel, mainChunkX, mainChunkZ, x, z, relativeX, y, relativeZ);
    }
}
