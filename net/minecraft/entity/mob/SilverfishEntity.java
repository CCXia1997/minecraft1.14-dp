package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.block.Block;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.IWorld;
import net.minecraft.block.InfestedBlock;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;

public class SilverfishEntity extends HostileEntity
{
    private static final TargetPredicate CLOSE_PLAYER_PREDICATE;
    private b c;
    
    public SilverfishEntity(final EntityType<? extends SilverfishEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initGoals() {
        this.c = new b(this);
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, this.c);
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(5, new a(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
    }
    
    @Override
    public double getHeightOffset() {
        return 0.1;
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 0.1f;
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.kj;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.kl;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.kk;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(SoundEvents.km, 0.15f, 1.0f);
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if ((source instanceof EntityDamageSource || source == DamageSource.MAGIC) && this.c != null) {
            this.c.g();
        }
        return super.damage(source, amount);
    }
    
    @Override
    public void tick() {
        this.aK = this.yaw;
        super.tick();
    }
    
    @Override
    public void setYaw(final float float1) {
        super.setYaw(this.yaw = float1);
    }
    
    @Override
    public float getPathfindingFavor(final BlockPos pos, final ViewableWorld world) {
        if (InfestedBlock.hasRegularBlock(world.getBlockState(pos.down()))) {
            return 10.0f;
        }
        return super.getPathfindingFavor(pos, world);
    }
    
    @Override
    protected boolean checkLightLevelForSpawn() {
        return true;
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        if (super.canSpawn(iWorld, spawnType)) {
            final PlayerEntity playerEntity3 = this.world.getClosestPlayer(SilverfishEntity.CLOSE_PLAYER_PREDICATE, this);
            return playerEntity3 == null;
        }
        return false;
    }
    
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }
    
    static {
        CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(5.0).ignoreDistanceScalingFactor();
    }
    
    static class b extends Goal
    {
        private final SilverfishEntity a;
        private int b;
        
        public b(final SilverfishEntity silverfishEntity) {
            this.a = silverfishEntity;
        }
        
        public void g() {
            if (this.b == 0) {
                this.b = 20;
            }
        }
        
        @Override
        public boolean canStart() {
            return this.b > 0;
        }
        
        @Override
        public void tick() {
            --this.b;
            Label_0236: {
                if (this.b <= 0) {
                    final World world1 = this.a.world;
                    final Random random2 = this.a.getRand();
                    final BlockPos blockPos3 = new BlockPos(this.a);
                    for (int integer4 = 0; integer4 <= 5 && integer4 >= -5; integer4 = ((integer4 <= 0) ? 1 : 0) - integer4) {
                        for (int integer5 = 0; integer5 <= 10 && integer5 >= -10; integer5 = ((integer5 <= 0) ? 1 : 0) - integer5) {
                            for (int integer6 = 0; integer6 <= 10 && integer6 >= -10; integer6 = ((integer6 <= 0) ? 1 : 0) - integer6) {
                                final BlockPos blockPos4 = blockPos3.add(integer5, integer4, integer6);
                                final BlockState blockState8 = world1.getBlockState(blockPos4);
                                final Block block9 = blockState8.getBlock();
                                if (block9 instanceof InfestedBlock) {
                                    if (world1.getGameRules().getBoolean("mobGriefing")) {
                                        world1.breakBlock(blockPos4, true);
                                    }
                                    else {
                                        world1.setBlockState(blockPos4, ((InfestedBlock)block9).getRegularBlock().getDefaultState(), 3);
                                    }
                                    if (random2.nextBoolean()) {
                                        break Label_0236;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    static class a extends WanderAroundGoal
    {
        private Direction h;
        private boolean i;
        
        public a(final SilverfishEntity silverfishEntity) {
            super(silverfishEntity, 1.0, 10);
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        @Override
        public boolean canStart() {
            if (this.owner.getTarget() != null) {
                return false;
            }
            if (!this.owner.getNavigation().isIdle()) {
                return false;
            }
            final Random random1 = this.owner.getRand();
            if (this.owner.world.getGameRules().getBoolean("mobGriefing") && random1.nextInt(10) == 0) {
                this.h = Direction.random(random1);
                final BlockPos blockPos2 = new BlockPos(this.owner.x, this.owner.y + 0.5, this.owner.z).offset(this.h);
                final BlockState blockState3 = this.owner.world.getBlockState(blockPos2);
                if (InfestedBlock.hasRegularBlock(blockState3)) {
                    return this.i = true;
                }
            }
            this.i = false;
            return super.canStart();
        }
        
        @Override
        public boolean shouldContinue() {
            return !this.i && super.shouldContinue();
        }
        
        @Override
        public void start() {
            if (!this.i) {
                super.start();
                return;
            }
            final IWorld iWorld1 = this.owner.world;
            final BlockPos blockPos2 = new BlockPos(this.owner.x, this.owner.y + 0.5, this.owner.z).offset(this.h);
            final BlockState blockState3 = iWorld1.getBlockState(blockPos2);
            if (InfestedBlock.hasRegularBlock(blockState3)) {
                iWorld1.setBlockState(blockPos2, InfestedBlock.getRegularBlock(blockState3.getBlock()), 3);
                this.owner.playSpawnEffects();
                this.owner.remove();
            }
        }
    }
}
