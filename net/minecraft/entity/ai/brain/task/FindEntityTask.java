package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.Entity;
import java.util.List;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

public class FindEntityTask<E extends LivingEntity, T extends LivingEntity> extends Task<E>
{
    private final int completionRange;
    private final float speed;
    private final EntityType<? extends T> entityType;
    private final int maxSquaredDistance;
    private final Predicate<T> predicate;
    private final Predicate<E> shouldRunPredicate;
    private final MemoryModuleType<T> targetModule;
    
    public FindEntityTask(final EntityType<? extends T> entityType, final int maxDistance, final Predicate<E> shouldRunPredicate, final Predicate<T> predicate, final MemoryModuleType<T> targetModule, final float speed, final int completionRange) {
        this.entityType = entityType;
        this.speed = speed;
        this.maxSquaredDistance = maxDistance * maxDistance;
        this.completionRange = completionRange;
        this.predicate = predicate;
        this.shouldRunPredicate = shouldRunPredicate;
        this.targetModule = targetModule;
    }
    
    public static <T extends LivingEntity> FindEntityTask<LivingEntity, T> create(final EntityType<? extends T> entityType, final int maxDistance, final MemoryModuleType<T> targetModule, final float speed, final int completionRange) {
        return new FindEntityTask<LivingEntity, T>(entityType, maxDistance, livingEntity -> true, livingEntity -> true, targetModule, speed, completionRange);
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.l, MemoryModuleState.c), Pair.of(MemoryModuleType.k, MemoryModuleState.b), Pair.of(this.targetModule, MemoryModuleState.b), Pair.of(MemoryModuleType.g, MemoryModuleState.a));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final E entity) {
        return this.shouldRunPredicate.test(entity) && entity.getBrain().<List<LivingEntity>>getOptionalMemory(MemoryModuleType.g).get().stream().anyMatch(livingEntity -> this.entityType.equals(livingEntity.getType()) && this.predicate.test((T)livingEntity));
    }
    
    @Override
    protected void run(final ServerWorld world, final E entity, final long time) {
        final Brain<?> brain5 = entity.getBrain();
        final Brain brain6;
        final MemoryModuleType k;
        final Object value;
        brain5.<List<LivingEntity>>getOptionalMemory(MemoryModuleType.g).ifPresent(list -> list.stream().filter(livingEntity -> this.entityType.equals(livingEntity.getType())).map(livingEntity -> livingEntity).filter(livingEntity2 -> livingEntity2.squaredDistanceTo(entity) <= this.maxSquaredDistance).filter(this.predicate).findFirst().ifPresent(livingEntity -> {
            brain6.putMemory(this.targetModule, livingEntity);
            brain6.putMemory(MemoryModuleType.l, new EntityPosWrapper(livingEntity));
            k = MemoryModuleType.k;
            new WalkTarget(new EntityPosWrapper(livingEntity), this.speed, this.completionRange);
            brain6.putMemory(k, value);
        }));
    }
}
