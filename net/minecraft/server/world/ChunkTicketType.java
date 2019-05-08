package net.minecraft.server.world;

import net.minecraft.util.math.ColumnPos;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.util.Void;
import java.util.Comparator;

public class ChunkTicketType<T>
{
    private final String name;
    private final Comparator<T> argumentComparator;
    public static final ChunkTicketType<Void> START;
    public static final ChunkTicketType<Void> DRAGON;
    public static final ChunkTicketType<ChunkPos> PLAYER;
    public static final ChunkTicketType<ChunkPos> FORCED;
    public static final ChunkTicketType<ChunkPos> e;
    public static final ChunkTicketType<ColumnPos> f;
    public static final ChunkTicketType<ChunkPos> UNKNOWN;
    
    public static <T> ChunkTicketType<T> create(final String string, final Comparator<T> comparator) {
        return new ChunkTicketType<T>(string, comparator);
    }
    
    protected ChunkTicketType(final String name, final Comparator<T> comparator) {
        this.name = name;
        this.argumentComparator = comparator;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public Comparator<T> getArgumentComparator() {
        return this.argumentComparator;
    }
    
    static {
        START = ChunkTicketType.<Void>create("start", (void1, void2) -> 0);
        DRAGON = ChunkTicketType.<Void>create("dragon", (void1, void2) -> 0);
        PLAYER = ChunkTicketType.<ChunkPos>create("player", Comparator.<ChunkPos>comparingLong(ChunkPos::toLong));
        FORCED = ChunkTicketType.<ChunkPos>create("forced", Comparator.<ChunkPos>comparingLong(ChunkPos::toLong));
        e = ChunkTicketType.<ChunkPos>create("light", Comparator.<ChunkPos>comparingLong(ChunkPos::toLong));
        f = ChunkTicketType.<ColumnPos>create("portal", Comparator.<ColumnPos>comparingLong(ColumnPos::toLong));
        UNKNOWN = ChunkTicketType.<ChunkPos>create("unknown", Comparator.<ChunkPos>comparingLong(ChunkPos::toLong));
    }
}
