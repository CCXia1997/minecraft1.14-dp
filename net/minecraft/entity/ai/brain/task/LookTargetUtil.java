package net.minecraft.entity.ai.brain.task;

import java.util.function.ToIntFunction;
import java.util.Comparator;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.EntityType;
import java.util.List;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.LivingEntity;

public class LookTargetUtil
{
    public static void lookAtAndWalkTowardsEachOther(final LivingEntity livingEntity1, final LivingEntity livingEntity2) {
        lookAtEachOther(livingEntity1, livingEntity2);
        walkTowardsEachOther(livingEntity1, livingEntity2);
    }
    
    public static boolean canSee(final Brain<?> brain, final LivingEntity livingEntity) {
        return brain.<List<LivingEntity>>getOptionalMemory(MemoryModuleType.g).filter(list -> list.contains(livingEntity)).isPresent();
    }
    
    public static boolean canSee(final Brain<?> brain, final MemoryModuleType<? extends LivingEntity> memoryModuleType, final EntityType<?> entityType) {
        return brain.getOptionalMemory(memoryModuleType).filter(livingEntity -> livingEntity.getType() == entityType).filter(LivingEntity::isAlive).filter(livingEntity -> canSee(brain, livingEntity)).isPresent();
    }
    
    public static void lookAtEachOther(final LivingEntity livingEntity1, final LivingEntity livingEntity2) {
        lookAt(livingEntity1, livingEntity2);
        lookAt(livingEntity2, livingEntity1);
    }
    
    public static void lookAt(final LivingEntity livingEntity1, final LivingEntity livingEntity2) {
        livingEntity1.getBrain().<EntityPosWrapper>putMemory((MemoryModuleType<EntityPosWrapper>)MemoryModuleType.l, new EntityPosWrapper(livingEntity2));
    }
    
    public static void walkTowardsEachOther(final LivingEntity livingEntity1, final LivingEntity livingEntity2) {
        final int integer3 = 2;
        walkTowards(livingEntity1, livingEntity2, 2);
        walkTowards(livingEntity2, livingEntity1, 2);
    }
    
    public static void walkTowards(final LivingEntity livingEntity1, final LivingEntity livingEntity2, final int integer) {
        final float float4 = (float)livingEntity1.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
        final EntityPosWrapper entityPosWrapper5 = new EntityPosWrapper(livingEntity2);
        final WalkTarget walkTarget6 = new WalkTarget(entityPosWrapper5, float4, integer);
        livingEntity1.getBrain().<LookTarget>putMemory(MemoryModuleType.l, entityPosWrapper5);
        livingEntity1.getBrain().<WalkTarget>putMemory(MemoryModuleType.k, walkTarget6);
    }
    
    public static void give(final LivingEntity livingEntity1, final ItemStack itemStack, final LivingEntity livingEntity3) {
        final double double4 = livingEntity1.y - 0.30000001192092896 + livingEntity1.getStandingEyeHeight();
        final ItemEntity itemEntity6 = new ItemEntity(livingEntity1.world, livingEntity1.x, double4, livingEntity1.z, itemStack);
        final BlockPos blockPos7 = new BlockPos(livingEntity3);
        final BlockPos blockPos8 = new BlockPos(livingEntity1);
        final float float9 = 0.3f;
        Vec3d vec3d10 = new Vec3d(blockPos7.subtract(blockPos8));
        vec3d10 = vec3d10.normalize().multiply(0.30000001192092896);
        itemEntity6.setVelocity(vec3d10);
        itemEntity6.setToDefaultPickupDelay();
        livingEntity1.world.spawnEntity(itemEntity6);
    }
    
    public static ChunkSectionPos getPosClosestToOccupiedPointOfInterest(final ServerWorld world, final ChunkSectionPos center, final int radius) {
        final int integer4 = world.getOccupiedPointOfInterestDistance(center);
        return ChunkSectionPos.stream(center, radius).filter(chunkSectionPos -> world.getOccupiedPointOfInterestDistance(chunkSectionPos) < integer4).min(Comparator.comparingInt(world::getOccupiedPointOfInterestDistance)).orElse(center);
    }
}
