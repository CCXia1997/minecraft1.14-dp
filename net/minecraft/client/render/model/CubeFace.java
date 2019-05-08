package net.minecraft.client.render.model;

import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Direction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum CubeFace
{
    a(new Corner[] { new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.SOUTH) }), 
    b(new Corner[] { new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.NORTH) }), 
    c(new Corner[] { new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.NORTH) }), 
    d(new Corner[] { new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.SOUTH) }), 
    e(new Corner[] { new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.SOUTH) }), 
    f(new Corner[] { new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.NORTH) });
    
    private static final CubeFace[] g;
    private final Corner[] corners;
    
    public static CubeFace a(final Direction direction) {
        return CubeFace.g[direction.getId()];
    }
    
    private CubeFace(final Corner[] arr) {
        this.corners = arr;
    }
    
    public Corner getCorner(final int integer) {
        return this.corners[integer];
    }
    
    static {
        g = SystemUtil.<CubeFace[]>consume(new CubeFace[6], arr -> {
            arr[DirectionIds.DOWN] = CubeFace.a;
            arr[DirectionIds.UP] = CubeFace.b;
            arr[DirectionIds.NORTH] = CubeFace.c;
            arr[DirectionIds.SOUTH] = CubeFace.d;
            arr[DirectionIds.WEST] = CubeFace.e;
            arr[DirectionIds.EAST] = CubeFace.f;
        });
    }
    
    @Environment(EnvType.CLIENT)
    public static final class DirectionIds
    {
        public static final int SOUTH;
        public static final int UP;
        public static final int EAST;
        public static final int NORTH;
        public static final int DOWN;
        public static final int WEST;
        
        static {
            SOUTH = Direction.SOUTH.getId();
            UP = Direction.UP.getId();
            EAST = Direction.EAST.getId();
            NORTH = Direction.NORTH.getId();
            DOWN = Direction.DOWN.getId();
            WEST = Direction.WEST.getId();
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class Corner
    {
        public final int xSide;
        public final int ySide;
        public final int zSide;
        
        private Corner(final int integer1, final int integer2, final int integer3) {
            this.xSide = integer1;
            this.ySide = integer2;
            this.zSide = integer3;
        }
    }
}
