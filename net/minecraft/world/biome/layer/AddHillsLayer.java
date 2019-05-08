package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biomes;
import org.apache.logging.log4j.LogManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.Logger;

public enum AddHillsLayer implements MergingLayer, NorthWestCoordinateTransformer
{
    a;
    
    private static final Logger LOGGER;
    private static final int BIRCH_FOREST_ID;
    private static final int BIRCH_FOREST_HILLS_ID;
    private static final int DESERT_ID;
    private static final int DESERT_HILLS_ID;
    private static final int MOUNTAINS_ID;
    private static final int WOODED_MOUNTAINS_ID;
    private static final int FOREST_ID;
    private static final int WOODED_HILLS_ID;
    private static final int SNOWY_TUNDRA_ID;
    private static final int SNOWY_MOUNTAINS_ID;
    private static final int JUNGLE_ID;
    private static final int JUNGLE_HILLS_ID;
    private static final int BAMBOO_JUNGLE_ID;
    private static final int BAMBOO_JUNGLE_HILLS_ID;
    private static final int BADLANDS_ID;
    private static final int WOODED_BADLANDS_PLATEAU_ID;
    private static final int PLAINS_ID;
    private static final int GIANT_TREE_TAIGA_ID;
    private static final int GIANT_TREE_TAIGA_HILLS_ID;
    private static final int DARK_FOREST_ID;
    private static final int SAVANNA_ID;
    private static final int SAVANNA_PLATEAU_ID;
    private static final int TAIGA_ID;
    private static final int SNOWY_TAIGA_ID;
    private static final int SNOWY_TAIGA_HILLS_ID;
    private static final int TAIGA_HILLS_ID;
    
    @Override
    public int sample(final LayerRandomnessSource context, final LayerSampler sampler1, final LayerSampler sampler2, final int x, final int z) {
        final int integer6 = sampler1.sample(this.transformX(x + 1), this.transformZ(z + 1));
        final int integer7 = sampler2.sample(this.transformX(x + 1), this.transformZ(z + 1));
        if (integer6 > 255) {
            AddHillsLayer.LOGGER.debug("old! {}", integer6);
        }
        final int integer8 = (integer7 - 2) % 29;
        if (!BiomeLayers.isShallowOcean(integer6) && integer7 >= 2 && integer8 == 1) {
            final Biome biome9 = Registry.BIOME.get(integer6);
            if (biome9 == null || !biome9.hasParent()) {
                final Biome biome10 = Biome.getParentBiome(biome9);
                return (biome10 == null) ? integer6 : Registry.BIOME.getRawId(biome10);
            }
        }
        if (context.nextInt(3) == 0 || integer8 == 0) {
            int integer9;
            if ((integer9 = integer6) == AddHillsLayer.DESERT_ID) {
                integer9 = AddHillsLayer.DESERT_HILLS_ID;
            }
            else if (integer6 == AddHillsLayer.FOREST_ID) {
                integer9 = AddHillsLayer.WOODED_HILLS_ID;
            }
            else if (integer6 == AddHillsLayer.BIRCH_FOREST_ID) {
                integer9 = AddHillsLayer.BIRCH_FOREST_HILLS_ID;
            }
            else if (integer6 == AddHillsLayer.DARK_FOREST_ID) {
                integer9 = AddHillsLayer.PLAINS_ID;
            }
            else if (integer6 == AddHillsLayer.TAIGA_ID) {
                integer9 = AddHillsLayer.TAIGA_HILLS_ID;
            }
            else if (integer6 == AddHillsLayer.GIANT_TREE_TAIGA_ID) {
                integer9 = AddHillsLayer.GIANT_TREE_TAIGA_HILLS_ID;
            }
            else if (integer6 == AddHillsLayer.SNOWY_TAIGA_ID) {
                integer9 = AddHillsLayer.SNOWY_TAIGA_HILLS_ID;
            }
            else if (integer6 == AddHillsLayer.PLAINS_ID) {
                integer9 = ((context.nextInt(3) == 0) ? AddHillsLayer.WOODED_HILLS_ID : AddHillsLayer.FOREST_ID);
            }
            else if (integer6 == AddHillsLayer.SNOWY_TUNDRA_ID) {
                integer9 = AddHillsLayer.SNOWY_MOUNTAINS_ID;
            }
            else if (integer6 == AddHillsLayer.JUNGLE_ID) {
                integer9 = AddHillsLayer.JUNGLE_HILLS_ID;
            }
            else if (integer6 == AddHillsLayer.BAMBOO_JUNGLE_ID) {
                integer9 = AddHillsLayer.BAMBOO_JUNGLE_HILLS_ID;
            }
            else if (integer6 == BiomeLayers.OCEAN_ID) {
                integer9 = BiomeLayers.DEEP_OCEAN_ID;
            }
            else if (integer6 == BiomeLayers.LUKEWARM_OCEAN_ID) {
                integer9 = BiomeLayers.DEEP_LUKEWARM_OCEAN_ID;
            }
            else if (integer6 == BiomeLayers.COLD_OCEAN_ID) {
                integer9 = BiomeLayers.DEEP_COLD_OCEAN_ID;
            }
            else if (integer6 == BiomeLayers.FROZEN_OCEAN_ID) {
                integer9 = BiomeLayers.DEEP_FROZEN_OCEAN_ID;
            }
            else if (integer6 == AddHillsLayer.MOUNTAINS_ID) {
                integer9 = AddHillsLayer.WOODED_MOUNTAINS_ID;
            }
            else if (integer6 == AddHillsLayer.SAVANNA_ID) {
                integer9 = AddHillsLayer.SAVANNA_PLATEAU_ID;
            }
            else if (BiomeLayers.areSimilar(integer6, AddHillsLayer.WOODED_BADLANDS_PLATEAU_ID)) {
                integer9 = AddHillsLayer.BADLANDS_ID;
            }
            else if ((integer6 == BiomeLayers.DEEP_OCEAN_ID || integer6 == BiomeLayers.DEEP_LUKEWARM_OCEAN_ID || integer6 == BiomeLayers.DEEP_COLD_OCEAN_ID || integer6 == BiomeLayers.DEEP_FROZEN_OCEAN_ID) && context.nextInt(3) == 0) {
                integer9 = ((context.nextInt(2) == 0) ? AddHillsLayer.PLAINS_ID : AddHillsLayer.FOREST_ID);
            }
            if (integer8 == 0 && integer9 != integer6) {
                final Biome biome10 = Biome.getParentBiome(Registry.BIOME.get(integer9));
                integer9 = ((biome10 == null) ? integer6 : Registry.BIOME.getRawId(biome10));
            }
            if (integer9 != integer6) {
                int integer10 = 0;
                if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 1), this.transformZ(z + 0)), integer6)) {
                    ++integer10;
                }
                if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 2), this.transformZ(z + 1)), integer6)) {
                    ++integer10;
                }
                if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 0), this.transformZ(z + 1)), integer6)) {
                    ++integer10;
                }
                if (BiomeLayers.areSimilar(sampler1.sample(this.transformX(x + 1), this.transformZ(z + 2)), integer6)) {
                    ++integer10;
                }
                if (integer10 >= 3) {
                    return integer9;
                }
            }
        }
        return integer6;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        BIRCH_FOREST_ID = Registry.BIOME.getRawId(Biomes.C);
        BIRCH_FOREST_HILLS_ID = Registry.BIOME.getRawId(Biomes.D);
        DESERT_ID = Registry.BIOME.getRawId(Biomes.d);
        DESERT_HILLS_ID = Registry.BIOME.getRawId(Biomes.s);
        MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.e);
        WOODED_MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.J);
        FOREST_ID = Registry.BIOME.getRawId(Biomes.f);
        WOODED_HILLS_ID = Registry.BIOME.getRawId(Biomes.t);
        SNOWY_TUNDRA_ID = Registry.BIOME.getRawId(Biomes.n);
        SNOWY_MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.o);
        JUNGLE_ID = Registry.BIOME.getRawId(Biomes.w);
        JUNGLE_HILLS_ID = Registry.BIOME.getRawId(Biomes.x);
        BAMBOO_JUNGLE_ID = Registry.BIOME.getRawId(Biomes.aw);
        BAMBOO_JUNGLE_HILLS_ID = Registry.BIOME.getRawId(Biomes.ax);
        BADLANDS_ID = Registry.BIOME.getRawId(Biomes.M);
        WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.N);
        PLAINS_ID = Registry.BIOME.getRawId(Biomes.c);
        GIANT_TREE_TAIGA_ID = Registry.BIOME.getRawId(Biomes.H);
        GIANT_TREE_TAIGA_HILLS_ID = Registry.BIOME.getRawId(Biomes.I);
        DARK_FOREST_ID = Registry.BIOME.getRawId(Biomes.E);
        SAVANNA_ID = Registry.BIOME.getRawId(Biomes.K);
        SAVANNA_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.L);
        TAIGA_ID = Registry.BIOME.getRawId(Biomes.g);
        SNOWY_TAIGA_ID = Registry.BIOME.getRawId(Biomes.F);
        SNOWY_TAIGA_HILLS_ID = Registry.BIOME.getRawId(Biomes.G);
        TAIGA_HILLS_ID = Registry.BIOME.getRawId(Biomes.u);
    }
}
