package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import java.util.Optional;
import net.minecraft.util.math.Position;
import java.util.Objects;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.util.GlobalPos;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.passive.VillagerEntity;

public class VillagerWorkTask extends Task<VillagerEntity>
{
    private int ticks;
    private boolean b;
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final VillagerEntity entity) {
        return this.a(world.getTimeOfDay() % 24000L, entity.getLastRestock());
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.c, MemoryModuleState.a), Pair.of(MemoryModuleType.l, MemoryModuleState.c), Pair.of(MemoryModuleType.u, MemoryModuleState.c));
    }
    
    @Override
    protected void finishRunning(final ServerWorld serverWorld, final VillagerEntity villagerEntity, final long time) {
        this.b = false;
        this.ticks = 0;
        villagerEntity.getBrain().forget(MemoryModuleType.l);
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        final Brain<VillagerEntity> brain5 = entity.getBrain();
        final VillagerEntity.GolemSpawnCondition golemSpawnCondition6 = brain5.<VillagerEntity.GolemSpawnCondition>getOptionalMemory(MemoryModuleType.u).orElseGet(VillagerEntity.GolemSpawnCondition::new);
        golemSpawnCondition6.setLastWorked(time);
        brain5.<VillagerEntity.GolemSpawnCondition>putMemory(MemoryModuleType.u, golemSpawnCondition6);
        if (!this.b) {
            entity.restock();
            this.b = true;
            entity.playWorkSound();
            brain5.<GlobalPos>getOptionalMemory(MemoryModuleType.c).ifPresent(globalPos -> brain5.<BlockPosLookTarget>putMemory((MemoryModuleType<BlockPosLookTarget>)MemoryModuleType.l, new BlockPosLookTarget(globalPos.getPos())));
        }
        ++this.ticks;
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        final Optional<GlobalPos> optional5 = entity.getBrain().<GlobalPos>getOptionalMemory(MemoryModuleType.c);
        if (!optional5.isPresent()) {
            return false;
        }
        final GlobalPos globalPos6 = optional5.get();
        return this.ticks < 100 && Objects.equals(globalPos6.getDimension(), world.getDimension().getType()) && globalPos6.getPos().isWithinDistance(entity.getPos(), 1.73);
    }
    
    private boolean a(final long long1, final long long3) {
        return long3 == 0L || long1 < long3 || long1 > long3 + 3500L;
    }
}
