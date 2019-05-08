package net.minecraft.world.gen.feature;

import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.structure.VillageStructureStart;
import com.google.common.collect.Lists;
import net.minecraft.entity.EntityType;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.biome.Biome;
import java.util.List;

public class PillagerOutpostFeature extends AbstractTempleFeature<PillagerOutpostFeatureConfig>
{
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS;
    
    public PillagerOutpostFeature(final Function<Dynamic<?>, ? extends PillagerOutpostFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public String getName() {
        return "Pillager_Outpost";
    }
    
    @Override
    public int getRadius() {
        return 3;
    }
    
    @Override
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return PillagerOutpostFeature.MONSTER_SPAWNS;
    }
    
    @Override
    public boolean shouldStartAt(final ChunkGenerator<?> chunkGenerator, final Random random, final int chunkX, final int chunkZ) {
        final ChunkPos chunkPos5 = this.getStart(chunkGenerator, random, chunkX, chunkZ, 0, 0);
        if (chunkX == chunkPos5.x && chunkZ == chunkPos5.z) {
            final int integer6 = chunkX >> 4;
            final int integer7 = chunkZ >> 4;
            random.setSeed((long)(integer6 ^ integer7 << 4) ^ chunkGenerator.getSeed());
            random.nextInt();
            if (random.nextInt(5) != 0) {
                return false;
            }
            final Biome biome8 = chunkGenerator.getBiomeSource().getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            if (chunkGenerator.hasStructure(biome8, Feature.PILLAGER_OUTPOST)) {
                for (int integer8 = chunkX - 10; integer8 <= chunkX + 10; ++integer8) {
                    for (int integer9 = chunkZ - 10; integer9 <= chunkZ + 10; ++integer9) {
                        if (Feature.VILLAGE.shouldStartAt(chunkGenerator, random, integer8, integer9)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }
    
    @Override
    protected int getSeedModifier() {
        return 165745296;
    }
    
    static {
        MONSTER_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.PILLAGER, 1, 1, 1));
    }
    
    public static class Start extends VillageStructureStart
    {
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            final BlockPos blockPos6 = new BlockPos(x * 16, 90, z * 16);
            PillagerOutpostGenerator.addPieces(chunkGenerator, structureManager, blockPos6, this.children, this.random);
            this.setBoundingBoxFromChildren();
        }
    }
}
