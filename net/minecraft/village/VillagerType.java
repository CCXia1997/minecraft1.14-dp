package net.minecraft.village;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import net.minecraft.world.biome.Biomes;
import java.util.HashMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import java.util.Map;

public interface VillagerType
{
    public static final VillagerType DESERT = create("desert");
    public static final VillagerType JUNGLE = create("jungle");
    public static final VillagerType PLAINS = create("plains");
    public static final VillagerType SAVANNA = create("savanna");
    public static final VillagerType SNOW = create("snow");
    public static final VillagerType SWAMP = create("swamp");
    public static final VillagerType TAIGA = create("taiga");
    public static final Map<Biome, VillagerType> BIOME_TO_TYPE = SystemUtil.<Map<Biome, VillagerType>>consume(Maps.newHashMap(), hashMap -> {
        hashMap.put(Biomes.M, VillagerType.DESERT);
        hashMap.put(Biomes.O, VillagerType.DESERT);
        hashMap.put(Biomes.d, VillagerType.DESERT);
        hashMap.put(Biomes.s, VillagerType.DESERT);
        hashMap.put(Biomes.ac, VillagerType.DESERT);
        hashMap.put(Biomes.at, VillagerType.DESERT);
        hashMap.put(Biomes.av, VillagerType.DESERT);
        hashMap.put(Biomes.au, VillagerType.DESERT);
        hashMap.put(Biomes.N, VillagerType.DESERT);
        hashMap.put(Biomes.aw, VillagerType.JUNGLE);
        hashMap.put(Biomes.ax, VillagerType.JUNGLE);
        hashMap.put(Biomes.w, VillagerType.JUNGLE);
        hashMap.put(Biomes.y, VillagerType.JUNGLE);
        hashMap.put(Biomes.x, VillagerType.JUNGLE);
        hashMap.put(Biomes.ai, VillagerType.JUNGLE);
        hashMap.put(Biomes.aj, VillagerType.JUNGLE);
        hashMap.put(Biomes.L, VillagerType.SAVANNA);
        hashMap.put(Biomes.K, VillagerType.SAVANNA);
        hashMap.put(Biomes.ar, VillagerType.SAVANNA);
        hashMap.put(Biomes.as, VillagerType.SAVANNA);
        hashMap.put(Biomes.Z, VillagerType.SNOW);
        hashMap.put(Biomes.l, VillagerType.SNOW);
        hashMap.put(Biomes.m, VillagerType.SNOW);
        hashMap.put(Biomes.ah, VillagerType.SNOW);
        hashMap.put(Biomes.B, VillagerType.SNOW);
        hashMap.put(Biomes.o, VillagerType.SNOW);
        hashMap.put(Biomes.F, VillagerType.SNOW);
        hashMap.put(Biomes.G, VillagerType.SNOW);
        hashMap.put(Biomes.an, VillagerType.SNOW);
        hashMap.put(Biomes.n, VillagerType.SNOW);
        hashMap.put(Biomes.h, VillagerType.SWAMP);
        hashMap.put(Biomes.ag, VillagerType.SWAMP);
        hashMap.put(Biomes.ao, VillagerType.TAIGA);
        hashMap.put(Biomes.ap, VillagerType.TAIGA);
        hashMap.put(Biomes.H, VillagerType.TAIGA);
        hashMap.put(Biomes.I, VillagerType.TAIGA);
        hashMap.put(Biomes.ad, VillagerType.TAIGA);
        hashMap.put(Biomes.aq, VillagerType.TAIGA);
        hashMap.put(Biomes.v, VillagerType.TAIGA);
        hashMap.put(Biomes.e, VillagerType.TAIGA);
        hashMap.put(Biomes.g, VillagerType.TAIGA);
        hashMap.put(Biomes.u, VillagerType.TAIGA);
        hashMap.put(Biomes.af, VillagerType.TAIGA);
        hashMap.put(Biomes.J, VillagerType.TAIGA);
    });
    
    default VillagerType create(final String id) {
        return Registry.register(Registry.VILLAGER_TYPE, new Identifier(id), new VillagerType() {
            @Override
            public String toString() {
                return id;
            }
        });
    }
    
    default VillagerType forBiome(final Biome biome) {
        return VillagerType.BIOME_TO_TYPE.getOrDefault(biome, VillagerType.PLAINS);
    }
}
