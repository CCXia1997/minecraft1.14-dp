package net.minecraft.entity;

import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import net.minecraft.world.Heightmap;
import java.util.Map;

public class SpawnRestriction
{
    private static final Map<EntityType<?>, Entry> mapping;
    
    private static void register(final EntityType<?> entityType, final Location location, final Heightmap.Type type) {
        SpawnRestriction.mapping.put(entityType, new Entry(type, location));
    }
    
    @Nullable
    public static Location getLocation(final EntityType<?> entityType) {
        final Entry entry2 = SpawnRestriction.mapping.get(entityType);
        return (entry2 == null) ? null : entry2.location;
    }
    
    public static Heightmap.Type getHeightMapType(@Nullable final EntityType<?> entityType) {
        final Entry entry2 = SpawnRestriction.mapping.get(entityType);
        return (entry2 == null) ? Heightmap.Type.f : entry2.heightMapType;
    }
    
    static {
        mapping = Maps.newHashMap();
        register(EntityType.COD, Location.b, Heightmap.Type.f);
        register(EntityType.DOLPHIN, Location.b, Heightmap.Type.f);
        register(EntityType.DROWNED, Location.b, Heightmap.Type.f);
        register(EntityType.GUARDIAN, Location.b, Heightmap.Type.f);
        register(EntityType.PUFFERFISH, Location.b, Heightmap.Type.f);
        register(EntityType.SALMON, Location.b, Heightmap.Type.f);
        register(EntityType.SQUID, Location.b, Heightmap.Type.f);
        register(EntityType.TROPICAL_FISH, Location.b, Heightmap.Type.f);
        register(EntityType.BAT, Location.a, Heightmap.Type.f);
        register(EntityType.BLAZE, Location.a, Heightmap.Type.f);
        register(EntityType.CAVE_SPIDER, Location.a, Heightmap.Type.f);
        register(EntityType.CHICKEN, Location.a, Heightmap.Type.f);
        register(EntityType.COW, Location.a, Heightmap.Type.f);
        register(EntityType.CREEPER, Location.a, Heightmap.Type.f);
        register(EntityType.DONKEY, Location.a, Heightmap.Type.f);
        register(EntityType.ENDERMAN, Location.a, Heightmap.Type.f);
        register(EntityType.ENDERMITE, Location.a, Heightmap.Type.f);
        register(EntityType.ENDER_DRAGON, Location.a, Heightmap.Type.f);
        register(EntityType.GHAST, Location.a, Heightmap.Type.f);
        register(EntityType.GIANT, Location.a, Heightmap.Type.f);
        register(EntityType.HORSE, Location.a, Heightmap.Type.f);
        register(EntityType.HUSK, Location.a, Heightmap.Type.f);
        register(EntityType.IRON_GOLEM, Location.a, Heightmap.Type.f);
        register(EntityType.LLAMA, Location.a, Heightmap.Type.f);
        register(EntityType.MAGMA_CUBE, Location.a, Heightmap.Type.f);
        register(EntityType.MOOSHROOM, Location.a, Heightmap.Type.f);
        register(EntityType.MULE, Location.a, Heightmap.Type.f);
        register(EntityType.OCELOT, Location.a, Heightmap.Type.e);
        register(EntityType.PARROT, Location.a, Heightmap.Type.e);
        register(EntityType.PIG, Location.a, Heightmap.Type.f);
        register(EntityType.PILLAGER, Location.a, Heightmap.Type.f);
        register(EntityType.POLAR_BEAR, Location.a, Heightmap.Type.f);
        register(EntityType.RABBIT, Location.a, Heightmap.Type.f);
        register(EntityType.SHEEP, Location.a, Heightmap.Type.f);
        register(EntityType.SILVERFISH, Location.a, Heightmap.Type.f);
        register(EntityType.SKELETON, Location.a, Heightmap.Type.f);
        register(EntityType.SKELETON_HORSE, Location.a, Heightmap.Type.f);
        register(EntityType.SLIME, Location.a, Heightmap.Type.f);
        register(EntityType.SNOW_GOLEM, Location.a, Heightmap.Type.f);
        register(EntityType.SPIDER, Location.a, Heightmap.Type.f);
        register(EntityType.STRAY, Location.a, Heightmap.Type.f);
        register(EntityType.TURTLE, Location.a, Heightmap.Type.f);
        register(EntityType.VILLAGER, Location.a, Heightmap.Type.f);
        register(EntityType.WITCH, Location.a, Heightmap.Type.f);
        register(EntityType.WITHER, Location.a, Heightmap.Type.f);
        register(EntityType.WITHER_SKELETON, Location.a, Heightmap.Type.f);
        register(EntityType.WOLF, Location.a, Heightmap.Type.f);
        register(EntityType.ZOMBIE, Location.a, Heightmap.Type.f);
        register(EntityType.ZOMBIE_HORSE, Location.a, Heightmap.Type.f);
        register(EntityType.ZOMBIE_PIGMAN, Location.a, Heightmap.Type.f);
        register(EntityType.ZOMBIE_VILLAGER, Location.a, Heightmap.Type.f);
    }
    
    static class Entry
    {
        private final Heightmap.Type heightMapType;
        private final Location location;
        
        public Entry(final Heightmap.Type type, final Location location) {
            this.heightMapType = type;
            this.location = location;
        }
    }
    
    public enum Location
    {
        a, 
        b;
    }
}
