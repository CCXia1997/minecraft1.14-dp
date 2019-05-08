package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum Attachment implements StringRepresentable
{
    a("floor"), 
    b("ceiling"), 
    c("single_wall"), 
    d("double_wall");
    
    private final String name;
    
    private Attachment(final String string1) {
        this.name = string1;
    }
    
    @Override
    public String asString() {
        return this.name;
    }
}
