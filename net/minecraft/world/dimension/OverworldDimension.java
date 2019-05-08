package net.minecraft.world.dimension;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.Heightmap;
import net.minecraft.tag.BlockTags;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.block.BlockState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.biome.source.CheckerboardBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGenerator;
import net.minecraft.world.gen.chunk.CavesChunkGenerator;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.DebugChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.biome.source.CheckerboardBiomeSourceConfig;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import com.mojang.datafixers.types.JsonOps;
import com.google.gson.JsonElement;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.World;

public class OverworldDimension extends Dimension
{
    public OverworldDimension(final World world, final DimensionType type) {
        super(world, type);
    }
    
    @Override
    public DimensionType getType() {
        return DimensionType.a;
    }
    
    @Override
    public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
        final LevelGeneratorType levelGeneratorType1 = this.world.getLevelProperties().getGeneratorType();
        final ChunkGeneratorType<FlatChunkGeneratorConfig, FlatChunkGenerator> chunkGeneratorType2 = ChunkGeneratorType.e;
        final ChunkGeneratorType<DebugChunkGeneratorConfig, DebugChunkGenerator> chunkGeneratorType3 = ChunkGeneratorType.d;
        final ChunkGeneratorType<CavesChunkGeneratorConfig, CavesChunkGenerator> chunkGeneratorType4 = ChunkGeneratorType.b;
        final ChunkGeneratorType<FloatingIslandsChunkGeneratorConfig, FloatingIslandsChunkGenerator> chunkGeneratorType5 = ChunkGeneratorType.c;
        final ChunkGeneratorType<OverworldChunkGeneratorConfig, OverworldChunkGenerator> chunkGeneratorType6 = ChunkGeneratorType.a;
        final BiomeSourceType<FixedBiomeSourceConfig, FixedBiomeSource> biomeSourceType7 = BiomeSourceType.FIXED;
        final BiomeSourceType<VanillaLayeredBiomeSourceConfig, VanillaLayeredBiomeSource> biomeSourceType8 = BiomeSourceType.VANILLA_LAYERED;
        final BiomeSourceType<CheckerboardBiomeSourceConfig, CheckerboardBiomeSource> biomeSourceType9 = BiomeSourceType.CHECKERBOARD;
        if (levelGeneratorType1 == LevelGeneratorType.FLAT) {
            final FlatChunkGeneratorConfig flatChunkGeneratorConfig10 = FlatChunkGeneratorConfig.fromDynamic(new Dynamic((DynamicOps)NbtOps.INSTANCE, this.world.getLevelProperties().getGeneratorOptions()));
            final FixedBiomeSourceConfig fixedBiomeSourceConfig11 = biomeSourceType7.getConfig().setBiome(flatChunkGeneratorConfig10.getBiome());
            return chunkGeneratorType2.create(this.world, biomeSourceType7.applyConfig(fixedBiomeSourceConfig11), flatChunkGeneratorConfig10);
        }
        if (levelGeneratorType1 == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES) {
            final FixedBiomeSourceConfig fixedBiomeSourceConfig12 = biomeSourceType7.getConfig().setBiome(Biomes.c);
            return chunkGeneratorType3.create(this.world, biomeSourceType7.applyConfig(fixedBiomeSourceConfig12), chunkGeneratorType3.createSettings());
        }
        if (levelGeneratorType1 == LevelGeneratorType.BUFFET) {
            BiomeSource biomeSource10 = null;
            final JsonElement jsonElement11 = (JsonElement)Dynamic.convert((DynamicOps)NbtOps.INSTANCE, (DynamicOps)JsonOps.INSTANCE, this.world.getLevelProperties().getGeneratorOptions());
            final JsonObject jsonObject12 = jsonElement11.getAsJsonObject();
            if (jsonObject12.has("biome_source") && jsonObject12.getAsJsonObject("biome_source").has("type") && jsonObject12.getAsJsonObject("biome_source").has("options")) {
                final BiomeSourceType<?, ?> biomeSourceType10 = Registry.BIOME_SOURCE_TYPE.get(new Identifier(jsonObject12.getAsJsonObject("biome_source").getAsJsonPrimitive("type").getAsString()));
                final JsonObject jsonObject13 = jsonObject12.getAsJsonObject("biome_source").getAsJsonObject("options");
                Biome[] arr15 = { Biomes.a };
                if (jsonObject13.has("biomes")) {
                    final JsonArray jsonArray16 = jsonObject13.getAsJsonArray("biomes");
                    arr15 = ((jsonArray16.size() > 0) ? new Biome[jsonArray16.size()] : new Biome[] { Biomes.a });
                    for (int integer17 = 0; integer17 < jsonArray16.size(); ++integer17) {
                        arr15[integer17] = Registry.BIOME.getOrEmpty(new Identifier(jsonArray16.get(integer17).getAsString())).orElse(Biomes.a);
                    }
                }
                if (BiomeSourceType.FIXED == biomeSourceType10) {
                    final FixedBiomeSourceConfig fixedBiomeSourceConfig13 = biomeSourceType7.getConfig().setBiome(arr15[0]);
                    biomeSource10 = biomeSourceType7.applyConfig(fixedBiomeSourceConfig13);
                }
                if (BiomeSourceType.CHECKERBOARD == biomeSourceType10) {
                    final int integer18 = jsonObject13.has("size") ? jsonObject13.getAsJsonPrimitive("size").getAsInt() : 2;
                    final CheckerboardBiomeSourceConfig checkerboardBiomeSourceConfig17 = biomeSourceType9.getConfig().a(arr15).a(integer18);
                    biomeSource10 = biomeSourceType9.applyConfig(checkerboardBiomeSourceConfig17);
                }
                if (BiomeSourceType.VANILLA_LAYERED == biomeSourceType10) {
                    final VanillaLayeredBiomeSourceConfig vanillaLayeredBiomeSourceConfig16 = biomeSourceType8.getConfig().setGeneratorSettings(new OverworldChunkGeneratorConfig()).setLevelProperties(this.world.getLevelProperties());
                    biomeSource10 = biomeSourceType8.applyConfig(vanillaLayeredBiomeSourceConfig16);
                }
            }
            if (biomeSource10 == null) {
                biomeSource10 = biomeSourceType7.applyConfig(biomeSourceType7.getConfig().setBiome(Biomes.a));
            }
            BlockState blockState13 = Blocks.b.getDefaultState();
            BlockState blockState14 = Blocks.A.getDefaultState();
            if (jsonObject12.has("chunk_generator") && jsonObject12.getAsJsonObject("chunk_generator").has("options")) {
                if (jsonObject12.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_block")) {
                    final String string15 = jsonObject12.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_block").getAsString();
                    blockState13 = Registry.BLOCK.get(new Identifier(string15)).getDefaultState();
                }
                if (jsonObject12.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_fluid")) {
                    final String string15 = jsonObject12.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_fluid").getAsString();
                    blockState14 = Registry.BLOCK.get(new Identifier(string15)).getDefaultState();
                }
            }
            if (jsonObject12.has("chunk_generator") && jsonObject12.getAsJsonObject("chunk_generator").has("type")) {
                final ChunkGeneratorType<?, ?> chunkGeneratorType7 = Registry.CHUNK_GENERATOR_TYPE.get(new Identifier(jsonObject12.getAsJsonObject("chunk_generator").getAsJsonPrimitive("type").getAsString()));
                if (ChunkGeneratorType.b == chunkGeneratorType7) {
                    final CavesChunkGeneratorConfig cavesChunkGeneratorConfig16 = chunkGeneratorType4.createSettings();
                    cavesChunkGeneratorConfig16.setDefaultBlock(blockState13);
                    cavesChunkGeneratorConfig16.setDefaultFluid(blockState14);
                    return chunkGeneratorType4.create(this.world, biomeSource10, cavesChunkGeneratorConfig16);
                }
                if (ChunkGeneratorType.c == chunkGeneratorType7) {
                    final FloatingIslandsChunkGeneratorConfig floatingIslandsChunkGeneratorConfig16 = chunkGeneratorType5.createSettings();
                    floatingIslandsChunkGeneratorConfig16.withCenter(new BlockPos(0, 64, 0));
                    floatingIslandsChunkGeneratorConfig16.setDefaultBlock(blockState13);
                    floatingIslandsChunkGeneratorConfig16.setDefaultFluid(blockState14);
                    return chunkGeneratorType5.create(this.world, biomeSource10, floatingIslandsChunkGeneratorConfig16);
                }
            }
            final OverworldChunkGeneratorConfig overworldChunkGeneratorConfig15 = chunkGeneratorType6.createSettings();
            overworldChunkGeneratorConfig15.setDefaultBlock(blockState13);
            overworldChunkGeneratorConfig15.setDefaultFluid(blockState14);
            return chunkGeneratorType6.create(this.world, biomeSource10, overworldChunkGeneratorConfig15);
        }
        final OverworldChunkGeneratorConfig overworldChunkGeneratorConfig16 = chunkGeneratorType6.createSettings();
        final VanillaLayeredBiomeSourceConfig vanillaLayeredBiomeSourceConfig17 = biomeSourceType8.getConfig().setLevelProperties(this.world.getLevelProperties()).setGeneratorSettings(overworldChunkGeneratorConfig16);
        return chunkGeneratorType6.create(this.world, biomeSourceType8.applyConfig(vanillaLayeredBiomeSourceConfig17), overworldChunkGeneratorConfig16);
    }
    
    @Nullable
    @Override
    public BlockPos getSpawningBlockInChunk(final ChunkPos chunkPos, final boolean checkMobSpawnValidity) {
        for (int integer3 = chunkPos.getStartX(); integer3 <= chunkPos.getEndX(); ++integer3) {
            for (int integer4 = chunkPos.getStartZ(); integer4 <= chunkPos.getEndZ(); ++integer4) {
                final BlockPos blockPos5 = this.getTopSpawningBlockPosition(integer3, integer4, checkMobSpawnValidity);
                if (blockPos5 != null) {
                    return blockPos5;
                }
            }
        }
        return null;
    }
    
    @Nullable
    @Override
    public BlockPos getTopSpawningBlockPosition(final int x, final int z, final boolean checkMobSpawnValidity) {
        final BlockPos.Mutable mutable4 = new BlockPos.Mutable(x, 0, z);
        final Biome biome5 = this.world.getBiome(mutable4);
        final BlockState blockState6 = biome5.getSurfaceConfig().getTopMaterial();
        if (checkMobSpawnValidity && !blockState6.getBlock().matches(BlockTags.K)) {
            return null;
        }
        final WorldChunk worldChunk7 = this.world.getChunk(x >> 4, z >> 4);
        final int integer8 = worldChunk7.sampleHeightmap(Heightmap.Type.e, x & 0xF, z & 0xF);
        if (integer8 < 0) {
            return null;
        }
        if (worldChunk7.sampleHeightmap(Heightmap.Type.b, x & 0xF, z & 0xF) > worldChunk7.sampleHeightmap(Heightmap.Type.d, x & 0xF, z & 0xF)) {
            return null;
        }
        for (int integer9 = integer8 + 1; integer9 >= 0; --integer9) {
            mutable4.set(x, integer9, z);
            final BlockState blockState7 = this.world.getBlockState(mutable4);
            if (!blockState7.getFluidState().isEmpty()) {
                break;
            }
            if (blockState7.equals(blockState6)) {
                return mutable4.up().toImmutable();
            }
        }
        return null;
    }
    
    @Override
    public float getSkyAngle(final long timeOfDay, final float delta) {
        final double double4 = MathHelper.fractionalPart(timeOfDay / 24000.0 - 0.25);
        final double double5 = 0.5 - Math.cos(double4 * 3.141592653589793) / 2.0;
        return (float)(double4 * 2.0 + double5) / 3.0f;
    }
    
    @Override
    public boolean hasVisibleSky() {
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public Vec3d getFogColor(final float skyAngle, final float tickDelta) {
        float float3 = MathHelper.cos(skyAngle * 6.2831855f) * 2.0f + 0.5f;
        float3 = MathHelper.clamp(float3, 0.0f, 1.0f);
        float float4 = 0.7529412f;
        float float5 = 0.84705883f;
        float float6 = 1.0f;
        float4 *= float3 * 0.94f + 0.06f;
        float5 *= float3 * 0.94f + 0.06f;
        float6 *= float3 * 0.91f + 0.09f;
        return new Vec3d(float4, float5, float6);
    }
    
    @Override
    public boolean canPlayersSleep() {
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderFog(final int entityX, final int entityZ) {
        return false;
    }
}
