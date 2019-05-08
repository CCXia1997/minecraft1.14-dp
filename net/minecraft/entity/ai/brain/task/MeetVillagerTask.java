package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.Entity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.EntityType;
import java.util.List;
import net.minecraft.util.math.Position;
import java.util.Objects;
import net.minecraft.util.GlobalPos;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.LivingEntity;

public class MeetVillagerTask extends Task<LivingEntity>
{
    @Override
    protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        final Brain<?> brain3 = entity.getBrain();
        final Optional<GlobalPos> optional4 = brain3.<GlobalPos>getOptionalMemory(MemoryModuleType.d);
        return world.getRandom().nextInt(100) == 0 && optional4.isPresent() && Objects.equals(world.getDimension().getType(), optional4.get().getDimension()) && optional4.get().getPos().isWithinDistance(entity.getPos(), 4.0) && brain3.<List<LivingEntity>>getOptionalMemory(MemoryModuleType.g).get().stream().anyMatch(livingEntity -> EntityType.VILLAGER.equals(livingEntity.getType()));
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.c), Pair.of(MemoryModuleType.l, MemoryModuleState.c), Pair.of(MemoryModuleType.d, MemoryModuleState.a), Pair.of(MemoryModuleType.g, MemoryModuleState.a), Pair.of(MemoryModuleType.m, MemoryModuleState.b));
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        final Brain<?> brain5 = entity.getBrain();
        final Brain brain6;
        final MemoryModuleType k;
        final Object value;
        brain5.<List<LivingEntity>>getOptionalMemory(MemoryModuleType.g).ifPresent(list -> list.stream().filter(livingEntity -> EntityType.VILLAGER.equals(livingEntity.getType())).filter(livingEntity2 -> livingEntity2.squaredDistanceTo(entity) <= 32.0).findFirst().ifPresent(livingEntity -> {
            brain6.putMemory(MemoryModuleType.m, livingEntity);
            brain6.putMemory(MemoryModuleType.l, new EntityPosWrapper(livingEntity));
            k = MemoryModuleType.k;
            new WalkTarget(new EntityPosWrapper(livingEntity), 0.3f, 1);
            brain6.putMemory(k, value);
        }));
    }
}
