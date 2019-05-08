package net.minecraft.entity.ai.brain.task;

import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BellBlock;
import net.minecraft.block.Blocks;
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

public class RingBellTask extends Task<LivingEntity>
{
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.d, MemoryModuleState.a));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        return world.random.nextFloat() > 0.95f;
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        final Brain<?> brain5 = entity.getBrain();
        final BlockPos blockPos6 = brain5.<GlobalPos>getOptionalMemory(MemoryModuleType.d).get().getPos();
        if (blockPos6.isWithinDistance(new BlockPos(entity), 2.0)) {
            final BlockState blockState7 = world.getBlockState(blockPos6);
            if (blockState7.getBlock() == Blocks.lT) {
                final BellBlock bellBlock8 = (BellBlock)blockState7.getBlock();
                for (final Direction direction10 : Direction.Type.HORIZONTAL) {
                    if (bellBlock8.ring(world, blockState7, world.getBlockEntity(blockPos6), new BlockHitResult(new Vec3d(0.5, 0.5, 0.5), direction10, blockPos6, false), null)) {
                        break;
                    }
                }
            }
        }
    }
}
