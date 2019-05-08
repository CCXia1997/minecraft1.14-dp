package net.minecraft.entity.mob;

import net.minecraft.world.Difficulty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.RangedAttacker;

public class IllusionerEntity extends SpellcastingIllagerEntity implements RangedAttacker
{
    private int bz;
    private final Vec3d[][] bA;
    
    public IllusionerEntity(final EntityType<? extends IllusionerEntity> type, final World world) {
        super(type, world);
        this.experiencePoints = 5;
        this.bA = new Vec3d[2][4];
        for (int integer3 = 0; integer3 < 4; ++integer3) {
            this.bA[0][integer3] = new Vec3d(0.0, 0.0, 0.0);
            this.bA[1][integer3] = new Vec3d(0.0, 0.0, 0.0);
        }
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtTargetGoal());
        this.goalSelector.add(4, new GiveInvisibilityGoal());
        this.goalSelector.add(5, new BlindTargetGoal());
        this.goalSelector.add(6, new BowAttackGoal<>(this, 0.5, 20, 15.0f));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[] { RaiderEntity.class }).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, AbstractTraderEntity.class, false).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, IronGolemEntity.class, false).setMaxTimeWithoutVisibility(300));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(18.0);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(32.0);
    }
    
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.jf));
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public BoundingBox getVisibilityBoundingBox() {
        return this.getBoundingBox().expand(3.0, 0.0, 3.0);
    }
    
    @Override
    public void updateState() {
        super.updateState();
        if (this.world.isClient && this.isInvisible()) {
            --this.bz;
            if (this.bz < 0) {
                this.bz = 0;
            }
            if (this.hurtTime == 1 || this.age % 1200 == 0) {
                this.bz = 3;
                final float float1 = -6.0f;
                final int integer2 = 13;
                for (int integer3 = 0; integer3 < 4; ++integer3) {
                    this.bA[0][integer3] = this.bA[1][integer3];
                    this.bA[1][integer3] = new Vec3d((-6.0f + this.random.nextInt(13)) * 0.5, Math.max(0, this.random.nextInt(6) - 4), (-6.0f + this.random.nextInt(13)) * 0.5);
                }
                for (int integer3 = 0; integer3 < 16; ++integer3) {
                    this.world.addParticle(ParticleTypes.f, this.x + (this.random.nextDouble() - 0.5) * this.getWidth(), this.y + this.random.nextDouble() * this.getHeight(), this.z + (this.random.nextDouble() - 0.5) * this.getWidth(), 0.0, 0.0, 0.0);
                }
                this.world.playSound(this.x, this.y, this.z, SoundEvents.fo, this.getSoundCategory(), 1.0f, 1.0f, false);
            }
            else if (this.hurtTime == this.ay - 1) {
                this.bz = 3;
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    this.bA[0][integer4] = this.bA[1][integer4];
                    this.bA[1][integer4] = new Vec3d(0.0, 0.0, 0.0);
                }
            }
        }
    }
    
    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.fk;
    }
    
    @Environment(EnvType.CLIENT)
    public Vec3d[] v(final float float1) {
        if (this.bz <= 0) {
            return this.bA[1];
        }
        double double2 = (this.bz - float1) / 3.0f;
        double2 = Math.pow(double2, 0.25);
        final Vec3d[] arr4 = new Vec3d[4];
        for (int integer5 = 0; integer5 < 4; ++integer5) {
            arr4[integer5] = this.bA[1][integer5].multiply(1.0 - double2).add(this.bA[0][integer5].multiply(double2));
        }
        return arr4;
    }
    
    @Override
    public boolean isTeammate(final Entity entity) {
        return super.isTeammate(entity) || (entity instanceof LivingEntity && ((LivingEntity)entity).getGroup() == EntityGroup.ILLAGER && this.getScoreboardTeam() == null && entity.getScoreboardTeam() == null);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.fk;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.fm;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.fn;
    }
    
    @Override
    protected SoundEvent getCastSpellSound() {
        return SoundEvents.fl;
    }
    
    @Override
    public void addBonusForWave(final int wave, final boolean boolean2) {
    }
    
    @Override
    public void attack(final LivingEntity target, final float float2) {
        final ItemStack itemStack3 = this.getArrowType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.jf)));
        final ProjectileEntity projectileEntity4 = ProjectileUtil.createArrowProjectile(this, itemStack3, float2);
        final double double5 = target.x - this.x;
        final double double6 = target.getBoundingBox().minY + target.getHeight() / 3.0f - projectileEntity4.y;
        final double double7 = target.z - this.z;
        final double double8 = MathHelper.sqrt(double5 * double5 + double7 * double7);
        projectileEntity4.setVelocity(double5, double6 + double8 * 0.20000000298023224, double7, 1.6f, (float)(14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ky, 1.0f, 1.0f / (this.getRand().nextFloat() * 0.4f + 0.8f));
        this.world.spawnEntity(projectileEntity4);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public State getState() {
        if (this.isSpellcasting()) {
            return State.c;
        }
        if (this.isAttacking()) {
            return State.d;
        }
        return State.a;
    }
    
    class GiveInvisibilityGoal extends CastSpellGoal
    {
        private GiveInvisibilityGoal() {
        }
        
        @Override
        public boolean canStart() {
            return super.canStart() && !IllusionerEntity.this.hasStatusEffect(StatusEffects.n);
        }
        
        @Override
        protected int getSpellTicks() {
            return 20;
        }
        
        @Override
        protected int startTimeDelay() {
            return 340;
        }
        
        @Override
        protected void castSpell() {
            IllusionerEntity.this.addPotionEffect(new StatusEffectInstance(StatusEffects.n, 1200));
        }
        
        @Nullable
        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.fq;
        }
        
        @Override
        protected a l() {
            return SpellcastingIllagerEntity.a.e;
        }
    }
    
    class BlindTargetGoal extends CastSpellGoal
    {
        private int targetId;
        
        private BlindTargetGoal() {
        }
        
        @Override
        public boolean canStart() {
            return super.canStart() && IllusionerEntity.this.getTarget() != null && IllusionerEntity.this.getTarget().getEntityId() != this.targetId && IllusionerEntity.this.world.getLocalDifficulty(new BlockPos(IllusionerEntity.this)).a((float)Difficulty.NORMAL.ordinal());
        }
        
        @Override
        public void start() {
            super.start();
            this.targetId = IllusionerEntity.this.getTarget().getEntityId();
        }
        
        @Override
        protected int getSpellTicks() {
            return 20;
        }
        
        @Override
        protected int startTimeDelay() {
            return 180;
        }
        
        @Override
        protected void castSpell() {
            IllusionerEntity.this.getTarget().addPotionEffect(new StatusEffectInstance(StatusEffects.o, 400));
        }
        
        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.fp;
        }
        
        @Override
        protected a l() {
            return SpellcastingIllagerEntity.a.f;
        }
    }
}
