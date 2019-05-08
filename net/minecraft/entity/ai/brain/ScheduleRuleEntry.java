package net.minecraft.entity.ai.brain;

public class ScheduleRuleEntry
{
    private final int startTime;
    private final float priority;
    
    public ScheduleRuleEntry(final int startTime, final float priority) {
        this.startTime = startTime;
        this.priority = priority;
    }
    
    public int getStartTime() {
        return this.startTime;
    }
    
    public float getPriority() {
        return this.priority;
    }
}
