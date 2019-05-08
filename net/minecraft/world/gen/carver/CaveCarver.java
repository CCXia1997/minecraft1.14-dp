package net.minecraft.world.gen.carver;

import net.minecraft.util.math.MathHelper;
import java.util.BitSet;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.gen.ProbabilityConfig;

public class CaveCarver extends Carver<ProbabilityConfig>
{
    public CaveCarver(final Function<Dynamic<?>, ? extends ProbabilityConfig> configDeserializer, final int heightLimit) {
        super(configDeserializer, heightLimit);
    }
    
    @Override
    public boolean shouldCarve(final Random random, final int chunkX, final int chunkZ, final ProbabilityConfig config) {
        return random.nextFloat() <= config.probability;
    }
    
    @Override
    public boolean carve(final Chunk chunk, final Random random, final int seaLevel, final int chunkX, final int chunkZ, final int mainChunkX, final int mainChunkZ, final BitSet mask, final ProbabilityConfig config) {
        final int integer10 = (this.getBranchFactor() * 2 - 1) * 16;
        for (int integer11 = random.nextInt(random.nextInt(random.nextInt(this.getMaxCaveCount()) + 1) + 1), integer12 = 0; integer12 < integer11; ++integer12) {
            final double double13 = chunkX * 16 + random.nextInt(16);
            final double double14 = this.getCaveY(random);
            final double double15 = chunkZ * 16 + random.nextInt(16);
            int integer13 = 1;
            if (random.nextInt(4) == 0) {
                final double double16 = 0.5;
                final float float22 = 1.0f + random.nextFloat() * 6.0f;
                this.carveCave(chunk, random.nextLong(), seaLevel, mainChunkX, mainChunkZ, double13, double14, double15, float22, 0.5, mask);
                integer13 += random.nextInt(4);
            }
            for (int integer14 = 0; integer14 < integer13; ++integer14) {
                final float float23 = random.nextFloat() * 6.2831855f;
                final float float22 = (random.nextFloat() - 0.5f) / 4.0f;
                final float float24 = this.getTunnelSystemWidth(random);
                final int integer15 = integer10 - random.nextInt(integer10 / 4);
                final int integer16 = 0;
                this.carveTunnels(chunk, random.nextLong(), seaLevel, mainChunkX, mainChunkZ, double13, double14, double15, float24, float23, float22, 0, integer15, this.getTunnelSystemHeightWidthRatio(), mask);
            }
        }
        return true;
    }
    
    protected int getMaxCaveCount() {
        return 15;
    }
    
    protected float getTunnelSystemWidth(final Random random) {
        float float2 = random.nextFloat() * 2.0f + random.nextFloat();
        if (random.nextInt(10) == 0) {
            float2 *= random.nextFloat() * random.nextFloat() * 3.0f + 1.0f;
        }
        return float2;
    }
    
    protected double getTunnelSystemHeightWidthRatio() {
        return 1.0;
    }
    
    protected int getCaveY(final Random random) {
        return random.nextInt(random.nextInt(120) + 8);
    }
    
    protected void carveCave(final Chunk chunk, final long seed, final int seaLevel, final int mainChunkX, final int mainChunkZ, final double x, final double y, final double z, final float width, final double heightWidthRatio, final BitSet mask) {
        final double double17 = 1.5 + MathHelper.sin(1.5707964f) * width;
        final double double18 = double17 * heightWidthRatio;
        this.carveRegion(chunk, seed, seaLevel, mainChunkX, mainChunkZ, x + 1.0, y, z, double17, double18, mask);
    }
    
    protected void carveTunnels(final Chunk chunk, final long seed, final int seaLevel, final int mainChunkX, final int mainChunkZ, double x, double y, double z, final float baseWidth, float xzAngle, float yAngle, final int currentBranch, final int height, final double heightWidthRatio, final BitSet mask) {
        final Random random21 = new Random(seed);
        final int integer22 = random21.nextInt(height / 2) + height / 4;
        final boolean boolean23 = random21.nextInt(6) == 0;
        float float24 = 0.0f;
        float float25 = 0.0f;
        for (int integer23 = currentBranch; integer23 < height; ++integer23) {
            final double double27 = 1.5 + MathHelper.sin(3.1415927f * integer23 / height) * baseWidth;
            final double double28 = double27 * heightWidthRatio;
            final float float26 = MathHelper.cos(yAngle);
            x += MathHelper.cos(xzAngle) * float26;
            y += MathHelper.sin(yAngle);
            z += MathHelper.sin(xzAngle) * float26;
            yAngle *= (boolean23 ? 0.92f : 0.7f);
            yAngle += float25 * 0.1f;
            xzAngle += float24 * 0.1f;
            float25 *= 0.9f;
            float24 *= 0.75f;
            float25 += (random21.nextFloat() - random21.nextFloat()) * random21.nextFloat() * 2.0f;
            float24 += (random21.nextFloat() - random21.nextFloat()) * random21.nextFloat() * 4.0f;
            if (integer23 == integer22 && baseWidth > 1.0f) {
                this.carveTunnels(chunk, random21.nextLong(), seaLevel, mainChunkX, mainChunkZ, x, y, z, random21.nextFloat() * 0.5f + 0.5f, xzAngle - 1.5707964f, yAngle / 3.0f, integer23, height, 1.0, mask);
                this.carveTunnels(chunk, random21.nextLong(), seaLevel, mainChunkX, mainChunkZ, x, y, z, random21.nextFloat() * 0.5f + 0.5f, xzAngle + 1.5707964f, yAngle / 3.0f, integer23, height, 1.0, mask);
                return;
            }
            if (random21.nextInt(4) != 0) {
                if (!this.canCarveBranch(mainChunkX, mainChunkZ, x, z, integer23, height, baseWidth)) {
                    return;
                }
                this.carveRegion(chunk, seed, seaLevel, mainChunkX, mainChunkZ, x, y, z, double27, double28, mask);
            }
        }
    }
    
    @Override
    protected boolean isPositionExcluded(final double scaledRelativeX, final double scaledRelativeY, final double scaledRelativeZ, final int y) {
        return scaledRelativeY <= -0.7 || scaledRelativeX * scaledRelativeX + scaledRelativeY * scaledRelativeY + scaledRelativeZ * scaledRelativeZ >= 1.0;
    }
}
