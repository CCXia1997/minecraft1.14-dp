package net.minecraft.entity.mob;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.block.Block;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import java.util.Random;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.MathHelper;
import java.util.EnumSet;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.item.ItemProvider;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import javax.annotation.Nullable;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import java.util.Optional;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import java.util.UUID;

public class EndermanEntity extends HostileEntity
{
    private static final UUID b;
    private static final EntityAttributeModifier c;
    private static final TrackedData<Optional<BlockState>> CARRIED_BLOCK;
    private static final TrackedData<Boolean> ANGRY;
    private static final Predicate<LivingEntity> PLAYER_ENDERMITE_PREDICATE;
    private int lastAngrySoundAge;
    private int ageWhenTargetSet;
    
    public EndermanEntity(final EntityType<? extends EndermanEntity> type, final World world) {
        super(type, world);
        this.stepHeight = 1.0f;
        this.setPathNodeTypeWeight(PathNodeType.g, -1.0f);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ChasePlayerGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0, 0.0f));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(10, new PlaceBlockGoal(this));
        this.goalSelector.add(11, new PickUpBlockGoal(this));
        this.targetSelector.add(1, new TeleportTowardsPlayerGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, EndermiteEntity.class, 10, true, false, EndermanEntity.PLAYER_ENDERMITE_PREDICATE));
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(40.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(64.0);
    }
    
    @Override
    public void setTarget(@Nullable final LivingEntity target) {
        super.setTarget(target);
        final EntityAttributeInstance entityAttributeInstance2 = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (target == null) {
            this.ageWhenTargetSet = 0;
            this.dataTracker.<Boolean>set(EndermanEntity.ANGRY, false);
            entityAttributeInstance2.removeModifier(EndermanEntity.c);
        }
        else {
            this.ageWhenTargetSet = this.age;
            this.dataTracker.<Boolean>set(EndermanEntity.ANGRY, true);
            if (!entityAttributeInstance2.hasModifier(EndermanEntity.c)) {
                entityAttributeInstance2.addModifier(EndermanEntity.c);
            }
        }
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Optional<BlockState>>startTracking(EndermanEntity.CARRIED_BLOCK, Optional.<BlockState>empty());
        this.dataTracker.<Boolean>startTracking(EndermanEntity.ANGRY, false);
    }
    
    public void playAngrySound() {
        if (this.age >= this.lastAngrySoundAge + 400) {
            this.lastAngrySoundAge = this.age;
            if (!this.isSilent()) {
                this.world.playSound(this.x, this.y + this.getStandingEyeHeight(), this.z, SoundEvents.cJ, this.getSoundCategory(), 2.5f, 1.0f, false);
            }
        }
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (EndermanEntity.ANGRY.equals(data) && this.isAngry() && this.world.isClient) {
            this.playAngrySound();
        }
        super.onTrackedDataSet(data);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        final BlockState blockState2 = this.getCarriedBlock();
        if (blockState2 != null) {
            tag.put("carriedBlockState", TagHelper.serializeBlockState(blockState2));
        }
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        BlockState blockState2 = null;
        if (tag.containsKey("carriedBlockState", 10)) {
            blockState2 = TagHelper.deserializeBlockState(tag.getCompound("carriedBlockState"));
            if (blockState2.isAir()) {
                blockState2 = null;
            }
        }
        this.setCarriedBlock(blockState2);
    }
    
    private boolean isPlayerStaring(final PlayerEntity playerEntity) {
        final ItemStack itemStack2 = playerEntity.inventory.armor.get(3);
        if (itemStack2.getItem() == Blocks.cN.getItem()) {
            return false;
        }
        final Vec3d vec3d3 = playerEntity.getRotationVec(1.0f).normalize();
        Vec3d vec3d4 = new Vec3d(this.x - playerEntity.x, this.getBoundingBox().minY + this.getStandingEyeHeight() - (playerEntity.y + playerEntity.getStandingEyeHeight()), this.z - playerEntity.z);
        final double double5 = vec3d4.length();
        vec3d4 = vec3d4.normalize();
        final double double6 = vec3d3.dotProduct(vec3d4);
        return double6 > 1.0 - 0.025 / double5 && playerEntity.canSee(this);
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 2.55f;
    }
    
    @Override
    public void updateState() {
        if (this.world.isClient) {
            for (int integer1 = 0; integer1 < 2; ++integer1) {
                this.world.addParticle(ParticleTypes.O, this.x + (this.random.nextDouble() - 0.5) * this.getWidth(), this.y + this.random.nextDouble() * this.getHeight() - 0.25, this.z + (this.random.nextDouble() - 0.5) * this.getWidth(), (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(), (this.random.nextDouble() - 0.5) * 2.0);
            }
        }
        this.jumping = false;
        super.updateState();
    }
    
    @Override
    protected void mobTick() {
        if (this.isTouchingWater()) {
            this.damage(DamageSource.DROWN, 1.0f);
        }
        if (this.world.isDaylight() && this.age >= this.ageWhenTargetSet + 600) {
            final float float1 = this.getBrightnessAtEyes();
            if (float1 > 0.5f && this.world.isSkyVisible(new BlockPos(this)) && this.random.nextFloat() * 30.0f < (float1 - 0.4f) * 2.0f) {
                this.setTarget(null);
                this.teleportRandomly();
            }
        }
        super.mobTick();
    }
    
    protected boolean teleportRandomly() {
        final double double1 = this.x + (this.random.nextDouble() - 0.5) * 64.0;
        final double double2 = this.y + (this.random.nextInt(64) - 32);
        final double double3 = this.z + (this.random.nextDouble() - 0.5) * 64.0;
        return this.teleport(double1, double2, double3);
    }
    
    protected boolean teleportTo(final Entity targetEntity) {
        Vec3d vec3d2 = new Vec3d(this.x - targetEntity.x, this.getBoundingBox().minY + this.getHeight() / 2.0f - targetEntity.y + targetEntity.getStandingEyeHeight(), this.z - targetEntity.z);
        vec3d2 = vec3d2.normalize();
        final double double3 = 16.0;
        final double double4 = this.x + (this.random.nextDouble() - 0.5) * 8.0 - vec3d2.x * 16.0;
        final double double5 = this.y + (this.random.nextInt(16) - 8) - vec3d2.y * 16.0;
        final double double6 = this.z + (this.random.nextDouble() - 0.5) * 8.0 - vec3d2.z * 16.0;
        return this.teleport(double4, double5, double6);
    }
    
    private boolean teleport(final double x, final double y, final double z) {
        final boolean boolean7 = this.teleport(x, y, z, true);
        if (boolean7) {
            this.world.playSound(null, this.prevX, this.prevY, this.prevZ, SoundEvents.cK, this.getSoundCategory(), 1.0f, 1.0f);
            this.playSound(SoundEvents.cK, 1.0f, 1.0f);
        }
        return boolean7;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isAngry() ? SoundEvents.cI : SoundEvents.cF;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.cH;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.cG;
    }
    
    @Override
    protected void dropEquipment(final DamageSource damageSource, final int addedDropChance, final boolean dropAllowed) {
        super.dropEquipment(damageSource, addedDropChance, dropAllowed);
        final BlockState blockState4 = this.getCarriedBlock();
        if (blockState4 != null) {
            this.dropItem(blockState4.getBlock());
        }
    }
    
    public void setCarriedBlock(@Nullable final BlockState blockState) {
        this.dataTracker.<Optional<BlockState>>set(EndermanEntity.CARRIED_BLOCK, Optional.<BlockState>ofNullable(blockState));
    }
    
    @Nullable
    public BlockState getCarriedBlock() {
        return this.dataTracker.<Optional<BlockState>>get(EndermanEntity.CARRIED_BLOCK).orElse(null);
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source instanceof ProjectileDamageSource || source == DamageSource.FIREWORKS) {
            for (int integer3 = 0; integer3 < 64; ++integer3) {
                if (this.teleportRandomly()) {
                    return true;
                }
            }
            return false;
        }
        final boolean boolean3 = super.damage(source, amount);
        if (source.bypassesArmor() && this.random.nextInt(10) != 0) {
            this.teleportRandomly();
        }
        return boolean3;
    }
    
    public boolean isAngry() {
        return this.dataTracker.<Boolean>get(EndermanEntity.ANGRY);
    }
    
    static {
        b = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
        c = new EntityAttributeModifier(EndermanEntity.b, "Attacking speed boost", 0.15000000596046448, EntityAttributeModifier.Operation.a).setSerialize(false);
        CARRIED_BLOCK = DataTracker.<Optional<BlockState>>registerData(EndermanEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);
        ANGRY = DataTracker.<Boolean>registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        PLAYER_ENDERMITE_PREDICATE = (livingEntity -> livingEntity instanceof EndermiteEntity && livingEntity.isPlayerSpawned());
    }
    
    static class TeleportTowardsPlayerGoal extends FollowTargetGoal<PlayerEntity>
    {
        private final EndermanEntity endermanEntity;
        private PlayerEntity targetPlayer;
        private int lookAtPlayerWarmup;
        private int ticksSinceUnseenTeleport;
        private final TargetPredicate staringPlayerPredicate;
        private final TargetPredicate validTargetPredicate;
        
        public TeleportTowardsPlayerGoal(final EndermanEntity endermanEntity) {
            super(endermanEntity, PlayerEntity.class, false);
            this.validTargetPredicate = new TargetPredicate().includeHidden();
            this.endermanEntity = endermanEntity;
            this.staringPlayerPredicate = new TargetPredicate().setBaseMaxDistance(this.getFollowRange()).setPredicate(playerEntity -> endermanEntity.isPlayerStaring(playerEntity));
        }
        
        @Override
        public boolean canStart() {
            this.targetPlayer = this.endermanEntity.world.getClosestPlayer(this.staringPlayerPredicate, this.endermanEntity);
            return this.targetPlayer != null;
        }
        
        @Override
        public void start() {
            this.lookAtPlayerWarmup = 5;
            this.ticksSinceUnseenTeleport = 0;
        }
        
        @Override
        public void stop() {
            this.targetPlayer = null;
            super.stop();
        }
        
        @Override
        public boolean shouldContinue() {
            if (this.targetPlayer == null) {
                return (this.targetEntity != null && this.validTargetPredicate.test(this.endermanEntity, this.targetEntity)) || super.shouldContinue();
            }
            if (!this.endermanEntity.isPlayerStaring(this.targetPlayer)) {
                return false;
            }
            this.endermanEntity.lookAtEntity(this.targetPlayer, 10.0f, 10.0f);
            return true;
        }
        
        @Override
        public void tick() {
            if (this.targetPlayer != null) {
                if (--this.lookAtPlayerWarmup <= 0) {
                    this.targetEntity = this.targetPlayer;
                    this.targetPlayer = null;
                    super.start();
                }
            }
            else {
                if (this.targetEntity != null && !this.endermanEntity.hasVehicle()) {
                    if (this.endermanEntity.isPlayerStaring((PlayerEntity)this.targetEntity)) {
                        if (this.targetEntity.squaredDistanceTo(this.endermanEntity) < 16.0) {
                            this.endermanEntity.teleportRandomly();
                        }
                        this.ticksSinceUnseenTeleport = 0;
                    }
                    else if (this.targetEntity.squaredDistanceTo(this.endermanEntity) > 256.0 && this.ticksSinceUnseenTeleport++ >= 30 && this.endermanEntity.teleportTo(this.targetEntity)) {
                        this.ticksSinceUnseenTeleport = 0;
                    }
                }
                super.tick();
            }
        }
    }
    
    static class ChasePlayerGoal extends Goal
    {
        private final EndermanEntity endermanEntity;
        
        public ChasePlayerGoal(final EndermanEntity endermanEntity) {
            this.endermanEntity = endermanEntity;
            this.setControls(EnumSet.<Control>of(Control.c, Control.a));
        }
        
        @Override
        public boolean canStart() {
            final LivingEntity livingEntity1 = this.endermanEntity.getTarget();
            if (!(livingEntity1 instanceof PlayerEntity)) {
                return false;
            }
            final double double2 = livingEntity1.squaredDistanceTo(this.endermanEntity);
            return double2 <= 256.0 && this.endermanEntity.isPlayerStaring((PlayerEntity)livingEntity1);
        }
        
        @Override
        public void start() {
            this.endermanEntity.getNavigation().stop();
        }
    }
    
    static class PlaceBlockGoal extends Goal
    {
        private final EndermanEntity owner;
        
        public PlaceBlockGoal(final EndermanEntity endermanEntity) {
            this.owner = endermanEntity;
        }
        
        @Override
        public boolean canStart() {
            return this.owner.getCarriedBlock() != null && this.owner.world.getGameRules().getBoolean("mobGriefing") && this.owner.getRand().nextInt(2000) == 0;
        }
        
        @Override
        public void tick() {
            final Random random1 = this.owner.getRand();
            final IWorld iWorld2 = this.owner.world;
            final int integer3 = MathHelper.floor(this.owner.x - 1.0 + random1.nextDouble() * 2.0);
            final int integer4 = MathHelper.floor(this.owner.y + random1.nextDouble() * 2.0);
            final int integer5 = MathHelper.floor(this.owner.z - 1.0 + random1.nextDouble() * 2.0);
            final BlockPos blockPos6 = new BlockPos(integer3, integer4, integer5);
            final BlockState blockState7 = iWorld2.getBlockState(blockPos6);
            final BlockPos blockPos7 = blockPos6.down();
            final BlockState blockState8 = iWorld2.getBlockState(blockPos7);
            final BlockState blockState9 = this.owner.getCarriedBlock();
            if (blockState9 != null && this.a(iWorld2, blockPos6, blockState9, blockState7, blockState8, blockPos7)) {
                iWorld2.setBlockState(blockPos6, blockState9, 3);
                this.owner.setCarriedBlock(null);
            }
        }
        
        private boolean a(final ViewableWorld viewableWorld, final BlockPos blockPos2, final BlockState blockState3, final BlockState blockState4, final BlockState blockState5, final BlockPos blockPos6) {
            return blockState4.isAir() && !blockState5.isAir() && Block.isShapeFullCube(blockState5.getCollisionShape(viewableWorld, blockPos6)) && blockState3.canPlaceAt(viewableWorld, blockPos2);
        }
    }
    
    static class PickUpBlockGoal extends Goal
    {
        private final EndermanEntity owner;
        
        public PickUpBlockGoal(final EndermanEntity endermanEntity) {
            this.owner = endermanEntity;
        }
        
        @Override
        public boolean canStart() {
            return this.owner.getCarriedBlock() == null && this.owner.world.getGameRules().getBoolean("mobGriefing") && this.owner.getRand().nextInt(20) == 0;
        }
        
        @Override
        public void tick() {
            final Random random1 = this.owner.getRand();
            final World world2 = this.owner.world;
            final int integer3 = MathHelper.floor(this.owner.x - 2.0 + random1.nextDouble() * 4.0);
            final int integer4 = MathHelper.floor(this.owner.y + random1.nextDouble() * 3.0);
            final int integer5 = MathHelper.floor(this.owner.z - 2.0 + random1.nextDouble() * 4.0);
            final BlockPos blockPos6 = new BlockPos(integer3, integer4, integer5);
            final BlockState blockState7 = world2.getBlockState(blockPos6);
            final Block block8 = blockState7.getBlock();
            final Vec3d vec3d9 = new Vec3d(MathHelper.floor(this.owner.x) + 0.5, integer4 + 0.5, MathHelper.floor(this.owner.z) + 0.5);
            final Vec3d vec3d10 = new Vec3d(integer3 + 0.5, integer4 + 0.5, integer5 + 0.5);
            final BlockHitResult blockHitResult11 = world2.rayTrace(new RayTraceContext(vec3d9, vec3d10, RayTraceContext.ShapeType.a, RayTraceContext.FluidHandling.NONE, this.owner));
            final boolean boolean12 = blockHitResult11.getType() != HitResult.Type.NONE && blockHitResult11.getBlockPos().equals(blockPos6);
            if (block8.matches(BlockTags.I) && boolean12) {
                this.owner.setCarriedBlock(blockState7);
                world2.clearBlockState(blockPos6, false);
            }
        }
    }
}
