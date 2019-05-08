package net.minecraft.entity.mob;

import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.world.BlockView;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.ViewableWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import java.util.List;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.tag.EntityTags;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;
import net.minecraft.entity.raid.RaiderEntity;

public class RavagerEntity extends RaiderEntity
{
    private static final Predicate<Entity> IS_NOT_RAVAGER;
    private int attackTick;
    private int stunTick;
    private int roarTick;
    
    public RavagerEntity(final EntityType<? extends RavagerEntity> type, final World world) {
        super(type, world);
        this.stepHeight = 1.0f;
        this.experiencePoints = 20;
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(4, new a());
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.4));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[] { RaiderEntity.class }).setGroupRevenge(new Class[0]));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(4, new FollowTargetGoal<>(this, AbstractTraderEntity.class, true));
        this.targetSelector.add(4, new FollowTargetGoal<>(this, IronGolemEntity.class, true));
    }
    
    @Override
    protected void F() {
        final boolean boolean1 = !(this.getPrimaryPassenger() instanceof MobEntity) || this.getPrimaryPassenger().getType().isTaggedWith(EntityTags.b);
        final boolean boolean2 = !(this.getVehicle() instanceof BoatEntity);
        this.goalSelector.setControlEnabled(Goal.Control.a, boolean1);
        this.goalSelector.setControlEnabled(Goal.Control.c, boolean1 && boolean2);
        this.goalSelector.setControlEnabled(Goal.Control.b, boolean1);
        this.goalSelector.setControlEnabled(Goal.Control.d, boolean1);
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(100.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(12.0);
        this.getAttributeInstance(EntityAttributes.ATTACK_KNOCKBACK).setBaseValue(1.5);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(32.0);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("AttackTick", this.attackTick);
        tag.putInt("StunTick", this.stunTick);
        tag.putInt("RoarTick", this.roarTick);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.attackTick = tag.getInt("AttackTick");
        this.stunTick = tag.getInt("StunTick");
        this.roarTick = tag.getInt("RoarTick");
    }
    
    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.fe;
    }
    
    @Override
    protected EntityNavigation createNavigation(final World world) {
        return new b(this, world);
    }
    
    @Override
    public int dA() {
        return 45;
    }
    
    @Override
    public double getMountedHeightOffset() {
        return 2.1;
    }
    
    @Override
    public boolean canBeControlledByRider() {
        return !this.isAiDisabled() && this.getPrimaryPassenger() instanceof LivingEntity;
    }
    
    @Nullable
    @Override
    public Entity getPrimaryPassenger() {
        if (this.getPassengerList().isEmpty()) {
            return null;
        }
        return this.getPassengerList().get(0);
    }
    
    @Override
    public void updateState() {
        super.updateState();
        if (!this.isAlive()) {
            return;
        }
        if (this.cannotMove()) {
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.0);
        }
        else {
            final double double1 = (this.getTarget() != null) ? 0.35 : 0.3;
            final double double2 = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(MathHelper.lerp(0.1, double2, double1));
        }
        if (this.horizontalCollision && this.world.getGameRules().getBoolean("mobGriefing")) {
            boolean boolean1 = false;
            final BoundingBox boundingBox2 = this.getBoundingBox().expand(0.2);
            for (final BlockPos blockPos4 : BlockPos.iterate(MathHelper.floor(boundingBox2.minX), MathHelper.floor(boundingBox2.minY), MathHelper.floor(boundingBox2.minZ), MathHelper.floor(boundingBox2.maxX), MathHelper.floor(boundingBox2.maxY), MathHelper.floor(boundingBox2.maxZ))) {
                final BlockState blockState5 = this.world.getBlockState(blockPos4);
                final Block block6 = blockState5.getBlock();
                if (block6 instanceof LeavesBlock) {
                    boolean1 = (this.world.breakBlock(blockPos4, true) || boolean1);
                }
            }
            if (!boolean1 && this.onGround) {
                this.jump();
            }
        }
        if (this.roarTick > 0) {
            --this.roarTick;
            if (this.roarTick == 10) {
                this.roar();
            }
        }
        if (this.attackTick > 0) {
            --this.attackTick;
        }
        if (this.stunTick > 0) {
            --this.stunTick;
            this.spawnStunnedParticles();
            if (this.stunTick == 0) {
                this.playSound(SoundEvents.fj, 1.0f, 1.0f);
                this.roarTick = 20;
            }
        }
    }
    
    private void spawnStunnedParticles() {
        if (this.random.nextInt(6) == 0) {
            final double double1 = this.x - this.getWidth() * Math.sin(this.aK * 0.017453292f) + (this.random.nextDouble() * 0.6 - 0.3);
            final double double2 = this.y + this.getHeight() - 0.3;
            final double double3 = this.z + this.getWidth() * Math.cos(this.aK * 0.017453292f) + (this.random.nextDouble() * 0.6 - 0.3);
            this.world.addParticle(ParticleTypes.u, double1, double2, double3, 0.4980392156862745, 0.5137254901960784, 0.5725490196078431);
        }
    }
    
    @Override
    protected boolean cannotMove() {
        return super.cannotMove() || this.attackTick > 0 || this.stunTick > 0 || this.roarTick > 0;
    }
    
    @Override
    public boolean canSee(final Entity entity) {
        return this.stunTick <= 0 && this.roarTick <= 0 && super.canSee(entity);
    }
    
    @Override
    protected void knockback(final LivingEntity target) {
        if (this.roarTick == 0) {
            if (this.random.nextDouble() < 0.5) {
                this.stunTick = 40;
                this.playSound(SoundEvents.fi, 1.0f, 1.0f);
                this.world.sendEntityStatus(this, (byte)39);
                target.pushAwayFrom(this);
            }
            else {
                this.knockBack(target);
            }
            target.velocityModified = true;
        }
    }
    
    private void roar() {
        if (this.isAlive()) {
            final List<Entity> list1 = this.world.<Entity>getEntities(LivingEntity.class, this.getBoundingBox().expand(4.0), RavagerEntity.IS_NOT_RAVAGER);
            for (final Entity entity3 : list1) {
                if (!(entity3 instanceof IllagerEntity)) {
                    entity3.damage(DamageSource.mob(this), 6.0f);
                }
                this.knockBack(entity3);
            }
            final Vec3d vec3d2 = this.getBoundingBox().getCenter();
            for (int integer3 = 0; integer3 < 40; ++integer3) {
                final double double4 = this.random.nextGaussian() * 0.2;
                final double double5 = this.random.nextGaussian() * 0.2;
                final double double6 = this.random.nextGaussian() * 0.2;
                this.world.addParticle(ParticleTypes.N, vec3d2.x, vec3d2.y, vec3d2.z, double4, double5, double6);
            }
        }
    }
    
    private void knockBack(final Entity entity) {
        final double double2 = entity.x - this.x;
        final double double3 = entity.z - this.z;
        final double double4 = double2 * double2 + double3 * double3;
        entity.addVelocity(double2 / double4 * 4.0, 0.2, double3 / double4 * 4.0);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 4) {
            this.attackTick = 10;
            this.playSound(SoundEvents.fd, 1.0f, 1.0f);
        }
        else if (status == 39) {
            this.stunTick = 40;
        }
        super.handleStatus(status);
    }
    
    @Environment(EnvType.CLIENT)
    public int getAttackTick() {
        return this.attackTick;
    }
    
    @Environment(EnvType.CLIENT)
    public int getStunTick() {
        return this.stunTick;
    }
    
    @Environment(EnvType.CLIENT)
    public int getRoarTick() {
        return this.roarTick;
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        this.attackTick = 10;
        this.world.sendEntityStatus(this, (byte)4);
        this.playSound(SoundEvents.fd, 1.0f, 1.0f);
        return super.tryAttack(entity);
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.fc;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.fg;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ff;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(SoundEvents.fh, 0.15f, 1.0f);
    }
    
    @Override
    public boolean canSpawn(final ViewableWorld world) {
        return !world.intersectsFluid(this.getBoundingBox());
    }
    
    @Override
    public void addBonusForWave(final int wave, final boolean boolean2) {
    }
    
    @Override
    public boolean canLead() {
        return false;
    }
    
    static {
        IS_NOT_RAVAGER = (entity -> entity.isAlive() && !(entity instanceof RavagerEntity));
    }
    
    class a extends MeleeAttackGoal
    {
        public a() {
            super(RavagerEntity.this, 1.0, true);
        }
        
        @Override
        protected double getSquaredMaxAttackDistance(final LivingEntity entity) {
            final float float2 = RavagerEntity.this.getWidth() - 0.1f;
            return float2 * 2.0f * (float2 * 2.0f) + entity.getWidth();
        }
    }
    
    static class b extends MobNavigation
    {
        public b(final MobEntity mobEntity, final World world) {
            super(mobEntity, world);
        }
        
        @Override
        protected PathNodeNavigator createPathNodeNavigator(final int integer) {
            this.nodeMaker = new c();
            return new PathNodeNavigator(this.nodeMaker, integer);
        }
    }
    
    static class c extends LandPathNodeMaker
    {
        private c() {
        }
        
        @Override
        protected PathNodeType a(final BlockView blockView, final boolean boolean2, final boolean boolean3, final BlockPos blockPos, final PathNodeType pathNodeType) {
            if (pathNodeType == PathNodeType.t) {
                return PathNodeType.b;
            }
            return super.a(blockView, boolean2, boolean3, blockPos, pathNodeType);
        }
    }
}
