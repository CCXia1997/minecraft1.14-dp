package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.structure.StructureStart;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DesertPyramidFeature extends AbstractTempleFeature<DefaultFeatureConfig>
{
    public DesertPyramidFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public String getName() {
        return "Desert_Pyramid";
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
        return 14357617;
    }
    
    public static class Start extends StructureStart
    {
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            final DesertTempleGenerator desertTempleGenerator6 = new DesertTempleGenerator(this.random, x * 16, z * 16);
            this.children.add(desertTempleGenerator6);
            this.setBoundingBoxFromChildren();
        }
    }
}
