package net.minecraft.block.pattern;

import net.minecraft.util.math.Vec3d;
import com.google.common.base.MoreObjects;
import net.minecraft.util.math.Vec3i;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import java.util.Iterator;
import net.minecraft.world.ViewableWorld;
import javax.annotation.Nullable;
import com.google.common.cache.LoadingCache;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import java.util.function.Predicate;

public class BlockPattern
{
    private final Predicate<CachedBlockPosition>[][][] pattern;
    private final int depth;
    private final int height;
    private final int width;
    
    public BlockPattern(final Predicate<CachedBlockPosition>[][][] arr) {
        this.pattern = arr;
        this.depth = arr.length;
        if (this.depth > 0) {
            this.height = arr[0].length;
            if (this.height > 0) {
                this.width = arr[0][0].length;
            }
            else {
                this.width = 0;
            }
        }
        else {
            this.height = 0;
            this.width = 0;
        }
    }
    
    public int getDepth() {
        return this.depth;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    @Nullable
    private Result testTransform(final BlockPos frontTopLeft, final Direction forwards, final Direction up, final LoadingCache<BlockPos, CachedBlockPosition> loadingCache) {
        for (int integer5 = 0; integer5 < this.width; ++integer5) {
            for (int integer6 = 0; integer6 < this.height; ++integer6) {
                for (int integer7 = 0; integer7 < this.depth; ++integer7) {
                    if (!this.pattern[integer7][integer6][integer5].test(loadingCache.getUnchecked(translate(frontTopLeft, forwards, up, integer5, integer6, integer7)))) {
                        return null;
                    }
                }
            }
        }
        return new Result(frontTopLeft, forwards, up, loadingCache, this.width, this.height, this.depth);
    }
    
    @Nullable
    public Result searchAround(final ViewableWorld world, final BlockPos blockPos) {
        final LoadingCache<BlockPos, CachedBlockPosition> loadingCache3 = makeCache(world, false);
        final int integer4 = Math.max(Math.max(this.width, this.height), this.depth);
        for (final BlockPos blockPos2 : BlockPos.iterate(blockPos, blockPos.add(integer4 - 1, integer4 - 1, integer4 - 1))) {
            for (final Direction direction10 : Direction.values()) {
                for (final Direction direction11 : Direction.values()) {
                    if (direction11 != direction10) {
                        if (direction11 != direction10.getOpposite()) {
                            final Result result15 = this.testTransform(blockPos2, direction10, direction11, loadingCache3);
                            if (result15 != null) {
                                return result15;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static LoadingCache<BlockPos, CachedBlockPosition> makeCache(final ViewableWorld world, final boolean forceLoad) {
        return CacheBuilder.newBuilder().<BlockPos, CachedBlockPosition>build(new BlockStateCacheLoader(world, forceLoad));
    }
    
    protected static BlockPos translate(final BlockPos pos, final Direction forwards, final Direction up, final int offsetLeft, final int offsetDown, final int offsetForwards) {
        if (forwards == up || forwards == up.getOpposite()) {
            throw new IllegalArgumentException("Invalid forwards & up combination");
        }
        final Vec3i vec3i7 = new Vec3i(forwards.getOffsetX(), forwards.getOffsetY(), forwards.getOffsetZ());
        final Vec3i vec3i8 = new Vec3i(up.getOffsetX(), up.getOffsetY(), up.getOffsetZ());
        final Vec3i vec3i9 = vec3i7.crossProduct(vec3i8);
        return pos.add(vec3i8.getX() * -offsetDown + vec3i9.getX() * offsetLeft + vec3i7.getX() * offsetForwards, vec3i8.getY() * -offsetDown + vec3i9.getY() * offsetLeft + vec3i7.getY() * offsetForwards, vec3i8.getZ() * -offsetDown + vec3i9.getZ() * offsetLeft + vec3i7.getZ() * offsetForwards);
    }
    
    static class BlockStateCacheLoader extends CacheLoader<BlockPos, CachedBlockPosition>
    {
        private final ViewableWorld world;
        private final boolean forceLoad;
        
        public BlockStateCacheLoader(final ViewableWorld viewableWorld, final boolean boolean2) {
            this.world = viewableWorld;
            this.forceLoad = boolean2;
        }
        
        public CachedBlockPosition a(final BlockPos blockPos) throws Exception {
            return new CachedBlockPosition(this.world, blockPos, this.forceLoad);
        }
    }
    
    public static class Result
    {
        private final BlockPos frontTopLeft;
        private final Direction forwards;
        private final Direction up;
        private final LoadingCache<BlockPos, CachedBlockPosition> cache;
        private final int width;
        private final int height;
        private final int depth;
        
        public Result(final BlockPos blockPos, final Direction direction2, final Direction direction3, final LoadingCache<BlockPos, CachedBlockPosition> loadingCache, final int integer5, final int integer6, final int integer7) {
            this.frontTopLeft = blockPos;
            this.forwards = direction2;
            this.up = direction3;
            this.cache = loadingCache;
            this.width = integer5;
            this.height = integer6;
            this.depth = integer7;
        }
        
        public BlockPos getFrontTopLeft() {
            return this.frontTopLeft;
        }
        
        public Direction getForwards() {
            return this.forwards;
        }
        
        public Direction getUp() {
            return this.up;
        }
        
        public int getWidth() {
            return this.width;
        }
        
        public int getHeight() {
            return this.height;
        }
        
        public CachedBlockPosition translate(final int integer1, final int integer2, final int integer3) {
            return this.cache.getUnchecked(BlockPattern.translate(this.frontTopLeft, this.getForwards(), this.getUp(), integer1, integer2, integer3));
        }
        
        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
        }
        
        public TeleportTarget a(final Direction direction, final BlockPos blockPos, final double double3, final Vec3d vec3d, final double double6) {
            final Direction direction2 = this.getForwards();
            final Direction direction3 = direction2.rotateYClockwise();
            final double double7 = this.getFrontTopLeft().getY() + 1 - double3 * this.getHeight();
            double double8;
            double double9;
            if (direction3 == Direction.NORTH) {
                double8 = blockPos.getX() + 0.5;
                double9 = this.getFrontTopLeft().getZ() + 1 - (1.0 - double6) * this.getWidth();
            }
            else if (direction3 == Direction.SOUTH) {
                double8 = blockPos.getX() + 0.5;
                double9 = this.getFrontTopLeft().getZ() + (1.0 - double6) * this.getWidth();
            }
            else if (direction3 == Direction.WEST) {
                double8 = this.getFrontTopLeft().getX() + 1 - (1.0 - double6) * this.getWidth();
                double9 = blockPos.getZ() + 0.5;
            }
            else {
                double8 = this.getFrontTopLeft().getX() + (1.0 - double6) * this.getWidth();
                double9 = blockPos.getZ() + 0.5;
            }
            double double10;
            double double11;
            if (direction2.getOpposite() == direction) {
                double10 = vec3d.x;
                double11 = vec3d.z;
            }
            else if (direction2.getOpposite() == direction.getOpposite()) {
                double10 = -vec3d.x;
                double11 = -vec3d.z;
            }
            else if (direction2.getOpposite() == direction.rotateYClockwise()) {
                double10 = -vec3d.z;
                double11 = vec3d.x;
            }
            else {
                double10 = vec3d.z;
                double11 = -vec3d.x;
            }
            final int integer20 = (direction2.getHorizontal() - direction.getOpposite().getHorizontal()) * 90;
            return new TeleportTarget(new Vec3d(double8, double7, double9), new Vec3d(double10, vec3d.y, double11), integer20);
        }
    }
    
    public static class TeleportTarget
    {
        public final Vec3d pos;
        public final Vec3d velocity;
        public final int yaw;
        
        public TeleportTarget(final Vec3d pos, final Vec3d velocity, final int yaw) {
            this.pos = pos;
            this.velocity = velocity;
            this.yaw = yaw;
        }
    }
}
