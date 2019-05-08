package net.minecraft.world.gen.decorator;

import java.util.stream.Stream;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public abstract class Decorator<DC extends DecoratorConfig>
{
    public static final Decorator<CountDecoratorConfig> a;
    public static final Decorator<CountDecoratorConfig> b;
    public static final Decorator<CountDecoratorConfig> c;
    public static final Decorator<CountDecoratorConfig> d;
    public static final Decorator<CountDecoratorConfig> e;
    public static final Decorator<NoiseHeightmapDecoratorConfig> f;
    public static final Decorator<NoiseHeightmapDecoratorConfig> g;
    public static final Decorator<NopeDecoratorConfig> NOPE;
    public static final Decorator<ChanceDecoratorConfig> i;
    public static final Decorator<ChanceDecoratorConfig> j;
    public static final Decorator<ChanceDecoratorConfig> k;
    public static final Decorator<ChanceDecoratorConfig> l;
    public static final Decorator<CountExtraChanceDecoratorConfig> m;
    public static final Decorator<RangeDecoratorConfig> n;
    public static final Decorator<RangeDecoratorConfig> o;
    public static final Decorator<RangeDecoratorConfig> p;
    public static final Decorator<RangeDecoratorConfig> q;
    public static final Decorator<ChanceRangeDecoratorConfig> r;
    public static final Decorator<CountChanceDecoratorConfig> s;
    public static final Decorator<CountChanceDecoratorConfig> t;
    public static final Decorator<CountDepthDecoratorConfig> u;
    public static final Decorator<NopeDecoratorConfig> v;
    public static final Decorator<HeightmapRangeDecoratorConfig> w;
    public static final Decorator<TopSolidHeightmapNoiseBiasedDecoratorConfig> x;
    public static final Decorator<CarvingMaskDecoratorConfig> y;
    public static final Decorator<CountDecoratorConfig> z;
    public static final Decorator<CountDecoratorConfig> A;
    public static final Decorator<CountDecoratorConfig> B;
    public static final Decorator<NopeDecoratorConfig> C;
    public static final Decorator<LakeDecoratorConfig> D;
    public static final Decorator<LakeDecoratorConfig> E;
    public static final Decorator<DungeonDecoratorConfig> F;
    public static final Decorator<NopeDecoratorConfig> G;
    public static final Decorator<ChanceDecoratorConfig> H;
    public static final Decorator<CountDecoratorConfig> I;
    public static final Decorator<NopeDecoratorConfig> J;
    public static final Decorator<NopeDecoratorConfig> K;
    public static final Decorator<NopeDecoratorConfig> L;
    private final Function<Dynamic<?>, ? extends DC> configDeserializer;
    
    private static <T extends DecoratorConfig, G extends Decorator<T>> G register(final String registryName, final G decorator) {
        return Registry.<G>register(Registry.DECORATOR, registryName, decorator);
    }
    
    public Decorator(final Function<Dynamic<?>, ? extends DC> configDeserializer) {
        this.configDeserializer = configDeserializer;
    }
    
    public DC deserialize(final Dynamic<?> dynamic) {
        return (DC)this.configDeserializer.apply(dynamic);
    }
    
    protected <FC extends FeatureConfig> boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final DC decoratorConfig, final ConfiguredFeature<FC> configuredFeature) {
        final AtomicBoolean atomicBoolean7 = new AtomicBoolean(false);
        final boolean boolean7;
        final AtomicBoolean atomicBoolean8;
        this.getPositions(world, generator, random, decoratorConfig, pos).forEach(blockPos -> {
            boolean7 = configuredFeature.generate(world, generator, random, blockPos);
            atomicBoolean8.set(atomicBoolean8.get() || boolean7);
            return;
        });
        return atomicBoolean7.get();
    }
    
    public abstract Stream<BlockPos> getPositions(final IWorld arg1, final ChunkGenerator<? extends ChunkGeneratorConfig> arg2, final Random arg3, final DC arg4, final BlockPos arg5);
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
    }
    
    static {
        a = Decorator.<DecoratorConfig, CountHeightmapDecorator>register("count_heightmap", new CountHeightmapDecorator(CountDecoratorConfig::deserialize));
        b = Decorator.<DecoratorConfig, CountTopSolidDecorator>register("count_top_solid", new CountTopSolidDecorator(CountDecoratorConfig::deserialize));
        c = Decorator.<DecoratorConfig, CountHeightmap32Decorator>register("count_heightmap_32", new CountHeightmap32Decorator(CountDecoratorConfig::deserialize));
        d = Decorator.<DecoratorConfig, CountHeightmapDoubleDecorator>register("count_heightmap_double", new CountHeightmapDoubleDecorator(CountDecoratorConfig::deserialize));
        e = Decorator.<DecoratorConfig, CountHeight64Decorator>register("count_height_64", new CountHeight64Decorator(CountDecoratorConfig::deserialize));
        f = Decorator.<DecoratorConfig, NoiseHeightmap32Decorator>register("noise_heightmap_32", new NoiseHeightmap32Decorator(NoiseHeightmapDecoratorConfig::deserialize));
        g = Decorator.<DecoratorConfig, NoiseHeightmapDoubleDecorator>register("noise_heightmap_double", new NoiseHeightmapDoubleDecorator(NoiseHeightmapDecoratorConfig::deserialize));
        NOPE = Decorator.<DecoratorConfig, NopeDecorator>register("nope", new NopeDecorator(NopeDecoratorConfig::deserialize));
        i = Decorator.<DecoratorConfig, ChanceHeightmapDecorator>register("chance_heightmap", new ChanceHeightmapDecorator(ChanceDecoratorConfig::deserialize));
        j = Decorator.<DecoratorConfig, ChanceHeightmapDoubleDecorator>register("chance_heightmap_double", new ChanceHeightmapDoubleDecorator(ChanceDecoratorConfig::deserialize));
        k = Decorator.<DecoratorConfig, ChancePassthroughDecorator>register("chance_passthrough", new ChancePassthroughDecorator(ChanceDecoratorConfig::deserialize));
        l = Decorator.<DecoratorConfig, ChanceTopSolidHeightmapDecorator>register("chance_top_solid_heightmap", new ChanceTopSolidHeightmapDecorator(ChanceDecoratorConfig::deserialize));
        m = Decorator.<DecoratorConfig, CountExtraHeightmapDecorator>register("count_extra_heightmap", new CountExtraHeightmapDecorator(CountExtraChanceDecoratorConfig::deserialize));
        n = Decorator.<DecoratorConfig, CountRangeDecorator>register("count_range", new CountRangeDecorator(RangeDecoratorConfig::deserialize));
        o = Decorator.<DecoratorConfig, CountBiasedRangeDecorator>register("count_biased_range", new CountBiasedRangeDecorator(RangeDecoratorConfig::deserialize));
        p = Decorator.<DecoratorConfig, CountVeryBiasedRangeDecorator>register("count_very_biased_range", new CountVeryBiasedRangeDecorator(RangeDecoratorConfig::deserialize));
        q = Decorator.<DecoratorConfig, RandomCountRangeDecorator>register("random_count_range", new RandomCountRangeDecorator(RangeDecoratorConfig::deserialize));
        r = Decorator.<DecoratorConfig, ChanceRangeDecorator>register("chance_range", new ChanceRangeDecorator(ChanceRangeDecoratorConfig::deserialize));
        s = Decorator.<DecoratorConfig, CountChanceHeightmapDecorator>register("count_chance_heightmap", new CountChanceHeightmapDecorator(CountChanceDecoratorConfig::deserialize));
        t = Decorator.<DecoratorConfig, CountChanceHeightmapDoubleDecorator>register("count_chance_heightmap_double", new CountChanceHeightmapDoubleDecorator(CountChanceDecoratorConfig::deserialize));
        u = Decorator.<DecoratorConfig, CountDepthAverageDecorator>register("count_depth_average", new CountDepthAverageDecorator(CountDepthDecoratorConfig::deserialize));
        v = Decorator.<DecoratorConfig, HeightmapDecorator>register("top_solid_heightmap", new HeightmapDecorator(NopeDecoratorConfig::deserialize));
        w = Decorator.<DecoratorConfig, HeightmapRangeDecorator>register("top_solid_heightmap_range", new HeightmapRangeDecorator(HeightmapRangeDecoratorConfig::deserialize));
        x = Decorator.<DecoratorConfig, HeightmapNoiseBiasedDecorator>register("top_solid_heightmap_noise_biased", new HeightmapNoiseBiasedDecorator(TopSolidHeightmapNoiseBiasedDecoratorConfig::deserialize));
        y = Decorator.<DecoratorConfig, CarvingMaskDecorator>register("carving_mask", new CarvingMaskDecorator(CarvingMaskDecoratorConfig::deserialize));
        z = Decorator.<DecoratorConfig, ForestRockDecorator>register("forest_rock", new ForestRockDecorator(CountDecoratorConfig::deserialize));
        A = Decorator.<DecoratorConfig, HellFireDecorator>register("hell_fire", new HellFireDecorator(CountDecoratorConfig::deserialize));
        B = Decorator.<DecoratorConfig, MagmaDecorator>register("magma", new MagmaDecorator(CountDecoratorConfig::deserialize));
        C = Decorator.<DecoratorConfig, EmeraldOreDecorator>register("emerald_ore", new EmeraldOreDecorator(NopeDecoratorConfig::deserialize));
        D = Decorator.<DecoratorConfig, LakeLakeDecorator>register("lava_lake", new LakeLakeDecorator(LakeDecoratorConfig::deserialize));
        E = Decorator.<DecoratorConfig, WaterLakeDecorator>register("water_lake", new WaterLakeDecorator(LakeDecoratorConfig::deserialize));
        F = Decorator.<DecoratorConfig, DungeonsDecorator>register("dungeons", new DungeonsDecorator(DungeonDecoratorConfig::deserialize));
        G = Decorator.<DecoratorConfig, DarkOakTreeDecorator>register("dark_oak_tree", new DarkOakTreeDecorator(NopeDecoratorConfig::deserialize));
        H = Decorator.<DecoratorConfig, IcebergDecorator>register("iceberg", new IcebergDecorator(ChanceDecoratorConfig::deserialize));
        I = Decorator.<DecoratorConfig, LightGemChanceDecorator>register("light_gem_chance", new LightGemChanceDecorator(CountDecoratorConfig::deserialize));
        J = Decorator.<DecoratorConfig, EndIslandDecorator>register("end_island", new EndIslandDecorator(NopeDecoratorConfig::deserialize));
        K = Decorator.<DecoratorConfig, ChorusPlantDecorator>register("chorus_plant", new ChorusPlantDecorator(NopeDecoratorConfig::deserialize));
        L = Decorator.<DecoratorConfig, EndGatewayDecorator>register("end_gateway", new EndGatewayDecorator(NopeDecoratorConfig::deserialize));
    }
}
