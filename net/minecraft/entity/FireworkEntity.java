package net.minecraft.entity;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.nbt.Tag;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.RayTraceContext;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import java.util.OptionalInt;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.data.TrackedData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.entity.projectile.Projectile;

@EnvironmentInterfaces({ @EnvironmentInterface(value = EnvType.CLIENT, itf = awj.class) })
public class FireworkEntity extends Entity implements FlyingItemEntity, Projectile
{
    private static final TrackedData<ItemStack> ITEM;
    private static final TrackedData<OptionalInt> SHOOTER_ENTITY_ID;
    private static final TrackedData<Boolean> SHOT_AT_ANGLE;
    private int life;
    private int lifeTime;
    private LivingEntity shooter;
    
    public FireworkEntity(final EntityType<? extends FireworkEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initDataTracker() {
        this.dataTracker.<ItemStack>startTracking(FireworkEntity.ITEM, ItemStack.EMPTY);
        this.dataTracker.<OptionalInt>startTracking(FireworkEntity.SHOOTER_ENTITY_ID, OptionalInt.empty());
        this.dataTracker.<Boolean>startTracking(FireworkEntity.SHOT_AT_ANGLE, false);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtDistance(final double distance) {
        return distance < 4096.0 && !this.wasShotByEntity();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderFrom(final double x, final double y, final double z) {
        return super.shouldRenderFrom(x, y, z) && !this.wasShotByEntity();
    }
    
    public FireworkEntity(final World world, final double x, final double y, final double z, final ItemStack item) {
        super(EntityType.FIREWORK_ROCKET, world);
        this.life = 0;
        this.setPosition(x, y, z);
        int integer9 = 1;
        if (!item.isEmpty() && item.hasTag()) {
            this.dataTracker.<ItemStack>set(FireworkEntity.ITEM, item.copy());
            integer9 += item.getOrCreateSubCompoundTag("Fireworks").getByte("Flight");
        }
        this.setVelocity(this.random.nextGaussian() * 0.001, 0.05, this.random.nextGaussian() * 0.001);
        this.lifeTime = 10 * integer9 + this.random.nextInt(6) + this.random.nextInt(7);
    }
    
    public FireworkEntity(final World world, final ItemStack item, final LivingEntity shooter) {
        this(world, shooter.x, shooter.y, shooter.z, item);
        this.dataTracker.<OptionalInt>set(FireworkEntity.SHOOTER_ENTITY_ID, OptionalInt.of(shooter.getEntityId()));
        this.shooter = shooter;
    }
    
    public FireworkEntity(final World world, final ItemStack itemStack, final double double3, final double double5, final double double7, final boolean boolean9) {
        this(world, double3, double5, double7, itemStack);
        this.dataTracker.<Boolean>set(FireworkEntity.SHOT_AT_ANGLE, boolean9);
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
        if (this.wasShotByEntity()) {
            if (this.shooter == null) {
                final Entity entity2;
                this.dataTracker.<OptionalInt>get(FireworkEntity.SHOOTER_ENTITY_ID).ifPresent(integer -> {
                    entity2 = this.world.getEntityById(integer);
                    if (entity2 instanceof LivingEntity) {
                        this.shooter = (LivingEntity)entity2;
                    }
                    return;
                });
            }
            if (this.shooter != null) {
                if (this.shooter.isFallFlying()) {
                    final Vec3d vec3d1 = this.shooter.getRotationVector();
                    final double double2 = 1.5;
                    final double double3 = 0.1;
                    final Vec3d vec3d2 = this.shooter.getVelocity();
                    this.shooter.setVelocity(vec3d2.add(vec3d1.x * 0.1 + (vec3d1.x * 1.5 - vec3d2.x) * 0.5, vec3d1.y * 0.1 + (vec3d1.y * 1.5 - vec3d2.y) * 0.5, vec3d1.z * 0.1 + (vec3d1.z * 1.5 - vec3d2.z) * 0.5));
                }
                this.setPosition(this.shooter.x, this.shooter.y, this.shooter.z);
                this.setVelocity(this.shooter.getVelocity());
            }
        }
        else {
            if (!this.wasShotAtAngle()) {
                this.setVelocity(this.getVelocity().multiply(1.15, 1.0, 1.15).add(0.0, 0.04, 0.0));
            }
            this.move(MovementType.a, this.getVelocity());
        }
        final Vec3d vec3d1 = this.getVelocity();
        final HitResult hitResult2 = ProjectileUtil.getCollision(this, this.getBoundingBox().stretch(vec3d1).expand(1.0), entity -> !entity.isSpectator() && entity.isAlive() && entity.collides(), RayTraceContext.ShapeType.a, true);
        if (!this.noClip) {
            this.handleCollision(hitResult2);
            this.velocityDirty = true;
        }
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
        if (this.life == 0 && !this.isSilent()) {
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.dl, SoundCategory.i, 3.0f, 1.0f);
        }
        ++this.life;
        if (this.world.isClient && this.life % 2 < 2) {
            this.world.addParticle(ParticleTypes.y, this.x, this.y - 0.3, this.z, this.random.nextGaussian() * 0.05, -this.getVelocity().y * 0.5, this.random.nextGaussian() * 0.05);
        }
        if (!this.world.isClient && this.life > this.lifeTime) {
            this.explodeAndRemove();
        }
    }
    
    private void explodeAndRemove() {
        this.world.sendEntityStatus(this, (byte)17);
        this.explode();
        this.remove();
    }
    
    protected void handleCollision(final HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY && !this.world.isClient) {
            this.explodeAndRemove();
        }
        else if (this.collided) {
            BlockPos blockPos2;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                blockPos2 = new BlockPos(((BlockHitResult)hitResult).getBlockPos());
            }
            else {
                blockPos2 = new BlockPos(this);
            }
            this.world.getBlockState(blockPos2).onEntityCollision(this.world, blockPos2, this);
            if (this.l()) {
                this.explodeAndRemove();
            }
        }
    }
    
    private boolean l() {
        final ItemStack itemStack1 = this.dataTracker.<ItemStack>get(FireworkEntity.ITEM);
        final CompoundTag compoundTag2 = itemStack1.isEmpty() ? null : itemStack1.getSubCompoundTag("Fireworks");
        final ListTag listTag3 = (compoundTag2 != null) ? compoundTag2.getList("Explosions", 10) : null;
        return listTag3 != null && !listTag3.isEmpty();
    }
    
    private void explode() {
        float float1 = 0.0f;
        final ItemStack itemStack2 = this.dataTracker.<ItemStack>get(FireworkEntity.ITEM);
        final CompoundTag compoundTag3 = itemStack2.isEmpty() ? null : itemStack2.getSubCompoundTag("Fireworks");
        final ListTag listTag4 = (compoundTag3 != null) ? compoundTag3.getList("Explosions", 10) : null;
        if (listTag4 != null && !listTag4.isEmpty()) {
            float1 = 5.0f + listTag4.size() * 2;
        }
        if (float1 > 0.0f) {
            if (this.shooter != null) {
                this.shooter.damage(DamageSource.FIREWORKS, 5.0f + listTag4.size() * 2);
            }
            final double double5 = 5.0;
            final Vec3d vec3d7 = new Vec3d(this.x, this.y, this.z);
            final List<LivingEntity> list8 = this.world.<LivingEntity>getEntities(LivingEntity.class, this.getBoundingBox().expand(5.0));
            for (final LivingEntity livingEntity10 : list8) {
                if (livingEntity10 == this.shooter) {
                    continue;
                }
                if (this.squaredDistanceTo(livingEntity10) > 25.0) {
                    continue;
                }
                boolean boolean11 = false;
                for (int integer12 = 0; integer12 < 2; ++integer12) {
                    final Vec3d vec3d8 = new Vec3d(livingEntity10.x, livingEntity10.y + livingEntity10.getHeight() * 0.5 * integer12, livingEntity10.z);
                    final HitResult hitResult14 = this.world.rayTrace(new RayTraceContext(vec3d7, vec3d8, RayTraceContext.ShapeType.a, RayTraceContext.FluidHandling.NONE, this));
                    if (hitResult14.getType() == HitResult.Type.NONE) {
                        boolean11 = true;
                        break;
                    }
                }
                if (!boolean11) {
                    continue;
                }
                final float float2 = float1 * (float)Math.sqrt((5.0 - this.distanceTo(livingEntity10)) / 5.0);
                livingEntity10.damage(DamageSource.FIREWORKS, float2);
            }
        }
    }
    
    private boolean wasShotByEntity() {
        return this.dataTracker.<OptionalInt>get(FireworkEntity.SHOOTER_ENTITY_ID).isPresent();
    }
    
    public boolean wasShotAtAngle() {
        return this.dataTracker.<Boolean>get(FireworkEntity.SHOT_AT_ANGLE);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 17 && this.world.isClient) {
            if (!this.l()) {
                for (int integer2 = 0; integer2 < this.random.nextInt(3) + 2; ++integer2) {
                    this.world.addParticle(ParticleTypes.N, this.x, this.y, this.z, this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05);
                }
            }
            else {
                final ItemStack itemStack2 = this.dataTracker.<ItemStack>get(FireworkEntity.ITEM);
                final CompoundTag compoundTag3 = itemStack2.isEmpty() ? null : itemStack2.getSubCompoundTag("Fireworks");
                final Vec3d vec3d4 = this.getVelocity();
                this.world.addFireworkParticle(this.x, this.y, this.z, vec3d4.x, vec3d4.y, vec3d4.z, compoundTag3);
            }
        }
        super.handleStatus(status);
    }
    
    public void writeCustomDataToTag(final CompoundTag tag) {
        tag.putInt("Life", this.life);
        tag.putInt("LifeTime", this.lifeTime);
        final ItemStack itemStack2 = this.dataTracker.<ItemStack>get(FireworkEntity.ITEM);
        if (!itemStack2.isEmpty()) {
            tag.put("FireworksItem", itemStack2.toTag(new CompoundTag()));
        }
        tag.putBoolean("ShotAtAngle", this.dataTracker.<Boolean>get(FireworkEntity.SHOT_AT_ANGLE));
    }
    
    public void readCustomDataFromTag(final CompoundTag tag) {
        this.life = tag.getInt("Life");
        this.lifeTime = tag.getInt("LifeTime");
        final ItemStack itemStack2 = ItemStack.fromTag(tag.getCompound("FireworksItem"));
        if (!itemStack2.isEmpty()) {
            this.dataTracker.<ItemStack>set(FireworkEntity.ITEM, itemStack2);
        }
        if (tag.containsKey("ShotAtAngle")) {
            this.dataTracker.<Boolean>set(FireworkEntity.SHOT_AT_ANGLE, tag.getBoolean("ShotAtAngle"));
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getStack() {
        final ItemStack itemStack1 = this.dataTracker.<ItemStack>get(FireworkEntity.ITEM);
        return itemStack1.isEmpty() ? new ItemStack(Items.nX) : itemStack1;
    }
    
    @Override
    public boolean canPlayerAttack() {
        return false;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
    
    @Override
    public void setVelocity(double dirX, double dirY, double double5, final float float7, final float float8) {
        final float float9 = MathHelper.sqrt(dirX * dirX + dirY * dirY + double5 * double5);
        dirX /= float9;
        dirY /= float9;
        double5 /= float9;
        dirX += this.random.nextGaussian() * 0.007499999832361937 * float8;
        dirY += this.random.nextGaussian() * 0.007499999832361937 * float8;
        double5 += this.random.nextGaussian() * 0.007499999832361937 * float8;
        dirX *= float7;
        dirY *= float7;
        double5 *= float7;
        this.setVelocity(dirX, dirY, double5);
    }
    
    static {
        ITEM = DataTracker.<ItemStack>registerData(FireworkEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
        SHOOTER_ENTITY_ID = DataTracker.<OptionalInt>registerData(FireworkEntity.class, TrackedDataHandlerRegistry.r);
        SHOT_AT_ANGLE = DataTracker.<Boolean>registerData(FireworkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
