package net.minecraft.entity.passive;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.state.property.Property;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.stat.Stats;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import java.util.EnumSet;
import com.google.common.collect.Sets;
import net.minecraft.item.Item;
import java.util.Set;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.world.BlockView;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import java.util.Random;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.Position;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.block.Block;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.ViewableWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.Blocks;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.data.TrackedData;

public class TurtleEntity extends AnimalEntity
{
    private static final TrackedData<BlockPos> HOME_POS;
    private static final TrackedData<Boolean> HAS_EGG;
    private static final TrackedData<Boolean> DIGGING_SAND;
    private static final TrackedData<BlockPos> TRAVEL_POS;
    private static final TrackedData<Boolean> LAND_BOUND;
    private static final TrackedData<Boolean> ACTIVELY_TRAVELLING;
    private int sandDiggingCounter;
    public static final Predicate<LivingEntity> BABY_TURTLE_ON_LAND_FILTER;
    
    public TurtleEntity(final EntityType<? extends TurtleEntity> type, final World world) {
        super(type, world);
        this.moveControl = new TurtleMoveControl(this);
        this.spawningGround = Blocks.C;
        this.stepHeight = 1.0f;
    }
    
    public void setHomePos(final BlockPos pos) {
        this.dataTracker.<BlockPos>set(TurtleEntity.HOME_POS, pos);
    }
    
    private BlockPos getHomePos() {
        return this.dataTracker.<BlockPos>get(TurtleEntity.HOME_POS);
    }
    
    private void setTravelPos(final BlockPos pos) {
        this.dataTracker.<BlockPos>set(TurtleEntity.TRAVEL_POS, pos);
    }
    
    private BlockPos getTravelPos() {
        return this.dataTracker.<BlockPos>get(TurtleEntity.TRAVEL_POS);
    }
    
    public boolean hasEgg() {
        return this.dataTracker.<Boolean>get(TurtleEntity.HAS_EGG);
    }
    
    private void setHasEgg(final boolean hasEgg) {
        this.dataTracker.<Boolean>set(TurtleEntity.HAS_EGG, hasEgg);
    }
    
    public boolean isDiggingSand() {
        return this.dataTracker.<Boolean>get(TurtleEntity.DIGGING_SAND);
    }
    
    private void setDiggingSand(final boolean diggingSand) {
        this.sandDiggingCounter = (diggingSand ? 1 : 0);
        this.dataTracker.<Boolean>set(TurtleEntity.DIGGING_SAND, diggingSand);
    }
    
    private boolean isLandBound() {
        return this.dataTracker.<Boolean>get(TurtleEntity.LAND_BOUND);
    }
    
    private void setLandBound(final boolean landBound) {
        this.dataTracker.<Boolean>set(TurtleEntity.LAND_BOUND, landBound);
    }
    
    private boolean isActivelyTravelling() {
        return this.dataTracker.<Boolean>get(TurtleEntity.ACTIVELY_TRAVELLING);
    }
    
    private void setActivelyTravelling(final boolean travelling) {
        this.dataTracker.<Boolean>set(TurtleEntity.ACTIVELY_TRAVELLING, travelling);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<BlockPos>startTracking(TurtleEntity.HOME_POS, BlockPos.ORIGIN);
        this.dataTracker.<Boolean>startTracking(TurtleEntity.HAS_EGG, false);
        this.dataTracker.<BlockPos>startTracking(TurtleEntity.TRAVEL_POS, BlockPos.ORIGIN);
        this.dataTracker.<Boolean>startTracking(TurtleEntity.LAND_BOUND, false);
        this.dataTracker.<Boolean>startTracking(TurtleEntity.ACTIVELY_TRAVELLING, false);
        this.dataTracker.<Boolean>startTracking(TurtleEntity.DIGGING_SAND, false);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("HomePosX", this.getHomePos().getX());
        tag.putInt("HomePosY", this.getHomePos().getY());
        tag.putInt("HomePosZ", this.getHomePos().getZ());
        tag.putBoolean("HasEgg", this.hasEgg());
        tag.putInt("TravelPosX", this.getTravelPos().getX());
        tag.putInt("TravelPosY", this.getTravelPos().getY());
        tag.putInt("TravelPosZ", this.getTravelPos().getZ());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        final int integer2 = tag.getInt("HomePosX");
        final int integer3 = tag.getInt("HomePosY");
        final int integer4 = tag.getInt("HomePosZ");
        this.setHomePos(new BlockPos(integer2, integer3, integer4));
        super.readCustomDataFromTag(tag);
        this.setHasEgg(tag.getBoolean("HasEgg"));
        final int integer5 = tag.getInt("TravelPosX");
        final int integer6 = tag.getInt("TravelPosY");
        final int integer7 = tag.getInt("TravelPosZ");
        this.setTravelPos(new BlockPos(integer5, integer6, integer7));
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        this.setHomePos(new BlockPos(this));
        this.setTravelPos(BlockPos.ORIGIN);
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        final BlockPos blockPos3 = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
        return blockPos3.getY() < iWorld.getSeaLevel() + 4 && super.canSpawn(iWorld, spawnType);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new TurtleEscapeDangerGoal(this, 1.2));
        this.goalSelector.add(1, new MateGoal(this, 1.0));
        this.goalSelector.add(1, new LayEggGoal(this, 1.0));
        this.goalSelector.add(2, new ApproachFoodHoldingPlayerGoal(this, 1.1, Blocks.aT.getItem()));
        this.goalSelector.add(3, new WanderInWaterGoal(this, 1.0));
        this.goalSelector.add(4, new GoHomeGoal(this, 1.0));
        this.goalSelector.add(7, new TravelGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(9, new WanderOnLandGoal(this, 1.0, 100));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }
    
    @Override
    public boolean canFly() {
        return false;
    }
    
    @Override
    public boolean canBreatheInWater() {
        return true;
    }
    
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }
    
    @Override
    public int getMinAmbientSoundDelay() {
        return 200;
    }
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (!this.isInsideWater() && this.onGround && !this.isChild()) {
            return SoundEvents.lV;
        }
        return super.getAmbientSound();
    }
    
    @Override
    protected void playSwimSound(final float volume) {
        super.playSwimSound(volume * 1.5f);
    }
    
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.mg;
    }
    
    @Nullable
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        if (this.isChild()) {
            return SoundEvents.mc;
        }
        return SoundEvents.mb;
    }
    
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        if (this.isChild()) {
            return SoundEvents.lX;
        }
        return SoundEvents.lW;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        final SoundEvent soundEvent3 = this.isChild() ? SoundEvents.mf : SoundEvents.me;
        this.playSound(soundEvent3, 0.15f, 1.0f);
    }
    
    @Override
    public boolean canEat() {
        return super.canEat() && !this.hasEgg();
    }
    
    @Override
    protected float af() {
        return this.F + 0.15f;
    }
    
    @Override
    public float getScaleFactor() {
        return this.isChild() ? 0.3f : 1.0f;
    }
    
    @Override
    protected EntityNavigation createNavigation(final World world) {
        return new TurtleSwimNavigation(this, world);
    }
    
    @Nullable
    @Override
    public PassiveEntity createChild(final PassiveEntity mate) {
        return EntityType.TURTLE.create(this.world);
    }
    
    @Override
    public boolean isBreedingItem(final ItemStack stack) {
        return stack.getItem() == Blocks.aT.getItem();
    }
    
    @Override
    public float getPathfindingFavor(final BlockPos pos, final ViewableWorld world) {
        if (!this.isLandBound() && world.getFluidState(pos).matches(FluidTags.a)) {
            return 10.0f;
        }
        return super.getPathfindingFavor(pos, world);
    }
    
    @Override
    public void updateState() {
        super.updateState();
        if (this.isAlive() && this.isDiggingSand() && this.sandDiggingCounter >= 1 && this.sandDiggingCounter % 5 == 0) {
            final BlockPos blockPos1 = new BlockPos(this);
            if (this.world.getBlockState(blockPos1.down()).getBlock() == Blocks.C) {
                this.world.playLevelEvent(2001, blockPos1, Block.getRawIdFromState(Blocks.C.getDefaultState()));
            }
        }
    }
    
    @Override
    protected void onGrowUp() {
        super.onGrowUp();
        if (!this.isChild() && this.world.getGameRules().getBoolean("doMobLoot")) {
            this.dropItem(Items.iZ, 1);
        }
    }
    
    @Override
    public void travel(final Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isInsideWater()) {
            this.updateVelocity(0.1f, movementInput);
            this.move(MovementType.a, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
            if (this.getTarget() == null && (!this.isLandBound() || !this.getHomePos().isWithinDistance(this.getPos(), 20.0))) {
                this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
            }
        }
        else {
            super.travel(movementInput);
        }
    }
    
    @Override
    public boolean canBeLeashedBy(final PlayerEntity player) {
        return false;
    }
    
    @Override
    public void onStruckByLightning(final LightningEntity lightning) {
        this.damage(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
    }
    
    static {
        HOME_POS = DataTracker.<BlockPos>registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
        HAS_EGG = DataTracker.<Boolean>registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        DIGGING_SAND = DataTracker.<Boolean>registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        TRAVEL_POS = DataTracker.<BlockPos>registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
        LAND_BOUND = DataTracker.<Boolean>registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ACTIVELY_TRAVELLING = DataTracker.<Boolean>registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        BABY_TURTLE_ON_LAND_FILTER = (livingEntity -> livingEntity.isChild() && !livingEntity.isInsideWater());
    }
    
    static class TurtleEscapeDangerGoal extends EscapeDangerGoal
    {
        TurtleEscapeDangerGoal(final TurtleEntity owner, final double speed) {
            super(owner, speed);
        }
        
        @Override
        public boolean canStart() {
            if (this.owner.getAttacker() == null && !this.owner.isOnFire()) {
                return false;
            }
            final BlockPos blockPos1 = this.locateClosestWater(this.owner.world, this.owner, 7, 4);
            if (blockPos1 != null) {
                this.targetX = blockPos1.getX();
                this.targetY = blockPos1.getY();
                this.targetZ = blockPos1.getZ();
                return true;
            }
            return this.findTarget();
        }
    }
    
    static class TravelGoal extends Goal
    {
        private final TurtleEntity owner;
        private final double speed;
        private boolean noPath;
        
        TravelGoal(final TurtleEntity turtle, final double speed) {
            this.owner = turtle;
            this.speed = speed;
        }
        
        @Override
        public boolean canStart() {
            return !this.owner.isLandBound() && !this.owner.hasEgg() && this.owner.isInsideWater();
        }
        
        @Override
        public void start() {
            final int integer1 = 512;
            final int integer2 = 4;
            final Random random3 = this.owner.random;
            final int integer3 = random3.nextInt(1025) - 512;
            int integer4 = random3.nextInt(9) - 4;
            final int integer5 = random3.nextInt(1025) - 512;
            if (integer4 + this.owner.y > this.owner.world.getSeaLevel() - 1) {
                integer4 = 0;
            }
            final BlockPos blockPos7 = new BlockPos(integer3 + this.owner.x, integer4 + this.owner.y, integer5 + this.owner.z);
            this.owner.setTravelPos(blockPos7);
            this.owner.setActivelyTravelling(true);
            this.noPath = false;
        }
        
        @Override
        public void tick() {
            if (this.owner.getNavigation().isIdle()) {
                final BlockPos blockPos1 = this.owner.getTravelPos();
                Vec3d vec3d2 = PathfindingUtil.a(this.owner, 16, 3, new Vec3d(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()), 0.3141592741012573);
                if (vec3d2 == null) {
                    vec3d2 = PathfindingUtil.a(this.owner, 8, 7, new Vec3d(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()));
                }
                if (vec3d2 != null) {
                    final int integer3 = MathHelper.floor(vec3d2.x);
                    final int integer4 = MathHelper.floor(vec3d2.z);
                    final int integer5 = 34;
                    if (!this.owner.world.isAreaLoaded(integer3 - 34, 0, integer4 - 34, integer3 + 34, 0, integer4 + 34)) {
                        vec3d2 = null;
                    }
                }
                if (vec3d2 == null) {
                    this.noPath = true;
                    return;
                }
                this.owner.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
            }
        }
        
        @Override
        public boolean shouldContinue() {
            return !this.owner.getNavigation().isIdle() && !this.noPath && !this.owner.isLandBound() && !this.owner.isInLove() && !this.owner.hasEgg();
        }
        
        @Override
        public void stop() {
            this.owner.setActivelyTravelling(false);
            super.stop();
        }
    }
    
    static class GoHomeGoal extends Goal
    {
        private final TurtleEntity owner;
        private final double speed;
        private boolean noPath;
        private int homeReachingTryTicks;
        
        GoHomeGoal(final TurtleEntity owner, final double speed) {
            this.owner = owner;
            this.speed = speed;
        }
        
        @Override
        public boolean canStart() {
            return !this.owner.isChild() && (this.owner.hasEgg() || (this.owner.getRand().nextInt(700) == 0 && !this.owner.getHomePos().isWithinDistance(this.owner.getPos(), 64.0)));
        }
        
        @Override
        public void start() {
            this.owner.setLandBound(true);
            this.noPath = false;
            this.homeReachingTryTicks = 0;
        }
        
        @Override
        public void stop() {
            this.owner.setLandBound(false);
        }
        
        @Override
        public boolean shouldContinue() {
            return !this.owner.getHomePos().isWithinDistance(this.owner.getPos(), 7.0) && !this.noPath && this.homeReachingTryTicks <= 600;
        }
        
        @Override
        public void tick() {
            final BlockPos blockPos1 = this.owner.getHomePos();
            final boolean boolean2 = blockPos1.isWithinDistance(this.owner.getPos(), 16.0);
            if (boolean2) {
                ++this.homeReachingTryTicks;
            }
            if (this.owner.getNavigation().isIdle()) {
                Vec3d vec3d3 = PathfindingUtil.a(this.owner, 16, 3, new Vec3d(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()), 0.3141592741012573);
                if (vec3d3 == null) {
                    vec3d3 = PathfindingUtil.a(this.owner, 8, 7, new Vec3d(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()));
                }
                if (vec3d3 != null && !boolean2 && this.owner.world.getBlockState(new BlockPos(vec3d3)).getBlock() != Blocks.A) {
                    vec3d3 = PathfindingUtil.a(this.owner, 16, 5, new Vec3d(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()));
                }
                if (vec3d3 == null) {
                    this.noPath = true;
                    return;
                }
                this.owner.getNavigation().startMovingTo(vec3d3.x, vec3d3.y, vec3d3.z, this.speed);
            }
        }
    }
    
    static class ApproachFoodHoldingPlayerGoal extends Goal
    {
        private static final TargetPredicate CLOSE_ENTITY_PREDICATE;
        private final TurtleEntity owner;
        private final double speed;
        private PlayerEntity targetPlayer;
        private int cooldown;
        private final Set<Item> attractiveItems;
        
        ApproachFoodHoldingPlayerGoal(final TurtleEntity owner, final double speed, final Item attractiveItem) {
            this.owner = owner;
            this.speed = speed;
            this.attractiveItems = Sets.<Item>newHashSet(attractiveItem);
            this.setControls(EnumSet.<Control>of(Control.a, Control.b));
        }
        
        @Override
        public boolean canStart() {
            if (this.cooldown > 0) {
                --this.cooldown;
                return false;
            }
            this.targetPlayer = this.owner.world.getClosestPlayer(ApproachFoodHoldingPlayerGoal.CLOSE_ENTITY_PREDICATE, this.owner);
            return this.targetPlayer != null && (this.isAttractive(this.targetPlayer.getMainHandStack()) || this.isAttractive(this.targetPlayer.getOffHandStack()));
        }
        
        private boolean isAttractive(final ItemStack stack) {
            return this.attractiveItems.contains(stack.getItem());
        }
        
        @Override
        public boolean shouldContinue() {
            return this.canStart();
        }
        
        @Override
        public void stop() {
            this.targetPlayer = null;
            this.owner.getNavigation().stop();
            this.cooldown = 100;
        }
        
        @Override
        public void tick() {
            this.owner.getLookControl().lookAt(this.targetPlayer, (float)(this.owner.dA() + 20), (float)this.owner.getLookPitchSpeed());
            if (this.owner.squaredDistanceTo(this.targetPlayer) < 6.25) {
                this.owner.getNavigation().stop();
            }
            else {
                this.owner.getNavigation().startMovingTo(this.targetPlayer, this.speed);
            }
        }
        
        static {
            CLOSE_ENTITY_PREDICATE = new TargetPredicate().setBaseMaxDistance(10.0).includeTeammates().includeInvulnerable();
        }
    }
    
    static class MateGoal extends AnimalMateGoal
    {
        private final TurtleEntity turtle;
        
        MateGoal(final TurtleEntity owner, final double speed) {
            super(owner, speed);
            this.turtle = owner;
        }
        
        @Override
        public boolean canStart() {
            return super.canStart() && !this.turtle.hasEgg();
        }
        
        @Override
        protected void breed() {
            ServerPlayerEntity serverPlayerEntity1 = this.owner.getLovingPlayer();
            if (serverPlayerEntity1 == null && this.mate.getLovingPlayer() != null) {
                serverPlayerEntity1 = this.mate.getLovingPlayer();
            }
            if (serverPlayerEntity1 != null) {
                serverPlayerEntity1.incrementStat(Stats.N);
                Criterions.BRED_ANIMALS.handle(serverPlayerEntity1, this.owner, this.mate, null);
            }
            this.turtle.setHasEgg(true);
            this.owner.resetLoveTicks();
            this.mate.resetLoveTicks();
            final Random random2 = this.owner.getRand();
            if (this.world.getGameRules().getBoolean("doMobLoot")) {
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.owner.x, this.owner.y, this.owner.z, random2.nextInt(7) + 1));
            }
        }
    }
    
    static class LayEggGoal extends MoveToTargetPosGoal
    {
        private final TurtleEntity turtle;
        
        LayEggGoal(final TurtleEntity turtle, final double speed) {
            super(turtle, speed, 16);
            this.turtle = turtle;
        }
        
        @Override
        public boolean canStart() {
            return this.turtle.hasEgg() && this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 9.0) && super.canStart();
        }
        
        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && this.turtle.hasEgg() && this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 9.0);
        }
        
        @Override
        public void tick() {
            super.tick();
            final BlockPos blockPos1 = new BlockPos(this.turtle);
            if (!this.turtle.isInsideWater() && this.hasReached()) {
                if (this.turtle.sandDiggingCounter < 1) {
                    this.turtle.setDiggingSand(true);
                }
                else if (this.turtle.sandDiggingCounter > 200) {
                    final World world2 = this.turtle.world;
                    world2.playSound(null, blockPos1, SoundEvents.md, SoundCategory.e, 0.3f, 0.9f + world2.random.nextFloat() * 0.2f);
                    world2.setBlockState(this.targetPos.up(), ((AbstractPropertyContainer<O, BlockState>)Blocks.jX.getDefaultState()).<Comparable, Integer>with((Property<Comparable>)TurtleEggBlock.EGGS, this.turtle.random.nextInt(4) + 1), 3);
                    this.turtle.setHasEgg(false);
                    this.turtle.setDiggingSand(false);
                    this.turtle.setLoveTicks(600);
                }
                if (this.turtle.isDiggingSand()) {
                    this.turtle.sandDiggingCounter++;
                }
            }
        }
        
        @Override
        protected boolean isTargetPos(final ViewableWorld world, final BlockPos pos) {
            if (!world.isAir(pos.up())) {
                return false;
            }
            final Block block3 = world.getBlockState(pos).getBlock();
            return block3 == Blocks.C;
        }
    }
    
    static class WanderOnLandGoal extends WanderAroundGoal
    {
        private final TurtleEntity turtle;
        
        private WanderOnLandGoal(final TurtleEntity turtleEntity, final double double2, final int integer4) {
            super(turtleEntity, double2, integer4);
            this.turtle = turtleEntity;
        }
        
        @Override
        public boolean canStart() {
            return !this.owner.isInsideWater() && !this.turtle.isLandBound() && !this.turtle.hasEgg() && super.canStart();
        }
    }
    
    static class WanderInWaterGoal extends MoveToTargetPosGoal
    {
        private final TurtleEntity turtle;
        
        private WanderInWaterGoal(final TurtleEntity turtle, final double speed) {
            super(turtle, turtle.isChild() ? 2.0 : speed, 24);
            this.turtle = turtle;
            this.lowestY = -1;
        }
        
        @Override
        public boolean shouldContinue() {
            return !this.turtle.isInsideWater() && this.tryingTime <= 1200 && this.isTargetPos(this.turtle.world, this.targetPos);
        }
        
        @Override
        public boolean canStart() {
            if (this.turtle.isChild() && !this.turtle.isInsideWater()) {
                return super.canStart();
            }
            return !this.turtle.isLandBound() && !this.turtle.isInsideWater() && !this.turtle.hasEgg() && super.canStart();
        }
        
        @Override
        public boolean shouldResetPath() {
            return this.tryingTime % 160 == 0;
        }
        
        @Override
        protected boolean isTargetPos(final ViewableWorld world, final BlockPos pos) {
            final Block block3 = world.getBlockState(pos).getBlock();
            return block3 == Blocks.A;
        }
    }
    
    static class TurtleMoveControl extends MoveControl
    {
        private final TurtleEntity turtle;
        
        TurtleMoveControl(final TurtleEntity owner) {
            super(owner);
            this.turtle = owner;
        }
        
        private void updateVelocity() {
            if (this.turtle.isInsideWater()) {
                this.turtle.setVelocity(this.turtle.getVelocity().add(0.0, 0.005, 0.0));
                if (!this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 16.0)) {
                    this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0f, 0.08f));
                }
                if (this.turtle.isChild()) {
                    this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 3.0f, 0.06f));
                }
            }
            else if (this.turtle.onGround) {
                this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0f, 0.06f));
            }
        }
        
        @Override
        public void tick() {
            this.updateVelocity();
            if (this.state != State.b || this.turtle.getNavigation().isIdle()) {
                this.turtle.setMovementSpeed(0.0f);
                return;
            }
            final double double1 = this.targetX - this.turtle.x;
            double double2 = this.targetY - this.turtle.y;
            final double double3 = this.targetZ - this.turtle.z;
            final double double4 = MathHelper.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
            double2 /= double4;
            final float float9 = (float)(MathHelper.atan2(double3, double1) * 57.2957763671875) - 90.0f;
            this.turtle.yaw = this.changeAngle(this.turtle.yaw, float9, 90.0f);
            this.turtle.aK = this.turtle.yaw;
            final float float10 = (float)(this.speed * this.turtle.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
            this.turtle.setMovementSpeed(MathHelper.lerp(0.125f, this.turtle.getMovementSpeed(), float10));
            this.turtle.setVelocity(this.turtle.getVelocity().add(0.0, this.turtle.getMovementSpeed() * double2 * 0.1, 0.0));
        }
    }
    
    static class TurtleSwimNavigation extends SwimNavigation
    {
        TurtleSwimNavigation(final TurtleEntity owner, final World world) {
            super(owner, world);
        }
        
        @Override
        protected boolean isAtValidPosition() {
            return true;
        }
        
        @Override
        protected PathNodeNavigator createPathNodeNavigator(final int integer) {
            return new PathNodeNavigator(new AmphibiousPathNodeMaker(), integer);
        }
        
        @Override
        public boolean isValidPosition(final BlockPos pos) {
            if (this.entity instanceof TurtleEntity) {
                final TurtleEntity turtleEntity2 = (TurtleEntity)this.entity;
                if (turtleEntity2.isActivelyTravelling()) {
                    return this.world.getBlockState(pos).getBlock() == Blocks.A;
                }
            }
            return !this.world.getBlockState(pos.down()).isAir();
        }
    }
}
