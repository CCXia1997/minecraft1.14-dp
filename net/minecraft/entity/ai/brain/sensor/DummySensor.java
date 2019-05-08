package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import java.util.Set;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.LivingEntity;

public class DummySensor extends Sensor<LivingEntity>
{
    @Override
    protected void sense(final ServerWorld world, final LivingEntity entity) {
    }
    
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of();
    }
}
