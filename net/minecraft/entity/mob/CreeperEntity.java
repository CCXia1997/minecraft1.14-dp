package net.minecraft.entity.mob;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.entity.LightningEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;

public class CreeperEntity extends HostileEntity
{
    private static final TrackedData<Integer> FUSE_SPEED;
    private static final TrackedData<Boolean> CHARGED;
    private static final TrackedData<Boolean> IGNITED;
    private int lastFuseTime;
    private int currentFuseTime;
    private int fuseTime;
    private int explosionRadius;
    private int headsDropped;
    
    public CreeperEntity(final EntityType<? extends CreeperEntity> type, final World world) {
        super(type, world);
        this.fuseTime = 30;
        this.explosionRadius = 3;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new CreeperIgniteGoal(this));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, OcelotEntity.class, 6.0f, 1.0, 1.2));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, CatEntity.class, 6.0f, 1.0, 1.2));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }
    
    @Override
    public int getSafeFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        }
        return 3 + (int)(this.getHealth() - 1.0f);
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
        super.handleFallDamage(fallDistance, damageMultiplier);
        this.currentFuseTime += (int)(fallDistance * 1.5f);
        if (this.currentFuseTime > this.fuseTime - 5) {
            this.currentFuseTime = this.fuseTime - 5;
        }
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Integer>startTracking(CreeperEntity.FUSE_SPEED, -1);
        this.dataTracker.<Boolean>startTracking(CreeperEntity.CHARGED, false);
        this.dataTracker.<Boolean>startTracking(CreeperEntity.IGNITED, false);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.dataTracker.<Boolean>get(CreeperEntity.CHARGED)) {
            tag.putBoolean("powered", true);
        }
        tag.putShort("Fuse", (short)this.fuseTime);
        tag.putByte("ExplosionRadius", (byte)this.explosionRadius);
        tag.putBoolean("ignited", this.getIgnited());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.dataTracker.<Boolean>set(CreeperEntity.CHARGED, tag.getBoolean("powered"));
        if (tag.containsKey("Fuse", 99)) {
            this.fuseTime = tag.getShort("Fuse");
        }
        if (tag.containsKey("ExplosionRadius", 99)) {
            this.explosionRadius = tag.getByte("ExplosionRadius");
        }
        if (tag.getBoolean("ignited")) {
            this.setIgnited();
        }
    }
    
    @Override
    public void tick() {
        if (this.isAlive()) {
            this.lastFuseTime = this.currentFuseTime;
            if (this.getIgnited()) {
                this.setFuseSpeed(1);
            }
            final int integer1 = this.getFuseSpeed();
            if (integer1 > 0 && this.currentFuseTime == 0) {
                this.playSound(SoundEvents.bx, 1.0f, 0.5f);
            }
            this.currentFuseTime += integer1;
            if (this.currentFuseTime < 0) {
                this.currentFuseTime = 0;
            }
            if (this.currentFuseTime >= this.fuseTime) {
                this.currentFuseTime = this.fuseTime;
                this.explode();
            }
        }
        super.tick();
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.bw;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.bv;
    }
    
    @Override
    protected void dropEquipment(final DamageSource damageSource, final int addedDropChance, final boolean dropAllowed) {
        super.dropEquipment(damageSource, addedDropChance, dropAllowed);
        final Entity entity4 = damageSource.getAttacker();
        if (entity4 != this && entity4 instanceof CreeperEntity) {
            final CreeperEntity creeperEntity5 = (CreeperEntity)entity4;
            if (creeperEntity5.shouldDropHead()) {
                creeperEntity5.onHeadDropped();
                this.dropItem(Items.CREEPER_HEAD);
            }
        }
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        return true;
    }
    
    public boolean isCharged() {
        return this.dataTracker.<Boolean>get(CreeperEntity.CHARGED);
    }
    
    @Environment(EnvType.CLIENT)
    public float getClientFuseTime(final float timeDelta) {
        return MathHelper.lerp(timeDelta, (float)this.lastFuseTime, (float)this.currentFuseTime) / (this.fuseTime - 2);
    }
    
    public int getFuseSpeed() {
        return this.dataTracker.<Integer>get(CreeperEntity.FUSE_SPEED);
    }
    
    public void setFuseSpeed(final int fuseSpeed) {
        this.dataTracker.<Integer>set(CreeperEntity.FUSE_SPEED, fuseSpeed);
    }
    
    @Override
    public void onStruckByLightning(final LightningEntity lightning) {
        super.onStruckByLightning(lightning);
        this.dataTracker.<Boolean>set(CreeperEntity.CHARGED, true);
    }
    
    @Override
    protected boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (itemStack3.getItem() == Items.jd) {
            this.world.playSound(player, this.x, this.y, this.z, SoundEvents.ds, this.getSoundCategory(), 1.0f, this.random.nextFloat() * 0.4f + 0.8f);
            player.swingHand(hand);
            if (!this.world.isClient) {
                this.setIgnited();
                itemStack3.<PlayerEntity>applyDamage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
                return true;
            }
        }
        return super.interactMob(player, hand);
    }
    
    private void explode() {
        if (!this.world.isClient) {
            final Explosion.DestructionType destructionType1 = this.world.getGameRules().getBoolean("mobGriefing") ? Explosion.DestructionType.c : Explosion.DestructionType.a;
            final float float2 = this.isCharged() ? 2.0f : 1.0f;
            this.dead = true;
            this.world.createExplosion(this, this.x, this.y, this.z, this.explosionRadius * float2, destructionType1);
            this.remove();
            this.spawnEffectsCloud();
        }
    }
    
    private void spawnEffectsCloud() {
        final Collection<StatusEffectInstance> collection1 = this.getStatusEffects();
        if (!collection1.isEmpty()) {
            final AreaEffectCloudEntity areaEffectCloudEntity2 = new AreaEffectCloudEntity(this.world, this.x, this.y, this.z);
            areaEffectCloudEntity2.setRadius(2.5f);
            areaEffectCloudEntity2.setRadiusStart(-0.5f);
            areaEffectCloudEntity2.setWaitTime(10);
            areaEffectCloudEntity2.setDuration(areaEffectCloudEntity2.getDuration() / 2);
            areaEffectCloudEntity2.setRadiusGrowth(-areaEffectCloudEntity2.getRadius() / areaEffectCloudEntity2.getDuration());
            for (final StatusEffectInstance statusEffectInstance4 : collection1) {
                areaEffectCloudEntity2.addEffect(new StatusEffectInstance(statusEffectInstance4));
            }
            this.world.spawnEntity(areaEffectCloudEntity2);
        }
    }
    
    public boolean getIgnited() {
        return this.dataTracker.<Boolean>get(CreeperEntity.IGNITED);
    }
    
    public void setIgnited() {
        this.dataTracker.<Boolean>set(CreeperEntity.IGNITED, true);
    }
    
    public boolean shouldDropHead() {
        return this.isCharged() && this.headsDropped < 1;
    }
    
    public void onHeadDropped() {
        ++this.headsDropped;
    }
    
    static {
        FUSE_SPEED = DataTracker.<Integer>registerData(CreeperEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CHARGED = DataTracker.<Boolean>registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        IGNITED = DataTracker.<Boolean>registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
