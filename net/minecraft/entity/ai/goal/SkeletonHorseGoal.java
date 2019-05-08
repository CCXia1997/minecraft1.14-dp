package net.minecraft.entity.ai.goal;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.EntityData;
import net.minecraft.world.IWorld;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.mob.SkeletonHorseEntity;

public class SkeletonHorseGoal extends Goal
{
    private final SkeletonHorseEntity owner;
    
    public SkeletonHorseGoal(final SkeletonHorseEntity skeletonHorseEntity) {
        this.owner = skeletonHorseEntity;
    }
    
    @Override
    public boolean canStart() {
        return this.owner.world.isPlayerInRange(this.owner.x, this.owner.y, this.owner.z, 10.0);
    }
    
    @Override
    public void tick() {
        final LocalDifficulty localDifficulty1 = this.owner.world.getLocalDifficulty(new BlockPos(this.owner));
        this.owner.r(false);
        this.owner.setTame(true);
        this.owner.setBreedingAge(0);
        ((ServerWorld)this.owner.world).addLightning(new LightningEntity(this.owner.world, this.owner.x, this.owner.y, this.owner.z, true));
        final SkeletonEntity skeletonEntity2 = this.a(localDifficulty1, this.owner);
        skeletonEntity2.startRiding(this.owner);
        for (int integer3 = 0; integer3 < 3; ++integer3) {
            final HorseBaseEntity horseBaseEntity4 = this.a(localDifficulty1);
            final SkeletonEntity skeletonEntity3 = this.a(localDifficulty1, horseBaseEntity4);
            skeletonEntity3.startRiding(horseBaseEntity4);
            horseBaseEntity4.addVelocity(this.owner.getRand().nextGaussian() * 0.5, 0.0, this.owner.getRand().nextGaussian() * 0.5);
        }
    }
    
    private HorseBaseEntity a(final LocalDifficulty localDifficulty) {
        final SkeletonHorseEntity skeletonHorseEntity2 = EntityType.SKELETON_HORSE.create(this.owner.world);
        skeletonHorseEntity2.initialize(this.owner.world, localDifficulty, SpawnType.k, null, null);
        skeletonHorseEntity2.setPosition(this.owner.x, this.owner.y, this.owner.z);
        skeletonHorseEntity2.T = 60;
        skeletonHorseEntity2.setPersistent();
        skeletonHorseEntity2.setTame(true);
        skeletonHorseEntity2.setBreedingAge(0);
        skeletonHorseEntity2.world.spawnEntity(skeletonHorseEntity2);
        return skeletonHorseEntity2;
    }
    
    private SkeletonEntity a(final LocalDifficulty localDifficulty, final HorseBaseEntity horseBaseEntity) {
        final SkeletonEntity skeletonEntity3 = EntityType.SKELETON.create(horseBaseEntity.world);
        skeletonEntity3.initialize(horseBaseEntity.world, localDifficulty, SpawnType.k, null, null);
        skeletonEntity3.setPosition(horseBaseEntity.x, horseBaseEntity.y, horseBaseEntity.z);
        skeletonEntity3.T = 60;
        skeletonEntity3.setPersistent();
        if (skeletonEntity3.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            skeletonEntity3.setEquippedStack(EquipmentSlot.HEAD, new ItemStack(Items.jZ));
        }
        skeletonEntity3.setEquippedStack(EquipmentSlot.HAND_MAIN, EnchantmentHelper.enchant(skeletonEntity3.getRand(), skeletonEntity3.getMainHandStack(), (int)(5.0f + localDifficulty.getClampedLocalDifficulty() * skeletonEntity3.getRand().nextInt(18)), false));
        skeletonEntity3.setEquippedStack(EquipmentSlot.HEAD, EnchantmentHelper.enchant(skeletonEntity3.getRand(), skeletonEntity3.getEquippedStack(EquipmentSlot.HEAD), (int)(5.0f + localDifficulty.getClampedLocalDifficulty() * skeletonEntity3.getRand().nextInt(18)), false));
        skeletonEntity3.world.spawnEntity(skeletonEntity3);
        return skeletonEntity3;
    }
}
