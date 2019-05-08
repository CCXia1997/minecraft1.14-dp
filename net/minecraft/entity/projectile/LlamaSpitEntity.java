package net.minecraft.entity.projectile;

import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.Tag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.Material;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.world.RayTraceContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.Entity;

public class LlamaSpitEntity extends Entity implements Projectile
{
    public LlamaEntity owner;
    private CompoundTag tag;
    
    public LlamaSpitEntity(final EntityType<? extends LlamaSpitEntity> type, final World world) {
        super(type, world);
    }
    
    public LlamaSpitEntity(final World world, final LlamaEntity llamaEntity) {
        this(EntityType.LLAMA_SPIT, world);
        this.owner = llamaEntity;
        this.setPosition(llamaEntity.x - (llamaEntity.getWidth() + 1.0f) * 0.5 * MathHelper.sin(llamaEntity.aK * 0.017453292f), llamaEntity.y + llamaEntity.getStandingEyeHeight() - 0.10000000149011612, llamaEntity.z + (llamaEntity.getWidth() + 1.0f) * 0.5 * MathHelper.cos(llamaEntity.aK * 0.017453292f));
    }
    
    @Environment(EnvType.CLIENT)
    public LlamaSpitEntity(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12) {
        this(EntityType.LLAMA_SPIT, world);
        this.setPosition(double2, double4, double6);
        for (int integer14 = 0; integer14 < 7; ++integer14) {
            final double double13 = 0.4 + 0.1 * integer14;
            world.addParticle(ParticleTypes.S, double2, double4, double6, double8 * double13, double10, double12 * double13);
        }
        this.setVelocity(double8, double10, double12);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.tag != null) {
            this.readTag();
        }
        final Vec3d vec3d1 = this.getVelocity();
        final HitResult hitResult2 = ProjectileUtil.getCollision(this, this.getBoundingBox().stretch(vec3d1).expand(1.0), entity -> !entity.isSpectator() && entity != this.owner, RayTraceContext.ShapeType.b, true);
        if (hitResult2 != null) {
            this.a(hitResult2);
        }
        this.x += vec3d1.x;
        this.y += vec3d1.y;
        this.z += vec3d1.z;
        final float float3 = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d1));
        this.yaw = (float)(MathHelper.atan2(vec3d1.x, vec3d1.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d1.y, float3) * 57.2957763671875);
        while (this.pitch - this.prevPitch < -180.0f) {
            this.prevPitch -= 360.0f;
        }
        while (this.pitch - this.prevPitch >= 180.0f) {
            this.prevPitch += 360.0f;
        }
        while (this.yaw - this.prevYaw < -180.0f) {
            this.prevYaw -= 360.0f;
        }
        while (this.yaw - this.prevYaw >= 180.0f) {
            this.prevYaw += 360.0f;
        }
        this.pitch = MathHelper.lerp(0.2f, this.prevPitch, this.pitch);
        this.yaw = MathHelper.lerp(0.2f, this.prevYaw, this.yaw);
        final float float4 = 0.99f;
        final float float5 = 0.06f;
        if (!this.world.containsBlockWithMaterial(this.getBoundingBox(), Material.AIR)) {
            this.remove();
            return;
        }
        if (this.isInsideWaterOrBubbleColumn()) {
            this.remove();
            return;
        }
        this.setVelocity(vec3d1.multiply(0.9900000095367432));
        if (!this.isUnaffectedByGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.05999999865889549, 0.0));
        }
        this.setPosition(this.x, this.y, this.z);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setVelocityClient(final double x, final double y, final double z) {
        this.setVelocity(x, y, z);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            final float float7 = MathHelper.sqrt(x * x + z * z);
            this.pitch = (float)(MathHelper.atan2(y, float7) * 57.2957763671875);
            this.yaw = (float)(MathHelper.atan2(x, z) * 57.2957763671875);
            this.prevPitch = this.pitch;
            this.prevYaw = this.yaw;
            this.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
        }
    }
    
    @Override
    public void setVelocity(final double dirX, final double dirY, final double double5, final float float7, final float float8) {
        final Vec3d vec3d9 = new Vec3d(dirX, dirY, double5).normalize().add(this.random.nextGaussian() * 0.007499999832361937 * float8, this.random.nextGaussian() * 0.007499999832361937 * float8, this.random.nextGaussian() * 0.007499999832361937 * float8).multiply(float7);
        this.setVelocity(vec3d9);
        final float float9 = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d9));
        this.yaw = (float)(MathHelper.atan2(vec3d9.x, double5) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d9.y, float9) * 57.2957763671875);
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }
    
    public void a(final HitResult hitResult) {
        final HitResult.Type type2 = hitResult.getType();
        if (type2 == HitResult.Type.ENTITY && this.owner != null) {
            ((EntityHitResult)hitResult).getEntity().damage(DamageSource.mobProjectile(this, this.owner).setProjectile(), 1.0f);
        }
        else if (type2 == HitResult.Type.BLOCK && !this.world.isClient) {
            this.remove();
        }
    }
    
    @Override
    protected void initDataTracker() {
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        if (tag.containsKey("Owner", 10)) {
            this.tag = tag.getCompound("Owner");
        }
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        if (this.owner != null) {
            final CompoundTag compoundTag2 = new CompoundTag();
            final UUID uUID3 = this.owner.getUuid();
            compoundTag2.putUuid("OwnerUUID", uUID3);
            tag.put("Owner", compoundTag2);
        }
    }
    
    private void readTag() {
        if (this.tag != null && this.tag.hasUuid("OwnerUUID")) {
            final UUID uUID1 = this.tag.getUuid("OwnerUUID");
            final List<LlamaEntity> list2 = this.world.<LlamaEntity>getEntities(LlamaEntity.class, this.getBoundingBox().expand(15.0));
            for (final LlamaEntity llamaEntity4 : list2) {
                if (llamaEntity4.getUuid().equals(uUID1)) {
                    this.owner = llamaEntity4;
                    break;
                }
            }
        }
        this.tag = null;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
