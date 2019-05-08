package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.util.SystemUtil;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.google.common.math.DoubleMath;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.BlockView;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.ViewableWorld;
import java.util.Iterator;
import java.util.stream.Stream;
import net.minecraft.util.math.AxisCycle;
import net.minecraft.util.math.Direction;
import java.util.Arrays;
import net.minecraft.util.BooleanBiFunction;
import com.google.common.math.IntMath;
import net.minecraft.util.math.BoundingBox;

public final class VoxelShapes
{
    private static final VoxelShape FULL_CUBE;
    public static final VoxelShape UNBOUNDED;
    private static final VoxelShape EMPTY;
    
    public static VoxelShape empty() {
        return VoxelShapes.EMPTY;
    }
    
    public static VoxelShape fullCube() {
        return VoxelShapes.FULL_CUBE;
    }
    
    public static VoxelShape cuboid(final double xMin, final double yMin, final double zMin, final double xMax, final double yMax, final double zMax) {
        return cuboid(new BoundingBox(xMin, yMin, zMin, xMax, yMax, zMax));
    }
    
    public static VoxelShape cuboid(final BoundingBox box) {
        final int integer2 = findRequiredBitResolution(box.minX, box.maxX);
        final int integer3 = findRequiredBitResolution(box.minY, box.maxY);
        final int integer4 = findRequiredBitResolution(box.minZ, box.maxZ);
        if (integer2 < 0 || integer3 < 0 || integer4 < 0) {
            return new ArrayVoxelShape(VoxelShapes.FULL_CUBE.voxels, new double[] { box.minX, box.maxX }, new double[] { box.minY, box.maxY }, new double[] { box.minZ, box.maxZ });
        }
        if (integer2 == 0 && integer3 == 0 && integer4 == 0) {
            return box.contains(0.5, 0.5, 0.5) ? fullCube() : empty();
        }
        final int integer5 = 1 << integer2;
        final int integer6 = 1 << integer3;
        final int integer7 = 1 << integer4;
        final int integer8 = (int)Math.round(box.minX * integer5);
        final int integer9 = (int)Math.round(box.maxX * integer5);
        final int integer10 = (int)Math.round(box.minY * integer6);
        final int integer11 = (int)Math.round(box.maxY * integer6);
        final int integer12 = (int)Math.round(box.minZ * integer7);
        final int integer13 = (int)Math.round(box.maxZ * integer7);
        final BitSetVoxelSet bitSetVoxelSet14 = new BitSetVoxelSet(integer5, integer6, integer7, integer8, integer10, integer12, integer9, integer11, integer13);
        for (long long15 = integer8; long15 < integer9; ++long15) {
            for (long long16 = integer10; long16 < integer11; ++long16) {
                for (long long17 = integer12; long17 < integer13; ++long17) {
                    bitSetVoxelSet14.set((int)long15, (int)long16, (int)long17, false, true);
                }
            }
        }
        return new SimpleVoxelShape(bitSetVoxelSet14);
    }
    
    private static int findRequiredBitResolution(final double min, final double max) {
        if (min < -1.0E-7 || max > 1.0000001) {
            return -1;
        }
        for (int integer5 = 0; integer5 <= 3; ++integer5) {
            final double double6 = min * (1 << integer5);
            final double double7 = max * (1 << integer5);
            final boolean boolean10 = Math.abs(double6 - Math.floor(double6)) < 1.0E-7;
            final boolean boolean11 = Math.abs(double7 - Math.floor(double7)) < 1.0E-7;
            if (boolean10 && boolean11) {
                return integer5;
            }
        }
        return -1;
    }
    
    protected static long lcm(final int integer1, final int integer2) {
        return integer1 * (long)(integer2 / IntMath.gcd(integer1, integer2));
    }
    
    public static VoxelShape union(final VoxelShape first, final VoxelShape second) {
        return combineAndSimplify(first, second, BooleanBiFunction.OR);
    }
    
    public static VoxelShape union(final VoxelShape first, final VoxelShape... others) {
        return Arrays.<VoxelShape>stream(others).reduce(first, VoxelShapes::union);
    }
    
    public static VoxelShape combineAndSimplify(final VoxelShape first, final VoxelShape second, final BooleanBiFunction function) {
        return combine(first, second, function).simplify();
    }
    
    public static VoxelShape combine(final VoxelShape voxelShape1, final VoxelShape voxelShape2, final BooleanBiFunction booleanBiFunction) {
        if (booleanBiFunction.apply(false, false)) {
            throw new IllegalArgumentException();
        }
        if (voxelShape1 == voxelShape2) {
            return booleanBiFunction.apply(true, true) ? voxelShape1 : empty();
        }
        final boolean boolean4 = booleanBiFunction.apply(true, false);
        final boolean boolean5 = booleanBiFunction.apply(false, true);
        if (voxelShape1.isEmpty()) {
            return boolean5 ? voxelShape2 : empty();
        }
        if (voxelShape2.isEmpty()) {
            return boolean4 ? voxelShape1 : empty();
        }
        final DoubleListPair doubleListPair6 = createListPair(1, voxelShape1.getIncludedPoints(Direction.Axis.X), voxelShape2.getIncludedPoints(Direction.Axis.X), boolean4, boolean5);
        final DoubleListPair doubleListPair7 = createListPair(doubleListPair6.getMergedList().size() - 1, voxelShape1.getIncludedPoints(Direction.Axis.Y), voxelShape2.getIncludedPoints(Direction.Axis.Y), boolean4, boolean5);
        final DoubleListPair doubleListPair8 = createListPair((doubleListPair6.getMergedList().size() - 1) * (doubleListPair7.getMergedList().size() - 1), voxelShape1.getIncludedPoints(Direction.Axis.Z), voxelShape2.getIncludedPoints(Direction.Axis.Z), boolean4, boolean5);
        final BitSetVoxelSet bitSetVoxelSet9 = BitSetVoxelSet.combine(voxelShape1.voxels, voxelShape2.voxels, doubleListPair6, doubleListPair7, doubleListPair8, booleanBiFunction);
        if (doubleListPair6 instanceof FractionalDoubleListPair && doubleListPair7 instanceof FractionalDoubleListPair && doubleListPair8 instanceof FractionalDoubleListPair) {
            return new SimpleVoxelShape(bitSetVoxelSet9);
        }
        return new ArrayVoxelShape(bitSetVoxelSet9, doubleListPair6.getMergedList(), doubleListPair7.getMergedList(), doubleListPair8.getMergedList());
    }
    
    public static boolean matchesAnywhere(final VoxelShape shape1, final VoxelShape shape2, final BooleanBiFunction predicate) {
        if (predicate.apply(false, false)) {
            throw new IllegalArgumentException();
        }
        if (shape1 == shape2) {
            return predicate.apply(true, true);
        }
        if (shape1.isEmpty()) {
            return predicate.apply(false, !shape2.isEmpty());
        }
        if (shape2.isEmpty()) {
            return predicate.apply(!shape1.isEmpty(), false);
        }
        final boolean boolean4 = predicate.apply(true, false);
        final boolean boolean5 = predicate.apply(false, true);
        for (final Direction.Axis axis9 : AxisCycle.AXES) {
            if (shape1.getMaximum(axis9) < shape2.getMinimum(axis9) - 1.0E-7) {
                return boolean4 || boolean5;
            }
            if (shape2.getMaximum(axis9) < shape1.getMinimum(axis9) - 1.0E-7) {
                return boolean4 || boolean5;
            }
        }
        final DoubleListPair doubleListPair6 = createListPair(1, shape1.getIncludedPoints(Direction.Axis.X), shape2.getIncludedPoints(Direction.Axis.X), boolean4, boolean5);
        final DoubleListPair doubleListPair7 = createListPair(doubleListPair6.getMergedList().size() - 1, shape1.getIncludedPoints(Direction.Axis.Y), shape2.getIncludedPoints(Direction.Axis.Y), boolean4, boolean5);
        final DoubleListPair doubleListPair8 = createListPair((doubleListPair6.getMergedList().size() - 1) * (doubleListPair7.getMergedList().size() - 1), shape1.getIncludedPoints(Direction.Axis.Z), shape2.getIncludedPoints(Direction.Axis.Z), boolean4, boolean5);
        return matchesAnywhere(doubleListPair6, doubleListPair7, doubleListPair8, shape1.voxels, shape2.voxels, predicate);
    }
    
    private static boolean matchesAnywhere(final DoubleListPair mergedX, final DoubleListPair mergedY, final DoubleListPair mergedZ, final VoxelSet shape1, final VoxelSet shape2, final BooleanBiFunction predicate) {
        return !mergedX.forAllOverlappingSections((integer6, integer7, integer8) -> mergedY.forAllOverlappingSections((integer7, integer8, integer9) -> mergedZ.forAllOverlappingSections((integer8, integer9, integer10) -> !predicate.apply(shape1.inBoundsAndContains(integer6, integer7, integer8), shape2.inBoundsAndContains(integer7, integer8, integer9)))));
    }
    
    public static double calculateMaxOffset(final Direction.Axis axis, final BoundingBox box, final Stream<VoxelShape> shapes, double double4) {
        final Iterator<VoxelShape> iterator6 = shapes.iterator();
        while (iterator6.hasNext()) {
            if (Math.abs(double4) < 1.0E-7) {
                return 0.0;
            }
            double4 = iterator6.next().a(axis, box, double4);
        }
        return double4;
    }
    
    public static double a(final Direction.Axis axis, final BoundingBox boundingBox, final ViewableWorld viewableWorld, final double double4, final VerticalEntityPosition verticalEntityPosition, final Stream<VoxelShape> stream7) {
        return a(boundingBox, viewableWorld, double4, verticalEntityPosition, AxisCycle.between(axis, Direction.Axis.Z), stream7);
    }
    
    private static double a(final BoundingBox boundingBox, final ViewableWorld viewableWorld, double double3, final VerticalEntityPosition verticalEntityPosition, final AxisCycle axisCycle, final Stream<VoxelShape> stream7) {
        if (boundingBox.getXSize() < 1.0E-6 || boundingBox.getYSize() < 1.0E-6 || boundingBox.getZSize() < 1.0E-6) {
            return double3;
        }
        if (Math.abs(double3) < 1.0E-7) {
            return 0.0;
        }
        final AxisCycle axisCycle2 = axisCycle.opposite();
        final Direction.Axis axis9 = axisCycle2.cycle(Direction.Axis.X);
        final Direction.Axis axis10 = axisCycle2.cycle(Direction.Axis.Y);
        final Direction.Axis axis11 = axisCycle2.cycle(Direction.Axis.Z);
        final BlockPos.Mutable mutable12 = new BlockPos.Mutable();
        final int integer13 = MathHelper.floor(boundingBox.getMin(axis9) - 1.0E-7) - 1;
        final int integer14 = MathHelper.floor(boundingBox.getMax(axis9) + 1.0E-7) + 1;
        final int integer15 = MathHelper.floor(boundingBox.getMin(axis10) - 1.0E-7) - 1;
        final int integer16 = MathHelper.floor(boundingBox.getMax(axis10) + 1.0E-7) + 1;
        final double double4 = boundingBox.getMin(axis11) - 1.0E-7;
        final double double5 = boundingBox.getMax(axis11) + 1.0E-7;
        final boolean boolean21 = double3 > 0.0;
        final int integer17 = boolean21 ? (MathHelper.floor(boundingBox.getMax(axis11) - 1.0E-7) - 1) : (MathHelper.floor(boundingBox.getMin(axis11) + 1.0E-7) + 1);
        int integer18 = a(double3, double4, double5);
        final int integer19 = boolean21 ? 1 : -1;
        int integer20 = Integer.MAX_VALUE;
        int integer21 = Integer.MAX_VALUE;
        Chunk chunk27 = null;
        int integer22 = integer17;
        while (true) {
            if (boolean21) {
                if (integer22 > integer18) {
                    break;
                }
            }
            else if (integer22 < integer18) {
                break;
            }
            for (int integer23 = integer13; integer23 <= integer14; ++integer23) {
                for (int integer24 = integer15; integer24 <= integer16; ++integer24) {
                    int integer25 = 0;
                    if (integer23 == integer13 || integer23 == integer14) {
                        ++integer25;
                    }
                    if (integer24 == integer15 || integer24 == integer16) {
                        ++integer25;
                    }
                    if (integer22 == integer17 || integer22 == integer18) {
                        ++integer25;
                    }
                    if (integer25 < 3) {
                        mutable12.a(axisCycle2, integer23, integer24, integer22);
                        final int integer26 = mutable12.getX() >> 4;
                        final int integer27 = mutable12.getZ() >> 4;
                        if (integer26 != integer20 || integer27 != integer21) {
                            chunk27 = viewableWorld.getChunk(integer26, integer27);
                            integer20 = integer26;
                            integer21 = integer27;
                        }
                        final BlockState blockState34 = chunk27.getBlockState(mutable12);
                        if (integer25 != 1 || blockState34.f()) {
                            if (integer25 != 2 || blockState34.getBlock() == Blocks.bn) {
                                double3 = blockState34.getCollisionShape(viewableWorld, mutable12, verticalEntityPosition).a(axis11, boundingBox.offset(-mutable12.getX(), -mutable12.getY(), -mutable12.getZ()), double3);
                                if (Math.abs(double3) < 1.0E-7) {
                                    return 0.0;
                                }
                                integer18 = a(double3, double4, double5);
                            }
                        }
                    }
                }
            }
            integer22 += integer19;
        }
        final double[] arr28 = { double3 };
        final Object o;
        stream7.forEach(voxelShape -> o[0] = voxelShape.a(axis11, boundingBox, o[0]));
        return arr28[0];
    }
    
    private static int a(final double double1, final double double3, final double double5) {
        return (double1 > 0.0) ? (MathHelper.floor(double5 + double1) + 1) : (MathHelper.floor(double3 + double1) - 1);
    }
    
    @Environment(EnvType.CLIENT)
    public static boolean a(final VoxelShape voxelShape1, final VoxelShape voxelShape2, final Direction direction) {
        if (voxelShape1 == fullCube() && voxelShape2 == fullCube()) {
            return true;
        }
        if (voxelShape2.isEmpty()) {
            return false;
        }
        final Direction.Axis axis4 = direction.getAxis();
        final Direction.AxisDirection axisDirection5 = direction.getDirection();
        final VoxelShape voxelShape3 = (axisDirection5 == Direction.AxisDirection.POSITIVE) ? voxelShape1 : voxelShape2;
        final VoxelShape voxelShape4 = (axisDirection5 == Direction.AxisDirection.POSITIVE) ? voxelShape2 : voxelShape1;
        final BooleanBiFunction booleanBiFunction8 = (axisDirection5 == Direction.AxisDirection.POSITIVE) ? BooleanBiFunction.ONLY_FIRST : BooleanBiFunction.ONLY_SECOND;
        return DoubleMath.fuzzyEquals(voxelShape3.getMaximum(axis4), 1.0, 1.0E-7) && DoubleMath.fuzzyEquals(voxelShape4.getMinimum(axis4), 0.0, 1.0E-7) && !matchesAnywhere(new SliceVoxelShape(voxelShape3, axis4, voxelShape3.voxels.getSize(axis4) - 1), new SliceVoxelShape(voxelShape4, axis4, 0), booleanBiFunction8);
    }
    
    public static VoxelShape a(final VoxelShape voxelShape, final Direction direction) {
        final Direction.Axis axis5 = direction.getAxis();
        boolean boolean3;
        int integer4;
        if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
            boolean3 = DoubleMath.fuzzyEquals(voxelShape.getMaximum(axis5), 1.0, 1.0E-7);
            integer4 = voxelShape.voxels.getSize(axis5) - 1;
        }
        else {
            boolean3 = DoubleMath.fuzzyEquals(voxelShape.getMinimum(axis5), 0.0, 1.0E-7);
            integer4 = 0;
        }
        if (!boolean3) {
            return empty();
        }
        return new SliceVoxelShape(voxelShape, axis5, integer4);
    }
    
    public static boolean b(final VoxelShape voxelShape1, final VoxelShape voxelShape2, final Direction direction) {
        if (voxelShape1 == fullCube() || voxelShape2 == fullCube()) {
            return true;
        }
        final Direction.Axis axis4 = direction.getAxis();
        final Direction.AxisDirection axisDirection5 = direction.getDirection();
        VoxelShape voxelShape3 = (axisDirection5 == Direction.AxisDirection.POSITIVE) ? voxelShape1 : voxelShape2;
        VoxelShape voxelShape4 = (axisDirection5 == Direction.AxisDirection.POSITIVE) ? voxelShape2 : voxelShape1;
        if (!DoubleMath.fuzzyEquals(voxelShape3.getMaximum(axis4), 1.0, 1.0E-7)) {
            voxelShape3 = empty();
        }
        if (!DoubleMath.fuzzyEquals(voxelShape4.getMinimum(axis4), 0.0, 1.0E-7)) {
            voxelShape4 = empty();
        }
        return !matchesAnywhere(fullCube(), combine(new SliceVoxelShape(voxelShape3, axis4, voxelShape3.voxels.getSize(axis4) - 1), new SliceVoxelShape(voxelShape4, axis4, 0), BooleanBiFunction.OR), BooleanBiFunction.ONLY_FIRST);
    }
    
    @VisibleForTesting
    protected static DoubleListPair createListPair(final int size, final DoubleList first, final DoubleList second, final boolean includeFirst, final boolean includeSecond) {
        final int integer6 = first.size() - 1;
        final int integer7 = second.size() - 1;
        if (first instanceof FractionalDoubleList && second instanceof FractionalDoubleList) {
            final long long8 = lcm(integer6, integer7);
            if (size * long8 <= 256L) {
                return new FractionalDoubleListPair(integer6, integer7);
            }
        }
        if (first.getDouble(integer6) < second.getDouble(0) - 1.0E-7) {
            return new DisjointDoubleListPair(first, second, false);
        }
        if (second.getDouble(integer7) < first.getDouble(0) - 1.0E-7) {
            return new DisjointDoubleListPair(second, first, true);
        }
        if (integer6 != integer7 || !Objects.equals(first, second)) {
            return new SimpleDoubleListPair(first, second, includeFirst, includeSecond);
        }
        if (first instanceof IdentityListMerger) {
            return (DoubleListPair)first;
        }
        if (second instanceof IdentityListMerger) {
            return (DoubleListPair)second;
        }
        return new IdentityListMerger(first);
    }
    
    static {
        final BitSetVoxelSet voxelSet1;
        FULL_CUBE = SystemUtil.<SimpleVoxelShape>get(() -> {
            voxelSet1 = new BitSetVoxelSet(1, 1, 1);
            voxelSet1.set(0, 0, 0, true, true);
            return new SimpleVoxelShape(voxelSet1);
        });
        UNBOUNDED = cuboid(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        EMPTY = new ArrayVoxelShape(new BitSetVoxelSet(0, 0, 0), (DoubleList)new DoubleArrayList(new double[] { 0.0 }), (DoubleList)new DoubleArrayList(new double[] { 0.0 }), (DoubleList)new DoubleArrayList(new double[] { 0.0 }));
    }
    
    public interface BoxConsumer
    {
        void consume(final double arg1, final double arg2, final double arg3, final double arg4, final double arg5, final double arg6);
    }
}
