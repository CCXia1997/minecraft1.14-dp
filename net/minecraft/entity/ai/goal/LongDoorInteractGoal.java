package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;

public class LongDoorInteractGoal extends DoorInteractGoal
{
    private final boolean a;
    private int ticksLeft;
    
    public LongDoorInteractGoal(final MobEntity owner, final boolean boolean2) {
        super(owner);
        this.owner = owner;
        this.a = boolean2;
    }
    
    @Override
    public boolean shouldContinue() {
        return this.a && this.ticksLeft > 0 && super.shouldContinue();
    }
    
    @Override
    public void start() {
        this.ticksLeft = 20;
        this.setDoorOpen(true);
    }
    
    @Override
    public void stop() {
        this.setDoorOpen(false);
    }
    
    @Override
    public void tick() {
        --this.ticksLeft;
        super.tick();
    }
}
