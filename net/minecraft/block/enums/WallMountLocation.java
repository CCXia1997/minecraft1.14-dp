package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum WallMountLocation implements StringRepresentable
{
    a("floor"), 
    b("wall"), 
    c("ceiling");
    
    private final String name;
    
    private WallMountLocation(final String string1) {
        this.name = string1;
    }
    
    @Override
    public String asString() {
        return this.name;
    }
}
