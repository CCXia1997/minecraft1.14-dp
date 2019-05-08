package net.minecraft.entity.passive;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.math.BlockPos;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorld;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.recipe.Ingredient;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.ItemProvider;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.entity.data.TrackedData;

public class RabbitEntity extends AnimalEntity
{
    private static final TrackedData<Integer> RABBIT_TYPE;
    private static final Identifier KILLER_BUNNY;
    private int jumpTicks;
    private int jumpDuration;
    private boolean lastOnGround;
    private int ticksUntilJump;
    private int moreCarrotTicks;
    
    public RabbitEntity(final EntityType<? extends RabbitEntity> type, final World world) {
        super(type, world);
        this.jumpControl = new RabbitJumpControl(this);
        this.moveControl = new RabbitMoveControl(this);
        this.setSpeed(0.0);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new RabbitEscapeDangerGoal(this, 2.2));
        this.goalSelector.add(2, new AnimalMateGoal(this, 0.8));
        this.goalSelector.add(3, new TemptGoal(this, 1.0, Ingredient.ofItems(Items.nI, Items.nN, Blocks.bo), false));
        this.goalSelector.add(4, new RabbitFleeGoal<>(this, PlayerEntity.class, 8.0f, 2.2, 2.2));
        this.goalSelector.add(4, new RabbitFleeGoal<>(this, WolfEntity.class, 10.0f, 2.2, 2.2));
        this.goalSelector.add(4, new RabbitFleeGoal<>(this, HostileEntity.class, 4.0f, 2.2, 2.2));
        this.goalSelector.add(5, new g(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6));
        this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
    }
    
    @Override
    protected float getJumpVelocity() {
        if (this.horizontalCollision || (this.moveControl.isMoving() && this.moveControl.getTargetY() > this.y + 0.5)) {
            return 0.5f;
        }
        final Path path1 = this.navigation.getCurrentPath();
        if (path1 != null && path1.getCurrentNodeIndex() < path1.getLength()) {
            final Vec3d vec3d2 = path1.getNodePosition(this);
            if (vec3d2.y > this.y + 0.5) {
                return 0.5f;
            }
        }
        if (this.moveControl.getSpeed() <= 0.6) {
            return 0.2f;
        }
        return 0.3f;
    }
    
    @Override
    protected void jump() {
        super.jump();
        final double double1 = this.moveControl.getSpeed();
        if (double1 > 0.0) {
            final double double2 = Entity.squaredHorizontalLength(this.getVelocity());
            if (double2 < 0.01) {
                this.updateVelocity(0.1f, new Vec3d(0.0, 0.0, 1.0));
            }
        }
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte)1);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public float v(final float float1) {
        if (this.jumpDuration == 0) {
            return 0.0f;
        }
        return (this.jumpTicks + float1) / this.jumpDuration;
    }
    
    public void setSpeed(final double speed) {
        this.getNavigation().setSpeed(speed);
        this.moveControl.moveTo(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ(), speed);
    }
    
    @Override
    public void setJumping(final boolean jumping) {
        super.setJumping(jumping);
        if (jumping) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * 0.8f);
        }
    }
    
    public void startJump() {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Integer>startTracking(RabbitEntity.RABBIT_TYPE, 0);
    }
    
    public void mobTick() {
        if (this.ticksUntilJump > 0) {
            --this.ticksUntilJump;
        }
        if (this.moreCarrotTicks > 0) {
            this.moreCarrotTicks -= this.random.nextInt(3);
            if (this.moreCarrotTicks < 0) {
                this.moreCarrotTicks = 0;
            }
        }
        if (this.onGround) {
            if (!this.lastOnGround) {
                this.setJumping(false);
                this.ef();
            }
            if (this.getRabbitType() == 99 && this.ticksUntilJump == 0) {
                final LivingEntity livingEntity1 = this.getTarget();
                if (livingEntity1 != null && this.squaredDistanceTo(livingEntity1) < 16.0) {
                    this.lookTowards(livingEntity1.x, livingEntity1.z);
                    this.moveControl.moveTo(livingEntity1.x, livingEntity1.y, livingEntity1.z, this.moveControl.getSpeed());
                    this.startJump();
                    this.lastOnGround = true;
                }
            }
            final RabbitJumpControl rabbitJumpControl1 = (RabbitJumpControl)this.jumpControl;
            if (!rabbitJumpControl1.isActive()) {
                if (this.moveControl.isMoving() && this.ticksUntilJump == 0) {
                    final Path path2 = this.navigation.getCurrentPath();
                    Vec3d vec3d3 = new Vec3d(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ());
                    if (path2 != null && path2.getCurrentNodeIndex() < path2.getLength()) {
                        vec3d3 = path2.getNodePosition(this);
                    }
                    this.lookTowards(vec3d3.x, vec3d3.z);
                    this.startJump();
                }
            }
            else if (!rabbitJumpControl1.d()) {
                this.dY();
            }
        }
        this.lastOnGround = this.onGround;
    }
    
    @Override
    public void attemptSprintingParticles() {
    }
    
    private void lookTowards(final double x, final double z) {
        this.yaw = (float)(MathHelper.atan2(z - this.z, x - this.x) * 57.2957763671875) - 90.0f;
    }
    
    private void dY() {
        ((RabbitJumpControl)this.jumpControl).a(true);
    }
    
    private void dZ() {
        ((RabbitJumpControl)this.jumpControl).a(false);
    }
    
    private void scheduleJump() {
        if (this.moveControl.getSpeed() < 2.2) {
            this.ticksUntilJump = 10;
        }
        else {
            this.ticksUntilJump = 1;
        }
    }
    
    private void ef() {
        this.scheduleJump();
        this.dZ();
    }
    
    @Override
    public void updateState() {
        super.updateState();
        if (this.jumpTicks != this.jumpDuration) {
            ++this.jumpTicks;
        }
        else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(3.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("RabbitType", this.getRabbitType());
        tag.putInt("MoreCarrotTicks", this.moreCarrotTicks);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setRabbitType(tag.getInt("RabbitType"));
        this.moreCarrotTicks = tag.getInt("MoreCarrotTicks");
    }
    
    protected SoundEvent getJumpSound() {
        return SoundEvents.jm;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ji;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.jl;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.jk;
    }
    
    @Override
    public boolean tryAttack(final Entity entity) {
        if (this.getRabbitType() == 99) {
            this.playSound(SoundEvents.jj, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            return entity.damage(DamageSource.mob(this), 8.0f);
        }
        return entity.damage(DamageSource.mob(this), 3.0f);
    }
    
    @Override
    public SoundCategory getSoundCategory() {
        return (this.getRabbitType() == 99) ? SoundCategory.f : SoundCategory.g;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        return !this.isInvulnerableTo(source) && super.damage(source, amount);
    }
    
    private boolean isBreedingItem(final Item item) {
        return item == Items.nI || item == Items.nN || item == Blocks.bo.getItem();
    }
    
    @Override
    public RabbitEntity createChild(final PassiveEntity mate) {
        final RabbitEntity rabbitEntity2 = EntityType.RABBIT.create(this.world);
        int integer3 = this.chooseType(this.world);
        if (this.random.nextInt(20) != 0) {
            if (mate instanceof RabbitEntity && this.random.nextBoolean()) {
                integer3 = ((RabbitEntity)mate).getRabbitType();
            }
            else {
                integer3 = this.getRabbitType();
            }
        }
        rabbitEntity2.setRabbitType(integer3);
        return rabbitEntity2;
    }
    
    @Override
    public boolean isBreedingItem(final ItemStack stack) {
        return this.isBreedingItem(stack.getItem());
    }
    
    public int getRabbitType() {
        return this.dataTracker.<Integer>get(RabbitEntity.RABBIT_TYPE);
    }
    
    public void setRabbitType(final int rabbitType) {
        if (rabbitType == 99) {
            this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(8.0);
            this.goalSelector.add(4, new RabbitAttackGoal(this));
            this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
            this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
            this.targetSelector.add(2, new FollowTargetGoal<>(this, WolfEntity.class, true));
            if (!this.hasCustomName()) {
                this.setCustomName(new TranslatableTextComponent(SystemUtil.createTranslationKey("entity", RabbitEntity.KILLER_BUNNY), new Object[0]));
            }
        }
        this.dataTracker.<Integer>set(RabbitEntity.RABBIT_TYPE, rabbitType);
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        int integer6 = this.chooseType(iWorld);
        boolean boolean7 = false;
        if (entityData instanceof RabbitEntityData) {
            integer6 = ((RabbitEntityData)entityData).type;
            boolean7 = true;
        }
        else {
            entityData = new RabbitEntityData(integer6);
        }
        this.setRabbitType(integer6);
        if (boolean7) {
            this.setBreedingAge(-24000);
        }
        return entityData;
    }
    
    private int chooseType(final IWorld world) {
        final Biome biome2 = world.getBiome(new BlockPos(this));
        final int integer3 = this.random.nextInt(100);
        if (biome2.getPrecipitation() == Biome.Precipitation.SNOW) {
            return (integer3 < 80) ? 1 : 3;
        }
        if (biome2.getCategory() == Biome.Category.DESERT) {
            return 4;
        }
        return (integer3 < 50) ? 0 : ((integer3 < 90) ? 5 : 2);
    }
    
    @Override
    public boolean canSpawn(final IWorld iWorld, final SpawnType spawnType) {
        final int integer3 = MathHelper.floor(this.x);
        final int integer4 = MathHelper.floor(this.getBoundingBox().minY);
        final int integer5 = MathHelper.floor(this.z);
        final BlockPos blockPos6 = new BlockPos(integer3, integer4, integer5);
        final Block block7 = iWorld.getBlockState(blockPos6.down()).getBlock();
        return block7 == Blocks.aQ || block7 == Blocks.cA || block7 == Blocks.C || super.canSpawn(iWorld, spawnType);
    }
    
    private boolean wantsCarrots() {
        return this.moreCarrotTicks == 0;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 1) {
            this.spawnSprintingParticles();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        }
        else {
            super.handleStatus(status);
        }
    }
    
    static {
        RABBIT_TYPE = DataTracker.<Integer>registerData(RabbitEntity.class, TrackedDataHandlerRegistry.INTEGER);
        KILLER_BUNNY = new Identifier("killer_bunny");
    }
    
    public static class RabbitEntityData implements EntityData
    {
        public final int type;
        
        public RabbitEntityData(final int type) {
            this.type = type;
        }
    }
    
    public class RabbitJumpControl extends JumpControl
    {
        private final RabbitEntity rabbit;
        private boolean d;
        
        public RabbitJumpControl(final RabbitEntity rabbitEntity2) {
            super(rabbitEntity2);
            this.rabbit = rabbitEntity2;
        }
        
        public boolean isActive() {
            return this.active;
        }
        
        public boolean d() {
            return this.d;
        }
        
        public void a(final boolean boolean1) {
            this.d = boolean1;
        }
        
        @Override
        public void tick() {
            if (this.active) {
                this.rabbit.startJump();
                this.active = false;
            }
        }
    }
    
    static class RabbitMoveControl extends MoveControl
    {
        private final RabbitEntity rabbit;
        private double j;
        
        public RabbitMoveControl(final RabbitEntity rabbitEntity) {
            super(rabbitEntity);
            this.rabbit = rabbitEntity;
        }
        
        @Override
        public void tick() {
            if (this.rabbit.onGround && !this.rabbit.jumping && !((RabbitJumpControl)this.rabbit.jumpControl).isActive()) {
                this.rabbit.setSpeed(0.0);
            }
            else if (this.isMoving()) {
                this.rabbit.setSpeed(this.j);
            }
            super.tick();
        }
        
        @Override
        public void moveTo(final double x, final double y, final double z, double speed) {
            if (this.rabbit.isInsideWater()) {
                speed = 1.5;
            }
            super.moveTo(x, y, z, speed);
            if (speed > 0.0) {
                this.j = speed;
            }
        }
    }
    
    static class RabbitFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T>
    {
        private final RabbitEntity rabbit;
        
        public RabbitFleeGoal(final RabbitEntity rabbitEntity, final Class<T> class2, final float float3, final double double4, final double double6) {
            super(rabbitEntity, class2, float3, double4, double6);
            this.rabbit = rabbitEntity;
        }
        
        @Override
        public boolean canStart() {
            return this.rabbit.getRabbitType() != 99 && super.canStart();
        }
    }
    
    static class g extends MoveToTargetPosGoal
    {
        private final RabbitEntity rabbit;
        private boolean h;
        private boolean i;
        
        public g(final RabbitEntity rabbitEntity) {
            super(rabbitEntity, 0.699999988079071, 16);
            this.rabbit = rabbitEntity;
        }
        
        @Override
        public boolean canStart() {
            if (this.cooldown <= 0) {
                if (!this.rabbit.world.getGameRules().getBoolean("mobGriefing")) {
                    return false;
                }
                this.i = false;
                this.h = this.rabbit.wantsCarrots();
                this.h = true;
            }
            return super.canStart();
        }
        
        @Override
        public boolean shouldContinue() {
            return this.i && super.shouldContinue();
        }
        
        @Override
        public void tick() {
            super.tick();
            this.rabbit.getLookControl().lookAt(this.targetPos.getX() + 0.5, this.targetPos.getY() + 1, this.targetPos.getZ() + 0.5, 10.0f, (float)this.rabbit.getLookPitchSpeed());
            if (this.hasReached()) {
                final World world1 = this.rabbit.world;
                final BlockPos blockPos2 = this.targetPos.up();
                final BlockState blockState3 = world1.getBlockState(blockPos2);
                final Block block4 = blockState3.getBlock();
                if (this.i && block4 instanceof CarrotsBlock) {
                    final Integer integer5 = blockState3.<Integer>get((Property<Integer>)CarrotsBlock.AGE);
                    if (integer5 == 0) {
                        world1.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 2);
                        world1.breakBlock(blockPos2, true);
                    }
                    else {
                        world1.setBlockState(blockPos2, ((AbstractPropertyContainer<O, BlockState>)blockState3).<Comparable, Integer>with((Property<Comparable>)CarrotsBlock.AGE, integer5 - 1), 2);
                        world1.playLevelEvent(2001, blockPos2, Block.getRawIdFromState(blockState3));
                    }
                    this.rabbit.moreCarrotTicks = 40;
                }
                this.i = false;
                this.cooldown = 10;
            }
        }
        
        @Override
        protected boolean isTargetPos(final ViewableWorld world, BlockPos pos) {
            Block block3 = world.getBlockState(pos).getBlock();
            if (block3 == Blocks.bV && this.h && !this.i) {
                pos = pos.up();
                final BlockState blockState4 = world.getBlockState(pos);
                block3 = blockState4.getBlock();
                if (block3 instanceof CarrotsBlock && ((CarrotsBlock)block3).isValidState(blockState4)) {
                    return this.i = true;
                }
            }
            return false;
        }
    }
    
    static class RabbitEscapeDangerGoal extends EscapeDangerGoal
    {
        private final RabbitEntity owner;
        
        public RabbitEscapeDangerGoal(final RabbitEntity rabbitEntity, final double double2) {
            super(rabbitEntity, double2);
            this.owner = rabbitEntity;
        }
        
        @Override
        public void tick() {
            super.tick();
            this.owner.setSpeed(this.speed);
        }
    }
    
    static class RabbitAttackGoal extends MeleeAttackGoal
    {
        public RabbitAttackGoal(final RabbitEntity rabbitEntity) {
            super(rabbitEntity, 1.4, true);
        }
        
        @Override
        protected double getSquaredMaxAttackDistance(final LivingEntity entity) {
            return 4.0f + entity.getWidth();
        }
    }
}
