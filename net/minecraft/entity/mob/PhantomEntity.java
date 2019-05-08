package net.minecraft.entity.mob;

import net.minecraft.world.Heightmap;
import net.minecraft.entity.ai.TargetPredicate;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3i;
import java.util.EnumSet;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import java.util.Random;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.data.TrackedData;

public class PhantomEntity extends FlyingEntity implements Monster
{
    private static final TrackedData<Integer> SIZE;
    private Vec3d c;
    private BlockPos d;
    private PhantomMovementType movementType;
    
    public PhantomEntity(final EntityType<? extends PhantomEntity> type, final World world) {
        super(type, world);
        this.c = Vec3d.ZERO;
        this.d = BlockPos.ORIGIN;
        this.movementType = PhantomMovementType.a;
        this.experiencePoints = 5;
        this.moveControl = new PhantomMoveControl(this);
        this.lookControl = new PhantomLookControl(this);
    }
    
    @Override
    protected BodyControl createBodyControl() {
        return new PhantomBodyControl(this);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new c());
        this.goalSelector.add(2, new i());
        this.goalSelector.add(3, new e());
        this.targetSelector.add(1, new b());
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Integer>startTracking(PhantomEntity.SIZE, 0);
    }
    
    public void setPhantomSize(final int integer) {
        this.dataTracker.<Integer>set(PhantomEntity.SIZE, MathHelper.clamp(integer, 0, 64));
    }
    
    private void onSizeChanged() {
        this.refreshSize();
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6 + this.getPhantomSize());
    }
    
    public int getPhantomSize() {
        return this.dataTracker.<Integer>get(PhantomEntity.SIZE);
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return entitySize.height * 0.35f;
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        if (PhantomEntity.SIZE.equals(data)) {
            this.onSizeChanged();
        }
        super.onTrackedDataSet(data);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient) {
            final float float1 = MathHelper.cos((this.getEntityId() * 3 + this.age) * 0.13f + 3.1415927f);
            final float float2 = MathHelper.cos((this.getEntityId() * 3 + this.age + 1) * 0.13f + 3.1415927f);
            if (float1 > 0.0f && float2 <= 0.0f) {
                this.world.playSound(this.x, this.y, this.z, SoundEvents.ik, this.getSoundCategory(), 0.95f + this.random.nextFloat() * 0.05f, 0.95f + this.random.nextFloat() * 0.05f, false);
            }
            final int integer3 = this.getPhantomSize();
            final float float3 = MathHelper.cos(this.yaw * 0.017453292f) * (1.3f + 0.21f * integer3);
            final float float4 = MathHelper.sin(this.yaw * 0.017453292f) * (1.3f + 0.21f * integer3);
            final float float5 = (0.3f + float1 * 0.45f) * (integer3 * 0.2f + 1.0f);
            this.world.addParticle(ParticleTypes.L, this.x + float3, this.y + float5, this.z + float4, 0.0, 0.0, 0.0);
            this.world.addParticle(ParticleTypes.L, this.x - float3, this.y + float5, this.z - float4, 0.0, 0.0, 0.0);
        }
        if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }
    
    @Override
    public void updateState() {
        if (this.isAlive() && this.isInDaylight()) {
            this.setOnFireFor(8);
        }
        super.updateState();
    }
    
    @Override
    protected void mobTick() {
        super.mobTick();
    }
    
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        this.d = new BlockPos(this).up(5);
        this.setPhantomSize(0);
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("AX")) {
            this.d = new BlockPos(tag.getInt("AX"), tag.getInt("AY"), tag.getInt("AZ"));
        }
        this.setPhantomSize(tag.getInt("Size"));
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("AX", this.d.getX());
        tag.putInt("AY", this.d.getY());
        tag.putInt("AZ", this.d.getZ());
        tag.putInt("Size", this.getPhantomSize());
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderAtDistance(final double distance) {
        return true;
    }
    
    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.f;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ih;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.il;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ij;
    }
    
    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }
    
    @Override
    protected float getSoundVolume() {
        return 1.0f;
    }
    
    @Override
    public boolean canTarget(final EntityType<?> entityType) {
        return true;
    }
    
    @Override
    public EntitySize getSize(final EntityPose entityPose) {
        final int integer2 = this.getPhantomSize();
        final EntitySize entitySize3 = super.getSize(entityPose);
        final float float4 = (entitySize3.width + 0.2f * integer2) / entitySize3.width;
        return entitySize3.scaled(float4);
    }
    
    static {
        SIZE = DataTracker.<Integer>registerData(PhantomEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
    
    enum PhantomMovementType
    {
        a, 
        b;
    }
    
    class PhantomMoveControl extends MoveControl
    {
        private float j;
        
        public PhantomMoveControl(final MobEntity mobEntity) {
            super(mobEntity);
            this.j = 0.1f;
        }
        
        @Override
        public void tick() {
            if (PhantomEntity.this.horizontalCollision) {
                final PhantomEntity i = PhantomEntity.this;
                i.yaw += 180.0f;
                this.j = 0.1f;
            }
            float float1 = (float)(PhantomEntity.this.c.x - PhantomEntity.this.x);
            final float float2 = (float)(PhantomEntity.this.c.y - PhantomEntity.this.y);
            float float3 = (float)(PhantomEntity.this.c.z - PhantomEntity.this.z);
            double double4 = MathHelper.sqrt(float1 * float1 + float3 * float3);
            final double double5 = 1.0 - MathHelper.abs(float2 * 0.7f) / double4;
            float1 *= (float)double5;
            float3 *= (float)double5;
            double4 = MathHelper.sqrt(float1 * float1 + float3 * float3);
            final double double6 = MathHelper.sqrt(float1 * float1 + float3 * float3 + float2 * float2);
            final float float4 = PhantomEntity.this.yaw;
            final float float5 = (float)MathHelper.atan2(float3, float1);
            final float float6 = MathHelper.wrapDegrees(PhantomEntity.this.yaw + 90.0f);
            final float float7 = MathHelper.wrapDegrees(float5 * 57.295776f);
            PhantomEntity.this.yaw = MathHelper.d(float6, float7, 4.0f) - 90.0f;
            PhantomEntity.this.aK = PhantomEntity.this.yaw;
            if (MathHelper.angleBetween(float4, PhantomEntity.this.yaw) < 3.0f) {
                this.j = MathHelper.c(this.j, 1.8f, 0.005f * (1.8f / this.j));
            }
            else {
                this.j = MathHelper.c(this.j, 0.2f, 0.025f);
            }
            final float float8 = (float)(-(MathHelper.atan2(-float2, double4) * 57.2957763671875));
            PhantomEntity.this.pitch = float8;
            final float float9 = PhantomEntity.this.yaw + 90.0f;
            final double double7 = this.j * MathHelper.cos(float9 * 0.017453292f) * Math.abs(float1 / double6);
            final double double8 = this.j * MathHelper.sin(float9 * 0.017453292f) * Math.abs(float3 / double6);
            final double double9 = this.j * MathHelper.sin(float8 * 0.017453292f) * Math.abs(float2 / double6);
            final Vec3d vec3d22 = PhantomEntity.this.getVelocity();
            PhantomEntity.this.setVelocity(vec3d22.add(new Vec3d(double7, double9, double8).subtract(vec3d22).multiply(0.2)));
        }
    }
    
    class PhantomBodyControl extends BodyControl
    {
        public PhantomBodyControl(final MobEntity mobEntity) {
            super(mobEntity);
        }
        
        @Override
        public void a() {
            PhantomEntity.this.headYaw = PhantomEntity.this.aK;
            PhantomEntity.this.aK = PhantomEntity.this.yaw;
        }
    }
    
    class PhantomLookControl extends LookControl
    {
        public PhantomLookControl(final MobEntity mobEntity) {
            super(mobEntity);
        }
        
        @Override
        public void tick() {
        }
    }
    
    abstract class h extends Goal
    {
        public h() {
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        protected boolean g() {
            return PhantomEntity.this.c.squaredDistanceTo(PhantomEntity.this.x, PhantomEntity.this.y, PhantomEntity.this.z) < 4.0;
        }
    }
    
    class e extends h
    {
        private float c;
        private float d;
        private float e;
        private float f;
        
        private e() {
        }
        
        @Override
        public boolean canStart() {
            return PhantomEntity.this.getTarget() == null || PhantomEntity.this.movementType == PhantomMovementType.a;
        }
        
        @Override
        public void start() {
            this.d = 5.0f + PhantomEntity.this.random.nextFloat() * 10.0f;
            this.e = -4.0f + PhantomEntity.this.random.nextFloat() * 9.0f;
            this.f = (PhantomEntity.this.random.nextBoolean() ? 1.0f : -1.0f);
            this.h();
        }
        
        @Override
        public void tick() {
            if (PhantomEntity.this.random.nextInt(350) == 0) {
                this.e = -4.0f + PhantomEntity.this.random.nextFloat() * 9.0f;
            }
            if (PhantomEntity.this.random.nextInt(250) == 0) {
                ++this.d;
                if (this.d > 15.0f) {
                    this.d = 5.0f;
                    this.f = -this.f;
                }
            }
            if (PhantomEntity.this.random.nextInt(450) == 0) {
                this.c = PhantomEntity.this.random.nextFloat() * 2.0f * 3.1415927f;
                this.h();
            }
            if (this.g()) {
                this.h();
            }
            if (PhantomEntity.this.c.y < PhantomEntity.this.y && !PhantomEntity.this.world.isAir(new BlockPos(PhantomEntity.this).down(1))) {
                this.e = Math.max(1.0f, this.e);
                this.h();
            }
            if (PhantomEntity.this.c.y > PhantomEntity.this.y && !PhantomEntity.this.world.isAir(new BlockPos(PhantomEntity.this).up(1))) {
                this.e = Math.min(-1.0f, this.e);
                this.h();
            }
        }
        
        private void h() {
            if (BlockPos.ORIGIN.equals(PhantomEntity.this.d)) {
                PhantomEntity.this.d = new BlockPos(PhantomEntity.this);
            }
            this.c += this.f * 15.0f * 0.017453292f;
            PhantomEntity.this.c = new Vec3d(PhantomEntity.this.d).add(this.d * MathHelper.cos(this.c), -4.0f + this.e, this.d * MathHelper.sin(this.c));
        }
    }
    
    class i extends h
    {
        private i() {
        }
        
        @Override
        public boolean canStart() {
            return PhantomEntity.this.getTarget() != null && PhantomEntity.this.movementType == PhantomMovementType.b;
        }
        
        @Override
        public boolean shouldContinue() {
            final LivingEntity livingEntity1 = PhantomEntity.this.getTarget();
            if (livingEntity1 == null) {
                return false;
            }
            if (!livingEntity1.isAlive()) {
                return false;
            }
            if (livingEntity1 instanceof PlayerEntity && (((PlayerEntity)livingEntity1).isSpectator() || ((PlayerEntity)livingEntity1).isCreative())) {
                return false;
            }
            if (!this.canStart()) {
                return false;
            }
            if (PhantomEntity.this.age % 20 == 0) {
                final List<CatEntity> list2 = PhantomEntity.this.world.<CatEntity>getEntities(CatEntity.class, PhantomEntity.this.getBoundingBox().expand(16.0), EntityPredicates.VALID_ENTITY);
                if (!list2.isEmpty()) {
                    for (final CatEntity catEntity4 : list2) {
                        catEntity4.hiss();
                    }
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public void start() {
        }
        
        @Override
        public void stop() {
            PhantomEntity.this.setTarget(null);
            PhantomEntity.this.movementType = PhantomMovementType.a;
        }
        
        @Override
        public void tick() {
            final LivingEntity livingEntity1 = PhantomEntity.this.getTarget();
            PhantomEntity.this.c = new Vec3d(livingEntity1.x, livingEntity1.y + livingEntity1.getHeight() * 0.5, livingEntity1.z);
            if (PhantomEntity.this.getBoundingBox().expand(0.20000000298023224).intersects(livingEntity1.getBoundingBox())) {
                PhantomEntity.this.tryAttack(livingEntity1);
                PhantomEntity.this.movementType = PhantomMovementType.a;
                PhantomEntity.this.world.playLevelEvent(1039, new BlockPos(PhantomEntity.this), 0);
            }
            else if (PhantomEntity.this.horizontalCollision || PhantomEntity.this.hurtTime > 0) {
                PhantomEntity.this.movementType = PhantomMovementType.a;
            }
        }
    }
    
    class c extends Goal
    {
        private int b;
        
        private c() {
        }
        
        @Override
        public boolean canStart() {
            final LivingEntity livingEntity1 = PhantomEntity.this.getTarget();
            return livingEntity1 != null && PhantomEntity.this.isTarget(PhantomEntity.this.getTarget(), TargetPredicate.DEFAULT);
        }
        
        @Override
        public void start() {
            this.b = 10;
            PhantomEntity.this.movementType = PhantomMovementType.a;
            this.g();
        }
        
        @Override
        public void stop() {
            PhantomEntity.this.d = PhantomEntity.this.world.getTopPosition(Heightmap.Type.e, PhantomEntity.this.d).up(10 + PhantomEntity.this.random.nextInt(20));
        }
        
        @Override
        public void tick() {
            if (PhantomEntity.this.movementType == PhantomMovementType.a) {
                --this.b;
                if (this.b <= 0) {
                    PhantomEntity.this.movementType = PhantomMovementType.b;
                    this.g();
                    this.b = (8 + PhantomEntity.this.random.nextInt(4)) * 20;
                    PhantomEntity.this.playSound(SoundEvents.im, 10.0f, 0.95f + PhantomEntity.this.random.nextFloat() * 0.1f);
                }
            }
        }
        
        private void g() {
            PhantomEntity.this.d = new BlockPos(PhantomEntity.this.getTarget()).up(20 + PhantomEntity.this.random.nextInt(20));
            if (PhantomEntity.this.d.getY() < PhantomEntity.this.world.getSeaLevel()) {
                PhantomEntity.this.d = new BlockPos(PhantomEntity.this.d.getX(), PhantomEntity.this.world.getSeaLevel() + 1, PhantomEntity.this.d.getZ());
            }
        }
    }
    
    class b extends Goal
    {
        private final TargetPredicate PLAYERS_IN_RANGE_PREDICATE;
        private int c;
        
        private b() {
            this.PLAYERS_IN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
            this.c = 20;
        }
        
        @Override
        public boolean canStart() {
            if (this.c > 0) {
                --this.c;
                return false;
            }
            this.c = 60;
            final List<PlayerEntity> list1 = PhantomEntity.this.world.getPlayersInBox(this.PLAYERS_IN_RANGE_PREDICATE, PhantomEntity.this, PhantomEntity.this.getBoundingBox().expand(16.0, 64.0, 16.0));
            if (!list1.isEmpty()) {
                list1.sort((playerEntity1, playerEntity2) -> (playerEntity1.y > playerEntity2.y) ? -1 : 1);
                for (final PlayerEntity playerEntity3 : list1) {
                    if (PhantomEntity.this.isTarget(playerEntity3, TargetPredicate.DEFAULT)) {
                        PhantomEntity.this.setTarget(playerEntity3);
                        return true;
                    }
                }
            }
            return false;
        }
        
        @Override
        public boolean shouldContinue() {
            final LivingEntity livingEntity1 = PhantomEntity.this.getTarget();
            return livingEntity1 != null && PhantomEntity.this.isTarget(livingEntity1, TargetPredicate.DEFAULT);
        }
    }
}
