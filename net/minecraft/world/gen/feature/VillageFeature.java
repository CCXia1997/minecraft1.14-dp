package net.minecraft.world.gen.feature;

import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.chunk.ChunkPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class VillageFeature extends StructureFeature<VillageFeatureConfig>
{
    public VillageFeature(final Function<Dynamic<?>, ? extends VillageFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    protected ChunkPos getStart(final ChunkGenerator<?> chunkGenerator, final Random random, final int integer3, final int integer4, final int integer5, final int integer6) {
        final int integer7 = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getVillageDistance();
        final int integer8 = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getVillageSeparation();
        final int integer9 = integer3 + integer7 * integer5;
        final int integer10 = integer4 + integer7 * integer6;
        final int integer11 = (integer9 < 0) ? (integer9 - integer7 + 1) : integer9;
        final int integer12 = (integer10 < 0) ? (integer10 - integer7 + 1) : integer10;
        int integer13 = integer11 / integer7;
        int integer14 = integer12 / integer7;
        ((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), integer13, integer14, 10387312);
        integer13 *= integer7;
        integer14 *= integer7;
        integer13 += random.nextInt(integer7 - integer8);
        integer14 += random.nextInt(integer7 - integer8);
        return new ChunkPos(integer13, integer14);
    }
    
    @Override
    public boolean shouldStartAt(final ChunkGenerator<?> chunkGenerator, final Random random, final int chunkX, final int chunkZ) {
        final ChunkPos chunkPos5 = this.getStart(chunkGenerator, random, chunkX, chunkZ, 0, 0);
        if (chunkX == chunkPos5.x && chunkZ == chunkPos5.z) {
            final Biome biome6 = chunkGenerator.getBiomeSource().getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return chunkGenerator.hasStructure(biome6, Feature.VILLAGE);
        }
        return false;
    }
    
    @Override
    public StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }
    
    @Override
    public String getName() {
        return "Village";
    }
    
    @Override
    public int getRadius() {
        return 8;
    }
    
    public static class Start extends VillageStructureStart
    {
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            final VillageFeatureConfig villageFeatureConfig6 = chunkGenerator.<VillageFeatureConfig>getStructureConfig(biome, Feature.VILLAGE);
            final BlockPos blockPos7 = new BlockPos(x * 16, 0, z * 16);
            VillageGenerator.addPieces(chunkGenerator, structureManager, blockPos7, this.children, this.random, villageFeatureConfig6);
            this.setBoundingBoxFromChildren();
        }
    }
}
