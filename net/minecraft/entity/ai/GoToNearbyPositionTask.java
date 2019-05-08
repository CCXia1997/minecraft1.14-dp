package net.minecraft.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.WalkTarget;
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
import net.minecraft.entity.ai.brain.task.Task;

public class GoToNearbyPositionTask extends Task<MobEntityWithAi>
{
    private final MemoryModuleType<GlobalPos> memoryModuleType;
    private final int b;
    private final int maxDistance;
    private long nextRunTime;
    
    public GoToNearbyPositionTask(final MemoryModuleType<GlobalPos> memoryModuleType, final int integer2, final int integer3) {
        this.memoryModuleType = memoryModuleType;
        this.b = integer2;
        this.maxDistance = integer3;
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final MobEntityWithAi entity) {
        final Optional<GlobalPos> optional3 = entity.getBrain().<GlobalPos>getOptionalMemory(this.memoryModuleType);
        return optional3.isPresent() && Objects.equals(world.getDimension().getType(), optional3.get().getDimension()) && optional3.get().getPos().isWithinDistance(entity.getPos(), this.maxDistance);
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.c), Pair.of(this.memoryModuleType, MemoryModuleState.a));
    }
    
    @Override
    protected void run(final ServerWorld world, final MobEntityWithAi entity, final long time) {
        if (time > this.nextRunTime) {
            final Brain<?> brain5 = entity.getBrain();
            final Optional<GlobalPos> optional6 = brain5.<GlobalPos>getOptionalMemory(this.memoryModuleType);
            optional6.ifPresent(globalPos -> brain5.<WalkTarget>putMemory(MemoryModuleType.k, new WalkTarget(globalPos.getPos(), 0.4f, this.b)));
            this.nextRunTime = time + 80L;
        }
    }
}
