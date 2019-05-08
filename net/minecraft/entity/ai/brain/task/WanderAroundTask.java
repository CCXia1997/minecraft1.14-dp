package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;

public class WanderAroundTask extends Task<MobEntity>
{
    @Nullable
    private Path a;
    @Nullable
    private BlockPos b;
    private float c;
    private int d;
    
    public WanderAroundTask(final int exactRunTime) {
        super(exactRunTime);
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.o, MemoryModuleState.b), Pair.of(MemoryModuleType.k, MemoryModuleState.a));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final MobEntity entity) {
        final Brain<?> brain3 = entity.getBrain();
        final WalkTarget walkTarget4 = brain3.<WalkTarget>getOptionalMemory(MemoryModuleType.k).get();
        if (!this.b(entity, walkTarget4) && this.a(entity, walkTarget4)) {
            this.b = walkTarget4.getLookTarget().getBlockPos();
            return true;
        }
        brain3.forget(MemoryModuleType.k);
        return false;
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final MobEntity entity, final long time) {
        if (this.a == null || this.b == null) {
            return false;
        }
        final Optional<WalkTarget> optional5 = entity.getBrain().<WalkTarget>getOptionalMemory(MemoryModuleType.k);
        final EntityNavigation entityNavigation6 = entity.getNavigation();
        return !entityNavigation6.isIdle() && optional5.isPresent() && !this.b(entity, optional5.get());
    }
    
    @Override
    protected void finishRunning(final ServerWorld serverWorld, final MobEntity mobEntity, final long time) {
        mobEntity.getNavigation().stop();
        mobEntity.getBrain().forget(MemoryModuleType.k);
        mobEntity.getBrain().forget(MemoryModuleType.o);
        this.a = null;
    }
    
    @Override
    protected void run(final ServerWorld world, final MobEntity entity, final long time) {
        entity.getBrain().<Path>putMemory(MemoryModuleType.o, this.a);
        entity.getNavigation().startMovingAlong(this.a, this.c);
        this.d = world.getRandom().nextInt(10);
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final MobEntity entity, final long time) {
        --this.d;
        if (this.d > 0) {
            return;
        }
        final Path path5 = entity.getNavigation().getCurrentPath();
        if (this.a != path5) {
            this.a = path5;
            entity.getBrain().<Path>putMemory(MemoryModuleType.o, path5);
        }
        if (path5 == null || this.b == null) {
            return;
        }
        final WalkTarget walkTarget6 = entity.getBrain().<WalkTarget>getOptionalMemory(MemoryModuleType.k).get();
        if (walkTarget6.getLookTarget().getBlockPos().getSquaredDistance(this.b) > 4.0 && this.a(entity, walkTarget6)) {
            this.b = walkTarget6.getLookTarget().getBlockPos();
            this.run(world, entity, time);
        }
    }
    
    private boolean a(final MobEntity mobEntity, final WalkTarget walkTarget) {
        final BlockPos blockPos3 = walkTarget.getLookTarget().getBlockPos();
        this.a = mobEntity.getNavigation().findPathTo(blockPos3);
        this.c = walkTarget.getSpeed();
        if (!this.b(mobEntity, walkTarget)) {
            if (this.a != null) {
                return true;
            }
            final Vec3d vec3d4 = PathfindingUtil.a((MobEntityWithAi)mobEntity, 10, 7, new Vec3d(blockPos3));
            if (vec3d4 != null) {
                this.a = mobEntity.getNavigation().findPathTo(vec3d4.x, vec3d4.y, vec3d4.z);
                return this.a != null;
            }
        }
        return false;
    }
    
    private boolean b(final MobEntity mobEntity, final WalkTarget walkTarget) {
        return walkTarget.getLookTarget().getBlockPos().getManhattanDistance(new BlockPos(mobEntity)) <= walkTarget.getCompletionRange();
    }
}
