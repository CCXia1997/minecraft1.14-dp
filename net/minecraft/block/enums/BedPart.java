package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum BedPart implements StringRepresentable
{
    a("head"), 
    b("foot");
    
    private final String name;
    
    private BedPart(final String string1) {
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
