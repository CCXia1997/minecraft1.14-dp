package net.minecraft.world.gen.feature;

import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.Heightmap;
import net.minecraft.util.BlockRotation;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.chunk.ChunkPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class EndCityFeature extends StructureFeature<DefaultFeatureConfig>
{
    public EndCityFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    protected ChunkPos getStart(final ChunkGenerator<?> chunkGenerator, final Random random, final int integer3, final int integer4, final int integer5, final int integer6) {
        final int integer7 = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getEndCityDistance();
        final int integer8 = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getEndCitySeparation();
        final int integer9 = integer3 + integer7 * integer5;
        final int integer10 = integer4 + integer7 * integer6;
        final int integer11 = (integer9 < 0) ? (integer9 - integer7 + 1) : integer9;
        final int integer12 = (integer10 < 0) ? (integer10 - integer7 + 1) : integer10;
        int integer13 = integer11 / integer7;
        int integer14 = integer12 / integer7;
        ((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), integer13, integer14, 10387313);
        integer13 *= integer7;
        integer14 *= integer7;
        integer13 += (random.nextInt(integer7 - integer8) + random.nextInt(integer7 - integer8)) / 2;
        integer14 += (random.nextInt(integer7 - integer8) + random.nextInt(integer7 - integer8)) / 2;
        return new ChunkPos(integer13, integer14);
    }
    
    @Override
    public boolean shouldStartAt(final ChunkGenerator<?> chunkGenerator, final Random random, final int chunkX, final int chunkZ) {
        final ChunkPos chunkPos5 = this.getStart(chunkGenerator, random, chunkX, chunkZ, 0, 0);
        if (chunkX != chunkPos5.x || chunkZ != chunkPos5.z) {
            return false;
        }
        final Biome biome6 = chunkGenerator.getBiomeSource().getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
        if (!chunkGenerator.hasStructure(biome6, Feature.END_CITY)) {
            return false;
        }
        final int integer7 = getGenerationHeight(chunkX, chunkZ, chunkGenerator);
        return integer7 >= 60;
    }
    
    @Override
    public StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }
    
    @Override
    public String getName() {
        return "EndCity";
    }
    
    @Override
    public int getRadius() {
        return 8;
    }
    
    private static int getGenerationHeight(final int chunkX, final int chunkZ, final ChunkGenerator<?> chunkGenerator) {
        final Random random4 = new Random(chunkX + chunkZ * 10387313);
        final BlockRotation blockRotation5 = BlockRotation.values()[random4.nextInt(BlockRotation.values().length)];
        int integer6 = 5;
        int integer7 = 5;
        if (blockRotation5 == BlockRotation.ROT_90) {
            integer6 = -5;
        }
        else if (blockRotation5 == BlockRotation.ROT_180) {
            integer6 = -5;
            integer7 = -5;
        }
        else if (blockRotation5 == BlockRotation.ROT_270) {
            integer7 = -5;
        }
        final int integer8 = (chunkX << 4) + 7;
        final int integer9 = (chunkZ << 4) + 7;
        final int integer10 = chunkGenerator.getHeightInGround(integer8, integer9, Heightmap.Type.a);
        final int integer11 = chunkGenerator.getHeightInGround(integer8, integer9 + integer7, Heightmap.Type.a);
        final int integer12 = chunkGenerator.getHeightInGround(integer8 + integer6, integer9, Heightmap.Type.a);
        final int integer13 = chunkGenerator.getHeightInGround(integer8 + integer6, integer9 + integer7, Heightmap.Type.a);
        return Math.min(Math.min(integer10, integer11), Math.min(integer12, integer13));
    }
    
    public static class Start extends StructureStart
    {
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            final BlockRotation blockRotation6 = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
            final int integer7 = getGenerationHeight(x, z, chunkGenerator);
            if (integer7 < 60) {
                return;
            }
            final BlockPos blockPos8 = new BlockPos(x * 16 + 8, integer7, z * 16 + 8);
            EndCityGenerator.addPieces(structureManager, blockPos8, blockRotation6, this.children, this.random);
            this.setBoundingBoxFromChildren();
        }
    }
}
