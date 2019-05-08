package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum WireConnection implements StringRepresentable
{
    a("up"), 
    b("side"), 
    c("none");
    
    private final String name;
    
    private WireConnection(final String string1) {
        this.name = string1;
    }
    
    @Override
    public String toString() {
        return this.asString();
    }
    
    @Override
    public String asString() {
        return this.name;
    }
}
