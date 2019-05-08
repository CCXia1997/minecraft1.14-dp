package net.minecraft.entity.mob;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.block.Blocks;
import java.time.temporal.TemporalField;
import java.time.temporal.ChronoField;
import java.time.LocalDate;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.EntityGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.AvoidSunlightGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.RangedAttacker;

public abstract class AbstractSkeletonEntity extends HostileEntity implements RangedAttacker
{
    private final BowAttackGoal<AbstractSkeletonEntity> bowAttackGoal;
    private final MeleeAttackGoal meleeAttackGoal;
    
    protected AbstractSkeletonEntity(final EntityType<? extends AbstractSkeletonEntity> type, final World world) {
        super(type, world);
        this.bowAttackGoal = new BowAttackGoal<AbstractSkeletonEntity>(this, 1.0, 20, 15.0f);
        this.meleeAttackGoal = new MeleeAttackGoal((MobEntityWithAi)this, 1.2, false) {
            @Override
            public void stop() {
                super.stop();
                AbstractSkeletonEntity.this.setAttacking(false);
            }
            
            @Override
            public void start() {
                super.start();
                AbstractSkeletonEntity.this.setAttacking(true);
            }
        };
        this.updateAttackType();
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new AvoidSunlightGoal(this));
        this.goalSelector.add(3, new EscapeSunlightGoal(this, 1.0));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, WolfEntity.class, 6.0f, 1.0, 1.2));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }
    
    abstract SoundEvent getStepSound();
    
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }
    
    @Override
    public void updateState() {
        boolean boolean1 = this.isInDaylight();
        if (boolean1) {
            final ItemStack itemStack2 = this.getEquippedStack(EquipmentSlot.HEAD);
            if (!itemStack2.isEmpty()) {
                if (itemStack2.hasDurability()) {
                    itemStack2.setDamage(itemStack2.getDamage() + this.random.nextInt(2));
                    if (itemStack2.getDamage() >= itemStack2.getDurability()) {
                        this.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                        this.setEquippedStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }
                boolean1 = false;
            }
            if (boolean1) {
                this.setOnFireFor(8);
            }
        }
        super.updateState();
    }
    
    @Override
    public void tickRiding() {
        super.tickRiding();
        if (this.getVehicle() instanceof MobEntityWithAi) {
            final MobEntityWithAi mobEntityWithAi1 = (MobEntityWithAi)this.getVehicle();
            this.aK = mobEntityWithAi1.aK;
        }
    }
    
    @Override
    protected void initEquipment(final LocalDifficulty localDifficulty) {
        super.initEquipment(localDifficulty);
        this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.jf));
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        this.initEquipment(localDifficulty);
        this.updateEnchantments(localDifficulty);
        this.updateAttackType();
        this.setCanPickUpLoot(this.random.nextFloat() < 0.55f * localDifficulty.getClampedLocalDifficulty());
        if (this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            final LocalDate localDate6 = LocalDate.now();
            final int integer7 = localDate6.get(ChronoField.DAY_OF_MONTH);
            final int integer8 = localDate6.get(ChronoField.MONTH_OF_YEAR);
            if (integer8 == 10 && integer7 == 31 && this.random.nextFloat() < 0.25f) {
                this.setEquippedStack(EquipmentSlot.HEAD, new ItemStack((this.random.nextFloat() < 0.1f) ? Blocks.cO : Blocks.cN));
                this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0f;
            }
        }
        return entityData;
    }
    
    public void updateAttackType() {
        if (this.world == null || this.world.isClient) {
            return;
        }
        this.goalSelector.remove(this.meleeAttackGoal);
        this.goalSelector.remove(this.bowAttackGoal);
        final ItemStack itemStack1 = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.jf));
        if (itemStack1.getItem() == Items.jf) {
            int integer2 = 20;
            if (this.world.getDifficulty() != Difficulty.HARD) {
                integer2 = 40;
            }
            this.bowAttackGoal.setAttackInterval(integer2);
            this.goalSelector.add(4, this.bowAttackGoal);
        }
        else {
            this.goalSelector.add(4, this.meleeAttackGoal);
        }
    }
    
    @Override
    public void attack(final LivingEntity target, final float float2) {
        final ItemStack itemStack3 = this.getArrowType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.jf)));
        final ProjectileEntity projectileEntity4 = this.createArrowProjectile(itemStack3, float2);
        final double double5 = target.x - this.x;
        final double double6 = target.getBoundingBox().minY + target.getHeight() / 3.0f - projectileEntity4.y;
        final double double7 = target.z - this.z;
        final double double8 = MathHelper.sqrt(double5 * double5 + double7 * double7);
        projectileEntity4.setVelocity(double5, double6 + double8 * 0.20000000298023224, double7, 1.6f, (float)(14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ky, 1.0f, 1.0f / (this.getRand().nextFloat() * 0.4f + 0.8f));
        this.world.spawnEntity(projectileEntity4);
    }
    
    protected ProjectileEntity createArrowProjectile(final ItemStack arrow, final float float2) {
        return ProjectileUtil.createArrowProjectile(this, arrow, float2);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.updateAttackType();
    }
    
    @Override
    public void setEquippedStack(final EquipmentSlot slot, final ItemStack itemStack) {
        super.setEquippedStack(slot, itemStack);
        if (!this.world.isClient) {
            this.updateAttackType();
        }
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 1.74f;
    }
    
    @Override
    public double getHeightOffset() {
        return -0.6;
    }
}
