package net.minecraft.village;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.SystemUtil;
import java.util.function.Function;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.function.BiFunction;
import java.util.Set;
import java.util.Arrays;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Stream;
import com.google.common.collect.Maps;
import java.util.UUID;
import java.util.Map;

public class VillagerGossips
{
    private final Map<UUID, Reputation> entityReputation;
    
    public VillagerGossips() {
        this.entityReputation = Maps.newHashMap();
    }
    
    private Stream<GossipEntry> entries() {
        return this.entityReputation.entrySet().stream().<GossipEntry>flatMap(entry -> entry.getValue().entriesFor((UUID)entry.getKey()));
    }
    
    private Collection<GossipEntry> pickGossips(final Random random, final int count) {
        final List<GossipEntry> list3 = this.entries().collect(Collectors.toList());
        if (list3.isEmpty()) {
            return Collections.emptyList();
        }
        final int[] arr4 = new int[list3.size()];
        int integer5 = 0;
        for (int integer6 = 0; integer6 < list3.size(); ++integer6) {
            final GossipEntry gossipEntry7 = list3.get(integer6);
            integer5 += Math.abs(gossipEntry7.getValue());
            arr4[integer6] = integer5 - 1;
        }
        final Set<GossipEntry> set6 = Sets.<GossipEntry>newIdentityHashSet();
        for (int integer7 = 0; integer7 < count; ++integer7) {
            final int integer8 = random.nextInt(integer5);
            final int integer9 = Arrays.binarySearch(arr4, integer8);
            set6.add(list3.get((integer9 < 0) ? (-integer9 - 1) : integer9));
        }
        return set6;
    }
    
    private Reputation getReputationFor(final UUID target) {
        return this.entityReputation.computeIfAbsent(target, uUID -> new Reputation());
    }
    
    public void shareGossipFrom(final VillagerGossips from, final Random random, final int count) {
        final Collection<GossipEntry> collection4 = from.pickGossips(random, count);
        final int integer2;
        collection4.forEach(gossipEntry -> {
            integer2 = gossipEntry.value - gossipEntry.type.value;
            if (integer2 > 2) {
                this.getReputationFor(gossipEntry.target).associatedGossip.mergeInt(gossipEntry.type, integer2, (BiFunction)VillagerGossips::max);
            }
        });
    }
    //根据uuid来获取对应的声望？并且获取声望的强度作为返回值，猜测reputation可能是同一类流言value的总和
    public int getReputationFor(final UUID target, final Predicate<VillageGossipType> predicate) {
        final Reputation reputation3 = this.entityReputation.get(target);
        return (reputation3 != null) ? reputation3.getValue(predicate) : 0;
    }
    
    public long getGossipCount(final VillageGossipType villageGossipType, final DoublePredicate doublePredicate) {
        return this.entityReputation.values().stream().filter(reputation -> doublePredicate.test(reputation.associatedGossip.getOrDefault(villageGossipType, 0) * villageGossipType.multiplier)).count();
    }
    
    public void startGossip(final UUID target, final VillageGossipType type, final int value) {
        this.getReputationFor(target).associatedGossip.mergeInt(type, value, (integer2, integer3) -> this.mergeReputation(type, integer2, integer3));
    }
    
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createList((Stream)this.entries().map(gossipEntry -> gossipEntry.<T>serialize(ops)).map(Dynamic::getValue)));
    }
    
    public void deserialize(final Dynamic<?> dynamic) {
        dynamic.asStream().map(GossipEntry::deserialize).flatMap(SystemUtil::stream).forEach(gossipEntry -> this.getReputationFor(gossipEntry.target).associatedGossip.put(gossipEntry.type, gossipEntry.value));
    }
    
    private static int max(final int left, final int right) {
        return Math.max(left, right);
    }
    
    private int mergeReputation(final VillageGossipType type, final int left, final int right) {
        final int integer4 = left + right;
        return (integer4 > type.maxReputation) ? Math.max(type.maxReputation, left) : integer4;
    }
    
    static class GossipEntry
    {
        public final UUID target;
        public final VillageGossipType type;
        public final int value;
        
        public GossipEntry(final UUID target, final VillageGossipType type, final int value) {
            this.target = target;
            this.type = type;
            this.value = value;
        }
        
        public int getValue() {
            return this.value * this.type.multiplier;
        }
        
        @Override
        public String toString() {
            return "GossipEntry{target=" + this.target + ", type=" + this.type + ", value=" + this.value + '}';
        }
        
        public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
            return SystemUtil.<T>writeUuid("Target", this.target, (com.mojang.datafixers.Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("Type"), ops.createString(this.type.key), ops.createString("Value"), ops.createInt(this.value)))));
        }
        
        public static Optional<GossipEntry> deserialize(final Dynamic<?> dynamic) {
            return dynamic.get("Type").asString().map(VillageGossipType::byKey).<GossipEntry>flatMap(villageGossipType -> SystemUtil.readUuid("Target", dynamic).flatMap(uUID -> dynamic.get("Value").asNumber().map(number -> new GossipEntry(uUID, villageGossipType, number.intValue()))));
        }
    }
    
    static class Reputation
    {
        private final Object2IntMap<VillageGossipType> associatedGossip;
        
        private Reputation() {
            this.associatedGossip = (Object2IntMap<VillageGossipType>)new Object2IntOpenHashMap();
        }
        
        public int getValue(final Predicate<VillageGossipType> predicate) {
            return this.associatedGossip.object2IntEntrySet().stream().filter(entry -> predicate.test((VillageGossipType)entry.getKey())).mapToInt(entry -> entry.getIntValue() * ((VillageGossipType)entry.getKey()).multiplier).sum();
        }
        
        public Stream<GossipEntry> entriesFor(final UUID target) {
            return this.associatedGossip.object2IntEntrySet().stream().<GossipEntry>map(entry -> new GossipEntry(target, (VillageGossipType)entry.getKey(), entry.getIntValue()));
        }
    }
}
