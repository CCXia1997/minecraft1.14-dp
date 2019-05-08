package net.minecraft.entity.passive;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.entity.LivingEntity;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SitGoal;
import java.util.UUID;
import java.util.Optional;
import net.minecraft.entity.data.TrackedData;

public abstract class TameableEntity extends AnimalEntity
{
    protected static final TrackedData<Byte> TAMEABLE_FLAGS;
    protected static final TrackedData<Optional<UUID>> OWNER_UUID;
    protected SitGoal sitGoal;
    
    protected TameableEntity(final EntityType<? extends TameableEntity> type, final World world) {
        super(type, world);
        this.onTamedChanged();
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Byte>startTracking(TameableEntity.TAMEABLE_FLAGS, (Byte)0);
        this.dataTracker.<Optional<UUID>>startTracking(TameableEntity.OWNER_UUID, Optional.<UUID>empty());
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.getOwnerUuid() == null) {
            tag.putString("OwnerUUID", "");
        }
        else {
            tag.putString("OwnerUUID", this.getOwnerUuid().toString());
        }
        tag.putBoolean("Sitting", this.isSitting());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        String string2;
        if (tag.containsKey("OwnerUUID", 8)) {
            string2 = tag.getString("OwnerUUID");
        }
        else {
            final String string3 = tag.getString("Owner");
            string2 = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string3);
        }
        if (!string2.isEmpty()) {
            try {
                this.setOwnerUuid(UUID.fromString(string2));
                this.setTamed(true);
            }
            catch (Throwable throwable3) {
                this.setTamed(false);
            }
        }
        if (this.sitGoal != null) {
            this.sitGoal.setEnabledWithOwner(tag.getBoolean("Sitting"));
        }
        this.setSitting(tag.getBoolean("Sitting"));
    }
    
    @Override
    public boolean canBeLeashedBy(final PlayerEntity player) {
        return !this.isLeashed();
    }
    
    protected void showEmoteParticle(final boolean positive) {
        ParticleParameters particleParameters2 = ParticleTypes.E;
        if (!positive) {
            particleParameters2 = ParticleTypes.Q;
        }
        for (int integer3 = 0; integer3 < 7; ++integer3) {
            final double double4 = this.random.nextGaussian() * 0.02;
            final double double5 = this.random.nextGaussian() * 0.02;
            final double double6 = this.random.nextGaussian() * 0.02;
            this.world.addParticle(particleParameters2, this.x + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), this.y + 0.5 + this.random.nextFloat() * this.getHeight(), this.z + this.random.nextFloat() * this.getWidth() * 2.0f - this.getWidth(), double4, double5, double6);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 7) {
            this.showEmoteParticle(true);
        }
        else if (status == 6) {
            this.showEmoteParticle(false);
        }
        else {
            super.handleStatus(status);
        }
    }
    
    public boolean isTamed() {
        return (this.dataTracker.<Byte>get(TameableEntity.TAMEABLE_FLAGS) & 0x4) != 0x0;
    }
    
    public void setTamed(final boolean boolean1) {
        final byte byte2 = this.dataTracker.<Byte>get(TameableEntity.TAMEABLE_FLAGS);
        if (boolean1) {
            this.dataTracker.<Byte>set(TameableEntity.TAMEABLE_FLAGS, (byte)(byte2 | 0x4));
        }
        else {
            this.dataTracker.<Byte>set(TameableEntity.TAMEABLE_FLAGS, (byte)(byte2 & 0xFFFFFFFB));
        }
        this.onTamedChanged();
    }
    
    protected void onTamedChanged() {
    }
    
    public boolean isSitting() {
        return (this.dataTracker.<Byte>get(TameableEntity.TAMEABLE_FLAGS) & 0x1) != 0x0;
    }
    
    public void setSitting(final boolean boolean1) {
        final byte byte2 = this.dataTracker.<Byte>get(TameableEntity.TAMEABLE_FLAGS);
        if (boolean1) {
            this.dataTracker.<Byte>set(TameableEntity.TAMEABLE_FLAGS, (byte)(byte2 | 0x1));
        }
        else {
            this.dataTracker.<Byte>set(TameableEntity.TAMEABLE_FLAGS, (byte)(byte2 & 0xFFFFFFFE));
        }
    }
    
    @Nullable
    public UUID getOwnerUuid() {
        return this.dataTracker.<Optional<UUID>>get(TameableEntity.OWNER_UUID).orElse(null);
    }
    
    public void setOwnerUuid(@Nullable final UUID uUID) {
        this.dataTracker.<Optional<UUID>>set(TameableEntity.OWNER_UUID, Optional.<UUID>ofNullable(uUID));
    }
    
    public void setOwner(final PlayerEntity playerEntity) {
        this.setTamed(true);
        this.setOwnerUuid(playerEntity.getUuid());
        if (playerEntity instanceof ServerPlayerEntity) {
            Criterions.TAME_ANIMAL.handle((ServerPlayerEntity)playerEntity, this);
        }
    }
    
    @Nullable
    public LivingEntity getOwner() {
        try {
            final UUID uUID1 = this.getOwnerUuid();
            if (uUID1 == null) {
                return null;
            }
            return this.world.getPlayerByUuid(uUID1);
        }
        catch (IllegalArgumentException illegalArgumentException1) {
            return null;
        }
    }
    
    @Override
    public boolean canTarget(final LivingEntity target) {
        return !this.isOwner(target) && super.canTarget(target);
    }
    
    public boolean isOwner(final LivingEntity livingEntity) {
        return livingEntity == this.getOwner();
    }
    
    public SitGoal getSitGoal() {
        return this.sitGoal;
    }
    
    public boolean canAttackWithOwner(final LivingEntity target, final LivingEntity owner) {
        return true;
    }
    
    @Override
    public AbstractTeam getScoreboardTeam() {
        if (this.isTamed()) {
            final LivingEntity livingEntity1 = this.getOwner();
            if (livingEntity1 != null) {
                return livingEntity1.getScoreboardTeam();
            }
        }
        return super.getScoreboardTeam();
    }
    
    @Override
    public boolean isTeammate(final Entity entity) {
        if (this.isTamed()) {
            final LivingEntity livingEntity2 = this.getOwner();
            if (entity == livingEntity2) {
                return true;
            }
            if (livingEntity2 != null) {
                return livingEntity2.isTeammate(entity);
            }
        }
        return super.isTeammate(entity);
    }
    
    @Override
    public void onDeath(final DamageSource damageSource) {
        if (!this.world.isClient && this.world.getGameRules().getBoolean("showDeathMessages") && this.getOwner() instanceof ServerPlayerEntity) {
            this.getOwner().sendMessage(this.getDamageTracker().getDeathMessage());
        }
        super.onDeath(damageSource);
    }
    
    static {
        TAMEABLE_FLAGS = DataTracker.<Byte>registerData(TameableEntity.class, TrackedDataHandlerRegistry.BYTE);
        OWNER_UUID = DataTracker.<Optional<UUID>>registerData(TameableEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    }
}
