package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum StairShape implements StringRepresentable
{
    a("straight"), 
    b("inner_left"), 
    c("inner_right"), 
    d("outer_left"), 
    e("outer_right");
    
    private final String name;
    
    private StairShape(final String string1) {
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
