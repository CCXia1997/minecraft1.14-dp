package net.minecraft.entity.ai.brain.sensor;

import net.minecraft.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.entity.Entity;
import java.util.Optional;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import java.util.Set;
import net.minecraft.entity.mob.MobEntity;

public class NearestBedSensor extends Sensor<MobEntity>
{
    public NearestBedSensor() {
        super(100);
    }
    
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.q);
    }
    
    @Override
    protected void sense(final ServerWorld world, final MobEntity entity) {
        entity.getBrain().<BlockPos>setMemory(MemoryModuleType.q, this.findNearestBed(world, entity));
    }
    
    private Optional<BlockPos> findNearestBed(final ServerWorld world, final MobEntity mob) {
        final PointOfInterestStorage pointOfInterestStorage3 = world.getPointOfInterestStorage();
        Path path3;
        final Predicate<BlockPos> predicate4 = blockPos -> {
            if (blockPos.equals(new BlockPos(mob))) {
                return true;
            }
            else {
                path3 = mob.getNavigation().findPathTo(blockPos);
                return path3 != null && path3.h();
            }
        };
        return pointOfInterestStorage3.getNearestPosition(PointOfInterestType.q.getCompletionCondition(), predicate4, new BlockPos(mob), 16, PointOfInterestStorage.OccupationStatus.ANY);
    }
}
