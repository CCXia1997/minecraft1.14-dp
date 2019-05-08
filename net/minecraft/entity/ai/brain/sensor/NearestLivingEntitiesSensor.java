package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.List;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import java.util.function.ToDoubleFunction;
import java.util.Comparator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.LivingEntity;

public class NearestLivingEntitiesSensor extends Sensor<LivingEntity>
{
    private static final TargetPredicate CLOSE_ENTITY_PREDICATE;
    
    @Override
    protected void sense(final ServerWorld world, final LivingEntity entity) {
        final List<LivingEntity> list3 = world.<LivingEntity>getEntities(LivingEntity.class, entity.getBoundingBox().expand(16.0, 16.0, 16.0), livingEntity2 -> livingEntity2 != entity && livingEntity2.isAlive());
        list3.sort(Comparator.comparingDouble(entity::squaredDistanceTo));
        final Brain<?> brain4 = entity.getBrain();
        brain4.<List<LivingEntity>>putMemory(MemoryModuleType.f, list3);
        brain4.<List<LivingEntity>>putMemory(MemoryModuleType.g, list3.stream().filter(livingEntity2 -> NearestLivingEntitiesSensor.CLOSE_ENTITY_PREDICATE.test(entity, livingEntity2)).filter(entity::canSee).collect(Collectors.toList()));
    }
    
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.f, MemoryModuleType.g);
    }
    
    static {
        CLOSE_ENTITY_PREDICATE = new TargetPredicate().setBaseMaxDistance(16.0).includeTeammates().ignoreEntityTargetRules().includeHidden();
    }
}
