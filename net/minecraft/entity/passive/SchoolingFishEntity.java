package net.minecraft.entity.passive;

import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import java.util.List;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.FollowGroupLeaderGoal;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;

public abstract class SchoolingFishEntity extends FishEntity
{
    private SchoolingFishEntity leader;
    private int groupSize;
    
    public SchoolingFishEntity(final EntityType<? extends SchoolingFishEntity> type, final World world) {
        super(type, world);
        this.groupSize = 1;
    }
    
    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(5, new FollowGroupLeaderGoal(this));
    }
    
    @Override
    public int getLimitPerChunk() {
        return this.getMaxGroupSize();
    }
    
    public int getMaxGroupSize() {
        return super.getLimitPerChunk();
    }
    
    @Override
    protected boolean hasSelfControl() {
        return !this.hasLeader();
    }
    
    public boolean hasLeader() {
        return this.leader != null && this.leader.isAlive();
    }
    
    public SchoolingFishEntity joinGroupOf(final SchoolingFishEntity schoolingFishEntity) {
        (this.leader = schoolingFishEntity).increaseGroupSize();
        return schoolingFishEntity;
    }
    
    public void leaveGroup() {
        this.leader.decreaseGroupSize();
        this.leader = null;
    }
    
    private void increaseGroupSize() {
        ++this.groupSize;
    }
    
    private void decreaseGroupSize() {
        --this.groupSize;
    }
    
    public boolean canHaveMoreFishInGroup() {
        return this.hasOtherFishInGroup() && this.groupSize < this.getMaxGroupSize();
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.hasOtherFishInGroup() && this.world.random.nextInt(200) == 1) {
            final List<FishEntity> list1 = this.world.<FishEntity>getEntities(this.getClass(), this.getBoundingBox().expand(8.0, 8.0, 8.0));
            if (list1.size() <= 1) {
                this.groupSize = 1;
            }
        }
    }
    
    public boolean hasOtherFishInGroup() {
        return this.groupSize > 1;
    }
    
    public boolean isCloseEnoughToLeader() {
        return this.squaredDistanceTo(this.leader) <= 121.0;
    }
    
    public void moveTowardLeader() {
        if (this.hasLeader()) {
            this.getNavigation().startMovingTo(this.leader, 1.0);
        }
    }
    
    public void pullInOtherFish(final Stream<SchoolingFishEntity> stream) {
        stream.limit(this.getMaxGroupSize() - this.groupSize).filter(schoolingFishEntity -> schoolingFishEntity != this).forEach(schoolingFishEntity -> schoolingFishEntity.joinGroupOf(this));
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        if (entityData == null) {
            entityData = new Data(this);
        }
        else {
            this.joinGroupOf(((Data)entityData).leader);
        }
        return entityData;
    }
    
    public static class Data implements EntityData
    {
        public final SchoolingFishEntity leader;
        
        public Data(final SchoolingFishEntity schoolingFishEntity) {
            this.leader = schoolingFishEntity;
        }
    }
}
