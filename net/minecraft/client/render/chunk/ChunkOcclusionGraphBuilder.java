package net.minecraft.client.render.chunk;

import net.minecraft.util.SystemUtil;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import java.util.BitSet;
import net.minecraft.util.math.Direction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChunkOcclusionGraphBuilder
{
    private static final int STEP_X;
    private static final int STEP_Z;
    private static final int STEP_Y;
    private static final Direction[] DIRECTIONS;
    private final BitSet closed;
    private static final int[] EDGE_POINTS;
    private int openCount;
    
    public ChunkOcclusionGraphBuilder() {
        this.closed = new BitSet(4096);
        this.openCount = 4096;
    }
    
    public void markClosed(final BlockPos pos) {
        this.closed.set(pack(pos), true);
        --this.openCount;
    }
    
    private static int pack(final BlockPos pos) {
        return pack(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
    }
    
    private static int pack(final int x, final int y, final int z) {
        return x << 0 | y << 8 | z << 4;
    }
    
    public ChunkOcclusionGraph build() {
        final ChunkOcclusionGraph chunkOcclusionGraph1 = new ChunkOcclusionGraph();
        if (4096 - this.openCount < 256) {
            chunkOcclusionGraph1.fill(true);
        }
        else if (this.openCount == 0) {
            chunkOcclusionGraph1.fill(false);
        }
        else {
            for (final int integer5 : ChunkOcclusionGraphBuilder.EDGE_POINTS) {
                if (!this.closed.get(integer5)) {
                    chunkOcclusionGraph1.addOpenEdgeFaces(this.getOpenFaces(integer5));
                }
            }
        }
        return chunkOcclusionGraph1;
    }
    
    public Set<Direction> getOpenFaces(final BlockPos pos) {
        return this.getOpenFaces(pack(pos));
    }
    
    private Set<Direction> getOpenFaces(final int pos) {
        final Set<Direction> set2 = EnumSet.<Direction>noneOf(Direction.class);
        final IntPriorityQueue intPriorityQueue3 = (IntPriorityQueue)new IntArrayFIFOQueue();
        intPriorityQueue3.enqueue(pos);
        this.closed.set(pos, true);
        while (!intPriorityQueue3.isEmpty()) {
            final int integer4 = intPriorityQueue3.dequeueInt();
            this.addEdgeFaces(integer4, set2);
            for (final Direction direction8 : ChunkOcclusionGraphBuilder.DIRECTIONS) {
                final int integer5 = this.offset(integer4, direction8);
                if (integer5 >= 0 && !this.closed.get(integer5)) {
                    this.closed.set(integer5, true);
                    intPriorityQueue3.enqueue(integer5);
                }
            }
        }
        return set2;
    }
    
    private void addEdgeFaces(final int pos, final Set<Direction> openFaces) {
        final int integer3 = pos >> 0 & 0xF;
        if (integer3 == 0) {
            openFaces.add(Direction.WEST);
        }
        else if (integer3 == 15) {
            openFaces.add(Direction.EAST);
        }
        final int integer4 = pos >> 8 & 0xF;
        if (integer4 == 0) {
            openFaces.add(Direction.DOWN);
        }
        else if (integer4 == 15) {
            openFaces.add(Direction.UP);
        }
        final int integer5 = pos >> 4 & 0xF;
        if (integer5 == 0) {
            openFaces.add(Direction.NORTH);
        }
        else if (integer5 == 15) {
            openFaces.add(Direction.SOUTH);
        }
    }
    
    private int offset(final int pos, final Direction direction) {
        switch (direction) {
            case DOWN: {
                if ((pos >> 8 & 0xF) == 0x0) {
                    return -1;
                }
                return pos - ChunkOcclusionGraphBuilder.STEP_Y;
            }
            case UP: {
                if ((pos >> 8 & 0xF) == 0xF) {
                    return -1;
                }
                return pos + ChunkOcclusionGraphBuilder.STEP_Y;
            }
            case NORTH: {
                if ((pos >> 4 & 0xF) == 0x0) {
                    return -1;
                }
                return pos - ChunkOcclusionGraphBuilder.STEP_Z;
            }
            case SOUTH: {
                if ((pos >> 4 & 0xF) == 0xF) {
                    return -1;
                }
                return pos + ChunkOcclusionGraphBuilder.STEP_Z;
            }
            case WEST: {
                if ((pos >> 0 & 0xF) == 0x0) {
                    return -1;
                }
                return pos - ChunkOcclusionGraphBuilder.STEP_X;
            }
            case EAST: {
                if ((pos >> 0 & 0xF) == 0xF) {
                    return -1;
                }
                return pos + ChunkOcclusionGraphBuilder.STEP_X;
            }
            default: {
                return -1;
            }
        }
    }
    
    static {
        STEP_X = (int)Math.pow(16.0, 0.0);
        STEP_Z = (int)Math.pow(16.0, 1.0);
        STEP_Y = (int)Math.pow(16.0, 2.0);
        DIRECTIONS = Direction.values();
        final boolean integer2;
        final int integer3;
        int integer4;
        int integer5;
        int integer6;
        int integer7;
        final int n;
        EDGE_POINTS = SystemUtil.<int[]>consume(new int[1352], arr -> {
            integer2 = false;
            integer3 = 15;
            integer4 = 0;
            for (integer5 = 0; integer5 < 16; ++integer5) {
                for (integer6 = 0; integer6 < 16; ++integer6) {
                    for (integer7 = 0; integer7 < 16; ++integer7) {
                        if (integer5 == 0 || integer5 == 15 || integer6 == 0 || integer6 == 15 || integer7 == 0 || integer7 == 15) {
                            integer4++;
                            arr[n] = pack(integer5, integer6, integer7);
                        }
                    }
                }
            }
        });
    }
}
