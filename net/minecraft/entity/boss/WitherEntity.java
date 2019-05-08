package net.minecraft.entity.boss;

import java.util.EnumSet;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ExplodingWitherSkullEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.world.Difficulty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.entity.ai.TargetPredicate;
import java.util.List;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.mob.HostileEntity;

public class WitherEntity extends HostileEntity implements RangedAttacker
{
    private static final TrackedData<Integer> TRACKED_ENTITY_ID_1;
    private static final TrackedData<Integer> TRACKED_ENTITY_ID_2;
    private static final TrackedData<Integer> TRACKED_ENTITY_ID_3;
    private static final List<TrackedData<Integer>> bz;
    private static final TrackedData<Integer> INVUL_TIMER;
    private static final TargetPredicate HEAD_TARGET_PREDICATE;
    private final float[] bC;
    private final float[] bD;
    private final float[] bE;
    private final float[] bF;
    private final int[] bG;
    private final int[] bH;
    private int bI;
    private final ServerBossBar bJ;
    private static final Predicate<LivingEntity> bK;
    
    public WitherEntity(final EntityType<? extends WitherEntity> type, final World world) {
        super(type, world);
        this.bC = new float[2];
        this.bD = new float[2];
        this.bE = new float[2];
        this.bF = new float[2];
        this.bG = new int[2];
        this.bH = new int[2];
        this.bJ = (ServerBossBar)new ServerBossBar(this.getDisplayName(), BossBar.Color.f, BossBar.Style.a).setDarkenSky(true);
        this.setHealth(this.getHealthMaximum());
        this.getNavigation().setCanSwim(true);
        this.experiencePoints = 50;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new a());
        this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 40, 20.0f));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, MobEntity.class, 0, false, false, WitherEntity.bK));
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Integer>startTracking(WitherEntity.TRACKED_ENTITY_ID_1, 0);
        this.dataTracker.<Integer>startTracking(WitherEntity.TRACKED_ENTITY_ID_2, 0);
        this.dataTracker.<Integer>startTracking(WitherEntity.TRACKED_ENTITY_ID_3, 0);
        this.dataTracker.<Integer>startTracking(WitherEntity.INVUL_TIMER, 0);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Invul", this.getInvulTimer());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setInvulTimer(tag.getInt("Invul"));
        if (this.hasCustomName()) {
            this.bJ.setName(this.getDisplayName());
        }
    }
    
    @Override
    public void setCustomName(@Nullable final TextComponent textComponent) {
        super.setCustomName(textComponent);
        this.bJ.setName(this.getDisplayName());
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.nm;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.np;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.no;
    }
    
    @Override
    public void updateState() {
        Vec3d vec3d1 = this.getVelocity().multiply(1.0, 0.6, 1.0);
        if (!this.world.isClient && this.getTrackedEntityId(0) > 0) {
            final Entity entity2 = this.world.getEntityById(this.getTrackedEntityId(0));
            if (entity2 != null) {
                double double3 = vec3d1.y;
                if (this.y < entity2.y || (!this.isAtHalfHealth() && this.y < entity2.y + 5.0)) {
                    double3 = Math.max(0.0, double3);
                    double3 += 0.3 - double3 * 0.6000000238418579;
                }
                vec3d1 = new Vec3d(vec3d1.x, double3, vec3d1.z);
                final Vec3d vec3d2 = new Vec3d(entity2.x - this.x, 0.0, entity2.z - this.z);
                if (Entity.squaredHorizontalLength(vec3d2) > 9.0) {
                    final Vec3d vec3d3 = vec3d2.normalize();
                    vec3d1 = vec3d1.add(vec3d3.x * 0.3 - vec3d1.x * 0.6, 0.0, vec3d3.z * 0.3 - vec3d1.z * 0.6);
                }
            }
        }
        this.setVelocity(vec3d1);
        if (Entity.squaredHorizontalLength(vec3d1) > 0.05) {
            this.yaw = (float)MathHelper.atan2(vec3d1.z, vec3d1.x) * 57.295776f - 90.0f;
        }
        super.updateState();
        for (int integer2 = 0; integer2 < 2; ++integer2) {
            this.bF[integer2] = this.bD[integer2];
            this.bE[integer2] = this.bC[integer2];
        }
        for (int integer2 = 0; integer2 < 2; ++integer2) {
            final int integer3 = this.getTrackedEntityId(integer2 + 1);
            Entity entity3 = null;
            if (integer3 > 0) {
                entity3 = this.world.getEntityById(integer3);
            }
            if (entity3 != null) {
                final double double4 = this.s(integer2 + 1);
                final double double5 = this.t(integer2 + 1);
                final double double6 = this.u(integer2 + 1);
                final double double7 = entity3.x - double4;
                final double double8 = entity3.y + entity3.getStandingEyeHeight() - double5;
                final double double9 = entity3.z - double6;
                final double double10 = MathHelper.sqrt(double7 * double7 + double9 * double9);
                final float float19 = (float)(MathHelper.atan2(double9, double7) * 57.2957763671875) - 90.0f;
                final float float20 = (float)(-(MathHelper.atan2(double8, double10) * 57.2957763671875));
                this.bC[integer2] = this.a(this.bC[integer2], float20, 40.0f);
                this.bD[integer2] = this.a(this.bD[integer2], float19, 10.0f);
            }
            else {
                this.bD[integer2] = this.a(this.bD[integer2], this.aK, 10.0f);
            }
        }
        final boolean boolean2 = this.isAtHalfHealth();
        for (int integer3 = 0; integer3 < 3; ++integer3) {
            final double double11 = this.s(integer3);
            final double double12 = this.t(integer3);
            final double double13 = this.u(integer3);
            this.world.addParticle(ParticleTypes.Q, double11 + this.random.nextGaussian() * 0.30000001192092896, double12 + this.random.nextGaussian() * 0.30000001192092896, double13 + this.random.nextGaussian() * 0.30000001192092896, 0.0, 0.0, 0.0);
            if (boolean2 && this.world.random.nextInt(4) == 0) {
                this.world.addParticle(ParticleTypes.u, double11 + this.random.nextGaussian() * 0.30000001192092896, double12 + this.random.nextGaussian() * 0.30000001192092896, double13 + this.random.nextGaussian() * 0.30000001192092896, 0.699999988079071, 0.699999988079071, 0.5);
            }
        }
        if (this.getInvulTimer() > 0) {
            for (int integer3 = 0; integer3 < 3; ++integer3) {
                this.world.addParticle(ParticleTypes.u, this.x + this.random.nextGaussian(), this.y + this.random.nextFloat() * 3.3f, this.z + this.random.nextGaussian(), 0.699999988079071, 0.699999988079071, 0.8999999761581421);
            }
        }
    }
    
    @Override
    protected void mobTick() {
        if (this.getInvulTimer() > 0) {
            final int integer1 = this.getInvulTimer() - 1;
            if (integer1 <= 0) {
                final Explosion.DestructionType destructionType2 = this.world.getGameRules().getBoolean("mobGriefing") ? Explosion.DestructionType.c : Explosion.DestructionType.a;
                this.world.createExplosion(this, this.x, this.y + this.getStandingEyeHeight(), this.z, 7.0f, false, destructionType2);
                this.world.playGlobalEvent(1023, new BlockPos(this), 0);
            }
            this.setInvulTimer(integer1);
            if (this.age % 10 == 0) {
                this.heal(10.0f);
            }
            return;
        }
        super.mobTick();
        for (int integer1 = 1; integer1 < 3; ++integer1) {
            if (this.age >= this.bG[integer1 - 1]) {
                this.bG[integer1 - 1] = this.age + 10 + this.random.nextInt(10);
                if ((this.world.getDifficulty() == Difficulty.NORMAL || this.world.getDifficulty() == Difficulty.HARD) && this.bH[integer1 - 1]++ > 15) {
                    final float float2 = 10.0f;
                    final float float3 = 5.0f;
                    final double double4 = MathHelper.nextDouble(this.random, this.x - 10.0, this.x + 10.0);
                    final double double5 = MathHelper.nextDouble(this.random, this.y - 5.0, this.y + 5.0);
                    final double double6 = MathHelper.nextDouble(this.random, this.z - 10.0, this.z + 10.0);
                    this.a(integer1 + 1, double4, double5, double6, true);
                    this.bH[integer1 - 1] = 0;
                }
                final int integer2 = this.getTrackedEntityId(integer1);
                if (integer2 > 0) {
                    final Entity entity3 = this.world.getEntityById(integer2);
                    if (entity3 == null || !entity3.isAlive() || this.squaredDistanceTo(entity3) > 900.0 || !this.canSee(entity3)) {
                        this.setTrackedEntityId(integer1, 0);
                    }
                    else if (entity3 instanceof PlayerEntity && ((PlayerEntity)entity3).abilities.invulnerable) {
                        this.setTrackedEntityId(integer1, 0);
                    }
                    else {
                        this.a(integer1 + 1, (LivingEntity)entity3);
                        this.bG[integer1 - 1] = this.age + 40 + this.random.nextInt(20);
                        this.bH[integer1 - 1] = 0;
                    }
                }
                else {
                    final List<LivingEntity> list3 = this.world.<LivingEntity>getTargets(LivingEntity.class, WitherEntity.HEAD_TARGET_PREDICATE, (LivingEntity)this, this.getBoundingBox().expand(20.0, 8.0, 20.0));
                    int integer3 = 0;
                    while (integer3 < 10 && !list3.isEmpty()) {
                        final LivingEntity livingEntity5 = list3.get(this.random.nextInt(list3.size()));
                        if (livingEntity5 != this && livingEntity5.isAlive() && this.canSee(livingEntity5)) {
                            if (!(livingEntity5 instanceof PlayerEntity)) {
                                this.setTrackedEntityId(integer1, livingEntity5.getEntityId());
                                break;
                            }
                            if (!((PlayerEntity)livingEntity5).abilities.invulnerable) {
                                this.setTrackedEntityId(integer1, livingEntity5.getEntityId());
                                break;
                            }
                            break;
                        }
                        else {
                            list3.remove(livingEntity5);
                            ++integer3;
                        }
                    }
                }
            }
        }
        if (this.getTarget() != null) {
            this.setTrackedEntityId(0, this.getTarget().getEntityId());
        }
        else {
            this.setTrackedEntityId(0, 0);
        }
        if (this.bI > 0) {
            --this.bI;
            if (this.bI == 0 && this.world.getGameRules().getBoolean("mobGriefing")) {
                final int integer1 = MathHelper.floor(this.y);
                final int integer2 = MathHelper.floor(this.x);
                final int integer4 = MathHelper.floor(this.z);
                boolean boolean4 = false;
                for (int integer5 = -1; integer5 <= 1; ++integer5) {
                    for (int integer6 = -1; integer6 <= 1; ++integer6) {
                        for (int integer7 = 0; integer7 <= 3; ++integer7) {
                            final int integer8 = integer2 + integer5;
                            final int integer9 = integer1 + integer7;
                            final int integer10 = integer4 + integer6;
                            final BlockPos blockPos11 = new BlockPos(integer8, integer9, integer10);
                            final BlockState blockState12 = this.world.getBlockState(blockPos11);
                            if (canDestroy(blockState12)) {
                                boolean4 = (this.world.breakBlock(blockPos11, true) || boolean4);
                            }
                        }
                    }
                }
                if (boolean4) {
                    this.world.playLevelEvent(null, 1022, new BlockPos(this), 0);
                }
            }
        }
        if (this.age % 20 == 0) {
            this.heal(1.0f);
        }
        this.bJ.setPercent(this.getHealth() / this.getHealthMaximum());
    }
    
    public static boolean canDestroy(final BlockState blockState) {
        return !blockState.isAir() && !BlockTags.X.contains(blockState.getBlock());
    }
    
    public void l() {
        this.setInvulTimer(220);
        this.setHealth(this.getHealthMaximum() / 3.0f);
    }
    
    @Override
    public void slowMovement(final BlockState state, final Vec3d multipliers) {
    }
    
    @Override
    public void onStartedTrackingBy(final ServerPlayerEntity serverPlayerEntity) {
        super.onStartedTrackingBy(serverPlayerEntity);
        this.bJ.addPlayer(serverPlayerEntity);
    }
    
    @Override
    public void onStoppedTrackingBy(final ServerPlayerEntity serverPlayerEntity) {
        super.onStoppedTrackingBy(serverPlayerEntity);
        this.bJ.removePlayer(serverPlayerEntity);
    }
    
    private double s(final int integer) {
        if (integer <= 0) {
            return this.x;
        }
        final float float2 = (this.aK + 180 * (integer - 1)) * 0.017453292f;
        final float float3 = MathHelper.cos(float2);
        return this.x + float3 * 1.3;
    }
    
    private double t(final int integer) {
        if (integer <= 0) {
            return this.y + 3.0;
        }
        return this.y + 2.2;
    }
    
    private double u(final int integer) {
        if (integer <= 0) {
            return this.z;
        }
        final float float2 = (this.aK + 180 * (integer - 1)) * 0.017453292f;
        final float float3 = MathHelper.sin(float2);
        return this.z + float3 * 1.3;
    }
    
    private float a(final float float1, final float float2, final float float3) {
        float float4 = MathHelper.wrapDegrees(float2 - float1);
        if (float4 > float3) {
            float4 = float3;
        }
        if (float4 < -float3) {
            float4 = -float3;
        }
        return float1 + float4;
    }
    
    private void a(final int integer, final LivingEntity livingEntity) {
        this.a(integer, livingEntity.x, livingEntity.y + livingEntity.getStandingEyeHeight() * 0.5, livingEntity.z, integer == 0 && this.random.nextFloat() < 0.001f);
    }
    
    private void a(final int integer, final double double2, final double double4, final double double6, final boolean boolean8) {
        this.world.playLevelEvent(null, 1024, new BlockPos(this), 0);
        final double double7 = this.s(integer);
        final double double8 = this.t(integer);
        final double double9 = this.u(integer);
        final double double10 = double2 - double7;
        final double double11 = double4 - double8;
        final double double12 = double6 - double9;
        final ExplodingWitherSkullEntity explodingWitherSkullEntity21 = new ExplodingWitherSkullEntity(this.world, this, double10, double11, double12);
        if (boolean8) {
            explodingWitherSkullEntity21.setCharged(true);
        }
        explodingWitherSkullEntity21.y = double8;
        explodingWitherSkullEntity21.x = double7;
        explodingWitherSkullEntity21.z = double9;
        this.world.spawnEntity(explodingWitherSkullEntity21);
    }
    
    @Override
    public void attack(final LivingEntity target, final float float2) {
        this.a(0, target);
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source == DamageSource.DROWN || source.getAttacker() instanceof WitherEntity) {
            return false;
        }
        if (this.getInvulTimer() > 0 && source != DamageSource.OUT_OF_WORLD) {
            return false;
        }
        if (this.isAtHalfHealth()) {
            final Entity entity3 = source.getSource();
            if (entity3 instanceof ProjectileEntity) {
                return false;
            }
        }
        final Entity entity3 = source.getAttacker();
        if (entity3 != null) {
            if (!(entity3 instanceof PlayerEntity)) {
                if (entity3 instanceof LivingEntity && ((LivingEntity)entity3).getGroup() == this.getGroup()) {
                    return false;
                }
            }
        }
        if (this.bI <= 0) {
            this.bI = 20;
        }
        for (int integer4 = 0; integer4 < this.bH.length; ++integer4) {
            final int[] bh = this.bH;
            final int n = integer4;
            bh[n] += 3;
        }
        return super.damage(source, amount);
    }
    
    @Override
    protected void dropEquipment(final DamageSource damageSource, final int addedDropChance, final boolean dropAllowed) {
        super.dropEquipment(damageSource, addedDropChance, dropAllowed);
        final ItemEntity itemEntity4 = this.dropItem(Items.nV);
        if (itemEntity4 != null) {
            itemEntity4.s();
        }
    }
    
    @Override
    protected void checkDespawn() {
        this.despawnCounter = 0;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getLightmapCoordinates() {
        return 15728880;
    }
    
    @Override
    public void handleFallDamage(final float fallDistance, final float damageMultiplier) {
    }
    
    @Override
    public boolean addPotionEffect(final StatusEffectInstance statusEffectInstance) {
        return false;
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(300.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.6000000238418579);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(40.0);
        this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(4.0);
    }
    
    @Environment(EnvType.CLIENT)
    public float a(final int integer) {
        return this.bD[integer];
    }
    
    @Environment(EnvType.CLIENT)
    public float b(final int integer) {
        return this.bC[integer];
    }
    
    public int getInvulTimer() {
        return this.dataTracker.<Integer>get(WitherEntity.INVUL_TIMER);
    }
    
    public void setInvulTimer(final int integer) {
        this.dataTracker.<Integer>set(WitherEntity.INVUL_TIMER, integer);
    }
    
    public int getTrackedEntityId(final int integer) {
        return this.dataTracker.<Integer>get(WitherEntity.bz.get(integer));
    }
    
    public void setTrackedEntityId(final int headIndex, final int integer2) {
        this.dataTracker.<Integer>set(WitherEntity.bz.get(headIndex), integer2);
    }
    
    public boolean isAtHalfHealth() {
        return this.getHealth() <= this.getHealthMaximum() / 2.0f;
    }
    
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }
    
    @Override
    protected boolean canStartRiding(final Entity entity) {
        return false;
    }
    
    @Override
    public boolean canUsePortals() {
        return false;
    }
    
    @Override
    public boolean isPotionEffective(final StatusEffectInstance statusEffectInstance) {
        return statusEffectInstance.getEffectType() != StatusEffects.t && super.isPotionEffective(statusEffectInstance);
    }
    
    static {
        TRACKED_ENTITY_ID_1 = DataTracker.<Integer>registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
        TRACKED_ENTITY_ID_2 = DataTracker.<Integer>registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
        TRACKED_ENTITY_ID_3 = DataTracker.<Integer>registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
        bz = ImmutableList.<TrackedData<Integer>>of(WitherEntity.TRACKED_ENTITY_ID_1, WitherEntity.TRACKED_ENTITY_ID_2, WitherEntity.TRACKED_ENTITY_ID_3);
        INVUL_TIMER = DataTracker.<Integer>registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
        HEAD_TARGET_PREDICATE = new TargetPredicate().setBaseMaxDistance(20.0);
        bK = (livingEntity -> livingEntity.getGroup() != EntityGroup.UNDEAD && livingEntity.du());
    }
    
    class a extends Goal
    {
        public a() {
            this.setControls(EnumSet.<Control>of(Control.a, Control.c, Control.b));
        }
        
        @Override
        public boolean canStart() {
            return WitherEntity.this.getInvulTimer() > 0;
        }
    }
}
