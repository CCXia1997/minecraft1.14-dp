package net.minecraft.world.gen.carver;

import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import java.util.Random;
import java.util.BitSet;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluids;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.fluid.Fluid;
import net.minecraft.block.Block;
import java.util.Set;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.ProbabilityConfig;

public abstract class Carver<C extends CarverConfig>
{
    public static final Carver<ProbabilityConfig> CAVE;
    public static final Carver<ProbabilityConfig> NETHER_CAVE;
    public static final Carver<ProbabilityConfig> RAVINE;
    public static final Carver<ProbabilityConfig> UNDERWATER_RAVINE;
    public static final Carver<ProbabilityConfig> UNDERWATER_CAVE;
    protected static final BlockState AIR;
    protected static final BlockState CAVE_AIR;
    protected static final FluidState WATER;
    protected static final FluidState LAVA;
    protected Set<Block> alwaysCarvableBlocks;
    protected Set<Fluid> carvableFluids;
    private final Function<Dynamic<?>, ? extends C> configDeserializer;
    protected final int heightLimit;
    
    private static <C extends CarverConfig, F extends Carver<C>> F register(final String string, final F carver) {
        return Registry.<F>register(Registry.CARVER, string, carver);
    }
    
    public Carver(final Function<Dynamic<?>, ? extends C> configDeserializer, final int heightLimit) {
        this.alwaysCarvableBlocks = ImmutableSet.<Block>of(Blocks.b, Blocks.c, Blocks.e, Blocks.g, Blocks.j, Blocks.k, Blocks.l, Blocks.i, Blocks.gJ, Blocks.fx, Blocks.fy, Blocks.fz, Blocks.fA, Blocks.fB, Blocks.fC, Blocks.fD, Blocks.fE, Blocks.fF, Blocks.fG, Blocks.fH, Blocks.fI, Blocks.fJ, Blocks.fK, Blocks.fL, Blocks.fM, Blocks.as, Blocks.hy, Blocks.dL, Blocks.cA, Blocks.gL);
        this.carvableFluids = ImmutableSet.of(Fluids.WATER);
        this.configDeserializer = configDeserializer;
        this.heightLimit = heightLimit;
    }
    
    public int getBranchFactor() {
        return 4;
    }
    
    protected boolean carveRegion(final Chunk chunk, final long seed, final int seaLevel, final int mainChunkX, final int mainChunkZ, final double centerX, final double centerY, final double centerZ, final double xzSize, final double ySize, final BitSet mask) {
        final Random random18 = new Random(seed + mainChunkX + mainChunkZ);
        final double double19 = mainChunkX * 16 + 8;
        final double double20 = mainChunkZ * 16 + 8;
        if (centerX < double19 - 16.0 - xzSize * 2.0 || centerZ < double20 - 16.0 - xzSize * 2.0 || centerX > double19 + 16.0 + xzSize * 2.0 || centerZ > double20 + 16.0 + xzSize * 2.0) {
            return false;
        }
        final int integer23 = Math.max(MathHelper.floor(centerX - xzSize) - mainChunkX * 16 - 1, 0);
        final int integer24 = Math.min(MathHelper.floor(centerX + xzSize) - mainChunkX * 16 + 1, 16);
        final int integer25 = Math.max(MathHelper.floor(centerY - ySize) - 1, 1);
        final int integer26 = Math.min(MathHelper.floor(centerY + ySize) + 1, this.heightLimit - 8);
        final int integer27 = Math.max(MathHelper.floor(centerZ - xzSize) - mainChunkZ * 16 - 1, 0);
        final int integer28 = Math.min(MathHelper.floor(centerZ + xzSize) - mainChunkZ * 16 + 1, 16);
        if (this.isRegionUncarvable(chunk, mainChunkX, mainChunkZ, integer23, integer24, integer25, integer26, integer27, integer28)) {
            return false;
        }
        boolean boolean29 = false;
        final BlockPos.Mutable mutable30 = new BlockPos.Mutable();
        final BlockPos.Mutable mutable31 = new BlockPos.Mutable();
        final BlockPos.Mutable mutable32 = new BlockPos.Mutable();
        for (int integer29 = integer23; integer29 < integer24; ++integer29) {
            final int integer30 = integer29 + mainChunkX * 16;
            final double double21 = (integer30 + 0.5 - centerX) / xzSize;
            for (int integer31 = integer27; integer31 < integer28; ++integer31) {
                final int integer32 = integer31 + mainChunkZ * 16;
                final double double22 = (integer32 + 0.5 - centerZ) / xzSize;
                if (double21 * double21 + double22 * double22 < 1.0) {
                    final AtomicBoolean atomicBoolean41 = new AtomicBoolean(false);
                    for (int integer33 = integer26; integer33 > integer25; --integer33) {
                        final double double23 = (integer33 - 0.5 - centerY) / ySize;
                        if (!this.isPositionExcluded(double21, double23, double22, integer33)) {
                            boolean29 |= this.carveAtPoint(chunk, mask, random18, mutable30, mutable31, mutable32, seaLevel, mainChunkX, mainChunkZ, integer30, integer32, integer29, integer33, integer31, atomicBoolean41);
                        }
                    }
                }
            }
        }
        return boolean29;
    }
    
    protected boolean carveAtPoint(final Chunk chunk, final BitSet mask, final Random random, final BlockPos.Mutable pos1, final BlockPos.Mutable pos2, final BlockPos.Mutable pos3, final int seaLevel, final int mainChunkX, final int mainChunkZ, final int x, final int z, final int relativeX, final int y, final int relativeZ, final AtomicBoolean atomicBoolean) {
        final int integer16 = relativeX | relativeZ << 4 | y << 8;
        if (mask.get(integer16)) {
            return false;
        }
        mask.set(integer16);
        pos1.set(x, y, z);
        final BlockState blockState17 = chunk.getBlockState(pos1);
        final BlockState blockState18 = chunk.getBlockState(pos2.set(pos1).setOffset(Direction.UP));
        if (blockState17.getBlock() == Blocks.i || blockState17.getBlock() == Blocks.dL) {
            atomicBoolean.set(true);
        }
        if (!this.canCarveBlock(blockState17, blockState18)) {
            return false;
        }
        if (y < 11) {
            chunk.setBlockState(pos1, Carver.LAVA.getBlockState(), false);
        }
        else {
            chunk.setBlockState(pos1, Carver.CAVE_AIR, false);
            if (atomicBoolean.get()) {
                pos3.set(pos1).setOffset(Direction.DOWN);
                if (chunk.getBlockState(pos3).getBlock() == Blocks.j) {
                    chunk.setBlockState(pos3, chunk.getBiome(pos1).getSurfaceConfig().getTopMaterial(), false);
                }
            }
        }
        return true;
    }
    
    public abstract boolean carve(final Chunk arg1, final Random arg2, final int arg3, final int arg4, final int arg5, final int arg6, final int arg7, final BitSet arg8, final C arg9);
    
    public abstract boolean shouldCarve(final Random arg1, final int arg2, final int arg3, final C arg4);
    
    protected boolean canAlwaysCarveBlock(final BlockState state) {
        return this.alwaysCarvableBlocks.contains(state.getBlock());
    }
    
    protected boolean canCarveBlock(final BlockState state, final BlockState stateAbove) {
        final Block block3 = state.getBlock();
        return this.canAlwaysCarveBlock(state) || ((block3 == Blocks.C || block3 == Blocks.E) && !stateAbove.getFluidState().matches(FluidTags.a));
    }
    
    protected boolean isRegionUncarvable(final Chunk chunk, final int mainChunkX, final int mainChunkZ, final int relMinX, final int relMaxX, final int minY, final int maxY, final int relMinZ, final int relMaxZ) {
        final BlockPos.Mutable mutable10 = new BlockPos.Mutable();
        for (int integer11 = relMinX; integer11 < relMaxX; ++integer11) {
            for (int integer12 = relMinZ; integer12 < relMaxZ; ++integer12) {
                for (int integer13 = minY - 1; integer13 <= maxY + 1; ++integer13) {
                    if (this.carvableFluids.contains(chunk.getFluidState(mutable10.set(integer11 + mainChunkX * 16, integer13, integer12 + mainChunkZ * 16)).getFluid())) {
                        return true;
                    }
                    if (integer13 != maxY + 1 && !this.isOnBoundary(relMinX, relMaxX, relMinZ, relMaxZ, integer11, integer12)) {
                        integer13 = maxY;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean isOnBoundary(final int minX, final int maxX, final int minZ, final int maxZ, final int x, final int z) {
        return x == minX || x == maxX - 1 || z == minZ || z == maxZ - 1;
    }
    
    protected boolean canCarveBranch(final int mainChunkX, final int mainChunkZ, final double relativeX, final double relativeZ, final int branch, final int branchCount, final float baseWidth) {
        final double double10 = mainChunkX * 16 + 8;
        final double double11 = mainChunkZ * 16 + 8;
        final double double12 = relativeX - double10;
        final double double13 = relativeZ - double11;
        final double double14 = branchCount - branch;
        final double double15 = baseWidth + 2.0f + 16.0f;
        return double12 * double12 + double13 * double13 - double14 * double14 <= double15 * double15;
    }
    
    protected abstract boolean isPositionExcluded(final double arg1, final double arg2, final double arg3, final int arg4);
    
    static {
        CAVE = Carver.<CarverConfig, CaveCarver>register("cave", new CaveCarver(ProbabilityConfig::deserialize, 256));
        NETHER_CAVE = Carver.<CarverConfig, NetherCaveCarver>register("hell_cave", new NetherCaveCarver(ProbabilityConfig::deserialize));
        RAVINE = Carver.<CarverConfig, RavineCarver>register("canyon", new RavineCarver(ProbabilityConfig::deserialize));
        UNDERWATER_RAVINE = Carver.<CarverConfig, UnderwaterRavineCarver>register("underwater_canyon", new UnderwaterRavineCarver(ProbabilityConfig::deserialize));
        UNDERWATER_CAVE = Carver.<CarverConfig, UnderwaterCaveCarver>register("underwater_cave", new UnderwaterCaveCarver(ProbabilityConfig::deserialize));
        AIR = Blocks.AIR.getDefaultState();
        CAVE_AIR = Blocks.kT.getDefaultState();
        WATER = Fluids.WATER.getDefaultState();
        LAVA = Fluids.LAVA.getDefaultState();
    }
}
