package net.minecraft.world.gen.feature;

import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.IWorld;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.util.math.Direction;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.structure.StructureStart;
import com.google.common.collect.Lists;
import net.minecraft.entity.EntityType;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.chunk.ChunkPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.biome.Biome;
import java.util.List;

public class OceanMonumentFeature extends StructureFeature<DefaultFeatureConfig>
{
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS;
    
    public OceanMonumentFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    protected ChunkPos getStart(final ChunkGenerator<?> chunkGenerator, final Random random, final int integer3, final int integer4, final int integer5, final int integer6) {
        final int integer7 = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getOceanMonumentSpacing();
        final int integer8 = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getOceanMonumentSeparation();
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
        if (chunkX == chunkPos5.x && chunkZ == chunkPos5.z) {
            final Set<Biome> set6 = chunkGenerator.getBiomeSource().getBiomesInArea(chunkX * 16 + 9, chunkZ * 16 + 9, 16);
            for (final Biome biome8 : set6) {
                if (!chunkGenerator.hasStructure(biome8, Feature.OCEAN_MONUMENT)) {
                    return false;
                }
            }
            final Set<Biome> set7 = chunkGenerator.getBiomeSource().getBiomesInArea(chunkX * 16 + 9, chunkZ * 16 + 9, 29);
            for (final Biome biome9 : set7) {
                if (biome9.getCategory() != Biome.Category.OCEAN && biome9.getCategory() != Biome.Category.RIVER) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }
    
    @Override
    public String getName() {
        return "Monument";
    }
    
    @Override
    public int getRadius() {
        return 8;
    }
    
    @Override
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return OceanMonumentFeature.MONSTER_SPAWNS;
    }
    
    static {
        MONSTER_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4));
    }
    
    public static class Start extends StructureStart
    {
        private boolean e;
        
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            this.b(x, z);
        }
        
        private void b(final int chunkX, final int chunkZ) {
            final int integer3 = chunkX * 16 - 29;
            final int integer4 = chunkZ * 16 - 29;
            final Direction direction5 = Direction.Type.HORIZONTAL.random(this.random);
            this.children.add(new OceanMonumentGenerator.Base(this.random, integer3, integer4, direction5));
            this.setBoundingBoxFromChildren();
            this.e = true;
        }
        
        @Override
        public void generateStructure(final IWorld world, final Random random, final MutableIntBoundingBox boundingBox, final ChunkPos pos) {
            if (!this.e) {
                this.children.clear();
                this.b(this.getChunkX(), this.getChunkZ());
            }
            super.generateStructure(world, random, boundingBox, pos);
        }
    }
}
