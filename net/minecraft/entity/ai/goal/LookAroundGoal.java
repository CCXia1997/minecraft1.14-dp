package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;

public class LookAroundGoal extends Goal
{
    private final MobEntity owner;
    private double deltaX;
    private double deltaZ;
    private int lookTime;
    
    public LookAroundGoal(final MobEntity owner) {
        this.owner = owner;
        this.setControls(EnumSet.<Control>of(Control.a, Control.b));
    }
    
    @Override
    public boolean canStart() {
        return this.owner.getRand().nextFloat() < 0.02f;
    }
    
    @Override
    public boolean shouldContinue() {
        return this.lookTime >= 0;
    }
    
    @Override
    public void start() {
        final double double1 = 6.283185307179586 * this.owner.getRand().nextDouble();
        this.deltaX = Math.cos(double1);
        this.deltaZ = Math.sin(double1);
        this.lookTime = 20 + this.owner.getRand().nextInt(20);
    }
    
    @Override
    public void tick() {
        --this.lookTime;
        this.owner.getLookControl().a(this.owner.x + this.deltaX, this.owner.y + this.owner.getStandingEyeHeight(), this.owner.z + this.deltaZ);
    }
}
