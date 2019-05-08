package net.minecraft.entity.ai.brain.task;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import net.minecraft.server.world.ServerWorld;
import java.util.List;
import net.minecraft.util.WeightedList;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;

public class CompositeTask<E extends LivingEntity> extends Task<E>
{
    private final Set<Pair<MemoryModuleType<?>, MemoryModuleState>> requiredMemoryState;
    private final Set<MemoryModuleType<?>> memoriesToForgetWhenStopped;
    private final Order order;
    private final RunMode runMode;
    private final WeightedList<Task<? super E>> tasks;
    
    public CompositeTask(final Set<Pair<MemoryModuleType<?>, MemoryModuleState>> requiredMemoryState, final Set<MemoryModuleType<?>> memoriesToForgetWhenStopped, final Order order, final RunMode runMode, final List<Pair<Task<? super E>, Integer>> tasks) {
        this.tasks = new WeightedList<Task<? super E>>();
        this.requiredMemoryState = requiredMemoryState;
        this.memoriesToForgetWhenStopped = memoriesToForgetWhenStopped;
        this.order = order;
        this.runMode = runMode;
        tasks.forEach(pair -> this.tasks.add(pair.getFirst(), (int)pair.getSecond()));
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return this.requiredMemoryState;
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final E entity, final long time) {
        return this.tasks.stream().filter(task -> task.getStatus() == Status.b).anyMatch(task5 -> task5.shouldKeepRunning(world, entity, time));
    }
    
    @Override
    protected boolean isTimeLimitExceeded(final long time) {
        return false;
    }
    
    @Override
    protected void run(final ServerWorld world, final E entity, final long time) {
        this.order.apply(this.tasks);
        this.runMode.<E>a(this.tasks, world, entity, time);
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final E entity, final long time) {
        this.tasks.stream().filter(task -> task.getStatus() == Status.b).forEach(task5 -> task5.tick(world, entity, time));
    }
    
    @Override
    protected void finishRunning(final ServerWorld serverWorld, final E livingEntity, final long time) {
        this.tasks.stream().filter(task -> task.getStatus() == Status.b).forEach(task5 -> task5.stop(serverWorld, livingEntity, time));
        this.memoriesToForgetWhenStopped.forEach(livingEntity.getBrain()::forget);
    }
    
    @Override
    public String toString() {
        final Set<? extends Task<? super E>> set1 = this.tasks.stream().filter(task -> task.getStatus() == Status.b).collect(Collectors.<Task<? super E>>toSet());
        return "(" + this.getClass().getSimpleName() + "): " + set1;
    }
    
    enum Order
    {
        a(weightedList -> {}), 
        b(WeightedList::shuffle);
        
        private final Consumer<WeightedList<?>> consumer;
        
        private Order(final Consumer<WeightedList<?>> consumer) {
            this.consumer = consumer;
        }
        
        public void apply(final WeightedList<?> list) {
            this.consumer.accept(list);
        }
    }
    
    enum RunMode
    {
        a {
            @Override
            public <E extends LivingEntity> void a(final WeightedList<Task<? super E>> weightedList, final ServerWorld serverWorld, final E livingEntity, final long long4) {
                weightedList.stream().filter(task -> task.getStatus() == Status.a).filter(task5 -> task5.tryStarting(serverWorld, livingEntity, long4)).findFirst();
            }
        }, 
        b {
            @Override
            public <E extends LivingEntity> void a(final WeightedList<Task<? super E>> weightedList, final ServerWorld serverWorld, final E livingEntity, final long long4) {
                weightedList.stream().filter(task -> task.getStatus() == Status.a).forEach(task5 -> task5.tryStarting(serverWorld, livingEntity, long4));
            }
        };
        
        public abstract <E extends LivingEntity> void a(final WeightedList<Task<? super E>> arg1, final ServerWorld arg2, final E arg3, final long arg4);
    }
}
