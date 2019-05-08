package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import java.util.Optional;
import net.minecraft.tag.BlockTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.mob.MobEntity;

public class JumpInBedTask extends Task<MobEntity>
{
    private final float walkSpeed;
    @Nullable
    private BlockPos bed;
    private int ticksOutOfBedUntilStopped;
    private int jumpsRemaining;
    private int ticksToNextJump;
    
    public JumpInBedTask(final float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.q, MemoryModuleState.a), Pair.of(MemoryModuleType.k, MemoryModuleState.b));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final MobEntity entity) {
        return entity.isChild() && this.shouldStartJumping(world, entity);
    }
    
    @Override
    protected void run(final ServerWorld world, final MobEntity entity, final long time) {
        super.run(world, entity, time);
        this.getNearestBed(entity).ifPresent(blockPos -> {
            this.bed = blockPos;
            this.ticksOutOfBedUntilStopped = 100;
            this.jumpsRemaining = 3 + world.random.nextInt(4);
            this.ticksToNextJump = 0;
            this.setWalkTarget(entity, blockPos);
        });
    }
    
    @Override
    protected void finishRunning(final ServerWorld serverWorld, final MobEntity mobEntity, final long time) {
        super.finishRunning(serverWorld, mobEntity, time);
        this.bed = null;
        this.ticksOutOfBedUntilStopped = 0;
        this.jumpsRemaining = 0;
        this.ticksToNextJump = 0;
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final MobEntity entity, final long time) {
        return entity.isChild() && this.bed != null && this.isBedAt(world, this.bed) && !this.isBedGoneTooLong(world, entity) && !this.isDoneJumping(world, entity);
    }
    
    @Override
    protected boolean isTimeLimitExceeded(final long time) {
        return false;
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final MobEntity entity, final long time) {
        if (!this.isAboveBed(world, entity)) {
            --this.ticksOutOfBedUntilStopped;
            return;
        }
        if (this.ticksToNextJump > 0) {
            --this.ticksToNextJump;
            return;
        }
        if (this.isOnBed(world, entity)) {
            entity.getJumpControl().setActive();
            --this.jumpsRemaining;
            this.ticksToNextJump = 5;
        }
    }
    
    private void setWalkTarget(final MobEntity mob, final BlockPos pos) {
        mob.getBrain().<WalkTarget>putMemory(MemoryModuleType.k, new WalkTarget(pos, this.walkSpeed, 0));
    }
    
    private boolean shouldStartJumping(final ServerWorld world, final MobEntity mob) {
        return this.isAboveBed(world, mob) || this.getNearestBed(mob).isPresent();
    }
    
    private boolean isAboveBed(final ServerWorld world, final MobEntity mob) {
        final BlockPos blockPos3 = new BlockPos(mob);
        final BlockPos blockPos4 = blockPos3.down();
        return this.isBedAt(world, blockPos3) || this.isBedAt(world, blockPos4);
    }
    
    private boolean isOnBed(final ServerWorld world, final MobEntity mob) {
        return this.isBedAt(world, new BlockPos(mob));
    }
    
    private boolean isBedAt(final ServerWorld world, final BlockPos pos) {
        return world.getBlockState(pos).matches(BlockTags.F);
    }
    
    private Optional<BlockPos> getNearestBed(final MobEntity mob) {
        return mob.getBrain().<BlockPos>getOptionalMemory(MemoryModuleType.q);
    }
    
    private boolean isBedGoneTooLong(final ServerWorld world, final MobEntity mob) {
        return !this.isAboveBed(world, mob) && this.ticksOutOfBedUntilStopped <= 0;
    }
    
    private boolean isDoneJumping(final ServerWorld world, final MobEntity mob) {
        return this.isAboveBed(world, mob) && this.jumpsRemaining <= 0;
    }
}
