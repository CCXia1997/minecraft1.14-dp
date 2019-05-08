package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.passive.VillagerEntity;

public class FollowCustomerTask extends Task<VillagerEntity>
{
    private final float speed;
    
    public FollowCustomerTask(final float speed) {
        super(Integer.MAX_VALUE);
        this.speed = speed;
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final VillagerEntity entity) {
        final PlayerEntity playerEntity3 = entity.getCurrentCustomer();
        return entity.isAlive() && playerEntity3 != null && !entity.isInsideWater() && !entity.velocityModified && entity.squaredDistanceTo(playerEntity3) <= 16.0 && playerEntity3.container != null;
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        return this.shouldRun(world, entity);
    }
    
    @Override
    protected void run(final ServerWorld world, final VillagerEntity entity, final long time) {
        this.update(entity);
    }
    
    @Override
    protected void finishRunning(final ServerWorld serverWorld, final VillagerEntity villagerEntity, final long time) {
        villagerEntity.resetCustomer();
        final Brain<?> brain5 = villagerEntity.getBrain();
        brain5.forget(MemoryModuleType.k);
        brain5.forget(MemoryModuleType.l);
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        this.update(entity);
    }
    
    @Override
    protected boolean isTimeLimitExceeded(final long time) {
        return false;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.c), Pair.of(MemoryModuleType.l, MemoryModuleState.c));
    }
    
    private void update(final VillagerEntity villager) {
        final EntityPosWrapper entityPosWrapper2 = new EntityPosWrapper(villager.getCurrentCustomer());
        final Brain<?> brain3 = villager.getBrain();
        brain3.<WalkTarget>putMemory(MemoryModuleType.k, new WalkTarget(entityPosWrapper2, this.speed, 2));
        brain3.<LookTarget>putMemory(MemoryModuleType.l, entityPosWrapper2);
    }
}
