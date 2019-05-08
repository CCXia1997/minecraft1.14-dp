package net.minecraft.entity.thrown;

import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.world.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.entity.Entity;

public abstract class ThrownEntity extends Entity implements Projectile
{
    private int blockX;
    private int blockY;
    private int blockZ;
    protected boolean inGround;
    public int shake;
    protected LivingEntity owner;
    private UUID ownerUuid;
    private Entity as;
    private int at;
    
    protected ThrownEntity(final EntityType<? extends ThrownEntity> type, final World world) {
        super(type, world);
        this.blockX = -1;
        this.blockY = -1;
        this.blockZ = -1;
    }
    
    protected ThrownEntity(final EntityType<? extends ThrownEntity> type, final double x, final double y, final double z, final World world) {
        this(type, world);
        this.setPosition(x, y, z);
    }
    
    protected ThrownEntity(final EntityType<? extends ThrownEntity> type, final LivingEntity owner, final World world) {
        this(type, owner.x, owner.y + owner.getStandingEyeHeight() - 0.10000000149011612, owner.z, world);
        this.owner = owner;
        this.ownerUuid = owner.getUuid();
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
    
    public void a(final Entity entity, final float float2, final float float3, final float float4, final float float5, final float float6) {
        final float float7 = -MathHelper.sin(float3 * 0.017453292f) * MathHelper.cos(float2 * 0.017453292f);
        final float float8 = -MathHelper.sin((float2 + float4) * 0.017453292f);
        final float float9 = MathHelper.cos(float3 * 0.017453292f) * MathHelper.cos(float2 * 0.017453292f);
        this.setVelocity(float7, float8, float9, float5, float6);
        final Vec3d vec3d10 = entity.getVelocity();
        this.setVelocity(this.getVelocity().add(vec3d10.x, entity.onGround ? 0.0 : vec3d10.y, vec3d10.z));
    }
    
    @Override
    public void setVelocity(final double dirX, final double dirY, final double double5, final float float7, final float float8) {
        final Vec3d vec3d9 = new Vec3d(dirX, dirY, double5).normalize().add(this.random.nextGaussian() * 0.007499999832361937 * float8, this.random.nextGaussian() * 0.007499999832361937 * float8, this.random.nextGaussian() * 0.007499999832361937 * float8).multiply(float7);
        this.setVelocity(vec3d9);
        final float float9 = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d9));
        this.yaw = (float)(MathHelper.atan2(vec3d9.x, vec3d9.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d9.y, float9) * 57.2957763671875);
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setVelocityClient(final double x, final double y, final double z) {
        this.setVelocity(x, y, z);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            final float float7 = MathHelper.sqrt(x * x + z * z);
            this.yaw = (float)(MathHelper.atan2(x, z) * 57.2957763671875);
            this.pitch = (float)(MathHelper.atan2(y, float7) * 57.2957763671875);
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
        }
    }
    
    @Override
    public void tick() {
        this.prevRenderX = this.x;
        this.prevRenderY = this.y;
        this.prevRenderZ = this.z;
        super.tick();
        if (this.shake > 0) {
            --this.shake;
        }
        if (this.inGround) {
            this.inGround = false;
            this.setVelocity(this.getVelocity().multiply(this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f));
        }
        final BoundingBox boundingBox1 = this.getBoundingBox().stretch(this.getVelocity()).expand(1.0);
        for (final Entity entity2 : this.world.getEntities(this, boundingBox1, entity -> !entity.isSpectator() && entity.collides())) {
            if (entity2 == this.as) {
                ++this.at;
                break;
            }
            if (this.owner != null && this.age < 2 && this.as == null) {
                this.as = entity2;
                this.at = 3;
                break;
            }
        }
        final HitResult hitResult2 = ProjectileUtil.getCollision(this, boundingBox1, entity -> !entity.isSpectator() && entity.collides() && entity != this.as, RayTraceContext.ShapeType.b, true);
        if (this.as != null && this.at-- <= 0) {
            this.as = null;
        }
        if (hitResult2.getType() != HitResult.Type.NONE) {
            if (hitResult2.getType() == HitResult.Type.BLOCK && this.world.getBlockState(((BlockHitResult)hitResult2).getBlockPos()).getBlock() == Blocks.cM) {
                this.setInPortal(((BlockHitResult)hitResult2).getBlockPos());
            }
            else {
                this.onCollision(hitResult2);
            }
        }
        final Vec3d vec3d3 = this.getVelocity();
        this.x += vec3d3.x;
        this.y += vec3d3.y;
        this.z += vec3d3.z;
        final float float4 = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d3));
        this.yaw = (float)(MathHelper.atan2(vec3d3.x, vec3d3.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(vec3d3.y, float4) * 57.2957763671875);
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
        float float6;
        if (this.isInsideWater()) {
            for (int integer6 = 0; integer6 < 4; ++integer6) {
                final float float5 = 0.25f;
                this.world.addParticle(ParticleTypes.e, this.x - vec3d3.x * 0.25, this.y - vec3d3.y * 0.25, this.z - vec3d3.z * 0.25, vec3d3.x, vec3d3.y, vec3d3.z);
            }
            float6 = 0.8f;
        }
        else {
            float6 = 0.99f;
        }
        this.setVelocity(vec3d3.multiply(float6));
        if (!this.isUnaffectedByGravity()) {
            final Vec3d vec3d4 = this.getVelocity();
            this.setVelocity(vec3d4.x, vec3d4.y - this.getGravity(), vec3d4.z);
        }
        this.setPosition(this.x, this.y, this.z);
    }
    
    protected float getGravity() {
        return 0.03f;
    }
    
    protected abstract void onCollision(final HitResult arg1);
    
    public void writeCustomDataToTag(final CompoundTag tag) {
        tag.putInt("xTile", this.blockX);
        tag.putInt("yTile", this.blockY);
        tag.putInt("zTile", this.blockZ);
        tag.putByte("shake", (byte)this.shake);
        tag.putByte("inGround", (byte)(this.inGround ? 1 : 0));
        if (this.ownerUuid != null) {
            tag.put("owner", TagHelper.serializeUuid(this.ownerUuid));
        }
    }
    
    public void readCustomDataFromTag(final CompoundTag tag) {
        this.blockX = tag.getInt("xTile");
        this.blockY = tag.getInt("yTile");
        this.blockZ = tag.getInt("zTile");
        this.shake = (tag.getByte("shake") & 0xFF);
        this.inGround = (tag.getByte("inGround") == 1);
        this.owner = null;
        if (tag.containsKey("owner", 10)) {
            this.ownerUuid = TagHelper.deserializeUuid(tag.getCompound("owner"));
        }
    }
    
    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUuid != null && this.world instanceof ServerWorld) {
            final Entity entity1 = ((ServerWorld)this.world).getEntity(this.ownerUuid);
            if (entity1 instanceof LivingEntity) {
                this.owner = (LivingEntity)entity1;
            }
            else {
                this.ownerUuid = null;
            }
        }
        return this.owner;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
