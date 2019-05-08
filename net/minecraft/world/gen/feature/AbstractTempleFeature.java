package net.minecraft.world.gen.feature;

import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.chunk.ChunkPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public abstract class AbstractTempleFeature<C extends FeatureConfig> extends StructureFeature<C>
{
    public AbstractTempleFeature(final Function<Dynamic<?>, ? extends C> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    protected ChunkPos getStart(final ChunkGenerator<?> chunkGenerator, final Random random, final int integer3, final int integer4, final int integer5, final int integer6) {
        final int integer7 = this.getSpacing(chunkGenerator);
        final int integer8 = this.getSeparation(chunkGenerator);
        final int integer9 = integer3 + integer7 * integer5;
        final int integer10 = integer4 + integer7 * integer6;
        final int integer11 = (integer9 < 0) ? (integer9 - integer7 + 1) : integer9;
        final int integer12 = (integer10 < 0) ? (integer10 - integer7 + 1) : integer10;
        int integer13 = integer11 / integer7;
        int integer14 = integer12 / integer7;
        ((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), integer13, integer14, this.getSeedModifier());
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
            final Biome biome6 = chunkGenerator.getBiomeSource().getBiome(new BlockPos(chunkX * 16 + 9, 0, chunkZ * 16 + 9));
            if (chunkGenerator.hasStructure(biome6, this)) {
                return true;
            }
        }
        return false;
    }
    
    protected int getSpacing(final ChunkGenerator<?> chunkGenerator) {
        return ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getTempleDistance();
    }
    
    protected int getSeparation(final ChunkGenerator<?> chunkGenerator) {
        return ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getTempleSeparation();
    }
    
    protected abstract int getSeedModifier();
}
