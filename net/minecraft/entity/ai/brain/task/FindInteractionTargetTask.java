package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.Entity;
import java.util.List;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

public class FindInteractionTargetTask extends Task<LivingEntity>
{
    private final EntityType<?> entityType;
    private final int maxSquaredDistance;
    private final Predicate<LivingEntity> predicate;
    private final Predicate<LivingEntity> shouldRunPredicate;
    
    public FindInteractionTargetTask(final EntityType<?> entityType, final int maxDistance, final Predicate<LivingEntity> shouldRunPredicate, final Predicate<LivingEntity> predicate) {
        this.entityType = entityType;
        this.maxSquaredDistance = maxDistance * maxDistance;
        this.predicate = predicate;
        this.shouldRunPredicate = shouldRunPredicate;
    }
    
    public FindInteractionTargetTask(final EntityType<?> entityType, final int integer) {
        this(entityType, integer, livingEntity -> true, livingEntity -> true);
    }
    
    public Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.l, MemoryModuleState.c), Pair.of(MemoryModuleType.m, MemoryModuleState.b), Pair.of(MemoryModuleType.g, MemoryModuleState.a));
    }
    
    public boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        return this.shouldRunPredicate.test(entity) && this.getVisibleMobs(entity).stream().anyMatch(this::test);
    }
    
    public void run(final ServerWorld world, final LivingEntity entity, final long time) {
        super.run(world, entity, time);
        final Brain<?> brain5 = entity.getBrain();
        final Brain brain6;
        brain5.<List<LivingEntity>>getOptionalMemory(MemoryModuleType.g).ifPresent(list -> list.stream().filter(livingEntity2 -> livingEntity2.squaredDistanceTo(entity) <= this.maxSquaredDistance).filter(this::test).findFirst().ifPresent(livingEntity -> {
            brain6.putMemory(MemoryModuleType.m, livingEntity);
            brain6.putMemory(MemoryModuleType.l, new EntityPosWrapper(livingEntity));
        }));
    }
    
    private boolean test(final LivingEntity entity) {
        return this.entityType.equals(entity.getType()) && this.predicate.test(entity);
    }
    
    private List<LivingEntity> getVisibleMobs(final LivingEntity entity) {
        return entity.getBrain().<List<LivingEntity>>getOptionalMemory(MemoryModuleType.g).get();
    }
}
