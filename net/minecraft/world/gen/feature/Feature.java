package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.SystemUtil;
import net.minecraft.state.property.Property;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.Blocks;
import java.util.Locale;
import com.google.common.collect.HashBiMap;
import java.util.Collections;
import net.minecraft.world.biome.Biome;
import java.util.Random;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.IWorld;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import java.util.List;
import com.google.common.collect.BiMap;
import net.minecraft.world.gen.ProbabilityConfig;

public abstract class Feature<FC extends FeatureConfig>
{
    public static final StructureFeature<PillagerOutpostFeatureConfig> PILLAGER_OUTPOST;
    public static final StructureFeature<MineshaftFeatureConfig> MINESHAFT;
    public static final StructureFeature<DefaultFeatureConfig> WOODLAND_MANSION;
    public static final StructureFeature<DefaultFeatureConfig> JUNGLE_TEMPLE;
    public static final StructureFeature<DefaultFeatureConfig> DESERT_PYRAMID;
    public static final StructureFeature<DefaultFeatureConfig> IGLOO;
    public static final StructureFeature<ShipwreckFeatureConfig> SHIPWRECK;
    public static final SwampHutFeature SWAMP_HUT;
    public static final StructureFeature<DefaultFeatureConfig> STRONGHOLD;
    public static final StructureFeature<DefaultFeatureConfig> OCEAN_MONUMENT;
    public static final StructureFeature<OceanRuinFeatureConfig> OCEAN_RUIN;
    public static final StructureFeature<DefaultFeatureConfig> NETHER_BRIDGE;
    public static final StructureFeature<DefaultFeatureConfig> END_CITY;
    public static final StructureFeature<BuriedTreasureFeatureConfig> BURIED_TREASURE;
    public static final StructureFeature<VillageFeatureConfig> VILLAGE;
    public static final Feature<DefaultFeatureConfig> q;
    public static final Feature<DefaultFeatureConfig> r;
    public static final Feature<DefaultFeatureConfig> s;
    public static final Feature<DefaultFeatureConfig> t;
    public static final Feature<DefaultFeatureConfig> u;
    public static final Feature<DefaultFeatureConfig> v;
    public static final Feature<DefaultFeatureConfig> w;
    public static final Feature<DefaultFeatureConfig> x;
    public static final Feature<DefaultFeatureConfig> y;
    public static final Feature<DefaultFeatureConfig> z;
    public static final Feature<DefaultFeatureConfig> A;
    public static final Feature<DefaultFeatureConfig> B;
    public static final Feature<DefaultFeatureConfig> C;
    public static final Feature<DefaultFeatureConfig> D;
    public static final FlowerFeature DEFAULT_FLOWER;
    public static final FlowerFeature FOREST_FLOWER;
    public static final FlowerFeature PLAIN_FLOWER;
    public static final FlowerFeature SWAMP_FLOWER;
    public static final FlowerFeature GENERAL_FOREST_FLOWER;
    public static final Feature<DefaultFeatureConfig> J;
    public static final Feature<DefaultFeatureConfig> K;
    public static final Feature<GrassFeatureConfig> L;
    public static final Feature<DefaultFeatureConfig> M;
    public static final Feature<DefaultFeatureConfig> N;
    public static final Feature<DefaultFeatureConfig> O;
    public static final Feature<DefaultFeatureConfig> P;
    public static final Feature<DefaultFeatureConfig> Q;
    public static final Feature<DefaultFeatureConfig> R;
    public static final Feature<DefaultFeatureConfig> S;
    public static final Feature<DefaultFeatureConfig> T;
    public static final Feature<DefaultFeatureConfig> U;
    public static final Feature<DefaultFeatureConfig> V;
    public static final Feature<DefaultFeatureConfig> W;
    public static final Feature<DefaultFeatureConfig> X;
    public static final Feature<DefaultFeatureConfig> Y;
    public static final Feature<DefaultFeatureConfig> Z;
    public static final Feature<DefaultFeatureConfig> aa;
    public static final Feature<DefaultFeatureConfig> ab;
    public static final Feature<DefaultFeatureConfig> ac;
    public static final Feature<DefaultFeatureConfig> ad;
    public static final Feature<IcebergFeatureConfig> ae;
    public static final Feature<BoulderFeatureConfig> af;
    public static final Feature<DefaultFeatureConfig> ag;
    public static final Feature<DefaultFeatureConfig> ah;
    public static final Feature<DefaultFeatureConfig> ai;
    public static final Feature<DefaultFeatureConfig> aj;
    public static final Feature<DefaultFeatureConfig> ak;
    public static final Feature<BushFeatureConfig> al;
    public static final Feature<DiskFeatureConfig> am;
    public static final Feature<DoublePlantFeatureConfig> an;
    public static final Feature<NetherSpringFeatureConfig> ao;
    public static final Feature<IcePatchFeatureConfig> ap;
    public static final Feature<LakeFeatureConfig> aq;
    public static final Feature<OreFeatureConfig> ar;
    public static final Feature<RandomRandomFeatureConfig> as;
    public static final Feature<RandomFeatureConfig> at;
    public static final Feature<SimpleRandomFeatureConfig> au;
    public static final Feature<RandomBooleanFeatureConfig> av;
    public static final Feature<EmeraldOreFeatureConfig> aw;
    public static final Feature<SpringFeatureConfig> ax;
    public static final Feature<EndSpikeFeatureConfig> ay;
    public static final Feature<DefaultFeatureConfig> az;
    public static final Feature<DefaultFeatureConfig> aA;
    public static final Feature<EndGatewayFeatureConfig> aB;
    public static final Feature<SeagrassFeatureConfig> aC;
    public static final Feature<DefaultFeatureConfig> aD;
    public static final Feature<DefaultFeatureConfig> aE;
    public static final Feature<DefaultFeatureConfig> aF;
    public static final Feature<DefaultFeatureConfig> aG;
    public static final Feature<SeaPickleFeatureConfig> aH;
    public static final Feature<SimpleBlockFeatureConfig> aI;
    public static final Feature<ProbabilityConfig> aJ;
    public static final Feature<DecoratedFeatureConfig> aK;
    public static final Feature<DecoratedFeatureConfig> aL;
    public static final Feature<DefaultFeatureConfig> aM;
    public static final Feature<FillLayerFeatureConfig> aN;
    public static final BonusChestFeature BONUS_CHEST;
    public static final BiMap<String, StructureFeature<?>> STRUCTURES;
    public static final List<StructureFeature<?>> JIGSAW_STRUCTURES;
    private final Function<Dynamic<?>, ? extends FC> configDeserializer;
    protected final boolean emitNeighborBlockUpdates;
    
    private static <C extends FeatureConfig, F extends Feature<C>> F register(final String name, final F feature) {
        return Registry.<F>register(Registry.FEATURE, name, feature);
    }
    
    public Feature(final Function<Dynamic<?>, ? extends FC> configDeserializer) {
        this.configDeserializer = configDeserializer;
        this.emitNeighborBlockUpdates = false;
    }
    
    public Feature(final Function<Dynamic<?>, ? extends FC> configDeserializer, final boolean emitNeighborBlockUpdates) {
        this.configDeserializer = configDeserializer;
        this.emitNeighborBlockUpdates = emitNeighborBlockUpdates;
    }
    
    public FC deserializeConfig(final Dynamic<?> dynamic) {
        return (FC)this.configDeserializer.apply(dynamic);
    }
    
    protected void setBlockState(final ModifiableWorld world, final BlockPos pos, final BlockState state) {
        if (this.emitNeighborBlockUpdates) {
            world.setBlockState(pos, state, 3);
        }
        else {
            world.setBlockState(pos, state, 2);
        }
    }
    
    public abstract boolean generate(final IWorld arg1, final ChunkGenerator<? extends ChunkGeneratorConfig> arg2, final Random arg3, final BlockPos arg4, final FC arg5);
    
    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return Collections.<Biome.SpawnEntry>emptyList();
    }
    
    public List<Biome.SpawnEntry> getCreatureSpawns() {
        return Collections.<Biome.SpawnEntry>emptyList();
    }
    
    static {
        PILLAGER_OUTPOST = Feature.<FeatureConfig, PillagerOutpostFeature>register("pillager_outpost", new PillagerOutpostFeature(PillagerOutpostFeatureConfig::deserialize));
        MINESHAFT = Feature.<FeatureConfig, MineshaftFeature>register("mineshaft", new MineshaftFeature(MineshaftFeatureConfig::deserialize));
        WOODLAND_MANSION = Feature.<FeatureConfig, WoodlandMansionFeature>register("woodland_mansion", new WoodlandMansionFeature(DefaultFeatureConfig::deserialize));
        JUNGLE_TEMPLE = Feature.<FeatureConfig, JungleTempleFeature>register("jungle_temple", new JungleTempleFeature(DefaultFeatureConfig::deserialize));
        DESERT_PYRAMID = Feature.<FeatureConfig, DesertPyramidFeature>register("desert_pyramid", new DesertPyramidFeature(DefaultFeatureConfig::deserialize));
        IGLOO = Feature.<FeatureConfig, IglooFeature>register("igloo", new IglooFeature(DefaultFeatureConfig::deserialize));
        SHIPWRECK = Feature.<FeatureConfig, ShipwreckFeature>register("shipwreck", new ShipwreckFeature(ShipwreckFeatureConfig::deserialize));
        SWAMP_HUT = Feature.<FeatureConfig, SwampHutFeature>register("swamp_hut", new SwampHutFeature(DefaultFeatureConfig::deserialize));
        STRONGHOLD = Feature.<FeatureConfig, StrongholdFeature>register("stronghold", new StrongholdFeature(DefaultFeatureConfig::deserialize));
        OCEAN_MONUMENT = Feature.<FeatureConfig, OceanMonumentFeature>register("ocean_monument", new OceanMonumentFeature(DefaultFeatureConfig::deserialize));
        OCEAN_RUIN = Feature.<FeatureConfig, OceanRuinFeature>register("ocean_ruin", new OceanRuinFeature(OceanRuinFeatureConfig::deserialize));
        NETHER_BRIDGE = Feature.<FeatureConfig, NetherFortressFeature>register("nether_bridge", new NetherFortressFeature(DefaultFeatureConfig::deserialize));
        END_CITY = Feature.<FeatureConfig, EndCityFeature>register("end_city", new EndCityFeature(DefaultFeatureConfig::deserialize));
        BURIED_TREASURE = Feature.<FeatureConfig, BuriedTreasureFeature>register("buried_treasure", new BuriedTreasureFeature(BuriedTreasureFeatureConfig::deserialize));
        VILLAGE = Feature.<FeatureConfig, VillageFeature>register("village", new VillageFeature(VillageFeatureConfig::deserialize));
        q = Feature.<FeatureConfig, LargeOakTreeFeature>register("fancy_tree", new LargeOakTreeFeature(DefaultFeatureConfig::deserialize, false));
        r = Feature.<FeatureConfig, BirchTreeFeature>register("birch_tree", new BirchTreeFeature(DefaultFeatureConfig::deserialize, false, false));
        s = Feature.<FeatureConfig, BirchTreeFeature>register("super_birch_tree", new BirchTreeFeature(DefaultFeatureConfig::deserialize, false, true));
        t = Feature.<FeatureConfig, JungleGroundBushFeature>register("jungle_ground_bush", new JungleGroundBushFeature(DefaultFeatureConfig::deserialize, Blocks.L.getDefaultState(), Blocks.ag.getDefaultState()));
        u = Feature.<FeatureConfig, JungleTreeFeature>register("jungle_tree", new JungleTreeFeature(DefaultFeatureConfig::deserialize, false, 4, Blocks.L.getDefaultState(), Blocks.aj.getDefaultState(), true));
        v = Feature.<FeatureConfig, PineTreeFeature>register("pine_tree", new PineTreeFeature(DefaultFeatureConfig::deserialize));
        w = Feature.<FeatureConfig, DarkOakTreeFeature>register("dark_oak_tree", new DarkOakTreeFeature(DefaultFeatureConfig::deserialize, false));
        x = Feature.<FeatureConfig, SavannaTreeFeature>register("savanna_tree", new SavannaTreeFeature(DefaultFeatureConfig::deserialize, false));
        y = Feature.<FeatureConfig, SpruceTreeFeature>register("spruce_tree", new SpruceTreeFeature(DefaultFeatureConfig::deserialize, false));
        z = Feature.<FeatureConfig, SwampTreeFeature>register("swamp_tree", new SwampTreeFeature(DefaultFeatureConfig::deserialize));
        A = Feature.<FeatureConfig, OakTreeFeature>register("normal_tree", new OakTreeFeature(DefaultFeatureConfig::deserialize, false));
        B = Feature.<FeatureConfig, MegaJungleTreeFeature>register("mega_jungle_tree", new MegaJungleTreeFeature(DefaultFeatureConfig::deserialize, false, 10, 20, Blocks.L.getDefaultState(), Blocks.aj.getDefaultState()));
        C = Feature.<FeatureConfig, MegaPineTreeFeature>register("mega_pine_tree", new MegaPineTreeFeature(DefaultFeatureConfig::deserialize, false, false));
        D = Feature.<FeatureConfig, MegaPineTreeFeature>register("mega_spruce_tree", new MegaPineTreeFeature(DefaultFeatureConfig::deserialize, false, true));
        DEFAULT_FLOWER = Feature.<FeatureConfig, DefaultFlowerFeature>register("default_flower", new DefaultFlowerFeature(DefaultFeatureConfig::deserialize));
        FOREST_FLOWER = Feature.<FeatureConfig, ForestFlowerFeature>register("forest_flower", new ForestFlowerFeature(DefaultFeatureConfig::deserialize));
        PLAIN_FLOWER = Feature.<FeatureConfig, PlainFlowerFeature>register("plain_flower", new PlainFlowerFeature(DefaultFeatureConfig::deserialize));
        SWAMP_FLOWER = Feature.<FeatureConfig, SwampFlowerFeature>register("swamp_flower", new SwampFlowerFeature(DefaultFeatureConfig::deserialize));
        GENERAL_FOREST_FLOWER = Feature.<FeatureConfig, GeneralForestFlowerFeature>register("general_forest_flower", new GeneralForestFlowerFeature(DefaultFeatureConfig::deserialize));
        J = Feature.<FeatureConfig, JungleGrassFeature>register("jungle_grass", new JungleGrassFeature(DefaultFeatureConfig::deserialize));
        K = Feature.<FeatureConfig, TaigaGrassFeature>register("taiga_grass", new TaigaGrassFeature(DefaultFeatureConfig::deserialize));
        L = Feature.<FeatureConfig, GrassFeature>register("grass", new GrassFeature(GrassFeatureConfig::deserialize));
        M = Feature.<FeatureConfig, VoidStartPlatformFeature>register("void_start_platform", new VoidStartPlatformFeature(DefaultFeatureConfig::deserialize));
        N = Feature.<FeatureConfig, CactusFeature>register("cactus", new CactusFeature(DefaultFeatureConfig::deserialize));
        O = Feature.<FeatureConfig, DeadBushFeature>register("dead_bush", new DeadBushFeature(DefaultFeatureConfig::deserialize));
        P = Feature.<FeatureConfig, DesertWellFeature>register("desert_well", new DesertWellFeature(DefaultFeatureConfig::deserialize));
        Q = Feature.<FeatureConfig, FossilFeature>register("fossil", new FossilFeature(DefaultFeatureConfig::deserialize));
        R = Feature.<FeatureConfig, NetherFireFeature>register("hell_fire", new NetherFireFeature(DefaultFeatureConfig::deserialize));
        S = Feature.<FeatureConfig, HugeRedMushroomFeature>register("huge_red_mushroom", new HugeRedMushroomFeature(DefaultFeatureConfig::deserialize));
        T = Feature.<FeatureConfig, HugeBrownMushroomFeature>register("huge_brown_mushroom", new HugeBrownMushroomFeature(DefaultFeatureConfig::deserialize));
        U = Feature.<FeatureConfig, IceSpikeFeature>register("ice_spike", new IceSpikeFeature(DefaultFeatureConfig::deserialize));
        V = Feature.<FeatureConfig, GlowstoneBlobFeature>register("glowstone_blob", new GlowstoneBlobFeature(DefaultFeatureConfig::deserialize));
        W = Feature.<FeatureConfig, MelonFeature>register("melon", new MelonFeature(DefaultFeatureConfig::deserialize));
        X = Feature.<FeatureConfig, WildCropFeature>register("pumpkin", new WildCropFeature(DefaultFeatureConfig::deserialize, Blocks.cI.getDefaultState()));
        Y = Feature.<FeatureConfig, ReedFeature>register("reed", new ReedFeature(DefaultFeatureConfig::deserialize));
        Z = Feature.<FeatureConfig, FreezeTopLayerFeature>register("freeze_top_layer", new FreezeTopLayerFeature(DefaultFeatureConfig::deserialize));
        aa = Feature.<FeatureConfig, VinesFeature>register("vines", new VinesFeature(DefaultFeatureConfig::deserialize));
        ab = Feature.<FeatureConfig, WaterlilyFeature>register("waterlily", new WaterlilyFeature(DefaultFeatureConfig::deserialize));
        ac = Feature.<FeatureConfig, DungeonFeature>register("monster_room", new DungeonFeature(DefaultFeatureConfig::deserialize));
        ad = Feature.<FeatureConfig, BlueIceFeature>register("blue_ice", new BlueIceFeature(DefaultFeatureConfig::deserialize));
        ae = Feature.<FeatureConfig, IcebergFeature>register("iceberg", new IcebergFeature(IcebergFeatureConfig::deserialize));
        af = Feature.<FeatureConfig, ForestRockFeature>register("forest_rock", new ForestRockFeature(BoulderFeatureConfig::deserialize));
        ag = Feature.<FeatureConfig, HayPileFeature>register("hay_pile", new HayPileFeature(DefaultFeatureConfig::deserialize));
        ah = Feature.<FeatureConfig, SnowPileFeature>register("snow_pile", new SnowPileFeature(DefaultFeatureConfig::deserialize));
        ai = Feature.<FeatureConfig, IcePileFeature>register("ice_pile", new IcePileFeature(DefaultFeatureConfig::deserialize));
        aj = Feature.<FeatureConfig, MelonPileFeature>register("melon_pile", new MelonPileFeature(DefaultFeatureConfig::deserialize));
        ak = Feature.<FeatureConfig, PumpkinPileFeature>register("pumpkin_pile", new PumpkinPileFeature(DefaultFeatureConfig::deserialize));
        al = Feature.<FeatureConfig, BushFeature>register("bush", new BushFeature(BushFeatureConfig::deserialize));
        am = Feature.<FeatureConfig, DiskFeature>register("disk", new DiskFeature(DiskFeatureConfig::deserialize));
        an = Feature.<FeatureConfig, DoublePlantFeature>register("double_plant", new DoublePlantFeature(DoublePlantFeatureConfig::deserialize));
        ao = Feature.<FeatureConfig, NetherSpringFeature>register("nether_spring", new NetherSpringFeature(NetherSpringFeatureConfig::deserialize));
        ap = Feature.<FeatureConfig, IcePatchFeature>register("ice_patch", new IcePatchFeature(IcePatchFeatureConfig::deserialize));
        aq = Feature.<FeatureConfig, LakeFeature>register("lake", new LakeFeature(LakeFeatureConfig::deserialize));
        ar = Feature.<FeatureConfig, OreFeature>register("ore", new OreFeature(OreFeatureConfig::deserialize));
        as = Feature.<FeatureConfig, RandomRandomFeature>register("random_random_selector", new RandomRandomFeature(RandomRandomFeatureConfig::deserialize));
        at = Feature.<FeatureConfig, RandomFeature>register("random_selector", new RandomFeature(RandomFeatureConfig::deserialize));
        au = Feature.<FeatureConfig, SimpleRandomFeature>register("simple_random_selector", new SimpleRandomFeature(SimpleRandomFeatureConfig::deserialize));
        av = Feature.<FeatureConfig, RandomBooleanFeature>register("random_boolean_selector", new RandomBooleanFeature(RandomBooleanFeatureConfig::deserialize));
        aw = Feature.<FeatureConfig, EmeraldOreFeature>register("emerald_ore", new EmeraldOreFeature(EmeraldOreFeatureConfig::deserialize));
        ax = Feature.<FeatureConfig, SpringFeature>register("spring_feature", new SpringFeature(SpringFeatureConfig::deserialize));
        ay = Feature.<FeatureConfig, EndSpikeFeature>register("end_spike", new EndSpikeFeature(EndSpikeFeatureConfig::deserialize));
        az = Feature.<FeatureConfig, EndIslandFeature>register("end_island", new EndIslandFeature(DefaultFeatureConfig::deserialize));
        aA = Feature.<FeatureConfig, ChorusPlantFeature>register("chorus_plant", new ChorusPlantFeature(DefaultFeatureConfig::deserialize));
        aB = Feature.<FeatureConfig, EndGatewayFeature>register("end_gateway", new EndGatewayFeature(EndGatewayFeatureConfig::deserialize));
        aC = Feature.<FeatureConfig, SeagrassFeature>register("seagrass", new SeagrassFeature(SeagrassFeatureConfig::deserialize));
        aD = Feature.<FeatureConfig, KelpFeature>register("kelp", new KelpFeature(DefaultFeatureConfig::deserialize));
        aE = Feature.<FeatureConfig, CoralTreeFeature>register("coral_tree", new CoralTreeFeature(DefaultFeatureConfig::deserialize));
        aF = Feature.<FeatureConfig, CoralMushroomFeature>register("coral_mushroom", new CoralMushroomFeature(DefaultFeatureConfig::deserialize));
        aG = Feature.<FeatureConfig, CoralClawFeature>register("coral_claw", new CoralClawFeature(DefaultFeatureConfig::deserialize));
        aH = Feature.<FeatureConfig, SeaPickleFeature>register("sea_pickle", new SeaPickleFeature(SeaPickleFeatureConfig::deserialize));
        aI = Feature.<FeatureConfig, SimpleBlockFeature>register("simple_block", new SimpleBlockFeature(SimpleBlockFeatureConfig::deserialize));
        aJ = Feature.<FeatureConfig, BambooFeature>register("bamboo", new BambooFeature(ProbabilityConfig::deserialize));
        aK = Feature.<FeatureConfig, DecoratedFeature>register("decorated", new DecoratedFeature(DecoratedFeatureConfig::deserialize));
        aL = Feature.<FeatureConfig, DecoratedFlowerFeature>register("decorated_flower", new DecoratedFlowerFeature(DecoratedFeatureConfig::deserialize));
        aM = Feature.<FeatureConfig, WildCropFeature>register("sweet_berry_bush", new WildCropFeature(DefaultFeatureConfig::deserialize, ((AbstractPropertyContainer<O, BlockState>)Blocks.lW.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)SweetBerryBushBlock.AGE, 3)));
        aN = Feature.<FeatureConfig, FillLayerFeature>register("fill_layer", new FillLayerFeature(FillLayerFeatureConfig::deserialize));
        BONUS_CHEST = Feature.<FeatureConfig, BonusChestFeature>register("bonus_chest", new BonusChestFeature(DefaultFeatureConfig::deserialize));
        STRUCTURES = SystemUtil.<BiMap<String, StructureFeature<?>>>consume(HashBiMap.create(), hashBiMap -> {
            hashBiMap.put("Pillager_Outpost".toLowerCase(Locale.ROOT), Feature.PILLAGER_OUTPOST);
            hashBiMap.put("Mineshaft".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.MINESHAFT);
            hashBiMap.put("Mansion".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.WOODLAND_MANSION);
            hashBiMap.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.JUNGLE_TEMPLE);
            hashBiMap.put("Desert_Pyramid".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.DESERT_PYRAMID);
            hashBiMap.put("Igloo".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.IGLOO);
            hashBiMap.put("Shipwreck".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.SHIPWRECK);
            hashBiMap.put("Swamp_Hut".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.SWAMP_HUT);
            hashBiMap.put("Stronghold".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.STRONGHOLD);
            hashBiMap.put("Monument".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.OCEAN_MONUMENT);
            hashBiMap.put("Ocean_Ruin".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.OCEAN_RUIN);
            hashBiMap.put("Fortress".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.NETHER_BRIDGE);
            hashBiMap.put("EndCity".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.END_CITY);
            hashBiMap.put("Buried_Treasure".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.BURIED_TREASURE);
            hashBiMap.put("Village".toLowerCase(Locale.ROOT), (StructureFeature<PillagerOutpostFeatureConfig>)Feature.VILLAGE);
            return;
        });
        JIGSAW_STRUCTURES = ImmutableList.<StructureFeature<PillagerOutpostFeatureConfig>>of(Feature.PILLAGER_OUTPOST, (StructureFeature<PillagerOutpostFeatureConfig>)Feature.VILLAGE);
    }
}
