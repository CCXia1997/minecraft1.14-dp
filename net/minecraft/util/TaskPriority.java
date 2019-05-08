package net.minecraft.util;

public enum TaskPriority
{
    a(-3), 
    b(-2), 
    c(-1), 
    d(0), 
    e(1), 
    f(2), 
    g(3);
    
    private final int priorityIndex;
    
    private TaskPriority(final int priorityIndex) {
        this.priorityIndex = priorityIndex;
    }
    
    public static TaskPriority getByIndex(final int priorityIndex) {
        for (final TaskPriority taskPriority5 : values()) {
            if (taskPriority5.priorityIndex == priorityIndex) {
                return taskPriority5;
            }
        }
        if (priorityIndex < TaskPriority.a.priorityIndex) {
            return TaskPriority.a;
        }
        return TaskPriority.g;
    }
    
    public int getPriorityIndex() {
        return this.priorityIndex;
    }
}
