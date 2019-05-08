package net.minecraft.entity.mob;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;

public class BlazeEntity extends HostileEntity
{
    private float b;
    private int c;
    private static final TrackedData<Byte> BLAZE_FLAGS;
    
    public BlazeEntity(final EntityType<? extends BlazeEntity> type, final World world) {
        super(type, world);
        this.b = 0.5f;
        this.setPathNodeTypeWeight(PathNodeType.g, -1.0f);
        this.setPathNodeTypeWeight(PathNodeType.f, 8.0f);
        this.setPathNodeTypeWeight(PathNodeType.j, 0.0f);
        this.setPathNodeTypeWeight(PathNodeType.k, 0.0f);
        this.experiencePoints = 10;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(4, new ShootFireballGoal(this));
        this.goalSelector.add(5, new GoToWalkTargetGoal(this, 1.0));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0, 0.0f));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(48.0);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Byte>startTracking(BlazeEntity.BLAZE_FLAGS, (Byte)0);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.aa;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.ad;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ac;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getLightmapCoordinates() {
        return 15728880;
    }
    
    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }
    
    @Override
    public void updateState() {
        if (!this.onGround && this.getVelocity().y < 0.0) {
            this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
        }
        if (this.world.isClient) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.world.playSound(this.x + 0.5, this.y + 0.5, this.z + 0.5, SoundEvents.ab, this.getSoundCategory(), 1.0f + this.random.nextFloat(), this.random.nextFloat() * 0.7f + 0.3f, false);
            }
            for (int integer1 = 0; integer1 < 2; ++integer1) {
                this.world.addParticle(ParticleTypes.J, this.x + (this.random.nextDouble() - 0.5) * this.getWidth(), this.y + this.random.nextDouble() * this.getHeight(), this.z + (this.random.nextDouble() - 0.5) * this.getWidth(), 0.0, 0.0, 0.0);
            }
        }
        super.updateState();
    }
    
    @Override
    protected void mobTick() {
        if (this.isTouchingWater()) {
            this.damage(DamageSource.DROWN, 1.0f);
        }
        --this.c;
        if (this.c <= 0) {
            this.c = 100;
            this.b = 0.5f + (float)this.random.nextGaussian() * 3.0f;
        }
        final LivingEntity livingEntity1 = this.getTarget();
        if (livingEntity1 != null && livingEntity1.y + livingEntity1.getStandingEyeHeight() > this.y + this.getStandingEyeHeight() + this.b) {
            final Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(this.getVelocity().add(0.0, (0.30000001192092896 - vec3d2.y) * 0.30000001192092896, 0.0));
            this.velocityDirty = true;
        }
        super.mobTick();
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
    }
    
    @Override
    public boolean isOnFire() {
        return this.isFireActive();
    }
    
    public boolean isFireActive() {
        return (this.dataTracker.<Byte>get(BlazeEntity.BLAZE_FLAGS) & 0x1) != 0x0;
    }
    
    public void setFireActive(final boolean boolean1) {
        byte byte2 = this.dataTracker.<Byte>get(BlazeEntity.BLAZE_FLAGS);
        if (boolean1) {
            byte2 |= 0x1;
        }
        else {
            byte2 &= 0xFFFFFFFE;
        }
        this.dataTracker.<Byte>set(BlazeEntity.BLAZE_FLAGS, byte2);
    }
    
    @Override
    protected boolean checkLightLevelForSpawn() {
        return true;
    }
    
    static {
        BLAZE_FLAGS = DataTracker.<Byte>registerData(BlazeEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
    
    static class ShootFireballGoal extends Goal
    {
        private final BlazeEntity a;
        private int b;
        private int c;
        
        public ShootFireballGoal(final BlazeEntity blazeEntity) {
            this.a = blazeEntity;
            this.setControls(EnumSet.<Control>of(Control.a, Control.b));
        }
        
        @Override
        public boolean canStart() {
            final LivingEntity livingEntity1 = this.a.getTarget();
            return livingEntity1 != null && livingEntity1.isAlive();
        }
        
        @Override
        public void start() {
            this.b = 0;
        }
        
        @Override
        public void stop() {
            this.a.setFireActive(false);
        }
        
        @Override
        public void tick() {
            --this.c;
            final LivingEntity livingEntity1 = this.a.getTarget();
            final double double2 = this.a.squaredDistanceTo(livingEntity1);
            if (double2 < 4.0) {
                if (this.c <= 0) {
                    this.c = 20;
                    this.a.tryAttack(livingEntity1);
                }
                this.a.getMoveControl().moveTo(livingEntity1.x, livingEntity1.y, livingEntity1.z, 1.0);
            }
            else if (double2 < this.g() * this.g()) {
                final double double3 = livingEntity1.x - this.a.x;
                final double double4 = livingEntity1.getBoundingBox().minY + livingEntity1.getHeight() / 2.0f - (this.a.y + this.a.getHeight() / 2.0f);
                final double double5 = livingEntity1.z - this.a.z;
                if (this.c <= 0) {
                    ++this.b;
                    if (this.b == 1) {
                        this.c = 60;
                        this.a.setFireActive(true);
                    }
                    else if (this.b <= 4) {
                        this.c = 6;
                    }
                    else {
                        this.c = 100;
                        this.b = 0;
                        this.a.setFireActive(false);
                    }
                    if (this.b > 1) {
                        final float float10 = MathHelper.sqrt(MathHelper.sqrt(double2)) * 0.5f;
                        this.a.world.playLevelEvent(null, 1018, new BlockPos((int)this.a.x, (int)this.a.y, (int)this.a.z), 0);
                        for (int integer11 = 0; integer11 < 1; ++integer11) {
                            final SmallFireballEntity smallFireballEntity12 = new SmallFireballEntity(this.a.world, this.a, double3 + this.a.getRand().nextGaussian() * float10, double4, double5 + this.a.getRand().nextGaussian() * float10);
                            smallFireballEntity12.y = this.a.y + this.a.getHeight() / 2.0f + 0.5;
                            this.a.world.spawnEntity(smallFireballEntity12);
                        }
                    }
                }
                this.a.getLookControl().lookAt(livingEntity1, 10.0f, 10.0f);
            }
            else {
                this.a.getNavigation().stop();
                this.a.getMoveControl().moveTo(livingEntity1.x, livingEntity1.y, livingEntity1.z, 1.0);
            }
            super.tick();
        }
        
        private double g() {
            final EntityAttributeInstance entityAttributeInstance1 = this.a.getAttributeInstance(EntityAttributes.FOLLOW_RANGE);
            return (entityAttributeInstance1 == null) ? 16.0 : entityAttributeInstance1.getValue();
        }
    }
}
