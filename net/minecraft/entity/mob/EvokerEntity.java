package net.minecraft.entity.mob;

import java.util.List;
import net.minecraft.util.DyeColor;
import net.minecraft.entity.EntityData;
import net.minecraft.world.IWorld;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.world.BlockView;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;

public class EvokerEntity extends SpellcastingIllagerEntity
{
    private SheepEntity wololoTarget;
    
    public EvokerEntity(final EntityType<? extends EvokerEntity> type, final World world) {
        super(type, world);
        this.experiencePoints = 10;
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtTargetOrWololoTarget());
        this.goalSelector.add(2, new FleeEntityGoal<>(this, PlayerEntity.class, 8.0f, 0.6, 1.0));
        this.goalSelector.add(4, new SummonVexGoal());
        this.goalSelector.add(5, new ConjureFangsGoal());
        this.goalSelector.add(6, new WololoGoal());
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[] { RaiderEntity.class }).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, AbstractTraderEntity.class, false).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, IronGolemEntity.class, false));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(12.0);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(24.0);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
    }
    
    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.cV;
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
    }
    
    @Override
    protected void mobTick() {
        super.mobTick();
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    @Override
    public boolean isTeammate(final Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity == this) {
            return true;
        }
        if (super.isTeammate(entity)) {
            return true;
        }
        if (entity instanceof VexEntity) {
            return this.isTeammate(((VexEntity)entity).getOwner());
        }
        return entity instanceof LivingEntity && ((LivingEntity)entity).getGroup() == EntityGroup.ILLAGER && this.getScoreboardTeam() == null && entity.getScoreboardTeam() == null;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.cT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.cW;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.cY;
    }
    
    private void setWololoTarget(@Nullable final SheepEntity sheepEntity) {
        this.wololoTarget = sheepEntity;
    }
    
    @Nullable
    private SheepEntity getWololoTarget() {
        return this.wololoTarget;
    }
    
    @Override
    protected SoundEvent getCastSpellSound() {
        return SoundEvents.cU;
    }
    
    @Override
    public void addBonusForWave(final int wave, final boolean boolean2) {
    }
    
    class LookAtTargetOrWololoTarget extends LookAtTargetGoal
    {
        private LookAtTargetOrWololoTarget() {
        }
        
        @Override
        public void tick() {
            if (EvokerEntity.this.getTarget() != null) {
                EvokerEntity.this.getLookControl().lookAt(EvokerEntity.this.getTarget(), (float)EvokerEntity.this.dA(), (float)EvokerEntity.this.getLookPitchSpeed());
            }
            else if (EvokerEntity.this.getWololoTarget() != null) {
                EvokerEntity.this.getLookControl().lookAt(EvokerEntity.this.getWololoTarget(), (float)EvokerEntity.this.dA(), (float)EvokerEntity.this.getLookPitchSpeed());
            }
        }
    }
    
    class ConjureFangsGoal extends CastSpellGoal
    {
        private ConjureFangsGoal() {
        }
        
        @Override
        protected int getSpellTicks() {
            return 40;
        }
        
        @Override
        protected int startTimeDelay() {
            return 100;
        }
        
        @Override
        protected void castSpell() {
            final LivingEntity livingEntity1 = EvokerEntity.this.getTarget();
            final double double2 = Math.min(livingEntity1.y, EvokerEntity.this.y);
            final double double3 = Math.max(livingEntity1.y, EvokerEntity.this.y) + 1.0;
            final float float6 = (float)MathHelper.atan2(livingEntity1.z - EvokerEntity.this.z, livingEntity1.x - EvokerEntity.this.x);
            if (EvokerEntity.this.squaredDistanceTo(livingEntity1) < 9.0) {
                for (int integer7 = 0; integer7 < 5; ++integer7) {
                    final float float7 = float6 + integer7 * 3.1415927f * 0.4f;
                    this.conjureFangs(EvokerEntity.this.x + MathHelper.cos(float7) * 1.5, EvokerEntity.this.z + MathHelper.sin(float7) * 1.5, double2, double3, float7, 0);
                }
                for (int integer7 = 0; integer7 < 8; ++integer7) {
                    final float float7 = float6 + integer7 * 3.1415927f * 2.0f / 8.0f + 1.2566371f;
                    this.conjureFangs(EvokerEntity.this.x + MathHelper.cos(float7) * 2.5, EvokerEntity.this.z + MathHelper.sin(float7) * 2.5, double2, double3, float7, 3);
                }
            }
            else {
                for (int integer7 = 0; integer7 < 16; ++integer7) {
                    final double double4 = 1.25 * (integer7 + 1);
                    final int integer8 = 1 * integer7;
                    this.conjureFangs(EvokerEntity.this.x + MathHelper.cos(float6) * double4, EvokerEntity.this.z + MathHelper.sin(float6) * double4, double2, double3, float6, integer8);
                }
            }
        }
        
        private void conjureFangs(final double z, final double maxY, final double warmup, final double double7, final float float9, final int integer10) {
            BlockPos blockPos11 = new BlockPos(z, double7, maxY);
            boolean boolean12 = false;
            double double8 = 0.0;
            do {
                final BlockPos blockPos12 = blockPos11.down();
                final BlockState blockState16 = EvokerEntity.this.world.getBlockState(blockPos12);
                if (Block.isSolidFullSquare(blockState16, EvokerEntity.this.world, blockPos12, Direction.UP)) {
                    if (!EvokerEntity.this.world.isAir(blockPos11)) {
                        final BlockState blockState17 = EvokerEntity.this.world.getBlockState(blockPos11);
                        final VoxelShape voxelShape18 = blockState17.getCollisionShape(EvokerEntity.this.world, blockPos11);
                        if (!voxelShape18.isEmpty()) {
                            double8 = voxelShape18.getMaximum(Direction.Axis.Y);
                        }
                    }
                    boolean12 = true;
                    break;
                }
                blockPos11 = blockPos11.down();
            } while (blockPos11.getY() >= MathHelper.floor(warmup) - 1);
            if (boolean12) {
                EvokerEntity.this.world.spawnEntity(new EvokerFangsEntity(EvokerEntity.this.world, z, blockPos11.getY() + double8, maxY, float9, integer10, EvokerEntity.this));
            }
        }
        
        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.cZ;
        }
        
        @Override
        protected a l() {
            return SpellcastingIllagerEntity.a.c;
        }
    }
    
    class SummonVexGoal extends CastSpellGoal
    {
        private final TargetPredicate closeVexPredicate;
        
        private SummonVexGoal() {
            this.closeVexPredicate = new TargetPredicate().setBaseMaxDistance(16.0).includeHidden().ignoreDistanceScalingFactor().includeInvulnerable().includeTeammates();
        }
        
        @Override
        public boolean canStart() {
            if (!super.canStart()) {
                return false;
            }
            final int integer1 = EvokerEntity.this.world.<LivingEntity>getTargets(VexEntity.class, this.closeVexPredicate, (LivingEntity)EvokerEntity.this, EvokerEntity.this.getBoundingBox().expand(16.0)).size();
            return EvokerEntity.this.random.nextInt(8) + 1 > integer1;
        }
        
        @Override
        protected int getSpellTicks() {
            return 100;
        }
        
        @Override
        protected int startTimeDelay() {
            return 340;
        }
        
        @Override
        protected void castSpell() {
            for (int integer1 = 0; integer1 < 3; ++integer1) {
                final BlockPos blockPos2 = new BlockPos(EvokerEntity.this).add(-2 + EvokerEntity.this.random.nextInt(5), 1, -2 + EvokerEntity.this.random.nextInt(5));
                final VexEntity vexEntity3 = EntityType.VEX.create(EvokerEntity.this.world);
                vexEntity3.setPositionAndAngles(blockPos2, 0.0f, 0.0f);
                vexEntity3.initialize(EvokerEntity.this.world, EvokerEntity.this.world.getLocalDifficulty(blockPos2), SpawnType.f, null, null);
                vexEntity3.setOwner(EvokerEntity.this);
                vexEntity3.setBounds(blockPos2);
                vexEntity3.setLifeTicks(20 * (30 + EvokerEntity.this.random.nextInt(90)));
                EvokerEntity.this.world.spawnEntity(vexEntity3);
            }
        }
        
        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.da;
        }
        
        @Override
        protected a l() {
            return SpellcastingIllagerEntity.a.b;
        }
    }
    
    public class WololoGoal extends CastSpellGoal
    {
        private final TargetPredicate purpleSheepPredicate;
        
        public WololoGoal() {
            this.purpleSheepPredicate = new TargetPredicate().setBaseMaxDistance(16.0).includeInvulnerable().setPredicate(livingEntity -> livingEntity.getColor() == DyeColor.l);
        }
        
        @Override
        public boolean canStart() {
            if (EvokerEntity.this.getTarget() != null) {
                return false;
            }
            if (EvokerEntity.this.isSpellcasting()) {
                return false;
            }
            if (EvokerEntity.this.age < this.startTime) {
                return false;
            }
            if (!EvokerEntity.this.world.getGameRules().getBoolean("mobGriefing")) {
                return false;
            }
            final List<SheepEntity> list1 = EvokerEntity.this.world.<SheepEntity>getTargets(SheepEntity.class, this.purpleSheepPredicate, (LivingEntity)EvokerEntity.this, EvokerEntity.this.getBoundingBox().expand(16.0, 4.0, 16.0));
            if (list1.isEmpty()) {
                return false;
            }
            EvokerEntity.this.setWololoTarget(list1.get(EvokerEntity.this.random.nextInt(list1.size())));
            return true;
        }
        
        @Override
        public boolean shouldContinue() {
            return EvokerEntity.this.getWololoTarget() != null && this.spellCooldown > 0;
        }
        
        @Override
        public void stop() {
            super.stop();
            EvokerEntity.this.setWololoTarget(null);
        }
        
        @Override
        protected void castSpell() {
            final SheepEntity sheepEntity1 = EvokerEntity.this.getWololoTarget();
            if (sheepEntity1 != null && sheepEntity1.isAlive()) {
                sheepEntity1.setColor(DyeColor.o);
            }
        }
        
        @Override
        protected int getInitialCooldown() {
            return 40;
        }
        
        @Override
        protected int getSpellTicks() {
            return 60;
        }
        
        @Override
        protected int startTimeDelay() {
            return 140;
        }
        
        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.db;
        }
        
        @Override
        protected a l() {
            return SpellcastingIllagerEntity.a.d;
        }
    }
}
