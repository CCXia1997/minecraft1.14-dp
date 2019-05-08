package net.minecraft.world;

import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;

public class ScheduledTick<T> implements Comparable<ScheduledTick<?>>
{
    private static long idCounter;
    private final T object;
    public final BlockPos pos;
    public final long time;
    public final TaskPriority priority;
    private final long id;
    
    public ScheduledTick(final BlockPos pos, final T t) {
        this(pos, t, 0L, TaskPriority.d);
    }
    
    public ScheduledTick(final BlockPos pos, final T t, final long time, final TaskPriority priority) {
        this.id = ScheduledTick.idCounter++;
        this.pos = pos.toImmutable();
        this.object = t;
        this.time = time;
        this.priority = priority;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof ScheduledTick) {
            final ScheduledTick<?> scheduledTick2 = o;
            return this.pos.equals(scheduledTick2.pos) && this.object == scheduledTick2.object;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.pos.hashCode();
    }
    
    public int a(final ScheduledTick<?> scheduledTick) {
        int integer2 = Long.compare(this.time, scheduledTick.time);
        if (integer2 != 0) {
            return integer2;
        }
        integer2 = Integer.compare(this.priority.ordinal(), scheduledTick.priority.ordinal());
        if (integer2 != 0) {
            return integer2;
        }
        return Long.compare(this.id, scheduledTick.id);
    }
    
    @Override
    public String toString() {
        return this.object + ": " + this.pos + ", " + this.time + ", " + this.priority + ", " + this.id;
    }
    
    public T getObject() {
        return this.object;
    }
}
