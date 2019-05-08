package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;

public class TrackIronGolemTargetGoal extends TrackTargetGoal
{
    private final IronGolemEntity ironGolem;
    private LivingEntity target;
    
    public TrackIronGolemTargetGoal(final IronGolemEntity ironGolemEntity) {
        super(ironGolemEntity, false, true);
        this.ironGolem = ironGolemEntity;
        this.setControls(EnumSet.<Control>of(Control.d));
    }
    
    @Override
    public boolean canStart() {
        return false;
    }
    
    @Override
    public void start() {
        this.ironGolem.setTarget(this.target);
        super.start();
    }
}
