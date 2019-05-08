package net.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import java.util.function.Function;

public class MultiTickScheduler<T> implements TickScheduler<T>
{
    private final Function<BlockPos, TickScheduler<T>> mapper;
    
    public MultiTickScheduler(final Function<BlockPos, TickScheduler<T>> mapper) {
        this.mapper = mapper;
    }
    
    @Override
    public boolean isScheduled(final BlockPos pos, final T object) {
        return this.mapper.apply(pos).isScheduled(pos, object);
    }
    
    @Override
    public void schedule(final BlockPos pos, final T object, final int delay, final TaskPriority priority) {
        this.mapper.apply(pos).schedule(pos, object, delay, priority);
    }
    
    @Override
    public boolean isTicking(final BlockPos pos, final T object) {
        return false;
    }
    
    @Override
    public void a(final Stream<ScheduledTick<T>> stream) {
        stream.forEach(scheduledTick -> this.mapper.apply(scheduledTick.pos).a(Stream.<ScheduledTick<T>>of(scheduledTick)));
    }
}
