package net.minecraft.entity.ai.brain.task;

import java.util.function.Predicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.util.math.Direction;
import java.util.Optional;
import net.minecraft.util.math.Vec3i;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;

public class WalkHomeTask extends Task<LivingEntity>
{
    private final float speed;
    private long lastRunTime;
    
    public WalkHomeTask(final float speed) {
        this.speed = speed;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.b), Pair.of(MemoryModuleType.b, MemoryModuleState.b));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        if (world.getTime() - this.lastRunTime < 40L) {
            return false;
        }
        final MobEntityWithAi mobEntityWithAi3 = (MobEntityWithAi)entity;
        final PointOfInterestStorage pointOfInterestStorage4 = world.getPointOfInterestStorage();
        final Optional<BlockPos> optional5 = pointOfInterestStorage4.getNearestPosition(PointOfInterestType.q.getCompletionCondition(), blockPos -> true, new BlockPos(entity), 48, PointOfInterestStorage.OccupationStatus.ANY);
        return optional5.isPresent() && optional5.get().getSquaredDistance(new Vec3i(mobEntityWithAi3.x, mobEntityWithAi3.y, mobEntityWithAi3.z)) > 4.0;
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
        pointOfInterestStorage6.getNearestPosition(PointOfInterestType.q.getCompletionCondition(), predicate7, new BlockPos(entity), 48, PointOfInterestStorage.OccupationStatus.ANY).ifPresent(blockPos -> {
            entity.getBrain().<WalkTarget>putMemory(MemoryModuleType.k, new WalkTarget(blockPos, this.speed, 1));
            DebugRendererInfoManager.sendPointOfInterest(world, blockPos);
        });
    }
}
