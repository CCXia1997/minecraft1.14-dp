package net.minecraft.client.world;

import net.minecraft.world.ScheduledTick;
import java.util.stream.Stream;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;

public class DummyClientTickScheduler<T> implements TickScheduler<T>
{
    private static final DummyClientTickScheduler<Object> INSTANCE;
    
    public static <T> DummyClientTickScheduler<T> get() {
        return (DummyClientTickScheduler<T>)DummyClientTickScheduler.INSTANCE;
    }
    
    @Override
    public boolean isScheduled(final BlockPos pos, final T object) {
        return false;
    }
    
    @Override
    public void schedule(final BlockPos pos, final T object, final int delay) {
    }
    
    @Override
    public void schedule(final BlockPos pos, final T object, final int delay, final TaskPriority priority) {
    }
    
    @Override
    public boolean isTicking(final BlockPos pos, final T object) {
        return false;
    }
    
    @Override
    public void a(final Stream<ScheduledTick<T>> stream) {
    }
    
    static {
        INSTANCE = new DummyClientTickScheduler<>();
    }
}
