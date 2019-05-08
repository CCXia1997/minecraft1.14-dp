package net.minecraft.world.gen.carver;

import net.minecraft.util.math.MathHelper;
import java.util.BitSet;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.gen.ProbabilityConfig;

public class RavineCarver extends Carver<ProbabilityConfig>
{
    private final float[] heightToHorizontalStretchFactor;
    
    public RavineCarver(final Function<Dynamic<?>, ? extends ProbabilityConfig> function) {
        super(function, 256);
        this.heightToHorizontalStretchFactor = new float[1024];
    }
    
    @Override
    public boolean shouldCarve(final Random random, final int chunkX, final int chunkZ, final ProbabilityConfig config) {
        return random.nextFloat() <= config.probability;
    }
    
    @Override
    public boolean carve(final Chunk chunk, final Random random, final int seaLevel, final int chunkX, final int chunkZ, final int mainChunkX, final int mainChunkZ, final BitSet mask, final ProbabilityConfig config) {
        final int integer10 = (this.getBranchFactor() * 2 - 1) * 16;
        final double double11 = chunkX * 16 + random.nextInt(16);
        final double double12 = random.nextInt(random.nextInt(40) + 8) + 20;
        final double double13 = chunkZ * 16 + random.nextInt(16);
        final float float17 = random.nextFloat() * 6.2831855f;
        final float float18 = (random.nextFloat() - 0.5f) * 2.0f / 8.0f;
        final double double14 = 3.0;
        final float float19 = (random.nextFloat() * 2.0f + random.nextFloat()) * 2.0f;
        final int integer11 = integer10 - random.nextInt(integer10 / 4);
        final int integer12 = 0;
        this.carveRavine(chunk, random.nextLong(), seaLevel, mainChunkX, mainChunkZ, double11, double12, double13, float19, float17, float18, 0, integer11, 3.0, mask);
        return true;
    }
    
    private void carveRavine(final Chunk chunk, final long seed, final int seaLevel, final int mainChunkX, final int mainChunkZ, double x, double y, double z, final float baseWidth, float xzAngle, float yAngle, final int branch, final int branchCount, final double heightWidthRatio, final BitSet mask) {
        final Random random21 = new Random(seed);
        float float22 = 1.0f;
        for (int integer23 = 0; integer23 < 256; ++integer23) {
            if (integer23 == 0 || random21.nextInt(3) == 0) {
                float22 = 1.0f + random21.nextFloat() * random21.nextFloat();
            }
            this.heightToHorizontalStretchFactor[integer23] = float22 * float22;
        }
        float float23 = 0.0f;
        float float24 = 0.0f;
        for (int integer24 = branch; integer24 < branchCount; ++integer24) {
            double double26 = 1.5 + MathHelper.sin(integer24 * 3.1415927f / branchCount) * baseWidth;
            double double27 = double26 * heightWidthRatio;
            double26 *= random21.nextFloat() * 0.25 + 0.75;
            double27 *= random21.nextFloat() * 0.25 + 0.75;
            final float float25 = MathHelper.cos(yAngle);
            final float float26 = MathHelper.sin(yAngle);
            x += MathHelper.cos(xzAngle) * float25;
            y += float26;
            z += MathHelper.sin(xzAngle) * float25;
            yAngle *= 0.7f;
            yAngle += float24 * 0.05f;
            xzAngle += float23 * 0.05f;
            float24 *= 0.8f;
            float23 *= 0.5f;
            float24 += (random21.nextFloat() - random21.nextFloat()) * random21.nextFloat() * 2.0f;
            float23 += (random21.nextFloat() - random21.nextFloat()) * random21.nextFloat() * 4.0f;
            if (random21.nextInt(4) != 0) {
                if (!this.canCarveBranch(mainChunkX, mainChunkZ, x, z, integer24, branchCount, baseWidth)) {
                    return;
                }
                this.carveRegion(chunk, seed, seaLevel, mainChunkX, mainChunkZ, x, y, z, double26, double27, mask);
            }
        }
    }
    
    @Override
    protected boolean isPositionExcluded(final double scaledRelativeX, final double scaledRelativeY, final double scaledRelativeZ, final int y) {
        return (scaledRelativeX * scaledRelativeX + scaledRelativeZ * scaledRelativeZ) * this.heightToHorizontalStretchFactor[y - 1] + scaledRelativeY * scaledRelativeY / 6.0 >= 1.0;
    }
}
