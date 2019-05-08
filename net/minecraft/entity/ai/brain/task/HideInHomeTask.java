package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.Position;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.entity.Entity;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;

public class HideInHomeTask extends Task<LivingEntity>
{
    private final float walkSpeed;
    private final int maxDistance;
    private final int preferredDistance;
    private Optional<BlockPos> homePosition;
    
    public HideInHomeTask(final int maxDistance, final float walkSpeed, final int preferredDistance) {
        this.homePosition = Optional.<BlockPos>empty();
        this.maxDistance = maxDistance;
        this.walkSpeed = walkSpeed;
        this.preferredDistance = preferredDistance;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.b), Pair.of(MemoryModuleType.b, MemoryModuleState.c), Pair.of(MemoryModuleType.v, MemoryModuleState.c));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        final Optional<BlockPos> optional3 = world.getPointOfInterestStorage().getPosition(pointOfInterestType -> pointOfInterestType == PointOfInterestType.q, blockPos -> true, new BlockPos(entity), this.preferredDistance + 1, PointOfInterestStorage.OccupationStatus.ANY);
        if (optional3.isPresent() && optional3.get().isWithinDistance(entity.getPos(), this.preferredDistance)) {
            this.homePosition = optional3;
        }
        else {
            this.homePosition = Optional.<BlockPos>empty();
        }
        return true;
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        final Brain<?> brain5 = entity.getBrain();
        Optional<BlockPos> optional6 = this.homePosition;
        if (!optional6.isPresent()) {
            optional6 = world.getPointOfInterestStorage().getPosition(pointOfInterestType -> pointOfInterestType == PointOfInterestType.q, blockPos -> true, PointOfInterestStorage.OccupationStatus.ANY, new BlockPos(entity), this.maxDistance, entity.getRand());
            if (!optional6.isPresent()) {
                final Optional<GlobalPos> optional7 = brain5.<GlobalPos>getOptionalMemory(MemoryModuleType.b);
                if (optional7.isPresent()) {
                    optional6 = Optional.<BlockPos>of(optional7.get().getPos());
                }
            }
        }
        if (optional6.isPresent()) {
            brain5.forget(MemoryModuleType.o);
            brain5.forget(MemoryModuleType.l);
            brain5.forget(MemoryModuleType.n);
            brain5.forget(MemoryModuleType.m);
            brain5.<GlobalPos>putMemory(MemoryModuleType.v, GlobalPos.create(world.getDimension().getType(), optional6.get()));
            if (!optional6.get().isWithinDistance(entity.getPos(), this.preferredDistance)) {
                brain5.<WalkTarget>putMemory(MemoryModuleType.k, new WalkTarget(optional6.get(), this.walkSpeed, this.preferredDistance));
            }
        }
    }
}
