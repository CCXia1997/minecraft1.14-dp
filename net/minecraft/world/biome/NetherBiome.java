package net.minecraft.world.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityCategory;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.decorator.ChanceRangeDecoratorConfig;
import net.minecraft.world.gen.feature.BushFeatureConfig;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.feature.NetherSpringFeatureConfig;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class NetherBiome extends Biome
{
    protected NetherBiome() {
        super(new Settings().<TernarySurfaceConfig>configureSurfaceBuilder(SurfaceBuilder.NETHER, SurfaceBuilder.NETHER_CONFIG).precipitation(Precipitation.NONE).category(Category.NETHER).depth(0.1f).scale(0.2f).temperature(2.0f).downfall(0.0f).waterColor(4159204).waterFogColor(329011).parent(null));
        this.<DefaultFeatureConfig>addStructureFeature(Feature.NETHER_BRIDGE, FeatureConfig.DEFAULT);
        this.<CarverConfig>addCarver(GenerationStep.Carver.AIR, Biome.configureCarver((Carver<C>)Carver.NETHER_CAVE, (C)new ProbabilityConfig(0.2f)));
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.<SpringFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ax, new SpringFeatureConfig(Fluids.LAVA.getDefaultState()), Decorator.p, new RangeDecoratorConfig(20, 8, 16, 256)));
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.<DefaultFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.NETHER_BRIDGE, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.<NetherSpringFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ao, new NetherSpringFeatureConfig(false), Decorator.n, new RangeDecoratorConfig(8, 4, 8, 128)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.R, FeatureConfig.DEFAULT, Decorator.A, new CountDecoratorConfig(10)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.<DefaultFeatureConfig, CountDecoratorConfig>configureFeature(Feature.V, FeatureConfig.DEFAULT, Decorator.I, new CountDecoratorConfig(10)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.<DefaultFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.V, FeatureConfig.DEFAULT, Decorator.n, new RangeDecoratorConfig(10, 0, 0, 128)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.<BushFeatureConfig, ChanceRangeDecoratorConfig>configureFeature(Feature.al, new BushFeatureConfig(Blocks.bB.getDefaultState()), Decorator.r, new ChanceRangeDecoratorConfig(0.5f, 0, 0, 128)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.<BushFeatureConfig, ChanceRangeDecoratorConfig>configureFeature(Feature.al, new BushFeatureConfig(Blocks.bC.getDefaultState()), Decorator.r, new ChanceRangeDecoratorConfig(0.5f, 0, 0, 128)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.<OreFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NETHERRACK, Blocks.fp.getDefaultState(), 14), Decorator.n, new RangeDecoratorConfig(16, 10, 20, 128)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.<OreFeatureConfig, CountDecoratorConfig>configureFeature(Feature.ar, new OreFeatureConfig(OreFeatureConfig.Target.NETHERRACK, Blocks.iB.getDefaultState(), 33), Decorator.B, new CountDecoratorConfig(4)));
        this.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.<NetherSpringFeatureConfig, RangeDecoratorConfig>configureFeature(Feature.ao, new NetherSpringFeatureConfig(true), Decorator.n, new RangeDecoratorConfig(16, 10, 20, 128)));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.GHAST, 50, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.ZOMBIE_PIGMAN, 100, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.MAGMA_CUBE, 2, 4, 4));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
    }
}
