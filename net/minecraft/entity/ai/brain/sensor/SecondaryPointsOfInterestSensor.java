package net.minecraft.entity.ai.brain.sensor;

import net.minecraft.entity.LivingEntity;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.world.dimension.DimensionType;
import java.util.List;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.GlobalPos;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.passive.VillagerEntity;

public class SecondaryPointsOfInterestSensor extends Sensor<VillagerEntity>
{
    public SecondaryPointsOfInterestSensor() {
        super(40);
    }
    
    @Override
    protected void sense(final ServerWorld world, final VillagerEntity entity) {
        final DimensionType dimensionType3 = world.getDimension().getType();
        final BlockPos blockPos4 = new BlockPos(entity);
        final List<GlobalPos> list5 = Lists.newArrayList();
        final int integer6 = 4;
        for (int integer7 = -4; integer7 <= 4; ++integer7) {
            for (int integer8 = -2; integer8 <= 2; ++integer8) {
                for (int integer9 = -4; integer9 <= 4; ++integer9) {
                    final BlockPos blockPos5 = blockPos4.add(integer7, integer8, integer9);
                    if (entity.getVillagerData().getProfession().getSecondaryJobSites().contains(world.getBlockState(blockPos5).getBlock())) {
                        list5.add(GlobalPos.create(dimensionType3, blockPos5));
                    }
                }
            }
        }
        final Brain<?> brain7 = entity.getBrain();
        if (!list5.isEmpty()) {
            brain7.<List<GlobalPos>>putMemory(MemoryModuleType.e, list5);
        }
        else {
            brain7.forget(MemoryModuleType.e);
        }
    }
    
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.e);
    }
}
