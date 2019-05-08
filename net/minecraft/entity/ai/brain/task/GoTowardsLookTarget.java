package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;

public class GoTowardsLookTarget extends Task<LivingEntity>
{
    private final float speed;
    private final int completionRange;
    
    public GoTowardsLookTarget(final float speed, final int completionRange) {
        this.speed = speed;
        this.completionRange = completionRange;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.b), Pair.of(MemoryModuleType.l, MemoryModuleState.a));
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        final Brain<?> brain5 = entity.getBrain();
        final LookTarget lookTarget6 = brain5.<LookTarget>getOptionalMemory(MemoryModuleType.l).get();
        brain5.<WalkTarget>putMemory(MemoryModuleType.k, new WalkTarget(lookTarget6, this.speed, this.completionRange));
    }
}
