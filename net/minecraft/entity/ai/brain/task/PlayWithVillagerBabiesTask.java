package net.minecraft.entity.ai.brain.task;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import com.google.common.collect.Maps;
import java.util.function.Function;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.PathfindingUtil;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.mob.MobEntityWithAi;

public class PlayWithVillagerBabiesTask extends Task<MobEntityWithAi>
{
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.h, MemoryModuleState.a), Pair.of(MemoryModuleType.k, MemoryModuleState.b), Pair.of(MemoryModuleType.l, MemoryModuleState.c), Pair.of(MemoryModuleType.m, MemoryModuleState.c));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final MobEntityWithAi entity) {
        return world.getRandom().nextInt(10) == 0 && this.hasVisibleVillagerBabies(entity);
    }
    
    @Override
    protected void run(final ServerWorld world, final MobEntityWithAi entity, final long time) {
        final LivingEntity livingEntity2 = this.findVisibleVillagerBaby(entity);
        if (livingEntity2 != null) {
            this.a(world, entity, livingEntity2);
            return;
        }
        final Optional<LivingEntity> optional6 = this.b(entity);
        if (optional6.isPresent()) {
            a(entity, optional6.get());
            return;
        }
        this.getVisibleMob(entity).ifPresent(livingEntity -> a(entity, livingEntity));
    }
    
    private void a(final ServerWorld serverWorld, final MobEntityWithAi mobEntityWithAi, final LivingEntity livingEntity) {
        for (int integer4 = 0; integer4 < 10; ++integer4) {
            final Vec3d vec3d5 = PathfindingUtil.findTargetStraight(mobEntityWithAi, 20, 8);
            if (vec3d5 != null && serverWorld.isNearOccupiedPointOfInterest(new BlockPos(vec3d5))) {
                mobEntityWithAi.getBrain().<WalkTarget>putMemory(MemoryModuleType.k, new WalkTarget(vec3d5, 0.6f, 0));
                return;
            }
        }
    }
    
    private static void a(final MobEntityWithAi mobEntityWithAi, final LivingEntity livingEntity) {
        final Brain<?> brain3 = mobEntityWithAi.getBrain();
        brain3.<LivingEntity>putMemory(MemoryModuleType.m, livingEntity);
        brain3.<EntityPosWrapper>putMemory((MemoryModuleType<EntityPosWrapper>)MemoryModuleType.l, new EntityPosWrapper(livingEntity));
        brain3.<WalkTarget>putMemory(MemoryModuleType.k, new WalkTarget(new EntityPosWrapper(livingEntity), 0.6f, 1));
    }
    
    private Optional<LivingEntity> getVisibleMob(final MobEntityWithAi entity) {
        return this.getVisibleVillagerBabies(entity).stream().findAny();
    }
    
    private Optional<LivingEntity> b(final MobEntityWithAi mobEntityWithAi) {
        final Map<LivingEntity, Integer> map2 = this.c(mobEntityWithAi);
        return map2.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).filter(entry -> entry.getValue() > 0 && entry.getValue() <= 5).<LivingEntity>map(Map.Entry::getKey).findFirst();
    }
    
    private Map<LivingEntity, Integer> c(final MobEntityWithAi mobEntityWithAi) {
        final Map<LivingEntity, Integer> map2 = Maps.newHashMap();
        final Integer n;
        this.getVisibleVillagerBabies(mobEntityWithAi).stream().filter(this::hasInteractionTarget).forEach(livingEntity -> n = map2.compute(this.getInteractionTarget(livingEntity), (livingEntity, integer) -> (integer == null) ? 1 : (integer + 1)));
        return map2;
    }
    
    private List<LivingEntity> getVisibleVillagerBabies(final MobEntityWithAi mobEntityWithAi) {
        return mobEntityWithAi.getBrain().<List<LivingEntity>>getOptionalMemory(MemoryModuleType.h).get();
    }
    
    private LivingEntity getInteractionTarget(final LivingEntity livingEntity) {
        return livingEntity.getBrain().<LivingEntity>getOptionalMemory(MemoryModuleType.m).get();
    }
    
    @Nullable
    private LivingEntity findVisibleVillagerBaby(final LivingEntity entity) {
        return entity.getBrain().<List<LivingEntity>>getOptionalMemory(MemoryModuleType.h).get().stream().filter(livingEntity2 -> this.isInteractionTargetOf(entity, livingEntity2)).findAny().orElse(null);
    }
    
    private boolean hasInteractionTarget(final LivingEntity entity) {
        return entity.getBrain().<LivingEntity>getOptionalMemory(MemoryModuleType.m).isPresent();
    }
    
    private boolean isInteractionTargetOf(final LivingEntity entity, final LivingEntity other) {
        return other.getBrain().<LivingEntity>getOptionalMemory(MemoryModuleType.m).filter(livingEntity2 -> livingEntity2 == entity).isPresent();
    }
    
    private boolean hasVisibleVillagerBabies(final MobEntityWithAi entity) {
        return entity.getBrain().hasMemoryModule(MemoryModuleType.h);
    }
}
