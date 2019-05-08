package net.minecraft.world.biome.source;

import com.google.common.collect.Lists;
import net.minecraft.world.biome.Biomes;
import javax.annotation.Nullable;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import java.util.Set;
import net.minecraft.world.gen.feature.StructureFeature;
import java.util.Map;
import net.minecraft.world.biome.Biome;
import java.util.List;

public abstract class BiomeSource
{
    private static final List<Biome> SPAWN_BIOMES;
    protected final Map<StructureFeature<?>, Boolean> structureFeatures;
    protected final Set<BlockState> topMaterials;
    
    protected BiomeSource() {
        this.structureFeatures = Maps.newHashMap();
        this.topMaterials = Sets.newHashSet();
    }
    
    public List<Biome> getSpawnBiomes() {
        return BiomeSource.SPAWN_BIOMES;
    }
    
    public Biome getBiome(final BlockPos pos) {
        return this.getBiome(pos.getX(), pos.getZ());
    }
    
    public abstract Biome getBiome(final int arg1, final int arg2);
    
    public Biome getBiomeForNoiseGen(final int x, final int z) {
        return this.getBiome(x << 2, z << 2);
    }
    
    public Biome[] sampleBiomes(final int x, final int z, final int width, final int height) {
        return this.sampleBiomes(x, z, width, height, true);
    }
    
    public abstract Biome[] sampleBiomes(final int arg1, final int arg2, final int arg3, final int arg4, final boolean arg5);
    
    public abstract Set<Biome> getBiomesInArea(final int arg1, final int arg2, final int arg3);
    
    @Nullable
    public abstract BlockPos locateBiome(final int arg1, final int arg2, final int arg3, final List<Biome> arg4, final Random arg5);
    
    public float c(final int integer1, final int integer2) {
        return 0.0f;
    }
    
    public abstract boolean hasStructureFeature(final StructureFeature<?> arg1);
    
    public abstract Set<BlockState> getTopMaterials();
    
    static {
        SPAWN_BIOMES = Lists.<Biome>newArrayList(Biomes.f, Biomes.c, Biomes.g, Biomes.u, Biomes.t, Biomes.w, Biomes.x);
    }
}
