package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import java.util.EnumSet;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.ai.TargetPredicate;

public class IronGolemLookGoal extends Goal
{
    private static final TargetPredicate CLOSE_VILLAGER_PREDICATE;
    private final IronGolemEntity ironGolemEntity;
    private VillagerEntity targetVillager;
    private int lookCountdown;
    
    public IronGolemLookGoal(final IronGolemEntity ironGolemEntity) {
        this.ironGolemEntity = ironGolemEntity;
        this.setControls(EnumSet.<Control>of(Control.a, Control.b));
    }
    
    @Override
    public boolean canStart() {
        if (!this.ironGolemEntity.world.isDaylight()) {
            return false;
        }
        if (this.ironGolemEntity.getRand().nextInt(8000) != 0) {
            return false;
        }
        this.targetVillager = this.ironGolemEntity.world.<VillagerEntity>getClosestEntity(VillagerEntity.class, IronGolemLookGoal.CLOSE_VILLAGER_PREDICATE, (LivingEntity)this.ironGolemEntity, this.ironGolemEntity.x, this.ironGolemEntity.y, this.ironGolemEntity.z, this.ironGolemEntity.getBoundingBox().expand(6.0, 2.0, 6.0));
        return this.targetVillager != null;
    }
    
    @Override
    public boolean shouldContinue() {
        return this.lookCountdown > 0;
    }
    
    @Override
    public void start() {
        this.lookCountdown = 400;
        this.ironGolemEntity.r(true);
    }
    
    @Override
    public void stop() {
        this.ironGolemEntity.r(false);
        this.targetVillager = null;
    }
    
    @Override
    public void tick() {
        this.ironGolemEntity.getLookControl().lookAt(this.targetVillager, 30.0f, 30.0f);
        --this.lookCountdown;
    }
    
    static {
        CLOSE_VILLAGER_PREDICATE = new TargetPredicate().setBaseMaxDistance(6.0).includeTeammates().includeInvulnerable();
    }
}
