package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public abstract class SurfaceBuilder<C extends SurfaceConfig>
{
    public static final BlockState AIR;
    public static final BlockState DIRT;
    public static final BlockState GRASS_BLOCK;
    public static final BlockState PODZOL;
    public static final BlockState GRAVEL;
    public static final BlockState STONE;
    public static final BlockState COARSE_DIRT;
    public static final BlockState SAND;
    public static final BlockState RED_SAND;
    public static final BlockState WHITE_TERRACOTTA;
    public static final BlockState MYCELIUM;
    public static final BlockState NETHERRACK;
    public static final BlockState END_STONE;
    public static final TernarySurfaceConfig AIR_CONFIG;
    public static final TernarySurfaceConfig PODZOL_CONFIG;
    public static final TernarySurfaceConfig GRAVEL_CONFIG;
    public static final TernarySurfaceConfig GRASS_CONFIG;
    public static final TernarySurfaceConfig DIRT_CONFIG;
    public static final TernarySurfaceConfig STONE_CONFIG;
    public static final TernarySurfaceConfig COARSE_DIRT_CONFIG;
    public static final TernarySurfaceConfig SAND_CONFIG;
    public static final TernarySurfaceConfig GRASS_SAND_UNDERWATER_CONFIG;
    public static final TernarySurfaceConfig SAND_SAND_UNDERWATER_CONFIG;
    public static final TernarySurfaceConfig BADLANDS_CONFIG;
    public static final TernarySurfaceConfig MYCELIUM_CONFIG;
    public static final TernarySurfaceConfig NETHER_CONFIG;
    public static final TernarySurfaceConfig END_CONFIG;
    public static final SurfaceBuilder<TernarySurfaceConfig> DEFAULT;
    public static final SurfaceBuilder<TernarySurfaceConfig> MOUNTAIN;
    public static final SurfaceBuilder<TernarySurfaceConfig> SHATTERED_SAVANNA;
    public static final SurfaceBuilder<TernarySurfaceConfig> GRAVELLY_MOUNTAIN;
    public static final SurfaceBuilder<TernarySurfaceConfig> GIANT_TREE_TAIGA;
    public static final SurfaceBuilder<TernarySurfaceConfig> SWAMP;
    public static final SurfaceBuilder<TernarySurfaceConfig> BADLANDS;
    public static final SurfaceBuilder<TernarySurfaceConfig> WOODED_BADLANDS;
    public static final SurfaceBuilder<TernarySurfaceConfig> ERODED_BADLANDS;
    public static final SurfaceBuilder<TernarySurfaceConfig> FROZEN_OCEAN;
    public static final SurfaceBuilder<TernarySurfaceConfig> NETHER;
    public static final SurfaceBuilder<TernarySurfaceConfig> NOPE;
    private final Function<Dynamic<?>, ? extends C> factory;
    
    private static <C extends SurfaceConfig, F extends SurfaceBuilder<C>> F register(final String string, final F surfaceBuilder) {
        return Registry.<F>register(Registry.SURFACE_BUILDER, string, surfaceBuilder);
    }
    
    public SurfaceBuilder(final Function<Dynamic<?>, ? extends C> function) {
        this.factory = function;
    }
    
    public abstract void generate(final Random arg1, final Chunk arg2, final Biome arg3, final int arg4, final int arg5, final int arg6, final double arg7, final BlockState arg8, final BlockState arg9, final int arg10, final long arg11, final C arg12);
    
    public void initSeed(final long seed) {
    }
    
    static {
        AIR = Blocks.AIR.getDefaultState();
        DIRT = Blocks.j.getDefaultState();
        GRASS_BLOCK = Blocks.i.getDefaultState();
        PODZOL = Blocks.l.getDefaultState();
        GRAVEL = Blocks.E.getDefaultState();
        STONE = Blocks.b.getDefaultState();
        COARSE_DIRT = Blocks.k.getDefaultState();
        SAND = Blocks.C.getDefaultState();
        RED_SAND = Blocks.D.getDefaultState();
        WHITE_TERRACOTTA = Blocks.fx.getDefaultState();
        MYCELIUM = Blocks.dL.getDefaultState();
        NETHERRACK = Blocks.cJ.getDefaultState();
        END_STONE = Blocks.dW.getDefaultState();
        AIR_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.AIR, SurfaceBuilder.AIR, SurfaceBuilder.AIR);
        PODZOL_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.PODZOL, SurfaceBuilder.DIRT, SurfaceBuilder.GRAVEL);
        GRAVEL_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.GRAVEL, SurfaceBuilder.GRAVEL, SurfaceBuilder.GRAVEL);
        GRASS_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.GRASS_BLOCK, SurfaceBuilder.DIRT, SurfaceBuilder.GRAVEL);
        DIRT_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.DIRT, SurfaceBuilder.DIRT, SurfaceBuilder.GRAVEL);
        STONE_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.STONE, SurfaceBuilder.STONE, SurfaceBuilder.GRAVEL);
        COARSE_DIRT_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.COARSE_DIRT, SurfaceBuilder.DIRT, SurfaceBuilder.GRAVEL);
        SAND_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.SAND, SurfaceBuilder.SAND, SurfaceBuilder.GRAVEL);
        GRASS_SAND_UNDERWATER_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.GRASS_BLOCK, SurfaceBuilder.DIRT, SurfaceBuilder.SAND);
        SAND_SAND_UNDERWATER_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.SAND, SurfaceBuilder.SAND, SurfaceBuilder.SAND);
        BADLANDS_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.RED_SAND, SurfaceBuilder.WHITE_TERRACOTTA, SurfaceBuilder.GRAVEL);
        MYCELIUM_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.MYCELIUM, SurfaceBuilder.DIRT, SurfaceBuilder.GRAVEL);
        NETHER_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.NETHERRACK, SurfaceBuilder.NETHERRACK, SurfaceBuilder.NETHERRACK);
        END_CONFIG = new TernarySurfaceConfig(SurfaceBuilder.END_STONE, SurfaceBuilder.END_STONE, SurfaceBuilder.END_STONE);
        DEFAULT = SurfaceBuilder.<SurfaceConfig, DefaultSurfaceBuilder>register("default", new DefaultSurfaceBuilder(TernarySurfaceConfig::deserialize));
        MOUNTAIN = SurfaceBuilder.<SurfaceConfig, MountainSurfaceBuilder>register("mountain", new MountainSurfaceBuilder(TernarySurfaceConfig::deserialize));
        SHATTERED_SAVANNA = SurfaceBuilder.<SurfaceConfig, ShatteredSavannaSurfaceBuilder>register("shattered_savanna", new ShatteredSavannaSurfaceBuilder(TernarySurfaceConfig::deserialize));
        GRAVELLY_MOUNTAIN = SurfaceBuilder.<SurfaceConfig, GravellyMountainSurfaceBuilder>register("gravelly_mountain", new GravellyMountainSurfaceBuilder(TernarySurfaceConfig::deserialize));
        GIANT_TREE_TAIGA = SurfaceBuilder.<SurfaceConfig, GiantTreeTaigaSurfaceBuilder>register("giant_tree_taiga", new GiantTreeTaigaSurfaceBuilder(TernarySurfaceConfig::deserialize));
        SWAMP = SurfaceBuilder.<SurfaceConfig, SwampSurfaceBuilder>register("swamp", new SwampSurfaceBuilder(TernarySurfaceConfig::deserialize));
        BADLANDS = SurfaceBuilder.<SurfaceConfig, BadlandsSurfaceBuilder>register("badlands", new BadlandsSurfaceBuilder(TernarySurfaceConfig::deserialize));
        WOODED_BADLANDS = SurfaceBuilder.<SurfaceConfig, WoodedBadlandsSurfaceBuilder>register("wooded_badlands", new WoodedBadlandsSurfaceBuilder(TernarySurfaceConfig::deserialize));
        ERODED_BADLANDS = SurfaceBuilder.<SurfaceConfig, ErodedBadlandsSurfaceBuilder>register("eroded_badlands", new ErodedBadlandsSurfaceBuilder(TernarySurfaceConfig::deserialize));
        FROZEN_OCEAN = SurfaceBuilder.<SurfaceConfig, FrozenOceanSurfaceBuilder>register("frozen_ocean", new FrozenOceanSurfaceBuilder(TernarySurfaceConfig::deserialize));
        NETHER = SurfaceBuilder.<SurfaceConfig, NetherSurfaceBuilder>register("nether", new NetherSurfaceBuilder(TernarySurfaceConfig::deserialize));
        NOPE = SurfaceBuilder.<SurfaceConfig, NopeSurfaceBuilder>register("nope", new NopeSurfaceBuilder(TernarySurfaceConfig::deserialize));
    }
}
