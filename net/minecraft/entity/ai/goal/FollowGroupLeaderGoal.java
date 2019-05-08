package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.passive.SchoolingFishEntity;

public class FollowGroupLeaderGoal extends Goal
{
    private final SchoolingFishEntity owner;
    private int moveDelay;
    private int checkSurroundingDelay;
    
    public FollowGroupLeaderGoal(final SchoolingFishEntity schoolingFishEntity) {
        this.owner = schoolingFishEntity;
        this.checkSurroundingDelay = this.getSurroundingSearchDelay(schoolingFishEntity);
    }
    
    protected int getSurroundingSearchDelay(final SchoolingFishEntity schoolingFishEntity) {
        return 200 + schoolingFishEntity.getRand().nextInt(200) % 20;
    }
    
    @Override
    public boolean canStart() {
        if (this.owner.hasOtherFishInGroup()) {
            return false;
        }
        if (this.owner.hasLeader()) {
            return true;
        }
        if (this.checkSurroundingDelay > 0) {
            --this.checkSurroundingDelay;
            return false;
        }
        this.checkSurroundingDelay = this.getSurroundingSearchDelay(this.owner);
        final Predicate<SchoolingFishEntity> predicate1 = schoolingFishEntity -> schoolingFishEntity.canHaveMoreFishInGroup() || !schoolingFishEntity.hasLeader();
        final List<SchoolingFishEntity> list2 = this.owner.world.<SchoolingFishEntity>getEntities(this.owner.getClass(), this.owner.getBoundingBox().expand(8.0, 8.0, 8.0), predicate1);
        final SchoolingFishEntity schoolingFishEntity2 = list2.stream().filter(SchoolingFishEntity::canHaveMoreFishInGroup).findAny().orElse(this.owner);
        schoolingFishEntity2.pullInOtherFish(list2.stream().filter(schoolingFishEntity -> !schoolingFishEntity.hasLeader()));
        return this.owner.hasLeader();
    }
    
    @Override
    public boolean shouldContinue() {
        return this.owner.hasLeader() && this.owner.isCloseEnoughToLeader();
    }
    
    @Override
    public void start() {
        this.moveDelay = 0;
    }
    
    @Override
    public void stop() {
        this.owner.leaveGroup();
    }
    
    @Override
    public void tick() {
        final int moveDelay = this.moveDelay - 1;
        this.moveDelay = moveDelay;
        if (moveDelay > 0) {
            return;
        }
        this.moveDelay = 10;
        this.owner.moveTowardLeader();
    }
}
