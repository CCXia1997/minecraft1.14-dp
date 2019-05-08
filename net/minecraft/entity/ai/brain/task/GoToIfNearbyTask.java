package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.PathfindingUtil;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import java.util.Optional;
import net.minecraft.util.math.Position;
import java.util.Objects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntityWithAi;

public class GoToIfNearbyTask extends Task<MobEntityWithAi>
{
    private final MemoryModuleType<GlobalPos> target;
    private long nextUpdateTime;
    private final int maxDistance;
    
    public GoToIfNearbyTask(final MemoryModuleType<GlobalPos> target, final int maxDistance) {
        this.target = target;
        this.maxDistance = maxDistance;
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final MobEntityWithAi entity) {
        final Optional<GlobalPos> optional3 = entity.getBrain().<GlobalPos>getOptionalMemory(this.target);
        return optional3.isPresent() && Objects.equals(world.getDimension().getType(), optional3.get().getDimension()) && optional3.get().getPos().isWithinDistance(entity.getPos(), this.maxDistance);
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.c), Pair.of(this.target, MemoryModuleState.a));
    }
    
    @Override
    protected void run(final ServerWorld world, final MobEntityWithAi entity, final long time) {
        if (time > this.nextUpdateTime) {
            final Optional<Vec3d> optional5 = Optional.<Vec3d>ofNullable(PathfindingUtil.findTargetStraight(entity, 8, 6));
            entity.getBrain().<WalkTarget>setMemory(MemoryModuleType.k, optional5.<WalkTarget>map(vec3d -> new WalkTarget(vec3d, 0.4f, 1)));
            this.nextUpdateTime = time + 180L;
        }
    }
}
