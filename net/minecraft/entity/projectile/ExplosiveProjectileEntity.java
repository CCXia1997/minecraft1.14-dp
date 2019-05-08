package net.minecraft.entity.projectile;

import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.world.RayTraceContext;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;

public abstract class ExplosiveProjectileEntity extends Entity
{
    public LivingEntity owner;
    private int life;
    private int ticks;
    public double posX;
    public double posY;
    public double posZ;
    
    protected ExplosiveProjectileEntity(final EntityType<? extends ExplosiveProjectileEntity> type, final World world) {
        super(type, world);
    }
    
    public ExplosiveProjectileEntity(final EntityType<? extends ExplosiveProjectileEntity> world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12, final World world14) {
        this(world, world14);
        this.setPositionAndAngles(double2, double4, double6, this.yaw, this.pitch);
        this.setPosition(double2, double4, double6);
        final double double13 = MathHelper.sqrt(double8 * double8 + double10 * double10 + double12 * double12);
        this.posX = double8 / double13 * 0.1;
        this.posY = double10 / double13 * 0.1;
        this.posZ = double12 / double13 * 0.1;
    }
    
    public ExplosiveProjectileEntity(final EntityType<? extends ExplosiveProjectileEntity> world, final LivingEntity owner, double double3, double double5, double double7, final World world9) {
        this(world, world9);
        this.owner = owner;
        this.setPositionAndAngles(owner.x, owner.y, owner.z, owner.yaw, owner.pitch);
        this.setPosition(this.x, this.y, this.z);
        this.setVelocity(Vec3d.ZERO);
        double3 += this.random.nextGaussian() * 0.4;
        double5 += this.random.nextGaussian() * 0.4;
        double7 += this.random.nextGaussian() * 0.4;
        final double double8 = MathHelper.sqrt(double3 * double3 + double5 * double5 + double7 * double7);
        this.posX = double3 / double8 * 0.1;
        this.posY = double5 / double8 * 0.1;
        this.posZ = double7 / double8 * 0.1;
    }
    
    @Override
    protected void initDataTracker() {
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtDistance(final double distance) {
        double double3 = this.getBoundingBox().averageDimension() * 4.0;
        if (Double.isNaN(double3)) {
            double3 = 4.0;
        }
        double3 *= 64.0;
        return distance < double3 * double3;
    }
    
    @Override
    public void tick() {
        if (!this.world.isClient && ((this.owner != null && this.owner.removed) || !this.world.isBlockLoaded(new BlockPos(this)))) {
            this.remove();
            return;
        }
        super.tick();
        if (this.isBurning()) {
            this.setOnFireFor(1);
        }
        ++this.ticks;
        final HitResult hitResult1 = ProjectileUtil.getCollision(this, true, this.ticks >= 25, this.owner, RayTraceContext.ShapeType.a);
        if (hitResult1.getType() != HitResult.Type.NONE) {
            this.onCollision(hitResult1);
        }
        final Vec3d vec3d2 = this.getVelocity();
        this.x += vec3d2.x;
        this.y += vec3d2.y;
        this.z += vec3d2.z;
        ProjectileUtil.a(this, 0.2f);
        float float3 = this.getDrag();
        if (this.isInsideWater()) {
            for (int integer4 = 0; integer4 < 4; ++integer4) {
                final float float4 = 0.25f;
                this.world.addParticle(ParticleTypes.e, this.x - vec3d2.x * 0.25, this.y - vec3d2.y * 0.25, this.z - vec3d2.z * 0.25, vec3d2.x, vec3d2.y, vec3d2.z);
            }
            float3 = 0.8f;
        }
        this.setVelocity(vec3d2.add(this.posX, this.posY, this.posZ).multiply(float3));
        this.world.addParticle(this.getParticleType(), this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
        this.setPosition(this.x, this.y, this.z);
    }
    
    protected boolean isBurning() {
        return true;
    }
    
    protected ParticleParameters getParticleType() {
        return ParticleTypes.Q;
    }
    
    protected float getDrag() {
        return 0.95f;
    }
    
    protected abstract void onCollision(final HitResult arg1);
    
    public void writeCustomDataToTag(final CompoundTag tag) {
        final Vec3d vec3d2 = this.getVelocity();
        tag.put("direction", this.toListTag(vec3d2.x, vec3d2.y, vec3d2.z));
        tag.put("power", this.toListTag(this.posX, this.posY, this.posZ));
        tag.putInt("life", this.life);
    }
    
    public void readCustomDataFromTag(final CompoundTag tag) {
        if (tag.containsKey("power", 9)) {
            final ListTag listTag2 = tag.getList("power", 6);
            if (listTag2.size() == 3) {
                this.posX = listTag2.getDouble(0);
                this.posY = listTag2.getDouble(1);
                this.posZ = listTag2.getDouble(2);
            }
        }
        this.life = tag.getInt("life");
        if (tag.containsKey("direction", 9) && tag.getList("direction", 6).size() == 3) {
            final ListTag listTag2 = tag.getList("direction", 6);
            this.setVelocity(listTag2.getDouble(0), listTag2.getDouble(1), listTag2.getDouble(2));
        }
        else {
            this.remove();
        }
    }
    
    @Override
    public boolean collides() {
        return true;
    }
    
    @Override
    public float getBoundingBoxMarginForTargeting() {
        return 1.0f;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.scheduleVelocityUpdate();
        if (source.getAttacker() != null) {
            final Vec3d vec3d3 = source.getAttacker().getRotationVector();
            this.setVelocity(vec3d3);
            this.posX = vec3d3.x * 0.1;
            this.posY = vec3d3.y * 0.1;
            this.posZ = vec3d3.z * 0.1;
            if (source.getAttacker() instanceof LivingEntity) {
                this.owner = (LivingEntity)source.getAttacker();
            }
            return true;
        }
        return false;
    }
    
    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getLightmapCoordinates() {
        return 15728880;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        final int integer1 = (this.owner == null) ? 0 : this.owner.getEntityId();
        return new EntitySpawnS2CPacket(this.getEntityId(), this.getUuid(), this.x, this.y, this.z, this.pitch, this.yaw, this.getType(), integer1, new Vec3d(this.posX, this.posY, this.posZ));
    }
}
