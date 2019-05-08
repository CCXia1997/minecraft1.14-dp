package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum DoubleBlockHalf implements StringRepresentable
{
    a, 
    b;
    
    @Override
    public String toString() {
        return this.asString();
    }
    
    @Override
    public String asString() {
        return (this == DoubleBlockHalf.a) ? "upper" : "lower";
    }
}
