package net.minecraft.util.math;

import com.google.common.collect.Iterators;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.function.Supplier;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Random;
import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.Map;
import net.minecraft.util.StringRepresentable;

public enum Direction implements StringRepresentable
{
    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new Vec3i(0, -1, 0)), 
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new Vec3i(0, 1, 0)), 
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new Vec3i(0, 0, -1)), 
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new Vec3i(0, 0, 1)), 
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new Vec3i(-1, 0, 0)), 
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new Vec3i(1, 0, 0));
    
    private final int id;
    private final int idOpposite;
    private final int idHorizontal;
    private final String name;
    private final Axis axis;
    private final AxisDirection direction;
    private final Vec3i vector;
    private static final Direction[] ALL;
    private static final Map<String, Direction> NAME_MAP;
    private static final Direction[] ID_TO_DIRECTION;
    private static final Direction[] HORIZONTAL;
    private static final Long2ObjectMap<Direction> VECTOR_TO_DIRECTION;
    
    private Direction(final int id, final int idOpposite, final int idHorizontal, final String name, final AxisDirection direction, final Axis axis, final Vec3i vector) {
        this.id = id;
        this.idHorizontal = idHorizontal;
        this.idOpposite = idOpposite;
        this.name = name;
        this.axis = axis;
        this.direction = direction;
        this.vector = vector;
    }
    
    public static Direction[] getEntityFacingOrder(final Entity entity) {
        final float float2 = entity.getPitch(1.0f) * 0.017453292f;
        final float float3 = -entity.getYaw(1.0f) * 0.017453292f;
        final float float4 = MathHelper.sin(float2);
        final float float5 = MathHelper.cos(float2);
        final float float6 = MathHelper.sin(float3);
        final float float7 = MathHelper.cos(float3);
        final boolean boolean8 = float6 > 0.0f;
        final boolean boolean9 = float4 < 0.0f;
        final boolean boolean10 = float7 > 0.0f;
        final float float8 = boolean8 ? float6 : (-float6);
        final float float9 = boolean9 ? (-float4) : float4;
        final float float10 = boolean10 ? float7 : (-float7);
        final float float11 = float8 * float5;
        final float float12 = float10 * float5;
        final Direction direction16 = boolean8 ? Direction.EAST : Direction.WEST;
        final Direction direction17 = boolean9 ? Direction.UP : Direction.DOWN;
        final Direction direction18 = boolean10 ? Direction.SOUTH : Direction.NORTH;
        if (float8 > float10) {
            if (float9 > float11) {
                return a(direction17, direction16, direction18);
            }
            if (float12 > float9) {
                return a(direction16, direction18, direction17);
            }
            return a(direction16, direction17, direction18);
        }
        else {
            if (float9 > float12) {
                return a(direction17, direction18, direction16);
            }
            if (float11 > float9) {
                return a(direction18, direction16, direction17);
            }
            return a(direction18, direction17, direction16);
        }
    }
    
    private static Direction[] a(final Direction direction1, final Direction direction2, final Direction direction3) {
        return new Direction[] { direction1, direction2, direction3, direction3.getOpposite(), direction2.getOpposite(), direction1.getOpposite() };
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getHorizontal() {
        return this.idHorizontal;
    }
    
    public AxisDirection getDirection() {
        return this.direction;
    }
    
    public Direction getOpposite() {
        return byId(this.idOpposite);
    }
    
    @Environment(EnvType.CLIENT)
    public Direction rotateClockwise(final Axis axis) {
        switch (axis) {
            case X: {
                if (this == Direction.WEST || this == Direction.EAST) {
                    return this;
                }
                return this.rotateXClockwise();
            }
            case Y: {
                if (this == Direction.UP || this == Direction.DOWN) {
                    return this;
                }
                return this.rotateYClockwise();
            }
            case Z: {
                if (this == Direction.NORTH || this == Direction.SOUTH) {
                    return this;
                }
                return this.rotateZClockwise();
            }
            default: {
                throw new IllegalStateException("Unable to get CW facing for axis " + axis);
            }
        }
    }
    
    public Direction rotateYClockwise() {
        switch (this) {
            case NORTH: {
                return Direction.EAST;
            }
            case EAST: {
                return Direction.SOUTH;
            }
            case SOUTH: {
                return Direction.WEST;
            }
            case WEST: {
                return Direction.NORTH;
            }
            default: {
                throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    private Direction rotateXClockwise() {
        switch (this) {
            case UP: {
                return Direction.NORTH;
            }
            case NORTH: {
                return Direction.DOWN;
            }
            case DOWN: {
                return Direction.SOUTH;
            }
            case SOUTH: {
                return Direction.UP;
            }
            default: {
                throw new IllegalStateException("Unable to get X-rotated facing of " + this);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    private Direction rotateZClockwise() {
        switch (this) {
            case UP: {
                return Direction.EAST;
            }
            case EAST: {
                return Direction.DOWN;
            }
            case DOWN: {
                return Direction.WEST;
            }
            case WEST: {
                return Direction.UP;
            }
            default: {
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
            }
        }
    }
    
    public Direction rotateYCounterclockwise() {
        switch (this) {
            case NORTH: {
                return Direction.WEST;
            }
            case EAST: {
                return Direction.NORTH;
            }
            case SOUTH: {
                return Direction.EAST;
            }
            case WEST: {
                return Direction.SOUTH;
            }
            default: {
                throw new IllegalStateException("Unable to get CCW facing of " + this);
            }
        }
    }
    
    public int getOffsetX() {
        return (this.axis == Axis.X) ? this.direction.offset() : 0;
    }
    
    public int getOffsetY() {
        return (this.axis == Axis.Y) ? this.direction.offset() : 0;
    }
    
    public int getOffsetZ() {
        return (this.axis == Axis.Z) ? this.direction.offset() : 0;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Axis getAxis() {
        return this.axis;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static Direction byName(@Nullable final String name) {
        if (name == null) {
            return null;
        }
        return Direction.NAME_MAP.get(name.toLowerCase(Locale.ROOT));
    }
    
    public static Direction byId(final int id) {
        return Direction.ID_TO_DIRECTION[MathHelper.abs(id % Direction.ID_TO_DIRECTION.length)];
    }
    
    public static Direction fromHorizontal(final int value) {
        return Direction.HORIZONTAL[MathHelper.abs(value % Direction.HORIZONTAL.length)];
    }
    
    @Nullable
    public static Direction fromVector(final int integer1, final int integer2, final int integer3) {
        return (Direction)Direction.VECTOR_TO_DIRECTION.get(BlockPos.asLong(integer1, integer2, integer3));
    }
    
    public static Direction fromRotation(final double rotation) {
        return fromHorizontal(MathHelper.floor(rotation / 90.0 + 0.5) & 0x3);
    }
    
    public static Direction from(final Axis axis, final AxisDirection direction) {
        switch (axis) {
            case X: {
                return (direction == AxisDirection.POSITIVE) ? Direction.EAST : Direction.WEST;
            }
            case Y: {
                return (direction == AxisDirection.POSITIVE) ? Direction.UP : Direction.DOWN;
            }
            default: {
                return (direction == AxisDirection.POSITIVE) ? Direction.SOUTH : Direction.NORTH;
            }
        }
    }
    
    public float asRotation() {
        return (float)((this.idHorizontal & 0x3) * 90);
    }
    
    public static Direction random(final Random random) {
        return values()[random.nextInt(values().length)];
    }
    
    public static Direction getFacing(final double x, final double double3, final double double5) {
        return getFacing((float)x, (float)double3, (float)double5);
    }
    
    public static Direction getFacing(final float x, final float y, final float z) {
        Direction direction4 = Direction.NORTH;
        float float5 = Float.MIN_VALUE;
        for (final Direction direction5 : Direction.ALL) {
            final float float6 = x * direction5.vector.getX() + y * direction5.vector.getY() + z * direction5.vector.getZ();
            if (float6 > float5) {
                float5 = float6;
                direction4 = direction5;
            }
        }
        return direction4;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public String asString() {
        return this.name;
    }
    
    public static Direction get(final AxisDirection direction, final Axis axis) {
        for (final Direction direction2 : values()) {
            if (direction2.getDirection() == direction && direction2.getAxis() == axis) {
                return direction2;
            }
        }
        throw new IllegalArgumentException("No such direction: " + direction + " " + axis);
    }
    
    public Vec3i getVector() {
        return this.vector;
    }
    
    static {
        ALL = values();
        NAME_MAP = Arrays.<Direction>stream(Direction.ALL).collect(Collectors.toMap(Direction::getName, direction -> direction));
        ID_TO_DIRECTION = Arrays.<Direction>stream(Direction.ALL).sorted(Comparator.comparingInt(direction -> direction.id)).<Direction>toArray(Direction[]::new);
        HORIZONTAL = Arrays.<Direction>stream(Direction.ALL).filter(direction -> direction.getAxis().isHorizontal()).sorted(Comparator.comparingInt(direction -> direction.idHorizontal)).<Direction>toArray(Direction[]::new);
        VECTOR_TO_DIRECTION = Arrays.<Direction>stream(Direction.ALL).collect(Collectors.toMap(direction -> new BlockPos(direction.getVector()).asLong(), direction -> direction, (direction1, direction2) -> {
            throw new IllegalArgumentException("Duplicate keys");
        }, (Supplier<R>)Long2ObjectOpenHashMap::new));
    }
    
    public enum Axis implements Predicate<Direction>, StringRepresentable
    {
        X("x") {
            @Override
            public int choose(final int integer1, final int integer2, final int integer3) {
                return integer1;
            }
            
            @Override
            public double choose(final double x, final double y, final double z) {
                return x;
            }
        }, 
        Y("y") {
            @Override
            public int choose(final int integer1, final int integer2, final int integer3) {
                return integer2;
            }
            
            @Override
            public double choose(final double x, final double y, final double z) {
                return y;
            }
        }, 
        Z("z") {
            @Override
            public int choose(final int integer1, final int integer2, final int integer3) {
                return integer3;
            }
            
            @Override
            public double choose(final double x, final double y, final double z) {
                return z;
            }
        };
        
        private static final Map<String, Axis> BY_NAME;
        private final String name;
        
        private Axis(final String string1) {
            this.name = string1;
        }
        
        @Nullable
        @Environment(EnvType.CLIENT)
        public static Axis fromName(final String name) {
            return Axis.BY_NAME.get(name.toLowerCase(Locale.ROOT));
        }
        
        public String getName() {
            return this.name;
        }
        
        public boolean isVertical() {
            return this == Axis.Y;
        }
        
        public boolean isHorizontal() {
            return this == Axis.X || this == Axis.Z;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public static Axis a(final Random random) {
            return values()[random.nextInt(values().length)];
        }
        
        public boolean a(@Nullable final Direction direction) {
            return direction != null && direction.getAxis() == this;
        }
        
        public Type getType() {
            switch (this) {
                case X:
                case Z: {
                    return Type.HORIZONTAL;
                }
                case Y: {
                    return Type.VERTICAL;
                }
                default: {
                    throw new Error("Someone's been tampering with the universe!");
                }
            }
        }
        
        @Override
        public String asString() {
            return this.name;
        }
        
        public abstract int choose(final int arg1, final int arg2, final int arg3);
        
        public abstract double choose(final double arg1, final double arg2, final double arg3);
        
        static {
            BY_NAME = Arrays.<Axis>stream(values()).collect(Collectors.toMap(Axis::getName, axis -> axis));
        }
    }
    
    public enum AxisDirection
    {
        POSITIVE(1, "Towards positive"), 
        NEGATIVE(-1, "Towards negative");
        
        private final int offset;
        private final String desc;
        
        private AxisDirection(final int integer1, final String string2) {
            this.offset = integer1;
            this.desc = string2;
        }
        
        public int offset() {
            return this.offset;
        }
        
        @Override
        public String toString() {
            return this.desc;
        }
    }
    
    public enum Type implements Iterable<Direction>, Predicate<Direction>
    {
        HORIZONTAL(new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST }, new Axis[] { Axis.X, Axis.Z }), 
        VERTICAL(new Direction[] { Direction.UP, Direction.DOWN }, new Axis[] { Axis.Y });
        
        private final Direction[] facingArray;
        private final Axis[] axisArray;
        
        private Type(final Direction[] arr, final Axis[] arr) {
            this.facingArray = arr;
            this.axisArray = arr;
        }
        
        public Direction random(final Random random) {
            return this.facingArray[random.nextInt(this.facingArray.length)];
        }
        
        public boolean a(@Nullable final Direction direction) {
            return direction != null && direction.getAxis().getType() == this;
        }
        
        @Override
        public Iterator<Direction> iterator() {
            return Iterators.<Direction>forArray(this.facingArray);
        }
    }
}
