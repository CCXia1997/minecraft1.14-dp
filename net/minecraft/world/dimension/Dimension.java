package net.minecraft.world.dimension;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class Dimension
{
    public static final float[] MOON_PHASE_TO_SIZE;
    protected final World world;
    private final DimensionType type;
    protected boolean waterVaporizes;
    protected boolean isNether;
    protected final float[] lightLevelToBrightness;
    private final float[] backgroundColor;
    
    public Dimension(final World world, final DimensionType type) {
        this.lightLevelToBrightness = new float[16];
        this.backgroundColor = new float[4];
        this.world = world;
        this.type = type;
        this.initializeLightLevelToBrightness();
    }
    
    protected void initializeLightLevelToBrightness() {
        final float float1 = 0.0f;
        for (int integer2 = 0; integer2 <= 15; ++integer2) {
            final float float2 = 1.0f - integer2 / 15.0f;
            this.lightLevelToBrightness[integer2] = (1.0f - float2) / (float2 * 3.0f + 1.0f) * 1.0f + 0.0f;
        }
    }
    
    public int getMoonPhase(final long time) {
        return (int)(time / 24000L % 8L + 8L) % 8;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public float[] getBackgroundColor(final float skyAngle, final float tickDelta) {
        final float float3 = 0.4f;
        final float float4 = MathHelper.cos(skyAngle * 6.2831855f) - 0.0f;
        final float float5 = -0.0f;
        if (float4 >= -0.4f && float4 <= 0.4f) {
            final float float6 = (float4 + 0.0f) / 0.4f * 0.5f + 0.5f;
            float float7 = 1.0f - (1.0f - MathHelper.sin(float6 * 3.1415927f)) * 0.99f;
            float7 *= float7;
            this.backgroundColor[0] = float6 * 0.3f + 0.7f;
            this.backgroundColor[1] = float6 * float6 * 0.7f + 0.2f;
            this.backgroundColor[2] = float6 * float6 * 0.0f + 0.2f;
            this.backgroundColor[3] = float7;
            return this.backgroundColor;
        }
        return null;
    }
    
    @Environment(EnvType.CLIENT)
    public float getCloudHeight() {
        return 128.0f;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean c() {
        return true;
    }
    
    @Nullable
    public BlockPos getForcedSpawnPoint() {
        return null;
    }
    
    @Environment(EnvType.CLIENT)
    public double getHorizonShadingRatio() {
        if (this.world.getLevelProperties().getGeneratorType() == LevelGeneratorType.FLAT) {
            return 1.0;
        }
        return 0.03125;
    }
    
    public boolean doesWaterVaporize() {
        return this.waterVaporizes;
    }
    
    public boolean hasSkyLight() {
        return this.type.hasSkyLight();
    }
    
    public boolean isNether() {
        return this.isNether;
    }
    
    public float[] getLightLevelToBrightness() {
        return this.lightLevelToBrightness;
    }
    
    public WorldBorder createWorldBorder() {
        return new WorldBorder();
    }
    
    public void saveWorldData() {
    }
    
    public void update() {
    }
    
    public abstract ChunkGenerator<?> createChunkGenerator();
    
    @Nullable
    public abstract BlockPos getSpawningBlockInChunk(final ChunkPos arg1, final boolean arg2);
    
    @Nullable
    public abstract BlockPos getTopSpawningBlockPosition(final int arg1, final int arg2, final boolean arg3);
    
    public abstract float getSkyAngle(final long arg1, final float arg2);
    
    public abstract boolean hasVisibleSky();
    
    @Environment(EnvType.CLIENT)
    public abstract Vec3d getFogColor(final float arg1, final float arg2);
    
    public abstract boolean canPlayersSleep();
    
    @Environment(EnvType.CLIENT)
    public abstract boolean shouldRenderFog(final int arg1, final int arg2);
    
    public abstract DimensionType getType();
    
    static {
        MOON_PHASE_TO_SIZE = new float[] { 1.0f, 0.75f, 0.5f, 0.25f, 0.0f, 0.25f, 0.5f, 0.75f };
    }
}
