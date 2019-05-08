package net.minecraft.world.gen.feature;

import java.util.Iterator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.structure.StructureStart;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class MineshaftFeature extends StructureFeature<MineshaftFeatureConfig>
{
    public MineshaftFeature(final Function<Dynamic<?>, ? extends MineshaftFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    public boolean shouldStartAt(final ChunkGenerator<?> chunkGenerator, final Random random, final int chunkX, final int chunkZ) {
        ((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), chunkX, chunkZ);
        final Biome biome5 = chunkGenerator.getBiomeSource().getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
        if (chunkGenerator.hasStructure(biome5, Feature.MINESHAFT)) {
            final MineshaftFeatureConfig mineshaftFeatureConfig6 = chunkGenerator.<MineshaftFeatureConfig>getStructureConfig(biome5, Feature.MINESHAFT);
            final double double7 = mineshaftFeatureConfig6.probability;
            return random.nextDouble() < double7;
        }
        return false;
    }
    
    @Override
    public StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }
    
    @Override
    public String getName() {
        return "Mineshaft";
    }
    
    @Override
    public int getRadius() {
        return 8;
    }
    
    public enum Type
    {
        NORMAL("normal"), 
        MESA("mesa");
        
        private static final Map<String, Type> nameMap;
        private final String name;
        
        private Type(final String string1) {
            this.name = string1;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static Type byName(final String nam) {
            return Type.nameMap.get(nam);
        }
        
        public static Type byIndex(final int index) {
            if (index < 0 || index >= values().length) {
                return Type.NORMAL;
            }
            return values()[index];
        }
        
        static {
            nameMap = Arrays.<Type>stream(values()).collect(Collectors.toMap(Type::getName, type -> type));
        }
    }
    
    public static class Start extends StructureStart
    {
        public Start(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
            super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
        }
        
        @Override
        public void initialize(final ChunkGenerator<?> chunkGenerator, final StructureManager structureManager, final int x, final int z, final Biome biome) {
            final MineshaftFeatureConfig mineshaftFeatureConfig6 = chunkGenerator.<MineshaftFeatureConfig>getStructureConfig(biome, Feature.MINESHAFT);
            final MineshaftGenerator.MineshaftRoom mineshaftRoom7 = new MineshaftGenerator.MineshaftRoom(0, this.random, (x << 4) + 2, (z << 4) + 2, mineshaftFeatureConfig6.type);
            this.children.add(mineshaftRoom7);
            mineshaftRoom7.a(mineshaftRoom7, this.children, this.random);
            this.setBoundingBoxFromChildren();
            if (mineshaftFeatureConfig6.type == Type.MESA) {
                final int integer8 = -5;
                final int integer9 = chunkGenerator.getSeaLevel() - this.boundingBox.maxY + this.boundingBox.getBlockCountY() / 2 + 5;
                this.boundingBox.translate(0, integer9, 0);
                for (final StructurePiece structurePiece11 : this.children) {
                    structurePiece11.translate(0, integer9, 0);
                }
            }
            else {
                this.a(chunkGenerator.getSeaLevel(), this.random, 10);
            }
        }
    }
}
