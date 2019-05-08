package net.minecraft.util.shape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.AxisCycle;
import net.minecraft.util.math.Direction;

public abstract class VoxelSet
{
    private static final Direction.Axis[] AXES;
    protected final int xSize;
    protected final int ySize;
    protected final int zSize;
    
    protected VoxelSet(final int xSize, final int ySize, final int zSize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }
    
    public boolean inBoundsAndContains(final AxisCycle cycle, final int x, final int y, final int z) {
        return this.inBoundsAndContains(cycle.choose(x, y, z, Direction.Axis.X), cycle.choose(x, y, z, Direction.Axis.Y), cycle.choose(x, y, z, Direction.Axis.Z));
    }
    
    public boolean inBoundsAndContains(final int x, final int y, final int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < this.xSize && y < this.ySize && z < this.zSize && this.contains(x, y, z);
    }
    
    public boolean contains(final AxisCycle cycle, final int integer2, final int integer3, final int integer4) {
        return this.contains(cycle.choose(integer2, integer3, integer4, Direction.Axis.X), cycle.choose(integer2, integer3, integer4, Direction.Axis.Y), cycle.choose(integer2, integer3, integer4, Direction.Axis.Z));
    }
    
    public abstract boolean contains(final int arg1, final int arg2, final int arg3);
    
    public abstract void set(final int arg1, final int arg2, final int arg3, final boolean arg4, final boolean arg5);
    
    public boolean isEmpty() {
        for (final Direction.Axis axis4 : VoxelSet.AXES) {
            if (this.getMin(axis4) >= this.getMax(axis4)) {
                return true;
            }
        }
        return false;
    }
    
    public abstract int getMin(final Direction.Axis arg1);
    
    public abstract int getMax(final Direction.Axis arg1);
    
    @Environment(EnvType.CLIENT)
    public int a(final Direction.Axis axis, final int integer2, final int integer3) {
        final int integer4 = this.getSize(axis);
        if (integer2 < 0 || integer3 < 0) {
            return integer4;
        }
        final Direction.Axis axis2 = AxisCycle.NEXT.cycle(axis);
        final Direction.Axis axis3 = AxisCycle.PREVIOUS.cycle(axis);
        if (integer2 >= this.getSize(axis2) || integer3 >= this.getSize(axis3)) {
            return integer4;
        }
        final AxisCycle axisCycle7 = AxisCycle.between(Direction.Axis.X, axis);
        for (int integer5 = 0; integer5 < integer4; ++integer5) {
            if (this.contains(axisCycle7, integer5, integer2, integer3)) {
                return integer5;
            }
        }
        return integer4;
    }
    
    @Environment(EnvType.CLIENT)
    public int b(final Direction.Axis axis, final int integer2, final int integer3) {
        if (integer2 < 0 || integer3 < 0) {
            return 0;
        }
        final Direction.Axis axis2 = AxisCycle.NEXT.cycle(axis);
        final Direction.Axis axis3 = AxisCycle.PREVIOUS.cycle(axis);
        if (integer2 >= this.getSize(axis2) || integer3 >= this.getSize(axis3)) {
            return 0;
        }
        final int integer4 = this.getSize(axis);
        final AxisCycle axisCycle7 = AxisCycle.between(Direction.Axis.X, axis);
        for (int integer5 = integer4 - 1; integer5 >= 0; --integer5) {
            if (this.contains(axisCycle7, integer5, integer2, integer3)) {
                return integer5 + 1;
            }
        }
        return 0;
    }
    
    public int getSize(final Direction.Axis axis) {
        return axis.choose(this.xSize, this.ySize, this.zSize);
    }
    
    public int getXSize() {
        return this.getSize(Direction.Axis.X);
    }
    
    public int getYSize() {
        return this.getSize(Direction.Axis.Y);
    }
    
    public int getZSize() {
        return this.getSize(Direction.Axis.Z);
    }
    
    @Environment(EnvType.CLIENT)
    public void forEachEdge(final b b, final boolean boolean2) {
        this.forEachEdge(b, AxisCycle.NONE, boolean2);
        this.forEachEdge(b, AxisCycle.NEXT, boolean2);
        this.forEachEdge(b, AxisCycle.PREVIOUS, boolean2);
    }
    
    @Environment(EnvType.CLIENT)
    private void forEachEdge(final b b, final AxisCycle axisCycle, final boolean boolean3) {
        final AxisCycle axisCycle2 = axisCycle.opposite();
        final int integer6 = this.getSize(axisCycle2.cycle(Direction.Axis.X));
        final int integer7 = this.getSize(axisCycle2.cycle(Direction.Axis.Y));
        final int integer8 = this.getSize(axisCycle2.cycle(Direction.Axis.Z));
        for (int integer9 = 0; integer9 <= integer6; ++integer9) {
            for (int integer10 = 0; integer10 <= integer7; ++integer10) {
                int integer11 = -1;
                for (int integer12 = 0; integer12 <= integer8; ++integer12) {
                    int integer13 = 0;
                    int integer14 = 0;
                    for (int integer15 = 0; integer15 <= 1; ++integer15) {
                        for (int integer16 = 0; integer16 <= 1; ++integer16) {
                            if (this.inBoundsAndContains(axisCycle2, integer9 + integer15 - 1, integer10 + integer16 - 1, integer12)) {
                                ++integer13;
                                integer14 ^= (integer15 ^ integer16);
                            }
                        }
                    }
                    if (integer13 == 1 || integer13 == 3 || (integer13 == 2 && (integer14 & 0x1) == 0x0)) {
                        if (boolean3) {
                            if (integer11 == -1) {
                                integer11 = integer12;
                            }
                        }
                        else {
                            b.consume(axisCycle2.choose(integer9, integer10, integer12, Direction.Axis.X), axisCycle2.choose(integer9, integer10, integer12, Direction.Axis.Y), axisCycle2.choose(integer9, integer10, integer12, Direction.Axis.Z), axisCycle2.choose(integer9, integer10, integer12 + 1, Direction.Axis.X), axisCycle2.choose(integer9, integer10, integer12 + 1, Direction.Axis.Y), axisCycle2.choose(integer9, integer10, integer12 + 1, Direction.Axis.Z));
                        }
                    }
                    else if (integer11 != -1) {
                        b.consume(axisCycle2.choose(integer9, integer10, integer11, Direction.Axis.X), axisCycle2.choose(integer9, integer10, integer11, Direction.Axis.Y), axisCycle2.choose(integer9, integer10, integer11, Direction.Axis.Z), axisCycle2.choose(integer9, integer10, integer12, Direction.Axis.X), axisCycle2.choose(integer9, integer10, integer12, Direction.Axis.Y), axisCycle2.choose(integer9, integer10, integer12, Direction.Axis.Z));
                        integer11 = -1;
                    }
                }
            }
        }
    }
    
    protected boolean isColumnFull(final int minZ, final int maxZ, final int x, final int y) {
        for (int integer5 = minZ; integer5 < maxZ; ++integer5) {
            if (!this.inBoundsAndContains(x, y, integer5)) {
                return false;
            }
        }
        return true;
    }
    
    protected void setColumn(final int minZ, final int maxZ, final int x, final int y, final boolean included) {
        for (int integer6 = minZ; integer6 < maxZ; ++integer6) {
            this.set(x, y, integer6, false, included);
        }
    }
    
    protected boolean isRectangleFull(final int minX, final int maxX, final int minZ, final int maxZ, final int y) {
        for (int integer6 = minX; integer6 < maxX; ++integer6) {
            if (!this.isColumnFull(minZ, maxZ, integer6, y)) {
                return false;
            }
        }
        return true;
    }
    
    public void forEachBox(final b consumer, final boolean largest) {
        final VoxelSet voxelSet3 = new BitSetVoxelSet(this);
        for (int integer4 = 0; integer4 <= this.xSize; ++integer4) {
            for (int integer5 = 0; integer5 <= this.ySize; ++integer5) {
                int integer6 = -1;
                for (int integer7 = 0; integer7 <= this.zSize; ++integer7) {
                    if (voxelSet3.inBoundsAndContains(integer4, integer5, integer7)) {
                        if (largest) {
                            if (integer6 == -1) {
                                integer6 = integer7;
                            }
                        }
                        else {
                            consumer.consume(integer4, integer5, integer7, integer4 + 1, integer5 + 1, integer7 + 1);
                        }
                    }
                    else if (integer6 != -1) {
                        int integer8 = integer4;
                        int integer9 = integer4;
                        int integer10 = integer5;
                        int integer11 = integer5;
                        voxelSet3.setColumn(integer6, integer7, integer4, integer5, false);
                        while (voxelSet3.isColumnFull(integer6, integer7, integer8 - 1, integer10)) {
                            voxelSet3.setColumn(integer6, integer7, integer8 - 1, integer10, false);
                            --integer8;
                        }
                        while (voxelSet3.isColumnFull(integer6, integer7, integer9 + 1, integer10)) {
                            voxelSet3.setColumn(integer6, integer7, integer9 + 1, integer10, false);
                            ++integer9;
                        }
                        while (voxelSet3.isRectangleFull(integer8, integer9 + 1, integer6, integer7, integer10 - 1)) {
                            for (int integer12 = integer8; integer12 <= integer9; ++integer12) {
                                voxelSet3.setColumn(integer6, integer7, integer12, integer10 - 1, false);
                            }
                            --integer10;
                        }
                        while (voxelSet3.isRectangleFull(integer8, integer9 + 1, integer6, integer7, integer11 + 1)) {
                            for (int integer12 = integer8; integer12 <= integer9; ++integer12) {
                                voxelSet3.setColumn(integer6, integer7, integer12, integer11 + 1, false);
                            }
                            ++integer11;
                        }
                        consumer.consume(integer8, integer10, integer6, integer9 + 1, integer11 + 1, integer7);
                        integer6 = -1;
                    }
                }
            }
        }
    }
    
    public void a(final a a) {
        this.a(a, AxisCycle.NONE);
        this.a(a, AxisCycle.NEXT);
        this.a(a, AxisCycle.PREVIOUS);
    }
    
    private void a(final a a, final AxisCycle axisCycle) {
        final AxisCycle axisCycle2 = axisCycle.opposite();
        final Direction.Axis axis4 = axisCycle2.cycle(Direction.Axis.Z);
        final int integer5 = this.getSize(axisCycle2.cycle(Direction.Axis.X));
        final int integer6 = this.getSize(axisCycle2.cycle(Direction.Axis.Y));
        final int integer7 = this.getSize(axis4);
        final Direction direction8 = Direction.from(axis4, Direction.AxisDirection.NEGATIVE);
        final Direction direction9 = Direction.from(axis4, Direction.AxisDirection.POSITIVE);
        for (int integer8 = 0; integer8 < integer5; ++integer8) {
            for (int integer9 = 0; integer9 < integer6; ++integer9) {
                boolean boolean12 = false;
                for (int integer10 = 0; integer10 <= integer7; ++integer10) {
                    final boolean boolean13 = integer10 != integer7 && this.contains(axisCycle2, integer8, integer9, integer10);
                    if (!boolean12 && boolean13) {
                        a.consume(direction8, axisCycle2.choose(integer8, integer9, integer10, Direction.Axis.X), axisCycle2.choose(integer8, integer9, integer10, Direction.Axis.Y), axisCycle2.choose(integer8, integer9, integer10, Direction.Axis.Z));
                    }
                    if (boolean12 && !boolean13) {
                        a.consume(direction9, axisCycle2.choose(integer8, integer9, integer10 - 1, Direction.Axis.X), axisCycle2.choose(integer8, integer9, integer10 - 1, Direction.Axis.Y), axisCycle2.choose(integer8, integer9, integer10 - 1, Direction.Axis.Z));
                    }
                    boolean12 = boolean13;
                }
            }
        }
    }
    
    static {
        AXES = Direction.Axis.values();
    }
    
    public interface a
    {
        void consume(final Direction arg1, final int arg2, final int arg3, final int arg4);
    }
    
    public interface b
    {
        void consume(final int arg1, final int arg2, final int arg3, final int arg4, final int arg5, final int arg6);
    }
}
