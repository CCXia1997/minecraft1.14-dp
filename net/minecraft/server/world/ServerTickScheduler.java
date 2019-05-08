package net.minecraft.server.world;

import java.util.AbstractList;
import net.minecraft.util.TaskPriority;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.Vec3i;
import java.util.Collections;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.chunk.ChunkPos;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import java.util.Iterator;
import net.minecraft.util.crash.CrashException;
import net.minecraft.block.BlockState;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashReport;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.function.Consumer;
import java.util.List;
import java.util.TreeSet;
import net.minecraft.world.ScheduledTick;
import java.util.Set;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.TickScheduler;

public class ServerTickScheduler<T> implements TickScheduler<T>
{
    protected final Predicate<T> invalidObjPredicate;
    protected final Function<T, Identifier> idToName;
    protected final Function<Identifier, T> nameToId;
    protected final Set<ScheduledTick<T>> ticksScheduled;
    protected final TreeSet<ScheduledTick<T>> ticksScheduledOrdered;
    private final ServerWorld world;
    private final List<ScheduledTick<T>> ticksCurrent;
    private final Consumer<ScheduledTick<T>> tickConsumer;
    
    public ServerTickScheduler(final ServerWorld world, final Predicate<T> invalidObjPredicate, final Function<T, Identifier> idToName, final Function<Identifier, T> nameToId, final Consumer<ScheduledTick<T>> tickConsumer) {
        this.ticksScheduled = Sets.newHashSet();
        this.ticksScheduledOrdered = new TreeSet<ScheduledTick<T>>();
        this.ticksCurrent = Lists.newArrayList();
        this.invalidObjPredicate = invalidObjPredicate;
        this.idToName = idToName;
        this.nameToId = nameToId;
        this.world = world;
        this.tickConsumer = tickConsumer;
    }
    
    public void tick() {
        int integer1 = this.ticksScheduledOrdered.size();
        if (integer1 != this.ticksScheduled.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        if (integer1 > 65536) {
            integer1 = 65536;
        }
        this.world.getProfiler().push("cleaning");
        for (int integer2 = 0; integer2 < integer1; ++integer2) {
            final ScheduledTick<T> scheduledTick3 = this.ticksScheduledOrdered.first();
            if (scheduledTick3.time > this.world.getTime()) {
                break;
            }
            this.ticksScheduledOrdered.remove(scheduledTick3);
            this.ticksScheduled.remove(scheduledTick3);
            this.ticksCurrent.add(scheduledTick3);
        }
        this.world.getProfiler().pop();
        this.world.getProfiler().push("ticking");
        final Iterator<ScheduledTick<T>> iterator2 = this.ticksCurrent.iterator();
        while (iterator2.hasNext()) {
            final ScheduledTick<T> scheduledTick3 = iterator2.next();
            iterator2.remove();
            if (this.world.isBlockLoaded(scheduledTick3.pos)) {
                try {
                    this.tickConsumer.accept(scheduledTick3);
                    continue;
                }
                catch (Throwable throwable4) {
                    final CrashReport crashReport5 = CrashReport.create(throwable4, "Exception while ticking");
                    final CrashReportSection crashReportSection6 = crashReport5.addElement("Block being ticked");
                    CrashReportSection.addBlockInfo(crashReportSection6, scheduledTick3.pos, null);
                    throw new CrashException(crashReport5);
                }
            }
            this.schedule(scheduledTick3.pos, scheduledTick3.getObject(), 0);
        }
        this.world.getProfiler().pop();
        this.ticksCurrent.clear();
    }
    
    @Override
    public boolean isTicking(final BlockPos pos, final T object) {
        return this.ticksCurrent.contains(new ScheduledTick(pos, object));
    }
    
    @Override
    public void a(final Stream<ScheduledTick<T>> stream) {
        stream.forEach(this::a);
    }
    
    public List<ScheduledTick<T>> getScheduledTicksInChunk(final boolean remove, final ChunkPos pos) {
        final int integer3 = (pos.x << 4) - 2;
        final int integer4 = integer3 + 16 + 2;
        final int integer5 = (pos.z << 4) - 2;
        final int integer6 = integer5 + 16 + 2;
        return this.getScheduledTicks(new MutableIntBoundingBox(integer3, 0, integer5, integer4, 256, integer6), remove);
    }
    
    public List<ScheduledTick<T>> getScheduledTicks(final MutableIntBoundingBox box, final boolean remove) {
        List<ScheduledTick<T>> list3 = null;
        for (int integer4 = 0; integer4 < 2; ++integer4) {
            Iterator<ScheduledTick<T>> iterator5;
            if (integer4 == 0) {
                iterator5 = this.ticksScheduledOrdered.iterator();
            }
            else {
                iterator5 = this.ticksCurrent.iterator();
            }
            while (iterator5.hasNext()) {
                final ScheduledTick<T> scheduledTick6 = iterator5.next();
                final BlockPos blockPos7 = scheduledTick6.pos;
                if (blockPos7.getX() >= box.minX && blockPos7.getX() < box.maxX && blockPos7.getZ() >= box.minZ && blockPos7.getZ() < box.maxZ) {
                    if (remove) {
                        if (integer4 == 0) {
                            this.ticksScheduled.remove(scheduledTick6);
                        }
                        iterator5.remove();
                    }
                    if (list3 == null) {
                        list3 = Lists.newArrayList();
                    }
                    list3.add(scheduledTick6);
                }
            }
        }
        return (list3 == null) ? Collections.<ScheduledTick<T>>emptyList() : list3;
    }
    
    public void copyScheduledTicks(final MutableIntBoundingBox box, final BlockPos offset) {
        final List<ScheduledTick<T>> list3 = this.getScheduledTicks(box, false);
        for (final ScheduledTick<T> scheduledTick5 : list3) {
            if (box.contains(scheduledTick5.pos)) {
                final BlockPos blockPos6 = scheduledTick5.pos.add(offset);
                this.scheduleTick(blockPos6, scheduledTick5.getObject(), (int)(scheduledTick5.time - this.world.getLevelProperties().getTime()), scheduledTick5.priority);
            }
        }
    }
    
    public ListTag toTag(final ChunkPos chunkPos) {
        final List<ScheduledTick<T>> list2 = this.getScheduledTicksInChunk(false, chunkPos);
        return ServerTickScheduler.<T>serializeScheduledTicks(this.idToName, list2, this.world.getTime());
    }
    
    public static <T> ListTag serializeScheduledTicks(final Function<T, Identifier> identifierProvider, final Iterable<ScheduledTick<T>> scheduledTicks, final long time) {
        final ListTag listTag5 = new ListTag();
        for (final ScheduledTick<T> scheduledTick7 : scheduledTicks) {
            final CompoundTag compoundTag8 = new CompoundTag();
            compoundTag8.putString("i", identifierProvider.apply(scheduledTick7.getObject()).toString());
            compoundTag8.putInt("x", scheduledTick7.pos.getX());
            compoundTag8.putInt("y", scheduledTick7.pos.getY());
            compoundTag8.putInt("z", scheduledTick7.pos.getZ());
            compoundTag8.putInt("t", (int)(scheduledTick7.time - time));
            compoundTag8.putInt("p", scheduledTick7.priority.getPriorityIndex());
            ((AbstractList<CompoundTag>)listTag5).add(compoundTag8);
        }
        return listTag5;
    }
    
    public void fromTag(final ListTag tags) {
        for (int integer2 = 0; integer2 < tags.size(); ++integer2) {
            final CompoundTag compoundTag3 = tags.getCompoundTag(integer2);
            final T object4 = this.nameToId.apply(new Identifier(compoundTag3.getString("i")));
            if (object4 != null) {
                this.scheduleTick(new BlockPos(compoundTag3.getInt("x"), compoundTag3.getInt("y"), compoundTag3.getInt("z")), object4, compoundTag3.getInt("t"), TaskPriority.getByIndex(compoundTag3.getInt("p")));
            }
        }
    }
    
    @Override
    public boolean isScheduled(final BlockPos pos, final T object) {
        return this.ticksScheduled.contains(new ScheduledTick(pos, object));
    }
    
    @Override
    public void schedule(final BlockPos pos, final T object, final int delay, final TaskPriority priority) {
        if (this.invalidObjPredicate.test(object)) {
            return;
        }
        this.scheduleTickUnchecked(pos, object, delay, priority);
    }
    
    protected void scheduleTick(final BlockPos pos, final T object, final int delay, final TaskPriority priority) {
        if (!this.invalidObjPredicate.test(object)) {
            this.scheduleTickUnchecked(pos, object, delay, priority);
        }
    }
    
    private void scheduleTickUnchecked(final BlockPos blockPos, final T object, final int integer, final TaskPriority taskPriority) {
        final ScheduledTick<T> scheduledTick5 = new ScheduledTick<T>(blockPos, object, integer + this.world.getTime(), taskPriority);
        this.a(scheduledTick5);
    }
    
    private void a(final ScheduledTick<T> scheduledTick) {
        if (!this.ticksScheduled.contains(scheduledTick)) {
            this.ticksScheduled.add(scheduledTick);
            this.ticksScheduledOrdered.add(scheduledTick);
        }
    }
}
