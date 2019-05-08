package net.minecraft.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import net.minecraft.util.math.Direction;
import java.util.Set;

public enum EightWayDirection
{
    a(new Direction[] { Direction.NORTH }), 
    b(new Direction[] { Direction.NORTH, Direction.EAST }), 
    c(new Direction[] { Direction.EAST }), 
    d(new Direction[] { Direction.SOUTH, Direction.EAST }), 
    e(new Direction[] { Direction.SOUTH }), 
    f(new Direction[] { Direction.SOUTH, Direction.WEST }), 
    g(new Direction[] { Direction.WEST }), 
    h(new Direction[] { Direction.NORTH, Direction.WEST });
    
    private static final int NORTHWEST_BIT;
    private static final int WEST_BIT;
    private static final int SOUTHWEST_BIT;
    private static final int SOUTH_BIT;
    private static final int SOUTHEAST_BIT;
    private static final int EAST_BIT;
    private static final int NORTHEAST_BIT;
    private static final int NORTH_BIT;
    private final Set<Direction> directions;
    
    private EightWayDirection(final Direction[] arr) {
        this.directions = Sets.<Direction>immutableEnumSet(Arrays.asList(arr));
    }
    
    public Set<Direction> getDirections() {
        return this.directions;
    }
    
    static {
        NORTHWEST_BIT = 1 << EightWayDirection.h.ordinal();
        WEST_BIT = 1 << EightWayDirection.g.ordinal();
        SOUTHWEST_BIT = 1 << EightWayDirection.f.ordinal();
        SOUTH_BIT = 1 << EightWayDirection.e.ordinal();
        SOUTHEAST_BIT = 1 << EightWayDirection.d.ordinal();
        EAST_BIT = 1 << EightWayDirection.c.ordinal();
        NORTHEAST_BIT = 1 << EightWayDirection.b.ordinal();
        NORTH_BIT = 1 << EightWayDirection.a.ordinal();
    }
}
