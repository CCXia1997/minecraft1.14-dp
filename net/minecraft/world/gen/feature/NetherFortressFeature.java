package net.minecraft.world.gen.feature;

import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.structure.StructureStart;
import com.google.common.collect.Lists;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.biome.Biome;
import java.util.List;

public class NetherFortressFeature extends StructureFeature<DefaultFeatureConfig>
{
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS;
    
    public NetherFortressFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean shouldStartAt(final ChunkGenerator<?> chunkGenerator, final Random random, final int chunkX, final int chunkZ) {
        final int integer5 = chunkX >> 4;
        final int integer6 = chunkZ >> 4;
        random.setSeed((long)(integer5 ^ integer6 << 4) ^ chunkGenerator.getSeed());
        random.nextInt();
        if (random.nextInt(3) != 0) {
            return false;
        }
        if (chunkX != (integer5 << 4) + 4 + random.nextInt(8)) {
            return false;
        }
        if (chunkZ != (integer6 << 4) + 4 + random.nextInt(8)) {
            return false;
        }
        final Biome biome7 = chunkGenerator.getBiomeSource().getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
        return chunkGenerator.hasStructure(biome7, Feature.NETHER_BRIDGE);
    }
    
    @Override
    public StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }
    
    @Override
    public String getName() {
        return "Fortress";
    }
    
    @Override
    public int getRadius() {
        return 8;
    }
    
    @Override
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return NetherFortressFeature.MONSTER_SPAWNS;
    }
    
    static {
        MONSTER_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.BLAZE, 10, 2, 3), new Biome.SpawnEntry(EntityType.ZOMBIE_PIGMAN, 5, 4, 4), new Biome.SpawnEntry(EntityType.WITHER_SKELETON, 8, 5, 5), new Biome.SpawnEntry(EntityType.SKELETON, 2, 5, 5), new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 3, 4, 4));
    }
    
    public static class Start extends StructureStart
    {
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            final NetherFortressGenerator.Start start6 = new NetherFortressGenerator.Start(this.random, (x << 4) + 2, (z << 4) + 2);
            this.children.add(start6);
            start6.a(start6, this.children, this.random);
            final List<StructurePiece> list7 = start6.d;
            while (!list7.isEmpty()) {
                final int integer8 = this.random.nextInt(list7.size());
                final StructurePiece structurePiece9 = list7.remove(integer8);
                structurePiece9.a(start6, this.children, this.random);
            }
            this.setBoundingBoxFromChildren();
            this.a(this.random, 48, 70);
        }
    }
}
