package net.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;

public interface TickScheduler<T>
{
    boolean isScheduled(final BlockPos arg1, final T arg2);
    
    default void schedule(final BlockPos pos, final T object, final int delay) {
        this.schedule(pos, object, delay, TaskPriority.d);
    }
    
    void schedule(final BlockPos arg1, final T arg2, final int arg3, final TaskPriority arg4);
    
    boolean isTicking(final BlockPos arg1, final T arg2);
    
    void a(final Stream<ScheduledTick<T>> arg1);
}
