package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum BlockHalf implements StringRepresentable
{
    TOP("top"), 
    BOTTOM("bottom");
    
    private final String name;
    
    private BlockHalf(final String string1) {
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
