package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.util.math.MutableIntBoundingBox;
import com.google.common.collect.Lists;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.biome.Biome;
import java.util.List;

public class SwampHutFeature extends AbstractTempleFeature<DefaultFeatureConfig>
{
    private static final List<Biome.SpawnEntry> MONSTER_SPAWNS;
    private static final List<Biome.SpawnEntry> CREATURE_SPAWNS;
    
    public SwampHutFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public String getName() {
        return "Swamp_Hut";
    }
    
    @Override
    public int getRadius() {
        return 3;
    }
    
    @Override
    public StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }
    
    @Override
    protected int getSeedModifier() {
        return 14357620;
    }
    
    @Override
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return SwampHutFeature.MONSTER_SPAWNS;
    }
    
    @Override
    public List<Biome.SpawnEntry> getCreatureSpawns() {
        return SwampHutFeature.CREATURE_SPAWNS;
    }
    
    public boolean c(final IWorld iWorld, final BlockPos blockPos) {
        final StructureStart structureStart3 = this.isInsideStructure(iWorld, blockPos, true);
        if (structureStart3 == StructureStart.DEFAULT || !(structureStart3 instanceof Start) || structureStart3.getChildren().isEmpty()) {
            return false;
        }
        final StructurePiece structurePiece4 = structureStart3.getChildren().get(0);
        return structurePiece4 instanceof SwampHutGenerator;
    }
    
    static {
        MONSTER_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.WITCH, 1, 1, 1));
        CREATURE_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.CAT, 1, 1, 1));
    }
    
    public static class Start extends StructureStart
    {
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            final SwampHutGenerator swampHutGenerator6 = new SwampHutGenerator(this.random, x * 16, z * 16);
            this.children.add(swampHutGenerator6);
            this.setBoundingBoxFromChildren();
        }
    }
}
