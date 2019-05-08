package net.minecraft.world.gen.carver;

import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
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

public class UnderwaterCaveCarver extends CaveCarver
{
    public UnderwaterCaveCarver(final Function<Dynamic<?>, ? extends ProbabilityConfig> function) {
        super(function, 256);
        this.alwaysCarvableBlocks = ImmutableSet.<Block>of(Blocks.b, Blocks.c, Blocks.e, Blocks.g, Blocks.j, Blocks.k, Blocks.l, Blocks.i, Blocks.gJ, Blocks.fx, Blocks.fy, Blocks.fz, Blocks.fA, Blocks.fB, Blocks.fC, Blocks.fD, Blocks.fE, Blocks.fF, Blocks.fG, Blocks.fH, Blocks.fI, Blocks.fJ, Blocks.fK, Blocks.fL, Blocks.fM, Blocks.as, Blocks.hy, Blocks.dL, Blocks.cA, Blocks.C, Blocks.E, Blocks.A, Blocks.B, Blocks.bJ, Blocks.AIR, Blocks.kT, Blocks.gL);
    }
    
    @Override
    protected boolean isRegionUncarvable(final Chunk chunk, final int mainChunkX, final int mainChunkZ, final int relMinX, final int relMaxX, final int minY, final int maxY, final int relMinZ, final int relMaxZ) {
        return false;
    }
    
    @Override
    protected boolean carveAtPoint(final Chunk chunk, final BitSet mask, final Random random, final BlockPos.Mutable pos1, final BlockPos.Mutable pos2, final BlockPos.Mutable pos3, final int seaLevel, final int mainChunkX, final int mainChunkZ, final int x, final int z, final int relativeX, final int y, final int relativeZ, final AtomicBoolean atomicBoolean) {
        return carveAtPoint(this, chunk, mask, random, pos1, seaLevel, mainChunkX, mainChunkZ, x, z, relativeX, y, relativeZ);
    }
    
    protected static boolean carveAtPoint(final Carver<?> carver, final Chunk chunk, final BitSet mask, final Random random, final BlockPos.Mutable pos, final int seaLevel, final int mainChunkX, final int mainChunkZ, final int x, final int z, final int relativeX, final int y, final int relativeZ) {
        if (y >= seaLevel) {
            return false;
        }
        final int integer14 = relativeX | relativeZ << 4 | y << 8;
        if (mask.get(integer14)) {
            return false;
        }
        mask.set(integer14);
        pos.set(x, y, z);
        final BlockState blockState15 = chunk.getBlockState(pos);
        if (!carver.canAlwaysCarveBlock(blockState15)) {
            return false;
        }
        if (y == 10) {
            final float float16 = random.nextFloat();
            if (float16 < 0.25) {
                chunk.setBlockState(pos, Blocks.iB.getDefaultState(), false);
                chunk.getBlockTickScheduler().schedule(pos, Blocks.iB, 0);
            }
            else {
                chunk.setBlockState(pos, Blocks.bJ.getDefaultState(), false);
            }
            return true;
        }
        if (y < 10) {
            chunk.setBlockState(pos, Blocks.B.getDefaultState(), false);
            return false;
        }
        boolean boolean16 = false;
        for (final Direction direction18 : Direction.Type.HORIZONTAL) {
            final int integer15 = x + direction18.getOffsetX();
            final int integer16 = z + direction18.getOffsetZ();
            if (integer15 >> 4 != mainChunkX || integer16 >> 4 != mainChunkZ || chunk.getBlockState(pos.set(integer15, y, integer16)).isAir()) {
                chunk.setBlockState(pos, UnderwaterCaveCarver.WATER.getBlockState(), false);
                chunk.getFluidTickScheduler().schedule(pos, UnderwaterCaveCarver.WATER.getFluid(), 0);
                boolean16 = true;
                break;
            }
        }
        pos.set(x, y, z);
        if (!boolean16) {
            chunk.setBlockState(pos, UnderwaterCaveCarver.WATER.getBlockState(), false);
            return true;
        }
        return true;
    }
}
