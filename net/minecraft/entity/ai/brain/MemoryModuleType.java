package net.minecraft.entity.ai.brain;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import java.util.Optional;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import java.util.List;
import net.minecraft.util.GlobalPos;

public class MemoryModuleType<U>
{
    public static final MemoryModuleType<Void> a;
    public static final MemoryModuleType<GlobalPos> b;
    public static final MemoryModuleType<GlobalPos> c;
    public static final MemoryModuleType<GlobalPos> d;
    public static final MemoryModuleType<List<GlobalPos>> e;
    public static final MemoryModuleType<List<LivingEntity>> f;
    public static final MemoryModuleType<List<LivingEntity>> g;
    public static final MemoryModuleType<List<LivingEntity>> h;
    public static final MemoryModuleType<List<PlayerEntity>> i;
    public static final MemoryModuleType<PlayerEntity> j;
    public static final MemoryModuleType<WalkTarget> k;
    public static final MemoryModuleType<LookTarget> l;
    public static final MemoryModuleType<LivingEntity> m;
    public static final MemoryModuleType<VillagerEntity> n;
    public static final MemoryModuleType<Path> o;
    public static final MemoryModuleType<List<GlobalPos>> p;
    public static final MemoryModuleType<BlockPos> q;
    public static final MemoryModuleType<DamageSource> r;
    public static final MemoryModuleType<LivingEntity> s;
    public static final MemoryModuleType<LivingEntity> t;
    public static final MemoryModuleType<VillagerEntity.GolemSpawnCondition> u;
    public static final MemoryModuleType<GlobalPos> v;
    public static final MemoryModuleType<Long> w;
    private final Optional<Function<Dynamic<?>, U>> factory;
    
    private MemoryModuleType(final Optional<Function<Dynamic<?>, U>> optional) {
        this.factory = optional;
    }
    
    public Identifier getId() {
        return Registry.MEMORY_MODULE_TYPE.getId(this);
    }
    
    @Override
    public String toString() {
        return this.getId().toString();
    }
    
    public Optional<Function<Dynamic<?>, U>> getFactory() {
        return this.factory;
    }
    
    private static <U> MemoryModuleType<U> register(final String string, final Optional<Function<Dynamic<?>, U>> optional) {
        return Registry.<MemoryModuleType<U>>register(Registry.MEMORY_MODULE_TYPE, new Identifier(string), new MemoryModuleType<U>(optional));
    }
    
    static {
        a = MemoryModuleType.<Void>register("dummy", Optional.<Function<Dynamic<?>, Void>>empty());
        b = MemoryModuleType.<GlobalPos>register("home", Optional.of((Function<Dynamic<?>, U>)GlobalPos::deserialize));
        c = MemoryModuleType.<GlobalPos>register("job_site", Optional.of((Function<Dynamic<?>, U>)GlobalPos::deserialize));
        d = MemoryModuleType.<GlobalPos>register("meeting_point", Optional.of((Function<Dynamic<?>, U>)GlobalPos::deserialize));
        e = MemoryModuleType.<List<GlobalPos>>register("secondary_job_site", Optional.<Function<Dynamic<?>, List<GlobalPos>>>empty());
        f = MemoryModuleType.<List<LivingEntity>>register("mobs", Optional.<Function<Dynamic<?>, List<LivingEntity>>>empty());
        g = MemoryModuleType.<List<LivingEntity>>register("visible_mobs", Optional.<Function<Dynamic<?>, List<LivingEntity>>>empty());
        h = MemoryModuleType.<List<LivingEntity>>register("visible_villager_babies", Optional.<Function<Dynamic<?>, List<LivingEntity>>>empty());
        i = MemoryModuleType.<List<PlayerEntity>>register("nearest_players", Optional.<Function<Dynamic<?>, List<PlayerEntity>>>empty());
        j = MemoryModuleType.<PlayerEntity>register("nearest_visible_player", Optional.<Function<Dynamic<?>, PlayerEntity>>empty());
        k = MemoryModuleType.<WalkTarget>register("walk_target", Optional.<Function<Dynamic<?>, WalkTarget>>empty());
        l = MemoryModuleType.<LookTarget>register("look_target", Optional.<Function<Dynamic<?>, LookTarget>>empty());
        m = MemoryModuleType.<LivingEntity>register("interaction_target", Optional.<Function<Dynamic<?>, LivingEntity>>empty());
        n = MemoryModuleType.<VillagerEntity>register("breed_target", Optional.<Function<Dynamic<?>, VillagerEntity>>empty());
        o = MemoryModuleType.<Path>register("path", Optional.<Function<Dynamic<?>, Path>>empty());
        p = MemoryModuleType.<List<GlobalPos>>register("interactable_doors", Optional.<Function<Dynamic<?>, List<GlobalPos>>>empty());
        q = MemoryModuleType.<BlockPos>register("nearest_bed", Optional.<Function<Dynamic<?>, BlockPos>>empty());
        r = MemoryModuleType.<DamageSource>register("hurt_by", Optional.<Function<Dynamic<?>, DamageSource>>empty());
        s = MemoryModuleType.<LivingEntity>register("hurt_by_entity", Optional.<Function<Dynamic<?>, LivingEntity>>empty());
        t = MemoryModuleType.<LivingEntity>register("nearest_hostile", Optional.<Function<Dynamic<?>, LivingEntity>>empty());
        u = MemoryModuleType.<VillagerEntity.GolemSpawnCondition>register("golem_spawn_conditions", Optional.<Function<Dynamic<?>, VillagerEntity.GolemSpawnCondition>>empty());
        v = MemoryModuleType.<GlobalPos>register("hiding_place", Optional.<Function<Dynamic<?>, GlobalPos>>empty());
        w = MemoryModuleType.<Long>register("heard_bell_time", Optional.<Function<Dynamic<?>, Long>>empty());
    }
}
