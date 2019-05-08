package net.minecraft.world.gen.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.World;
import net.minecraft.util.registry.Registry;
import java.util.function.Supplier;

public class ChunkGeneratorType<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> implements ChunkGeneratorFactory<C, T>
{
    public static final ChunkGeneratorType<OverworldChunkGeneratorConfig, OverworldChunkGenerator> a;
    public static final ChunkGeneratorType<CavesChunkGeneratorConfig, CavesChunkGenerator> b;
    public static final ChunkGeneratorType<FloatingIslandsChunkGeneratorConfig, FloatingIslandsChunkGenerator> c;
    public static final ChunkGeneratorType<DebugChunkGeneratorConfig, DebugChunkGenerator> d;
    public static final ChunkGeneratorType<FlatChunkGeneratorConfig, FlatChunkGenerator> e;
    private final ChunkGeneratorFactory<C, T> factory;
    private final boolean buffetScreenOption;
    private final Supplier<C> settingsSupplier;
    
    private static <C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> ChunkGeneratorType<C, T> register(final String string, final ChunkGeneratorFactory<C, T> chunkGeneratorFactory, final Supplier<C> supplier, final boolean boolean4) {
        return Registry.<ChunkGeneratorType<C, T>>register(Registry.CHUNK_GENERATOR_TYPE, string, new ChunkGeneratorType<C, T>(chunkGeneratorFactory, boolean4, supplier));
    }
    
    public ChunkGeneratorType(final ChunkGeneratorFactory<C, T> factory, final boolean buffetScreenOption, final Supplier<C> settingsSupplier) {
        this.factory = factory;
        this.buffetScreenOption = buffetScreenOption;
        this.settingsSupplier = settingsSupplier;
    }
    
    @Override
    public T create(final World world, final BiomeSource biomeSource, final C config) {
        return this.factory.create(world, biomeSource, config);
    }
    
    public C createSettings() {
        return this.settingsSupplier.get();
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isBuffetScreenOption() {
        return this.buffetScreenOption;
    }
    
    static {
        a = ChunkGeneratorType.<OverworldChunkGeneratorConfig, OverworldChunkGenerator>register("surface", OverworldChunkGenerator::new, OverworldChunkGeneratorConfig::new, true);
        b = ChunkGeneratorType.<CavesChunkGeneratorConfig, CavesChunkGenerator>register("caves", CavesChunkGenerator::new, CavesChunkGeneratorConfig::new, true);
        c = ChunkGeneratorType.<FloatingIslandsChunkGeneratorConfig, FloatingIslandsChunkGenerator>register("floating_islands", FloatingIslandsChunkGenerator::new, FloatingIslandsChunkGeneratorConfig::new, true);
        d = ChunkGeneratorType.<DebugChunkGeneratorConfig, DebugChunkGenerator>register("debug", DebugChunkGenerator::new, DebugChunkGeneratorConfig::new, false);
        e = ChunkGeneratorType.<FlatChunkGeneratorConfig, FlatChunkGenerator>register("flat", FlatChunkGenerator::new, FlatChunkGeneratorConfig::new, false);
    }
}
