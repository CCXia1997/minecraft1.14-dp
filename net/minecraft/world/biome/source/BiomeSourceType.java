package net.minecraft.world.biome.source;

import net.minecraft.util.registry.Registry;
import java.util.function.Supplier;
import java.util.function.Function;

public class BiomeSourceType<C extends BiomeSourceConfig, T extends BiomeSource>
{
    public static final BiomeSourceType<CheckerboardBiomeSourceConfig, CheckerboardBiomeSource> CHECKERBOARD;
    public static final BiomeSourceType<FixedBiomeSourceConfig, FixedBiomeSource> FIXED;
    public static final BiomeSourceType<VanillaLayeredBiomeSourceConfig, VanillaLayeredBiomeSource> VANILLA_LAYERED;
    public static final BiomeSourceType<TheEndBiomeSourceConfig, TheEndBiomeSource> THE_END;
    private final Function<C, T> biomeSource;
    private final Supplier<C> config;
    
    private static <C extends BiomeSourceConfig, T extends BiomeSource> BiomeSourceType<C, T> register(final String id, final Function<C, T> biomeSource, final Supplier<C> config) {
        return Registry.<BiomeSourceType<C, T>>register(Registry.BIOME_SOURCE_TYPE, id, new BiomeSourceType<C, T>(biomeSource, config));
    }
    
    public BiomeSourceType(final Function<C, T> biomeSource, final Supplier<C> config) {
        this.biomeSource = biomeSource;
        this.config = config;
    }
    
    public T applyConfig(final C config) {
        return this.biomeSource.apply(config);
    }
    
    public C getConfig() {
        return this.config.get();
    }
    
    static {
        CHECKERBOARD = BiomeSourceType.<CheckerboardBiomeSourceConfig, CheckerboardBiomeSource>register("checkerboard", CheckerboardBiomeSource::new, CheckerboardBiomeSourceConfig::new);
        FIXED = BiomeSourceType.<FixedBiomeSourceConfig, FixedBiomeSource>register("fixed", FixedBiomeSource::new, FixedBiomeSourceConfig::new);
        VANILLA_LAYERED = BiomeSourceType.<VanillaLayeredBiomeSourceConfig, VanillaLayeredBiomeSource>register("vanilla_layered", VanillaLayeredBiomeSource::new, VanillaLayeredBiomeSourceConfig::new);
        THE_END = BiomeSourceType.<TheEndBiomeSourceConfig, TheEndBiomeSource>register("the_end", TheEndBiomeSource::new, TheEndBiomeSourceConfig::new);
    }
}
