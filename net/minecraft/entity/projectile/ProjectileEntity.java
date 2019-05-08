package net.minecraft.entity.projectile;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.ProjectileUtil;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.damage.DamageSource;
import com.google.common.collect.Lists;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import java.util.Iterator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import java.util.List;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.sound.SoundEvent;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import java.util.UUID;
import java.util.Optional;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.Entity;

public abstract class ProjectileEntity extends Entity implements Projectile
{
    private static final TrackedData<Byte> PROJECTILE_FLAGS;
    protected static final TrackedData<Optional<UUID>> b;
    private static final TrackedData<Byte> PIERCE_LEVEL;
    @Nullable
    private BlockState inBlockState;
    protected boolean inGround;
    protected int inGroundTime;
    public PickupType pickupType;
    public int shake;
    public UUID ownerUuid;
    private int life;
    private int av;
    private double damage;
    private int ax;
    private SoundEvent sound;
    private IntOpenHashSet piercedEntities;
    private List<Entity> piercingKilledEntities;
    
    protected ProjectileEntity(final EntityType<? extends ProjectileEntity> type, final World world) {
        super(type, world);
        this.pickupType = PickupType.NO_PICKUP;
        this.damage = 2.0;
        this.sound = this.getSound();
    }
    
    protected ProjectileEntity(final EntityType<? extends ProjectileEntity> entityType, final double double2, final double double4, final double double6, final World world8) {
        this(entityType, world8);
        this.setPosition(double2, double4, double6);
    }
    
    protected ProjectileEntity(final EntityType<? extends ProjectileEntity> entityType, final LivingEntity livingEntity, final World world) {
        this(entityType, livingEntity.x, livingEntity.y + livingEntity.getStandingEyeHeight() - 0.10000000149011612, livingEntity.z, world);
        this.setOwner(livingEntity);
        if (livingEntity instanceof PlayerEntity) {
            this.pickupType = PickupType.PICKUP;
        }
    }
    
    public void setSound(final SoundEvent soundEvent) {
        this.sound = soundEvent;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtDistance(final double distance) {
        double double3 = this.getBoundingBox().averageDimension() * 10.0;
        if (Double.isNaN(double3)) {
            double3 = 1.0;
        }
        double3 *= 64.0 * getRenderDistanceMultiplier();
        return distance < double3 * double3;
    }
    
    @Override
    protected void initDataTracker() {
        this.dataTracker.<Byte>startTracking(ProjectileEntity.PROJECTILE_FLAGS, (Byte)0);
        this.dataTracker.<Optional<UUID>>startTracking(ProjectileEntity.b, Optional.<UUID>empty());
        this.dataTracker.<Byte>startTracking(ProjectileEntity.PIERCE_LEVEL, (Byte)0);
    }
    
    public void a(final Entity entity, final float float2, final float float3, final float float4, final float float5, final float float6) {
        final float float7 = -MathHelper.sin(float3 * 0.017453292f) * MathHelper.cos(float2 * 0.017453292f);
        final float float8 = -MathHelper.sin(float2 * 0.017453292f);
        final float float9 = MathHelper.cos(float3 * 0.017453292f) * MathHelper.cos(float2 * 0.017453292f);
        this.setVelocity(float7, float8, float9, float5, float6);
        this.setVelocity(this.getVelocity().add(entity.getVelocity().x, entity.onGround ? 0.0 : entity.getVelocity().y, entity.getVelocity().z));
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
        this.life = 0;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setPositionAndRotations(final double x, final double y, final double z, final float float7, final float float8, final int integer9, final boolean boolean10) {
        this.setPosition(x, y, z);
        this.setRotation(float7, float8);
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
            this.life = 0;
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        final boolean boolean1 = this.isNoClip();
        Vec3d vec3d2 = this.getVelocity();
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            final float float3 = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d2));
            this.yaw = (float)(MathHelper.atan2(vec3d2.x, vec3d2.z) * 57.2957763671875);
            this.pitch = (float)(MathHelper.atan2(vec3d2.y, float3) * 57.2957763671875);
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
        }
        final BlockPos blockPos3 = new BlockPos(this.x, this.y, this.z);
        final BlockState blockState4 = this.world.getBlockState(blockPos3);
        if (!blockState4.isAir() && !boolean1) {
            final VoxelShape voxelShape5 = blockState4.getCollisionShape(this.world, blockPos3);
            if (!voxelShape5.isEmpty()) {
                for (final BoundingBox boundingBox7 : voxelShape5.getBoundingBoxes()) {
                    if (boundingBox7.offset(blockPos3).contains(new Vec3d(this.x, this.y, this.z))) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }
        if (this.shake > 0) {
            --this.shake;
        }
        if (this.isInsideWaterOrRain()) {
            this.extinguish();
        }
        if (this.inGround && !boolean1) {
            if (this.inBlockState != blockState4 && this.world.doesNotCollide(this.getBoundingBox().expand(0.06))) {
                this.inGround = false;
                this.setVelocity(vec3d2.multiply(this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f));
                this.life = 0;
                this.av = 0;
            }
            else if (!this.world.isClient) {
                this.age();
            }
            ++this.inGroundTime;
            return;
        }
        this.inGroundTime = 0;
        ++this.av;
        final Vec3d vec3d3 = new Vec3d(this.x, this.y, this.z);
        Vec3d vec3d4 = vec3d3.add(vec3d2);
        HitResult hitResult7 = this.world.rayTrace(new RayTraceContext(vec3d3, vec3d4, RayTraceContext.ShapeType.a, RayTraceContext.FluidHandling.NONE, this));
        if (hitResult7.getType() != HitResult.Type.NONE) {
            vec3d4 = hitResult7.getPos();
        }
        while (!this.removed) {
            EntityHitResult entityHitResult8 = this.getEntityCollision(vec3d3, vec3d4);
            if (entityHitResult8 != null) {
                hitResult7 = entityHitResult8;
            }
            if (hitResult7 != null && hitResult7.getType() == HitResult.Type.ENTITY) {
                final Entity entity9 = ((EntityHitResult)hitResult7).getEntity();
                final Entity entity10 = this.getOwner();
                if (entity9 instanceof PlayerEntity && entity10 instanceof PlayerEntity && !((PlayerEntity)entity10).shouldDamagePlayer((PlayerEntity)entity9)) {
                    hitResult7 = null;
                    entityHitResult8 = null;
                }
            }
            if (hitResult7 != null && !boolean1) {
                this.onHit(hitResult7);
                this.velocityDirty = true;
            }
            if (entityHitResult8 == null) {
                break;
            }
            if (this.getPierceLevel() <= 0) {
                break;
            }
            hitResult7 = null;
        }
        vec3d2 = this.getVelocity();
        final double double8 = vec3d2.x;
        final double double9 = vec3d2.y;
        final double double10 = vec3d2.z;
        if (this.isCritical()) {
            for (int integer14 = 0; integer14 < 4; ++integer14) {
                this.world.addParticle(ParticleTypes.g, this.x + double8 * integer14 / 4.0, this.y + double9 * integer14 / 4.0, this.z + double10 * integer14 / 4.0, -double8, -double9 + 0.2, -double10);
            }
        }
        this.x += double8;
        this.y += double9;
        this.z += double10;
        final float float4 = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d2));
        if (boolean1) {
            this.yaw = (float)(MathHelper.atan2(-double8, -double10) * 57.2957763671875);
        }
        else {
            this.yaw = (float)(MathHelper.atan2(double8, double10) * 57.2957763671875);
        }
        this.pitch = (float)(MathHelper.atan2(double9, float4) * 57.2957763671875);
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
        float float5 = 0.99f;
        final float float6 = 0.05f;
        if (this.isInsideWater()) {
            for (int integer15 = 0; integer15 < 4; ++integer15) {
                final float float7 = 0.25f;
                this.world.addParticle(ParticleTypes.e, this.x - double8 * 0.25, this.y - double9 * 0.25, this.z - double10 * 0.25, double8, double9, double10);
            }
            float5 = this.getDragInWater();
        }
        this.setVelocity(vec3d2.multiply(float5));
        if (!this.isUnaffectedByGravity() && !boolean1) {
            final Vec3d vec3d5 = this.getVelocity();
            this.setVelocity(vec3d5.x, vec3d5.y - 0.05000000074505806, vec3d5.z);
        }
        this.setPosition(this.x, this.y, this.z);
        this.checkBlockCollision();
    }
    
    protected void age() {
        ++this.life;
        if (this.life >= 1200) {
            this.remove();
        }
    }
    
    protected void onHit(final HitResult hitResult) {
        final HitResult.Type type2 = hitResult.getType();
        if (type2 == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult)hitResult);
        }
        else if (type2 == HitResult.Type.BLOCK) {
            final BlockHitResult blockHitResult3 = (BlockHitResult)hitResult;
            final BlockState blockState4 = this.world.getBlockState(blockHitResult3.getBlockPos());
            this.inBlockState = blockState4;
            final Vec3d vec3d5 = blockHitResult3.getPos().subtract(this.x, this.y, this.z);
            this.setVelocity(vec3d5);
            final Vec3d vec3d6 = vec3d5.normalize().multiply(0.05000000074505806);
            this.x -= vec3d6.x;
            this.y -= vec3d6.y;
            this.z -= vec3d6.z;
            this.playSound(this.l(), 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
            this.inGround = true;
            this.shake = 7;
            this.setCritical(false);
            this.setPierceLevel((byte)0);
            this.setSound(SoundEvents.B);
            this.setShotFromCrossbow(false);
            this.clearPiercingStatus();
            blockState4.onProjectileHit(this.world, blockState4, blockHitResult3, this);
        }
    }
    
    private void clearPiercingStatus() {
        if (this.piercingKilledEntities != null) {
            this.piercingKilledEntities.clear();
        }
        if (this.piercedEntities != null) {
            this.piercedEntities.clear();
        }
    }
    
    protected void onEntityHit(final EntityHitResult entityHitResult) {
        final Entity entity2 = entityHitResult.getEntity();
        final float float3 = (float)this.getVelocity().length();
        int integer4 = MathHelper.ceil(Math.max(float3 * this.damage, 0.0));
        if (this.getPierceLevel() > 0) {
            if (this.piercedEntities == null) {
                this.piercedEntities = new IntOpenHashSet(5);
            }
            if (this.piercingKilledEntities == null) {
                this.piercingKilledEntities = Lists.newArrayListWithCapacity(5);
            }
            if (this.piercedEntities.size() >= this.getPierceLevel() + 1) {
                this.remove();
                return;
            }
            this.piercedEntities.add(entity2.getEntityId());
        }
        if (this.isCritical()) {
            integer4 += this.random.nextInt(integer4 / 2 + 2);
        }
        final Entity entity3 = this.getOwner();
        DamageSource damageSource5;
        if (entity3 == null) {
            damageSource5 = DamageSource.arrow(this, this);
        }
        else {
            damageSource5 = DamageSource.arrow(this, entity3);
            if (entity3 instanceof LivingEntity) {
                ((LivingEntity)entity3).onAttacking(entity2);
            }
        }
        if (this.isOnFire() && !(entity2 instanceof EndermanEntity)) {
            entity2.setOnFireFor(5);
        }
        if (entity2.damage(damageSource5, (float)integer4)) {
            if (entity2 instanceof LivingEntity) {
                final LivingEntity livingEntity7 = (LivingEntity)entity2;
                if (!this.world.isClient && this.getPierceLevel() <= 0) {
                    livingEntity7.setStuckArrows(livingEntity7.getStuckArrows() + 1);
                }
                if (this.ax > 0) {
                    final Vec3d vec3d8 = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply(this.ax * 0.6);
                    if (vec3d8.lengthSquared() > 0.0) {
                        livingEntity7.addVelocity(vec3d8.x, 0.1, vec3d8.z);
                    }
                }
                if (!this.world.isClient && entity3 instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity7, entity3);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)entity3, livingEntity7);
                }
                this.onHit(livingEntity7);
                if (entity3 != null && livingEntity7 != entity3 && livingEntity7 instanceof PlayerEntity && entity3 instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity)entity3).networkHandler.sendPacket(new GameStateChangeS2CPacket(6, 0.0f));
                }
                if (!entity2.isAlive() && this.piercingKilledEntities != null) {
                    this.piercingKilledEntities.add(livingEntity7);
                }
                if (!this.world.isClient && entity3 instanceof ServerPlayerEntity) {
                    final ServerPlayerEntity serverPlayerEntity8 = (ServerPlayerEntity)entity3;
                    if (this.piercingKilledEntities != null && this.isShotFromCrossbow()) {
                        Criterions.KILLED_BY_CROSSBOW.trigger(serverPlayerEntity8, this.piercingKilledEntities, this.piercingKilledEntities.size());
                    }
                    else if (!entity2.isAlive() && this.isShotFromCrossbow()) {
                        Criterions.KILLED_BY_CROSSBOW.trigger(serverPlayerEntity8, Arrays.<Entity>asList(entity2), 0);
                    }
                }
            }
            this.playSound(this.sound, 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
            if (this.getPierceLevel() <= 0 && !(entity2 instanceof EndermanEntity)) {
                this.remove();
            }
        }
        else {
            this.setVelocity(this.getVelocity().multiply(-0.1));
            this.yaw += 180.0f;
            this.prevYaw += 180.0f;
            this.av = 0;
            if (!this.world.isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
                if (this.pickupType == PickupType.PICKUP) {
                    this.dropStack(this.asItemStack(), 0.1f);
                }
                this.remove();
            }
        }
    }
    
    protected SoundEvent getSound() {
        return SoundEvents.B;
    }
    
    protected final SoundEvent l() {
        return this.sound;
    }
    
    protected void onHit(final LivingEntity livingEntity) {
    }
    
    @Nullable
    protected EntityHitResult getEntityCollision(final Vec3d vec3d1, final Vec3d vec3d2) {
        return ProjectileUtil.getEntityCollision(this.world, this, vec3d1, vec3d2, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), entity -> !entity.isSpectator() && entity.isAlive() && entity.collides() && (entity != this.getOwner() || this.av >= 5) && (this.piercedEntities == null || !this.piercedEntities.contains(entity.getEntityId())));
    }
    
    public void writeCustomDataToTag(final CompoundTag tag) {
        tag.putShort("life", (short)this.life);
        if (this.inBlockState != null) {
            tag.put("inBlockState", TagHelper.serializeBlockState(this.inBlockState));
        }
        tag.putByte("shake", (byte)this.shake);
        tag.putByte("inGround", (byte)(this.inGround ? 1 : 0));
        tag.putByte("pickup", (byte)this.pickupType.ordinal());
        tag.putDouble("damage", this.damage);
        tag.putBoolean("crit", this.isCritical());
        tag.putByte("PierceLevel", this.getPierceLevel());
        if (this.ownerUuid != null) {
            tag.putUuid("OwnerUUID", this.ownerUuid);
        }
        tag.putString("SoundEvent", Registry.SOUND_EVENT.getId(this.sound).toString());
        tag.putBoolean("ShotFromCrossbow", this.isShotFromCrossbow());
    }
    
    public void readCustomDataFromTag(final CompoundTag tag) {
        this.life = tag.getShort("life");
        if (tag.containsKey("inBlockState", 10)) {
            this.inBlockState = TagHelper.deserializeBlockState(tag.getCompound("inBlockState"));
        }
        this.shake = (tag.getByte("shake") & 0xFF);
        this.inGround = (tag.getByte("inGround") == 1);
        if (tag.containsKey("damage", 99)) {
            this.damage = tag.getDouble("damage");
        }
        if (tag.containsKey("pickup", 99)) {
            this.pickupType = PickupType.fromOrdinal(tag.getByte("pickup"));
        }
        else if (tag.containsKey("player", 99)) {
            this.pickupType = (tag.getBoolean("player") ? PickupType.PICKUP : PickupType.NO_PICKUP);
        }
        this.setCritical(tag.getBoolean("crit"));
        this.setPierceLevel(tag.getByte("PierceLevel"));
        if (tag.hasUuid("OwnerUUID")) {
            this.ownerUuid = tag.getUuid("OwnerUUID");
        }
        if (tag.containsKey("SoundEvent", 8)) {
            this.sound = Registry.SOUND_EVENT.getOrEmpty(new Identifier(tag.getString("SoundEvent"))).orElse(this.getSound());
        }
        this.setShotFromCrossbow(tag.getBoolean("ShotFromCrossbow"));
    }
    
    public void setOwner(@Nullable final Entity entity) {
        this.ownerUuid = ((entity == null) ? null : entity.getUuid());
        if (entity instanceof PlayerEntity) {
            this.pickupType = (((PlayerEntity)entity).abilities.creativeMode ? PickupType.CREATIVE_PICKUP : PickupType.PICKUP);
        }
    }
    
    @Nullable
    public Entity getOwner() {
        if (this.ownerUuid != null && this.world instanceof ServerWorld) {
            return ((ServerWorld)this.world).getEntity(this.ownerUuid);
        }
        return null;
    }
    
    @Override
    public void onPlayerCollision(final PlayerEntity playerEntity) {
        if (this.world.isClient || (!this.inGround && !this.isNoClip()) || this.shake > 0) {
            return;
        }
        boolean boolean2 = this.pickupType == PickupType.PICKUP || (this.pickupType == PickupType.CREATIVE_PICKUP && playerEntity.abilities.creativeMode) || (this.isNoClip() && this.getOwner().getUuid() == playerEntity.getUuid());
        if (this.pickupType == PickupType.PICKUP && !playerEntity.inventory.insertStack(this.asItemStack())) {
            boolean2 = false;
        }
        if (boolean2) {
            playerEntity.sendPickup(this, 1);
            this.remove();
        }
    }
    
    protected abstract ItemStack asItemStack();
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    public void setDamage(final double double1) {
        this.damage = double1;
    }
    
    public double getDamage() {
        return this.damage;
    }
    
    public void a(final int integer) {
        this.ax = integer;
    }
    
    @Override
    public boolean canPlayerAttack() {
        return false;
    }
    
    @Override
    protected float getEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 0.0f;
    }
    
    public void setCritical(final boolean boolean1) {
        this.setProjectileFlag(1, boolean1);
    }
    
    public void setPierceLevel(final byte byte1) {
        this.dataTracker.<Byte>set(ProjectileEntity.PIERCE_LEVEL, byte1);
    }
    
    private void setProjectileFlag(final int index, final boolean boolean2) {
        final byte byte3 = this.dataTracker.<Byte>get(ProjectileEntity.PROJECTILE_FLAGS);
        if (boolean2) {
            this.dataTracker.<Byte>set(ProjectileEntity.PROJECTILE_FLAGS, (byte)(byte3 | index));
        }
        else {
            this.dataTracker.<Byte>set(ProjectileEntity.PROJECTILE_FLAGS, (byte)(byte3 & ~index));
        }
    }
    
    public boolean isCritical() {
        final byte byte1 = this.dataTracker.<Byte>get(ProjectileEntity.PROJECTILE_FLAGS);
        return (byte1 & 0x1) != 0x0;
    }
    
    public boolean isShotFromCrossbow() {
        final byte byte1 = this.dataTracker.<Byte>get(ProjectileEntity.PROJECTILE_FLAGS);
        return (byte1 & 0x4) != 0x0;
    }
    
    public byte getPierceLevel() {
        return this.dataTracker.<Byte>get(ProjectileEntity.PIERCE_LEVEL);
    }
    
    public void a(final LivingEntity livingEntity, final float float2) {
        final int integer3 = EnchantmentHelper.getEquipmentLevel(Enchantments.w, livingEntity);
        final int integer4 = EnchantmentHelper.getEquipmentLevel(Enchantments.x, livingEntity);
        this.setDamage(float2 * 2.0f + (this.random.nextGaussian() * 0.25 + this.world.getDifficulty().getId() * 0.11f));
        if (integer3 > 0) {
            this.setDamage(this.getDamage() + integer3 * 0.5 + 0.5);
        }
        if (integer4 > 0) {
            this.a(integer4);
        }
        if (EnchantmentHelper.getEquipmentLevel(Enchantments.y, livingEntity) > 0) {
            this.setOnFireFor(100);
        }
    }
    
    protected float getDragInWater() {
        return 0.6f;
    }
    
    public void setNoClip(final boolean boolean1) {
        this.setProjectileFlag(2, this.noClip = boolean1);
    }
    
    public boolean isNoClip() {
        if (!this.world.isClient) {
            return this.noClip;
        }
        return (this.dataTracker.<Byte>get(ProjectileEntity.PROJECTILE_FLAGS) & 0x2) != 0x0;
    }
    
    public void setShotFromCrossbow(final boolean boolean1) {
        this.setProjectileFlag(4, boolean1);
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        final Entity entity1 = this.getOwner();
        return new EntitySpawnS2CPacket(this, (entity1 == null) ? 0 : entity1.getEntityId());
    }
    
    static {
        PROJECTILE_FLAGS = DataTracker.<Byte>registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
        b = DataTracker.<Optional<UUID>>registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
        PIERCE_LEVEL = DataTracker.<Byte>registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
    
    public enum PickupType
    {
        NO_PICKUP, 
        PICKUP, 
        CREATIVE_PICKUP;
        
        public static PickupType fromOrdinal(int integer) {
            if (integer < 0 || integer > values().length) {
                integer = 0;
            }
            return values()[integer];
        }
    }
}
