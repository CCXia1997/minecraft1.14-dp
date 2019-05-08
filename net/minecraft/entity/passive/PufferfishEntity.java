package net.minecraft.entity.passive;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.sound.SoundEvent;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.damage.DamageSource;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.entity.data.TrackedData;

public class PufferfishEntity extends FishEntity
{
    private static final TrackedData<Integer> PUFF_STATE;
    private int c;
    private int d;
    private static final Predicate<LivingEntity> bz;
    
    public PufferfishEntity(final EntityType<? extends PufferfishEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Integer>startTracking(PufferfishEntity.PUFF_STATE, 0);
    }
    
    public int getPuffState() {
        return this.dataTracker.<Integer>get(PufferfishEntity.PUFF_STATE);
    }
    
    public void setPuffState(final int puffState) {
        this.dataTracker.<Integer>set(PufferfishEntity.PUFF_STATE, puffState);
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (PufferfishEntity.PUFF_STATE.equals(data)) {
            this.refreshSize();
        }
        super.onTrackedDataSet(data);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("PuffState", this.getPuffState());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setPuffState(tag.getInt("PuffState"));
    }
    
    @Override
    protected ItemStack getFishBucketItem() {
        return new ItemStack(Items.kH);
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new a(this));
    }
    
    @Override
    public void tick() {
        if (!this.world.isClient && this.isAlive() && this.canMoveVoluntarily()) {
            if (this.c > 0) {
                if (this.getPuffState() == 0) {
                    this.playSound(SoundEvents.jc, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(1);
                }
                else if (this.c > 40 && this.getPuffState() == 1) {
                    this.playSound(SoundEvents.jc, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(2);
                }
                ++this.c;
            }
            else if (this.getPuffState() != 0) {
                if (this.d > 60 && this.getPuffState() == 2) {
                    this.playSound(SoundEvents.jb, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(1);
                }
                else if (this.d > 100 && this.getPuffState() == 1) {
                    this.playSound(SoundEvents.jb, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(0);
                }
                ++this.d;
            }
        }
        super.tick();
    }
    
    @Override
    public void updateState() {
        super.updateState();
        if (this.isAlive() && this.getPuffState() > 0) {
            final List<MobEntity> list1 = this.world.<MobEntity>getEntities(MobEntity.class, this.getBoundingBox().expand(0.3), PufferfishEntity.bz);
            for (final MobEntity mobEntity3 : list1) {
                if (mobEntity3.isAlive()) {
                    this.sting(mobEntity3);
                }
            }
        }
    }
    
    private void sting(final MobEntity mob) {
        final int integer2 = this.getPuffState();
        if (mob.damage(DamageSource.mob(this), (float)(1 + integer2))) {
            mob.addPotionEffect(new StatusEffectInstance(StatusEffects.s, 60 * integer2, 0));
            this.playSound(SoundEvents.jg, 1.0f, 1.0f);
        }
    }
    
    @Override
    public void onPlayerCollision(final PlayerEntity playerEntity) {
        final int integer2 = this.getPuffState();
        if (playerEntity instanceof ServerPlayerEntity && integer2 > 0 && playerEntity.damage(DamageSource.mob(this), (float)(1 + integer2))) {
            ((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(new GameStateChangeS2CPacket(9, 0.0f));
            playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.s, 60 * integer2, 0));
        }
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ja;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.jd;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.jf;
    }
    
    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.je;
    }
    
    @Override
    public EntitySize getSize(final EntityPose entityPose) {
        return super.getSize(entityPose).scaled(getScaleForPuffState(this.getPuffState()));
    }
    
    private static float getScaleForPuffState(final int puffState) {
        switch (puffState) {
            case 1: {
                return 0.7f;
            }
            case 0: {
                return 0.5f;
            }
            default: {
                return 1.0f;
            }
        }
    }
    
    static {
        PUFF_STATE = DataTracker.<Integer>registerData(PufferfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
        bz = (livingEntity -> {
            if (livingEntity == null) {
                return false;
            }
            else if (livingEntity instanceof PlayerEntity && (livingEntity.isSpectator() || livingEntity.isCreative())) {
                return false;
            }
            else {
                return livingEntity.getGroup() != EntityGroup.AQUATIC;
            }
        });
    }
    
    static class a extends Goal
    {
        private final PufferfishEntity a;
        
        public a(final PufferfishEntity pufferfishEntity) {
            this.a = pufferfishEntity;
        }
        
        @Override
        public boolean canStart() {
            final List<LivingEntity> list1 = this.a.world.<LivingEntity>getEntities(LivingEntity.class, this.a.getBoundingBox().expand(2.0), PufferfishEntity.bz);
            return !list1.isEmpty();
        }
        
        @Override
        public void start() {
            this.a.c = 1;
            this.a.d = 0;
        }
        
        @Override
        public void stop() {
            this.a.c = 0;
        }
        
        @Override
        public boolean shouldContinue() {
            final List<LivingEntity> list1 = this.a.world.<LivingEntity>getEntities(LivingEntity.class, this.a.getBoundingBox().expand(2.0), PufferfishEntity.bz);
            return !list1.isEmpty();
        }
    }
}
