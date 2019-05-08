package net.minecraft.entity.projectile;

import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import java.util.Iterator;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.world.RayTraceContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.Difficulty;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import com.google.common.collect.Lists;
import net.minecraft.util.math.Position;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;

public class ShulkerBulletEntity extends Entity
{
    private LivingEntity owner;
    private Entity target;
    @Nullable
    private Direction direction;
    private int e;
    private double f;
    private double g;
    private double ar;
    @Nullable
    private UUID ownerUuid;
    private BlockPos ownerPos;
    @Nullable
    private UUID targetUuid;
    private BlockPos targetPos;
    
    public ShulkerBulletEntity(final EntityType<? extends ShulkerBulletEntity> type, final World world) {
        super(type, world);
        this.noClip = true;
    }
    
    @Environment(EnvType.CLIENT)
    public ShulkerBulletEntity(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12) {
        this(EntityType.SHULKER_BULLET, world);
        this.setPositionAndAngles(double2, double4, double6, this.yaw, this.pitch);
        this.setVelocity(double8, double10, double12);
    }
    
    public ShulkerBulletEntity(final World world, final LivingEntity livingEntity, final Entity entity, final Direction.Axis axis) {
        this(EntityType.SHULKER_BULLET, world);
        this.owner = livingEntity;
        final BlockPos blockPos5 = new BlockPos(livingEntity);
        final double double6 = blockPos5.getX() + 0.5;
        final double double7 = blockPos5.getY() + 0.5;
        final double double8 = blockPos5.getZ() + 0.5;
        this.setPositionAndAngles(double6, double7, double8, this.yaw, this.pitch);
        this.target = entity;
        this.direction = Direction.UP;
        this.a(axis);
    }
    
    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.f;
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        if (this.owner != null) {
            final BlockPos blockPos2 = new BlockPos(this.owner);
            final CompoundTag compoundTag3 = TagHelper.serializeUuid(this.owner.getUuid());
            compoundTag3.putInt("X", blockPos2.getX());
            compoundTag3.putInt("Y", blockPos2.getY());
            compoundTag3.putInt("Z", blockPos2.getZ());
            tag.put("Owner", compoundTag3);
        }
        if (this.target != null) {
            final BlockPos blockPos2 = new BlockPos(this.target);
            final CompoundTag compoundTag3 = TagHelper.serializeUuid(this.target.getUuid());
            compoundTag3.putInt("X", blockPos2.getX());
            compoundTag3.putInt("Y", blockPos2.getY());
            compoundTag3.putInt("Z", blockPos2.getZ());
            tag.put("Target", compoundTag3);
        }
        if (this.direction != null) {
            tag.putInt("Dir", this.direction.getId());
        }
        tag.putInt("Steps", this.e);
        tag.putDouble("TXD", this.f);
        tag.putDouble("TYD", this.g);
        tag.putDouble("TZD", this.ar);
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        this.e = tag.getInt("Steps");
        this.f = tag.getDouble("TXD");
        this.g = tag.getDouble("TYD");
        this.ar = tag.getDouble("TZD");
        if (tag.containsKey("Dir", 99)) {
            this.direction = Direction.byId(tag.getInt("Dir"));
        }
        if (tag.containsKey("Owner", 10)) {
            final CompoundTag compoundTag2 = tag.getCompound("Owner");
            this.ownerUuid = TagHelper.deserializeUuid(compoundTag2);
            this.ownerPos = new BlockPos(compoundTag2.getInt("X"), compoundTag2.getInt("Y"), compoundTag2.getInt("Z"));
        }
        if (tag.containsKey("Target", 10)) {
            final CompoundTag compoundTag2 = tag.getCompound("Target");
            this.targetUuid = TagHelper.deserializeUuid(compoundTag2);
            this.targetPos = new BlockPos(compoundTag2.getInt("X"), compoundTag2.getInt("Y"), compoundTag2.getInt("Z"));
        }
    }
    
    @Override
    protected void initDataTracker() {
    }
    
    private void setDirection(@Nullable final Direction direction) {
        this.direction = direction;
    }
    
    private void a(@Nullable final Direction.Axis axis) {
        double double3 = 0.5;
        BlockPos blockPos2;
        if (this.target == null) {
            blockPos2 = new BlockPos(this).down();
        }
        else {
            double3 = this.target.getHeight() * 0.5;
            blockPos2 = new BlockPos(this.target.x, this.target.y + double3, this.target.z);
        }
        double double4 = blockPos2.getX() + 0.5;
        double double5 = blockPos2.getY() + double3;
        double double6 = blockPos2.getZ() + 0.5;
        Direction direction11 = null;
        if (!blockPos2.isWithinDistance(this.getPos(), 2.0)) {
            final BlockPos blockPos3 = new BlockPos(this);
            final List<Direction> list13 = Lists.newArrayList();
            if (axis != Direction.Axis.X) {
                if (blockPos3.getX() < blockPos2.getX() && this.world.isAir(blockPos3.east())) {
                    list13.add(Direction.EAST);
                }
                else if (blockPos3.getX() > blockPos2.getX() && this.world.isAir(blockPos3.west())) {
                    list13.add(Direction.WEST);
                }
            }
            if (axis != Direction.Axis.Y) {
                if (blockPos3.getY() < blockPos2.getY() && this.world.isAir(blockPos3.up())) {
                    list13.add(Direction.UP);
                }
                else if (blockPos3.getY() > blockPos2.getY() && this.world.isAir(blockPos3.down())) {
                    list13.add(Direction.DOWN);
                }
            }
            if (axis != Direction.Axis.Z) {
                if (blockPos3.getZ() < blockPos2.getZ() && this.world.isAir(blockPos3.south())) {
                    list13.add(Direction.SOUTH);
                }
                else if (blockPos3.getZ() > blockPos2.getZ() && this.world.isAir(blockPos3.north())) {
                    list13.add(Direction.NORTH);
                }
            }
            direction11 = Direction.random(this.random);
            if (list13.isEmpty()) {
                for (int integer14 = 5; !this.world.isAir(blockPos3.offset(direction11)) && integer14 > 0; direction11 = Direction.random(this.random), --integer14) {}
            }
            else {
                direction11 = list13.get(this.random.nextInt(list13.size()));
            }
            double4 = this.x + direction11.getOffsetX();
            double5 = this.y + direction11.getOffsetY();
            double6 = this.z + direction11.getOffsetZ();
        }
        this.setDirection(direction11);
        final double double7 = double4 - this.x;
        final double double8 = double5 - this.y;
        final double double9 = double6 - this.z;
        final double double10 = MathHelper.sqrt(double7 * double7 + double8 * double8 + double9 * double9);
        if (double10 == 0.0) {
            this.f = 0.0;
            this.g = 0.0;
            this.ar = 0.0;
        }
        else {
            this.f = double7 / double10 * 0.15;
            this.g = double8 / double10 * 0.15;
            this.ar = double9 / double10 * 0.15;
        }
        this.velocityDirty = true;
        this.e = 10 + this.random.nextInt(5) * 10;
    }
    
    @Override
    public void tick() {
        if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
            return;
        }
        super.tick();
        if (!this.world.isClient) {
            if (this.target == null && this.targetUuid != null) {
                final List<LivingEntity> list1 = this.world.<LivingEntity>getEntities(LivingEntity.class, new BoundingBox(this.targetPos.add(-2, -2, -2), this.targetPos.add(2, 2, 2)));
                for (final LivingEntity livingEntity3 : list1) {
                    if (livingEntity3.getUuid().equals(this.targetUuid)) {
                        this.target = livingEntity3;
                        break;
                    }
                }
                this.targetUuid = null;
            }
            if (this.owner == null && this.ownerUuid != null) {
                final List<LivingEntity> list1 = this.world.<LivingEntity>getEntities(LivingEntity.class, new BoundingBox(this.ownerPos.add(-2, -2, -2), this.ownerPos.add(2, 2, 2)));
                for (final LivingEntity livingEntity3 : list1) {
                    if (livingEntity3.getUuid().equals(this.ownerUuid)) {
                        this.owner = livingEntity3;
                        break;
                    }
                }
                this.ownerUuid = null;
            }
            if (this.target != null && this.target.isAlive() && (!(this.target instanceof PlayerEntity) || !((PlayerEntity)this.target).isSpectator())) {
                this.f = MathHelper.clamp(this.f * 1.025, -1.0, 1.0);
                this.g = MathHelper.clamp(this.g * 1.025, -1.0, 1.0);
                this.ar = MathHelper.clamp(this.ar * 1.025, -1.0, 1.0);
                final Vec3d vec3d1 = this.getVelocity();
                this.setVelocity(vec3d1.add((this.f - vec3d1.x) * 0.2, (this.g - vec3d1.y) * 0.2, (this.ar - vec3d1.z) * 0.2));
            }
            else if (!this.isUnaffectedByGravity()) {
                this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            }
            final HitResult hitResult1 = ProjectileUtil.getCollision(this, true, false, this.owner, RayTraceContext.ShapeType.a);
            if (hitResult1.getType() != HitResult.Type.NONE) {
                this.onHit(hitResult1);
            }
        }
        final Vec3d vec3d1 = this.getVelocity();
        this.setPosition(this.x + vec3d1.x, this.y + vec3d1.y, this.z + vec3d1.z);
        ProjectileUtil.a(this, 0.5f);
        if (this.world.isClient) {
            this.world.addParticle(ParticleTypes.t, this.x - vec3d1.x, this.y - vec3d1.y + 0.15, this.z - vec3d1.z, 0.0, 0.0, 0.0);
        }
        else if (this.target != null && !this.target.removed) {
            if (this.e > 0) {
                --this.e;
                if (this.e == 0) {
                    this.a((this.direction == null) ? null : this.direction.getAxis());
                }
            }
            if (this.direction != null) {
                final BlockPos blockPos2 = new BlockPos(this);
                final Direction.Axis axis3 = this.direction.getAxis();
                if (this.world.doesBlockHaveSolidTopSurface(blockPos2.offset(this.direction), this)) {
                    this.a(axis3);
                }
                else {
                    final BlockPos blockPos3 = new BlockPos(this.target);
                    if ((axis3 == Direction.Axis.X && blockPos2.getX() == blockPos3.getX()) || (axis3 == Direction.Axis.Z && blockPos2.getZ() == blockPos3.getZ()) || (axis3 == Direction.Axis.Y && blockPos2.getY() == blockPos3.getY())) {
                        this.a(axis3);
                    }
                }
            }
        }
    }
    
    @Override
    public boolean isOnFire() {
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtDistance(final double distance) {
        return distance < 16384.0;
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
    
    protected void onHit(final HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            final Entity entity2 = ((EntityHitResult)hitResult).getEntity();
            final boolean boolean3 = entity2.damage(DamageSource.mobProjectile(this, this.owner).setProjectile(), 4.0f);
            if (boolean3) {
                this.dealDamage(this.owner, entity2);
                if (entity2 instanceof LivingEntity) {
                    ((LivingEntity)entity2).addPotionEffect(new StatusEffectInstance(StatusEffects.y, 200));
                }
            }
        }
        else {
            ((ServerWorld)this.world).<DefaultParticleType>spawnParticles(ParticleTypes.w, this.x, this.y, this.z, 2, 0.2, 0.2, 0.2, 0.0);
            this.playSound(SoundEvents.ka, 1.0f, 1.0f);
        }
        this.remove();
    }
    
    @Override
    public boolean collides() {
        return true;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (!this.world.isClient) {
            this.playSound(SoundEvents.kb, 1.0f, 1.0f);
            ((ServerWorld)this.world).<DefaultParticleType>spawnParticles(ParticleTypes.g, this.x, this.y, this.z, 15, 0.2, 0.2, 0.2, 0.0);
            this.remove();
        }
        return true;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
