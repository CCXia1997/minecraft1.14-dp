package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.Biomes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class SetBaseBiomesLayer implements IdentitySamplingLayer
{
    private static final int BIRCH_FOREST_ID;
    private static final int DESERT_ID;
    private static final int MOUNTAINS_ID;
    private static final int FOREST_ID;
    private static final int SNOWY_TUNDRA_ID;
    private static final int JUNGLE_ID;
    private static final int BADLANDS_PLATEAU_ID;
    private static final int WOODED_BADLANDS_PLATEAU_ID;
    private static final int MUSHROOM_FIELDS_ID;
    private static final int PLAINS_ID;
    private static final int GIANT_TREE_TAIGA_ID;
    private static final int DARK_FOREST_ID;
    private static final int SAVANNA_ID;
    private static final int SWAMP_ID;
    private static final int TAIGA_ID;
    private static final int SNOWY_TAIGA_ID;
    private static final int[] OLD_GROUP_1;
    private static final int[] DRY_BIOMES;
    private static final int[] TEMPERATE_BIOMES;
    private static final int[] COOL_BIOMES;
    private static final int[] SNOWY_BIOMES;
    private final OverworldChunkGeneratorConfig config;
    private int[] chosenGroup1;
    
    public SetBaseBiomesLayer(final LevelGeneratorType generatorType, final OverworldChunkGeneratorConfig generatorConfig) {
        this.chosenGroup1 = SetBaseBiomesLayer.DRY_BIOMES;
        if (generatorType == LevelGeneratorType.DEFAULT_1_1) {
            this.chosenGroup1 = SetBaseBiomesLayer.OLD_GROUP_1;
            this.config = null;
        }
        else {
            this.config = generatorConfig;
        }
    }
    
    @Override
    public int sample(final LayerRandomnessSource context, int value) {
        if (this.config != null && this.config.getForcedBiome() >= 0) {
            return this.config.getForcedBiome();
        }
        final int integer3 = (value & 0xF00) >> 8;
        value &= 0xFFFFF0FF;
        if (BiomeLayers.isOcean(value) || value == SetBaseBiomesLayer.MUSHROOM_FIELDS_ID) {
            return value;
        }
        switch (value) {
            case 1: {
                if (integer3 > 0) {
                    return (context.nextInt(3) == 0) ? SetBaseBiomesLayer.BADLANDS_PLATEAU_ID : SetBaseBiomesLayer.WOODED_BADLANDS_PLATEAU_ID;
                }
                return this.chosenGroup1[context.nextInt(this.chosenGroup1.length)];
            }
            case 2: {
                if (integer3 > 0) {
                    return SetBaseBiomesLayer.JUNGLE_ID;
                }
                return SetBaseBiomesLayer.TEMPERATE_BIOMES[context.nextInt(SetBaseBiomesLayer.TEMPERATE_BIOMES.length)];
            }
            case 3: {
                if (integer3 > 0) {
                    return SetBaseBiomesLayer.GIANT_TREE_TAIGA_ID;
                }
                return SetBaseBiomesLayer.COOL_BIOMES[context.nextInt(SetBaseBiomesLayer.COOL_BIOMES.length)];
            }
            case 4: {
                return SetBaseBiomesLayer.SNOWY_BIOMES[context.nextInt(SetBaseBiomesLayer.SNOWY_BIOMES.length)];
            }
            default: {
                return SetBaseBiomesLayer.MUSHROOM_FIELDS_ID;
            }
        }
    }
    
    static {
        BIRCH_FOREST_ID = Registry.BIOME.getRawId(Biomes.C);
        DESERT_ID = Registry.BIOME.getRawId(Biomes.d);
        MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.e);
        FOREST_ID = Registry.BIOME.getRawId(Biomes.f);
        SNOWY_TUNDRA_ID = Registry.BIOME.getRawId(Biomes.n);
        JUNGLE_ID = Registry.BIOME.getRawId(Biomes.w);
        BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.O);
        WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.N);
        MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.p);
        PLAINS_ID = Registry.BIOME.getRawId(Biomes.c);
        GIANT_TREE_TAIGA_ID = Registry.BIOME.getRawId(Biomes.H);
        DARK_FOREST_ID = Registry.BIOME.getRawId(Biomes.E);
        SAVANNA_ID = Registry.BIOME.getRawId(Biomes.K);
        SWAMP_ID = Registry.BIOME.getRawId(Biomes.h);
        TAIGA_ID = Registry.BIOME.getRawId(Biomes.g);
        SNOWY_TAIGA_ID = Registry.BIOME.getRawId(Biomes.F);
        OLD_GROUP_1 = new int[] { SetBaseBiomesLayer.DESERT_ID, SetBaseBiomesLayer.FOREST_ID, SetBaseBiomesLayer.MOUNTAINS_ID, SetBaseBiomesLayer.SWAMP_ID, SetBaseBiomesLayer.PLAINS_ID, SetBaseBiomesLayer.TAIGA_ID };
        DRY_BIOMES = new int[] { SetBaseBiomesLayer.DESERT_ID, SetBaseBiomesLayer.DESERT_ID, SetBaseBiomesLayer.DESERT_ID, SetBaseBiomesLayer.SAVANNA_ID, SetBaseBiomesLayer.SAVANNA_ID, SetBaseBiomesLayer.PLAINS_ID };
        TEMPERATE_BIOMES = new int[] { SetBaseBiomesLayer.FOREST_ID, SetBaseBiomesLayer.DARK_FOREST_ID, SetBaseBiomesLayer.MOUNTAINS_ID, SetBaseBiomesLayer.PLAINS_ID, SetBaseBiomesLayer.BIRCH_FOREST_ID, SetBaseBiomesLayer.SWAMP_ID };
        COOL_BIOMES = new int[] { SetBaseBiomesLayer.FOREST_ID, SetBaseBiomesLayer.MOUNTAINS_ID, SetBaseBiomesLayer.TAIGA_ID, SetBaseBiomesLayer.PLAINS_ID };
        SNOWY_BIOMES = new int[] { SetBaseBiomesLayer.SNOWY_TUNDRA_ID, SetBaseBiomesLayer.SNOWY_TUNDRA_ID, SetBaseBiomesLayer.SNOWY_TUNDRA_ID, SetBaseBiomesLayer.SNOWY_TAIGA_ID };
    }
}
