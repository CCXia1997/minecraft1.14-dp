package net.minecraft.world.gen.feature;

import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class OceanRuinFeature extends AbstractTempleFeature<OceanRuinFeatureConfig>
{
    public OceanRuinFeature(final Function<Dynamic<?>, ? extends OceanRuinFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public String getName() {
        return "Ocean_Ruin";
    }
    
    @Override
    public int getRadius() {
        return 3;
    }
    
    @Override
    protected int getSpacing(final ChunkGenerator<?> chunkGenerator) {
        return ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getOceanRuinSpacing();
    }
    
    @Override
    protected int getSeparation(final ChunkGenerator<?> chunkGenerator) {
        return ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getOceanRuinSeparation();
    }
    
    @Override
    public StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }
    
    @Override
    protected int getSeedModifier() {
        return 14357621;
    }
    
    public static class Start extends StructureStart
    {
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            final OceanRuinFeatureConfig oceanRuinFeatureConfig6 = chunkGenerator.<OceanRuinFeatureConfig>getStructureConfig(biome, Feature.OCEAN_RUIN);
            final int integer7 = x * 16;
            final int integer8 = z * 16;
            final BlockPos blockPos9 = new BlockPos(integer7, 90, integer8);
            final BlockRotation blockRotation10 = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
            OceanRuinGenerator.addPieces(structureManager, blockPos9, blockRotation10, this.children, this.random, oceanRuinFeatureConfig6);
            this.setBoundingBoxFromChildren();
        }
    }
    
    public enum BiomeType
    {
        WARM("warm"), 
        COLD("cold");
        
        private static final Map<String, BiomeType> nameMap;
        private final String name;
        
        private BiomeType(final String string1) {
            this.name = string1;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static BiomeType byName(final String name) {
            return BiomeType.nameMap.get(name);
        }
        
        static {
            nameMap = Arrays.<BiomeType>stream(values()).collect(Collectors.toMap(BiomeType::getName, biomeType -> biomeType));
        }
    }
}
