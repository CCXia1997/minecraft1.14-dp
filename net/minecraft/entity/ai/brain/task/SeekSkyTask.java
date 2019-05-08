package net.minecraft.entity.ai.brain.task;

import javax.annotation.Nullable;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.util.math.Vec3d;
import java.util.Optional;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;

public class SeekSkyTask extends Task<LivingEntity>
{
    private final float speed;
    
    public SeekSkyTask(final float speed) {
        this.speed = speed;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.k, MemoryModuleState.b));
    }
    
    @Override
    protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
        final Optional<Vec3d> optional5 = Optional.<Vec3d>ofNullable(this.findNearbySky(world, entity));
        if (optional5.isPresent()) {
            entity.getBrain().<WalkTarget>setMemory(MemoryModuleType.k, optional5.<WalkTarget>map(vec3d -> new WalkTarget(vec3d, this.speed, 0)));
        }
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final LivingEntity entity) {
        return !world.isSkyVisible(new BlockPos(entity.x, entity.getBoundingBox().minY, entity.z));
    }
    
    @Nullable
    private Vec3d findNearbySky(final ServerWorld world, final LivingEntity entity) {
        final Random random3 = entity.getRand();
        final BlockPos blockPos4 = new BlockPos(entity.x, entity.getBoundingBox().minY, entity.z);
        for (int integer5 = 0; integer5 < 10; ++integer5) {
            final BlockPos blockPos5 = blockPos4.add(random3.nextInt(20) - 10, random3.nextInt(6) - 3, random3.nextInt(20) - 10);
            if (world.isSkyVisible(blockPos5)) {
                return new Vec3d(blockPos5.getX(), blockPos5.getY(), blockPos5.getZ());
            }
        }
        return null;
    }
}
