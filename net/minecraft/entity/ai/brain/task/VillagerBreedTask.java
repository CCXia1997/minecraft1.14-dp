package net.minecraft.entity.ai.brain.task;

import net.minecraft.util.GlobalPos;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.EntityType;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.passive.VillagerEntity;

public class VillagerBreedTask extends Task<VillagerEntity>
{
    private long a;
    
    public VillagerBreedTask() {
        super(350, 350);
    }
    
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.n, MemoryModuleState.a), Pair.of(MemoryModuleType.g, MemoryModuleState.a));
    }
    
    @Override
    protected boolean shouldRun(final ServerWorld world, final VillagerEntity entity) {
        return this.b(entity);
    }
    
    @Override
    protected boolean shouldKeepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        return time <= this.a && this.b(entity);
    }
    
    @Override
    protected void run(final ServerWorld world, final VillagerEntity entity, final long time) {
        final VillagerEntity villagerEntity5 = this.a(entity);
        LookTargetUtil.lookAtAndWalkTowardsEachOther(entity, villagerEntity5);
        world.sendEntityStatus(villagerEntity5, (byte)18);
        world.sendEntityStatus(entity, (byte)18);
        final int integer6 = 275 + entity.getRand().nextInt(50);
        this.a = time + integer6;
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        final VillagerEntity villagerEntity5 = this.a(entity);
        if (entity.squaredDistanceTo(villagerEntity5) > 5.0) {
            return;
        }
        LookTargetUtil.lookAtAndWalkTowardsEachOther(entity, villagerEntity5);
        if (time >= this.a) {
            final Optional<BlockPos> optional6 = this.b(world, entity);
            if (!optional6.isPresent()) {
                world.sendEntityStatus(villagerEntity5, (byte)13);
                world.sendEntityStatus(entity, (byte)13);
                return;
            }
            entity.consumeAvailableFood();
            villagerEntity5.consumeAvailableFood();
            final Optional<VillagerEntity> optional7 = this.a(entity, villagerEntity5);
            if (optional7.isPresent()) {
                entity.depleteFood(12);
                villagerEntity5.depleteFood(12);
                this.a(world, optional7.get(), optional6.get());
            }
            else {
                world.getPointOfInterestStorage().releaseTicket(optional6.get());
            }
        }
        if (entity.getRand().nextInt(35) == 0) {
            world.sendEntityStatus(villagerEntity5, (byte)12);
            world.sendEntityStatus(entity, (byte)12);
        }
    }
    
    @Override
    protected void keepRunning(final ServerWorld world, final VillagerEntity entity, final long time) {
        entity.getBrain().forget(MemoryModuleType.n);
    }
    
    private VillagerEntity a(final VillagerEntity villagerEntity) {
        return villagerEntity.getBrain().<VillagerEntity>getOptionalMemory(MemoryModuleType.n).get();
    }
    
    private boolean b(final VillagerEntity villagerEntity) {
        final Brain<VillagerEntity> brain2 = villagerEntity.getBrain();
        if (!brain2.<VillagerEntity>getOptionalMemory(MemoryModuleType.n).isPresent()) {
            return false;
        }
        final VillagerEntity villagerEntity2 = this.a(villagerEntity);
        return LookTargetUtil.canSee(brain2, MemoryModuleType.n, EntityType.VILLAGER) && villagerEntity.isReadyToBreed() && villagerEntity2.isReadyToBreed();
    }
    
    private Optional<BlockPos> b(final ServerWorld serverWorld, final VillagerEntity villagerEntity) {
        return serverWorld.getPointOfInterestStorage().getPosition(PointOfInterestType.q.getCompletionCondition(), blockPos -> true, new BlockPos(villagerEntity), 48);
    }
    
    private Optional<VillagerEntity> a(final VillagerEntity villagerEntity1, final VillagerEntity villagerEntity2) {
        final VillagerEntity villagerEntity3 = villagerEntity1.createChild((PassiveEntity)villagerEntity2);
        if (villagerEntity3 == null) {
            return Optional.<VillagerEntity>empty();
        }
        villagerEntity1.setBreedingAge(6000);
        villagerEntity2.setBreedingAge(6000);
        villagerEntity3.setBreedingAge(-24000);
        villagerEntity3.setPositionAndAngles(villagerEntity1.x, villagerEntity1.y, villagerEntity1.z, 0.0f, 0.0f);
        villagerEntity1.world.spawnEntity(villagerEntity3);
        villagerEntity1.world.sendEntityStatus(villagerEntity3, (byte)12);
        return Optional.<VillagerEntity>of(villagerEntity3);
    }
    
    private void a(final ServerWorld serverWorld, final VillagerEntity villagerEntity, final BlockPos blockPos) {
        final GlobalPos globalPos4 = GlobalPos.create(serverWorld.getDimension().getType(), blockPos);
        villagerEntity.getBrain().<GlobalPos>putMemory(MemoryModuleType.b, globalPos4);
    }
}
