package net.minecraft.world.dimension;

import net.minecraft.world.border.WorldBorder;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TheNetherDimension extends Dimension
{
    public TheNetherDimension(final World world, final DimensionType type) {
        super(world, type);
        this.waterVaporizes = true;
        this.isNether = true;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public Vec3d getFogColor(final float skyAngle, final float tickDelta) {
        return new Vec3d(0.20000000298023224, 0.029999999329447746, 0.029999999329447746);
    }
    
    @Override
    protected void initializeLightLevelToBrightness() {
        final float float1 = 0.1f;
        for (int integer2 = 0; integer2 <= 15; ++integer2) {
            final float float2 = 1.0f - integer2 / 15.0f;
            this.lightLevelToBrightness[integer2] = (1.0f - float2) / (float2 * 3.0f + 1.0f) * 0.9f + 0.1f;
        }
    }
    
    @Override
    public ChunkGenerator<?> createChunkGenerator() {
        final CavesChunkGeneratorConfig cavesChunkGeneratorConfig1 = ChunkGeneratorType.b.createSettings();
        cavesChunkGeneratorConfig1.setDefaultBlock(Blocks.cJ.getDefaultState());
        cavesChunkGeneratorConfig1.setDefaultFluid(Blocks.B.getDefaultState());
        return ChunkGeneratorType.b.create(this.world, BiomeSourceType.FIXED.applyConfig(BiomeSourceType.FIXED.getConfig().setBiome(Biomes.j)), cavesChunkGeneratorConfig1);
    }
    
    @Override
    public boolean hasVisibleSky() {
        return false;
    }
    
    @Nullable
    @Override
    public BlockPos getSpawningBlockInChunk(final ChunkPos chunkPos, final boolean checkMobSpawnValidity) {
        return null;
    }
    
    @Nullable
    @Override
    public BlockPos getTopSpawningBlockPosition(final int x, final int z, final boolean checkMobSpawnValidity) {
        return null;
    }
    
    @Override
    public float getSkyAngle(final long timeOfDay, final float delta) {
        return 0.5f;
    }
    
    @Override
    public boolean canPlayersSleep() {
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderFog(final int entityX, final int entityZ) {
        return true;
    }
    
    @Override
    public WorldBorder createWorldBorder() {
        return new WorldBorder() {
            @Override
            public double getCenterX() {
                return super.getCenterX() / 8.0;
            }
            
            @Override
            public double getCenterZ() {
                return super.getCenterZ() / 8.0;
            }
        };
    }
    
    @Override
    public DimensionType getType() {
        return DimensionType.b;
    }
}
