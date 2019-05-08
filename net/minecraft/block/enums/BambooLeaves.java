package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum BambooLeaves implements StringRepresentable
{
    a("none"), 
    b("small"), 
    c("large");
    
    private final String name;
    
    private BambooLeaves(final String string1) {
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
