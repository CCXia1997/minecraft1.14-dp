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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityCategory;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;

public class FollowMobTask extends Task<LivingEntity>
{
    private final Predicate<LivingEntity> mobType;
    private final float maxDistanceSquared;
    
    public FollowMobTask(final EntityCategory entityCategory, final float float2) {
        this(livingEntity -> entityCategory.equals(livingEntity.getType().getCategory()), float2);
    }
    
    public FollowMobTask(final EntityType<?> mobType, final float maxDistanceSquared) {
        this(livingEntity -> mobType.equals(livingEntity.getType()), maxDistanceSquared);
    }
    
    public FollowMobTask(final Predicate<LivingEntity> predicate, final float float2) {
        this.mobType = predicate;
        this.maxDistanceSquared = float2 * float2;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.l, MemoryModuleState.b), Pair.of(MemoryModuleType.g, MemoryModuleState.a));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        return entity.getBrain().<List<LivingEntity>>getOptionalMemory(MemoryModuleType.g).get().stream().anyMatch(this.mobType);
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        final Brain<?> brain5 = entity.getBrain();
        brain5.<List<LivingEntity>>getOptionalMemory(MemoryModuleType.g).ifPresent(list -> list.stream().filter(this.mobType).filter(livingEntity2 -> livingEntity2.squaredDistanceTo(entity) <= this.maxDistanceSquared).findFirst().ifPresent(livingEntity -> brain5.<EntityPosWrapper>putMemory((MemoryModuleType<EntityPosWrapper>)MemoryModuleType.l, new EntityPosWrapper(livingEntity))));
    }
}
