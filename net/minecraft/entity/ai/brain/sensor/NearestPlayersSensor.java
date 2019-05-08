package net.minecraft.entity.ai.brain.sensor;

import net.minecraft.server.network.ServerPlayerEntity;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.ToDoubleFunction;
import java.util.Comparator;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.LivingEntity;

public class NearestPlayersSensor extends Sensor<LivingEntity>
{
    @Override
    protected void sense(final ServerWorld world, final LivingEntity entity) {
        final List<PlayerEntity> list3 = world.getPlayers().stream().filter(EntityPredicates.EXCEPT_SPECTATOR).filter(serverPlayerEntity -> entity.squaredDistanceTo(serverPlayerEntity) < 256.0).sorted(Comparator.comparingDouble(entity::squaredDistanceTo)).collect(Collectors.toList());
        final Brain<?> brain4 = entity.getBrain();
        brain4.<List<PlayerEntity>>putMemory(MemoryModuleType.i, list3);
        brain4.<PlayerEntity>setMemory(MemoryModuleType.j, list3.stream().filter(entity::canSee).findFirst());
    }
    
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.i, (MemoryModuleType<List<PlayerEntity>>)MemoryModuleType.j);
    }
}
