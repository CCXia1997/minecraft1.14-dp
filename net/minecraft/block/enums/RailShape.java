package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum RailShape implements StringRepresentable
{
    a(0, "north_south"), 
    b(1, "east_west"), 
    c(2, "ascending_east"), 
    d(3, "ascending_west"), 
    e(4, "ascending_north"), 
    f(5, "ascending_south"), 
    g(6, "south_east"), 
    h(7, "south_west"), 
    i(8, "north_west"), 
    j(9, "north_east");
    
    private final int id;
    private final String name;
    
    private RailShape(final int integer1, final String string2) {
        this.id = integer1;
        this.name = string2;
    }
    
    public int getId() {
        return this.id;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public boolean isAscending() {
        return this == RailShape.e || this == RailShape.c || this == RailShape.f || this == RailShape.d;
    }
    
    @Override
    public String asString() {
        return this.name;
    }
}
