package net.minecraft.client.render.chunk;

import java.util.Iterator;
import net.minecraft.util.math.Direction;
import java.util.Set;
import java.util.BitSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChunkOcclusionGraph
{
    private static final int DIRECTION_COUNT;
    private final BitSet visibility;
    
    public ChunkOcclusionGraph() {
        this.visibility = new BitSet(ChunkOcclusionGraph.DIRECTION_COUNT * ChunkOcclusionGraph.DIRECTION_COUNT);
    }
    
    public void addOpenEdgeFaces(final Set<Direction> faces) {
        for (final Direction direction3 : faces) {
            for (final Direction direction4 : faces) {
                this.setVisibleThrough(direction3, direction4, true);
            }
        }
    }
    
    public void setVisibleThrough(final Direction from, final Direction to, final boolean visible) {
        this.visibility.set(from.ordinal() + to.ordinal() * ChunkOcclusionGraph.DIRECTION_COUNT, visible);
        this.visibility.set(to.ordinal() + from.ordinal() * ChunkOcclusionGraph.DIRECTION_COUNT, visible);
    }
    
    public void fill(final boolean visible) {
        this.visibility.set(0, this.visibility.size(), visible);
    }
    
    public boolean isVisibleThrough(final Direction from, final Direction to) {
        return this.visibility.get(from.ordinal() + to.ordinal() * ChunkOcclusionGraph.DIRECTION_COUNT);
    }
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(' ');
        for (final Direction direction5 : Direction.values()) {
            stringBuilder1.append(' ').append(direction5.toString().toUpperCase().charAt(0));
        }
        stringBuilder1.append('\n');
        for (final Direction direction5 : Direction.values()) {
            stringBuilder1.append(direction5.toString().toUpperCase().charAt(0));
            for (final Direction direction6 : Direction.values()) {
                if (direction5 == direction6) {
                    stringBuilder1.append("  ");
                }
                else {
                    final boolean boolean10 = this.isVisibleThrough(direction5, direction6);
                    stringBuilder1.append(' ').append(boolean10 ? 'Y' : 'n');
                }
            }
            stringBuilder1.append('\n');
        }
        return stringBuilder1.toString();
    }
    
    static {
        DIRECTION_COUNT = Direction.values().length;
    }
}
