package net.minecraft.entity.mob;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ZombieRaiseArmsGoal;
import java.util.Random;
import net.minecraft.block.Blocks;
import java.util.EnumSet;
import net.minecraft.world.BlockView;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.util.Hand;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.util.math.BlockPos;
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
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.ai.RangedAttacker;

public class DrownedEntity extends ZombieEntity implements RangedAttacker
{
    private boolean targettingUnderwater;
    protected final SwimNavigation waterNavigation;
    protected final MobNavigation landNavigation;
    
    public DrownedEntity(final EntityType<? extends DrownedEntity> type, final World world) {
        super(type, world);
        this.stepHeight = 1.0f;
        this.moveControl = new DrownedMoveControl(this);
        this.setPathNodeTypeWeight(PathNodeType.g, 0.0f);
        this.waterNavigation = new SwimNavigation(this, world);
        this.landNavigation = new MobNavigation(this, world);
    }
    
    @Override
    protected void initCustomGoals() {
        this.goalSelector.add(1, new c(this, 1.0));
        this.goalSelector.add(2, new f(this, 1.0, 40, 10.0f));
        this.goalSelector.add(2, new a(this, 1.0, false));
        this.goalSelector.add(5, new b(this, 1.0));
        this.goalSelector.add(6, new e(this, 1.0, this.world.getSeaLevel()));
        this.goalSelector.add(7, new WanderAroundGoal(this, 1.0));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[] { DrownedEntity.class }).setGroupRevenge(ZombiePigmanEntity.class));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::h));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, AbstractTraderEntity.class, false));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(5, new FollowTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }
    
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        if (this.getEquippedStack(EquipmentSlot.HAND_OFF).isEmpty() && this.random.nextFloat() < 0.03f) {
            this.setEquippedStack(EquipmentSlot.HAND_OFF, new ItemStack(Items.pw));
            this.handDropChances[EquipmentSlot.HAND_OFF.getEntitySlotId()] = 2.0f;
        }
        return entityData;
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        final Biome biome3 = iWorld.getBiome(new BlockPos(this.x, this.y, this.z));
        if (biome3 == Biomes.i || biome3 == Biomes.m) {
            return this.random.nextInt(15) == 0 && super.canSpawn(iWorld, spawnType);
        }
        return this.random.nextInt(40) == 0 && this.ed() && super.canSpawn(iWorld, spawnType);
    }
    
    @Override
    protected boolean a(final IWorld iWorld, final SpawnType spawnType, final BlockPos blockPos) {
        return iWorld.getFluidState(blockPos).matches(FluidTags.a);
    }
    
    private boolean ed() {
        return this.getBoundingBox().minY < this.world.getSeaLevel() - 5;
    }
    
    @Override
    protected boolean shouldBreakDoors() {
        return false;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isInsideWater()) {
            return SoundEvents.cb;
        }
        return SoundEvents.ca;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        if (this.isInsideWater()) {
            return SoundEvents.cf;
        }
        return SoundEvents.ce;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        if (this.isInsideWater()) {
            return SoundEvents.cd;
        }
        return SoundEvents.cc;
    }
    
    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ch;
    }
    
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ci;
    }
    
    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }
    
    @Override
    protected void initEquipment(final LocalDifficulty localDifficulty) {
        if (this.random.nextFloat() > 0.9) {
            final int integer2 = this.random.nextInt(16);
            if (integer2 < 10) {
                this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.pu));
            }
            else {
                this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.kY));
            }
        }
    }
    
    @Override
    protected boolean isBetterItemFor(final ItemStack current, final ItemStack previous, final EquipmentSlot slot) {
        if (previous.getItem() == Items.pw) {
            return false;
        }
        if (previous.getItem() == Items.pu) {
            return current.getItem() == Items.pu && current.getDamage() < previous.getDamage();
        }
        return current.getItem() == Items.pu || super.isBetterItemFor(current, previous, slot);
    }
    
    @Override
    protected boolean canConvertInWater() {
        return false;
    }
    
    @Override
    public boolean canSpawn(final ViewableWorld world) {
        return world.intersectsEntities(this);
    }
    
    public boolean h(@Nullable final LivingEntity livingEntity) {
        return livingEntity != null && (!this.world.isDaylight() || livingEntity.isInsideWater());
    }
    
    @Override
    public boolean canFly() {
        return !this.isSwimming();
    }
    
    private boolean isTargettingUnderwater() {
        if (this.targettingUnderwater) {
            return true;
        }
        final LivingEntity livingEntity1 = this.getTarget();
        return livingEntity1 != null && livingEntity1.isInsideWater();
    }
    
    @Override
    public void travel(final Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isInsideWater() && this.isTargettingUnderwater()) {
            this.updateVelocity(0.01f, movementInput);
            this.move(MovementType.a, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
        }
        else {
            super.travel(movementInput);
        }
    }
    
    @Override
    public void updateSwimming() {
        if (!this.world.isClient) {
            if (this.canMoveVoluntarily() && this.isInsideWater() && this.isTargettingUnderwater()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            }
            else {
                this.navigation = this.landNavigation;
                this.setSwimming(false);
            }
        }
    }
    
    protected boolean ea() {
        final Path path1 = this.getNavigation().getCurrentPath();
        if (path1 != null) {
            final PathNode pathNode2 = path1.k();
            if (pathNode2 != null) {
                final double double3 = this.squaredDistanceTo(pathNode2.x, pathNode2.y, pathNode2.z);
                if (double3 < 4.0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void attack(final LivingEntity target, final float float2) {
        final TridentEntity tridentEntity3 = new TridentEntity(this.world, this, new ItemStack(Items.pu));
        final double double4 = target.x - this.x;
        final double double5 = target.getBoundingBox().minY + target.getHeight() / 3.0f - tridentEntity3.y;
        final double double6 = target.z - this.z;
        final double double7 = MathHelper.sqrt(double4 * double4 + double6 * double6);
        tridentEntity3.setVelocity(double4, double5 + double7 * 0.20000000298023224, double6, 1.6f, (float)(14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.cg, 1.0f, 1.0f / (this.getRand().nextFloat() * 0.4f + 0.8f));
        this.world.spawnEntity(tridentEntity3);
    }
    
    public void setTargettingUnderwater(final boolean boolean1) {
        this.targettingUnderwater = boolean1;
    }
    
    static class f extends ProjectileAttackGoal
    {
        private final DrownedEntity a;
        
        public f(final RangedAttacker rangedAttacker, final double double2, final int integer, final float float5) {
            super(rangedAttacker, double2, integer, float5);
            this.a = (DrownedEntity)rangedAttacker;
        }
        
        @Override
        public boolean canStart() {
            return super.canStart() && this.a.getMainHandStack().getItem() == Items.pu;
        }
        
        @Override
        public void start() {
            super.start();
            this.a.setAttacking(true);
            this.a.setCurrentHand(Hand.a);
        }
        
        @Override
        public void stop() {
            super.stop();
            this.a.clearActiveItem();
            this.a.setAttacking(false);
        }
    }
    
    static class e extends Goal
    {
        private final DrownedEntity a;
        private final double b;
        private final int c;
        private boolean d;
        
        public e(final DrownedEntity drownedEntity, final double double2, final int integer4) {
            this.a = drownedEntity;
            this.b = double2;
            this.c = integer4;
        }
        
        @Override
        public boolean canStart() {
            return !this.a.world.isDaylight() && this.a.isInsideWater() && this.a.y < this.c - 2;
        }
        
        @Override
        public boolean shouldContinue() {
            return this.canStart() && !this.d;
        }
        
        @Override
        public void tick() {
            if (this.a.y < this.c - 1 && (this.a.getNavigation().isIdle() || this.a.ea())) {
                final Vec3d vec3d1 = PathfindingUtil.a(this.a, 4, 8, new Vec3d(this.a.x, this.c - 1, this.a.z));
                if (vec3d1 == null) {
                    this.d = true;
                    return;
                }
                this.a.getNavigation().startMovingTo(vec3d1.x, vec3d1.y, vec3d1.z, this.b);
            }
        }
        
        @Override
        public void start() {
            this.a.setTargettingUnderwater(true);
            this.d = false;
        }
        
        @Override
        public void stop() {
            this.a.setTargettingUnderwater(false);
        }
    }
    
    static class b extends MoveToTargetPosGoal
    {
        private final DrownedEntity g;
        
        public b(final DrownedEntity drownedEntity, final double double2) {
            super(drownedEntity, double2, 8, 2);
            this.g = drownedEntity;
        }
        
        @Override
        public boolean canStart() {
            return super.canStart() && !this.g.world.isDaylight() && this.g.isInsideWater() && this.g.y >= this.g.world.getSeaLevel() - 3;
        }
        
        @Override
        public boolean shouldContinue() {
            return super.shouldContinue();
        }
        
        @Override
        protected boolean isTargetPos(final ViewableWorld world, final BlockPos pos) {
            final BlockPos blockPos3 = pos.up();
            return world.isAir(blockPos3) && world.isAir(blockPos3.up()) && world.getBlockState(pos).hasSolidTopSurface(world, pos, this.g);
        }
        
        @Override
        public void start() {
            this.g.setTargettingUnderwater(false);
            this.g.navigation = this.g.landNavigation;
            super.start();
        }
        
        @Override
        public void stop() {
            super.stop();
        }
    }
    
    static class c extends Goal
    {
        private final MobEntityWithAi a;
        private double b;
        private double c;
        private double d;
        private final double e;
        private final World f;
        
        public c(final MobEntityWithAi mobEntityWithAi, final double double2) {
            this.a = mobEntityWithAi;
            this.e = double2;
            this.f = mobEntityWithAi.world;
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        @Override
        public boolean canStart() {
            if (!this.f.isDaylight()) {
                return false;
            }
            if (this.a.isInsideWater()) {
                return false;
            }
            final Vec3d vec3d1 = this.g();
            if (vec3d1 == null) {
                return false;
            }
            this.b = vec3d1.x;
            this.c = vec3d1.y;
            this.d = vec3d1.z;
            return true;
        }
        
        @Override
        public boolean shouldContinue() {
            return !this.a.getNavigation().isIdle();
        }
        
        @Override
        public void start() {
            this.a.getNavigation().startMovingTo(this.b, this.c, this.d, this.e);
        }
        
        @Nullable
        private Vec3d g() {
            final Random random1 = this.a.getRand();
            final BlockPos blockPos2 = new BlockPos(this.a.x, this.a.getBoundingBox().minY, this.a.z);
            for (int integer3 = 0; integer3 < 10; ++integer3) {
                final BlockPos blockPos3 = blockPos2.add(random1.nextInt(20) - 10, 2 - random1.nextInt(8), random1.nextInt(20) - 10);
                if (this.f.getBlockState(blockPos3).getBlock() == Blocks.A) {
                    return new Vec3d(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ());
                }
            }
            return null;
        }
    }
    
    static class a extends ZombieRaiseArmsGoal
    {
        private final DrownedEntity d;
        
        public a(final DrownedEntity drownedEntity, final double double2, final boolean boolean4) {
            super(drownedEntity, double2, boolean4);
            this.d = drownedEntity;
        }
        
        @Override
        public boolean canStart() {
            return super.canStart() && this.d.h(this.d.getTarget());
        }
        
        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && this.d.h(this.d.getTarget());
        }
    }
    
    static class DrownedMoveControl extends MoveControl
    {
        private final DrownedEntity drowned;
        
        public DrownedMoveControl(final DrownedEntity drownedEntity) {
            super(drownedEntity);
            this.drowned = drownedEntity;
        }
        
        @Override
        public void tick() {
            final LivingEntity livingEntity1 = this.drowned.getTarget();
            if (this.drowned.isTargettingUnderwater() && this.drowned.isInsideWater()) {
                if ((livingEntity1 != null && livingEntity1.y > this.drowned.y) || this.drowned.targettingUnderwater) {
                    this.drowned.setVelocity(this.drowned.getVelocity().add(0.0, 0.002, 0.0));
                }
                if (this.state != State.b || this.drowned.getNavigation().isIdle()) {
                    this.drowned.setMovementSpeed(0.0f);
                    return;
                }
                final double double2 = this.targetX - this.drowned.x;
                double double3 = this.targetY - this.drowned.y;
                final double double4 = this.targetZ - this.drowned.z;
                final double double5 = MathHelper.sqrt(double2 * double2 + double3 * double3 + double4 * double4);
                double3 /= double5;
                final float float10 = (float)(MathHelper.atan2(double4, double2) * 57.2957763671875) - 90.0f;
                this.drowned.yaw = this.changeAngle(this.drowned.yaw, float10, 90.0f);
                this.drowned.aK = this.drowned.yaw;
                final float float11 = (float)(this.speed * this.drowned.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
                final float float12 = MathHelper.lerp(0.125f, this.drowned.getMovementSpeed(), float11);
                this.drowned.setMovementSpeed(float12);
                this.drowned.setVelocity(this.drowned.getVelocity().add(float12 * double2 * 0.005, float12 * double3 * 0.1, float12 * double4 * 0.005));
            }
            else {
                if (!this.drowned.onGround) {
                    this.drowned.setVelocity(this.drowned.getVelocity().add(0.0, -0.008, 0.0));
                }
                super.tick();
            }
        }
    }
}
