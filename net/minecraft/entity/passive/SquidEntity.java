package net.minecraft.entity.passive;

import net.minecraft.fluid.FluidState;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.LivingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.WaterCreatureEntity;

public class SquidEntity extends WaterCreatureEntity
{
    public float b;
    public float c;
    public float d;
    public float bz;
    public float bA;
    public float bB;
    public float bC;
    public float bD;
    private float constantVelocityRate;
    private float bF;
    private float bG;
    private float constantVelocityX;
    private float constantVelocityY;
    private float constantVelocityZ;
    
    public SquidEntity(final EntityType<? extends SquidEntity> type, final World world) {
        super(type, world);
        this.random.setSeed(this.getEntityId());
        this.bF = 1.0f / (this.random.nextFloat() + 1.0f) * 0.2f;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimInOneDirectionGoal(this));
        this.goalSelector.add(1, new EscapeAttackerGoal());
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return entitySize.height * 0.5f;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.li;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.lk;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.lj;
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    public void updateState() {
        super.updateState();
        this.c = this.b;
        this.bz = this.d;
        this.bB = this.bA;
        this.bD = this.bC;
        this.bA += this.bF;
        if (this.bA > 6.283185307179586) {
            if (this.world.isClient) {
                this.bA = 6.2831855f;
            }
            else {
                this.bA -= (float)6.283185307179586;
                if (this.random.nextInt(10) == 0) {
                    this.bF = 1.0f / (this.random.nextFloat() + 1.0f) * 0.2f;
                }
                this.world.sendEntityStatus(this, (byte)19);
            }
        }
        if (this.isInsideWaterOrBubbleColumn()) {
            if (this.bA < 3.1415927f) {
                final float float1 = this.bA / 3.1415927f;
                this.bC = MathHelper.sin(float1 * float1 * 3.1415927f) * 3.1415927f * 0.25f;
                if (float1 > 0.75) {
                    this.constantVelocityRate = 1.0f;
                    this.bG = 1.0f;
                }
                else {
                    this.bG *= 0.8f;
                }
            }
            else {
                this.bC = 0.0f;
                this.constantVelocityRate *= 0.9f;
                this.bG *= 0.99f;
            }
            if (!this.world.isClient) {
                this.setVelocity(this.constantVelocityX * this.constantVelocityRate, this.constantVelocityY * this.constantVelocityRate, this.constantVelocityZ * this.constantVelocityRate);
            }
            final Vec3d vec3d1 = this.getVelocity();
            final float float2 = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d1));
            this.aK += (-(float)MathHelper.atan2(vec3d1.x, vec3d1.z) * 57.295776f - this.aK) * 0.1f;
            this.yaw = this.aK;
            this.d += (float)(3.141592653589793 * this.bG * 1.5);
            this.b += (-(float)MathHelper.atan2(float2, vec3d1.y) * 57.295776f - this.b) * 0.1f;
        }
        else {
            this.bC = MathHelper.abs(MathHelper.sin(this.bA)) * 3.1415927f * 0.25f;
            if (!this.world.isClient) {
                double double1 = this.getVelocity().y;
                if (this.hasStatusEffect(StatusEffects.y)) {
                    double1 = 0.05 * (this.getStatusEffect(StatusEffects.y).getAmplifier() + 1);
                }
                else if (!this.isUnaffectedByGravity()) {
                    double1 -= 0.08;
                }
                this.setVelocity(0.0, double1 * 0.9800000190734863, 0.0);
            }
            this.b += (float)((-90.0f - this.b) * 0.02);
        }
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (super.damage(source, amount) && this.getAttacker() != null) {
            this.squirt();
            return true;
        }
        return false;
    }
    
    private Vec3d f(final Vec3d vec3d) {
        Vec3d vec3d2 = vec3d.rotateX(this.c * 0.017453292f);
        vec3d2 = vec3d2.rotateY(-this.aL * 0.017453292f);
        return vec3d2;
    }
    
    private void squirt() {
        this.playSound(SoundEvents.ll, this.getSoundVolume(), this.getSoundPitch());
        final Vec3d vec3d1 = this.f(new Vec3d(0.0, -1.0, 0.0)).add(this.x, this.y, this.z);
        for (int integer2 = 0; integer2 < 30; ++integer2) {
            final Vec3d vec3d2 = this.f(new Vec3d(this.random.nextFloat() * 0.6 - 0.3, -1.0, this.random.nextFloat() * 0.6 - 0.3));
            final Vec3d vec3d3 = vec3d2.multiply(0.3 + this.random.nextFloat() * 2.0f);
            ((ServerWorld)this.world).<DefaultParticleType>spawnParticles(ParticleTypes.T, vec3d1.x, vec3d1.y + 0.5, vec3d1.z, 0, vec3d3.x, vec3d3.y, vec3d3.z, 0.10000000149011612);
        }
    }
    
    @Override
    public void travel(final Vec3d movementInput) {
        this.move(MovementType.a, this.getVelocity());
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        return this.y > 45.0 && this.y < iWorld.getSeaLevel();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 19) {
            this.bA = 0.0f;
        }
        else {
            super.handleStatus(status);
        }
    }
    
    public void setConstantVelocity(final float float1, final float float2, final float float3) {
        this.constantVelocityX = float1;
        this.constantVelocityY = float2;
        this.constantVelocityZ = float3;
    }
    
    public boolean hasConstantVelocity() {
        return this.constantVelocityX != 0.0f || this.constantVelocityY != 0.0f || this.constantVelocityZ != 0.0f;
    }
    
    class SwimInOneDirectionGoal extends Goal
    {
        private final SquidEntity owner;
        
        public SwimInOneDirectionGoal(final SquidEntity squidEntity2) {
            this.owner = squidEntity2;
        }
        
        @Override
        public boolean canStart() {
            return true;
        }
        
        @Override
        public void tick() {
            final int integer1 = this.owner.getDespawnCounter();
            if (integer1 > 100) {
                this.owner.setConstantVelocity(0.0f, 0.0f, 0.0f);
            }
            else if (this.owner.getRand().nextInt(50) == 0 || !this.owner.insideWater || !this.owner.hasConstantVelocity()) {
                final float float2 = this.owner.getRand().nextFloat() * 6.2831855f;
                final float float3 = MathHelper.cos(float2) * 0.2f;
                final float float4 = -0.1f + this.owner.getRand().nextFloat() * 0.2f;
                final float float5 = MathHelper.sin(float2) * 0.2f;
                this.owner.setConstantVelocity(float3, float4, float5);
            }
        }
    }
    
    class EscapeAttackerGoal extends Goal
    {
        private int timer;
        
        private EscapeAttackerGoal() {
        }
        
        @Override
        public boolean canStart() {
            final LivingEntity livingEntity1 = SquidEntity.this.getAttacker();
            return SquidEntity.this.isInsideWater() && livingEntity1 != null && SquidEntity.this.squaredDistanceTo(livingEntity1) < 100.0;
        }
        
        @Override
        public void start() {
            this.timer = 0;
        }
        
        @Override
        public void tick() {
            ++this.timer;
            final LivingEntity livingEntity1 = SquidEntity.this.getAttacker();
            if (livingEntity1 == null) {
                return;
            }
            Vec3d vec3d2 = new Vec3d(SquidEntity.this.x - livingEntity1.x, SquidEntity.this.y - livingEntity1.y, SquidEntity.this.z - livingEntity1.z);
            final BlockState blockState3 = SquidEntity.this.world.getBlockState(new BlockPos(SquidEntity.this.x + vec3d2.x, SquidEntity.this.y + vec3d2.y, SquidEntity.this.z + vec3d2.z));
            final FluidState fluidState4 = SquidEntity.this.world.getFluidState(new BlockPos(SquidEntity.this.x + vec3d2.x, SquidEntity.this.y + vec3d2.y, SquidEntity.this.z + vec3d2.z));
            if (fluidState4.matches(FluidTags.a) || blockState3.isAir()) {
                final double double5 = vec3d2.length();
                if (double5 > 0.0) {
                    vec3d2.normalize();
                    float float7 = 3.0f;
                    if (double5 > 5.0) {
                        float7 -= (float)((double5 - 5.0) / 5.0);
                    }
                    if (float7 > 0.0f) {
                        vec3d2 = vec3d2.multiply(float7);
                    }
                }
                if (blockState3.isAir()) {
                    vec3d2 = vec3d2.subtract(0.0, vec3d2.y, 0.0);
                }
                SquidEntity.this.setConstantVelocity((float)vec3d2.x / 20.0f, (float)vec3d2.y / 20.0f, (float)vec3d2.z / 20.0f);
            }
            if (this.timer % 10 == 5) {
                SquidEntity.this.world.addParticle(ParticleTypes.e, SquidEntity.this.x, SquidEntity.this.y, SquidEntity.this.z, 0.0, 0.0, 0.0);
            }
        }
    }
}
