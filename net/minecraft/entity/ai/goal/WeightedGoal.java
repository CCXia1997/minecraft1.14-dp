package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class WeightedGoal extends Goal
{
    private final Goal goal;
    private final int weight;
    private boolean running;
    
    public WeightedGoal(final int integer, final Goal goal) {
        this.weight = integer;
        this.goal = goal;
    }
    
    public boolean canBeReplacedBy(final WeightedGoal goal) {
        return this.canStop() && goal.getWeight() < this.getWeight();
    }
    
    @Override
    public boolean canStart() {
        return this.goal.canStart();
    }
    
    @Override
    public boolean shouldContinue() {
        return this.goal.shouldContinue();
    }
    
    @Override
    public boolean canStop() {
        return this.goal.canStop();
    }
    
    @Override
    public void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        this.goal.start();
    }
    
    @Override
    public void stop() {
        if (!this.running) {
            return;
        }
        this.running = false;
        this.goal.stop();
    }
    
    @Override
    public void tick() {
        this.goal.tick();
    }
    
    @Override
    public void setControls(final EnumSet<Control> enumSet) {
        this.goal.setControls(enumSet);
    }
    
    @Override
    public EnumSet<Control> getControls() {
        return this.goal.getControls();
    }
    
    public boolean isRunning() {
        return this.running;
    }
    
    public int getWeight() {
        return this.weight;
    }
    
    public Goal getGoal() {
        return this.goal;
    }
    
    @Override
    public boolean equals(@Nullable final Object object) {
        return this == object || (object != null && this.getClass() == object.getClass() && this.goal.equals(((WeightedGoal)object).goal));
    }
    
    @Override
    public int hashCode() {
        return this.goal.hashCode();
    }
}
