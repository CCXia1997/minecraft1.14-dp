package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.world.dimension.DimensionType;
import java.util.List;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.GlobalPos;
import net.minecraft.tag.BlockTags;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.LivingEntity;

public class InteractableDoorsSensor extends Sensor<LivingEntity>
{
    @Override
    protected void sense(final ServerWorld world, final LivingEntity entity) {
        final DimensionType dimensionType3 = world.getDimension().getType();
        final BlockPos blockPos4 = new BlockPos(entity);
        final List<GlobalPos> list5 = Lists.newArrayList();
        for (int integer6 = -1; integer6 <= 1; ++integer6) {
            for (int integer7 = -1; integer7 <= 1; ++integer7) {
                for (int integer8 = -1; integer8 <= 1; ++integer8) {
                    final BlockPos blockPos5 = blockPos4.add(integer6, integer7, integer8);
                    if (world.getBlockState(blockPos5).matches(BlockTags.g)) {
                        list5.add(GlobalPos.create(dimensionType3, blockPos5));
                    }
                }
            }
        }
        final Brain<?> brain6 = entity.getBrain();
        if (!list5.isEmpty()) {
            brain6.<List<GlobalPos>>putMemory(MemoryModuleType.p, list5);
        }
        else {
            brain6.forget(MemoryModuleType.p);
        }
    }
    
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.p);
    }
}
