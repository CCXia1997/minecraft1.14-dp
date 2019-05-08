package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.WalkTarget;
import java.util.Optional;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.mob.MobEntityWithAi;

public class FindWalkTargetTask extends Task<MobEntityWithAi>
{
    private final float walkSpeed;
    
    public FindWalkTargetTask(final float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.b));
    }
    
    @Override
    protected void run(final ServerWorld world, final MobEntityWithAi entity, final long time) {
        final BlockPos blockPos5 = new BlockPos(entity);
        if (world.isNearOccupiedPointOfInterest(blockPos5)) {
            this.a(entity);
        }
        else {
            final ChunkSectionPos chunkSectionPos6 = ChunkSectionPos.from(blockPos5);
            final ChunkSectionPos chunkSectionPos7 = LookTargetUtil.getPosClosestToOccupiedPointOfInterest(world, chunkSectionPos6, 2);
            if (chunkSectionPos7 != chunkSectionPos6) {
                this.a(entity, chunkSectionPos7);
            }
            else {
                this.a(entity);
            }
        }
    }
    
    private void a(final MobEntityWithAi mobEntityWithAi, final ChunkSectionPos chunkSectionPos) {
        final BlockPos blockPos3 = chunkSectionPos.getCenterPos();
        final Optional<Vec3d> optional4 = Optional.<Vec3d>ofNullable(PathfindingUtil.a(mobEntityWithAi, 10, 7, new Vec3d(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ())));
        mobEntityWithAi.getBrain().<WalkTarget>setMemory(MemoryModuleType.k, optional4.<WalkTarget>map(vec3d -> new WalkTarget(vec3d, this.walkSpeed, 0)));
    }
    
    private void a(final MobEntityWithAi mobEntityWithAi) {
        final Optional<Vec3d> optional2 = Optional.<Vec3d>ofNullable(PathfindingUtil.findTargetStraight(mobEntityWithAi, 10, 7));
        mobEntityWithAi.getBrain().<WalkTarget>setMemory(MemoryModuleType.k, optional2.<WalkTarget>map(vec3d -> new WalkTarget(vec3d, this.walkSpeed, 0)));
    }
}
