package net.minecraft.util.math;

public enum AxisCycle
{
    NONE {
        @Override
        public int choose(final int integer1, final int integer2, final int integer3, final Direction.Axis axis) {
            return axis.choose(integer1, integer2, integer3);
        }
        
        @Override
        public Direction.Axis cycle(final Direction.Axis axis) {
            return axis;
        }
        
        @Override
        public AxisCycle opposite() {
            return this;
        }
    }, 
    NEXT {
        @Override
        public int choose(final int integer1, final int integer2, final int integer3, final Direction.Axis axis) {
            return axis.choose(integer3, integer1, integer2);
        }
        
        @Override
        public Direction.Axis cycle(final Direction.Axis axis) {
            return AxisCycle$2.AXES[Math.floorMod(axis.ordinal() + 1, 3)];
        }
        
        @Override
        public AxisCycle opposite() {
            return AxisCycle$2.PREVIOUS;
        }
    }, 
    PREVIOUS {
        @Override
        public int choose(final int integer1, final int integer2, final int integer3, final Direction.Axis axis) {
            return axis.choose(integer2, integer3, integer1);
        }
        
        @Override
        public Direction.Axis cycle(final Direction.Axis axis) {
            return AxisCycle$3.AXES[Math.floorMod(axis.ordinal() - 1, 3)];
        }
        
        @Override
        public AxisCycle opposite() {
            return AxisCycle$3.NEXT;
        }
    };
    
    public static final Direction.Axis[] AXES;
    public static final AxisCycle[] VALUES;
    
    public abstract int choose(final int arg1, final int arg2, final int arg3, final Direction.Axis arg4);
    
    public abstract Direction.Axis cycle(final Direction.Axis arg1);
    
    public abstract AxisCycle opposite();
    
    public static AxisCycle between(final Direction.Axis from, final Direction.Axis to) {
        return AxisCycle.VALUES[Math.floorMod(to.ordinal() - from.ordinal(), 3)];
    }
    
    static {
        AXES = Direction.Axis.values();
        VALUES = values();
    }
}
