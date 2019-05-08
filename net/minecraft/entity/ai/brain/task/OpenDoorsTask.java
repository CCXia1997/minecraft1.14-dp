package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;
import net.minecraft.block.DoorBlock;
import net.minecraft.tag.BlockTags;
import java.util.function.Predicate;
import java.util.function.Function;
import net.minecraft.entity.ai.brain.Brain;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.GlobalPos;
import java.util.List;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;

public class OpenDoorsTask extends Task<LivingEntity>
{
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.o, MemoryModuleState.a), Pair.of(MemoryModuleType.p, MemoryModuleState.a));
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        final Brain<?> brain5 = entity.getBrain();
        final Path path6 = brain5.<Path>getOptionalMemory(MemoryModuleType.o).get();
        final List<GlobalPos> list7 = brain5.<List<GlobalPos>>getOptionalMemory(MemoryModuleType.p).get();
        final List<BlockPos> list8 = path6.getNodes().stream().map(pathNode -> new BlockPos(pathNode.x, pathNode.y, pathNode.z)).collect(Collectors.toList());
        final Set<BlockPos> set9 = this.getDoorsOnPath(world, list7, list8);
        final int integer10 = path6.getCurrentNodeIndex() - 1;
        this.openDoors(world, list8, set9, integer10);
    }
    
    private Set<BlockPos> getDoorsOnPath(final ServerWorld world, final List<GlobalPos> doors, final List<BlockPos> path) {
        return doors.stream().filter(globalPos -> globalPos.getDimension() == world.getDimension().getType()).map(GlobalPos::getPos).filter(path::contains).collect(Collectors.toSet());
    }
    
    private void openDoors(final ServerWorld world, final List<BlockPos> path, final Set<BlockPos> doors, final int pathIndex) {
        final int integer5;
        final BlockState blockState6;
        final Block block7;
        doors.forEach(blockPos -> {
            integer5 = path.indexOf(blockPos);
            blockState6 = world.getBlockState(blockPos);
            block7 = blockState6.getBlock();
            if (BlockTags.g.contains(block7) && block7 instanceof DoorBlock) {
                ((DoorBlock)block7).setOpen(world, blockPos, integer5 >= pathIndex);
            }
        });
    }
}
