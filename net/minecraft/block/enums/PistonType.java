package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum PistonType implements StringRepresentable
{
    a("normal"), 
    b("sticky");
    
    private final String name;
    
    private PistonType(final String string1) {
        this.name = string1;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public String asString() {
        return this.name;
    }
}
