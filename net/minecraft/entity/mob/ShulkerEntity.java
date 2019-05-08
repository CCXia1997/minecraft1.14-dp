package net.minecraft.entity.mob;

import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.world.Difficulty;
import java.util.EnumSet;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import java.util.Random;
import net.minecraft.util.DyeColor;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.state.property.Property;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import java.util.Optional;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import java.util.UUID;
import net.minecraft.entity.passive.GolemEntity;

public class ShulkerEntity extends GolemEntity implements Monster
{
    private static final UUID ATTR_COVERED_ARMOR_BONUS_UUID;
    private static final EntityAttributeModifier ATTR_COVERED_ARMOR_BONUS;
    protected static final TrackedData<Direction> ATTACHED_FACE;
    protected static final TrackedData<Optional<BlockPos>> ATTACHED_BLOCK;
    protected static final TrackedData<Byte> PEEK_AMOUNT;
    protected static final TrackedData<Byte> COLOR;
    private float bC;
    private float bD;
    private BlockPos bE;
    private int bF;
    
    public ShulkerEntity(final EntityType<? extends ShulkerEntity> type, final World world) {
        super(type, world);
        this.aL = 180.0f;
        this.aK = 180.0f;
        this.bE = null;
        this.experiencePoints = 5;
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        this.aK = 180.0f;
        this.aL = 180.0f;
        this.yaw = 180.0f;
        this.prevYaw = 180.0f;
        this.headYaw = 180.0f;
        this.prevHeadYaw = 180.0f;
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(4, new ShootBulletGoal());
        this.goalSelector.add(7, new PeekGoal());
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new SearchForPlayerGoal(this));
        this.targetSelector.add(3, new SearchForTargetGoal(this));
    }
    
    @Override
    protected boolean canClimb() {
        return false;
    }
    
    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.f;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.jX;
    }
    
    @Override
    public void playAmbientSound() {
        if (!this.ed()) {
            super.playAmbientSound();
        }
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.kd;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        if (this.ed()) {
            return SoundEvents.kf;
        }
        return SoundEvents.ke;
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Direction>startTracking(ShulkerEntity.ATTACHED_FACE, Direction.DOWN);
        this.dataTracker.<Optional<BlockPos>>startTracking(ShulkerEntity.ATTACHED_BLOCK, Optional.<BlockPos>empty());
        this.dataTracker.<Byte>startTracking(ShulkerEntity.PEEK_AMOUNT, (Byte)0);
        this.dataTracker.<Byte>startTracking(ShulkerEntity.COLOR, (Byte)16);
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
    }
    
    @Override
    protected BodyControl createBodyControl() {
        return new ShulkerBodyControl(this);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.dataTracker.<Direction>set(ShulkerEntity.ATTACHED_FACE, Direction.byId(tag.getByte("AttachFace")));
        this.dataTracker.<Byte>set(ShulkerEntity.PEEK_AMOUNT, tag.getByte("Peek"));
        this.dataTracker.<Byte>set(ShulkerEntity.COLOR, tag.getByte("Color"));
        if (tag.containsKey("APX")) {
            final int integer2 = tag.getInt("APX");
            final int integer3 = tag.getInt("APY");
            final int integer4 = tag.getInt("APZ");
            this.dataTracker.<Optional<BlockPos>>set(ShulkerEntity.ATTACHED_BLOCK, Optional.<BlockPos>of(new BlockPos(integer2, integer3, integer4)));
        }
        else {
            this.dataTracker.<Optional<BlockPos>>set(ShulkerEntity.ATTACHED_BLOCK, Optional.<BlockPos>empty());
        }
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putByte("AttachFace", (byte)this.dataTracker.<Direction>get(ShulkerEntity.ATTACHED_FACE).getId());
        tag.putByte("Peek", this.dataTracker.<Byte>get(ShulkerEntity.PEEK_AMOUNT));
        tag.putByte("Color", this.dataTracker.<Byte>get(ShulkerEntity.COLOR));
        final BlockPos blockPos2 = this.getAttachedBlock();
        if (blockPos2 != null) {
            tag.putInt("APX", blockPos2.getX());
            tag.putInt("APY", blockPos2.getY());
            tag.putInt("APZ", blockPos2.getZ());
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        BlockPos blockPos1 = this.dataTracker.<Optional<BlockPos>>get(ShulkerEntity.ATTACHED_BLOCK).orElse(null);
        if (blockPos1 == null && !this.world.isClient) {
            blockPos1 = new BlockPos(this);
            this.dataTracker.<Optional<BlockPos>>set(ShulkerEntity.ATTACHED_BLOCK, Optional.<BlockPos>of(blockPos1));
        }
        if (this.hasVehicle()) {
            blockPos1 = null;
            final float float2 = this.getVehicle().yaw;
            this.yaw = float2;
            this.aK = float2;
            this.aL = float2;
            this.bF = 0;
        }
        else if (!this.world.isClient) {
            final BlockState blockState2 = this.world.getBlockState(blockPos1);
            if (!blockState2.isAir()) {
                if (blockState2.getBlock() == Blocks.bn) {
                    final Direction direction3 = blockState2.<Direction>get((Property<Direction>)PistonBlock.FACING);
                    if (this.world.isAir(blockPos1.offset(direction3))) {
                        blockPos1 = blockPos1.offset(direction3);
                        this.dataTracker.<Optional<BlockPos>>set(ShulkerEntity.ATTACHED_BLOCK, Optional.<BlockPos>of(blockPos1));
                    }
                    else {
                        this.l();
                    }
                }
                else if (blockState2.getBlock() == Blocks.aW) {
                    final Direction direction3 = blockState2.<Direction>get((Property<Direction>)PistonHeadBlock.FACING);
                    if (this.world.isAir(blockPos1.offset(direction3))) {
                        blockPos1 = blockPos1.offset(direction3);
                        this.dataTracker.<Optional<BlockPos>>set(ShulkerEntity.ATTACHED_BLOCK, Optional.<BlockPos>of(blockPos1));
                    }
                    else {
                        this.l();
                    }
                }
                else {
                    this.l();
                }
            }
            BlockPos blockPos2 = blockPos1.offset(this.getAttachedFace());
            if (!this.world.doesBlockHaveSolidTopSurface(blockPos2, this)) {
                boolean boolean4 = false;
                for (final Direction direction4 : Direction.values()) {
                    blockPos2 = blockPos1.offset(direction4);
                    if (this.world.doesBlockHaveSolidTopSurface(blockPos2, this)) {
                        this.dataTracker.<Direction>set(ShulkerEntity.ATTACHED_FACE, direction4);
                        boolean4 = true;
                        break;
                    }
                }
                if (!boolean4) {
                    this.l();
                }
            }
            final BlockPos blockPos3 = blockPos1.offset(this.getAttachedFace().getOpposite());
            if (this.world.doesBlockHaveSolidTopSurface(blockPos3, this)) {
                this.l();
            }
        }
        final float float2 = this.getPeekAmount() * 0.01f;
        this.bC = this.bD;
        if (this.bD > float2) {
            this.bD = MathHelper.clamp(this.bD - 0.05f, float2, 1.0f);
        }
        else if (this.bD < float2) {
            this.bD = MathHelper.clamp(this.bD + 0.05f, 0.0f, float2);
        }
        if (blockPos1 != null) {
            if (this.world.isClient) {
                if (this.bF > 0 && this.bE != null) {
                    --this.bF;
                }
                else {
                    this.bE = blockPos1;
                }
            }
            this.x = blockPos1.getX() + 0.5;
            this.y = blockPos1.getY();
            this.z = blockPos1.getZ() + 0.5;
            this.prevX = this.x;
            this.prevY = this.y;
            this.prevZ = this.z;
            this.prevRenderX = this.x;
            this.prevRenderY = this.y;
            this.prevRenderZ = this.z;
            final double double3 = 0.5 - MathHelper.sin((0.5f + this.bD) * 3.1415927f) * 0.5;
            final double double4 = 0.5 - MathHelper.sin((0.5f + this.bC) * 3.1415927f) * 0.5;
            final Direction direction5 = this.getAttachedFace().getOpposite();
            this.setBoundingBox(new BoundingBox(this.x - 0.5, this.y, this.z - 0.5, this.x + 0.5, this.y + 1.0, this.z + 0.5).stretch(direction5.getOffsetX() * double3, direction5.getOffsetY() * double3, direction5.getOffsetZ() * double3));
            final double double5 = double3 - double4;
            if (double5 > 0.0) {
                final List<Entity> list10 = this.world.getEntities(this, this.getBoundingBox());
                if (!list10.isEmpty()) {
                    for (final Entity entity12 : list10) {
                        if (!(entity12 instanceof ShulkerEntity) && !entity12.noClip) {
                            entity12.move(MovementType.e, new Vec3d(double5 * direction5.getOffsetX(), double5 * direction5.getOffsetY(), double5 * direction5.getOffsetZ()));
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void move(final MovementType type, final Vec3d offset) {
        if (type == MovementType.d) {
            this.l();
        }
        else {
            super.move(type, offset);
        }
    }
    
    @Override
    public void setPosition(final double x, final double y, final double z) {
        super.setPosition(x, y, z);
        if (this.dataTracker == null || this.age == 0) {
            return;
        }
        final Optional<BlockPos> optional7 = this.dataTracker.<Optional<BlockPos>>get(ShulkerEntity.ATTACHED_BLOCK);
        final Optional<BlockPos> optional8 = Optional.<BlockPos>of(new BlockPos(x, y, z));
        if (!optional8.equals(optional7)) {
            this.dataTracker.<Optional<BlockPos>>set(ShulkerEntity.ATTACHED_BLOCK, optional8);
            this.dataTracker.<Byte>set(ShulkerEntity.PEEK_AMOUNT, (Byte)0);
            this.velocityDirty = true;
        }
    }
    
    protected boolean l() {
        if (this.isAiDisabled() || !this.isAlive()) {
            return true;
        }
        final BlockPos blockPos1 = new BlockPos(this);
        for (int integer2 = 0; integer2 < 5; ++integer2) {
            final BlockPos blockPos2 = blockPos1.add(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
            if (blockPos2.getY() > 0 && this.world.isAir(blockPos2) && this.world.getWorldBorder().contains(blockPos2) && this.world.doesNotCollide(this, new BoundingBox(blockPos2))) {
                boolean boolean4 = false;
                for (final Direction direction8 : Direction.values()) {
                    if (this.world.doesBlockHaveSolidTopSurface(blockPos2.offset(direction8), this)) {
                        this.dataTracker.<Direction>set(ShulkerEntity.ATTACHED_FACE, direction8);
                        boolean4 = true;
                        break;
                    }
                }
                if (boolean4) {
                    this.playSound(SoundEvents.ki, 1.0f, 1.0f);
                    this.dataTracker.<Optional<BlockPos>>set(ShulkerEntity.ATTACHED_BLOCK, Optional.<BlockPos>of(blockPos2));
                    this.dataTracker.<Byte>set(ShulkerEntity.PEEK_AMOUNT, (Byte)0);
                    this.setTarget(null);
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void updateState() {
        super.updateState();
        this.setVelocity(Vec3d.ZERO);
        this.aL = 180.0f;
        this.aK = 180.0f;
        this.yaw = 180.0f;
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (ShulkerEntity.ATTACHED_BLOCK.equals(data) && this.world.isClient && !this.hasVehicle()) {
            final BlockPos blockPos2 = this.getAttachedBlock();
            if (blockPos2 != null) {
                if (this.bE == null) {
                    this.bE = blockPos2;
                }
                else {
                    this.bF = 6;
                }
                this.x = blockPos2.getX() + 0.5;
                this.y = blockPos2.getY();
                this.z = blockPos2.getZ() + 0.5;
                this.prevX = this.x;
                this.prevY = this.y;
                this.prevZ = this.z;
                this.prevRenderX = this.x;
                this.prevRenderY = this.y;
                this.prevRenderZ = this.z;
            }
        }
        super.onTrackedDataSet(data);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void setPositionAndRotations(final double x, final double y, final double z, final float float7, final float float8, final int integer9, final boolean boolean10) {
        this.bf = 0;
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        if (this.ed()) {
            final Entity entity3 = source.getSource();
            if (entity3 instanceof ProjectileEntity) {
                return false;
            }
        }
        if (super.damage(source, amount)) {
            if (this.getHealth() < this.getHealthMaximum() * 0.5 && this.random.nextInt(4) == 0) {
                this.l();
            }
            return true;
        }
        return false;
    }
    
    private boolean ed() {
        return this.getPeekAmount() == 0;
    }
    
    @Nullable
    @Override
    public BoundingBox ap() {
        return this.isAlive() ? this.getBoundingBox() : null;
    }
    
    public Direction getAttachedFace() {
        return this.dataTracker.<Direction>get(ShulkerEntity.ATTACHED_FACE);
    }
    
    @Nullable
    public BlockPos getAttachedBlock() {
        return this.dataTracker.<Optional<BlockPos>>get(ShulkerEntity.ATTACHED_BLOCK).orElse(null);
    }
    
    public void setAttachedBlock(@Nullable final BlockPos blockPos) {
        this.dataTracker.<Optional<BlockPos>>set(ShulkerEntity.ATTACHED_BLOCK, Optional.<BlockPos>ofNullable(blockPos));
    }
    
    public int getPeekAmount() {
        return this.dataTracker.<Byte>get(ShulkerEntity.PEEK_AMOUNT);
    }
    
    public void setPeekAmount(final int integer) {
        if (!this.world.isClient) {
            this.getAttributeInstance(EntityAttributes.ARMOR).removeModifier(ShulkerEntity.ATTR_COVERED_ARMOR_BONUS);
            if (integer == 0) {
                this.getAttributeInstance(EntityAttributes.ARMOR).addModifier(ShulkerEntity.ATTR_COVERED_ARMOR_BONUS);
                this.playSound(SoundEvents.kc, 1.0f, 1.0f);
            }
            else {
                this.playSound(SoundEvents.kg, 1.0f, 1.0f);
            }
        }
        this.dataTracker.<Byte>set(ShulkerEntity.PEEK_AMOUNT, (byte)integer);
    }
    
    @Environment(EnvType.CLIENT)
    public float v(final float float1) {
        return MathHelper.lerp(float1, this.bC, this.bD);
    }
    
    @Environment(EnvType.CLIENT)
    public int dY() {
        return this.bF;
    }
    
    @Environment(EnvType.CLIENT)
    public BlockPos dZ() {
        return this.bE;
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 0.5f;
    }
    
    @Override
    public int getLookPitchSpeed() {
        return 180;
    }
    
    @Override
    public int dA() {
        return 180;
    }
    
    @Override
    public void pushAwayFrom(final Entity entity) {
    }
    
    @Override
    public float getBoundingBoxMarginForTargeting() {
        return 0.0f;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean ea() {
        return this.bE != null && this.getAttachedBlock() != null;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public DyeColor getColor() {
        final Byte byte1 = this.dataTracker.<Byte>get(ShulkerEntity.COLOR);
        if (byte1 == 16 || byte1 > 15) {
            return null;
        }
        return DyeColor.byId(byte1);
    }
    
    static {
        ATTR_COVERED_ARMOR_BONUS_UUID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
        ATTR_COVERED_ARMOR_BONUS = new EntityAttributeModifier(ShulkerEntity.ATTR_COVERED_ARMOR_BONUS_UUID, "Covered armor bonus", 20.0, EntityAttributeModifier.Operation.a).setSerialize(false);
        ATTACHED_FACE = DataTracker.<Direction>registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.FACING);
        ATTACHED_BLOCK = DataTracker.<Optional<BlockPos>>registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.OPTIONA_BLOCK_POS);
        PEEK_AMOUNT = DataTracker.<Byte>registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
        COLOR = DataTracker.<Byte>registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
    
    class ShulkerBodyControl extends BodyControl
    {
        public ShulkerBodyControl(final MobEntity mobEntity) {
            super(mobEntity);
        }
        
        @Override
        public void a() {
        }
    }
    
    class PeekGoal extends Goal
    {
        private int counter;
        
        private PeekGoal() {
        }
        
        @Override
        public boolean canStart() {
            return ShulkerEntity.this.getTarget() == null && ShulkerEntity.this.random.nextInt(40) == 0;
        }
        
        @Override
        public boolean shouldContinue() {
            return ShulkerEntity.this.getTarget() == null && this.counter > 0;
        }
        
        @Override
        public void start() {
            this.counter = 20 * (1 + ShulkerEntity.this.random.nextInt(3));
            ShulkerEntity.this.setPeekAmount(30);
        }
        
        @Override
        public void stop() {
            if (ShulkerEntity.this.getTarget() == null) {
                ShulkerEntity.this.setPeekAmount(0);
            }
        }
        
        @Override
        public void tick() {
            --this.counter;
        }
    }
    
    class ShootBulletGoal extends Goal
    {
        private int counter;
        
        public ShootBulletGoal() {
            this.setControls(EnumSet.<Control>of(Control.a, Control.b));
        }
        
        @Override
        public boolean canStart() {
            final LivingEntity livingEntity1 = ShulkerEntity.this.getTarget();
            return livingEntity1 != null && livingEntity1.isAlive() && ShulkerEntity.this.world.getDifficulty() != Difficulty.PEACEFUL;
        }
        
        @Override
        public void start() {
            this.counter = 20;
            ShulkerEntity.this.setPeekAmount(100);
        }
        
        @Override
        public void stop() {
            ShulkerEntity.this.setPeekAmount(0);
        }
        
        @Override
        public void tick() {
            if (ShulkerEntity.this.world.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }
            --this.counter;
            final LivingEntity livingEntity1 = ShulkerEntity.this.getTarget();
            ShulkerEntity.this.getLookControl().lookAt(livingEntity1, 180.0f, 180.0f);
            final double double2 = ShulkerEntity.this.squaredDistanceTo(livingEntity1);
            if (double2 < 400.0) {
                if (this.counter <= 0) {
                    this.counter = 20 + ShulkerEntity.this.random.nextInt(10) * 20 / 2;
                    ShulkerEntity.this.world.spawnEntity(new ShulkerBulletEntity(ShulkerEntity.this.world, ShulkerEntity.this, livingEntity1, ShulkerEntity.this.getAttachedFace().getAxis()));
                    ShulkerEntity.this.playSound(SoundEvents.kh, 2.0f, (ShulkerEntity.this.random.nextFloat() - ShulkerEntity.this.random.nextFloat()) * 0.2f + 1.0f);
                }
            }
            else {
                ShulkerEntity.this.setTarget(null);
            }
            super.tick();
        }
    }
    
    class SearchForPlayerGoal extends FollowTargetGoal<PlayerEntity>
    {
        public SearchForPlayerGoal(final ShulkerEntity shulkerEntity2) {
            super(shulkerEntity2, PlayerEntity.class, true);
        }
        
        @Override
        public boolean canStart() {
            return ShulkerEntity.this.world.getDifficulty() != Difficulty.PEACEFUL && super.canStart();
        }
        
        @Override
        protected BoundingBox getSearchBox(final double distance) {
            final Direction direction3 = ((ShulkerEntity)this.entity).getAttachedFace();
            if (direction3.getAxis() == Direction.Axis.X) {
                return this.entity.getBoundingBox().expand(4.0, distance, distance);
            }
            if (direction3.getAxis() == Direction.Axis.Z) {
                return this.entity.getBoundingBox().expand(distance, distance, 4.0);
            }
            return this.entity.getBoundingBox().expand(distance, 4.0, distance);
        }
    }
    
    static class SearchForTargetGoal extends FollowTargetGoal<LivingEntity>
    {
        public SearchForTargetGoal(final ShulkerEntity shulkerEntity) {
            super(shulkerEntity, LivingEntity.class, 10, true, false, entity -> entity instanceof Monster);
        }
        
        @Override
        public boolean canStart() {
            return this.entity.getScoreboardTeam() != null && super.canStart();
        }
        
        @Override
        protected BoundingBox getSearchBox(final double distance) {
            final Direction direction3 = ((ShulkerEntity)this.entity).getAttachedFace();
            if (direction3.getAxis() == Direction.Axis.X) {
                return this.entity.getBoundingBox().expand(4.0, distance, distance);
            }
            if (direction3.getAxis() == Direction.Axis.Z) {
                return this.entity.getBoundingBox().expand(distance, distance, 4.0);
            }
            return this.entity.getBoundingBox().expand(distance, 4.0, distance);
        }
    }
}
