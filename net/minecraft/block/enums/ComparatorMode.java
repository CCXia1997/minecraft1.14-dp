package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum ComparatorMode implements StringRepresentable
{
    a("compare"), 
    b("subtract");
    
    private final String name;
    
    private ComparatorMode(final String string1) {
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
