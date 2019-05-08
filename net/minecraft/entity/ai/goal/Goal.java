package net.minecraft.entity.ai.goal;

import java.util.Collection;
import java.util.EnumSet;

public abstract class Goal
{
    private final EnumSet<Control> controls;
    
    public Goal() {
        this.controls = EnumSet.<Control>noneOf(Control.class);
    }
    
    public abstract boolean canStart();
    
    public boolean shouldContinue() {
        return this.canStart();
    }
    
    public boolean canStop() {
        return true;
    }
    
    public void start() {
    }
    
    public void stop() {
    }
    
    public void tick() {
    }
    
    public void setControls(final EnumSet<Control> enumSet) {
        this.controls.clear();
        this.controls.addAll(enumSet);
    }
    
    public EnumSet<Control> getControls() {
        return this.controls;
    }
    
    public enum Control
    {
        a, 
        b, 
        c, 
        d;
    }
}
