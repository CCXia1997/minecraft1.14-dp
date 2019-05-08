package net.minecraft.village;

import org.apache.logging.log4j.LogManager;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Optional;
import com.google.common.collect.Sets;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.BlockPos;
import java.util.stream.Stream;
import java.util.function.Predicate;
import com.mojang.datafixers.Dynamic;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Set;
import java.util.Map;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.DynamicSerializable;

public class PointOfInterestSet implements DynamicSerializable
{
    private static final Logger LOGGER;
    private final Short2ObjectMap<PointOfInterest> pointsOfInterestByPos;
    private final Map<PointOfInterestType, Set<PointOfInterest>> pointsOfInterestByType;
    private final Runnable updateListener;
    private boolean valid;
    
    public PointOfInterestSet(final Runnable updateListener) {
        this.pointsOfInterestByPos = (Short2ObjectMap<PointOfInterest>)new Short2ObjectOpenHashMap();
        this.pointsOfInterestByType = Maps.newHashMap();
        this.updateListener = updateListener;
        this.valid = true;
    }
    
    public <T> PointOfInterestSet(final Runnable runnable, final Dynamic<T> dynamic) {
        this.pointsOfInterestByPos = (Short2ObjectMap<PointOfInterest>)new Short2ObjectOpenHashMap();
        this.pointsOfInterestByType = Maps.newHashMap();
        this.updateListener = runnable;
        try {
            this.valid = dynamic.get("Valid").asBoolean(false);
            dynamic.get("Records").asStream().forEach(dynamic -> this.add(new PointOfInterest(dynamic, runnable)));
        }
        catch (Exception exception3) {
            PointOfInterestSet.LOGGER.error("Failed to load POI chunk", (Throwable)exception3);
            this.clear();
            this.valid = false;
        }
    }
    
    public Stream<PointOfInterest> get(final Predicate<PointOfInterestType> predicate, final PointOfInterestStorage.OccupationStatus occupationStatus) {
        return this.pointsOfInterestByType.entrySet().stream().filter(entry -> predicate.test(entry.getKey())).<PointOfInterest>flatMap(entry -> entry.getValue().stream()).filter(occupationStatus.getPredicate());
    }
    
    public void add(final BlockPos blockPos, final PointOfInterestType pointOfInterestType) {
        if (this.add(new PointOfInterest(blockPos, pointOfInterestType, this.updateListener))) {
            PointOfInterestSet.LOGGER.debug(String.format("Added POI of type %s @ %s", pointOfInterestType, blockPos));
            this.updateListener.run();
        }
    }
    
    private boolean add(final PointOfInterest poi) {
        final BlockPos blockPos2 = poi.getPos();
        final PointOfInterestType pointOfInterestType3 = poi.getType();
        final short short4 = ChunkSectionPos.packToShort(blockPos2);
        final PointOfInterest pointOfInterest5 = (PointOfInterest)this.pointsOfInterestByPos.get(short4);
        if (pointOfInterest5 == null) {
            this.pointsOfInterestByPos.put(short4, poi);
            this.pointsOfInterestByType.computeIfAbsent(pointOfInterestType3, pointOfInterestType -> Sets.newHashSet()).add(poi);
            return true;
        }
        if (pointOfInterestType3.equals(pointOfInterest5.getType())) {
            return false;
        }
        throw new IllegalStateException("POI data mismatch: already registered at " + blockPos2);
    }
    
    public void remove(final BlockPos pos) {
        final PointOfInterest pointOfInterest2 = (PointOfInterest)this.pointsOfInterestByPos.remove(ChunkSectionPos.packToShort(pos));
        if (pointOfInterest2 == null) {
            PointOfInterestSet.LOGGER.error("POI data mismatch: never registered at " + pos);
            return;
        }
        this.pointsOfInterestByType.get(pointOfInterest2.getType()).remove(pointOfInterest2);
        PointOfInterestSet.LOGGER.debug(String.format("Removed POI of type %s @ %s", pointOfInterest2.getType(), pointOfInterest2.getPos()));
        this.updateListener.run();
    }
    
    public boolean releaseTicket(final BlockPos pos) {
        final PointOfInterest pointOfInterest2 = (PointOfInterest)this.pointsOfInterestByPos.get(ChunkSectionPos.packToShort(pos));
        if (pointOfInterest2 == null) {
            throw new IllegalStateException("POI never registered at " + pos);
        }
        final boolean boolean3 = pointOfInterest2.releaseTicket();
        this.updateListener.run();
        return boolean3;
    }
    
    public boolean test(final BlockPos pos, final Predicate<PointOfInterestType> predicate) {
        final short short3 = ChunkSectionPos.packToShort(pos);
        final PointOfInterest pointOfInterest4 = (PointOfInterest)this.pointsOfInterestByPos.get(short3);
        return pointOfInterest4 != null && predicate.test(pointOfInterest4.getType());
    }
    
    public Optional<PointOfInterestType> getType(final BlockPos pos) {
        final short short2 = ChunkSectionPos.packToShort(pos);
        final PointOfInterest pointOfInterest3 = (PointOfInterest)this.pointsOfInterestByPos.get(short2);
        return (pointOfInterest3 != null) ? Optional.<PointOfInterestType>of(pointOfInterest3.getType()) : Optional.<PointOfInterestType>empty();
    }
    
    @Override
    public <T> T serialize(final DynamicOps<T> ops) {
        final T object2 = (T)ops.createList((Stream)this.pointsOfInterestByPos.values().stream().map(pointOfInterest -> pointOfInterest.<T>serialize(ops)));
        return (T)ops.createMap((Map)ImmutableMap.of(ops.createString("Records"), object2, ops.createString("Valid"), ops.createBoolean(this.valid)));
    }
    
    public void updatePointsOfInterest(final Consumer<BiConsumer<BlockPos, PointOfInterestType>> consumer) {
        if (!this.valid) {
            final Short2ObjectMap<PointOfInterest> short2ObjectMap2 = (Short2ObjectMap<PointOfInterest>)new Short2ObjectOpenHashMap((Short2ObjectMap)this.pointsOfInterestByPos);
            this.clear();
            final short short4;
            final Short2ObjectMap short2ObjectMap3;
            final PointOfInterest pointOfInterest5;
            consumer.accept((blockPos, pointOfInterestType) -> {
                short4 = ChunkSectionPos.packToShort(blockPos);
                pointOfInterest5 = (PointOfInterest)short2ObjectMap3.computeIfAbsent(short4, integer -> new PointOfInterest(blockPos, pointOfInterestType, this.updateListener));
                this.add(pointOfInterest5);
                return;
            });
            this.valid = true;
            this.updateListener.run();
        }
    }
    
    private void clear() {
        this.pointsOfInterestByPos.clear();
        this.pointsOfInterestByType.clear();
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
