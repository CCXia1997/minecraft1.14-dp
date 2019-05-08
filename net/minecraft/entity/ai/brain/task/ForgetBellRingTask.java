package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.GlobalPos;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;

public class ForgetBellRingTask extends Task<LivingEntity>
{
    private final int a;
    private final int b;
    private int c;
    
    public ForgetBellRingTask(final int minRunTime, final int maxRunTime) {
        this.b = minRunTime * 20;
        this.c = 0;
        this.a = maxRunTime;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.v, MemoryModuleState.a), Pair.of(MemoryModuleType.w, MemoryModuleState.a));
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        final Brain<?> brain5 = entity.getBrain();
        final Optional<Long> optional6 = brain5.<Long>getOptionalMemory(MemoryModuleType.w);
        final boolean boolean7 = optional6.get() + 300L <= time;
        if (this.c > this.b || boolean7) {
            brain5.forget(MemoryModuleType.w);
            brain5.forget(MemoryModuleType.v);
            brain5.refreshActivities(world.getTimeOfDay(), world.getTime());
            this.c = 0;
            return;
        }
        final BlockPos blockPos8 = brain5.<GlobalPos>getOptionalMemory(MemoryModuleType.v).get().getPos();
        if (blockPos8.isWithinDistance(new BlockPos(entity), this.a + 1)) {
            ++this.c;
        }
    }
}
