package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import java.util.Optional;
import net.minecraft.entity.ai.brain.WalkTarget;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.mob.MobEntityWithAi;

public class WanderIndoorsTask extends Task<MobEntityWithAi>
{
    private final float speed;
    
    public WanderIndoorsTask(final float float1) {
        this.speed = float1;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.b));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final MobEntityWithAi entity) {
        return !world.isSkyVisible(new BlockPos(entity));
    }
    
    @Override
    protected void run(final ServerWorld world, final MobEntityWithAi entity, final long time) {
        final BlockPos blockPos2 = new BlockPos(entity);
        final List<BlockPos> list6 = BlockPos.stream(blockPos2.add(-1, -1, -1), blockPos2.add(1, 1, 1)).map(BlockPos::toImmutable).collect(Collectors.toList());
        Collections.shuffle(list6);
        final Optional<BlockPos> optional7 = list6.stream().filter(blockPos -> !world.isSkyVisible(blockPos)).filter(blockPos -> world.doesBlockHaveSolidTopSurface(blockPos, entity)).filter(blockPos -> world.doesNotCollide(entity)).findFirst();
        optional7.ifPresent(blockPos -> entity.getBrain().<WalkTarget>putMemory(MemoryModuleType.k, new WalkTarget(blockPos, this.speed, 0)));
    }
}
