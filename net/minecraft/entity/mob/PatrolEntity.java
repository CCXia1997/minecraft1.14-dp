package net.minecraft.entity.mob;

import java.util.Random;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Position;
import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.EquipmentSlot;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;

public abstract class PatrolEntity extends HostileEntity
{
    private BlockPos patrolTarget;
    private boolean patrolLeader;
    private boolean patrolling;
    
    protected PatrolEntity(final EntityType<? extends PatrolEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(4, new PatrolGoal<>(this, 0.7, 0.595));
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.patrolTarget != null) {
            tag.put("PatrolTarget", TagHelper.serializeBlockPos(this.patrolTarget));
        }
        tag.putBoolean("PatrolLeader", this.patrolLeader);
        tag.putBoolean("Patrolling", this.patrolling);
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.containsKey("PatrolTarget")) {
            this.patrolTarget = TagHelper.deserializeBlockPos(tag.getCompound("PatrolTarget"));
        }
        this.patrolLeader = tag.getBoolean("PatrolLeader");
        this.patrolling = tag.getBoolean("Patrolling");
    }
    
    @Override
    public double getHeightOffset() {
        return -0.45;
    }
    
    public boolean canLead() {
        return true;
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable final EntityData entityData, @Nullable final CompoundTag compoundTag) {
        if (difficulty != SpawnType.p && difficulty != SpawnType.h && difficulty != SpawnType.d && this.random.nextFloat() < 0.06f && this.canLead()) {
            this.patrolLeader = true;
        }
        if (this.isPatrolLeader()) {
            this.setEquippedStack(EquipmentSlot.HEAD, Raid.OMINOUS_BANNER);
            this.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0f);
        }
        if (difficulty == SpawnType.p) {
            this.patrolling = true;
        }
        return super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
    }
    
    @Override
    public boolean canImmediatelyDespawn(final double distanceSquared) {
        return !this.patrolling || distanceSquared > 16384.0;
    }
    
    public void setPatrolTarget(final BlockPos blockPos) {
        this.patrolTarget = blockPos;
        this.patrolling = true;
    }
    
    public BlockPos getPatrolTarget() {
        return this.patrolTarget;
    }
    
    public boolean hasPatrolTarget() {
        return this.patrolTarget != null;
    }
    
    public void setPatrolLeader(final boolean boolean1) {
        this.patrolLeader = boolean1;
        this.patrolling = true;
    }
    
    public boolean isPatrolLeader() {
        return this.patrolLeader;
    }
    
    public boolean hasNoRaid() {
        return true;
    }
    
    public void setRandomPatrolTarget() {
        this.patrolTarget = new BlockPos(this).add(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
        this.patrolling = true;
    }
    
    protected boolean isRaidCenterSet() {
        return this.patrolling;
    }
    
    public static class PatrolGoal<T extends PatrolEntity> extends Goal
    {
        private final T owner;
        private final double leaderSpeed;
        private final double fellowSpeed;
        
        public PatrolGoal(final T patrolEntity, final double double2, final double double4) {
            this.owner = patrolEntity;
            this.leaderSpeed = double2;
            this.fellowSpeed = double4;
            this.setControls(EnumSet.<Control>of(Control.a));
        }
        
        @Override
        public boolean canStart() {
            return this.owner.isRaidCenterSet() && this.owner.getTarget() == null && !this.owner.hasPassengers() && this.owner.hasPatrolTarget();
        }
        
        @Override
        public void start() {
        }
        
        @Override
        public void stop() {
        }
        
        @Override
        public void tick() {
            final boolean boolean1 = this.owner.isPatrolLeader();
            final EntityNavigation entityNavigation2 = this.owner.getNavigation();
            if (entityNavigation2.isIdle()) {
                if (!boolean1 || !this.owner.getPatrolTarget().isWithinDistance(this.owner.getPos(), 10.0)) {
                    Vec3d vec3d3 = new Vec3d(this.owner.getPatrolTarget());
                    final Vec3d vec3d4 = new Vec3d(this.owner.x, this.owner.y, this.owner.z);
                    final Vec3d vec3d5 = vec3d4.subtract(vec3d3);
                    vec3d3 = vec3d5.rotateY(90.0f).multiply(0.4).add(vec3d3);
                    final Vec3d vec3d6 = vec3d3.subtract(vec3d4).normalize().multiply(10.0).add(vec3d4);
                    BlockPos blockPos7 = new BlockPos((int)vec3d6.x, (int)vec3d6.y, (int)vec3d6.z);
                    blockPos7 = this.owner.world.getTopPosition(Heightmap.Type.f, blockPos7);
                    if (!entityNavigation2.startMovingTo(blockPos7.getX(), blockPos7.getY(), blockPos7.getZ(), boolean1 ? this.fellowSpeed : this.leaderSpeed)) {
                        this.wander();
                    }
                    else if (boolean1) {
                        final List<PatrolEntity> list8 = this.owner.world.<PatrolEntity>getEntities(PatrolEntity.class, this.owner.getBoundingBox().expand(16.0), patrolEntity -> !patrolEntity.isPatrolLeader() && patrolEntity.hasNoRaid());
                        for (final PatrolEntity patrolEntity2 : list8) {
                            patrolEntity2.setPatrolTarget(blockPos7);
                        }
                    }
                }
                else {
                    this.owner.setRandomPatrolTarget();
                }
            }
        }
        
        private void wander() {
            final Random random1 = this.owner.getRand();
            final BlockPos blockPos2 = this.owner.world.getTopPosition(Heightmap.Type.f, new BlockPos(this.owner).add(-8 + random1.nextInt(16), 0, -8 + random1.nextInt(16)));
            this.owner.getNavigation().startMovingTo(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), this.leaderSpeed);
        }
    }
}
