package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum ChestType implements StringRepresentable
{
    a("single", 0), 
    b("left", 2), 
    c("right", 1);
    
    public static final ChestType[] VALUES;
    private final String name;
    private final int opposite;
    
    private ChestType(final String string1, final int integer2) {
        this.name = string1;
        this.opposite = integer2;
    }
    
    @Override
    public String asString() {
        return this.name;
    }
    
    public ChestType getOpposite() {
        return ChestType.VALUES[this.opposite];
    }
    
    static {
        VALUES = values();
    }
}
