package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.LivingEntity;

public abstract class Task<E extends LivingEntity>
{
    private Status status;
    private long endTime;
    private final int minRunTime;
    private final int maxRunTime;
    
    public Task() {
        this(60);
    }
    
    public Task(final int exactRunTime) {
        this(exactRunTime, exactRunTime);
    }
    
    public Task(final int minRunTime, final int maxRunTime) {
        this.status = Status.a;
        this.minRunTime = minRunTime;
        this.maxRunTime = maxRunTime;
    }
    
    public Status getStatus() {
        return this.status;
    }
    
    public final boolean tryStarting(final ServerWorld world, final E entity, final long time) {
        if (this.hasRequiredMemoryState(entity) && this.shouldRun(world, entity)) {
            this.status = Status.b;
            final int integer5 = this.minRunTime + world.getRandom().nextInt(this.maxRunTime + 1 - this.minRunTime);
            this.endTime = time + integer5;
            this.run(world, entity, time);
            return true;
        }
        return false;
    }
    
    protected void run(final ServerWorld world, final E entity, final long time) {
    }
    
    public final void tick(final ServerWorld serverWorld, final E livingEntity, final long time) {
        if (!this.isTimeLimitExceeded(time) && this.shouldKeepRunning(serverWorld, livingEntity, time)) {
            this.keepRunning(serverWorld, livingEntity, time);
        }
        else {
            this.stop(serverWorld, livingEntity, time);
        }
    }
    
    protected void keepRunning(final ServerWorld world, final E entity, final long time) {
    }
    
    public final void stop(final ServerWorld world, final E entity, final long time) {
        this.status = Status.a;
        this.finishRunning(world, entity, time);
    }
    
    protected void finishRunning(final ServerWorld serverWorld, final E livingEntity, final long time) {
    }
    
    protected boolean shouldKeepRunning(final ServerWorld world, final E entity, final long time) {
        return false;
    }
    
    protected boolean isTimeLimitExceeded(final long time) {
        return time > this.endTime;
    }
    
    protected boolean shouldRun(final ServerWorld world, final E entity) {
        return true;
    }
    
    protected abstract Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState();
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
    
    private boolean hasRequiredMemoryState(final E entity) {
        final MemoryModuleType<?> memoryModuleType3;
        final MemoryModuleState memoryModuleState4;
        return this.getRequiredMemoryState().stream().allMatch(pair -> {
            memoryModuleType3 = pair.getFirst();
            memoryModuleState4 = (MemoryModuleState)pair.getSecond();
            return entity.getBrain().isMemoryInState(memoryModuleType3, memoryModuleState4);
        });
    }
    
    public enum Status
    {
        a, 
        b;
    }
}
