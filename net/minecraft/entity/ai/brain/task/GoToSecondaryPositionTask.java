package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.WalkTarget;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import java.util.Optional;
import net.minecraft.util.math.Position;
import java.util.Objects;
import net.minecraft.server.world.ServerWorld;
import javax.annotation.Nullable;
import net.minecraft.util.GlobalPos;
import java.util.List;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;

public class GoToSecondaryPositionTask extends Task<VillagerEntity>
{
    private final MemoryModuleType<List<GlobalPos>> secondaryPositions;
    private final MemoryModuleType<GlobalPos> primaryPosition;
    private final float speed;
    private final int completionRange;
    private final int primaryPositionActivationDistance;
    private long nextRunTime;
    @Nullable
    private GlobalPos chosenPosition;
    
    public GoToSecondaryPositionTask(final MemoryModuleType<List<GlobalPos>> secondaryPositions, final float speed, final int completionRange, final int primaryPositionActivationDistance, final MemoryModuleType<GlobalPos> primaryPosition) {
        this.secondaryPositions = secondaryPositions;
        this.speed = speed;
        this.completionRange = completionRange;
        this.primaryPositionActivationDistance = primaryPositionActivationDistance;
        this.primaryPosition = primaryPosition;
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final VillagerEntity entity) {
        final Optional<List<GlobalPos>> optional3 = entity.getBrain().<List<GlobalPos>>getOptionalMemory(this.secondaryPositions);
        final Optional<GlobalPos> optional4 = entity.getBrain().<GlobalPos>getOptionalMemory(this.primaryPosition);
        if (optional3.isPresent() && optional4.isPresent()) {
            final List<GlobalPos> list5 = optional3.get();
            if (!list5.isEmpty()) {
                this.chosenPosition = list5.get(world.getRandom().nextInt(list5.size()));
                return this.chosenPosition != null && Objects.equals(world.getDimension().getType(), this.chosenPosition.getDimension()) && optional4.get().getPos().isWithinDistance(entity.getPos(), this.primaryPositionActivationDistance);
            }
        }
        return false;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.c), Pair.of(this.secondaryPositions, MemoryModuleState.a), Pair.of(this.primaryPosition, MemoryModuleState.a));
    }
    
    @Override
    protected void run(final ServerWorld world, final VillagerEntity entity, final long time) {
        if (time > this.nextRunTime && this.chosenPosition != null) {
            entity.getBrain().<WalkTarget>putMemory(MemoryModuleType.k, new WalkTarget(this.chosenPosition.getPos(), this.speed, this.completionRange));
            this.nextRunTime = time + 100L;
        }
    }
}
