package net.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.util.TaskPriority;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.nbt.ListTag;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import java.util.function.Predicate;

public class ChunkTickScheduler<T> implements TickScheduler<T>
{
    protected final Predicate<T> shouldExclude;
    protected final Function<T, Identifier> idToName;
    protected final Function<Identifier, T> nameToId;
    private final ChunkPos pos;
    private final ShortList[] scheduledPositions;
    
    public ChunkTickScheduler(final Predicate<T> shouldExclude, final Function<T, Identifier> idToName, final Function<Identifier, T> nameToId, final ChunkPos pos) {
        this(shouldExclude, idToName, nameToId, pos, new ListTag());
    }
    
    public ChunkTickScheduler(final Predicate<T> shouldExclude, final Function<T, Identifier> idToName, final Function<Identifier, T> nameToId, final ChunkPos pos, final ListTag data) {
        this.scheduledPositions = new ShortList[16];
        this.shouldExclude = shouldExclude;
        this.idToName = idToName;
        this.nameToId = nameToId;
        this.pos = pos;
        for (int integer6 = 0; integer6 < data.size(); ++integer6) {
            final ListTag listTag7 = data.getListTag(integer6);
            for (int integer7 = 0; integer7 < listTag7.size(); ++integer7) {
                Chunk.getList(this.scheduledPositions, integer6).add(listTag7.getShort(integer7));
            }
        }
    }
    
    public ListTag toNbt() {
        return ChunkSerializer.toNbt(this.scheduledPositions);
    }
    
    public void tick(final TickScheduler<T> scheduler, final Function<BlockPos, T> dataMapper) {
        for (int integer3 = 0; integer3 < this.scheduledPositions.length; ++integer3) {
            if (this.scheduledPositions[integer3] != null) {
                for (final Short short5 : this.scheduledPositions[integer3]) {
                    final BlockPos blockPos6 = ProtoChunk.joinBlockPos(short5, integer3, this.pos);
                    scheduler.schedule(blockPos6, dataMapper.apply(blockPos6), 0);
                }
                this.scheduledPositions[integer3].clear();
            }
        }
    }
    
    @Override
    public boolean isScheduled(final BlockPos pos, final T object) {
        return false;
    }
    
    @Override
    public void schedule(final BlockPos pos, final T object, final int delay, final TaskPriority priority) {
        Chunk.getList(this.scheduledPositions, pos.getY() >> 4).add(ProtoChunk.getPackedSectionRelative(pos));
    }
    
    @Override
    public boolean isTicking(final BlockPos pos, final T object) {
        return false;
    }
    
    @Override
    public void a(final Stream<ScheduledTick<T>> stream) {
        stream.forEach(scheduledTick -> this.schedule(scheduledTick.pos, scheduledTick.getObject(), 0, scheduledTick.priority));
    }
}
