package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.util.GlobalPos;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;

public class VillagerWalkTowardsTask extends Task<VillagerEntity>
{
    private final MemoryModuleType<GlobalPos> a;
    private final float b;
    private final int c;
    private final int d;
    
    public VillagerWalkTowardsTask(final MemoryModuleType<GlobalPos> memoryModuleType, final float float2, final int integer3, final int integer4) {
        this.a = memoryModuleType;
        this.b = float2;
        this.c = integer3;
        this.d = integer4;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.b), Pair.of(this.a, MemoryModuleState.a));
    }
    
    @Override
    protected void run(final ServerWorld world, final VillagerEntity entity, final long time) {
        final Brain<?> brain5 = entity.getBrain();
        final Brain brain6;
        brain5.<GlobalPos>getOptionalMemory(this.a).ifPresent(globalPos -> {
            if (this.a(world, entity, globalPos)) {
                entity.releaseTicketFor(this.a);
                brain6.forget(this.a);
            }
            else if (!this.b(world, entity, globalPos)) {
                brain6.putMemory(MemoryModuleType.k, new WalkTarget(globalPos.getPos(), this.b, this.c));
            }
        });
    }
    
    private boolean a(final ServerWorld serverWorld, final VillagerEntity villagerEntity, final GlobalPos globalPos) {
        return globalPos.getDimension() != serverWorld.getDimension().getType() || globalPos.getPos().getManhattanDistance(new BlockPos(villagerEntity)) > this.d;
    }
    
    private boolean b(final ServerWorld serverWorld, final VillagerEntity villagerEntity, final GlobalPos globalPos) {
        return globalPos.getDimension() == serverWorld.getDimension().getType() && globalPos.getPos().getManhattanDistance(new BlockPos(villagerEntity)) <= this.c;
    }
}
