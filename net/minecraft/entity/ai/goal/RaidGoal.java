package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;
import javax.annotation.Nullable;
import java.util.function.Predicate;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.LivingEntity;

public class RaidGoal<T extends LivingEntity> extends FollowTargetGoal<T>
{
    private int cooldown;
    
    public RaidGoal(final RaiderEntity raiderEntity, final Class<T> class2, final boolean boolean3, @Nullable final Predicate<LivingEntity> predicate) {
        super(raiderEntity, class2, 500, boolean3, false, predicate);
        this.cooldown = 0;
    }
    
    public int getCooldown() {
        return this.cooldown;
    }
    
    public void decreaseCooldown() {
        --this.cooldown;
    }
    
    @Override
    public boolean canStart() {
        if (this.cooldown > 0 || !this.entity.getRand().nextBoolean()) {
            return false;
        }
        if (!((RaiderEntity)this.entity).hasActiveRaid()) {
            return false;
        }
        this.findClosestTarget();
        return this.targetEntity != null;
    }
    
    @Override
    public void start() {
        this.cooldown = 200;
        super.start();
    }
}
