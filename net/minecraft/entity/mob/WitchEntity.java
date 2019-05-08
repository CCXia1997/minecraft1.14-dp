package net.minecraft.entity.mob;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.potion.Potion;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.ItemProvider;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potions;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.tag.FluidTags;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.PotionUtil;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.DisableableFollowTargetGoal;
import net.minecraft.entity.ai.goal.RaidGoal;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import java.util.UUID;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.raid.RaiderEntity;

public class WitchEntity extends RaiderEntity implements RangedAttacker
{
    private static final UUID DRINKING_SPEED_PENALTY_MODIFIER_ID;
    private static final EntityAttributeModifier DRINKING_SPEED_PENALTY_MODIFIER;
    private static final TrackedData<Boolean> DRINKING;
    private int drinkTimeLeft;
    private RaidGoal<RaiderEntity> raidGoal;
    private DisableableFollowTargetGoal<PlayerEntity> attackPlayerGoal;
    
    public WitchEntity(final EntityType<? extends WitchEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.raidGoal = new RaidGoal<RaiderEntity>(this, RaiderEntity.class, true, livingEntity -> livingEntity != null && this.hasActiveRaid() && livingEntity.getType() != EntityType.WITCH);
        this.attackPlayerGoal = new DisableableFollowTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, null);
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 60, 10.0f));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[] { RaiderEntity.class }));
        this.targetSelector.add(2, this.raidGoal);
        this.targetSelector.add(3, this.attackPlayerGoal);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().<Boolean>startTracking(WitchEntity.DRINKING, false);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ng;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.nk;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ni;
    }
    
    public void setDrinking(final boolean drinking) {
        this.getDataTracker().<Boolean>set(WitchEntity.DRINKING, drinking);
    }
    
    public boolean isDrinking() {
        return this.getDataTracker().<Boolean>get(WitchEntity.DRINKING);
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(26.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }
    
    @Override
    public void updateState() {
        if (!this.world.isClient && this.isAlive()) {
            this.raidGoal.decreaseCooldown();
            if (this.raidGoal.getCooldown() <= 0) {
                this.attackPlayerGoal.setEnabled(true);
            }
            else {
                this.attackPlayerGoal.setEnabled(false);
            }
            if (this.isDrinking()) {
                if (this.drinkTimeLeft-- <= 0) {
                    this.setDrinking(false);
                    final ItemStack itemStack1 = this.getMainHandStack();
                    this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
                    if (itemStack1.getItem() == Items.ml) {
                        final List<StatusEffectInstance> list2 = PotionUtil.getPotionEffects(itemStack1);
                        if (list2 != null) {
                            for (final StatusEffectInstance statusEffectInstance4 : list2) {
                                this.addPotionEffect(new StatusEffectInstance(statusEffectInstance4));
                            }
                        }
                    }
                    this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).removeModifier(WitchEntity.DRINKING_SPEED_PENALTY_MODIFIER);
                }
            }
            else {
                Potion potion1 = null;
                if (this.random.nextFloat() < 0.15f && this.isInFluid(FluidTags.a) && !this.hasStatusEffect(StatusEffects.m)) {
                    potion1 = Potions.x;
                }
                else if (this.random.nextFloat() < 0.15f && (this.isOnFire() || (this.getRecentDamageSource() != null && this.getRecentDamageSource().isFire())) && !this.hasStatusEffect(StatusEffects.l)) {
                    potion1 = Potions.m;
                }
                else if (this.random.nextFloat() < 0.05f && this.getHealth() < this.getHealthMaximum()) {
                    potion1 = Potions.z;
                }
                else if (this.random.nextFloat() < 0.5f && this.getTarget() != null && !this.hasStatusEffect(StatusEffects.a) && this.getTarget().squaredDistanceTo(this) > 121.0) {
                    potion1 = Potions.o;
                }
                if (potion1 != null) {
                    this.setEquippedStack(EquipmentSlot.HAND_MAIN, PotionUtil.setPotion(new ItemStack(Items.ml), potion1));
                    this.drinkTimeLeft = this.getMainHandStack().getMaxUseTime();
                    this.setDrinking(true);
                    this.world.playSound(null, this.x, this.y, this.z, SoundEvents.nj, this.getSoundCategory(), 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
                    final EntityAttributeInstance entityAttributeInstance2 = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
                    entityAttributeInstance2.removeModifier(WitchEntity.DRINKING_SPEED_PENALTY_MODIFIER);
                    entityAttributeInstance2.addModifier(WitchEntity.DRINKING_SPEED_PENALTY_MODIFIER);
                }
            }
            if (this.random.nextFloat() < 7.5E-4f) {
                this.world.sendEntityStatus(this, (byte)15);
            }
        }
        super.updateState();
    }
    
    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.nh;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 15) {
            for (int integer2 = 0; integer2 < this.random.nextInt(35) + 10; ++integer2) {
                this.world.addParticle(ParticleTypes.Y, this.x + this.random.nextGaussian() * 0.12999999523162842, this.getBoundingBox().maxY + 0.5 + this.random.nextGaussian() * 0.12999999523162842, this.z + this.random.nextGaussian() * 0.12999999523162842, 0.0, 0.0, 0.0);
            }
        }
        else {
            super.handleStatus(status);
        }
    }
    
    @Override
    protected float applyEnchantmentsToDamage(final DamageSource source, float amount) {
        amount = super.applyEnchantmentsToDamage(source, amount);
        if (source.getAttacker() == this) {
            amount = 0.0f;
        }
        if (source.getMagic()) {
            amount *= (float)0.15;
        }
        return amount;
    }
    
    @Override
    public void attack(final LivingEntity target, final float float2) {
        if (this.isDrinking()) {
            return;
        }
        final Vec3d vec3d3 = target.getVelocity();
        final double double4 = target.x + vec3d3.x - this.x;
        final double double5 = target.y + target.getStandingEyeHeight() - 1.100000023841858 - this.y;
        final double double6 = target.z + vec3d3.z - this.z;
        final float float3 = MathHelper.sqrt(double4 * double4 + double6 * double6);
        Potion potion11 = Potions.B;
        if (target instanceof RaiderEntity) {
            if (target.getHealth() <= 4.0f) {
                potion11 = Potions.z;
            }
            else {
                potion11 = Potions.G;
            }
            this.setTarget(null);
        }
        else if (float3 >= 8.0f && !target.hasStatusEffect(StatusEffects.b)) {
            potion11 = Potions.r;
        }
        else if (target.getHealth() >= 8.0f && !target.hasStatusEffect(StatusEffects.s)) {
            potion11 = Potions.D;
        }
        else if (float3 <= 3.0f && !target.hasStatusEffect(StatusEffects.r) && this.random.nextFloat() < 0.25f) {
            potion11 = Potions.M;
        }
        final ThrownPotionEntity thrownPotionEntity12 = new ThrownPotionEntity(this.world, this);
        thrownPotionEntity12.setItemStack(PotionUtil.setPotion(new ItemStack(Items.oS), potion11));
        final ThrownPotionEntity thrownPotionEntity13 = thrownPotionEntity12;
        thrownPotionEntity13.pitch += 20.0f;
        thrownPotionEntity12.setVelocity(double4, double5 + float3 * 0.2f, double6, 0.75f, 8.0f);
        this.world.playSound(null, this.x, this.y, this.z, SoundEvents.nl, this.getSoundCategory(), 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
        this.world.spawnEntity(thrownPotionEntity12);
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 1.62f;
    }
    
    @Override
    public void addBonusForWave(final int wave, final boolean boolean2) {
    }
    
    @Override
    public boolean canLead() {
        return false;
    }
    
    static {
        DRINKING_SPEED_PENALTY_MODIFIER_ID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
        DRINKING_SPEED_PENALTY_MODIFIER = new EntityAttributeModifier(WitchEntity.DRINKING_SPEED_PENALTY_MODIFIER_ID, "Drinking speed penalty", -0.25, EntityAttributeModifier.Operation.a).setSerialize(false);
        DRINKING = DataTracker.<Boolean>registerData(WitchEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
