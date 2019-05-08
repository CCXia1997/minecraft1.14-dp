package net.minecraft.world.biome;

import net.minecraft.world.gen.feature.IcebergFeatureConfig;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.decorator.NoiseHeightmapDecoratorConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.decorator.CountChanceDecoratorConfig;
import net.minecraft.world.gen.feature.BushFeatureConfig;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.GrassFeatureConfig;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.feature.DoublePlantFeatureConfig;
import net.minecraft.world.gen.feature.BoulderFeatureConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import java.util.List;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.EmeraldOreFeatureConfig;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.decorator.DungeonDecoratorConfig;
import net.minecraft.world.gen.decorator.LakeDecoratorConfig;
import net.minecraft.world.gen.feature.LakeFeatureConfig;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.VillageFeatureConfig;
import net.minecraft.world.gen.feature.BuriedTreasureFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PillagerOutpostFeatureConfig;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.GenerationStep;

public class DefaultBiomeFeatures
{
    public static void addLandCarvers(final Biome biome) {
        biome.<CarverConfig>addCarver(GenerationStep.Carver.AIR, Biome.configureCarver((Carver<C>)Carver.CAVE, (C)new ProbabilityConfig(0.14285715f)));
        biome.<CarverConfig>addCarver(GenerationStep.Carver.AIR, Biome.configureCarver((Carver<C>)Carver.RAVINE, (C)new ProbabilityConfig(0.02f)));
    }
    
    public static void addOceanCarvers(final Biome biome) {
        biome.<CarverConfig>addCarver(GenerationStep.Carver.AIR, Biome.configureCarver((Carver<C>)Carver.CAVE, (C)new ProbabilityConfig(0.06666667f)));
        biome.<CarverConfig>addCarver(GenerationStep.Carver.AIR, Biome.configureCarver((Carver<C>)Carver.RAVINE, (C)new ProbabilityConfig(0.02f)));
        biome.<CarverConfig>addCarver(GenerationStep.Carver.LIQUID, Biome.configureCarver((Carver<C>)Carver.UNDERWATER_RAVINE, (C)new ProbabilityConfig(0.02f)));
        biome.<CarverConfig>addCarver(GenerationStep.Carver.LIQUID, Biome.configureCarver((Carver<C>)Carver.UNDERWATER_CAVE, (C)new ProbabilityConfig(0.06666667f)));
    }
    
    public static void addDefaultStructures(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, Biome.<MineshaftFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004000000189989805, MineshaftFeature.Type.NORMAL), Decorator.NOPE, DecoratorConfig.DEFAULT));
        biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<PillagerOutpostFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.PILLAGER_OUTPOST, new PillagerOutpostFeatureConfig(0.004), Decorator.NOPE, DecoratorConfig.DEFAULT));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.STRONGHOLD, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
        biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.SWAMP_HUT, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
        biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.DESERT_PYRAMID, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
        biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.JUNGLE_TEMPLE, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
        biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.IGLOO, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
        biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<ShipwreckFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.SHIPWRECK, new ShipwreckFeatureConfig(false), Decorator.NOPE, DecoratorConfig.DEFAULT));
        biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.OCEAN_MONUMENT, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
        biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.WOODLAND_MANSION, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
        biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<OceanRuinFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.OCEAN_RUIN, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3f, 0.9f), Decorator.NOPE, DecoratorConfig.DEFAULT));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, Biome.<BuriedTreasureFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.BURIED_TREASURE, new BuriedTreasureFeatureConfig(0.01f), Decorator.NOPE, DecoratorConfig.DEFAULT));
        biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<VillageFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.VILLAGE, new VillageFeatureConfig("village/plains/town_centers", 6), Decorator.NOPE, DecoratorConfig.DEFAULT));
    }
    
    public static void addDefaultLakes(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, Biome.<LakeFeatureConfig, LakeDecoratorConfig>configureFeature(Feature.aq, new LakeFeatureConfig(Blocks.A.getDefaultState()), Decorator.E, new LakeDecoratorConfig(4)));
        biome.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, Biome.<LakeFeatureConfig, LakeDecoratorConfig>configureFeature(Feature.aq, new LakeFeatureConfig(Blocks.B.getDefaultState()), Decorator.D, new LakeDecoratorConfig(80)));
    }
    
    public static void addDesertLakes(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, Biome.<LakeFeatureConfig, LakeDecoratorConfig>configureFeature(Feature.aq, new LakeFeatureConfig(Blocks.B.getDefaultState()), Decorator.D, new LakeDecoratorConfig(80)));
    }
    
    public static void addDungeons(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, Biome.<DefaultFeatureConfig, DungeonDecoratorConfig>configureFeature(Feature.ac, FeatureConfig.DEFAULT, Decorator.F, new DungeonDecoratorConfig(8)));
    }
    
    public static void addMineables(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<OreFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.j.getDefaultState(), 33), Decorator.n, new RangeDecoratorConfig(10, 0, 0, 256)));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<OreFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.E.getDefaultState(), 33), Decorator.n, new RangeDecoratorConfig(8, 0, 0, 256)));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<OreFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.c.getDefaultState(), 33), Decorator.n, new RangeDecoratorConfig(10, 0, 0, 80)));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<OreFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.e.getDefaultState(), 33), Decorator.n, new RangeDecoratorConfig(10, 0, 0, 80)));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<OreFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.g.getDefaultState(), 33), Decorator.n, new RangeDecoratorConfig(10, 0, 0, 80)));
    }
    
    public static void addDefaultOres(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<OreFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.H.getDefaultState(), 17), Decorator.n, new RangeDecoratorConfig(20, 0, 0, 128)));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<OreFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.G.getDefaultState(), 9), Decorator.n, new RangeDecoratorConfig(20, 0, 0, 64)));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<OreFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.F.getDefaultState(), 9), Decorator.n, new RangeDecoratorConfig(2, 0, 0, 32)));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<OreFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.cw.getDefaultState(), 8), Decorator.n, new RangeDecoratorConfig(8, 0, 0, 16)));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<OreFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.bR.getDefaultState(), 8), Decorator.n, new RangeDecoratorConfig(1, 0, 0, 16)));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<OreFeatureConfig, CountDepthDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.ap.getDefaultState(), 7), Decorator.u, new CountDepthDecoratorConfig(1, 16, 16)));
    }
    
    public static void addExtraGoldOre(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<OreFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.F.getDefaultState(), 9), Decorator.n, new RangeDecoratorConfig(20, 32, 32, 80)));
    }
    
    public static void addEmeraldOre(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<EmeraldOreFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.aw, new EmeraldOreFeatureConfig(Blocks.b.getDefaultState(), Blocks.eb.getDefaultState()), Decorator.C, DecoratorConfig.DEFAULT));
    }
    
    public static void addInfestedStone(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.<OreFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.dr.getDefaultState(), 9), Decorator.n, new RangeDecoratorConfig(7, 0, 0, 64)));
    }
    
    public static void addDefaultDisks(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<DiskFeatureConfig, CountDecoratorConfig>configureFeature(Feature.am, new DiskFeatureConfig(Blocks.C.getDefaultState(), 7, 2, Lists.<BlockState>newArrayList(Blocks.j.getDefaultState(), Blocks.i.getDefaultState())), Decorator.b, new CountDecoratorConfig(3)));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<DiskFeatureConfig, CountDecoratorConfig>configureFeature(Feature.am, new DiskFeatureConfig(Blocks.cE.getDefaultState(), 4, 1, Lists.<BlockState>newArrayList(Blocks.j.getDefaultState(), Blocks.cE.getDefaultState())), Decorator.b, new CountDecoratorConfig(1)));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<DiskFeatureConfig, CountDecoratorConfig>configureFeature(Feature.am, new DiskFeatureConfig(Blocks.E.getDefaultState(), 6, 2, Lists.<BlockState>newArrayList(Blocks.j.getDefaultState(), Blocks.i.getDefaultState())), Decorator.b, new CountDecoratorConfig(1)));
    }
    
    public static void addClay(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.<DiskFeatureConfig, CountDecoratorConfig>configureFeature(Feature.am, new DiskFeatureConfig(Blocks.cE.getDefaultState(), 4, 1, Lists.<BlockState>newArrayList(Blocks.j.getDefaultState(), Blocks.cE.getDefaultState())), Decorator.b, new CountDecoratorConfig(1)));
    }
    
    public static void addMossyRocks(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, Biome.<BoulderFeatureConfig, CountDecoratorConfig>configureFeature(Feature.af, new BoulderFeatureConfig(Blocks.bI.getDefaultState(), 0), Decorator.z, new CountDecoratorConfig(3)));
    }
    
    public static void addLargeFerns(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DoublePlantFeatureConfig, CountDecoratorConfig>configureFeature(Feature.an, new DoublePlantFeatureConfig(Blocks.gR.getDefaultState()), Decorator.c, new CountDecoratorConfig(7)));
    }
    
    public static void addSweetBerryBushesSnowy(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, ChanceDecoratorConfig>configureFeature(Feature.aM, FeatureConfig.DEFAULT, Decorator.j, new ChanceDecoratorConfig(12)));
    }
    
    public static void addSweetBerryBushes(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.aM, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(1)));
    }
    
    public static void addBamboo(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<ProbabilityConfig, CountDecoratorConfig>configureFeature(Feature.aJ, new ProbabilityConfig(0.0f), Decorator.d, new CountDecoratorConfig(16)));
    }
    
    public static void addBambooJungleTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<ProbabilityConfig, TopSolidHeightmapNoiseBiasedDecoratorConfig>configureFeature(Feature.aJ, new ProbabilityConfig(0.2f), Decorator.x, new TopSolidHeightmapNoiseBiasedDecoratorConfig(160, 80.0, 0.3, Heightmap.Type.a)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.q, Feature.t, Feature.B }, new FeatureConfig[] { FeatureConfig.DEFAULT, FeatureConfig.DEFAULT, FeatureConfig.DEFAULT }, new float[] { 0.05f, 0.15f, 0.7f }, Feature.J, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(30, 0.1f, 1)));
    }
    
    public static void addTaigaTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.v }, new FeatureConfig[] { FeatureConfig.DEFAULT }, new float[] { 0.33333334f }, Feature.y, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(10, 0.1f, 1)));
    }
    
    public static void addWaterBiomeOakTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.q }, new FeatureConfig[] { FeatureConfig.DEFAULT }, new float[] { 0.1f }, Feature.A, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(0, 0.1f, 1)));
    }
    
    public static void addBirchTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.r, FeatureConfig.DEFAULT, Decorator.m, new CountExtraChanceDecoratorConfig(10, 0.1f, 1)));
    }
    
    public static void addForestTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.r, Feature.q }, new FeatureConfig[] { FeatureConfig.DEFAULT, FeatureConfig.DEFAULT }, new float[] { 0.2f, 0.1f }, Feature.A, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(10, 0.1f, 1)));
    }
    
    public static void addTallBirchTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.s }, new FeatureConfig[] { FeatureConfig.DEFAULT }, new float[] { 0.5f }, Feature.r, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(10, 0.1f, 1)));
    }
    
    public static void addSavannaTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.x }, new FeatureConfig[] { FeatureConfig.DEFAULT }, new float[] { 0.8f }, Feature.A, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(1, 0.1f, 1)));
    }
    
    public static void addExtraSavannaTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.x }, new FeatureConfig[] { FeatureConfig.DEFAULT }, new float[] { 0.8f }, Feature.A, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(2, 0.1f, 1)));
    }
    
    public static void addMountainTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.y, Feature.q }, new FeatureConfig[] { FeatureConfig.DEFAULT, FeatureConfig.DEFAULT }, new float[] { 0.666f, 0.1f }, Feature.A, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(0, 0.1f, 1)));
    }
    
    public static void addExtraMountainTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.y, Feature.q }, new FeatureConfig[] { FeatureConfig.DEFAULT, FeatureConfig.DEFAULT }, new float[] { 0.666f, 0.1f }, Feature.A, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(3, 0.1f, 1)));
    }
    
    public static void addJungleTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.q, Feature.t, Feature.B }, new FeatureConfig[] { FeatureConfig.DEFAULT, FeatureConfig.DEFAULT, FeatureConfig.DEFAULT }, new float[] { 0.1f, 0.5f, 0.33333334f }, Feature.u, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(50, 0.1f, 1)));
    }
    
    public static void addJungleEdgeTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.q, Feature.t }, new FeatureConfig[] { FeatureConfig.DEFAULT, FeatureConfig.DEFAULT }, new float[] { 0.1f, 0.5f }, Feature.u, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(2, 0.1f, 1)));
    }
    
    public static void addBadlandsPlateauTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.A, FeatureConfig.DEFAULT, Decorator.m, new CountExtraChanceDecoratorConfig(5, 0.1f, 1)));
    }
    
    public static void addSnowySpruceTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.y, FeatureConfig.DEFAULT, Decorator.m, new CountExtraChanceDecoratorConfig(0, 0.1f, 1)));
    }
    
    public static void addGiantSpruceTaigaTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.D, Feature.v }, new FeatureConfig[] { FeatureConfig.DEFAULT, FeatureConfig.DEFAULT }, new float[] { 0.33333334f, 0.33333334f }, Feature.y, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(10, 0.1f, 1)));
    }
    
    public static void addGiantTreeTaigaTrees(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.D, Feature.C, Feature.v }, new FeatureConfig[] { FeatureConfig.DEFAULT, FeatureConfig.DEFAULT, FeatureConfig.DEFAULT }, new float[] { 0.025641026f, 0.30769232f, 0.33333334f }, Feature.y, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(10, 0.1f, 1)));
    }
    
    public static void addJungleGrass(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.J, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(25)));
    }
    
    public static void addSavannaTallGrass(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DoublePlantFeatureConfig, CountDecoratorConfig>configureFeature(Feature.an, new DoublePlantFeatureConfig(Blocks.gQ.getDefaultState()), Decorator.c, new CountDecoratorConfig(7)));
    }
    
    public static void addShatteredSavanaGrass(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<GrassFeatureConfig, CountDecoratorConfig>configureFeature(Feature.L, new GrassFeatureConfig(Blocks.aQ.getDefaultState()), Decorator.d, new CountDecoratorConfig(5)));
    }
    
    public static void addSavannaGrass(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<GrassFeatureConfig, CountDecoratorConfig>configureFeature(Feature.L, new GrassFeatureConfig(Blocks.aQ.getDefaultState()), Decorator.d, new CountDecoratorConfig(20)));
    }
    
    public static void addBadlandsGrass(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<GrassFeatureConfig, CountDecoratorConfig>configureFeature(Feature.L, new GrassFeatureConfig(Blocks.aQ.getDefaultState()), Decorator.d, new CountDecoratorConfig(1)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.O, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(20)));
    }
    
    public static void addForestFlowers(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomRandomFeatureConfig, CountDecoratorConfig>configureFeature(Feature.as, new RandomRandomFeatureConfig(new Feature[] { Feature.an, Feature.an, Feature.an, Feature.GENERAL_FOREST_FLOWER }, new FeatureConfig[] { new DoublePlantFeatureConfig(Blocks.gN.getDefaultState()), new DoublePlantFeatureConfig(Blocks.gO.getDefaultState()), new DoublePlantFeatureConfig(Blocks.gP.getDefaultState()), FeatureConfig.DEFAULT }, 0), Decorator.c, new CountDecoratorConfig(5)));
    }
    
    public static void addForestGrass(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<GrassFeatureConfig, CountDecoratorConfig>configureFeature(Feature.L, new GrassFeatureConfig(Blocks.aQ.getDefaultState()), Decorator.d, new CountDecoratorConfig(2)));
    }
    
    public static void addSwampFeatures(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.z, FeatureConfig.DEFAULT, Decorator.m, new CountExtraChanceDecoratorConfig(2, 0.1f, 1)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.SWAMP_FLOWER, FeatureConfig.DEFAULT, Decorator.c, new CountDecoratorConfig(1)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<GrassFeatureConfig, CountDecoratorConfig>configureFeature(Feature.L, new GrassFeatureConfig(Blocks.aQ.getDefaultState()), Decorator.d, new CountDecoratorConfig(5)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.O, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(1)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.ab, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(4)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<BushFeatureConfig, CountChanceDecoratorConfig>configureFeature(Feature.al, new BushFeatureConfig(Blocks.bB.getDefaultState()), Decorator.s, new CountChanceDecoratorConfig(8, 0.25f)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<BushFeatureConfig, CountChanceDecoratorConfig>configureFeature(Feature.al, new BushFeatureConfig(Blocks.bC.getDefaultState()), Decorator.t, new CountChanceDecoratorConfig(8, 0.125f)));
    }
    
    public static void addMushroomFieldsFeatures(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomBooleanFeatureConfig, CountDecoratorConfig>configureFeature(Feature.av, new RandomBooleanFeatureConfig(Feature.S, FeatureConfig.DEFAULT, Feature.T, FeatureConfig.DEFAULT), Decorator.a, new CountDecoratorConfig(1)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<BushFeatureConfig, CountChanceDecoratorConfig>configureFeature(Feature.al, new BushFeatureConfig(Blocks.bB.getDefaultState()), Decorator.s, new CountChanceDecoratorConfig(1, 0.25f)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<BushFeatureConfig, CountChanceDecoratorConfig>configureFeature(Feature.al, new BushFeatureConfig(Blocks.bC.getDefaultState()), Decorator.t, new CountChanceDecoratorConfig(1, 0.125f)));
    }
    
    public static void addPlainsFeatures(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<RandomFeatureConfig, CountExtraChanceDecoratorConfig>configureFeature(Feature.at, new RandomFeatureConfig(new Feature[] { Feature.q }, new FeatureConfig[] { FeatureConfig.DEFAULT }, new float[] { 0.33333334f }, Feature.A, FeatureConfig.DEFAULT), Decorator.m, new CountExtraChanceDecoratorConfig(0, 0.05f, 1)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, NoiseHeightmapDecoratorConfig>configureFeature(Feature.PLAIN_FLOWER, FeatureConfig.DEFAULT, Decorator.f, new NoiseHeightmapDecoratorConfig(-0.8, 15, 4)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<GrassFeatureConfig, NoiseHeightmapDecoratorConfig>configureFeature(Feature.L, new GrassFeatureConfig(Blocks.aQ.getDefaultState()), Decorator.g, new NoiseHeightmapDecoratorConfig(-0.8, 5, 10)));
    }
    
    public static void addDesertDeadBushes(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.O, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(2)));
    }
    
    public static void addGiantTaigaGrass(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.K, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(7)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.O, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(1)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<BushFeatureConfig, CountChanceDecoratorConfig>configureFeature(Feature.al, new BushFeatureConfig(Blocks.bB.getDefaultState()), Decorator.s, new CountChanceDecoratorConfig(3, 0.25f)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<BushFeatureConfig, CountChanceDecoratorConfig>configureFeature(Feature.al, new BushFeatureConfig(Blocks.bC.getDefaultState()), Decorator.t, new CountChanceDecoratorConfig(3, 0.125f)));
    }
    
    public static void addDefaultFlowers(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.DEFAULT_FLOWER, FeatureConfig.DEFAULT, Decorator.c, new CountDecoratorConfig(2)));
    }
    
    public static void addExtraDefaultFlowers(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.DEFAULT_FLOWER, FeatureConfig.DEFAULT, Decorator.c, new CountDecoratorConfig(4)));
    }
    
    public static void addDefaultGrass(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<GrassFeatureConfig, CountDecoratorConfig>configureFeature(Feature.L, new GrassFeatureConfig(Blocks.aQ.getDefaultState()), Decorator.d, new CountDecoratorConfig(1)));
    }
    
    public static void addTaigaGrass(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.K, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(1)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<BushFeatureConfig, CountChanceDecoratorConfig>configureFeature(Feature.al, new BushFeatureConfig(Blocks.bB.getDefaultState()), Decorator.s, new CountChanceDecoratorConfig(1, 0.25f)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<BushFeatureConfig, CountChanceDecoratorConfig>configureFeature(Feature.al, new BushFeatureConfig(Blocks.bC.getDefaultState()), Decorator.t, new CountChanceDecoratorConfig(1, 0.125f)));
    }
    
    public static void addPlainsTallGrass(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DoublePlantFeatureConfig, NoiseHeightmapDecoratorConfig>configureFeature(Feature.an, new DoublePlantFeatureConfig(Blocks.gQ.getDefaultState()), Decorator.f, new NoiseHeightmapDecoratorConfig(-0.8, 0, 7)));
    }
    
    public static void addDefaultMushrooms(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<BushFeatureConfig, ChanceDecoratorConfig>configureFeature(Feature.al, new BushFeatureConfig(Blocks.bB.getDefaultState()), Decorator.j, new ChanceDecoratorConfig(4)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<BushFeatureConfig, ChanceDecoratorConfig>configureFeature(Feature.al, new BushFeatureConfig(Blocks.bC.getDefaultState()), Decorator.j, new ChanceDecoratorConfig(8)));
    }
    
    public static void addDefaultVegetation(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.Y, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(10)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, ChanceDecoratorConfig>configureFeature(Feature.X, FeatureConfig.DEFAULT, Decorator.j, new ChanceDecoratorConfig(32)));
    }
    
    public static void addBadlandsVegetation(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.Y, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(13)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, ChanceDecoratorConfig>configureFeature(Feature.X, FeatureConfig.DEFAULT, Decorator.j, new ChanceDecoratorConfig(32)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.N, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(5)));
    }
    
    public static void addJungleVegetation(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.W, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(1)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.aa, FeatureConfig.DEFAULT, Decorator.e, new CountDecoratorConfig(50)));
    }
    
    public static void addDesertVegetation(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.Y, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(60)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, ChanceDecoratorConfig>configureFeature(Feature.X, FeatureConfig.DEFAULT, Decorator.j, new ChanceDecoratorConfig(32)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.N, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(10)));
    }
    
    public static void addSwampVegetation(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.Y, FeatureConfig.DEFAULT, Decorator.d, new CountDecoratorConfig(20)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, ChanceDecoratorConfig>configureFeature(Feature.X, FeatureConfig.DEFAULT, Decorator.j, new ChanceDecoratorConfig(32)));
    }
    
    public static void addDesertFeatures(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<DefaultFeatureConfig, ChanceDecoratorConfig>configureFeature(Feature.P, FeatureConfig.DEFAULT, Decorator.i, new ChanceDecoratorConfig(1000)));
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.<DefaultFeatureConfig, ChanceDecoratorConfig>configureFeature(Feature.Q, FeatureConfig.DEFAULT, Decorator.k, new ChanceDecoratorConfig(64)));
    }
    
    public static void addFossils(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.<DefaultFeatureConfig, ChanceDecoratorConfig>configureFeature(Feature.Q, FeatureConfig.DEFAULT, Decorator.k, new ChanceDecoratorConfig(64)));
    }
    
    public static void addKelp(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, TopSolidHeightmapNoiseBiasedDecoratorConfig>configureFeature(Feature.aD, FeatureConfig.DEFAULT, Decorator.x, new TopSolidHeightmapNoiseBiasedDecoratorConfig(120, 80.0, 0.0, Heightmap.Type.c)));
    }
    
    public static void addSeagrassOnStone(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<SimpleBlockFeatureConfig, CarvingMaskDecoratorConfig>configureFeature(Feature.aI, new SimpleBlockFeatureConfig(Blocks.aT.getDefaultState(), new BlockState[] { Blocks.b.getDefaultState() }, new BlockState[] { Blocks.A.getDefaultState() }, new BlockState[] { Blocks.A.getDefaultState() }), Decorator.y, new CarvingMaskDecoratorConfig(GenerationStep.Carver.LIQUID, 0.1f)));
    }
    
    public static void addSeagrass(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<SeagrassFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.aC, new SeagrassFeatureConfig(80, 0.3), Decorator.v, DecoratorConfig.DEFAULT));
    }
    
    public static void addMoreSeagrass(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<SeagrassFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.aC, new SeagrassFeatureConfig(80, 0.8), Decorator.v, DecoratorConfig.DEFAULT));
    }
    
    public static void addLessKelp(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<DefaultFeatureConfig, TopSolidHeightmapNoiseBiasedDecoratorConfig>configureFeature(Feature.aD, FeatureConfig.DEFAULT, Decorator.x, new TopSolidHeightmapNoiseBiasedDecoratorConfig(80, 80.0, 0.0, Heightmap.Type.c)));
    }
    
    public static void addSprings(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<SpringFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ax, new SpringFeatureConfig(Fluids.WATER.getDefaultState()), Decorator.o, new RangeDecoratorConfig(50, 8, 8, 256)));
        biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<SpringFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ax, new SpringFeatureConfig(Fluids.LAVA.getDefaultState()), Decorator.p, new RangeDecoratorConfig(20, 8, 16, 256)));
    }
    
    public static void addIcebergs(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, Biome.<IcebergFeatureConfig, ChanceDecoratorConfig>configureFeature(Feature.ae, new IcebergFeatureConfig(Blocks.gL.getDefaultState()), Decorator.H, new ChanceDecoratorConfig(16)));
        biome.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, Biome.<IcebergFeatureConfig, ChanceDecoratorConfig>configureFeature(Feature.ae, new IcebergFeatureConfig(Blocks.kN.getDefaultState()), Decorator.H, new ChanceDecoratorConfig(200)));
    }
    
    public static void addBlueIce(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<DefaultFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ad, FeatureConfig.DEFAULT, Decorator.q, new RangeDecoratorConfig(20, 30, 32, 64)));
    }
    
    public static void addFrozenTopLayer(final Biome biome) {
        biome.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.Z, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
    }
}
