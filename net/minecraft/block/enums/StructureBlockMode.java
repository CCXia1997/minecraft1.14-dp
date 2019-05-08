package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum StructureBlockMode implements StringRepresentable
{
    a("save"), 
    b("load"), 
    c("corner"), 
    d("data");
    
    private final String name;
    
    private StructureBlockMode(final String string1) {
        this.name = string1;
    }
    
    @Override
    public String asString() {
        return this.name;
    }
}
