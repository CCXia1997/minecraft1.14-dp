package net.minecraft.entity.mob;

import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.network.Packet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.damage.DamageSource;
import java.util.Iterator;
import java.util.List;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;

public class EvokerFangsEntity extends Entity
{
    private int warmup;
    private boolean c;
    private int ticksLeft;
    private boolean hasAttacked;
    private LivingEntity owner;
    private UUID ownerUuid;
    
    public EvokerFangsEntity(final EntityType<? extends EvokerFangsEntity> type, final World world) {
        super(type, world);
        this.ticksLeft = 22;
    }
    
    public EvokerFangsEntity(final World world, final double x, final double y, final double z, final float float8, final int integer9, final LivingEntity livingEntity10) {
        this(EntityType.EVOKER_FANGS, world);
        this.warmup = integer9;
        this.setOwner(livingEntity10);
        this.yaw = float8 * 57.295776f;
        this.setPosition(x, y, z);
    }
    
    @Override
    protected void initDataTracker() {
    }
    
    public void setOwner(@Nullable final LivingEntity livingEntity) {
        this.owner = livingEntity;
        this.ownerUuid = ((livingEntity == null) ? null : livingEntity.getUuid());
    }
    
    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUuid != null && this.world instanceof ServerWorld) {
            final Entity entity1 = ((ServerWorld)this.world).getEntity(this.ownerUuid);
            if (entity1 instanceof LivingEntity) {
                this.owner = (LivingEntity)entity1;
            }
        }
        return this.owner;
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        this.warmup = tag.getInt("Warmup");
        if (tag.hasUuid("OwnerUUID")) {
            this.ownerUuid = tag.getUuid("OwnerUUID");
        }
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        tag.putInt("Warmup", this.warmup);
        if (this.ownerUuid != null) {
            tag.putUuid("OwnerUUID", this.ownerUuid);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            if (this.hasAttacked) {
                --this.ticksLeft;
                if (this.ticksLeft == 14) {
                    for (int integer1 = 0; integer1 < 12; ++integer1) {
                        final double double2 = this.x + (this.random.nextDouble() * 2.0 - 1.0) * this.getWidth() * 0.5;
                        final double double3 = this.y + 0.05 + this.random.nextDouble();
                        final double double4 = this.z + (this.random.nextDouble() * 2.0 - 1.0) * this.getWidth() * 0.5;
                        final double double5 = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        final double double6 = 0.3 + this.random.nextDouble() * 0.3;
                        final double double7 = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        this.world.addParticle(ParticleTypes.g, double2, double3 + 1.0, double4, double5, double6, double7);
                    }
                }
            }
        }
        else if (--this.warmup < 0) {
            if (this.warmup == -8) {
                final List<LivingEntity> list1 = this.world.<LivingEntity>getEntities(LivingEntity.class, this.getBoundingBox().expand(0.2, 0.0, 0.2));
                for (final LivingEntity livingEntity3 : list1) {
                    this.damage(livingEntity3);
                }
            }
            if (!this.c) {
                this.world.sendEntityStatus(this, (byte)4);
                this.c = true;
            }
            if (--this.ticksLeft < 0) {
                this.remove();
            }
        }
    }
    
    private void damage(final LivingEntity target) {
        final LivingEntity livingEntity2 = this.getOwner();
        if (!target.isAlive() || target.isInvulnerable() || target == livingEntity2) {
            return;
        }
        if (livingEntity2 == null) {
            target.damage(DamageSource.MAGIC, 6.0f);
        }
        else {
            if (livingEntity2.isTeammate(target)) {
                return;
            }
            target.damage(DamageSource.magic(this, livingEntity2), 6.0f);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        super.handleStatus(status);
        if (status == 4) {
            this.hasAttacked = true;
            if (!this.isSilent()) {
                this.world.playSound(this.x, this.y, this.z, SoundEvents.cX, this.getSoundCategory(), 1.0f, this.random.nextFloat() * 0.2f + 0.85f, false);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public float getAnimationProgress(final float tickDelta) {
        if (!this.hasAttacked) {
            return 0.0f;
        }
        final int integer2 = this.ticksLeft - 2;
        if (integer2 <= 0) {
            return 1.0f;
        }
        return 1.0f - (integer2 - tickDelta) / 20.0f;
    }
    
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
