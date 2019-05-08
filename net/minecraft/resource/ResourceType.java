package net.minecraft.resource;

public enum ResourceType
{
    ASSETS("assets"), 
    DATA("data");
    
    private final String name;
    
    private ResourceType(final String string1) {
        this.name = string1;
    }
    
    public String getName() {
        return this.name;
    }
}
