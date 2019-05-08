package net.minecraft.entity.ai.brain.sensor;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import java.util.Set;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.LivingEntity;

public abstract class Sensor<E extends LivingEntity>
{
    private final int senseInterval;
    protected long lastSenseTime;
    
    public Sensor(final int senseInterval) {
        this.senseInterval = senseInterval;
    }
    
    public Sensor() {
        this(20);
    }
    
    public final void canSense(final ServerWorld world, final E entity) {
        if (world.getTime() - this.lastSenseTime >= this.senseInterval) {
            this.lastSenseTime = world.getTime();
            this.sense(world, entity);
        }
    }
    
    protected abstract void sense(final ServerWorld arg1, final E arg2);
    
    public abstract Set<MemoryModuleType<?>> getOutputMemoryModules();
}
