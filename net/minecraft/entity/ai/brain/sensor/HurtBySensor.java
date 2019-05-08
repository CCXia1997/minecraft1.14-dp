package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.LivingEntity;

public class HurtBySensor extends Sensor<LivingEntity>
{
    @Override
    protected void sense(final ServerWorld world, final LivingEntity entity) {
        final Brain<?> brain3 = entity.getBrain();
        if (entity.getRecentDamageSource() != null) {
            brain3.<DamageSource>putMemory(MemoryModuleType.r, entity.getRecentDamageSource());
            final Entity entity2 = brain3.<DamageSource>getOptionalMemory(MemoryModuleType.r).get().getAttacker();
            if (entity2 instanceof LivingEntity) {
                brain3.<LivingEntity>putMemory(MemoryModuleType.s, (LivingEntity)entity2);
            }
        }
        else {
            brain3.forget(MemoryModuleType.r);
        }
    }
    
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.r, (MemoryModuleType<DamageSource>)MemoryModuleType.s);
    }
}
