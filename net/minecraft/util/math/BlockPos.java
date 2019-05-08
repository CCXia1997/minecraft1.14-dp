package net.minecraft.util.math;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import com.google.common.collect.AbstractIterator;
import java.util.stream.StreamSupport;
import java.util.function.Consumer;
import net.minecraft.util.CuboidBlockIterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import net.minecraft.util.BlockRotation;
import java.util.stream.IntStream;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Spliterator;
import com.mojang.datafixers.Dynamic;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.Logger;
import javax.annotation.concurrent.Immutable;
import net.minecraft.util.DynamicSerializable;

@Immutable
public class BlockPos extends Vec3i implements DynamicSerializable
{
    private static final Logger LOGGER;
    public static final BlockPos ORIGIN;
    private static final int SIZE_BITS_X;
    private static final int SIZE_BITS_Z;
    private static final int SIZE_BITS_Y;
    private static final long BITS_X;
    private static final long BITS_Y;
    private static final long BITS_Z;
    private static final int BIT_SHIFT_Z;
    private static final int BIT_SHIFT_X;
    
    public BlockPos(final int integer1, final int integer2, final int integer3) {
        super(integer1, integer2, integer3);
    }
    
    public BlockPos(final double double1, final double double3, final double double5) {
        super(double1, double3, double5);
    }
    
    public BlockPos(final Entity entity) {
        this(entity.x, entity.y, entity.z);
    }
    
    public BlockPos(final Vec3d vec3d) {
        this(vec3d.x, vec3d.y, vec3d.z);
    }
    
    public BlockPos(final Position position) {
        this(position.getX(), position.getY(), position.getZ());
    }
    
    public BlockPos(final Vec3i vec3i) {
        this(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }
    
    public static <T> BlockPos deserialize(final Dynamic<T> dynamic) {
        final Spliterator.OfInt ofInt2 = dynamic.asIntStream().spliterator();
        final int[] arr3 = new int[3];
        if (ofInt2.tryAdvance(integer -> arr3[0] = integer) && ofInt2.tryAdvance(integer -> arr3[1] = integer)) {
            ofInt2.tryAdvance(integer -> arr3[2] = integer);
        }
        return new BlockPos(arr3[0], arr3[1], arr3[2]);
    }
    
    @Override
    public <T> T serialize(final DynamicOps<T> ops) {
        return (T)ops.createIntList(IntStream.of(this.getX(), this.getY(), this.getZ()));
    }
    
    public static long offset(final long value, final Direction direction3) {
        return add(value, direction3.getOffsetX(), direction3.getOffsetY(), direction3.getOffsetZ());
    }
    
    public static long add(final long value, final int x, final int y, final int integer5) {
        return asLong(unpackLongX(value) + x, unpackLongY(value) + y, unpackLongZ(value) + integer5);
    }
    
    public static int unpackLongX(final long long1) {
        return (int)(long1 << 64 - BlockPos.BIT_SHIFT_X - BlockPos.SIZE_BITS_X >> 64 - BlockPos.SIZE_BITS_X);
    }
    
    public static int unpackLongY(final long long1) {
        return (int)(long1 << 64 - BlockPos.SIZE_BITS_Y >> 64 - BlockPos.SIZE_BITS_Y);
    }
    
    public static int unpackLongZ(final long long1) {
        return (int)(long1 << 64 - BlockPos.BIT_SHIFT_Z - BlockPos.SIZE_BITS_Z >> 64 - BlockPos.SIZE_BITS_Z);
    }
    
    public static BlockPos fromLong(final long value) {
        return new BlockPos(unpackLongX(value), unpackLongY(value), unpackLongZ(value));
    }
    
    public static long asLong(final int x, final int y, final int z) {
        long long4 = 0L;
        long4 |= ((long)x & BlockPos.BITS_X) << BlockPos.BIT_SHIFT_X;
        long4 |= ((long)y & BlockPos.BITS_Y) << 0;
        long4 |= ((long)z & BlockPos.BITS_Z) << BlockPos.BIT_SHIFT_Z;
        return long4;
    }
    
    public static long removeChunkSectionLocalY(final long long1) {
        return long1 & 0xFFFFFFFFFFFFFFF0L;
    }
    
    public long asLong() {
        return asLong(this.getX(), this.getY(), this.getZ());
    }
    
    public BlockPos add(final double y, final double double3, final double double5) {
        if (y == 0.0 && double3 == 0.0 && double5 == 0.0) {
            return this;
        }
        return new BlockPos(this.getX() + y, this.getY() + double3, this.getZ() + double5);
    }
    
    public BlockPos add(final int x, final int y, final int z) {
        if (x == 0 && y == 0 && z == 0) {
            return this;
        }
        return new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }
    
    public BlockPos add(final Vec3i vec3i) {
        return this.add(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }
    
    public BlockPos subtract(final Vec3i vec3i) {
        return this.add(-vec3i.getX(), -vec3i.getY(), -vec3i.getZ());
    }
    
    public BlockPos up() {
        return this.up(1);
    }
    
    public BlockPos up(final int integer) {
        return this.offset(Direction.UP, integer);
    }
    
    public BlockPos down() {
        return this.down(1);
    }
    
    public BlockPos down(final int integer) {
        return this.offset(Direction.DOWN, integer);
    }
    
    public BlockPos north() {
        return this.north(1);
    }
    
    public BlockPos north(final int integer) {
        return this.offset(Direction.NORTH, integer);
    }
    
    public BlockPos south() {
        return this.south(1);
    }
    
    public BlockPos south(final int integer) {
        return this.offset(Direction.SOUTH, integer);
    }
    
    public BlockPos west() {
        return this.west(1);
    }
    
    public BlockPos west(final int integer) {
        return this.offset(Direction.WEST, integer);
    }
    
    public BlockPos east() {
        return this.east(1);
    }
    
    public BlockPos east(final int integer) {
        return this.offset(Direction.EAST, integer);
    }
    
    public BlockPos offset(final Direction direction) {
        return this.offset(direction, 1);
    }
    
    public BlockPos offset(final Direction distance, final int integer) {
        if (integer == 0) {
            return this;
        }
        return new BlockPos(this.getX() + distance.getOffsetX() * integer, this.getY() + distance.getOffsetY() * integer, this.getZ() + distance.getOffsetZ() * integer);
    }
    
    public BlockPos rotate(final BlockRotation blockRotation) {
        switch (blockRotation) {
            default: {
                return this;
            }
            case ROT_90: {
                return new BlockPos(-this.getZ(), this.getY(), this.getX());
            }
            case ROT_180: {
                return new BlockPos(-this.getX(), this.getY(), -this.getZ());
            }
            case ROT_270: {
                return new BlockPos(this.getZ(), this.getY(), -this.getX());
            }
        }
    }
    
    @Override
    public BlockPos crossProduct(final Vec3i vec) {
        return new BlockPos(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }
    
    public BlockPos toImmutable() {
        return this;
    }
    
    public static Iterable<BlockPos> iterate(final BlockPos pos1, final BlockPos pos2) {
        return iterate(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()), Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
    }
    
    public static Stream<BlockPos> stream(final BlockPos pos1, final BlockPos pos2) {
        return stream(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()), Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
    }
    
    public static Stream<BlockPos> stream(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
        return StreamSupport.<BlockPos>stream((Spliterator<BlockPos>)new Spliterators.AbstractSpliterator<BlockPos>((long)((maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1)), 64) {
            final CuboidBlockIterator connector = new CuboidBlockIterator(minX, minY, minZ, maxX, maxY, maxZ);
            final Mutable b = new Mutable();
            
            @Override
            public boolean tryAdvance(final Consumer<? super BlockPos> consumer) {
                if (this.connector.step()) {
                    consumer.accept(this.b.set(this.connector.getX(), this.connector.getY(), this.connector.getZ()));
                    return true;
                }
                return false;
            }
        }, false);
    }
    
    public static Iterable<BlockPos> iterate(final int minX, final int maxX, final int minY, final int maxY, final int minZ, final int maxZ) {
        return () -> new AbstractIterator<BlockPos>() {
            final CuboidBlockIterator iterator;
            final Mutable pos;
            final /* synthetic */ int minX;
            final /* synthetic */ int minY;
            final /* synthetic */ int minZ;
            final /* synthetic */ int maxX;
            final /* synthetic */ int maxY;
            final /* synthetic */ int maxZ;
            
            {
                this.iterator = new CuboidBlockIterator(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
                this.pos = new Mutable();
            }
            
            protected BlockPos a() {
                return this.iterator.step() ? this.pos.set(this.iterator.getX(), this.iterator.getY(), this.iterator.getZ()) : this.endOfData();
            }
        };
    }
    
    static {
        LOGGER = LogManager.getLogger();
        ORIGIN = new BlockPos(0, 0, 0);
        SIZE_BITS_X = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
        SIZE_BITS_Z = BlockPos.SIZE_BITS_X;
        SIZE_BITS_Y = 64 - BlockPos.SIZE_BITS_X - BlockPos.SIZE_BITS_Z;
        BITS_X = (1L << BlockPos.SIZE_BITS_X) - 1L;
        BITS_Y = (1L << BlockPos.SIZE_BITS_Y) - 1L;
        BITS_Z = (1L << BlockPos.SIZE_BITS_Z) - 1L;
        BIT_SHIFT_Z = BlockPos.SIZE_BITS_Y;
        BIT_SHIFT_X = BlockPos.SIZE_BITS_Y + BlockPos.SIZE_BITS_Z;
    }
    
    public static class Mutable extends BlockPos
    {
        protected int x;
        protected int y;
        protected int z;
        
        public Mutable() {
            this(0, 0, 0);
        }
        
        public Mutable(final BlockPos blockPos) {
            this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
        
        public Mutable(final int integer1, final int integer2, final int integer3) {
            super(0, 0, 0);
            this.x = integer1;
            this.y = integer2;
            this.z = integer3;
        }
        
        public Mutable(final double double1, final double double3, final double double5) {
            this(MathHelper.floor(double1), MathHelper.floor(double3), MathHelper.floor(double5));
        }
        
        @Override
        public BlockPos add(final double y, final double double3, final double double5) {
            return super.add(y, double3, double5).toImmutable();
        }
        
        @Override
        public BlockPos add(final int x, final int y, final int z) {
            return super.add(x, y, z).toImmutable();
        }
        
        @Override
        public BlockPos offset(final Direction distance, final int integer) {
            return super.offset(distance, integer).toImmutable();
        }
        
        @Override
        public BlockPos rotate(final BlockRotation blockRotation) {
            return super.rotate(blockRotation).toImmutable();
        }
        
        @Override
        public int getX() {
            return this.x;
        }
        
        @Override
        public int getY() {
            return this.y;
        }
        
        @Override
        public int getZ() {
            return this.z;
        }
        
        public Mutable set(final int x, final int y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }
        
        public Mutable set(final Entity entity) {
            return this.set(entity.x, entity.y, entity.z);
        }
        
        public Mutable set(final double double1, final double double3, final double double5) {
            return this.set(MathHelper.floor(double1), MathHelper.floor(double3), MathHelper.floor(double5));
        }
        
        public Mutable set(final Vec3i vec3i) {
            return this.set(vec3i.getX(), vec3i.getY(), vec3i.getZ());
        }
        
        public Mutable setFromLong(final long long1) {
            return this.set(BlockPos.unpackLongX(long1), BlockPos.unpackLongY(long1), BlockPos.unpackLongZ(long1));
        }
        
        public Mutable a(final AxisCycle axisCycle, final int integer2, final int integer3, final int integer4) {
            return this.set(axisCycle.choose(integer2, integer3, integer4, Direction.Axis.X), axisCycle.choose(integer2, integer3, integer4, Direction.Axis.Y), axisCycle.choose(integer2, integer3, integer4, Direction.Axis.Z));
        }
        
        public Mutable setOffset(final Direction direction) {
            return this.setOffset(direction, 1);
        }
        
        public Mutable setOffset(final Direction direction, final int integer) {
            return this.set(this.x + direction.getOffsetX() * integer, this.y + direction.getOffsetY() * integer, this.z + direction.getOffsetZ() * integer);
        }
        
        public Mutable setOffset(final int integer1, final int integer2, final int integer3) {
            return this.set(this.x + integer1, this.y + integer2, this.z + integer3);
        }
        
        public void setY(final int integer) {
            this.y = integer;
        }
        
        @Override
        public BlockPos toImmutable() {
            return new BlockPos(this);
        }
    }
    
    public static final class PooledMutable extends Mutable implements AutoCloseable
    {
        private boolean free;
        private static final List<PooledMutable> POOL;
        
        private PooledMutable(final int y, final int z, final int integer3) {
            super(y, z, integer3);
        }
        
        public static PooledMutable get() {
            return get(0, 0, 0);
        }
        
        public static PooledMutable getEntityPos(final Entity entity) {
            return get(entity.x, entity.y, entity.z);
        }
        
        public static PooledMutable get(final double double1, final double double3, final double double5) {
            return get(MathHelper.floor(double1), MathHelper.floor(double3), MathHelper.floor(double5));
        }
        
        public static PooledMutable get(final int integer1, final int integer2, final int integer3) {
            synchronized (PooledMutable.POOL) {
                if (!PooledMutable.POOL.isEmpty()) {
                    final PooledMutable pooledMutable5 = PooledMutable.POOL.remove(PooledMutable.POOL.size() - 1);
                    if (pooledMutable5 != null && pooledMutable5.free) {
                        pooledMutable5.free = false;
                        pooledMutable5.set(integer1, integer2, integer3);
                        return pooledMutable5;
                    }
                }
            }
            return new PooledMutable(integer1, integer2, integer3);
        }
        
        @Override
        public PooledMutable set(final int x, final int y, final int z) {
            return (PooledMutable)super.set(x, y, z);
        }
        
        @Override
        public PooledMutable set(final Entity entity) {
            return (PooledMutable)super.set(entity);
        }
        
        @Override
        public PooledMutable set(final double double1, final double double3, final double double5) {
            return (PooledMutable)super.set(double1, double3, double5);
        }
        
        @Override
        public PooledMutable set(final Vec3i vec3i) {
            return (PooledMutable)super.set(vec3i);
        }
        
        @Override
        public PooledMutable setOffset(final Direction direction) {
            return (PooledMutable)super.setOffset(direction);
        }
        
        @Override
        public PooledMutable setOffset(final Direction direction, final int integer) {
            return (PooledMutable)super.setOffset(direction, integer);
        }
        
        @Override
        public PooledMutable setOffset(final int integer1, final int integer2, final int integer3) {
            return (PooledMutable)super.setOffset(integer1, integer2, integer3);
        }
        
        @Override
        public void close() {
            synchronized (PooledMutable.POOL) {
                if (PooledMutable.POOL.size() < 100) {
                    PooledMutable.POOL.add(this);
                }
                this.free = true;
            }
        }
        
        static {
            POOL = Lists.newArrayList();
        }
    }
}
