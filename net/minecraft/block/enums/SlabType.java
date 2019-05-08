package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum SlabType implements StringRepresentable
{
    a("top"), 
    b("bottom"), 
    c("double");
    
    private final String name;
    
    private SlabType(final String string1) {
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
