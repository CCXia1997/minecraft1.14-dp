package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.util.math.Position;
import java.util.Objects;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.village.PointOfInterestType;
import java.util.function.Predicate;
import net.minecraft.util.GlobalPos;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.LivingEntity;

public class ForgetCompletedPointOfInterestTask extends Task<LivingEntity>
{
    private final MemoryModuleType<GlobalPos> memoryModule;
    private final Predicate<PointOfInterestType> condition;
    
    public ForgetCompletedPointOfInterestTask(final PointOfInterestType pointOfInterestType, final MemoryModuleType<GlobalPos> memoryModule) {
        this.condition = pointOfInterestType.getCompletionCondition();
        this.memoryModule = memoryModule;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(this.memoryModule, MemoryModuleState.a));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        final GlobalPos globalPos3 = entity.getBrain().<GlobalPos>getOptionalMemory(this.memoryModule).get();
        return Objects.equals(world.getDimension().getType(), globalPos3.getDimension()) && globalPos3.getPos().isWithinDistance(entity.getPos(), 3.0);
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        final Brain<?> brain5 = entity.getBrain();
        final GlobalPos globalPos6 = brain5.<GlobalPos>getOptionalMemory(this.memoryModule).get();
        final ServerWorld serverWorld7 = world.getServer().getWorld(globalPos6.getDimension());
        if (!serverWorld7.getPointOfInterestStorage().test(globalPos6.getPos(), this.condition)) {
            brain5.forget(this.memoryModule);
        }
    }
}
