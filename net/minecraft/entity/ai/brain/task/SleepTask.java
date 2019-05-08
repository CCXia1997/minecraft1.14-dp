package net.minecraft.entity.ai.brain.task;

import net.minecraft.util.math.BlockPos;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Activity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.block.BedBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.Position;
import java.util.Objects;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.GlobalPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.LivingEntity;

public class SleepTask extends Task<LivingEntity>
{
    private long a;
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        if (entity.hasVehicle()) {
            return false;
        }
        final GlobalPos globalPos3 = entity.getBrain().<GlobalPos>getOptionalMemory(MemoryModuleType.b).get();
        if (!Objects.equals(world.getDimension().getType(), globalPos3.getDimension())) {
            return false;
        }
        final BlockState blockState4 = world.getBlockState(globalPos3.getPos());
        return globalPos3.getPos().isWithinDistance(entity.getPos(), 2.0) && blockState4.getBlock().matches(BlockTags.F) && !blockState4.<Boolean>get((Property<Boolean>)BedBlock.OCCUPIED);
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.b, MemoryModuleState.a));
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final LivingEntity entity, final long time) {
        final Optional<GlobalPos> optional5 = entity.getBrain().<GlobalPos>getOptionalMemory(MemoryModuleType.b);
        if (!optional5.isPresent()) {
            return false;
        }
        final BlockPos blockPos6 = optional5.get().getPos();
        return entity.getBrain().hasActivity(Activity.e) && entity.y > blockPos6.getY() + 0.4 && blockPos6.isWithinDistance(entity.getPos(), 1.14);
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        if (time > this.a) {
            entity.sleep(entity.getBrain().<GlobalPos>getOptionalMemory(MemoryModuleType.b).get().getPos());
        }
    }
    
    @Override
    protected boolean isTimeLimitExceeded(final long time) {
        return false;
    }
    
    @Override
    protected void finishRunning(final ServerWorld serverWorld, final LivingEntity livingEntity, final long time) {
        if (livingEntity.isSleeping()) {
            livingEntity.wakeUp();
            this.a = time + 40L;
        }
    }
}
