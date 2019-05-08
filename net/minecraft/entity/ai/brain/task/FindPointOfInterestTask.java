package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.util.GlobalPos;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.village.PointOfInterestType;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;

public class FindPointOfInterestTask extends Task<LivingEntity>
{
    private final Predicate<PointOfInterestType> pointOfInterestType;
    private final MemoryModuleType<GlobalPos> targetMemoryModule;
    private final boolean onlyRunIfChild;
    private long lastRunTime;
    
    public FindPointOfInterestTask(final PointOfInterestType pointOfInterestType, final MemoryModuleType<GlobalPos> targetMemoryModule, final boolean onlyRunIfChild) {
        this.pointOfInterestType = pointOfInterestType.getCompletionCondition();
        this.targetMemoryModule = targetMemoryModule;
        this.onlyRunIfChild = onlyRunIfChild;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(this.targetMemoryModule, MemoryModuleState.b));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        return (!this.onlyRunIfChild || !entity.isChild()) && world.getTime() - this.lastRunTime >= 40L;
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        this.lastRunTime = world.getTime();
        final MobEntityWithAi mobEntityWithAi5 = (MobEntityWithAi)entity;
        final PointOfInterestStorage pointOfInterestStorage6 = world.getPointOfInterestStorage();
        final BlockPos.Mutable mutable4;
        final MobEntity mobEntity;
        final Path path5;
        final Predicate<BlockPos> predicate7 = blockPos -> {
            mutable4 = new BlockPos.Mutable(blockPos);
            if (world.getBlockState(blockPos.down()).isAir()) {
                mutable4.setOffset(Direction.DOWN);
            }
            while (world.getBlockState(mutable4).isAir() && mutable4.getY() >= 0) {
                mutable4.setOffset(Direction.DOWN);
            }
            path5 = mobEntity.getNavigation().findPathTo(mutable4.toImmutable());
            return path5 != null && path5.h();
        };
        pointOfInterestStorage6.getNearestPosition(this.pointOfInterestType, predicate7, new BlockPos(entity), 48).ifPresent(blockPos -> {
            entity.getBrain().<GlobalPos>putMemory(this.targetMemoryModule, GlobalPos.create(world.getDimension().getType(), blockPos));
            DebugRendererInfoManager.sendPointOfInterest(world, blockPos);
        });
    }
}
