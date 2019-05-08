package net.minecraft.block.entity;

import net.minecraft.block.Blocks;
import net.minecraft.sound.SoundEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Random;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.entity.mob.Monster;
import java.util.Iterator;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Lists;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.Tickable;

public class ConduitBlockEntity extends BlockEntity implements Tickable
{
    private static final Block[] ACTIVATING_BLOCKS;
    public int ticks;
    private float ticksActive;
    private boolean active;
    private boolean eyeOpen;
    private final List<BlockPos> activatingBlocks;
    @Nullable
    private LivingEntity targetEntity;
    @Nullable
    private UUID targetUuid;
    private long nextAmbientSoundTime;
    
    public ConduitBlockEntity() {
        this(BlockEntityType.CONDUIT);
    }
    
    public ConduitBlockEntity(final BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
        this.activatingBlocks = Lists.newArrayList();
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        if (compoundTag.containsKey("target_uuid")) {
            this.targetUuid = TagHelper.deserializeUuid(compoundTag.getCompound("target_uuid"));
        }
        else {
            this.targetUuid = null;
        }
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (this.targetEntity != null) {
            compoundTag.put("target_uuid", TagHelper.serializeUuid(this.targetEntity.getUuid()));
        }
        return compoundTag;
    }
    
    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 5, this.toInitialChunkDataTag());
    }
    
    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }
    
    @Override
    public void tick() {
        ++this.ticks;
        final long long1 = this.world.getTime();
        if (long1 % 40L == 0L) {
            this.setActive(this.updateActivatingBlocks());
            if (!this.world.isClient && this.isActive()) {
                this.givePlayersEffects();
                this.attackHostileEntity();
            }
        }
        if (long1 % 80L == 0L && this.isActive()) {
            this.playSound(SoundEvents.bm);
        }
        if (long1 > this.nextAmbientSoundTime && this.isActive()) {
            this.nextAmbientSoundTime = long1 + 60L + this.world.getRandom().nextInt(40);
            this.playSound(SoundEvents.bn);
        }
        if (this.world.isClient) {
            this.updateTargetEntity();
            this.spawnNautilusParticles();
            if (this.isActive()) {
                ++this.ticksActive;
            }
        }
    }
    
    private boolean updateActivatingBlocks() {
        this.activatingBlocks.clear();
        for (int integer1 = -1; integer1 <= 1; ++integer1) {
            for (int integer2 = -1; integer2 <= 1; ++integer2) {
                for (int integer3 = -1; integer3 <= 1; ++integer3) {
                    final BlockPos blockPos4 = this.pos.add(integer1, integer2, integer3);
                    if (!this.world.isWaterAt(blockPos4)) {
                        return false;
                    }
                }
            }
        }
        for (int integer1 = -2; integer1 <= 2; ++integer1) {
            for (int integer2 = -2; integer2 <= 2; ++integer2) {
                for (int integer3 = -2; integer3 <= 2; ++integer3) {
                    final int integer4 = Math.abs(integer1);
                    final int integer5 = Math.abs(integer2);
                    final int integer6 = Math.abs(integer3);
                    if (integer4 > 1 || integer5 > 1 || integer6 > 1) {
                        if ((integer1 == 0 && (integer5 == 2 || integer6 == 2)) || (integer2 == 0 && (integer4 == 2 || integer6 == 2)) || (integer3 == 0 && (integer4 == 2 || integer5 == 2))) {
                            final BlockPos blockPos5 = this.pos.add(integer1, integer2, integer3);
                            final BlockState blockState8 = this.world.getBlockState(blockPos5);
                            for (final Block block12 : ConduitBlockEntity.ACTIVATING_BLOCKS) {
                                if (blockState8.getBlock() == block12) {
                                    this.activatingBlocks.add(blockPos5);
                                }
                            }
                        }
                    }
                }
            }
        }
        this.setEyeOpen(this.activatingBlocks.size() >= 42);
        return this.activatingBlocks.size() >= 16;
    }
    
    private void givePlayersEffects() {
        final int integer1 = this.activatingBlocks.size();
        final int integer2 = integer1 / 7 * 16;
        final int integer3 = this.pos.getX();
        final int integer4 = this.pos.getY();
        final int integer5 = this.pos.getZ();
        final BoundingBox boundingBox6 = new BoundingBox(integer3, integer4, integer5, integer3 + 1, integer4 + 1, integer5 + 1).expand(integer2).stretch(0.0, this.world.getHeight(), 0.0);
        final List<PlayerEntity> list7 = this.world.<PlayerEntity>getEntities(PlayerEntity.class, boundingBox6);
        if (list7.isEmpty()) {
            return;
        }
        for (final PlayerEntity playerEntity9 : list7) {
            if (this.pos.isWithinDistance(new BlockPos(playerEntity9), integer2) && playerEntity9.isInsideWaterOrRain()) {
                playerEntity9.addPotionEffect(new StatusEffectInstance(StatusEffects.C, 260, 0, true, true));
            }
        }
    }
    
    private void attackHostileEntity() {
        final LivingEntity livingEntity2 = this.targetEntity;
        final int integer2 = this.activatingBlocks.size();
        if (integer2 < 42) {
            this.targetEntity = null;
        }
        else if (this.targetEntity == null && this.targetUuid != null) {
            this.targetEntity = this.findTargetEntity();
            this.targetUuid = null;
        }
        else if (this.targetEntity == null) {
            final List<LivingEntity> list3 = this.world.<LivingEntity>getEntities(LivingEntity.class, this.getAttackZone(), livingEntity -> livingEntity instanceof Monster && livingEntity.isInsideWaterOrRain());
            if (!list3.isEmpty()) {
                this.targetEntity = list3.get(this.world.random.nextInt(list3.size()));
            }
        }
        else if (!this.targetEntity.isAlive() || !this.pos.isWithinDistance(new BlockPos(this.targetEntity), 8.0)) {
            this.targetEntity = null;
        }
        if (this.targetEntity != null) {
            this.world.playSound(null, this.targetEntity.x, this.targetEntity.y, this.targetEntity.z, SoundEvents.bo, SoundCategory.e, 1.0f, 1.0f);
            this.targetEntity.damage(DamageSource.MAGIC, 4.0f);
        }
        if (livingEntity2 != this.targetEntity) {
            final BlockState blockState3 = this.getCachedState();
            this.world.updateListeners(this.pos, blockState3, blockState3, 2);
        }
    }
    
    private void updateTargetEntity() {
        if (this.targetUuid == null) {
            this.targetEntity = null;
        }
        else if (this.targetEntity == null || !this.targetEntity.getUuid().equals(this.targetUuid)) {
            this.targetEntity = this.findTargetEntity();
            if (this.targetEntity == null) {
                this.targetUuid = null;
            }
        }
    }
    
    private BoundingBox getAttackZone() {
        final int integer1 = this.pos.getX();
        final int integer2 = this.pos.getY();
        final int integer3 = this.pos.getZ();
        return new BoundingBox(integer1, integer2, integer3, integer1 + 1, integer2 + 1, integer3 + 1).expand(8.0);
    }
    
    @Nullable
    private LivingEntity findTargetEntity() {
        final List<LivingEntity> list1 = this.world.<LivingEntity>getEntities(LivingEntity.class, this.getAttackZone(), livingEntity -> livingEntity.getUuid().equals(this.targetUuid));
        if (list1.size() == 1) {
            return list1.get(0);
        }
        return null;
    }
    
    private void spawnNautilusParticles() {
        final Random random1 = this.world.random;
        float float2 = MathHelper.sin((this.ticks + 35) * 0.1f) / 2.0f + 0.5f;
        float2 = (float2 * float2 + float2) * 0.3f;
        final Vec3d vec3d3 = new Vec3d(this.pos.getX() + 0.5f, this.pos.getY() + 1.5f + float2, this.pos.getZ() + 0.5f);
        for (final BlockPos blockPos5 : this.activatingBlocks) {
            if (random1.nextInt(50) != 0) {
                continue;
            }
            final float float3 = -0.5f + random1.nextFloat();
            final float float4 = -2.0f + random1.nextFloat();
            final float float5 = -0.5f + random1.nextFloat();
            final BlockPos blockPos6 = blockPos5.subtract(this.pos);
            final Vec3d vec3d4 = new Vec3d(float3, float4, float5).add(blockPos6.getX(), blockPos6.getY(), blockPos6.getZ());
            this.world.addParticle(ParticleTypes.ac, vec3d3.x, vec3d3.y, vec3d3.z, vec3d4.x, vec3d4.y, vec3d4.z);
        }
        if (this.targetEntity != null) {
            final Vec3d vec3d5 = new Vec3d(this.targetEntity.x, this.targetEntity.y + this.targetEntity.getStandingEyeHeight(), this.targetEntity.z);
            final float float6 = (-0.5f + random1.nextFloat()) * (3.0f + this.targetEntity.getWidth());
            final float float3 = -1.0f + random1.nextFloat() * this.targetEntity.getHeight();
            final float float4 = (-0.5f + random1.nextFloat()) * (3.0f + this.targetEntity.getWidth());
            final Vec3d vec3d6 = new Vec3d(float6, float3, float4);
            this.world.addParticle(ParticleTypes.ac, vec3d5.x, vec3d5.y, vec3d5.z, vec3d6.x, vec3d6.y, vec3d6.z);
        }
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isEyeOpen() {
        return this.eyeOpen;
    }
    
    private void setActive(final boolean active) {
        if (active != this.active) {
            this.playSound(active ? SoundEvents.bl : SoundEvents.bp);
        }
        this.active = active;
    }
    
    private void setEyeOpen(final boolean eyeOpen) {
        this.eyeOpen = eyeOpen;
    }
    
    @Environment(EnvType.CLIENT)
    public float getRotation(final float tickDelta) {
        return (this.ticksActive + tickDelta) * -0.0375f;
    }
    
    public void playSound(final SoundEvent soundEvent) {
        this.world.playSound(null, this.pos, soundEvent, SoundCategory.e, 1.0f, 1.0f);
    }
    
    static {
        ACTIVATING_BLOCKS = new Block[] { Blocks.gi, Blocks.gj, Blocks.gr, Blocks.gk };
    }
}
