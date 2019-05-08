package net.minecraft.server.world;

import net.minecraft.nbt.ListTag;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import java.util.Collection;
import com.google.common.collect.Sets;
import java.util.List;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import net.minecraft.world.ScheduledTick;
import java.util.Set;
import net.minecraft.world.TickScheduler;

public class SimpleTickScheduler<T> implements TickScheduler<T>
{
    protected final Set<ScheduledTick<T>> scheduledTicks;
    private final Function<T, Identifier> identifierProvider;
    
    public SimpleTickScheduler(final Function<T, Identifier> function, final List<ScheduledTick<T>> list) {
        this.scheduledTicks = Sets.newHashSet();
        this.identifierProvider = function;
        this.scheduledTicks.addAll(list);
    }
    
    @Override
    public boolean isScheduled(final BlockPos pos, final T object) {
        return false;
    }
    
    @Override
    public void schedule(final BlockPos pos, final T object, final int delay, final TaskPriority priority) {
        this.scheduledTicks.add(new ScheduledTick<T>(pos, object, delay, priority));
    }
    
    @Override
    public boolean isTicking(final BlockPos pos, final T object) {
        return false;
    }
    
    @Override
    public void a(final Stream<ScheduledTick<T>> stream) {
        stream.forEach(this.scheduledTicks::add);
    }
    
    public Stream<ScheduledTick<T>> stream() {
        return this.scheduledTicks.stream();
    }
    
    public ListTag toTag(final long long1) {
        return ServerTickScheduler.<T>serializeScheduledTicks(this.identifierProvider, this.scheduledTicks, long1);
    }
}
