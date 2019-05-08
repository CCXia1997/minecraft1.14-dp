package net.minecraft.entity.passive;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import javax.annotation.Nullable;
import net.minecraft.sound.SoundEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.block.BlockState;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.ai.RangedAttacker;

public class SnowmanEntity extends GolemEntity implements RangedAttacker
{
    private static final TrackedData<Byte> SNOWMAN_FLAGS;
    
    public SnowmanEntity(final EntityType<? extends SnowmanEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.25, 20, 10.0f));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0, 1.0000001E-5f));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new FollowTargetGoal<>(this, MobEntity.class, 10, true, false, livingEntity -> livingEntity instanceof Monster));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(4.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Byte>startTracking(SnowmanEntity.SNOWMAN_FLAGS, (Byte)16);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("Pumpkin", this.hasPumpkin());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("Pumpkin")) {
            this.setHasPumpkin(tag.getBoolean("Pumpkin"));
        }
    }
    
    @Override
    public void updateState() {
        super.updateState();
        if (!this.world.isClient) {
            int integer1 = MathHelper.floor(this.x);
            int integer2 = MathHelper.floor(this.y);
            int integer3 = MathHelper.floor(this.z);
            if (this.isTouchingWater()) {
                this.damage(DamageSource.DROWN, 1.0f);
            }
            if (this.world.getBiome(new BlockPos(integer1, 0, integer3)).getTemperature(new BlockPos(integer1, integer2, integer3)) > 1.0f) {
                this.damage(DamageSource.ON_FIRE, 1.0f);
            }
            if (!this.world.getGameRules().getBoolean("mobGriefing")) {
                return;
            }
            final BlockState blockState4 = Blocks.cA.getDefaultState();
            for (int integer4 = 0; integer4 < 4; ++integer4) {
                integer1 = MathHelper.floor(this.x + (integer4 % 2 * 2 - 1) * 0.25f);
                integer2 = MathHelper.floor(this.y);
                integer3 = MathHelper.floor(this.z + (integer4 / 2 % 2 * 2 - 1) * 0.25f);
                final BlockPos blockPos6 = new BlockPos(integer1, integer2, integer3);
                if (this.world.getBlockState(blockPos6).isAir() && this.world.getBiome(blockPos6).getTemperature(blockPos6) < 0.8f && blockState4.canPlaceAt(this.world, blockPos6)) {
                    this.world.setBlockState(blockPos6, blockState4);
                }
            }
        }
    }
    
    @Override
    public void attack(final LivingEntity target, final float float2) {
        final SnowballEntity snowballEntity3 = new SnowballEntity(this.world, this);
        final double double4 = target.y + target.getStandingEyeHeight() - 1.100000023841858;
        final double double5 = target.x - this.x;
        final double double6 = double4 - snowballEntity3.y;
        final double double7 = target.z - this.z;
        final float float3 = MathHelper.sqrt(double5 * double5 + double7 * double7) * 0.2f;
        snowballEntity3.setVelocity(double5, double6 + float3, double7, 1.6f, 12.0f);
        this.playSound(SoundEvents.kY, 1.0f, 1.0f / (this.getRand().nextFloat() * 0.4f + 0.8f));
        this.world.spawnEntity(snowballEntity3);
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 1.7f;
    }
    
    @Override
    protected boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (itemStack3.getItem() == Items.lW && this.hasPumpkin() && !this.world.isClient) {
            this.setHasPumpkin(false);
            itemStack3.<PlayerEntity>applyDamage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
        }
        return super.interactMob(player, hand);
    }
    
    public boolean hasPumpkin() {
        return (this.dataTracker.<Byte>get(SnowmanEntity.SNOWMAN_FLAGS) & 0x10) != 0x0;
    }
    
    public void setHasPumpkin(final boolean boolean1) {
        final byte byte2 = this.dataTracker.<Byte>get(SnowmanEntity.SNOWMAN_FLAGS);
        if (boolean1) {
            this.dataTracker.<Byte>set(SnowmanEntity.SNOWMAN_FLAGS, (byte)(byte2 | 0x10));
        }
        else {
            this.dataTracker.<Byte>set(SnowmanEntity.SNOWMAN_FLAGS, (byte)(byte2 & 0xFFFFFFEF));
        }
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.kV;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.kX;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.kW;
    }
    
    static {
        SNOWMAN_FLAGS = DataTracker.<Byte>registerData(SnowmanEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
}
