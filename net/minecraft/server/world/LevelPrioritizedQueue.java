package net.minecraft.server.world;

import javax.annotation.Nullable;
import java.util.function.Function;
import com.mojang.datafixers.util.Either;
import java.util.stream.Stream;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.collect.Lists;
import net.minecraft.world.chunk.ChunkPos;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Optional;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.List;

public class LevelPrioritizedQueue<T>
{
    public static final int LEVEL_COUNT;
    private final List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>> levelToPosToElements;
    private volatile int firstNonEmptyLevel;
    private final String name;
    private final LongSet chunkPositions;
    private final int maxSize;
    
    public LevelPrioritizedQueue(final String name, final int maxSize) {
        this.levelToPosToElements = IntStream.range(0, LevelPrioritizedQueue.LEVEL_COUNT).mapToObj(integer -> new Long2ObjectLinkedOpenHashMap()).collect(Collectors.toList());
        this.firstNonEmptyLevel = LevelPrioritizedQueue.LEVEL_COUNT;
        this.chunkPositions = (LongSet)new LongOpenHashSet();
        this.name = name;
        this.maxSize = maxSize;
    }
    
    protected void updateLevel(final int fromLevel, final ChunkPos pos, final int toLevel) {
        if (fromLevel >= LevelPrioritizedQueue.LEVEL_COUNT) {
            return;
        }
        final Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap4 = this.levelToPosToElements.get(fromLevel);
        final List<Optional<T>> list5 = (List<Optional<T>>)long2ObjectLinkedOpenHashMap4.remove(pos.toLong());
        if (fromLevel == this.firstNonEmptyLevel) {
            while (this.firstNonEmptyLevel < LevelPrioritizedQueue.LEVEL_COUNT && this.levelToPosToElements.get(this.firstNonEmptyLevel).isEmpty()) {
                ++this.firstNonEmptyLevel;
            }
        }
        if (list5 != null && !list5.isEmpty()) {
            ((List)this.levelToPosToElements.get(toLevel).computeIfAbsent(pos.toLong(), long1 -> Lists.newArrayList())).addAll(list5);
            this.firstNonEmptyLevel = Math.min(this.firstNonEmptyLevel, toLevel);
        }
    }
    
    protected void add(final Optional<T> element, final long pos, final int level) {
        ((List)this.levelToPosToElements.get(level).computeIfAbsent(pos, long1 -> Lists.newArrayList())).add(element);
        this.firstNonEmptyLevel = Math.min(this.firstNonEmptyLevel, level);
    }
    
    protected void clearPosition(final long pos, final boolean includePresent) {
        for (final Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap5 : this.levelToPosToElements) {
            final List<Optional<T>> list6 = (List<Optional<T>>)long2ObjectLinkedOpenHashMap5.get(pos);
            if (list6 == null) {
                continue;
            }
            if (includePresent) {
                list6.clear();
            }
            else {
                list6.removeIf(optional -> !optional.isPresent());
            }
            if (!list6.isEmpty()) {
                continue;
            }
            long2ObjectLinkedOpenHashMap5.remove(pos);
        }
        while (this.firstNonEmptyLevel < LevelPrioritizedQueue.LEVEL_COUNT && this.levelToPosToElements.get(this.firstNonEmptyLevel).isEmpty()) {
            ++this.firstNonEmptyLevel;
        }
        this.chunkPositions.remove(pos);
    }
    
    private Runnable createPositionAdder(final long pos) {
        return () -> this.chunkPositions.add(pos);
    }
    
    @Nullable
    public Stream<Either<T, Runnable>> poll() {
        if (this.chunkPositions.size() >= this.maxSize) {
            return null;
        }
        if (this.firstNonEmptyLevel < LevelPrioritizedQueue.LEVEL_COUNT) {
            final int integer1 = this.firstNonEmptyLevel;
            final Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap2 = this.levelToPosToElements.get(integer1);
            final long long3 = long2ObjectLinkedOpenHashMap2.firstLongKey();
            final List<Optional<T>> list5 = (List<Optional<T>>)long2ObjectLinkedOpenHashMap2.removeFirst();
            while (this.firstNonEmptyLevel < LevelPrioritizedQueue.LEVEL_COUNT && this.levelToPosToElements.get(this.firstNonEmptyLevel).isEmpty()) {
                ++this.firstNonEmptyLevel;
            }
            return list5.stream().<Either<T, Runnable>>map(optional3 -> optional3.map(Either::left).orElseGet(() -> Either.right(this.createPositionAdder(long3))));
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.name + " " + this.firstNonEmptyLevel + "...";
    }
    
    static {
        LEVEL_COUNT = ThreadedAnvilChunkStorage.MAX_LEVEL + 2;
    }
}
